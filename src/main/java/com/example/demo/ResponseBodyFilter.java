package com.example.demo;

import java.lang.annotation.*;

import static com.example.demo.ResponseBodyFilterAdvice.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBodyFilter {

    /**
     * The name of the request parameter containing
     * the list of attributes to be filtered out.
     * The default value is "fields".
     * E.g.:
     * ?fields=id,firstName,LastName
     *
     * @return filterOut Request parameter name
     */
    String filterOut() default "fields";

    /**
     * The name of the request parameter containing
     * the list of attributes to be excluded.
     * The default value is "exclude".
     * E.g.:
     * ?exclude=id,firstName
     *
     * @return filterOut Request parameter name
     */
    String exclude() default "exclude";

    /**
     * The delimiter character or regex
     * to split the list of attributes.
     * The default delimiter is ","(comma).
     *
     * @return delimiter The delimiter
     */
    String delimiter() default "\\s*,\\s*";

    /**
     * The filter name.
     *
     * @return filter The filter name.
     * @see {@link com.fasterxml.jackson.annotation.JsonFilter}
     */
    String filter() default DEFAULT_RESPONSE_BODY_FILTER;
}