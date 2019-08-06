/*-
 * <<
 * sag
 * ==
 * Copyright (C) 2019 sia
 * ==
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
 * >>
 */


package com.creditease.gateway.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * Swagger DocumentationConfig
 * 
 * @author peihua
 * 
 **/

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationConfig.class);

    private final RouteLocator routeLocator;

    private static final String PROTOCAL = "v2/api-docs";

    private static final String VERSION = "1.0";

    @Value("${spring.application.name}")
    private String appName;

    public DocumentationConfig(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SwaggerResource> get() {

        LOGGER.info(">start get swagger infomation .. ");

        @SuppressWarnings("rawtypes")
        List resources = new LinkedList<>();

        List<Route> routes = routeLocator.getRoutes();

        Collections.sort(routes, new Comparator<Route>() {

            @Override
            public int compare(Route o1, Route o2) {

                return o1.getId().compareTo(o2.getId());
            }

        });

        routes.forEach(route -> {
            resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", PROTOCAL), VERSION));

        });
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {

        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
