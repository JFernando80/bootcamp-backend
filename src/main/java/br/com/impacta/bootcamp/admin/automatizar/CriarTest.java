package br.com.impacta.bootcamp.admin.automatizar;

import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.commons.util.*;
import br.com.impacta.bootcamp.formacao.model.UserActivity;
import jakarta.persistence.Id;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CriarTest {

    private static final String BEANS = "        Beans beans = new Beans();";
    public static void main(String[] args) {
        Class c = UserActivity.class;
        criar(c);
        CriarTest.analisarClasseTest(c);
    }


    public static void criar(Class clazz) {
        String tipo = "serviceTest";
        Util.montarPathTest(clazz);
        Util.criarPasta(CriarDepencias.PATH_TEST);

        String nomeArquivo = clazz.getSimpleName().concat(Util.primeiraMaiuscula(tipo)).concat(".java");

        File file = new File(CriarDepencias.PATH_TEST.concat("/"), nomeArquivo);

        boolean idUUID = false;

        List<Field> campos = Arrays.stream(clazz.getDeclaredFields()).toList();
        for (Field campo : campos) {
            if (campo.isAnnotationPresent(Id.class) && campo.getType().equals(UUID.class)) {
                idUUID = true;
                break;
            }
        }

        try (FileWriter arq = new FileWriter(file)) {
            PrintWriter gravarArq = new PrintWriter(arq);

            String nc = clazz.getSimpleName();
            String ncm = Util.primeiraMinuscula(nc);

            gravarArq.println("package ".concat(CriarDepencias.PACKAGE_BASE).concat(";"));
            gravarArq.println("");
            montarImports(gravarArq, nc, clazz);

            gravarArq.println("");
            gravarArq.println("@MockitoSettings(strictness = Strictness.LENIENT)");
            gravarArq.println("@ExtendWith(MockitoExtension.class)");
            gravarArq.println("public class "+nc+"ServiceTest {");
            gravarArq.println("");
            gravarArq.println("    @InjectMocks");
            gravarArq.println("    private "+nc+"ServiceImpl service;");
            gravarArq.println("");
            gravarArq.println("    @Mock");
            gravarArq.println("    private "+nc+"Repository "+Util.primeiraMinuscula(nc)+"Repository;");

            if (Util.hasClassePersonal(clazz)) {
                montarMockPersonal(gravarArq, clazz);
            }

            montarMocks(gravarArq, clazz);
            gravarArq.println("");
            gravarArq.println("    private Beans beans = new Beans();");

            if (idUUID) {
                gravarArq.println("    private static final UUID id = UUID.randomUUID();");
                gravarArq.println("    private static final UUID idErro = UUID.randomUUID();");
            } else {
                gravarArq.println("    private static final Long id = 1L;");
                gravarArq.println("    private static final Long idErro = 2L;");
            }
            gravarArq.println("");

            montarClasseDTOs(gravarArq, clazz);
            gravarArq.println("");
            gravarArq.println("    private "+nc+"DTO montarNull() {");
            gravarArq.println("        return new "+nc+"DTO();");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    private "+nc+"DTO montarDtoOK() {");
            String dtoOk = "dtoOK";
            gravarArq.println("        "+nc+"DTO " + dtoOk + " = new "+nc+"DTO();");
            gravarArq.println("");
            gravarArq.println("        dtoOK.setId(id);");
            montarFieldsOK(gravarArq, clazz, dtoOk);

            if (Util.hasCompany(clazz)) {
                gravarArq.println("        "+dtoOk+".setCompanyCnpj(\"321\");");
            }

            if (Util.hasFilial(clazz)) {
                gravarArq.println("        "+dtoOk+".setFilialCnpj(\"321\");");
            }

            gravarArq.println("        return "+dtoOk+";");
            gravarArq.println("    }");
            gravarArq.println("");
            String dtoLimite = "dtoLimite";
            gravarArq.println("    private "+nc+"DTO montarDtoOkLimite() {");
            gravarArq.println("        "+nc+"DTO "+dtoLimite+" = new "+nc+"DTO();");
            gravarArq.println("");
            montarFieldsOKLimite(gravarArq, clazz, dtoLimite);
            if (Util.hasCompany(clazz)) {
                gravarArq.println("        "+dtoLimite+".setCompanyCnpj(\"321\");");
            }
            gravarArq.println("        return "+dtoLimite+";");
            gravarArq.println("    }");
            gravarArq.println("");
            String dtoErroMin = "dtoErroMin";
            gravarArq.println("    private "+nc+"DTO montarDtoErroMin() {");
            gravarArq.println("        "+nc+"DTO "+dtoErroMin+" = new "+nc+"DTO();");
            gravarArq.println("");
            montarFieldsErroMin(gravarArq, clazz, dtoErroMin);
            gravarArq.println("        return "+dtoErroMin+";");
            gravarArq.println("    }");
            gravarArq.println("");
            String dtoErroMax = "dtoErroMax";
            gravarArq.println("    private "+nc+"DTO montarErroMax() {");
            gravarArq.println("        "+nc+"DTO "+dtoErroMax+" = new "+nc+"DTO();");
            gravarArq.println("");
            montarFieldsErroMax(gravarArq, clazz, dtoErroMax);
            gravarArq.println("        return "+dtoErroMax+";");
            gravarArq.println("    }");
            gravarArq.println("");

            if (Util.hasClassePersonal(clazz)) {
                montarFieldsClassePersonal(gravarArq, clazz);
            }

            if (Util.hasCompany(clazz)) {
                gravarArq.println("    private Company montarFilial() {");
                gravarArq.println("        Company company = new Company();");
                gravarArq.println("        company.setCnpj(\"321\");");
                gravarArq.println("        company.setNomeFantasia(\"aaa\");");
                gravarArq.println("        return company;");
                gravarArq.println("    }");
                gravarArq.println("");
            }

            if (Util.hasCompany(clazz)) {
                gravarArq.println("    private Company montarCompany() {");
                gravarArq.println("        Company company = new Company();");
                gravarArq.println("        company.setCnpj(\"321\");");
                gravarArq.println("        return company;");
                gravarArq.println("    }");
                gravarArq.println("");
                gravarArq.println("    private Company montarCompanyErro() {");
                gravarArq.println("        Company company = new Company();");
                gravarArq.println("        company.setId(1L);");
                gravarArq.println("        company.setCnpj(\"333\");");
                gravarArq.println("        return company;");
                gravarArq.println("    }");
                gravarArq.println("");
            }


            gravarArq.println("    @BeforeEach");
            gravarArq.println("    public  void setup() {");
            gravarArq.println("        ReflectionTestUtils.setField(service, \"beans\", new Beans());");
            gravarArq.println("        ReflectionTestUtils.setField(service, \"offset\", 10);");

            if (Util.hasClassePersonal(clazz)) {
                mockClassePersonal(gravarArq, clazz);
            }

            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarErroCamposNull() {");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+"DTO dtoNull = montarNull();");
            gravarArq.println("        try {");
            gravarArq.println("            service.save(dtoNull);");
            gravarArq.println("        } catch (BusinessRuleException e) {");
            gravarArq.println("            Field[] fields = dtoNull.getClass().getDeclaredFields();");
            gravarArq.println("            List<Field> obrigatorios = new ArrayList<>();");
            gravarArq.println("            for (Field field : fields) {");
            gravarArq.println("                String campo = Util.toSneakCase(field.getName());");
            gravarArq.println("                String hash = beans.toHexString(campo);");
            gravarArq.println("                for (String erro : e.erros) {");
            gravarArq.println("");
            gravarArq.println("                    if (erro.contains(hash)) {");
            gravarArq.println("                        if (Util.isTipoDTO(field)) {");
            gravarArq.println("                            Assertions.assertEquals(\"A classe \"+campo+\" nao pode ser nula. \"+hash, erro);");
            gravarArq.println("                        } else {");
            gravarArq.println("                            Assertions.assertEquals(\"O campo \"+campo+\" é obrigatório. \"+hash, erro);");
            gravarArq.println("                        }");
            gravarArq.println("                    }");
            gravarArq.println("                }");
            gravarArq.println("");
            gravarArq.println("                if (UtilTest.olharAnotacaoRequired(field)) {");
            gravarArq.println("                    obrigatorios.add(field);");
            gravarArq.println("                }");
            gravarArq.println("            }");
            gravarArq.println("");
            gravarArq.println("            Assertions.assertEquals(e.erros.size(), obrigatorios.size());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarErroCamposMinimo() {");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+"DTO erroMin = montarDtoErroMin();");
            gravarArq.println("        try {");
            gravarArq.println("            service.save(erroMin);");
            gravarArq.println("        } catch (BusinessRuleException e) {");
            gravarArq.println("            Field[] fields = erroMin.getClass().getDeclaredFields();");
            gravarArq.println("            List<Field> obrigatorios = new ArrayList<>();");
            gravarArq.println("            for (Field field : fields) {");
            gravarArq.println("                String campo = Util.toSneakCase(field.getName());");
            gravarArq.println("                String hash = beans.toHexString(campo);");
            gravarArq.println("                Validation valid = field.getDeclaredAnnotation(Validation.class);");
            gravarArq.println("                for (String erro : e.erros) {");
            gravarArq.println("                    if (erro.contains(hash)) {");
            gravarArq.println("                        Long minValue = UtilTest.olharAnotacaoLengthMin(field);");
            gravarArq.println("                        if (field.getType().equals(Long.class)) {");
            gravarArq.println("                            Assertions.assertEquals(\"O valor do campo \"+campo+\" deve ser maior que \"+minValue+\". \"+hash, erro);");
            gravarArq.println("                        } else if (valid  != null && !valid.dateMin().equalsIgnoreCase(\"\")) {");
            gravarArq.println("                            Assertions.assertEquals(\"O campo \"+campo+\" é menor que o permitido. Data Minima maior que \"+beans.converterDateToString(beans.validarDateMin(valid.dateMin(), 0))+\". \"+hash, erro);");
            gravarArq.println("                        } else if (field.getType().equals(String.class)) {");
            gravarArq.println("                            Assertions.assertEquals(\"O campo \"+campo+\" deve ter mais que \"+minValue+\" caracter/es. \"+hash, erro);");
            gravarArq.println("                        }");
            gravarArq.println("                    }");
            gravarArq.println("                }");
            gravarArq.println("");
            gravarArq.println("                if (UtilTest.olharAnotacaoMin(field)) {");
            gravarArq.println("                    obrigatorios.add(field);");
            gravarArq.println("                }");
            gravarArq.println("            }");
            gravarArq.println("");
            gravarArq.println("            Assertions.assertEquals(e.erros.size(), obrigatorios.size());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarErroCamposMaximo() {");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+"DTO erroMax = montarErroMax();");
            gravarArq.println("        try {");
            gravarArq.println("            service.save(erroMax);");
            gravarArq.println("        } catch (BusinessRuleException e) {");
            gravarArq.println("            Field[] fields = erroMax.getClass().getDeclaredFields();");
            gravarArq.println("            List<Field> obrigatorios = new ArrayList<>();");
            gravarArq.println("            for (Field field : fields) {");
            gravarArq.println("                String campo = Util.toSneakCase(field.getName());");
            gravarArq.println("                String hash = beans.toHexString(campo);");
            gravarArq.println("                Validation valid = field.getDeclaredAnnotation(Validation.class);");
            gravarArq.println("                for (String erro : e.erros) {");
            gravarArq.println("                    if (erro.contains(hash)) {");
            gravarArq.println("                        Long maxValue = UtilTest.olharAnotacaoLengthMax(field);");
            gravarArq.println("                        if (field.getType().equals(Long.class)) {");
            gravarArq.println("                            Assertions.assertEquals(\"O valor do campo \"+campo+\" deve ser menor que \"+maxValue+\". \"+hash, erro);");
            gravarArq.println("                        } else if (valid  != null && !valid.dateMax().equalsIgnoreCase(\"\")) {");
            gravarArq.println("                            Assertions.assertEquals(\"O campo \"+campo+\" é maior que o permitido. Data Máxima: \"+beans.converterDateToString(beans.validarDateMax(valid.dateMax(), 0))+\". \"+hash, erro);");
            gravarArq.println("                        } else if (field.getType().equals(String.class)) {");
            gravarArq.println("                            Assertions.assertEquals(\"O campo \"+campo+\" deve ter menos que \"+maxValue+\" caracter/es. \"+hash, erro);");
            gravarArq.println("                        }");
            gravarArq.println("                    }");
            gravarArq.println("                }");
            gravarArq.println("");
            gravarArq.println("                if (UtilTest.olharAnotacaoMax(field)) {");
            gravarArq.println("                    obrigatorios.add(field);");
            gravarArq.println("                }");
            gravarArq.println("            }");
            gravarArq.println("");
            gravarArq.println("            Assertions.assertEquals(e.erros.size(), obrigatorios.size());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarErroRepetido() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            gravarArq.println(BEANS);
            gravarArq.println("");
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            gravarArq.println("");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }

            if (Util.hasDate(clazz)) {
                settarDateEntity(gravarArq, clazz, "dtoOk");
            }

            gravarArq.println("        "+Util.primeiraMinuscula(nc)+".setId(idErro);");

            if (Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }
            if (Util.hasClassePersonal(clazz)) {
                settarClassePersonalMax(gravarArq, clazz);
                gravarArq.println("");
            }

            gravarArq.println("");
            gravarArq.println("        List<"+nc+"> "+Util.primeiraMinuscula(nc)+"s = new ArrayList<>();");
            gravarArq.println("        "+Util.primeiraMinuscula(nc)+"s.add("+Util.primeiraMinuscula(nc)+");");
            gravarArq.println("        Page<"+nc+"> msPage = new PageImpl<>("+Util.primeiraMinuscula(nc)+"s);");

            gravarArq.println("");
            gravarArq.println("        Mockito.when("+Util.primeiraMinuscula(nc)+"Repository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);");
            if (Util.hasTipoDTO(clazz)) {
                montarMockDTO(gravarArq, clazz);
            }

            gravarArq.println("        try {");
            gravarArq.println("            service.save(dtoOk);");
            gravarArq.println("        } catch (BusinessRuleException e) {");
            gravarArq.println("            Assertions.assertEquals(\"Já existe este "+Util.primeiraMinuscula(nc)+" cadastrado\", e.getMessage());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarOK() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }
            if (Util.hasClassePersonal(clazz)) {
                settarClassePersonalMax(gravarArq, clazz);
                gravarArq.println("");
            }
            gravarArq.println("        List<"+nc+"> "+Util.primeiraMinuscula(nc)+"s = new ArrayList<>();");
            gravarArq.println("        "+Util.primeiraMinuscula(nc)+"s.add("+Util.primeiraMinuscula(nc)+");");

            if (Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }

            gravarArq.println("        Page<"+nc+"> msPage = new PageImpl<>("+Util.primeiraMinuscula(nc)+"s);");
            gravarArq.println("        Mockito.when("+Util.primeiraMinuscula(nc)+"Repository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);");
            if (Util.hasTipoDTO(clazz)) {
                montarMockDTO(gravarArq, clazz);
            }

            gravarArq.println("        service.save(dtoOk);");
            gravarArq.println("");
            gravarArq.println("        Collection<Invocation> invocations = Mockito.mockingDetails("+Util.primeiraMinuscula(nc)+"Repository).getInvocations();");
            gravarArq.println("        Assertions.assertEquals(3, invocations.size());");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void tentarSalvarOKLimite() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOkLimite();");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            gravarArq.println("");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }
            if (Util.hasClassePersonal(clazz)) {
                settarClassePersonalMax(gravarArq, clazz);
                gravarArq.println("");
            }

            gravarArq.println("        List<"+nc+"> "+Util.primeiraMinuscula(nc)+"s = new ArrayList<>();");
            gravarArq.println("        "+ncm+"s.add("+ncm+");");

            if (Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }

            gravarArq.println("");
            gravarArq.println("        Page<"+nc+"> msPage = new PageImpl<>("+Util.primeiraMinuscula(nc)+"s);");
            gravarArq.println("        Mockito.when("+Util.primeiraMinuscula(nc)+"Repository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);");
            if (Util.hasTipoDTO(clazz)) {
                montarMockDTO(gravarArq, clazz);
            }

            gravarArq.println("        service.save(dtoOk);");
            gravarArq.println("");
            gravarArq.println("        Collection<Invocation> invocations = Mockito.mockingDetails("+Util.primeiraMinuscula(nc)+"Repository).getInvocations();");
            gravarArq.println("        Assertions.assertEquals(3, invocations.size());");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void findByIdInternoErroIdNaoEncontrado() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOkLimite();");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }
            gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
            gravarArq.println("        try {");
            gravarArq.println("            service.findByIdInterno(idErro);");
            gravarArq.println("        } catch (BusinessRuleException e) {");
            gravarArq.println("            Assertions.assertEquals(\""+nc+" não encontrado\", e.getMessage());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void findByIdInternoOk() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }
            gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
            gravarArq.println("        Assertions.assertEquals("+ncm+", service.findByIdInterno(id));");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void updateOk() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            settarValorId(gravarArq, clazz);
            gravarArq.println(BEANS);
            gravarArq.println("");
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");

            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }

            if (Util.hasTipoClassePersonal(clazz)) {
                settarClassePersonal(gravarArq, clazz);
            }

            if(Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }


            if(Util.hasFilial(clazz)) {
                gravarArq.println("        "+ncm+".setFilial(montarFilial());");
            }

            gravarArq.println("");
            gravarArq.println("        List<"+nc+"> "+Util.primeiraMinuscula(nc)+"s = new ArrayList<>();");
            gravarArq.println("        "+Util.primeiraMinuscula(nc)+"s.add("+Util.primeiraMinuscula(nc)+");");
            gravarArq.println("        Page<"+nc+"> msPage = new PageImpl<>("+Util.primeiraMinuscula(nc)+"s);");

            gravarArq.println("");
            gravarArq.println("        Mockito.when("+Util.primeiraMinuscula(nc)+"Repository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);");
            gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
            gravarArq.println("");
            if (Util.hasCompany(clazz)) {
                gravarArq.println("        service.update(dtoOk, montarCompany());");
            } else {
                gravarArq.println("        service.update(dtoOk);");
            }

            gravarArq.println("        Collection<Invocation> invocations = Mockito.mockingDetails("+ncm+"Repository).getInvocations();");

            if (Util.hasCompany(clazz)) {
                gravarArq.println("        Assertions.assertEquals(5, invocations.size());");
            } else {
                gravarArq.println("        Assertions.assertEquals(2, invocations.size());");
            }

            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void updateErro() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            settarValorId(gravarArq, clazz);
            gravarArq.println(BEANS);
            gravarArq.println("");
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }

            if (Util.hasTipoClassePersonal(clazz)) {
                settarClassePersonal(gravarArq, clazz);
            }

            if(Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }

            if(Util.hasFilial(clazz)) {
                gravarArq.println("        "+ncm+".setFilial(montarFilial());");
            }

            gravarArq.println("");
            gravarArq.println("        List<"+nc+"> "+Util.primeiraMinuscula(nc)+"s = new ArrayList<>();");
            gravarArq.println("        "+Util.primeiraMinuscula(nc)+"s.add("+Util.primeiraMinuscula(nc)+");");
            gravarArq.println("        Page<"+nc+"> msPage = new PageImpl<>("+Util.primeiraMinuscula(nc)+"s);");

            gravarArq.println("");
            gravarArq.println("        Mockito.when("+Util.primeiraMinuscula(nc)+"Repository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(msPage);");
            gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
            gravarArq.println("        Mockito.when("+ncm+"Repository.save(Mockito.any())).thenThrow(new RuntimeException(\"erro entity\"));");
            gravarArq.println("");
            gravarArq.println("        try {");
            if (Util.hasCompany(clazz)) {
                gravarArq.println("            service.update(dtoOk, montarCompany());");
            } else {
                gravarArq.println("            service.update(dtoOk);");
            }

            gravarArq.println("        } catch (Exception e) {");
            gravarArq.println("            Assertions.assertEquals(\"erro desconhecido: erro entity\", e.getMessage());");
            gravarArq.println("        }");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("    @Test");
            gravarArq.println("    public void deleteOk() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            settarValorId(gravarArq, clazz);
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
            if(Util.hasCompany(clazz)) {
                gravarArq.println("        "+ncm+".setCompany(montarCompany());");
            }

            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }

            gravarArq.println("");
            gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
            gravarArq.println("");
            if (Util.hasCompany(clazz)) {
                gravarArq.println("        service.delete(dtoOk.getId(), montarCompany());");
            } else {
                gravarArq.println("        service.delete(dtoOk.getId());");
            }

            gravarArq.println("        Collection<Invocation> invocations = Mockito.mockingDetails("+ncm+"Repository).getInvocations();");
            gravarArq.println("");
            gravarArq.println("        Assertions.assertEquals(2, invocations.size());");
            gravarArq.println("    }");
            gravarArq.println("");

            if (Util.hasCompany(clazz)) {
                gravarArq.println("    @Test");
                gravarArq.println("    public void deleteCompanyDiferente() {");
                gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
                settarValorId(gravarArq, clazz);
                gravarArq.println(BEANS);
                gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
                gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");
                if(Util.hasCompany(clazz)) {
                    gravarArq.println("        "+ncm+".setCompany(montarCompanyErro());");
                }

                if (Util.hasTipoDTO(clazz)) {
                    settarDtos(gravarArq, clazz);
                }

                gravarArq.println("");
                gravarArq.println("        Mockito.when("+ncm+"Repository.findById(id)).thenReturn(Optional.of("+ncm+"));");
                gravarArq.println("");
                gravarArq.println("        try {");
                gravarArq.println("            service.delete(dtoOk.getId(), montarCompany());");
                gravarArq.println("        } catch (BusinessRuleException e) {");
                gravarArq.println("            Assertions.assertEquals(\"Este "+nc+" não pertence a esta empresa\", e.getMessage());");
                gravarArq.println("        }");
                gravarArq.println("    }");
                gravarArq.println("");
            }


            gravarArq.println("    @Test");
            gravarArq.println("    public void montarDTOOk() {");
            gravarArq.println("        "+nc+"DTO dtoOk = montarDtoOK();");
            gravarArq.println(BEANS);
            gravarArq.println("        "+nc+" "+Util.primeiraMinuscula(nc)+" = new "+nc+"();");
            gravarArq.println("        beans.updateObjectos("+Util.primeiraMinuscula(nc)+", dtoOk);");

            if (Util.hasTipoDTO(clazz)) {
                settarDtos(gravarArq, clazz);
            }
            if (Util.hasDate(clazz)) {
                settarDateEntity(gravarArq, clazz, "dtoOk");
            }
            gravarArq.println("");
            if (Util.hasTipoDTO(clazz)) {
                montarMockDTO(gravarArq, clazz);
            }

            if (Util.hasClassePersonal(clazz)) {
                settarClassePersonal(gravarArq, clazz, ncm);
            }

            gravarArq.println("");
            gravarArq.println("        "+nc+"DTO n = new "+nc+"DTO();");
            gravarArq.println("        beans.updateObjectos(n, "+ncm+");");
            if (Util.hasTipoDTO(clazz)) {
                settarDtoField(gravarArq, clazz);
            }

            if (Util.hasClassePersonal(clazz)) {
                resettarFieldsPersonal(gravarArq, clazz);
            }

            if (Util.hasDate(clazz)) {
                settarDateFieldDTO(gravarArq, clazz, "dtoOk");
            }

            gravarArq.println("");

            gravarArq.println("        "+nc+"DTO novo = service.montarDTO("+ncm+");");
            gravarArq.println("");
            gravarArq.println("        Assertions.assertEquals(dtoOk, novo);");
            gravarArq.println("        Assertions.assertEquals(dtoOk, n);");
            gravarArq.println("    }");
            gravarArq.println("");
            gravarArq.println("}");

        } catch (Exception e) {

        }
    }

    private static void settarClassePersonalMax(PrintWriter gravarArq, Class clazz) {
        String nc = Util.primeiraMaiuscula(clazz.getSimpleName());
        String ncm = Util.primeiraMinuscula(nc);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                String fn = Util.primeiraMaiuscula(field.getName());
                gravarArq.println("        "+ncm+".set"+fn+"(montar"+fn+"());");
            }

        }
    }

    private static void resettarFieldsPersonal(PrintWriter gravarArq, Class clazz) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                String fn = Util.primeiraMaiuscula(field.getName());
                String pt = Util.primeiraMaiuscula(personal.busca());
                gravarArq.println("        n.set"+fn+pt+"(dtoOk.get"+fn+pt+"());");

                if (!personal.exibe().equalsIgnoreCase("")) {
                    String ex = Util.primeiraMaiuscula(personal.exibe());
                    gravarArq.println("        n.set"+fn+ex+"(dtoOk.get"+fn+ex+"());");
                }
            }

        }

    }

    private static void settarClassePersonal(PrintWriter gravarArq, Class clazz, String ncm) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                String fn = Util.primeiraMaiuscula(field.getName());
                gravarArq.println("        "+ncm+".set"+fn+"(montar"+fn+"());");
            }

        }
    }

    private static void mockClassePersonal(PrintWriter gravarArq, Class clazz) {

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                String fn = Util.primeiraMaiuscula(field.getName());
                String fnm = Util.primeiraMinuscula(fn);
                String pt = Util.primeiraMaiuscula(personal.busca());

                gravarArq.print("        Mockito.when("+fnm+"Service.findBy"+pt+"Interno(");
                gravarArq.print("Mockito.any())).thenReturn(montar"+fn+"());\n");
            }
        }

    }

    private static void montarFieldsClassePersonal(PrintWriter gravarArq, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null && !field.getName().equalsIgnoreCase("company")
                && !field.getName().equalsIgnoreCase("filial")) {
                String fn = Util.primeiraMaiuscula(field.getName());
                gravarArq.println("    private "+fn+" montar"+fn+"() {");
                montarFieldsEntity(gravarArq, field, null);
                gravarArq.println("        return "+field.getName()+";");
                gravarArq.println("    }");
                gravarArq.println("");
            }
        }
    }

    private static void montarMockPersonal(PrintWriter gravarArq, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Set<String> nomes = new HashSet<>();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n");
                builder.append("    @Mock\n");
                builder.append("    private "+Util.primeiraMaiuscula(field.getType().getSimpleName())+"Service "+field.getName()+"Service;");
                nomes.add(builder.toString());
            }
        }

        nomes.forEach(gravarArq::println);
    }

    private static void settarDateEntity(PrintWriter gravarArq, Class clazz, String nomeDto) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String nc = Util.primeiraMaiuscula(clazz.getSimpleName());
            String ncm = Util.primeiraMinuscula(nc);
            String fd = Util.primeiraMaiuscula(field.getName());

            if (field.getType().equals(Date.class)) {
                gravarArq.println("        "+ncm+".set"+fd+"(beans.converterStringToDate("+nomeDto+".get"+fd+"S()));");
            }
        }
    }

    private static void settarDateFieldDTO(PrintWriter gravarArq, Class clazz, String nomeDto) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType().equals(Date.class)) {
                String fn = Util.primeiraMaiuscula(field.getName());
                gravarArq.println("        n.set"+fn+"S("+nomeDto+".get"+fn+"S());");
            }
        }

    }

    private static void settarDtoField(PrintWriter gravarArq, Class clazz) {

        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+clazz.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();


            for (Field field : fields) {
                String fn = Util.primeiraMaiuscula(field.getName());
                ValidationClass validation = field.getDeclaredAnnotation(ValidationClass.class);
                if (validation != null) {
                    gravarArq.println("        n.set"+fn+"(dtoOk.get"+fn+"());");
                }
            }

        } catch (Exception e) {

        }


    }

    public static void analisarClasseTest(Class clazz) {
        String tipo = "serviceTest";
        Util.montarPathTest(clazz);
        Util.criarPasta(CriarDepencias.PATH_TEST);


        String nomeArquivo = clazz.getSimpleName().concat(Util.primeiraMaiuscula(tipo)).concat(".java");

        File file = new File(CriarDepencias.PATH_TEST.concat("/"), nomeArquivo);
        try (FileReader arq = new FileReader(file)){
            List<String> linhas = new ArrayList<>();
            Map<String, Long> map = new HashMap<>();
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();
            while (linha != null) {
                linhas.add(linha);
                if ((linha.startsWith("    private") || linha.startsWith("    public")) &&
                        linha.endsWith(" {")) {
                    if (map.containsKey(linha)) {
                        long qtd = map.get(linha) + 1;
                        map.put(linha, qtd);
                    } else {
                        map.put(linha, 1L);
                    }
                }

                linha = lerArq.readLine();
            }

            boolean excluir = false;
            for (int i = 0; i < linhas.size(); i++) {
                String l = linhas.get(i);
                if (map.containsKey(l) && map.get(l) > 1L) {
                    excluir = true;
                    map.put(l, map.get(l) - 1L);
                }

                if (excluir) {
                    linhas.remove(i);
                    i--;
                    if (l.equalsIgnoreCase("    }")) {
                        excluir = false;
                    }
                }
            }

            analisarMetodosLinhasDuplicadas(linhas);
            removerEnterDobrado(linhas);
            Beans beans = new Beans();
            String nome = file.getAbsolutePath();
            file.delete();
            beans.escreverArquivo(linhas, new File(nome));

        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n",
                    e.getMessage());
        }
    }

    private static void removerEnterDobrado(List<String> linhas) {

        boolean ant = false;
        for (int i = 0; i < linhas.size(); i++) {

            if (linhas.get(i).equalsIgnoreCase("") && ant) {
                linhas.remove(i);
                i = 0;
            } else ant = linhas.get(i).equalsIgnoreCase("");

        }

    }

    private static void analisarMetodosLinhasDuplicadas(List<String> linhas) {

        List<String> metodo = new ArrayList<>();
        boolean met = false;
        for (int i = 0; i < linhas.size(); i++) {
            String l = linhas.get(i);

            if ((l.startsWith("    private") || l.startsWith("    public")) &&
                    l.endsWith(" {")) {
                met = true;
            }

            if (met) {
                metodo.add(l);
                if (l.endsWith("    }")) {
                    met = false;
                    Map<String, Long> map = analisandoMetodo(metodo);
                    List<String> aa = new ArrayList<>(map.keySet());
                    boolean zerar = false;
                    for (String s : aa) {
                        if (map.containsKey(s) && map.get(s) > 1) {
                            int posicao = i;
                            zerar = true;
                            while(true) {
                                if (linhas.get(posicao).equalsIgnoreCase(s)) {
                                    linhas.remove(posicao);
                                    long qtd = map.get(s) - 1;
                                    map.put(s, qtd);
                                    break;
                                } else {
                                    posicao --;
                                }
                            }
                        }

                    }
                    if (zerar) {
                        i = 0;
                    }
                    metodo = new ArrayList<>();
                }
            }
        }
    }

    private static Map<String, Long> analisandoMetodo(List<String> metodo) {
        Map<String, Long> map = new HashMap<>();

        for (int i = 0; i < metodo.size(); i++) {
            String l = metodo.get(i);
            if (!l.equalsIgnoreCase("")) {
                if (map.containsKey(l)) {
                    long valor = map.get(l)+1;
                    map.put(l, valor);
                } else {
                    map.put(l, 1L);
                }
            }
        }

        List<String> aa = map.keySet().stream().collect(Collectors.toList());
        for (String s : aa) {
            if (map.containsKey(s)) {
                if (map.get(s) == 1) {
                    map.remove(s);
                }
            }
        }
        return map;
    }

    private static void settarValorId(PrintWriter gravarArq, Class clazz) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Id classeDTO = field.getDeclaredAnnotation(Id.class);

            if (classeDTO != null) {
                if (field.getType().equals(Long.class)) {
                    gravarArq.println("        dtoOk.set"+Util.primeiraMaiuscula(field.getName())+"(1L);");
                } else if (field.getType().equals(String.class)) {
                    gravarArq.println("        dtoOk.set"+Util.primeiraMaiuscula(field.getName())+"(a);");
                } else if (field.getType().equals(Double.class)) {
                    gravarArq.println("        dtoOk.set"+Util.primeiraMaiuscula(field.getName())+"(1d);");
                }
            }
        }

    }

    private static void montarMockDTO(PrintWriter gravarArq, Class clazz) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClasseDTO classeDTO = field.getDeclaredAnnotation(ClasseDTO.class);
            if (classeDTO != null) {
                String fn = field.getType().getSimpleName();
                String fnm = Util.primeiraMinuscula(fn);
                gravarArq.println("        Mockito.when("+fnm+"Service.montarDTO(Mockito.any("+fn+".class))).thenReturn(montar"+fn+"DTO());");
            }
        }
    }

    private static void settarDtos(PrintWriter gravarArq, Class clazz) {
        String cn = clazz.getSimpleName();
        String cnm = Util.primeiraMinuscula(cn);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClasseDTO classeDTO = field.getDeclaredAnnotation(ClasseDTO.class);
            if (classeDTO != null) {
                String fn = field.getType().getSimpleName();
                gravarArq.println("        "+cnm+".set"+fn+"(montar"+fn+"());");
            }
        }
    }

    private static void settarClassePersonal(PrintWriter gravarArq, Class clazz) {
        String cn = clazz.getSimpleName();
        String cnm = Util.primeiraMinuscula(cn);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClassePersonal classeDTO = field.getDeclaredAnnotation(ClassePersonal.class);
            if (classeDTO != null) {
                String fn = field.getType().getSimpleName();
                gravarArq.println("        "+cnm+".set"+fn+"(montar"+fn+"());");
            }
        }
    }

    private static void montarClasseDTOs(PrintWriter gravarArq, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClasseDTO classeDTO = field.getDeclaredAnnotation(ClasseDTO.class);
            if (classeDTO != null) {
                String fn = field.getType().getSimpleName();
                gravarArq.println("    private "+fn+" montar"+fn+"() {");
                montarFieldsEntity(gravarArq, field, null);
                gravarArq.println("        return "+ Util.primeiraMinuscula(fn)+";");
                gravarArq.println("    }");
                gravarArq.println("");

                try {
                    String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+clazz.getSimpleName()+"DTO";
                    Class dto = Class.forName(classeDto);
                    Field[] fields1 = dto.getDeclaredFields();
                    for (Field field1 : fields1) {
                        ValidationClass valid = field1.getDeclaredAnnotation(ValidationClass.class);
                        if (valid != null) {
                            String fn1 = field1.getType().getSimpleName();
                            gravarArq.println("    private " + fn1 + " montar" + fn1 + "() {");
                            montarFieldsDTO(gravarArq, field1, null);
                            gravarArq.println("        return " + Util.primeiraMinuscula(fn1) + ";");
                            gravarArq.println("    }");
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    private static void montarMocks(PrintWriter gravarArq, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ClasseDTO classeDTO = field.getDeclaredAnnotation(ClasseDTO.class);
            if (classeDTO != null) {
                gravarArq.println("");
                gravarArq.println("    @Mock");
                gravarArq.println("    private "+Util.primeiraMaiuscula(field.getName())+"Service "+field.getName()+"Service;");
            }
        }

    }

    private static void fieldsDTO(Class clazz, Map<String, String> mapa) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Util.isTipoDTO(field)) {
                String frase = CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+".dto."+field.getType().getSimpleName()+"DTO;";
                mapa.put(frase, frase);
                fieldsDTO(field.getType(), mapa);
            }
        }
    }

    private static void fieldsDTOService(PrintWriter gravarArq, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Util.isTipoDTO(field)) {
                gravarArq.println(CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+".service."+field.getType().getSimpleName()+"Service;");
            }
        }
    }

    private static void montarFieldsOKLimite(PrintWriter gravarArq, Class c, String dtoOk) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();
            for (Field field : fields) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();
                    String pattern = valid.datePattern();

                    if (lengthMax != Long.MAX_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMax) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+lengthMax+"L);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+field.getType().getDeclaredFields()[0].getName()+");");
                        }
                    } else if (!dataMax.equalsIgnoreCase("")) {
                        montarDateFieldMax(gravarArq, field, dataMin, +2, dtoOk);
                    } else if (required) {
                        if (field.getName().startsWith("data")) {
                            String dataS = "beans.getDataPattern(\"+1d"+"\", \"max\", "+2+"));";
                            gravarArq.println("        "+dtoOk+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            long valor = lengthMin;
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+valor+"L);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+(field.getType().getDeclaredFields()[0].getName())+");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (lengthMin - 1) + "D);");
                        }
                    }
                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsDTO(gravarArq, field, dtoOk);
                }
            }

        } catch (Exception e) {

        }
    }

    private static void montarFieldsErroMin(PrintWriter gravarArq, Class c, String dtoOk) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();
            for (Field field : fields) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();
                    String pattern = valid.datePattern();

                    if (lengthMin != Long.MIN_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin - 1) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(\"" + valor + "\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (lengthMin - 1) + "L);");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (lengthMin - 1) + "D);");
                        }

                    }else if (!dataMin.equalsIgnoreCase("")) {
                        montarDateFieldMin(gravarArq, field, dataMin, +2, dtoOk);
                    } else if (required) {
                        if (field.getName().startsWith("data")) {
                            String dataS = "beans.getDataPattern(\"+1d"+"\", \"max\", "+2+"));";
                            gravarArq.println("        "+dtoOk+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            long valor = lengthMin;
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + valor + "L);");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+(lengthMin-1)+"D);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+(field.getType().getDeclaredFields()[0].getName())+");");
                        }
                    }
                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsDTO(gravarArq, field, dtoOk);
                }
            }

        } catch (Exception e) {

        }
    }

    private static void montarFieldsErroMax(PrintWriter gravarArq, Class c, String dtoOk) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();
            for (Field field : fields) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();

                    if (lengthMax != Long.MAX_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMax + 1) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(\"" + valor + "\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (lengthMax + 1) + "L);");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (lengthMax + 1) + "D);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + fn + "." + (field.getType().getDeclaredFields()[0].getName()) + ");");
                        }
                    } else if (!dataMax.equalsIgnoreCase("")) {
                        montarDateFieldMax(gravarArq, field, dataMax, 20, dtoOk);
                    } else if (required) {
                        if (field.getName().startsWith("data")) {
                            String dataS = "beans.getDataPattern(\"+1d"+"\", \"max\", "+2+"));";
                            gravarArq.println("        "+dtoOk+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            long valor = lengthMin;
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + valor + "L);");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+(lengthMin-1)+"D);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+(field.getType().getDeclaredFields()[0].getName())+");");
                        }
                    }
                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsDTO(gravarArq, field, dtoOk);
                }
            }

        } catch (Exception e) {

        }
    }

    private static void montarFieldsOK(PrintWriter gravarArq, Class c, String dtoOk) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();
            for (Field field : fields) {
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();
                    String pattern = valid.datePattern();

                    if (lengthMin != Long.MIN_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                            long valor = lengthMin;
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+valor+"L);");
                        } else if (field.getType().isEnum()) {
                            String fn = Util.primeiraMaiuscula(field.getName());
                            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+(field.getType().getDeclaredFields()[0].getName())+");");
                        }
                    } else if (!dataMin.equalsIgnoreCase("")) {
                        montarDateFieldMax(gravarArq, field, dataMax, -2, dtoOk);
                    } else if (required) {
                        popularOutrosCampos(gravarArq, field, dtoOk);
                    }
                } else {
                    if (field.getName().contains("data")) {
                        String fn = Util.primeiraMaiuscula(field.getName());

                        gravarArq.println("        "+dtoOk+".set"+fn+"(beans.getDataPattern(\"+1d\", \"max\", -2));");
                    } else {
                        popularOutrosCampos(gravarArq, field, dtoOk);
                    }

                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsDTO(gravarArq, field, dtoOk);
                }
            }

        } catch (Exception e) {

        }
    }

    private static void montarDateFieldMax(PrintWriter gravarArq, Field field, String dataMin, int dias, String nomeDto) {
        String dataS = "beans.getDataPattern(\""+dataMin+"\", \"max\", "+dias+"));";
        gravarArq.println("        "+nomeDto+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);

    }

    private static void montarDateFieldMin(PrintWriter gravarArq, Field field, String dataMin, int dias, String nomeDto) {
        String dataS = "beans.getDataPattern(\""+dataMin+"\", \"min\", "+dias+"));";
        gravarArq.println("        "+nomeDto+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);

    }

    private static void popularOutrosCampos(PrintWriter gravarArq, Field field, String dtoOk) {
        if (field.getName().startsWith("data")) {
            String dataS = "beans.getDataPattern(\"+1d"+"\", \"max\", "+2+"));";
            gravarArq.println("        "+dtoOk+".set"+Util.primeiraMaiuscula(field.getName())+"("+dataS);
        } else if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
            String valor = "aaa";
            String fn = Util.primeiraMaiuscula(field.getName());
            gravarArq.println("        "+dtoOk+".set"+fn+"(\""+valor+"\");");
        } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
            long valor = 123L;
            String fn = Util.primeiraMaiuscula(field.getName());
            gravarArq.println("        "+dtoOk+".set"+fn+"("+valor+"L);");
        } else if (field.getType().isEnum()) {
            String fn = Util.primeiraMaiuscula(field.getName());
            gravarArq.println("        "+dtoOk+".set"+fn+"("+fn+"."+(field.getType().getDeclaredFields()[0].getName())+");");
        } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
            String fn = Util.primeiraMaiuscula(field.getName());
            gravarArq.println("        " + dtoOk + ".set" + fn + "(" + (1) + "D);");
        }
    }

    private static void montarFieldsDTO(PrintWriter gravarArq, Field fd, String dto) {
        try {
            Class c = Class.forName(fd.getType().getName());

            String classeDto;
            if (!c.getSimpleName().endsWith("DTO")) {
                classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
            } else {
                classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName();
            }


            Class cdto = Class.forName(classeDto);

            String cn = c.getSimpleName();
            String cnm = Util.primeiraMinuscula(cn);
            Field[] dtoFields = cdto.getDeclaredFields();
            Field[] fields = c.getDeclaredFields();
            gravarArq.println();
            gravarArq.println("        "+cn+" "+cnm+" = new "+cn+"();");
            for (Field field : fields) {
                String fn = Util.primeiraMaiuscula(field.getName());
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                Field campoDTO = buscarField(field, dtoFields);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();

                    if (lengthMin != Long.MIN_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }

                            gravarArq.println("        "+cnm+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().equals(Long.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+"L);");
                        } else if (field.getType().equals(Double.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+"D);");
                        } else if (field.getType().equals(Integer.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+");");
                        } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+");");
                        }
                    } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                        gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+");");
                    }
                } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                    gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+");");
                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsDTO(gravarArq, field, cnm);
                }
            }

            if (dto != null) {
                gravarArq.println("        "+dto+".set"+Util.primeiraMaiuscula(fd.getName())+"("+cnm+");");
                gravarArq.println("");
            }

        } catch (Exception e) {

        }

    }

    private static void montarFieldsEntity(PrintWriter gravarArq, Field fd, String dto) {
        try {
            Class c = Class.forName(fd.getType().getName());

            String classeDto;
            if (!c.getSimpleName().endsWith("DTO")) {
                if (c.getTypeName().endsWith("DTO")) {
                    classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
                } else {
                    classeDto = c.getName();
                }
            } else {
                classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName();
            }


            Class cdto = Class.forName(classeDto);

            String cn = c.getSimpleName();
            String cnm = Util.primeiraMinuscula(cn);
            Field[] dtoFields = cdto.getDeclaredFields();
            Field[] fields = c.getDeclaredFields();
            gravarArq.println();
            gravarArq.println("        "+cn+" "+cnm+" = new "+cn+"();");
            for (Field field : fields) {
                String fn = Util.primeiraMaiuscula(field.getName());
                Validation valid = field.getDeclaredAnnotation(Validation.class);
                Field campoDTO = buscarField(field, dtoFields);
                if (valid != null) {
                    boolean required = valid.required();
                    long lengthMax = valid.lengthMax();
                    long lengthMin = valid.lengthMin();
                    String dataMin = valid.dateMin();
                    String dataMax = valid.dateMax();

                    if (lengthMin != Long.MIN_VALUE) {
                        if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                            String valor = "";
                            while (valor.length() < lengthMin) {
                                valor = valor.concat("a");
                            }

                            gravarArq.println("        "+cnm+".set"+fn+"(\""+valor+"\");");
                        } else if (field.getType().equals(Long.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+"L);");
                        } else if (field.getType().equals(Double.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+"D);");
                        } else if (field.getType().equals(Integer.class)) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+lengthMin+");");
                        } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                            gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+".name());");
                        }
                    } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                        gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+".name());");
                    }
                } else if (campoDTO != null && campoDTO.getType().isEnum()) {
                    gravarArq.println("        "+cnm+".set"+fn+"("+fn+"."+campoDTO.getType().getDeclaredFields()[0].getName()+".name());");
                }

                ValidationClass vClass = field.getDeclaredAnnotation(ValidationClass.class);
                if (vClass != null) {
                    montarFieldsEntity(gravarArq, field, cnm);
                }
            }

            if (dto != null) {
                gravarArq.println("        "+dto+".set"+Util.primeiraMaiuscula(fd.getName())+"("+cnm+");");
                gravarArq.println("");
            }

        } catch (Exception e) {

        }

    }

    public static Field buscarField(Field field, Field[] fields) {

        for (Field field1 : fields) {
            if (field.getName().equalsIgnoreCase(field1.getName())) {
                return field1;
            }
        }

        return null;
    }

    private static void montarImports(PrintWriter gravarArq, String nc, Class clazz) {
        gravarArq.println("import com.ferracio.ongs.automatizar.Util;");
        gravarArq.println("import com.ferracio.ongs.commons.exception.BusinessRuleException;");
        gravarArq.println("import com.ferracio.ongs.commons.util.Beans;");
        gravarArq.println("import org.mockito.junit.jupiter.MockitoExtension;");
        gravarArq.println("import org.mockito.junit.jupiter.MockitoSettings;");
        gravarArq.println("import org.mockito.quality.Strictness;");
        gravarArq.println("import org.junit.jupiter.api.extension.ExtendWith;");


        Map<String, String> mapa = new HashMap<>();

        if (Util.hasTipoDTO(clazz)) {
            fieldsDTO(clazz, mapa);
        }

        String frase = "import com.ferracio.ongs.commons.util.Validation;";
        mapa.put(frase, frase);

        if (Util.hasEnum(clazz)) {
            fieldsEnumImport(clazz, mapa);
        }

        if (Util.hasClassePersonal(clazz)) {
            montarImportsPersonal(gravarArq, clazz);
        }

        fieldsEntity(clazz, mapa);

        gravarArq.println(CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+ ".dto."+nc+"DTO;");
        gravarArq.println(CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+ ".model."+nc+";");
        if (Util.hasTipoDTO(clazz)) {
            imporEntity(clazz, mapa);
        }
        gravarArq.println(CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+ ".repository."+nc+"Repository;");
        if (Util.hasTipoDTO(clazz)) {
            fieldsDTOService(gravarArq, clazz);
        }

        List<String> all = new ArrayList<>(mapa.keySet());
        all.forEach(gravarArq::println);
        gravarArq.println(CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+ ".service.impl."+nc+"ServiceImpl;");
        gravarArq.println("import com.ferracio.ongs.util.UtilTest;");
        gravarArq.println("import org.junit.jupiter.api.Assertions;");
        gravarArq.println("import org.junit.jupiter.api.BeforeEach;");
        gravarArq.println("import org.junit.jupiter.api.Test;");
        gravarArq.println("import org.mockito.InjectMocks;");
        gravarArq.println("import org.mockito.Mock;");
        gravarArq.println("import org.mockito.Mockito;");
        gravarArq.println("import org.mockito.invocation.Invocation;");
        gravarArq.println("import org.springframework.boot.test.context.SpringBootTest;");
        gravarArq.println("import org.springframework.data.domain.Page;");
        gravarArq.println("import org.springframework.data.domain.PageImpl;");
        gravarArq.println("import org.springframework.data.domain.Pageable;");
        gravarArq.println("import org.springframework.data.jpa.domain.Specification;");
        gravarArq.println("import org.springframework.test.util.ReflectionTestUtils;");
        gravarArq.println("");
        gravarArq.println("import java.lang.reflect.Field;");
        gravarArq.println("import java.util.*;");
    }

    private static void fieldsEntity(Class clazz, Map<String, String> mapa) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(Util.isClasseCriada(field)  ) {
                String frase = CriarDepencias.IMPORT+field.getType().getName()+";";
                mapa.put(frase, frase);

                if (!CriarDepencias.passouCompany) {
                    if (field.getType().getSimpleName().toLowerCase().equalsIgnoreCase("company")) {
                        CriarDepencias.passouCompany = true;
                    }

                    if (!field.isEnumConstant()) {
                        fieldsEntity(field.getType(), mapa);
                    }
                }
            }
        }

        fieldsEnumImport(clazz, mapa);
    }

    private static void montarImportsPersonal(PrintWriter gravarArq, Class c) {
        Field[] fields = c.getDeclaredFields();

        Set<String> nomes = new HashSet<>();
        for (Field field : fields) {
            ClassePersonal personal = field.getAnnotation(ClassePersonal.class);
            if (personal != null) {
                String pacote = field.getType().getPackage().getName();
                pacote = pacote.substring(0, pacote.lastIndexOf("."));

                nomes.add("import "+ field.getType().getName() + ";");
                nomes.add("import "+pacote+".service." + Util.primeiraMaiuscula(field.getType().getSimpleName()) + "Service;");
            }
        }

        nomes.forEach(gravarArq::println);
    }



    private static void fieldsEnumImport(Class c, Map<String, String> mapa) {
        String classeDto = CriarDepencias.PACKAGE_BASE+".dto."+c.getSimpleName()+"DTO";
        try {
            Class dto = Class.forName(classeDto);
            Field[] fields = dto.getDeclaredFields();

            for (Field field : fields) {
                ValidationClass valid = field.getDeclaredAnnotation(ValidationClass.class);

                if (field.getType().isEnum()) {
                    String frase = CriarDepencias.IMPORT+field.getType().getName()+";";
                    mapa.put(frase, frase);
                } else if (valid != null) {
                    fieldsEnumImportDTO(field.getType(), mapa);
                }
            }
        } catch (Exception e) {

        }
    }

    private static void fieldsEnumImportDTO( Class c, Map<String, String> mapa) {
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            ValidationClass valid = field.getDeclaredAnnotation(ValidationClass.class);

            if (field.getType().isEnum()) {
                String frase = CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+".enums."+field.getType().getSimpleName()+";";
                mapa.put(frase, frase);
            } else if (valid != null) {
                fieldsEnumImportDTO(field.getType(), mapa);
            }
        }
    }

    private static void imporEntity(Class clazz,  Map<String, String> mapa) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Util.isTipoDTO(field)) {
                String frase = CriarDepencias.IMPORT+CriarDepencias.PACKAGE_BASE+".model."+field.getType().getSimpleName()+";";
                mapa.put(frase, frase);
                imporEntity(field.getType(), mapa);
            }
        }
    }
}
