package br.com.impacta.bootcamp.admin.specification;

import br.com.impacta.bootcamp.commons.enums.Status;
import br.com.impacta.bootcamp.admin.model.PermissionGroup;
import br.com.impacta.bootcamp.admin.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionTypeSpecification implements Specification<PermissionType> {

    private List<SearchCriteria> list;
    public PermissionTypeSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<PermissionType> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<PermissionType, Status> status = root.join("status", JoinType.LEFT);
        Join<PermissionType, PermissionGroup> permissionGroup = root.join("permissionGroup", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(status);
        allJoins.add(permissionGroup);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
