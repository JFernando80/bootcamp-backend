package br.com.impacta.bootcamp.admin.controller;

import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.dto.ClassesVariaveisDTO;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.Classes;
import br.com.impacta.bootcamp.admin.service.TokenService;
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
@RequestMapping(value = "/token")
@Slf4j
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassesVariaveisService classesVariaveisService;

    @Autowired
    private Beans beans;

    @Monitorar
    @PostMapping(value = "")
    public JsonResponse save(@RequestBody TokenDTO dto,
                             @RequestAttribute(value = "content") Content content) {
        tokenService.save(dto);
        return JsonResponse.ok("Token salvo com sucesso");
    }

    @Monitorar
    @PutMapping(value = "/{id}")
    public JsonResponse update(@RequestBody TokenDTO dto,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("id") String id) {

        dto.setId(beans.isLong(id));
        tokenService.update(dto);
        return JsonResponse.ok("Token atualizado com sucesso");
    }

    @Monitorar
    @DeleteMapping(value = "/{id}")
    public JsonResponse delete(@PathVariable("id") String id,
                               @RequestAttribute(value = "content") Content content) {
        tokenService.delete(beans.isLong(id));
        return JsonResponse.ok("Token excluido com sucesso");
    }

    @Monitorar
    @PostMapping(value = "/filtro/{pagina}")
    public JsonResponse filtro(@RequestBody List<SearchCriteriaDTO> lista,
                               @RequestAttribute(value = "content") Content content,
                               @PathVariable("pagina") int pagina) {
        return JsonResponse.ok(tokenService.getAll(lista, pagina));
    }

    @Monitorar
    @GetMapping(value = "/consulta")
    public JsonResponse consulta(@RequestAttribute(value = "content") Content content) {

        Classes classes = classesService.findByName(Token.class.getName());
        List<ClassesVariaveisDTO> campos = classesVariaveisService.findAllByClassesAndStatus(classes, content);;
        return JsonResponse.ok(campos);
    }
}
