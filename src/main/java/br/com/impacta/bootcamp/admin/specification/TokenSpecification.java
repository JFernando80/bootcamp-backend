package br.com.impacta.bootcamp.admin.specification;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.admin.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class TokenSpecification implements Specification<Token> {

    private List<SearchCriteria> list;
    public TokenSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Token> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<Token, User> user = root.join("user", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(user);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
