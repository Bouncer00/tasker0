package com.mbancer.repository;

import com.mbancer.domain.Project;

import com.mbancer.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select distinct project from Project project left join fetch project.users")
    List<Project> findAllWithEagerRelationships();

    @Query("select project from Project project left join fetch project.users where project.id =:id")
    Project findOneWithEagerRelationships(@Param("id") Long id);

    Page<Project> findAllByUsersIdIn(List<Long> longs, Pageable pageable);
}
