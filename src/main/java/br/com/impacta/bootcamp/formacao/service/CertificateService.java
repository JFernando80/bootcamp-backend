package br.com.impacta.bootcamp.formacao.service;

import br.com.impacta.bootcamp.formacao.dto.CertificateDTO;
import br.com.impacta.bootcamp.formacao.model.Certificate;

import java.util.UUID;

public interface CertificateService {

    void save(CertificateDTO dto);

    Certificate findByIdInterno(UUID id);

    Certificate findByToken(UUID id);

    CertificateDTO montarDTO(Certificate entity);
}
