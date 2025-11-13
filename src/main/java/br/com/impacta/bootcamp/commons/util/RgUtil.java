package br.com.impacta.bootcamp.commons.util;

public class RgUtil {

    public static boolean validar(String rg) {
        if (rg.contains(".")) {
            rg = removerPontosCpf(rg);
        }
        if (rg.length() != 9) {
            return false;
        }

        int sm = 0;

        try {
            String validador = "23456789";

            for (int i = 0; i < 8; i++) {
                sm += Integer.parseInt(rg.substring(i, i+1)) *
                        Integer.parseInt(validador.substring(i, i+1));
            }

            String digito = ""+(11 - (sm % 11));

            if (digito.equalsIgnoreCase("10")) {
                digito = "x";
            } else if (digito.equalsIgnoreCase("11")) {
                digito = "0";
            }

            if (digito.equalsIgnoreCase(rg.substring(rg.length()-1))) {
                return true;
            }

            return false;


        } catch (Exception erro) {
            return false;
        }
    }

    private static String removerPontosCpf(String valor) {
        while (valor.contains(".") ||
            valor.contains("-")) {
            valor = valor.replace(".", "");
            valor = valor.replace("-", "");
        }

        return valor;
    }

    private static boolean isNumero(String valor) {
        for (int i = 0 ; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
