package br.com.impacta.bootcamp.seguranca.controller;

import br.com.impacta.bootcamp.commons.model.JsonResponse;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping(value = "/{screen}")
    public JsonResponse getById(@PathVariable("screen") String screen) {
        SecurityDTO dto = securityService.get(screen);
        return JsonResponse.ok(dto);
    }

}
