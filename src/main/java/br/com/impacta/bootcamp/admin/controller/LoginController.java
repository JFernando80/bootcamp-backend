package br.com.impacta.bootcamp.admin.controller;

import br.com.impacta.bootcamp.admin.dto.LoginDTO;
import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.service.LoginService;
import br.com.impacta.bootcamp.admin.service.TokenService;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.commons.model.JsonResponse;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.RSAUtil;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Beans beans;

    @PostMapping(value = "/login")
    public JsonResponse login(
            @RequestBody LoginDTO loginDTO,
            @RequestHeader("token") String token) {

        return JsonResponse.ok(loginService.getToken(token, loginDTO));
    }

    @PostMapping(value = "/token")
    public  JsonResponse token(
            @RequestBody TokenDTO token,
            @RequestHeader("token") String securityId) {

        return JsonResponse.ok(loginService.getPorToken(token));
    }

    @PostMapping(value = "/refresh_token")
    public  JsonResponse refreshToken(@RequestBody TokenDTO token, @RequestHeader("token") String securityId) {
        beans.isLong(securityId);

        return JsonResponse.ok(loginService.getPorRefreshToken(token, securityId));
    }



}
