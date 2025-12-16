package br.com.impacta.bootcamp.formacao.controller;

import br.com.impacta.bootcamp.commons.model.JsonResponse;
import br.com.impacta.bootcamp.commons.util.Monitorar;
import br.com.impacta.bootcamp.commons.util.EnumUtils;
import br.com.impacta.bootcamp.formacao.enums.StatusCourse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/formacao_enums")
@Slf4j
public class FormacaoEnumsController {

    @Monitorar
    @GetMapping(value = "/status_course/all")
    public JsonResponse getStatusCourseAll() {
        return JsonResponse.ok(EnumUtils.allStatus(StatusCourse.values()));
    }

}
