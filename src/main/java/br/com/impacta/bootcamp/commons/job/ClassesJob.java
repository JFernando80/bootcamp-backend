package br.com.impacta.bootcamp.commons.job;

import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.admin.service.ClassesService;
import br.com.impacta.bootcamp.admin.service.ClassesVariaveisService;
import br.com.impacta.bootcamp.commons.util.Beans;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ClassesJob {

    @Autowired
    private Beans beans;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesVariaveisService classesVariaveisService;

    @Scheduled(fixedDelay = 86400000, initialDelay = 1000)
    private void contruirEsquemaClasses() {

        log.info("Iniciando o processmento das classes: "+beans.converterDateToString(new Date()));
        Set<Class<?>> classes = new HashSet<>();
        findAnnotatedClasses("br.com.impacta", classes);

        try {
            //exibe a lista classes
            for (Class<?> c : classes) {
                if (c.getName().contains("model") && !c.getSimpleName().equals("JsonResponse")
                        && !c.getSimpleName().equals("SearchCriteria")
                        && !c.getSimpleName().equals("Content")) {
                    Classes cl = classesService.findByName(c.getName());
                    if (cl == null) {
                        cl = new Classes();
                        cl.setName(c.getName());
                        cl.setSimpleName(c.getSimpleName());
                        classesService.save(classesService.montarDTO(cl));

                        cl = classesService.findByName(c.getName());
                    }

                    List<ClassesVariaveis> lista = new ArrayList<>();
                    beans.camposDaClasse(c, lista, cl);
                    for (ClassesVariaveis cv : lista) {
                        ClassesVariaveis cvv = classesVariaveisService.findByClassesAndVariavel(cl, cv.getVariavel());
                        if (cvv == null) {
                            try {
                                classesVariaveisService.save(classesVariaveisService.montarDTO(cv));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro ao ler as classes: "+e.getMessage(), e);
        }

        log.info("Finalizando o processmento das classes");
    }


    public void findAnnotatedClasses(String scanPackage, Set<Class<?>> classes) {
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage)) {
            printMetadata(beanDef, classes);
        }
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        return provider;
    }

    private void printMetadata(BeanDefinition beanDef, Set<Class<?>> classes) {
        try {
            Class<?> cl = Class.forName(beanDef.getBeanClassName());
            Entity findable = cl.getAnnotation(Entity.class);
            classes.add(cl);
        } catch (Exception e) {
            System.err.println("Got exception: " + e.getMessage());
        }
    }
}
