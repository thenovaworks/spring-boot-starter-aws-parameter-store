package io.symplesims.spring.aws.ssm.autoconfigure;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SsmParameterValue {
    String value();

    ValueType type() default ValueType.STRING;

    /**
     * Whether attribute name of Map is fullname or not. It can only work true if ValueType.MAP.
     */
    boolean fullname() default false;
}
