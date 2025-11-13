package br.com.impacta.bootcamp.admin.controller;

import br.com.impacta.bootcamp.admin.dto.PerfilPermissionTypeDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.service.PerfilPermissionTypeService;
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
@RequestMapping(value = "/perfil_permission_type")
@Slf4j
public class PerfilPermissionTypeController {

    @Autowired
    private PerfilPermissionTypeService perfilPermissionTypeService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesVariaveisService classesVariaveisService;

    @Autowired
    private Beans beans;

    @Monitorar
    @PostMapping(value = "")
    public JsonResponse save(@RequestBody PerfilPermissionTypeDTO dto,
                             @RequestAttribute(value = "content") Content content) {
        perfilPermissionTypeService.save(dto);
        return JsonResponse.ok("Perfil Permission Type salvo com sucesso");
    }

    @Monitorar
    @PutMapping(value = "/{id}")
    public JsonResponse update(@RequestBody PerfilPermissionTypeDTO dto,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("id") String id) {

        dto.setId(beans.isLong(id));
        perfilPermissionTypeService.update(dto);
        return JsonResponse.ok("Perfil Permission Type atualizado com sucesso");
    }

    @Monitorar
    @DeleteMapping(value = "/{id}")
    public JsonResponse delete(@PathVariable("id") String id,
                               @RequestAttribute(value = "content") Content content) {
        perfilPermissionTypeService.delete(beans.isLong(id));
        return JsonResponse.ok("Perfil Permission Type excluido com sucesso");
    }

    @Monitorar
    @PostMapping(value = "/filtro/{pagina}")
    public JsonResponse filtro(@RequestBody List<SearchCriteriaDTO> lista,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("pagina") int pagina) {
        return JsonResponse.ok(perfilPermissionTypeService.getAll(lista, pagina));
    }

    @Monitorar
    @GetMapping(value = "/consulta")
    public JsonResponse consulta(@RequestAttribute(value = "content") Content content) {

        Classes classes = classesService.findByName(PerfilPermissionType.class.getName());
        List<ClassesVariaveisDTO> campos = classesVariaveisService.findAllByClassesAndStatus(classes, content);;
        return JsonResponse.ok(campos);
    }
}
