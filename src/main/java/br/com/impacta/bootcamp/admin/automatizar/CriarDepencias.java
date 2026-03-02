package br.com.impacta.bootcamp.admin.automatizar;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CriarDepencias {

    public static final String SOURCE = "src/main/java";
    public static final String SOURCE_TEST = "src/test/java";
    public static String PATH = "";
    public static String PATH_TEST = "";
    public static String PACKAGE_BASE = "";
    public static String PACKAGE_TEST = "";
    public static String IMPORT = "import ";
    public static String AUTOWIRED = "    @Autowired";
    public static String OVERRIDE = "    @Override";
    public static boolean passouCompany = false;
    public static boolean passouStatus = false;

    public static final String PRIVATE_STRING = "    private String ";

    public static void main(String[] args) {

        Class c = null;
        Util.montarPath(c);

        log.info("ola");

        CriarEnums.criarEnums();




        log.info("Criando classe test");
        CriarTest.criar(c);
        log.info("Criador finalizado");
        Util.sleep();

        CriarTest.analisarClasseTest(c);

        apagarLixos();
    }

    public static void apagarLixos() {
        File file = new File(CriarDepencias.PATH);
        List<File> files = Stream.of(file.listFiles()).filter(f -> f.getName().contains("home")).collect(Collectors.toList());

        while(!files.isEmpty()) {
            try {
                FileUtils.deleteDirectory(files.get(0));
                if (!files.get(0).exists()) {
                    files.remove(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
