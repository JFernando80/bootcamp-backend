package br.com.impacta.bootcamp.admin.specification;

import br.com.impacta.bootcamp.admin.model.PermissionType;
import br.com.impacta.bootcamp.admin.model.Permission;
import br.com.impacta.bootcamp.admin.model.*;
import br.com.impacta.bootcamp.commons.model.SearchCriteria;
import br.com.impacta.bootcamp.commons.util.Beans;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilPermissionTypeSpecification implements Specification<PerfilPermissionType> {

    private List<SearchCriteria> list;
    public PerfilPermissionTypeSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<PerfilPermissionType> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        Join<PerfilPermissionType, PermissionType> permissionType = root.join("permissionType", JoinType.LEFT);
        Join<PerfilPermissionType, Permission> permission = root.join("permission", JoinType.LEFT);
        List<Join> allJoins = new ArrayList<>();
        allJoins.add(permissionType);
        allJoins.add(permission);

        Beans.montarSpecification(predicates, list, root, builder, allJoins);

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
