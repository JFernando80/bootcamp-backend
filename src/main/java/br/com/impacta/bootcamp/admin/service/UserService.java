package br.com.impacta.bootcamp.admin.service;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina);

    void save(UserDTO dto);

    void update(UserDTO dto);

    User findByIdInterno(UUID id);

    void delete(UUID id);

    UserDTO montarDTO(User entity);

    User findByEmailInterno(String email);

    User validateUserAndPassword(String usuario, String senha);

    void criarUsuario(String token, UserDTO usuario);
}
