package br.com.impacta.bootcamp.formacao.controller;

import br.com.impacta.bootcamp.commons.model.JsonResponse;
import br.com.impacta.bootcamp.formacao.service.CertificateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/certificate")
@Slf4j
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping(value = "")
    public JsonResponse getCertificado(
            //@RequestAttribute(value = "content") Content content,
            @RequestParam(value = "certificate_token") String token) {

        return JsonResponse.ok(certificateService.gerarCertificado(null, token ));
    }
}
