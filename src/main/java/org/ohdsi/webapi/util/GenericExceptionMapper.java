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
import org.hibernate.exception.ConstraintViolationException;
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
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
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
    private static final String CONFLICT_MESSAGE = "The request conflicts with existing data";
    // PostgreSQL unique violation, e.g.
    // ERROR: duplicate key value violates unique constraint "uq_cs_name"
    //   Detail: Key (concept_set_name)=(Diabetes) already exists.
    private static final Pattern UNIQUE_CONSTRAINT = Pattern.compile("unique constraint \"([^\"]+)\"");
    private static final Pattern DUPLICATE_KEY_DETAIL = Pattern.compile("Key \\(([^)]+)\\)=\\((.*)\\) already exists");
    private static final int MAX_CAUSES = 32;
    // Unique name constraints added by V2.8.0.20190424150601__add-unique-name-constraint-to-entities.sql
    private static final Map<String, UniqueName> UNIQUE_NAME_CONSTRAINTS;
    static {
        Map<String, UniqueName> constraints = new HashMap<>();
        constraints.put("uq_cs_name", new UniqueName("Concept set", "concept_set_name"));
        constraints.put("uq_cd_name", new UniqueName("Cohort definition", "name"));
        constraints.put("uq_cc_name", new UniqueName("Characterization", "name"));
        constraints.put("uq_fe_name", new UniqueName("Feature analysis", "name"));
        constraints.put("uq_pw_name", new UniqueName("Pathway analysis", "name"));
        constraints.put("uq_ir_name", new UniqueName("Incidence rate analysis", "name"));
        constraints.put("uq_es_name", new UniqueName("Estimation analysis", "name"));
        constraints.put("uq_pd_name", new UniqueName("Prediction analysis", "name"));
        UNIQUE_NAME_CONSTRAINTS = Collections.unmodifiableMap(constraints);
    }

    @Override
    public Response toResponse(Throwable ex) {
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        LOGGER.error(errorStackTrace.toString());
        Status responseStatus;
        if (ex instanceof DataIntegrityViolationException) {
            responseStatus = Status.CONFLICT;
            ex = new RuntimeException(getConflictMessage((DataIntegrityViolationException) ex));
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

    /**
     * Client-safe message for a constraint violation. For a duplicate entity name it names the
     * clashing name (the value the caller submitted); it never returns database text.
     */
    private String getConflictMessage(DataIntegrityViolationException ex) {
        Iterable<Throwable> causes = getCauses(ex);
        String constraintName = null;
        for (Throwable cause : causes) {
            if (cause instanceof ConstraintViolationException && ((ConstraintViolationException) cause).getConstraintName() != null) {
                constraintName = ((ConstraintViolationException) cause).getConstraintName();
                break;
            }
        }
        if (constraintName == null) {
            for (Throwable cause : causes) {
                String violated = getViolatedConstraint(cause);
                if (violated != null) {
                    constraintName = violated;
                    break;
                }
            }
        }
        UniqueName uniqueName = constraintName == null ? null : UNIQUE_NAME_CONSTRAINTS.get(constraintName.toLowerCase(Locale.ROOT));
        if (uniqueName == null) {
            return CONFLICT_MESSAGE;
        }
        String duplicatedName = null;
        for (Throwable cause : causes) {
            if (constraintName.equalsIgnoreCase(getViolatedConstraint(cause))) {
                Matcher detail = DUPLICATE_KEY_DETAIL.matcher(cause.getMessage());
                if (detail.find() && uniqueName.column.equals(detail.group(1))) {
                    duplicatedName = detail.group(2);
                    break;
                }
            }
        }
        return duplicatedName == null
                ? String.format("%s name is already in use", uniqueName.entity)
                : String.format("%s name '%s' is already in use", uniqueName.entity, duplicatedName);
    }

    // The unique constraint a database error reports. A BatchUpdateException message also echoes the
    // statement, so only the server error itself (its next exception) is read.
    private String getViolatedConstraint(Throwable cause) {
        if (!(cause instanceof SQLException) || cause instanceof BatchUpdateException || cause.getMessage() == null) {
            return null;
        }
        Matcher constraint = UNIQUE_CONSTRAINT.matcher(cause.getMessage());
        return constraint.find() ? constraint.group(1) : null;
    }

    // The exception, its causes and any chained SQLExceptions, guarded against cycles.
    private Iterable<Throwable> getCauses(Throwable ex) {
        Set<Throwable> causes = new LinkedHashSet<>();
        Deque<Throwable> pending = new ArrayDeque<>();
        pending.add(ex);
        while (!pending.isEmpty() && causes.size() < MAX_CAUSES) {
            Throwable current = pending.poll();
            if (!causes.add(current)) {
                continue;
            }
            if (current.getCause() != null) {
                pending.add(current.getCause());
            }
            if (current instanceof SQLException && ((SQLException) current).getNextException() != null) {
                pending.add(((SQLException) current).getNextException());
            }
        }
        return causes;
    }

    private Throwable getThrowable(UndeclaredThrowableException ex) {
        if (Objects.nonNull(ex.getUndeclaredThrowable()) && ex.getUndeclaredThrowable() instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException) ex.getUndeclaredThrowable();
            return ite.getTargetException();
        }
        return null;
    }

    private static final class UniqueName {
        private final String entity;
        private final String column;

        private UniqueName(String entity, String column) {
            this.entity = entity;
            this.column = column;
        }
    }
}