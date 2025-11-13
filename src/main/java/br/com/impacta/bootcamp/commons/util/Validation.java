package br.com.impacta.bootcamp.commons.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {

    long lengthMax() default Long.MAX_VALUE;
    long lengthMin() default Long.MIN_VALUE;

    boolean cpfvalido() default false;

    //para dateMin/DateMax valores extra = -18y,
    String dateMin() default "";
    String dateMax() default "";
    String datePattern() default "";
    boolean required()  default false;
    String message() default "";
    String subClasse() default "";
}
