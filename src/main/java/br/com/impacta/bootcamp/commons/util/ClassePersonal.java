package br.com.impacta.bootcamp.commons.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassePersonal {

    String busca() default "id";
    String exibe() default "";
}
