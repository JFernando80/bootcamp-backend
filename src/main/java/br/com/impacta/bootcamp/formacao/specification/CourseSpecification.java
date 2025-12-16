package br.com.impacta.bootcamp.formacao.specification;

import br.com.impacta.bootcamp.admin.model.User;
import br.com.impacta.bootcamp.formacao.enums.StatusCourse;
import br.com.impacta.bootcamp.formacao.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseSpecification implements Specification<Course> {

    private List<SearchCriteria> list;
    public CourseSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<Course, User> ownerUser = root.join("ownerUser", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(ownerUser);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
