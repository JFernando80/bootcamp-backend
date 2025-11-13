package br.com.impacta.bootcamp.commons.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationAux {

    long lengthMax() default 9999;
    long lengthMin() default 0;
    boolean required()  default false;
    String message() default "";
    String subClasse() default "";
}
