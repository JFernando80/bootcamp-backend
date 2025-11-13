package br.com.impacta.bootcamp.admin.repository;

import br.com.impacta.bootcamp.admin.model.Token;
import br.com.impacta.bootcamp.admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TokenRepository extends
        JpaRepository<Token, Long>,
        JpaSpecificationExecutor<Token> {

    Token findTop1ByUser(User user);

    Token findByToken(String token);

    Token findByRefreshToken(String rtoken);
}
