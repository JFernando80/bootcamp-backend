package br.com.impacta.bootcamp.formacao.controller;

import br.com.impacta.bootcamp.formacao.dto.UserCourseDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.formacao.model.UserCourse;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.formacao.service.UserCourseService;
import br.com.impacta.bootcamp.admin.service.ClassesService;
import br.com.impacta.bootcamp.admin.service.ClassesVariaveisService;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.model.Content;
import br.com.impacta.bootcamp.commons.model.JsonResponse;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Monitorar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user_course")
@Slf4j
public class UserCourseController {

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesVariaveisService classesVariaveisService;

    @Autowired
    private Beans beans;

    @Monitorar
    @PostMapping(value = "")
    public JsonResponse save(@RequestBody UserCourseDTO dto,
                             @RequestAttribute(value = "content") Content content) {
        userCourseService.save(dto);
        return JsonResponse.ok("User Course salvo com sucesso");
    }

    @Monitorar
    @PutMapping(value = "/{id}")
    public JsonResponse update(@RequestBody UserCourseDTO dto,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("id") String id) {

        dto.setId(beans.isUUID(id));
        userCourseService.update(dto);
        return JsonResponse.ok("User Course atualizado com sucesso");
    }

    @Monitorar
    @DeleteMapping(value = "/{id}")
    public JsonResponse delete(@PathVariable("id") String id,
                               @RequestAttribute(value = "content") Content content) {
        userCourseService.delete(beans.isUUID(id));
        return JsonResponse.ok("User Course excluido com sucesso");
    }

    @Monitorar
    @PostMapping(value = "/filtro/{pagina}")
    public JsonResponse filtro(@RequestBody List<SearchCriteriaDTO> lista,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("pagina") int pagina) {
        return JsonResponse.ok(userCourseService.getAll(lista, pagina));
    }

    @Monitorar
    @GetMapping(value = "/consulta")
    public JsonResponse consulta(@RequestAttribute(value = "content") Content content) {

        Classes classes = classesService.findByName(UserCourse.class.getName());
        List<ClassesVariaveisDTO> campos = classesVariaveisService.findAllByClassesAndStatus(classes, content);;
        return JsonResponse.ok(campos);
    }
}
