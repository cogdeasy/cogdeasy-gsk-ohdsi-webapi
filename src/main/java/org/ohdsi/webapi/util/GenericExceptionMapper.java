/*
 * Copyright 2015 fdefalco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ohdsi.webapi.util;

import org.ohdsi.webapi.exception.BadRequestAtlasException;
import org.ohdsi.webapi.exception.ConceptNotExistException;
import org.ohdsi.webapi.exception.ConversionAtlasException;
import org.ohdsi.webapi.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.support.ErrorMessage;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ohdsi.webapi.vocabulary.ConceptRecommendedNotInstalledException;

/**
 *
 * @author fdefalco
 */

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);
    private static final Pattern DUPLICATE_KEY = Pattern.compile("Detail: Key \\((\\w+)\\)=\\((.*?)\\) already exists");
    private static final String CONCEPT_SET_NAME_CONSTRAINT = "uq_cs_name";
    private static final String CONCEPT_SET_NAME_COLUMN = "concept_set_name";
    private static final int MAX_CAUSE_DEPTH = 32;
    private static final String CONFLICT_MESSAGE = "The request conflicts with existing data";

    @Override
    public Response toResponse(Throwable ex) {
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        LOGGER.error(errorStackTrace.toString());
        Status responseStatus;
        if (ex instanceof DataIntegrityViolationException) {
            responseStatus = Status.CONFLICT;
            ex = new RuntimeException(getConflictMessage(ex));
        } else if (ex instanceof UnauthorizedException || ex instanceof ForbiddenException) {
            responseStatus = Status.FORBIDDEN;
        } else if (ex instanceof NotFoundException) {
            responseStatus = Status.NOT_FOUND;
        } else if (ex instanceof BadRequestException) {
            responseStatus = Status.BAD_REQUEST;
        } else if (ex instanceof UndeclaredThrowableException) {
            Throwable throwable = getThrowable((UndeclaredThrowableException)ex);
            if (Objects.nonNull(throwable)) {
                if (throwable instanceof UnauthorizedException || throwable instanceof ForbiddenException) {
                    responseStatus = Status.FORBIDDEN;
                } else if (throwable instanceof BadRequestAtlasException || throwable instanceof ConceptNotExistException) {
                    responseStatus = Status.BAD_REQUEST;
                    ex = throwable;
                } else if (throwable instanceof ConversionAtlasException) {
                    responseStatus = Status.BAD_REQUEST;
                    // New exception must be created or direct self-reference exception will be thrown
                    ex = new RuntimeException(throwable.getMessage());
                } else {
                    responseStatus = Status.INTERNAL_SERVER_ERROR;
                    ex = new RuntimeException("An exception occurred: " + ex.getClass().getName());
                }
            } else {
                responseStatus = Status.INTERNAL_SERVER_ERROR;
                ex = new RuntimeException("An exception occurred: " + ex.getClass().getName());
            }
        } else if (ex instanceof UserException) {
            responseStatus = Status.INTERNAL_SERVER_ERROR;
            // Create new message to prevent sending error information to client
            ex = new RuntimeException(ex.getMessage());
        } else if (ex instanceof ConceptNotExistException) {
            responseStatus = Status.BAD_REQUEST;
        } else if (ex instanceof ConceptRecommendedNotInstalledException) {
          responseStatus = Status.NOT_IMPLEMENTED;
        } else {
            responseStatus = Status.INTERNAL_SERVER_ERROR;
            // Create new message to prevent sending error information to client
            ex = new RuntimeException("An exception occurred: " + ex.getClass().getName());
        }
        // Clean stacktrace, but keep message
        ex.setStackTrace(new StackTraceElement[0]);
        ErrorMessage errorMessage = new ErrorMessage(ex);
        return Response.status(responseStatus)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getConflictMessage(Throwable ex) {
        try {
            List<String> sqlMessages = getSqlMessages(ex);
            boolean conceptSetName = sqlMessages.stream().anyMatch(m -> m.contains(CONCEPT_SET_NAME_CONSTRAINT));
            for (String message : sqlMessages) {
                Matcher matcher = DUPLICATE_KEY.matcher(message);
                if (matcher.find()) {
                    String column = matcher.group(1);
                    String value = matcher.group(2);
                    if (conceptSetName || CONCEPT_SET_NAME_COLUMN.equals(column)) {
                        return String.format("Concept set name '%s' is already in use", value);
                    }
                    if (column.toLowerCase().contains("name")) {
                        return String.format("Name '%s' is already in use", value);
                    }
                    return CONFLICT_MESSAGE;
                }
            }
            return conceptSetName ? "A concept set with this name already exists" : CONFLICT_MESSAGE;
        } catch (RuntimeException e) {
            LOGGER.warn("Could not derive a conflict message", e);
            return CONFLICT_MESSAGE;
        }
    }

    // Messages of every SQLException in the cause chain, including next exceptions, deepest first.
    private List<String> getSqlMessages(Throwable ex) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<String> messages = new ArrayDeque<>();
        Deque<Throwable> pending = new ArrayDeque<>();
        pending.add(ex);
        while (!pending.isEmpty() && visited.size() < MAX_CAUSE_DEPTH) {
            Throwable current = pending.poll();
            if (!visited.add(current)) {
                continue;
            }
            if (current instanceof SQLException) {
                SQLException sqlException = (SQLException) current;
                if (sqlException.getMessage() != null) {
                    messages.push(sqlException.getMessage());
                }
                if (sqlException.getNextException() != null) {
                    pending.add(sqlException.getNextException());
                }
            }
            if (current.getCause() != null) {
                pending.add(current.getCause());
            }
        }
        return new ArrayList<>(messages);
    }

    private Throwable getThrowable(UndeclaredThrowableException ex) {
        if (Objects.nonNull(ex.getUndeclaredThrowable()) && ex.getUndeclaredThrowable() instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException) ex.getUndeclaredThrowable();
            return ite.getTargetException();
        }
        return null;
    }
}