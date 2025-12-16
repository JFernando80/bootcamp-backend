package br.com.impacta.bootcamp.commons.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Util {

    public static String toSneakCase(String valor) {
        String result = "";

        char c = valor.charAt(0);
        result = result + Character.toLowerCase(c);

        for (int i = 1; i < valor.length(); i++) {

            char ch = valor.charAt(i);

            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result
                        = result
                        + Character.toLowerCase(ch);
            } else {
                result = result + ch;
            }
        }

        // return the result
        return result;
    }

    public static boolean isTipoDTO(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equalsIgnoreCase(ClasseDTO.class.getName()) ||
                    annotation.annotationType().getName().equalsIgnoreCase(ValidationClass.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
