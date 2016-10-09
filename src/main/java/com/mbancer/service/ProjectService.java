package com.mbancer.service;

import com.mbancer.domain.Project;
import com.mbancer.web.rest.dto.ProjectDTO;
import com.mbancer.web.rest.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Project.
 */
public interface ProjectService {

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save
     * @return the persisted entity
     */
    ProjectDTO save(ProjectDTO projectDTO);

    /**
     *  Get all the projects.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Project> findAll(Pageable pageable);

    /**
     *  Get the "id" project.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProjectDTO findOne(Long id);

    /**
     *  Delete the "id" project.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the project corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Project> search(String query, Pageable pageable);

    void addTaskToProject(final long projectId, final long taskId);

    Page<ProjectDTO> getByUser(Long userId, Pageable pageable);

    Page<ProjectDTO> getByCurrentUser(Pageable pageable);

    Page<UserDTO> getMembers(Long projectId, Pageable pageable);
}
