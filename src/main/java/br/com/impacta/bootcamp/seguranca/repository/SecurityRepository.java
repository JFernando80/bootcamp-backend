package br.com.impacta.bootcamp.seguranca.repository;

import br.com.impacta.bootcamp.seguranca.model.Security;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SecurityRepository extends CrudRepository<Security, Long> {

    Security findByPublicKey(String publicKey);

    List<Security> findTop100ByExpirationDateLessThan(Date expiredDates);
}
