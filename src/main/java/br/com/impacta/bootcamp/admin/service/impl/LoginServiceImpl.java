package br.com.impacta.bootcamp.admin.service.impl;

import br.com.impacta.bootcamp.admin.dto.LoginDTO;
import br.com.impacta.bootcamp.admin.dto.TokenDTO;
import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.dto.UsuarioLogadoDTO;
import br.com.impacta.bootcamp.admin.model.PerfilPermissionType;
import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.UserPermissionType;
import br.com.impacta.bootcamp.admin.service.*;
import br.com.impacta.bootcamp.commons.dto.PermissionsDTO;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.JwtTokenUtil;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserPermissionTypeService userPermissionTypeService;
    @Autowired
    private PerfilPermissionTypeService perfilPermissionTypeService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private Beans beans;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final boolean LOGIN_TRUE = true;
    private static final boolean LOGIN_FALSE = false;

    private static final String PATH_BASE_64 = "data:image/png;base64, ";

    @Override
    public UsuarioLogadoDTO getPorToken(TokenDTO token) {
        Token token1 = tokenService.getTokenByToken(token.getToken());

        return montarUsuarioLogadoDTO(token1.getUser(), LOGIN_FALSE);
    }

    @Override
    public UsuarioLogadoDTO getPorRefreshToken(TokenDTO token, String securityId) {
        Token token1 = tokenService.getRefreshTokenByUser(token.getRefreshToken(), Long.parseLong(securityId));

        return montarUsuarioLogadoDTO(token1.getUser(), LOGIN_FALSE);
    }

    @Override
    public String getToken(String token, LoginDTO loginDTO) {
        beans.isLong(token);
        SecurityDTO segurancaDTO = securityService.findById(Long.parseLong(token));
        securityService.delete(segurancaDTO);

        String salt = segurancaDTO.getPublicKey();
        String decrypt = beans.decrypt(salt, loginDTO.getLogin());
        String usuario = decrypt.substring(0, decrypt.indexOf("}*{"));
        String senha = decrypt.substring(decrypt.indexOf("}*{")+3, decrypt.length());

        User user = userService.validateUserAndPassword(usuario, senha);

        UsuarioLogadoDTO usuarioLogadoDTO = montarUsuarioLogadoDTO(user, LOGIN_TRUE);

        return jwtTokenUtil.generateToken(usuarioLogadoDTO);
    }

    @Override
    public UsuarioLogadoDTO montarUsuarioLogadoDTO(User user, boolean login) {
        UsuarioLogadoDTO usuarioLogadoDTO = new UsuarioLogadoDTO();

        List<UserPermissionType> userPermissionTypes = userPermissionTypeService.findAllByUserInterno(user);
        for (UserPermissionType type : userPermissionTypes) {
            List<PerfilPermissionType> list = perfilPermissionTypeService.listFromTypePermissionInterno(type.getPermissionType());
            list.forEach(perfilPermissionTypeDTO ->
                    usuarioLogadoDTO.getPermissionsDTOS().add(
                            montarPermissionsDTO(perfilPermissionTypeDTO.getPermission().getSerial())));
        }

        TokenDTO tokenDTO = tokenService.getTokenByUser(user, login);
        usuarioLogadoDTO.setUserDTO(removePasswordAndSalt(user));
        usuarioLogadoDTO.setTokenDTO(tokenDTO);
        return usuarioLogadoDTO;
    }

    private PermissionsDTO montarPermissionsDTO(Long serial) {
        return new PermissionsDTO(serial);
    }

    private UserDTO removePasswordAndSalt(User user) {
        if (user != null) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            return dto;
        }
        return null;
    }
}
