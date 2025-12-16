package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.model.ClassesVariaveis;
import br.com.impacta.bootcamp.admin.repository.ClassesVariaveisRepository;
import br.com.impacta.bootcamp.admin.service.ClassesService;
import br.com.impacta.bootcamp.admin.service.ClassesVariaveisService;
import br.com.impacta.bootcamp.admin.specification.ClassesVariaveisSpecification;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.enums.Status;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.model.Content;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassesVariaveisServiceImpl implements ClassesVariaveisService {

    @Autowired
    private ClassesVariaveisRepository classesVariaveisRepository;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        ClassesVariaveisSpecification msCategoria = new ClassesVariaveisSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<ClassesVariaveis> msPage = classesVariaveisRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(ClassesVariaveisDTO dto) {
        isRepetido(dto);
        classesVariaveisRepository.save(montarEntity(dto));
    }

    @Override
    public void update(ClassesVariaveisDTO dto) {
        updateInterno(dto);
    }

    @Override
    public ClassesVariaveis findByIdInterno(Long id) {
        return classesVariaveisRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("ClassesVariaveis não encontrado"));
    }

    @Override
    public void delete(Long id) {
        ClassesVariaveis entity = findByIdInterno(id);
        classesVariaveisRepository.delete(entity);
    }

    @Override
    public ClassesVariaveisDTO montarDTO(ClassesVariaveis entity) {
        ClassesVariaveisDTO dto = new ClassesVariaveisDTO();
        beans.updateObjectos(dto, entity);
        dto.setTipo(entity.getTipo());
        dto.setVariavel(entity.getVariavel());
        dto.setHeader(entity.getVariavel());
        dto.setClassesDTO(classesService.montarDTO(entity.getClasses()));

        return dto;
    }

    @Override
    public List<ClassesVariaveisDTO> findAllByClassesAndStatus(Classes classes, Locale locale) {
        String local = locale.getLanguage() + "_" + locale.getCountry();
        List<ClassesVariaveisDTO> lista =  classesVariaveisRepository.findAllByClassesOrderByIdAsc(classes)
                .stream()
                .map(classesVariaveis -> montarDTO(classesVariaveis, local)).collect(Collectors.toList());

        List<ClassesVariaveisDTO> listaNova = new ArrayList<>();
        for (ClassesVariaveisDTO dto : lista) {
            if (dto.getTipo().equalsIgnoreCase("date")) {
                ClassesVariaveisDTO variaveisDTO = new ClassesVariaveisDTO();
                variaveisDTO.setStatus(dto.getStatus());
                variaveisDTO.setClassesDTO(dto.getClassesDTO());
                variaveisDTO.setVariavel(dto.getVariavel());
                variaveisDTO.setHeader(dto.getHeader() +" Inicio");
                variaveisDTO.setTipo(dto.getTipo());
                variaveisDTO.setId(dto.getId());
                listaNova.add(variaveisDTO);

                variaveisDTO = new ClassesVariaveisDTO();
                variaveisDTO.setStatus(dto.getStatus());
                variaveisDTO.setClassesDTO(dto.getClassesDTO());
                variaveisDTO.setVariavel(dto.getVariavel());
                variaveisDTO.setHeader(dto.getHeader() +" Fim");
                variaveisDTO.setTipo(dto.getTipo());
                variaveisDTO.setId(dto.getId());
                listaNova.add(variaveisDTO);

            } else {
                listaNova.add(dto);
            }
        }

        Set<String> ordem = new HashSet<>();
        for (ClassesVariaveisDTO classe : listaNova) {
            ordem.add(classe.getVariavel());
        }

        ClassesVariaveisDTO dto = new ClassesVariaveisDTO();
        dto.setTipo("ordem");
        dto.setOrdem(new ArrayList<>(ordem));
        dto.setVariavel("ordem");
        dto.setHeader("ordem");

        listaNova.add(dto);
        return listaNova;
    }

    @Override
    public ClassesVariaveis findByClassesAndVariavel(Classes cl, String variavel) {
        return classesVariaveisRepository.findByClassesAndVariavel(cl, variavel);
    }

    @Override
    public List<ClassesVariaveisDTO> findAllByClassesAndStatus(Classes classes, Content content) {
        String local = content.getLocale().getLanguage() + "_" + content.getLocale().getCountry();

        List<ClassesVariaveisDTO> listaNova;
        Set<String> ordem = new HashSet<>();

        listaNova = montarPorClasses(local, classes);

        for (ClassesVariaveisDTO classe : listaNova) {
            ordem.add(classe.getVariavel());
        }

        ClassesVariaveisDTO dto = new ClassesVariaveisDTO();
        dto.setTipo("ordem");
        dto.setOrdem(new ArrayList<>(ordem));
        dto.setVariavel("ordem");
        dto.setHeader("ordem");
        dto.setId(50000000L);

        listaNova.add(dto);
        return listaNova;
    }

    private List<ClassesVariaveisDTO> montarPorClasses(String local, Classes classes) {
        List<ClassesVariaveisDTO> listaNova = new ArrayList<>();
        List<ClassesVariaveisDTO> lista =  classesVariaveisRepository.findAllByClassesOrderByIdAsc(classes)
                .stream()
                .map(classesVariaveis -> montarDTO(classesVariaveis, local)).collect(Collectors.toList());

        for (ClassesVariaveisDTO dto : lista) {
            if (dto.getTipo().equalsIgnoreCase("date")) {
                listaNova.add(ClassesVariaveisDTO.montar(dto, "Inicio"));

                listaNova.add(ClassesVariaveisDTO.montar(dto, " Fim") );
            } else {
                listaNova.add(dto);
            }
        }

        return listaNova;
    }

    private ClassesVariaveis montarEntity(ClassesVariaveisDTO dto) {
        ClassesVariaveis entity = new ClassesVariaveis();
        beans.updateObjectos(entity, dto);

        Classes classes = classesService.findByName(dto.getClassesDTO().getName());
        entity.setClasses(classes);

        return entity ;
    }

    private void updateInterno(ClassesVariaveisDTO dto) {
        isRepetido(dto);
        ClassesVariaveis entity = findByIdInterno(dto.getId());
        ClassesVariaveis updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            classesVariaveisRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(ClassesVariaveisDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(ClassesVariaveisDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("name");
        criteria.setValue(dto.getClassesDTO().getName());
        criteria.setOperation(SearchOperation.EQUAL.name());
        criteria.setClasses("classes");
        lista.add(criteria);

        criteria = new SearchCriteriaDTO();
        criteria.setKey("variavel");
        criteria.setValue(dto.getVariavel());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    ClassesVariaveisDTO dto1 = (ClassesVariaveisDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este classesVariaveis cadastrado");
            }
        }
    }

    private ClassesVariaveisDTO montarDTO(ClassesVariaveis classesVariaveis, String local) {

        ClassesDTO dto = new ClassesDTO(classesVariaveis.getClasses().getId(), classesVariaveis.getClasses().getName(), classesVariaveis.getClasses().getSimpleName());

        ClassesVariaveisDTO variaveisDTO = new ClassesVariaveisDTO();
        variaveisDTO.setStatus(Status.valueOf(classesVariaveis.getStatus()));
        variaveisDTO.setClassesDTO(dto);
        variaveisDTO.setVariavel(classesVariaveis.getVariavel());
        variaveisDTO.setHeader(classesVariaveis.getVariavel());
        variaveisDTO.setTipo(classesVariaveis.getTipo());
        variaveisDTO.setId(classesVariaveis.getId());
        return variaveisDTO;

    }
}
