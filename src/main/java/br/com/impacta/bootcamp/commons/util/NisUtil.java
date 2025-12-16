package br.com.impacta.bootcamp.commons.util;

public class NisUtil {

    public static boolean validar(String nis) {
        if (nis.contains(".")) {
            nis = removerPontosCpf(nis);
        }

        if (!isNumero(nis) || nis.length() != 11) {
            return false;
        }

        if (nis.equals("00000000000") ||
                nis.equals("11111111111") ||
                nis.equals("22222222222") || nis.equals("33333333333") ||
                nis.equals("44444444444") || nis.equals("55555555555") ||
                nis.equals("66666666666") || nis.equals("77777777777") ||
                nis.equals("88888888888") || nis.equals("99999999999") ||
                (nis.length() != 11))
            return(false);

        int sm = 0;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            String validador = "3298765432";
            for (int i = 0; i <= 9; i++) {
                sm += Integer.parseInt(nis.substring(i, i+1)) *
                        Integer.parseInt(validador.substring(i, i+1));
            }

            int digito = 11 - (sm % 11);

            if (digito == Integer.parseInt(nis.substring(nis.length()-1))) {
                return true;
            }

            return false;


        } catch (Exception erro) {
            return(false);
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
