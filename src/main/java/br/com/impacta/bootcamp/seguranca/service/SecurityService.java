package br.com.impacta.bootcamp.seguranca.service;


import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;

public interface SecurityService {

    SecurityDTO findByPublicKey(String publicKey);

    SecurityDTO get(String screen);

    void delete(SecurityDTO segurancaDTO);

    SecurityDTO findById(Long id);
}
