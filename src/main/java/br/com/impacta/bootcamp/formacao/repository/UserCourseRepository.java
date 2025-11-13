package br.com.impacta.bootcamp.formacao.repository;

import br.com.impacta.bootcamp.formacao.model.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface UserCourseRepository extends
        JpaRepository<UserCourse, UUID>,
        JpaSpecificationExecutor<UserCourse> {

}
