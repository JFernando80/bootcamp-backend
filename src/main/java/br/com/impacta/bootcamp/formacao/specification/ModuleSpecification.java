package br.com.impacta.bootcamp.formacao.specification;

import br.com.impacta.bootcamp.formacao.model.Course;
import br.com.impacta.bootcamp.formacao.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.formacao.model.Module;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModuleSpecification implements Specification<Module> {

    private List<SearchCriteria> list;
    public ModuleSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Module> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<Module, Course> course = root.join("course", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(course);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
