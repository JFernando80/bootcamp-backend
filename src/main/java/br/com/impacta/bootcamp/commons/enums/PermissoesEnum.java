package br.com.impacta.bootcamp.commons.enums;

import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.formacao.model.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum PermissoesEnum {

    CADASTRAR_COURSE(1, Constants.ATENDIDO, "Cadastrar Course", Constants.MENU, Status.ATIVO, 28385985763L),
    LISTAR_COURSE(2, Constants.ATENDIDO, "Listar Course", Constants.MENU, Status.ATIVO, 48117929230L),
    EDITAR_COURSE(3, Constants.ATENDIDO, "Editar Course", Constants.EDITAR, Status.ATIVO, 122803556242L),
    DELETAR_COURSE(4, Constants.ATENDIDO, "Deletar Course", Constants.DELETAR, Status.ATIVO, 637430260702L),RELATORIO_SENAI(0, Constants.ATENDIDO, "Relatorio Senai", Constants.MENU, Status.ATIVO, 403540333738L),




    ;

    public static void main(String[] args) {

        criarPermissoesClass(Course.class.getSimpleName());
//        criarPermissoesMenu("relatorioSenai");
//        criarPermissoesIndividual("evolucaoSistema", "MENU_ADM");
    }

    public static void criarPermissoesIndividual(String classeNome, String tipo) {

        Beans beans = new Beans();

        String classe = classeNome;
        String constante = Constants.MENU.toLowerCase();


        Long id = Stream.of(PermissoesEnum.values()).mapToLong(PermissoesEnum::getId).max().orElse(0L) + 1L;
        long valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
        System.out.println(tipo+"_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                ", \"Menu "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.MENU, Status.ATIVO, "+valor+"L),");

    }

    public static PermissoesEnum findBySerial(long serial) {
        Optional<PermissoesEnum> perm =  Arrays.stream(PermissoesEnum.values()).filter(p -> p.serial == serial).findAny();

        if (perm.isPresent()) {
            return perm.get();
        }

        return  null;
    }

    public static void criarPermissoesMenu(String classeNome) {

        Beans beans = new Beans();

        String classe = classeNome;
        String constante = Constants.ATENDIDO.toLowerCase();


        Long id = Stream.of(PermissoesEnum.values()).mapToLong(PermissoesEnum::getId).max().orElse(0L) + 1L;
        long valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
        System.out.println(beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                ", \""+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.MENU, Status.ATIVO, "+valor+"L),");

    }

    public static void criarPermissoesClass(String classe) {
        Beans beans = new Beans();

        try {
            String constante = Constants.ATENDIDO.toLowerCase();


            Long id = Stream.of(PermissoesEnum.values()).mapToLong(PermissoesEnum::getId).max().orElse(0L) + 1L;
            long valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
            System.out.println("CADASTRAR_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                    ", \"Cadastrar "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.MENU, Status.ATIVO, "+valor+"L),");

            id ++;
            valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
            System.out.println("LISTAR_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                    ", \"Listar "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.MENU, Status.ATIVO, "+valor+"L),");

            id ++;
            valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
            System.out.println("EDITAR_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                    ", \"Editar "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.EDITAR, Status.ATIVO, "+valor+"L),");

            id ++;
            valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
            System.out.println("DELETAR_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
                    ", \"Deletar "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.DELETAR, Status.ATIVO, "+valor+"L),");
//
//            id ++;
//            valor = (long) ( (Math.random()*1000000)*(Math.random()*1000000));
//            System.out.println("LISTAR_ALL_"+beans.camelToSnake(classe).toUpperCase() + "("+id+", Constants."+constante.toUpperCase()+
//                    ", \"Listar All "+beans.capitalize(beans.camelToSnake(classe).replaceAll("_", " "))+"\", Constants.LISTAR, Status.ATIVO, "+valor+"L),");

        } catch (Exception e) {

        }

    }



    private final long id;
    private final String permission;
    private final String permissionDescription;
    private final String screen;
    private final Status status;
    private final long serial;


    private static class Constants {
        public  static final String USUARIOS = "usuarios";
        public  static final String MENU = "Menu";
        public  static final String SEGURANCA = "Segurança";
        public  static final String EDITAR = "editar";
        public  static final String FUNCAO = "funcção";
        public  static final String DELETAR = "deletar";
        public  static final String LISTAR = "listar";
        public  static final String ADMIN = "admin";
        public  static final String EMPRESAS = "empresas";
        public  static final String CENTRO_DE_CUSTO = "centro de custo";
        public  static final String CONFIGURACOES = "configurações";
        public  static final String PRODUTO = "produto";
        public  static final String PEDIDO = "pedido";
        public  static final String RELATORIOS = "relatorios";
        public  static final String ECOMMERCE = "ecommerce";
        public  static final String FORNECEDORES = "fornecedores";
        public  static final String FINANCEIRO = "financeiro";
        public  static final String PROJETO = "projeto";
        public  static final String AGENDA = "agenda";
        public  static final String UPC = "upc";
        public  static final String RH = "rh";
        public  static final String COTACAO = "cotação";
        public  static final String ADMINISTRATIVO = "administrativo";
        public  static final String ATENDIDO = "atendido";
        public  static final String TREINAMENTO = "treinamento";
        public  static final String PESQUISA = "pesquisa";
    }
}
