package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.formacao.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends
        JpaRepository<Certificate, UUID>,
        JpaSpecificationExecutor<Certificate> {

    Certificate findByToken(UUID token);
}
