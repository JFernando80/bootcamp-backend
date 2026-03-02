package br.com.impacta.bootcamp.admin.automatizar;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.stream.Stream;

public class CriarEnums {

    public static void criarEnums() {
        String tipo = "enums";

        File file = new File(CriarDepencias.PATH.concat("/").concat(tipo));

        String pacote = "controller";
        Util.criarPasta(pacote);

        String nomeArquivo = CriarDepencias.PACKAGE_BASE;
        nomeArquivo = nomeArquivo.substring(1+nomeArquivo.lastIndexOf("."))+Util.primeiraMaiuscula(tipo)+Util.primeiraMaiuscula(pacote);
        File arquivo = new File(CriarDepencias.PATH.concat("/").concat(pacote), Util.primeiraMaiuscula(nomeArquivo)+".java");


        try (FileWriter arq = new FileWriter(arquivo)) {
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.println("package ".concat(CriarDepencias.PACKAGE_BASE).concat(".").concat(pacote).concat(";"));
            gravarArq.println("");
            criarImports(gravarArq, file);
            gravarArq.println("");
            gravarArq.println("@RestController");
            gravarArq.println("@RequestMapping(value = \"/"+Util.toSneakCase(nomeArquivo).substring(0,1+nomeArquivo.toLowerCase().indexOf(pacote))+"\")");
            gravarArq.println("@Slf4j");
            gravarArq.println("public class "+Util.primeiraMaiuscula(nomeArquivo)+" {");
            gravarArq.println("");
            criarBody(gravarArq, file);
            gravarArq.println("}");
        } catch (Exception e) {
        }


    }

    private static void criarBody(PrintWriter gravarArq, File file) {

        File[] files = listarEnums(file);
        if (files != null) {

            for (File f : files) {
                String nome = f.getName();
                nome = nome.substring(0, nome.lastIndexOf("."));
                gravarArq.println("    @Monitorar");
                gravarArq.println("    @GetMapping(value = \"/"+Util.toSneakCase(nome)+"/all\")");
                gravarArq.println("    public JsonResponse get"+nome+"All() {");
                gravarArq.println("        return JsonResponse.ok(EnumUtils.allStatus(" + nome + ".values()));");
                gravarArq.println("    }");
                gravarArq.println("");
            }
        }
    }

    private static File[] listarEnums(File file) {
        if (file.isDirectory()) {
            return  file.listFiles();
        }
        return null;
    }

    private static void criarImports(PrintWriter gravarArq, File file) {
        gravarArq.println("import com.ferracio.ongs.commons.model.JsonResponse;");
        gravarArq.println("import com.ferracio.ongs.commons.util.Monitorar;");
        gravarArq.println("import com.ferracio.ongs.commons.util.EnumUtils;");

        File[] files = listarEnums(file);
        if (files != null) {
            Stream.of(files).forEach(f -> gravarArq.println(
                    "import "+CriarDepencias.PACKAGE_BASE+".enums."
                            +f.getName().substring(0, f.getName().lastIndexOf("."))+";"
            ));
        }

        gravarArq.println("");


        gravarArq.println("import lombok.extern.slf4j.Slf4j;");
        gravarArq.println("import org.springframework.web.bind.annotation.GetMapping;");
        gravarArq.println("import org.springframework.web.bind.annotation.RequestMapping;");
        gravarArq.println("import org.springframework.web.bind.annotation.RestController;");
    }

}
