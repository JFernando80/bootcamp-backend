package br.com.impacta.bootcamp.admin.service.impl;


import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.admin.dto.UserDTO;
import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.repository.UserRepository;
import br.com.impacta.bootcamp.admin.service.UserService;
import br.com.impacta.bootcamp.admin.specification.UserSpecification;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.AES;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    @Override
    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        UserSpecification msCategoria = new UserSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<User> msPage = userRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(UserDTO dto) {
        isRepetido(dto);
        userRepository.save(montarEntity(dto));
    }

    @Override
    public void update(UserDTO dto) {
        updateInterno(dto);
    }

    @Override
    public User findByIdInterno(UUID id) {
        return userRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("User não encontrado"));
    }

    @Override
    public void delete(UUID id) {
        User entity = findByIdInterno(id);
        userRepository.delete(entity);
    }

    @Override
    public UserDTO montarDTO(User entity) {
        UserDTO dto = new UserDTO();
        beans.updateObjectos(dto, entity);
        dto.setCreatedAtS(beans.converterDateToString(entity.getCreatedAt()));
        dto.setUpdatedAtS(beans.converterDateToString(entity.getUpdatedAt()));
        dto.setDeletedAtS(beans.converterDateToString(entity.getDeletedAt()));

        return dto;
    }

    @Override
    public User findByEmailInterno(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void criarUsuario(String token, UserDTO usuario) {
        SecurityDTO segurancaDTO = securityService.findById(Long.parseLong(token));
        securityService.delete(segurancaDTO);

        String password = beans.decrypt(segurancaDTO.getPublicKey(), usuario.getPasswordHash());

        password = beans.toHexString(password+segurancaDTO.getPublicKey());
        usuario.setSalt(segurancaDTO.getPublicKey());
        usuario.setPasswordHash(password);
        usuario.setCreatedAtS(beans.converterDateToString(new Date()));

        save(usuario);
    }

    @Override
    public User validateUserAndPassword(String usuario, String senha) {
        User user = findByEmailInterno(usuario);

        String hash = senha + user.getSalt();
        hash = beans.toHexString(hash);
        if (Objects.equals(user.getPasswordHash(), hash)) {
            return user;
        }

        throw new BusinessRuleException("Usuario ou senha invalidos");
    }

    private User montarEntity(UserDTO dto) {
        User entity = new User();
        beans.updateObjectos(entity, dto);
        Date createdAt = beans.converterStringToDate(dto.getCreatedAtS());
        entity.setCreatedAt(createdAt);

        Date updatedAt = beans.converterStringToDate(dto.getUpdatedAtS());
        entity.setUpdatedAt(updatedAt);

        Date deletedAt = beans.converterStringToDate(dto.getDeletedAtS());
        entity.setDeletedAt(deletedAt);

        return entity ;
    }

    private void updateInterno(UserDTO dto) {
        isRepetido(dto);
        User entity = findByIdInterno(dto.getId());
        User updated = montarEntity(dto);

        try {
            beans.updateObjectos(entity, updated);
            userRepository.save(entity);
        } catch (BusinessRuleException e) {
             throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("erro desconhecido: "+e.getMessage());
        }
    }


    private void isValido(UserDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(UserDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("email");
        criteria.setValue(dto.getEmail());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    UserDTO dto1 = (UserDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                        ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este user cadastrado");
            }
        }
    }
}
