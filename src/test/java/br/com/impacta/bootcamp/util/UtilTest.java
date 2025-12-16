package br.com.impacta.bootcamp.util;


import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.commons.util.ValidationClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class UtilTest {

    public static boolean olharAnotacaoRequired(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equalsIgnoreCase(Validation.class.getSimpleName())) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                boolean required = valid.required();
                long lengthMax = valid.lengthMax();
                long lengthMin = valid.lengthMin();
                String dataMin = valid.dateMin();
                String dataMax = valid.dateMax();

                return required;
            } else if (annotation.annotationType().getSimpleName().equalsIgnoreCase(ValidationClass.class.getSimpleName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean olharAnotacaoMin(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equalsIgnoreCase(Validation.class.getSimpleName())) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                boolean required = valid.required();
                long lengthMax = valid.lengthMax();
                long lengthMin = valid.lengthMin();
                String dataMin = valid.dateMin();
                String dataMax = valid.dateMax();

                if (lengthMin != Long.MIN_VALUE) {
                    return true;
                } else if (!dataMin.equalsIgnoreCase("")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean olharAnotacaoMax(Field field) {
        Validation valid = field.getDeclaredAnnotation(Validation.class);
        if (valid != null) {
            boolean required = valid.required();
            long lengthMax = valid.lengthMax();
            long lengthMin = valid.lengthMin();
            String dataMin = valid.dateMin();
            String dataMax = valid.dateMax();

            if (lengthMax != Long.MAX_VALUE) {
                return true;
            } else if (!dataMax.equalsIgnoreCase("")) {
                return true;
            }
        }

        return false;
    }

    public static Long olharAnotacaoLengthMin(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equalsIgnoreCase(Validation.class.getSimpleName())) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                boolean required = valid.required();
                long lengthMax = valid.lengthMax();
                long lengthMin = valid.lengthMin();
                String dataMin = valid.dateMin();
                String dataMax = valid.dateMax();

                return lengthMin;
            }
        }

        return null;
    }

    public static Long olharAnotacaoLengthMax(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equalsIgnoreCase(Validation.class.getSimpleName())) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                boolean required = valid.required();
                long lengthMax = valid.lengthMax();
                long lengthMin = valid.lengthMin();
                String dataMin = valid.dateMin();
                String dataMax = valid.dateMax();

                return lengthMax;
            }
        }

        return null;
    }
}
