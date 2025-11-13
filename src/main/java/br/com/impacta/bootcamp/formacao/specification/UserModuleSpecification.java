package br.com.impacta.bootcamp.formacao.specification;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.model.Module;
import br.com.impacta.bootcamp.formacao.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserModuleSpecification implements Specification<UserModule> {

    private List<SearchCriteria> list;
    public UserModuleSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<UserModule> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<UserModule, User> user = root.join("user", JoinType.LEFT);
        Join<UserModule, Module> module = root.join("module", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(user);
        allJoins.add(module);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
