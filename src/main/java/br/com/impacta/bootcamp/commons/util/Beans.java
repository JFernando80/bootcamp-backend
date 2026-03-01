package br.com.impacta.bootcamp.commons.util;

import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.admin.model.PermissionGroup;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.dto.PermissionsDTO;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.*;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.model.Content;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class Beans {

    private static final String HOME_DIR = System.getProperty("user.home")+"/ongs/temp/";

    public static final String HOME_FILES = System.getProperty("user.home")+"/ongs/arquivos/";

    public static final String HOME_IA = System.getProperty("user.home")+"/deploy/production/ongia/ativ";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private AES aes;

    @Value("${ambiente}")
    private String ambiente;


    private String patternHora = "HH:mm";
    private String pattern = "dd/MM/yyyy";


    public byte[] gerarRelatorio(Map<String, Object> parametros, InputStream inputStream, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()){
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parametros, connection);

            return  JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            log.info("Erro ao gerar o relatorio. Mensagem: {}", e.getMessage());
            e.printStackTrace();
            throw new BusinessRuleException(e.getMessage());
        } catch (Exception  e) {
            e.printStackTrace();
            log.info("Erro ao gerar o relatorio. Mensagem: {}", e.getMessage());
            throw new BusinessRuleException(e.getMessage());
        }
    }
    public void podeAcessar(Content content, PermissoesEnum permission) {
        boolean existe = false;
        for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
            if (dto.getPermission().equals(permission.getSerial())) {
                existe = true;
                break;
            }
        }

        if (!existe) {
            throw new BusinessRuleException("Usuario não pode acessar este recurso");
        }
    }

    public String getFieldJson(JSONObject jsonObject, String campo) {
        if (jsonObject.has(campo)) {
            return jsonObject.getString(campo);
        }

        return "";
    }

    public Long dateToLong(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public Date longToDate(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.getTime();
    }

    public Pageable montarPageable(int pagina, int offset, List<SearchCriteriaDTO> lista) {
        for (SearchCriteriaDTO dto : lista) {
            if (dto.getKey().equalsIgnoreCase("ordem")) {
                if (dto.getValue() != null) {
                    String valor = dto.getValue().toString();
                    return PageRequest.of(pagina-1, offset, Sort.by(changeDotToCamelCase(valor)));
                }
            }
        }
        return PageRequest.of(pagina-1, offset, Sort.by("id"));
    }

    private boolean hasOrdem(List<SearchCriteriaDTO> lista) {
        for (SearchCriteriaDTO dto : lista) {
            if (dto.getKey().equalsIgnoreCase("ordem")) {
                if (Objects.nonNull(dto.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Calendar newCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, 0, 0,0 );
        return calendar;
    }

    public Calendar zerarHoraMinutoSegundo(Calendar calendar) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(calendar.getTime());
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        return calendar1;
    }

    public Pageable montarPageable(int pagina, int offset, List<SearchCriteriaDTO> lista, String parametro) {
        if (parametro == null || hasOrdem(lista)) {
            return montarPageable(pagina, offset, lista);
        }
        return PageRequest.of(pagina-1, offset, Sort.by(parametro));
    }

    public Pageable montarPageableInverse(int pagina, int offset, List<SearchCriteriaDTO> lista, String parametro) {
        if (parametro == null || hasOrdem(lista)) {
            return montarPageable(pagina, offset, lista);
        }
        return PageRequest.of(pagina-1, offset, Sort.by(parametro).descending());
    }

    private String changeDotToCamelCase(String valor) {
        while (valor.contains(".")) {
            int dot = valor.indexOf(".");
            String aux1 = valor.substring(0, dot);
            String aux2 = valor.substring(dot+1, valor.length());
            valor = aux1+aux2.substring(0,1).toUpperCase()+aux2.substring(1);
        }
        return valor;
    }

    public UUID isUUID(String valor) {
        try {
            return UUID.fromString(valor.trim());
        } catch (Exception n) {
            throw new BusinessRuleException("O uuid não é válido.");
        }
    }

    public Long isLong(String valor) {
        try {
            return Long.parseLong(valor.trim());
        } catch (Exception n) {
            throw new BusinessRuleException("Numero inválido");
        }
    }

    public Double isDouble(String valor) {
        try {
            return Double.parseDouble(valor.trim());
        } catch (Exception n) {
            throw new BusinessRuleException("Numero inválido");
        }
    }

    public Double moedaParaDouble(String moeda) {

        if (moeda != null) {
            moeda = moeda.replace(',', '.');
            try {
                return Double.parseDouble(moeda);
            } catch (Exception ignored) {

            }
        }


        return null;
    }

    public String doubleToString(Double valor) {
        String novo = String.valueOf(valor);
        int index = novo.indexOf(".");
        int quantidade = novo.substring(index).length();
        while (quantidade <= 2) {
            novo = novo.concat("0");
            index = novo.indexOf(".");
            quantidade = novo.substring(index).length();
        }

        return novo;
    }

    public void criarPastas(File file) {
        if (file.isDirectory()) {
            file.mkdirs();
        }
    }

    public Integer isInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception n) {
            return null;
        }
    }

    public byte[] fileToBytes(File file) {
        byte[] bytesArray = new byte[(int) file.length()];

        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

        } catch (IOException e) {

        }

        return bytesArray;
    }

    public Object clone(Object o) {
        try {
            String json = new ObjectMapper().writeValueAsString(o);
            return new ObjectMapper().readValue(json, o.getClass());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean objectEmpty(Object o) {
        return Objects.isNull(o);
    }

    public boolean objectIsNotEmpty(Object o) {
        return !objectEmpty(o) && isNotEmpty(String.valueOf(o));
    }

    public boolean validaSearchCriteriaDTO(SearchCriteriaDTO sc) {
        if (objectIsNotEmpty(sc.getValue()) && objectIsNotEmpty(sc.getOperation())) {
            return true;
        } else if ((!objectIsNotEmpty(sc.getValue())) && objectIsNotEmpty(sc.getOperation()) && (sc.getOperation().equalsIgnoreCase(SearchOperation.NOT_NULL.name())
                || sc.getOperation().equalsIgnoreCase("is_null"))) {
            return true;
        }

        return false;
    }

    public boolean parseLong(String valor) {
        try {
            Long.parseLong(valor);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }

    public boolean isEmpty(String valor) {
        return valor == null || valor.trim().length() == 0;
    }

    public String formartarMoeda(Double valor) {
        if (valor != null) {
            DecimalFormat formato = new DecimalFormat("#.00");
            return formato.format(valor);
        } else {
            return "0.00";
        }
    }


    public Double formartarMoedaNumber(Double valor) {
        if (valor != null) {
            BigDecimal val = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_EVEN);
            return val.doubleValue();
        } else {
            return 0D;
        }
    }

    public boolean isNotEmpty(String valor) {
        return !isEmpty(valor);
    }

    public String decrypt(String secret, String cipherText) {

        try {

            byte[] cipherData = Base64.getDecoder().decode(cipherText);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

            return decryptedText;
        } catch (Exception e) {
            throw new BusinessRuleException("salt invalido");
        }
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            while (generatedLength < keyLength + ivLength) {

                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            Arrays.fill(generatedData, (byte)0);
        }
    }

    private byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(String senha) {
        try {
            BigInteger number = new BigInteger(1, getSHA(senha));

            StringBuilder hexString = new StringBuilder(number.toString(16));

            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {

        }

        return "falhou";
    }

    public void camposDaClasse(Class aClass, List<ClassesVariaveis> campos, Classes classes) {
        Field[] fields = aClass.getDeclaredFields();

        for (Field f: fields) {
            if (f.getType().getSuperclass() != null && f.getType().getSimpleName() != null &&
                    !TypesClasses.existe(f.getType().getSimpleName().toUpperCase())
                    && !TypesClasses.existe(f.getType().getSuperclass().getSimpleName())) {
                try {
                    int nivel = 0;
                    Class clazz = Class.forName(f.getType().getName());
                    Field[] fieldss = clazz.getDeclaredFields();
                    if (clazz.getSimpleName().equals(PermissionGroup.class.getSimpleName())) {
                        nivel = 3;
                    } else {
                        nivel++;
                    }
                    for (Field field: fieldss) {
                        montarRelacionamento(clazz, f.getName() ,field, campos, nivel, classes);
                    }
                } catch (ClassNotFoundException e) {
                }
            } else {
                ClassesVariaveis cv = new ClassesVariaveis();
                cv.setTipo(f.getType().getSimpleName());
                cv.setVariavel(f.getName());
                cv.setClasses(classes);
                cv.setStatus(Status.INATIVO.name());

                if (!f.isAnnotationPresent(Ignore.class)) {
                    campos.add(cv);
                }
            }
        }
    }

    private void montarRelacionamento(Class clazz1, String fieldName, Field f, List<ClassesVariaveis> campos, int nivel, Classes classes) {
        if (f.getType().getSuperclass() != null && !TypesClasses.existe(f.getType().getSimpleName())
                && !TypesClasses.existe(f.getType().getSuperclass().getSimpleName())
                && nivel < 3) {
            try {
                Class clazz = Class.forName(f.getType().getName());
                Field[] fields = clazz.getDeclaredFields();

                if (clazz1.isAssignableFrom(clazz)) {
                    nivel = 3;
                } else {
                    nivel++;
                }
                for (Field field: fields) {
                    montarRelacionamento(clazz, f.getName(), field, campos, nivel, classes);
                }
            } catch (ClassNotFoundException e) {
            }
        } else {

            ClassesVariaveis cv = new ClassesVariaveis();
            cv.setTipo(f.getType().getSimpleName());
            cv.setVariavel(fieldName+"."+f.getName());
            cv.setClasses(classes);
            cv.setStatus(Status.INATIVO.name());
            campos.add(cv);
        }
    }

    private String verificarSeNumero(String numero) {
        String aux = numero.replace(",",".");
        try {
            Double valor = isDouble(aux);
            if (valor != null) {
                return aux;
            }
        } catch (Exception ignored) {

        }

        return numero;
    }

    public SearchCriteria instanciar(SearchCriteriaDTO criteriaDTO) {
        SearchCriteriaDTO dto = SearchCriteriaDTO.montarSearchCriteriaDTO(criteriaDTO);

        if (dto.getValue() != null && !dto.getValue().toString().isEmpty()) {
            dto.setValue(verificarSeNumero(dto.getValue().toString()));
        }

        if (dto.getKey().contains("data")) {
            if (dto.getValue() != null && dto.getValue().toString().length() > 0
                    && dto.getValue().toString().length() != 10) {
                throw new BusinessRuleException(dto.getKey() + " inválido");
            }
            if (dto.getOperation().contains("LESS")) {
                LocalDateTime now = new LocalDateTime(converterStringToDate(String.valueOf(dto.getValue().toString())));
                now = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
                dto.setValue(now.toDate());

            } else if (dto.getOperation().contains("IS_NULL")) {
//                LocalDateTime now = new LocalDateTime(converterStringToDate(String.valueOf(dto.getValue().toString())));
//                now = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
//                dto.setValue(now.toDate());

            } else {
                dto.setValue(converterStringToDate(String.valueOf(dto.getValue())));
            }

        }

        if (Objects.nonNull(dto.getKey()) && "idade".equalsIgnoreCase(dto.getKey())) {
            if (dto.getValue() != null) {
                int anos = Integer.parseInt(dto.getValue().toString());
                Calendar dataInicio = Calendar.getInstance();
                dataInicio.set(Calendar.YEAR, dataInicio.get(Calendar.YEAR) - anos);

                Calendar dataFim = Calendar.getInstance();
                dataFim.set(Calendar.YEAR, dataFim.get(Calendar.YEAR) - anos - 1);

                return new SearchCriteria("dataNascimento",
                        dataInicio.getTime(),
                        SearchOperation.find(SearchOperation.BETWEEN.name()),
                        dto.getClasses(), dataFim.getTime(), null);
            }
        }

        if (dto.getKey().equalsIgnoreCase("foraDaSisc")) {
            String value = dto.getValue().toString();
            if (value.toLowerCase().contains("f")) {
                dto.setValue(false);
            } else {
                dto.setValue(true);
            }
        }

        if (dto.getClasses() != null && dto.getClasses().isEmpty()) {
            dto.setClasses(null);
        }

        if (criteriaDTO.getValue() != null && criteriaDTO.getValue().getClass().getSimpleName().contains("List")) {
            dto.setValue(criteriaDTO.getValue());
        }

        if (criteriaDTO.getValue() instanceof Boolean) {
           dto.setValue(Boolean.valueOf(dto.getValue().toString()));
        }


        try {
            dto.setValue(UUID.fromString(criteriaDTO.getValue().toString()));
        } catch (Exception e) {

        }
        if (criteriaDTO.getValue() instanceof UUID) {
            dto.setValue(criteriaDTO.getValue());
        }

        if (dto.getValue2() == null) {
            return new SearchCriteria(dto.getKey(),
                    dto.getValue(),
                    SearchOperation.find(dto.getOperation()),
                    dto.getClasses(), null, null);
        } else {
            return new SearchCriteria(dto.getKey(),
                    dto.getValue(),
                    SearchOperation.find(dto.getOperation()),
                    dto.getClasses(), dto.getValue2(), true);
        }


    }

    public SearchCriteria instanciar(User user) {

        return new SearchCriteria("id",
                user.getId(),
                SearchOperation.find("EQUAL"),
                "usuario", null, null);
    }

    public void updateObjectos(Object a, Object b) {
        if (Objects.nonNull(a) && Objects.nonNull(b)) {
            Method[] ma = a.getClass().getDeclaredMethods();
            Method[] mb = b.getClass().getDeclaredMethods();
            try {
                for (Method methoda : ma) {
                    methoda.setAccessible(true);
                    for (Method methodb : mb) {
                        methodb.setAccessible(true);
                        if (methoda.getName().startsWith("get")) {
                            if (methoda.getModifiers() != Modifier.PRIVATE
                                    && methoda.getName().equalsIgnoreCase(methodb.getName())) {
                                String valora = String.valueOf(methoda.invoke(a));
                                String valorb = String.valueOf(methodb.invoke(b));
                                if (!valorb.equals("null") && !valora.equals(valorb)) {
                                    try {
                                        String f = firstMinuscula(methoda.getName().substring(3));
                                        Field field = a.getClass().getDeclaredField(f);
                                        field.setAccessible(true);
                                        if (methodb.invoke(b).getClass().isEnum()) {
                                            field.set(a, methodb.invoke(b).toString());
                                        } else if (field.getType().isEnum()) {
                                            field.set(a, Enum.valueOf((Class<Enum>) field.getType(), (String) methodb.invoke(b)));
                                        } else {
                                            field.set(a, methodb.invoke(b));
                                        }

                                    } catch (Exception e) {

                                    }

                                    break;
                                } else if (valorb.equals("null") && !valora.equals(valorb)) {
                                    try {
                                        String f = firstMinuscula(methoda.getName().substring(3));
                                        if (!f.equalsIgnoreCase("id")) {
                                            Field field = a.getClass().getDeclaredField(f);
                                            field.setAccessible(true);
                                            field.set(a, null);
                                        }
                                    } catch (Exception e) {

                                    }

                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public String firstMinuscula(String field) {
        String inicial = field.substring(0,1);
        if (inicial.equals(inicial.toUpperCase())) {
            inicial = inicial.toLowerCase();
        }

        return inicial.concat(field.substring(1));
    }

    public Date converterLongToDate(Long valor) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(valor);

        return calendar.getTime();
    }

    public  Date converterStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if (Objects.nonNull(date)) {
            try {
                return simpleDateFormat.parse(date);
            } catch(Exception ignored) {

            }
        }
        return null;
    }

    public int getLastDayInMonth(Date date, int calendarDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        if (cal.get(Calendar.DAY_OF_WEEK) == calendarDay) {
            return cal.get(Calendar.DAY_OF_MONTH);
        } else {
            cal.add( Calendar.DAY_OF_MONTH, -( cal.get( Calendar.DAY_OF_WEEK ) % 7 + 1 ) );
            return cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    private String patternCompleta = "dd/MM/yyyy HH:mm";
    public  Date converterStringToDateCompleta(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternCompleta);
        if (Objects.nonNull(date)) {
            try {
                return simpleDateFormat.parse(date);
            } catch(Exception ignored) {

            }
        }
        return null;
    }

    public String getDataPatternCompleta(String pattern, String tipo, int dias) {
        Beans beans = new Beans();

        Date data;
        if (tipo.equalsIgnoreCase("max")) {
            data = beans.validarDateMax(pattern, dias);
        } else {
            data = beans.validarDateMin(pattern, dias);
        }

        return beans.converterDateToStringCompleta(data);
    }

    public String getDataPattern(String pattern, String tipo, int dias) {
        Beans beans = new Beans();

        Date data;
        if (tipo.equalsIgnoreCase("max")) {
            data = beans.validarDateMax(pattern, dias);
        } else {
            data = beans.validarDateMin(pattern, dias);
        }

        return beans.converterDateToString(data);
    }

    public  Date validarDateMin(String padrao, int dias) {

        try {
            int valor = Integer.parseInt(padrao.substring(1, padrao.length()-1));
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
                dateTime = dateTime.minusDays(dias);

            } else {
                if (ultimo.equals("y")) {
                    dateTime = dateTime.minusYears(valor).minusDays(1);
                } else if (ultimo.equals("m")) {
                    dateTime = dateTime.minusMonths(valor).minusDays(1);
                } else {
                    dateTime = dateTime.minusDays(valor);
                }

                dateTime = dateTime.minusDays(dias);
            }
            return dateTime.toDate();
        } catch (Exception e) {

        }

        return null;
    }

    public  Date validarDateMax(String padrao, int dias) {

        try {
            int valor = Integer.parseInt(padrao.substring(1, padrao.length()-1));
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
                dateTime = dateTime.plusDays(dias);

            } else {
                if (ultimo.equals("y")) {
                    dateTime = dateTime.minusYears(valor).minusDays(1);
                } else if (ultimo.equals("m")) {
                    dateTime = dateTime.minusMonths(valor).minusDays(1);
                } else {
                    dateTime = dateTime.minusDays(valor);
                }

                dateTime = dateTime.plusDays(dias);
            }
            return dateTime.toDate();
        } catch (Exception e) {

        }

        return null;
    }


    public  Date converterStringToHour(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternHora);
        if (Objects.nonNull(date)) {
            try {
                return simpleDateFormat.parse(date);
            } catch(Exception e) {

            }
        }
        return null;
    }

    public String converterDateToString(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            if (Objects.nonNull(date)) {
                return simpleDateFormat.format(date);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public String converterDateToStringCompleta(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternCompleta);
            if (Objects.nonNull(date)) {
                return simpleDateFormat.format(date);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public void escreverArquivo(List<String> linhas, File file) {

        try (FileWriter arq = new FileWriter(file)) {
            PrintWriter gravarArq = new PrintWriter(arq);

            for (String linha : linhas) {
                gravarArq.println(linha);
            }

            gravarArq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String converterHourToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternHora);
        if (Objects.nonNull(date)) {
            return simpleDateFormat.format(date);
        }
        return null;
    }
//
//    public void podeAcessarADM(Content content) {
//
//        if (!content.getUser().getTipo().equalsIgnoreCase("app")) {
//            throw new BusinessRuleException("Usuario não pode acessar este recurso");
//        }
//    }
//
//    public void podeAcessar(Content content, long permission) {
//        boolean existe = false;
//        for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
//            if (dto.getPermission().equals(permission)) {
//                existe = true;
//                break;
//            }
//        }
//
//        if (!existe) {
//            throw new BusinessRuleException("Usuario não pode acessar este recurso");
//        }
//    }
//
//    public byte[] getFileFromResourceToBytes(String nome) {
//        try (InputStream is = getClass().getResourceAsStream(nome)) {
//            File targetFile = new File(UUID.randomUUID().toString());
//            FileUtils.copyInputStreamToFile(is, targetFile);
//
//            byte[] novo = fileToBytes(targetFile);
//
//            deleteFile(targetFile);
//            return novo;
//
//        } catch (Exception ignored) {
//
//        }
//
//        return null;
//    }
//
//    public void podeAcessar(Content content, PermissoesEnum permission, PermissoesEnum permission2) {
//        boolean existe = false;
//        for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
//            if (dto.getPermission().equals(permission.getSerial())) {
//                existe = true;
//                break;
//            }
//        }
//
//        if (!existe) {
//            for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
//                if (dto.getPermission().equals(permission2.getSerial())) {
//                    existe = true;
//                    break;
//                }
//            }
//        }
//
//        if (!existe) {
//            throw new BusinessRuleException("Usuario não pode acessar este recurso");
//        }
//    }
//
//    public void podeAcessar(Content content, PermissoesEnum permission) {
//        boolean existe = false;
//        for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
//            if (dto.getPermission().equals(permission.getSerial())) {
//                existe = true;
//                break;
//            }
//        }
//
//        if (!existe) {
//            throw new BusinessRuleException("Usuario não pode acessar este recurso");
//        }
//    }
//
//    public boolean podeAcessarBoolean(Content content, long permission) {
//        for (PermissionsDTO dto : content.getUsuarioLogadoDTO().getPermissionsDTOS()) {
//            if (dto.getPermission().equals(permission)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private static String firstLower(String word) {
        String primeira = word.substring(0,1);
        return primeira.toLowerCase().concat(word.substring(1));
    }

    public String removerLetras(String valor) {
        String aux = "";
        if (valor != null) {
            for (int i = 0 ; i < valor.length(); i++) {
                if (Character.isDigit(valor.charAt(i))) {
                    aux = aux + valor.charAt(i);
                }
            }
        }
        return aux;
    }

    public boolean isNumero(String valor) {
        for (int i = 0 ; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
               return false;
            }
        }

        return true;
    }

    public String getMesFromDate (Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("MMM");
    }

    public int getMesFromDateNumero (Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getMonthOfYear();
    }

    public int getDiaFromDateNumero (Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getDayOfMonth();
    }

    public String getAnoFromDate (Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("YYYY");
    }

    public int getAnoFromDateNumero (Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getYear();
    }

    public String removerAcentosOnly(String palavra) {
        palavra = palavra.toLowerCase();
        String acentos = "áãâàéèẽêíìĩîóòôõúùũûç";
        String sacento = "aaaaeeeeiiiioooouuuuc";

        for (int i = 0; i < palavra.length(); i++) {
            for (int j = 0; j < acentos.length(); j++) {
                if (palavra.contains(acentos.substring(j, j + 1))) {
                    int index = palavra.indexOf(acentos.substring(j, j + 1));
                    palavra = palavra.substring(0, index) + sacento.substring(j, j + 1) + palavra.substring(index + 1);
                }
            }
        }

        return palavra;
    }

    public String removerNumeros(String palavra) {
        String nova = "";

        for (int i = 0; i < palavra.length(); i++) {
            if (!Character.isDigit(palavra.charAt(i))) {
                nova = nova.concat(String.valueOf(palavra.charAt(i)));
            }
        }

        return nova;
    }

    public String removerTodosEspacos(String valor) {
        while (valor.contains(" ")) {
            valor = valor.replace(" ", "");
        }

        return valor;
    }

    public String removerEspacosDuplos(String valor) {
        if (valor == null) {
             return null;
        }

        while (valor.contains("  ")) {
            valor = valor.replace("  ", " ");
        }

        return valor.trim();
    }

    public String removerAcentosToUpperCase(String palavra) {

        if (palavra == null) {
            return palavra;
        }

        palavra = palavra.toLowerCase().trim();

        String acentos = "áãâàéèẽêíìĩîóòôõúùũûç";
        String sacento = "aaaaeeeeiiiioooouuuuc";

        for (int i = 0; i < palavra.length(); i++) {
            for (int j = 0; j < acentos.length(); j++) {
                if (palavra.contains(acentos.substring(j, j+1))) {
                    int index = palavra.indexOf(acentos.substring(j, j+1));
                    palavra = palavra.substring(0, index)+sacento.substring(j, j+1)+palavra.substring(index+1);
                }
            }
        }

        palavra = removerEspacosDuplos(palavra);

        return palavra.toUpperCase();
    }


    public String removerAcentos(String palavra) {

        if (palavra == null) {
            return palavra;
        }

        String acentos = "áãâàéèẽêíìĩîóòôõúùũûç";
        String sacento = "aaaaeeeeiiiioooouuuuc";

        for (int i = 0; i < palavra.length(); i++) {
            for (int j = 0; j < acentos.length(); j++) {
                if (palavra.contains(acentos.substring(j, j+1))) {
                    int index = palavra.indexOf(acentos.substring(j, j+1));
                    palavra = palavra.substring(0, index)+sacento.substring(j, j+1)+palavra.substring(index+1);
                }
            }
        }

        while(palavra.contains(" ") || palavra.contains(".") || palavra.contains("-") || palavra.contains("/")) {
            palavra = palavra.replace(" ", "");
            palavra = palavra.replace(".", "");
            palavra = palavra.replace("-", "");
            palavra = palavra.replace("/", "");
        }
        return palavra;
    }

    public void converterByteArrayToFile(File file, byte[] arquivo) {
        try {
            FileUtils.writeByteArrayToFile(file, arquivo);
        } catch (IOException e) {

        }
    }

    public String converterToDateGMTtoDateString(String data) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(data);

        Date date = dateTime.toDate();
        return converterDateToString(date);
    }

    public String getMap( Map<String, String> cnpjM, String chave) {
        if (cnpjM.containsKey(chave)) {
            return cnpjM.get(chave).toString();
        }

        return null;
    }

    public File converterByteArrayToFile(byte[] arquivo, String extensao) {
        try {
            File file = new File(HOME_DIR+UUID.randomUUID().toString()+"."+extensao);
            FileUtils.writeByteArrayToFile(file, arquivo);
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public File converterByteArrayToFile(byte[] arquivo) {
        try {
            File file = new File(HOME_DIR+UUID.randomUUID().toString());
            FileUtils.writeByteArrayToFile(file, arquivo);
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    public void gravarArquivo(File file) {
        try (FileOutputStream fos = new FileOutputStream(file.getName())) {

            byte[] mybytes = fileToBytes(file);

            fos.write(mybytes);
        } catch (Exception e) {

        }
    }

    public static void montarSpecificationOr(List<Predicate> predicates, List<SearchCriteria> list,
                                             Root<?> root, CriteriaBuilder builder, List<Join> allJoins) {
        for (SearchCriteria criteria : list) {

            if (criteria.getValue2() != null) {
                if (criteria.getClasses() == null) {
                    if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                        predicates.add(builder.equal(
                                root.get(criteria.getKey()), criteria.getValue()));
                        predicates.add(builder.equal(
                                root.get(criteria.getKey()), criteria.getValue2()));
                    }
                } else {
                    for (Join join: allJoins) {
                        if (join.getAttribute().getName().equals(criteria.getClasses())) {
                            if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                                predicates.add(builder.equal(
                                        join.get(criteria.getKey()), criteria.getValue()));
                                predicates.add(builder.equal(
                                        join.get(criteria.getKey()), criteria.getValue2()));
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isNumber(Object valor) {

        try {
            Double.parseDouble(valor.toString());
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    private static boolean isDate(Object valor) {
        if (Objects.nonNull(valor)) {
            try {
                if (valor instanceof Date) {
                    return true;
                }
            } catch(Exception ignored) {

            }
        }
        return false;
    }

    public static void montarSpecification(List<Predicate> predicates, List<SearchCriteria> list,
                                      Root<?> root, CriteriaBuilder builder, List<Join> allJoins) {
        for (SearchCriteria criteria : list) {

            if (criteria.getOr() == null) {
                if (criteria.getClasses() == null) {
                    if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                        if (criteria.getValue() instanceof Date) {
                            predicates.add(builder.greaterThan(
                                    root.get(criteria.getKey()), (Date) criteria.getValue()));
                        } else {
                            predicates.add(builder.greaterThan(
                                    root.get(criteria.getKey()), criteria.getValue().toString()));
                        }

                    } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                        if (criteria.getValue() instanceof Date) {
                            predicates.add(builder.lessThan(
                                    root.get(criteria.getKey()),  (Date) criteria.getValue()));
                        } else {
                            predicates.add(builder.lessThan(
                                    root.get(criteria.getKey()), criteria.getValue().toString()));
                        }

                    } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                        if (criteria.getValue() instanceof Date) {
                            predicates.add(builder.greaterThanOrEqualTo(
                                    root.get(criteria.getKey()), (Date) criteria.getValue()));
                        } else {
                            predicates.add(builder.greaterThanOrEqualTo(
                                    root.get(criteria.getKey()), criteria.getValue().toString()));
                        }

                    } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                        if (criteria.getValue() instanceof Date) {
                            predicates.add(builder.lessThanOrEqualTo(
                                    root.get(criteria.getKey()), (Date) criteria.getValue()));
                        } else {
                            predicates.add(builder.lessThanOrEqualTo(
                                    root.get(criteria.getKey()), criteria.getValue().toString()));
                        }
                    } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                        predicates.add(builder.notEqual(
                                root.get(criteria.getKey()), criteria.getValue()));
                    } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                        predicates.add(builder.equal(
                                root.get(criteria.getKey()), criteria.getValue()));
                    } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                        predicates.add(builder.like(
                                builder.lower(root.get(criteria.getKey())),
                                "%" + criteria.getValue().toString().toLowerCase() + "%"));
                    } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                        predicates.add(builder.like(
                                builder.lower(root.get(criteria.getKey())),
                                criteria.getValue().toString().toLowerCase() + "%"));
                    } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                        predicates.add(builder.like(
                                builder.lower(root.get(criteria.getKey())),
                                "%" + criteria.getValue().toString().toLowerCase()));
                    } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                        predicates.add(builder.in(root.get(criteria.getKey())).value(criteria.getValue()));
                    } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                        predicates.add(builder.not(root.get(criteria.getKey())).in(criteria.getValue()));
                    }  else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                        predicates.add(builder.isNull(root.get(criteria.getKey())));
                    } else if (criteria.getOperation().equals(SearchOperation.NOT_NULL)) {
                        predicates.add(builder.isNotNull(root.get(criteria.getKey())));
                    } else if (criteria.getOperation().equals(SearchOperation.BETWEEN)) {
                        predicates.add(builder.lessThan(
                                root.get(criteria.getKey()), (Date) criteria.getValue()));
                        predicates.add(builder.greaterThanOrEqualTo(
                                root.get(criteria.getKey()), (Date) criteria.getValue2()));

                    }
                } else {
                    for (Join join: allJoins) {
                        if (join.getAttribute().getName().equals(criteria.getClasses())) {
                            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                                if (criteria.getValue() instanceof Date) {
                                    predicates.add(builder.greaterThan(
                                            join.get(criteria.getKey()), (Date) criteria.getValue()));
                                } else {
                                    predicates.add(builder.greaterThan(
                                            join.get(criteria.getKey()), criteria.getValue().toString()));
                                }
                            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                                if (criteria.getValue() instanceof Date) {
                                    predicates.add(builder.lessThan(
                                            join.get(criteria.getKey()), (Date) criteria.getValue()));
                                } else {
                                    predicates.add(builder.lessThan(
                                            join.get(criteria.getKey()), criteria.getValue().toString()));
                                }
                            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                                if (criteria.getValue() instanceof Date) {
                                    predicates.add(builder.greaterThanOrEqualTo(
                                            join.get(criteria.getKey()), (Date) criteria.getValue()));
                                } else {
                                    predicates.add(builder.greaterThanOrEqualTo(
                                            join.get(criteria.getKey()), criteria.getValue().toString()));
                                }
                            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                                if (criteria.getValue() instanceof Date) {
                                    predicates.add(builder.lessThanOrEqualTo(
                                            join.get(criteria.getKey()), (Date) criteria.getValue()));
                                } else {
                                    predicates.add(builder.lessThanOrEqualTo(
                                            join.get(criteria.getKey()), criteria.getValue().toString()));
                                }
                            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                                predicates.add(builder.notEqual(
                                        join.get(criteria.getKey()), criteria.getValue()));
                            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                                predicates.add(builder.equal(
                                        join.get(criteria.getKey()), criteria.getValue()));
                            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                                predicates.add(builder.like(
                                        builder.lower(join.get(criteria.getKey())),
                                        "%" + criteria.getValue().toString().toLowerCase() + "%"));
                            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                                predicates.add(builder.like(
                                        builder.lower(join.get(criteria.getKey())),
                                        criteria.getValue().toString().toLowerCase() + "%"));
                            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                                predicates.add(builder.like(
                                        builder.lower(join.get(criteria.getKey())),
                                        "%" + criteria.getValue().toString().toLowerCase()));
                            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                                predicates.add(builder.in(join.get(criteria.getKey())).value(criteria.getValue()));
                            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                                predicates.add(builder.not(join.get(criteria.getKey())).in(criteria.getValue()));
                            } else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                                predicates.add(builder.isNull(join.get(criteria.getKey())));
                            } else if (criteria.getOperation().equals(SearchOperation.NOT_NULL)) {
                                predicates.add(builder.isNotNull(join.get(criteria.getKey())));
                            } else if (criteria.getOperation().equals(SearchOperation.BETWEEN)) {
                                predicates.add(builder.lessThan(
                                        join.get(criteria.getKey()), (Date) criteria.getValue()));
                                predicates.add(builder.greaterThanOrEqualTo(
                                        join.get(criteria.getKey()), (Date) criteria.getValue2()));
                            }
                        }
                    }
                }
            }
        }
    }


    public void deleteFile(File file) {
        while (file.exists()) {
            file.delete();
        }
    }

    public Long getIdade(Date dataNascimento) {
        Calendar dateTime = Calendar.getInstance();
        Calendar nascimento = Calendar.getInstance();
        nascimento.setTime(dataNascimento);
        long tempo = dateTime.getTimeInMillis() - nascimento.getTimeInMillis();

        return (long)(tempo / 1000 / 60 / 60 / 24 / 365.25);
    }

    public Long getIdade(Date dataNascimento, Date dataCorte) {
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTime(dataCorte);
        Calendar nascimento = Calendar.getInstance();
        nascimento.setTime(dataNascimento);
        long tempo = dateTime.getTimeInMillis() - nascimento.getTimeInMillis();

        return (long)(tempo / 1000 / 60 / 60 / 24 / 365.25);
    }

    public String getIdadeTexto(Date dataNascimento) {
        Calendar dateTime = Calendar.getInstance();
        Calendar nascimento = Calendar.getInstance();
        nascimento.setTime(dataNascimento);
        long tempo = dateTime.getTimeInMillis() - nascimento.getTimeInMillis();

        Double idadee = Double.valueOf(tempo);
        idadee = idadee  / 1000 / 60 / 60 / 24 / 365.25;
        String idade = idadee.intValue()+"";

        if (idadee < 7) {
            idade = idade + " anos e ";
            Double parte = idadee - idadee.intValue();

            Double meses = parte * 365.25 / 30;

            idade = idade + " "+ meses.intValue()+" meses e ";

            int mesFim = getMesFromDateNumero(new Date());

            int diaIni = getDiaFromDateNumero(dataNascimento);
            int diaFim = getDiaFromDateNumero(new Date());

            int dias = 0;
            if (diaIni < diaFim) {
                dias =  diaFim - diaIni;
            } else if (diaIni > diaFim) {
                int diasMesAnt = 0;
                if (mesFim == 1) {
                    diasMesAnt = Meses.getByMes(12).getDias();
                } else {
                    diasMesAnt = Meses.getByMes(mesFim - 1).getDias();
                }

                dias = diasMesAnt - diaIni + diaFim;
            }

            idade = idade + dias + " dias";
        } else {
            idade = idade + " anos";
        }


        return idade;
    }

    public void esperarEmMilli(int tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public String toJson(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (Exception e) {

        }

        return null;
    }

    public Object toObject(String o) {
        try {
            return new ObjectMapper().readValue(o, Object.class);
        } catch (Exception e) {

        }

        return null;
    }

    public String formarCNPJ(String cnpj) {
        if (cnpj == null) {
            return cnpj;
        }
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    public String formarCPF(String cpj) {

        if (cpj == null) {
            return cpj;
        }
        return cpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public String encriptar(Object o, String token) {
        if (Objects.nonNull(o)) {
            try {
                String objeto =  new ObjectMapper().writeValueAsString(o);
                return aes.encrypt(objeto, token);
            } catch (Exception ignored) {

            }
        }

        return null;
    }

    public String camelToSnake(String str) {
        StringBuilder result = new StringBuilder();

        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public String capitalize(String str) {

        char[] charArray = str.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {

            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            } else {
                foundSpace = true;
            }
        }

        return String.valueOf(charArray);
    }

    public void isMesmoUsuario(User usuario, User atualizador) {
        if (usuario.getId() != atualizador.getId()) {
            throw new BusinessRuleException("O usuario " + atualizador.getName() + " não pode atualizar este prosseguimento");
        }

        log.info("usuario: "+usuario.getName()+" - "+ usuario.getId() + " atualizador: "+atualizador.getId() + " - " +atualizador.getName());
    }

    public String decripty(String texto, String key) {

        return aes.decrypt(texto, key);
    }

    public byte[] reduzirImagem(byte[] urlDocumentoI) {

        File input = new File(UUID.randomUUID().toString());
        converterByteArrayToFile(input, urlDocumentoI);

        try {
            BufferedImage originalImage = ImageIO.read(input);

            Double proporcao = 1200D /  originalImage.getWidth();

            int novoLargura = (int) (originalImage.getWidth()*proporcao);
            int novaAltura = (int) (originalImage.getHeight()*proporcao);

            java.awt.Image resizedImage = originalImage.getScaledInstance(novoLargura, novaAltura, 4);

            BufferedImage novaBufferedImage = new BufferedImage(novoLargura, novaAltura, BufferedImage.TYPE_INT_RGB);
            novaBufferedImage.getGraphics().drawImage(resizedImage, 0, 0, null);

            File output = new File(UUID.randomUUID().toString());
            ImageIO.write(novaBufferedImage, "jpg", output);

            byte[] retorno = fileToBytes(output);
            deleteFile(input);
            deleteFile(output);

            return retorno;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public String adicionaEspacoAntesDeMaiuscula(String nome) {
        String aux = "";

        for ( int  i = 0; i < nome.length(); i ++) {
            String parte = nome.substring(i, i+1);
            if (parte.equals(parte.toUpperCase())) {
                aux = aux + " " + parte;
            } else {
                aux = aux + parte;
            }
        }

        return aux;
    }

}
