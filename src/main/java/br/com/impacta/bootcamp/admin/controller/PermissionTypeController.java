package br.com.impacta.bootcamp.admin.controller;

import br.com.impacta.bootcamp.admin.dto.PermissionTypeDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.service.PermissionTypeService;
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
@RequestMapping(value = "/permission_type")
@Slf4j
public class PermissionTypeController {

    @Autowired
    private PermissionTypeService permissionTypeService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesVariaveisService classesVariaveisService;

    @Autowired
    private Beans beans;

    @Monitorar
    @PostMapping(value = "")
    public JsonResponse save(@RequestBody PermissionTypeDTO dto,
                             @RequestAttribute(value = "content") Content content) {
        permissionTypeService.save(dto);
        return JsonResponse.ok("Permission Type salvo com sucesso");
    }

    @Monitorar
    @PutMapping(value = "/{id}")
    public JsonResponse update(@RequestBody PermissionTypeDTO dto,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("id") String id) {

        dto.setId(beans.isLong(id));
        permissionTypeService.update(dto);
        return JsonResponse.ok("Permission Type atualizado com sucesso");
    }

    @Monitorar
    @DeleteMapping(value = "/{id}")
    public JsonResponse delete(@PathVariable("id") String id,
                               @RequestAttribute(value = "content") Content content) {
        permissionTypeService.delete(beans.isLong(id));
        return JsonResponse.ok("Permission Type excluido com sucesso");
    }

    @Monitorar
    @PostMapping(value = "/filtro/{pagina}")
    public JsonResponse filtro(@RequestBody List<SearchCriteriaDTO> lista,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("pagina") int pagina) {
        return JsonResponse.ok(permissionTypeService.getAll(lista, pagina));
    }

    @Monitorar
    @GetMapping(value = "/consulta")
    public JsonResponse consulta(@RequestAttribute(value = "content") Content content) {

        Classes classes = classesService.findByName(PermissionType.class.getName());
        List<ClassesVariaveisDTO> campos = classesVariaveisService.findAllByClassesAndStatus(classes, content);;
        return JsonResponse.ok(campos);
    }
}
