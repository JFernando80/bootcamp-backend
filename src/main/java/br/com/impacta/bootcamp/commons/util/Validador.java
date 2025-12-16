package br.com.impacta.bootcamp.commons.util;

import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validador {

    private static final String PATTERN_DATE = "(^[+,-])([0-9]*)([$(ymd)])";
    private static final Pattern pattern = Pattern.compile(PATTERN_DATE);

    public static <T> boolean validador(T objeto, boolean subclasse, List<String> erros)  {
        Beans beans = new Beans();
        Class<?> classe = objeto.getClass();
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validation.class)) {
                Validation validation = field.getAnnotation(Validation.class);
                try{
                    String fieldNome = Util.toSneakCase(field.getName());
                    field.setAccessible(true);
                    if (validation.required()) {
                        String valor = String.valueOf(field.get(objeto));
                        if ((field.get(objeto) instanceof Double && valor.equalsIgnoreCase("0.0")) ||
                                (valorIsNull(valor))) {
                            erros.add("O campo "+ fieldNome +" é obrigatório. "+beans.toHexString(fieldNome)+". "+beans.toHexString(fieldNome));
                        }
                    }

                    if (validation.lengthMax() != Long.MAX_VALUE) {
                        String value = String.valueOf(field.get(objeto));

                        if (!valorIsNull(value)) {
                            if (field.get(objeto) instanceof Double || value.equalsIgnoreCase("0.0")) {
                                double valor = Double.parseDouble(value);
                                if (valor > validation.lengthMax()) {
                                    erros.add("O valor do campo "+fieldNome+" deve ser menor que "+validation.lengthMax()+". "+beans.toHexString(fieldNome));
                                }
                            } else if (field.get(objeto) instanceof Long) {
                                long valor = Long.parseLong(value);
                                if (valor > validation.lengthMax()) {
                                    erros.add("O valor do campo "+fieldNome+" deve ser menor que "+validation.lengthMax());
                                }
                            } else {
                                if (value.length() > validation.lengthMax()) {
                                    erros.add("O campo "+fieldNome+" deve ter menos que "+validation.lengthMax()+" caracter/es. "+beans.toHexString(fieldNome));
                                }
                            }
                        }
                    }

                    if (validation.lengthMin() != Long.MIN_VALUE) {
                        String value = String.valueOf(field.get(objeto));

                        if ((field.get(objeto) instanceof Double && !value.equalsIgnoreCase("0.0"))) {
                            if (!valorIsNull(value)) {
                                double valor = Double.parseDouble(value);
                                if (valor < validation.lengthMin()) {
                                    erros.add("O valor do campo "+fieldNome+" deve ser maior que "+validation.lengthMin()+". "+beans.toHexString(fieldNome));
                                }
                            }
                        } else if (field.get(objeto) instanceof Long) {
                            if (!valorIsNull(value)) {
                                long valor = Long.parseLong(value);
                                if (valor < validation.lengthMin()) {
                                    erros.add("O valor do campo "+fieldNome+" deve ser maior que "+validation.lengthMin());
                                }
                            }
                        } else {
                            if (value.length() < validation.lengthMin()) {
                                erros.add("O campo "+fieldNome+" deve ter mais que "+validation.lengthMin()+" caracter/es. "+beans.toHexString(fieldNome));
                            }
                        }
                    }

                    if (!validation.dateMax().equals("")) {
                        String padrao = validation.dateMax();
                        String value = String.valueOf(field.get(objeto));
                        Matcher matcher = pattern.matcher(validation.dateMax());
                        if (matcher.matches()) {
                            validarDateMax(padrao, value, erros, fieldNome);
                        }
                    }

                    if (!validation.dateMin().equals("")) {
                        String padrao = validation.dateMin();
                        String value = String.valueOf(field.get(objeto));
                        Matcher matcher = pattern.matcher(validation.dateMin());
                        if (matcher.matches()) {
                            validarDateMin(padrao, value, erros, fieldNome);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new BusinessRuleException("Erro leitura dos campos: "+e.getMessage());
                }
            } else if (field.isAnnotationPresent(ValidationClass.class)) {
                field.setAccessible(true);

                try {
                    Object o =  field.get(objeto);
                    if (o != null) {
                        validador(o, true, erros);
                    } else {
                        erros.add("A classe "+field.getName()+" nao pode ser nula");
                    }
                } catch (Exception e) {
                    throw new BusinessRuleException(e.getMessage());
                }
            }
        }
        return false;
    }

    private static <T> void limparCamposStringVazio(T objeto)  {
        for (Field field : objeto.getClass().getDeclaredFields()) {
            try {
                if (field.getType().getSimpleName().equalsIgnoreCase("String")) {
                    field.setAccessible(true);
                    String value = String.valueOf(field.get(objeto));
                    if (value.equalsIgnoreCase("")) {
                        field.set(objeto, null);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BusinessRuleException("Erro leitura dos campos: "+e.getMessage());
            }

        }
    }

    public static <T> boolean validador(T objeto)  {
        limparCamposStringVazio(objeto);
        Class<?> classe = objeto.getClass();
        Beans beans = new Beans();
        List<String> erros = new ArrayList<>();
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validation.class)) {
                Validation validation = field.getAnnotation(Validation.class);
                try{
                    String fieldNome = Util.toSneakCase(field.getName());
                    field.setAccessible(true);
                    if (validation.required()) {
                        String valor = String.valueOf(field.get(objeto));
                        if ((field.get(objeto) instanceof Double && valor.equalsIgnoreCase("0.0")) ||
                                (valorIsNull(valor))) {
                            erros.add("O campo "+ fieldNome +" é obrigatório. "+beans.toHexString(fieldNome));
                        }
                    }

                    if (validation.lengthMax() != Long.MAX_VALUE) {
                        String value = String.valueOf(field.get(objeto));

                        if (!valorIsNull(value)) {
                            if (field.get(objeto) instanceof Double ) {
                                double valor = Double.parseDouble(value);
                                if (valor > validation.lengthMax()) {
                                    erros.add("O valor do campo "+fieldNome+" deve ser menor que "+validation.lengthMax()+". "+beans.toHexString(fieldNome));
                                }
                            } else if (field.get(objeto) instanceof Long) {
                                if (!valorIsNull(value)) {
                                    long valor = Long.parseLong(value);
                                    if (valor > validation.lengthMax()) {
                                        erros.add("O valor do campo " + fieldNome + " deve ser menor que " + validation.lengthMax()+". "+beans.toHexString(fieldNome));
                                    }
                                }
                            } else {
                                if (value.length() > validation.lengthMax()) {
                                    erros.add("O campo "+fieldNome+" deve ter menos que "+validation.lengthMax()+" caracter/es. "+beans.toHexString(fieldNome));
                                }
                            }
                        }
                    }

                    if (validation.lengthMin() != Long.MIN_VALUE) {
                        String value = String.valueOf(field.get(objeto));

                        if (!valorIsNull(value)) {
                            if ((field.get(objeto) instanceof Double)) {
                                if (!valorIsNull(value)) {
                                    double valor = Double.parseDouble(value);
                                    if (valor < validation.lengthMin()) {
                                        erros.add("O valor do campo "+fieldNome+" deve ser maior que "+validation.lengthMin()+". "+beans.toHexString(fieldNome));
                                    }
                                }
                            } else if (field.get(objeto) instanceof Long) {
                                if (!valorIsNull(value)) {
                                    long valor = Long.parseLong(value);
                                    if (valor < validation.lengthMin()) {
                                        erros.add("O valor do campo "+fieldNome+" deve ser maior que "+validation.lengthMin()+". "+beans.toHexString(fieldNome));
                                    }
                                }
                            } else {
                                if (value.length() == 0 || value.length() < validation.lengthMin()) {
                                    erros.add("O campo "+fieldNome+" deve ter mais que "+validation.lengthMin()+" caracter/es. " +beans.toHexString(fieldNome));
                                }
                            }
                        }

                    }

                    if (!validation.dateMax().equals("")) {
                        String padrao = validation.dateMax();
                        String value = String.valueOf(field.get(objeto));
                        Matcher matcher = pattern.matcher(validation.dateMax());
                        if (matcher.matches() && !valorIsNull(value) && !value.isEmpty()) {
                            validarDateMax(padrao, value, erros, fieldNome);
                        }
                    }

                    if (!validation.dateMin().equals("")) {
                        String padrao = validation.dateMin();
                        String value = String.valueOf(field.get(objeto));
                        Matcher matcher = pattern.matcher(validation.dateMin());
                        if (matcher.matches() && !valorIsNull(value) && !value.isEmpty()) {
                            validarDateMin(padrao, value, erros, fieldNome);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new BusinessRuleException("Erro leitura dos campos: "+e.getMessage());
                }
            } else if (field.isAnnotationPresent(ValidationClass.class)) {
                try {
                    field.setAccessible(true);
                    Object o =  field.get(objeto);
                    if (o != null) {
                        validador(o, true, erros);
                    } else {
                        erros.add("A classe "+field.getName()+" nao pode ser nula");
                    }
                } catch (Exception e) {
                    throw new BusinessRuleException(e.getMessage());
                }
            }
        }

        if (!erros.isEmpty()) {
            throw new BusinessRuleException(erros);
        }
        return false;
    }

    private static void validarDateMax(String padrao, String value, List<String> erros, String fieldNome) {

        try {
            if (!valorIsNull(value)) {
                int valor = Integer.parseInt(padrao.substring(1, padrao.length()-1));
                Beans beans = new Beans();
                Date data = beans.converterStringToDate(value);
                DateTime dateTime = DateTime.now();

                String primeiro = padrao.substring(0,1);
                String ultimo = padrao.substring(padrao.length()-1);

                if (primeiro.equals("+")) {
                    if (ultimo.equals("y")) {
                        dateTime = dateTime.plusYears(valor);
                    } else if (ultimo.equals("m")) {
                        dateTime = dateTime.plusMonths(valor);
                    } else {
                        dateTime = dateTime.plusDays(valor);
                    }

                    if (data.after(dateTime.toDate())) {
                        erros.add("O campo " + fieldNome + " é maior que o permitido. Data Máxima: "+beans.converterDateToString(dateTime.toDate())+". "+beans.toHexString(fieldNome));
                    }
                } else {
                    if (ultimo.equals("y")) {
                        dateTime = dateTime.minusYears(valor).minusDays(1);
                    } else if (ultimo.equals("m")) {
                        dateTime = dateTime.minusMonths(valor).minusDays(1);
                    } else {
                        dateTime = dateTime.minusDays(valor);
                    }

                    if (data.after(dateTime.toDate())) {
                        erros.add("O campo "  + fieldNome +  " é maior que o permitido. Data Máxima: "+beans.converterDateToString(dateTime.toDate())+". "+beans.toHexString(fieldNome));
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessRuleException(e.getMessage());
        }

    }

    private static void validarDateMin(String padrao, String value, List<String> erros, String fieldNome) {

        try {
            if (!valorIsNull(value)) {
                int valor = Integer.parseInt(padrao.substring(1, padrao.length()-1));
                Beans beans = new Beans();
                Date data = beans.converterStringToDate(value);
                DateTime dateTime = DateTime.now();

                String primeiro = padrao.substring(0,1);
                String ultimo = padrao.substring(padrao.length()-1);

                if (primeiro.equals("+")) {
                    if (ultimo.equals("y")) {
                        dateTime = dateTime.plusYears(valor);
                    } else if (ultimo.equals("m")) {
                        dateTime = dateTime.plusMonths(valor);
                    } else {
                        dateTime = dateTime.plusDays(valor);
                    }

                    if (data.before(dateTime.toDate())) {
                        erros.add("O campo " + fieldNome + " é menor que o permitido. Data Minima maior que "+beans.converterDateToString(dateTime.toDate())+". "+beans.toHexString(fieldNome));
                    }
                } else {
                    if (ultimo.equals("y")) {
                        dateTime = dateTime.minusYears(valor).minusDays(1);
                    } else if (ultimo.equals("m")) {
                        dateTime = dateTime.minusMonths(valor).minusDays(1);
                    } else {
                        dateTime = dateTime.minusDays(valor);
                    }

                    if (data.before(dateTime.toDate())) {
                        erros.add("O campo "  + fieldNome +  " é menor que o permitido. Data Minima maior que "+beans.converterDateToString(dateTime.toDate())+". "+beans.toHexString(fieldNome));
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessRuleException(e.getMessage());
        }

    }

    private static boolean valorIsNull(String valor) {
        return valor == null || valor.equalsIgnoreCase("null") ;
    }

    public static <T> boolean validadorAuxiliar(T objeto, boolean subclasse)  {
        Class<?> classe = objeto.getClass();
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(ValidationAux.class)) {
                ValidationAux validation = field.getAnnotation(ValidationAux.class);
                try{
                    field.setAccessible(true);
                    if (validation.required()) {
                        String valor = String.valueOf(field.get(objeto));
                        if (valor.equalsIgnoreCase("null")) {
                            if (subclasse) {
                                throw new BusinessRuleException(validation.subClasse());
                            } else {
                                throw new BusinessRuleException(validation.message());
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new BusinessRuleException("Erro leitura dos campos: "+e.getMessage());
                }
            } else if (field.isAnnotationPresent(ValidationClassAux.class)) {
                field.setAccessible(true);
                try {
                    validadorAuxiliar(Class.forName(field.getType().getName()).getConstructor().newInstance(), true);
                } catch (Exception e) {
                    throw new BusinessRuleException(e.getMessage());
                }
            }
        }
        return false;
    }

    public static <T> boolean validadorAuxiliar(T objeto)  {
        Class<?> classe = objeto.getClass();
        for (Field field : classe.getDeclaredFields()) {
            if (field.isAnnotationPresent(ValidationAux.class)) {
                ValidationAux validation = field.getAnnotation(ValidationAux.class);
                try{
                    field.setAccessible(true);
                    if (validation.required()) {
                        String valor = String.valueOf(field.get(objeto));
                        if (valor.equalsIgnoreCase("null")) {
                            throw new BusinessRuleException(validation.message());
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new BusinessRuleException("Erro leitura dos campos: "+e.getMessage());
                }
            } else if (field.isAnnotationPresent(ValidationClassAux.class)) {
                field.setAccessible(true);
                try {
                    Object o =  field.get(objeto);
                    validadorAuxiliar(o, true);
                } catch (Exception e) {
                    throw new BusinessRuleException(e.getMessage());
                }
            }
        }
        return false;
    }
}
