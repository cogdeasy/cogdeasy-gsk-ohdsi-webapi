/*
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
package org.ohdsi.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableValueFactoryProvider implements ValueParamProvider {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    @Override
    public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
        if (parameter.getRawType() == Pageable.class
                && parameter.isAnnotationPresent(Pagination.class)) {
            return PageableValueFactoryProvider::provide;
        }
        return null;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }

    static Pageable provide(ContainerRequest request) {
        MultivaluedMap<String, String> query = request.getUriInfo().getQueryParameters();
        int page = intParam(query.getFirst("page"), DEFAULT_PAGE);
        int size = intParam(query.getFirst("size"), DEFAULT_SIZE);
        List<String> sort = query.containsKey("sort") ? query.get("sort") : Collections.emptyList();

        List<Sort.Order> orders = new ArrayList<>();
        for (String propOrder: sort) {
            String[] propOrderSplit = propOrder.split(",");
            String property = propOrderSplit[0];
            Sort.Direction direction = propOrderSplit.length == 1
                    ? null : Sort.Direction.fromOptionalString(propOrderSplit[1]).orElse(null);
            orders.add(direction == null ? Sort.Order.by(property) : new Sort.Order(direction, property));
        }

        return PageRequest.of(page, size, orders.isEmpty() ? Sort.unsorted() : Sort.by(orders));
    }

    private static int intParam(String value, int defaultValue) {
        return value == null ? defaultValue : Integer.parseInt(value);
    }
}
