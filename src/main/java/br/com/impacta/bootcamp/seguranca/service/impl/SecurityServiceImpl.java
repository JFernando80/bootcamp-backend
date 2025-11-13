package br.com.impacta.bootcamp.seguranca.service.impl;

import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.seguranca.dto.SecurityDTO;
import br.com.impacta.bootcamp.seguranca.model.Security;
import br.com.impacta.bootcamp.seguranca.repository.SecurityRepository;
import br.com.impacta.bootcamp.seguranca.service.SecurityService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private Beans beans;

    @Override
    public SecurityDTO findByPublicKey(String publicKey) {
        Security security = securityRepository.findByPublicKey(publicKey);
        return montarDTO(security);
    }

    private SecurityDTO montarDTO(Security entity) {
        SecurityDTO dto = new SecurityDTO();
        beans.updateObjectos(dto, entity);
        return dto;
    }

    @Override
    public SecurityDTO get(String screen) {
        Security security = Security.buildSecurity(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), screen, DateTime.now().plusMinutes(15).toDate());

        securityRepository.save(security);
        return montarDTO(security);
    }

    @Override
    public void delete(SecurityDTO segurancaDTO) {
        Security security = securityRepository.findById(segurancaDTO.getId())
                .orElseThrow(() -> new BusinessRuleException("security.not.found", String.valueOf(segurancaDTO.getId())));

        securityRepository.delete(security);
    }

    @Override
    public SecurityDTO findById(Long id) {
        Security security = securityRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("security.not.found", String.valueOf(id)));
        return montarDTO(security);
    }
}
