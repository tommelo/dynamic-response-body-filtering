package com.example.demo;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@ControllerAdvice
public class ResponseBodyFilterAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    public static final String DEFAULT_RESPONSE_BODY_FILTER = "RESPONSE_BODY_FILTER";

    private final SimpleFilterProvider filterProvider = new SimpleFilterProvider();

    private final String[] EMPTY_FIELDS = new String[]{};

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue container,
                                           MediaType mediaType,
                                           MethodParameter methodParameter,
                                           ServerHttpRequest request,
                                           ServerHttpResponse response) {
        FilterProvider filters = container.getFilters();
        ResponseBodyFilter responseBodyFilter = methodParameter.getMethodAnnotation(ResponseBodyFilter.class);

        if (Objects.isNull(responseBodyFilter) || Objects.nonNull(filters)) {
            return;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String filterOutParameter = responseBodyFilter.filterOut();
        String excludeParameter = responseBodyFilter.exclude();
        String delimiter = responseBodyFilter.delimiter();

        String[] filterOutFields = parseFilterAttribute(servletRequest, filterOutParameter, delimiter);
        String[] excludeFields = parseFilterAttribute(servletRequest, excludeParameter, delimiter);
        PropertyFilter filter = getPropertyFilter(filterOutFields, excludeFields);

        filterProvider.addFilter(responseBodyFilter.filter(), filter);
        container.setFilters(filterProvider);
    }

    private PropertyFilter getPropertyFilter(String[] filterOut, String[] exclude) {
        if (exclude.length > 0) {
            return SimpleBeanPropertyFilter.serializeAllExcept(exclude);
        }

        if (filterOut.length > 0) {
            return SimpleBeanPropertyFilter.filterOutAllExcept(filterOut);
        }

        return SimpleBeanPropertyFilter.serializeAll();
    }

    private String[] parseFilterAttribute(HttpServletRequest servletRequest, String parameter, String delimiter) {
        String value = servletRequest.getParameter(parameter);
        if (Objects.isNull(value)) {
            return EMPTY_FIELDS;
        }

        String[] attributes = value.split(delimiter);
        String[] fields = Arrays.stream(attributes).filter(e -> !e.isEmpty()).toArray(String[]::new);

        return fields.length == 0
                ? EMPTY_FIELDS
                : fields;
    }

}