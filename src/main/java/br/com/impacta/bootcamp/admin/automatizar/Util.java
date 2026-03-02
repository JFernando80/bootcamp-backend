package br.com.impacta.bootcamp.admin.automatizar;

import br.com.impacta.bootcamp.commons.util.*;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Util {

    public static String getVariavel(String variavel) {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("String", "String");
        mapa.put("Long", "int");
        mapa.put("long", "int");
        mapa.put("Integer", "int");
        mapa.put("Boolean", "bool");
        mapa.put("Double", "double");

        if (mapa.containsKey(variavel)) {
            return mapa.get(variavel);
        }

        return variavel;
    }

    public static String returnType(Class type) {
        String nome = type.getSimpleName().toLowerCase();
        if (nome.equalsIgnoreCase("string") || type.isEnum() ) {
            return "''";
        } else if (nome.equalsIgnoreCase("double") || nome.equalsIgnoreCase("long")
                || nome.equalsIgnoreCase("integer") || nome.equalsIgnoreCase("int")) {
            return "0";
        } else if (nome.equalsIgnoreCase("boolean")) {
            return "false";
        }

        return "123456";
    }

    public static boolean hasEnum( Class c) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();

            for (Field field : fields) {
                if (field.getType().isEnum()) {
                    return true;
                }
            }
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean isClasseCriada(Field field) {
        List<String> conhecidas = new ArrayList<>();
        conhecidas.add("String");
        conhecidas.add("double");
        conhecidas.add("long");
        conhecidas.add("boolean");
        conhecidas.add("date");
        conhecidas.add("list");
        conhecidas.add("byte");
        conhecidas.add("UUID");
        conhecidas.add("byte[]");

        return conhecidas.stream()
                .noneMatch(f -> f.equalsIgnoreCase(field.getType().getSimpleName()));
    }

    public static String buscarTipoField(Field field, String busca) {
        Class c = field.getType();
        Field[] fields = c.getDeclaredFields();

        for (Field f1 : fields) {
            if (f1.getName().equalsIgnoreCase(busca)) {
                return f1.getType().getSimpleName();
            }
        }

        return null;
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

    public static boolean hasTipoDTO(Class c) {
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            ClasseDTO classeDTO = field.getAnnotation(ClasseDTO.class);
            if (classeDTO != null) {
                return true;
            }
        }

        return false;

    }

    public static boolean hasTipoClassePersonal(Class c) {
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            ClassePersonal classeDTO = field.getAnnotation(ClassePersonal.class);
            if (classeDTO != null) {
                return true;
            }
        }

        return false;

    }

    public static JSONObject isEnum(Field field) {
        JSONObject json = new JSONObject();
        json.put("enum", false);

        String tipo = "enums";
        File pathEnums = new File(CriarDepencias.PATH, tipo);
        if (pathEnums.isDirectory()) {
            File[] files = pathEnums.listFiles();
            for (File file : files) {
                String nome = file.getName();
                nome = nome.substring(0, nome.lastIndexOf("."));
                if (nome.equalsIgnoreCase(field.getName())) {
                    json.put("enum", true);
                    json.put("nome", nome);
                }
            }
        }

        return json;
    }

    public static void montarImports(Class c, PrintWriter gravarArq) {
        String tipo = "enums";
        File pathEnums = new File(CriarDepencias.PATH, tipo);
        if (pathEnums.isDirectory()) {
            Field[] fields = c.getDeclaredFields();
            File[] files = pathEnums.listFiles();

            for (Field field : fields) {
                for (File file : files) {
                    String nome = file.getName();
                    nome = nome.substring(0, nome.lastIndexOf("."));
                    if (nome.equalsIgnoreCase(field.getName())) {
                        try {
                            Class aClass = Class.forName(CriarDepencias.PACKAGE_BASE.concat(".").concat(tipo).concat(".").concat(nome));
                            gravarArq.println("import ".concat(aClass.getName()).concat(";"));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (Util.isTipoDTO(field)) {

                }
            }

        }
    }

    public static Field getFieldByName(String name, Field[] fields) {
        for (Field field : fields) {
            if (field.getName().toLowerCase().contains(name)) {
                return field;
            }
        }

        return null;
    }

    public static void sleep() {
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void montarPath(Class clazz) {
        String nomeArquivo = "deivid.txt";
        File file = new File(nomeArquivo);
        String pacote = clazz.getPackage().getName();

        String pathBase = file.getAbsolutePath();
        pathBase = pathBase.substring(0, pathBase.length()-nomeArquivo.length());
        pathBase = pathBase.concat(CriarDepencias.SOURCE);
        while (pacote.contains(".")) {
            pacote = pacote.replace(".", "/");
        }
        pathBase = pathBase.concat("/").concat(pacote);
        int ultimaBarra = pathBase.lastIndexOf("/");

        pathBase = pathBase.substring(0, ultimaBarra);

        pacote = clazz.getPackage().getName();
        ultimaBarra = pacote.lastIndexOf(".");

        file.delete();

        CriarDepencias.PACKAGE_BASE = pacote.substring(0, ultimaBarra);
        CriarDepencias.PATH = pathBase;
    }

    public static void montarPathTest(Class clazz) {
        String nomeArquivo = "deivid.txt";
        File file = new File(nomeArquivo);
        String pacote = clazz.getPackage().getName();

        String pathBase = file.getAbsolutePath();
        pathBase = pathBase.substring(0, pathBase.length()-nomeArquivo.length());
        pathBase = pathBase.concat(CriarDepencias.SOURCE_TEST);
        while (pacote.contains(".")) {
            pacote = pacote.replace(".", "/");
        }
        pathBase = pathBase.concat("/").concat(pacote);
        int ultimaBarra = pathBase.lastIndexOf("/");

        pathBase = pathBase.substring(0, ultimaBarra);

        pacote = clazz.getPackage().getName();
        ultimaBarra = pacote.lastIndexOf(".");

        file.delete();

        CriarDepencias.PACKAGE_BASE = pacote.substring(0, ultimaBarra);
        CriarDepencias.PATH_TEST = pathBase;
    }

    public static String primeiraMaiuscula(String valor) {
        return valor.substring(0,1).toUpperCase().concat(valor.substring(1));
    }

    public static String primeiraMinuscula(String valor) {
        return valor.substring(0,1).toLowerCase().concat(valor.substring(1));
    }

    public static void criarPasta(String nome) {
        File file = new File(CriarDepencias.PATH.concat("/").concat(nome));

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean hasClassePersonal(Class c) {
        Field[] fields= c.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal !=null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasClasseUnico(Class c) {
        Field[] fields= c.getDeclaredFields();
        for (Field field : fields) {
            Unico personal = field.getAnnotation(Unico.class);
            if (personal !=null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasValidation(Class c) {
        Field[] fields= c.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            boolean b = Stream.of(annotations).anyMatch(f -> f.annotationType().getSimpleName().equalsIgnoreCase(Validation.class.getSimpleName()));

            if (b) {
                return b;
            }
        }

        return false;
    }

    public static boolean hasCompany(Class c) {
        Field[] fields= c.getDeclaredFields();
        return Stream.of(fields).anyMatch(f -> f.getType().getSimpleName().equalsIgnoreCase("company"));
    }

    public static boolean hasFilial(Class c) {
        Field[] fields= c.getDeclaredFields();
        return Stream.of(fields)
                .filter(f -> f.getType().getSimpleName().equalsIgnoreCase("company"))
                .anyMatch(f -> f.getName().equalsIgnoreCase("filial"));
    }

    public static String companyCnpj(Class c) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();

            Field field = Stream.of(fields).filter(f -> f.getName().equalsIgnoreCase("companycnpj")).findFirst().orElse(null);

            if (field != null) {
                return field.getName();
            }

        } catch (Exception e) {

        }
        return null;
    }

    public static boolean hasDate(Class c) {
        Field[] fields= c.getDeclaredFields();
        return Stream.of(fields).anyMatch(f -> f.getType().getSimpleName().equalsIgnoreCase("date"));
    }

    public static String toSpaceCase(String valor) {
        String result = "";

        char c = valor.charAt(0);
        result = result + c;

        for (int i = 1; i < valor.length(); i++) {

            char ch = valor.charAt(i);

            if (Character.isUpperCase(ch)) {
                result = result + ' ';
                result = result + ch;
            } else {
                result = result + ch;
            }
        }

        // return the result
        return result;
    }

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
}
