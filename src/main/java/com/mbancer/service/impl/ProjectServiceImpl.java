package com.mbancer.service.impl;

import com.mbancer.domain.Task;
import com.mbancer.domain.User;
import com.mbancer.repository.TaskRepository;
import com.mbancer.repository.UserRepository;
import com.mbancer.security.SecurityUtils;
import com.mbancer.service.ProjectService;
import com.mbancer.domain.Project;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.search.ProjectSearchRepository;
import com.mbancer.web.rest.dto.ProjectDTO;
import com.mbancer.web.rest.dto.UserDTO;
import com.mbancer.web.rest.mapper.ProjectMapper;
import com.mbancer.web.rest.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Project.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService{

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ProjectSearchRepository projectSearchRepository;

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save
     * @return the persisted entity
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.projectDTOToProject(projectDTO);
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        project.getUsers().add(user);
        project = projectRepository.save(project);
        ProjectDTO result = projectMapper.projectToProjectDTO(project);
        projectSearchRepository.save(project);
        return result;
    }

    /**
     *  Get all the projects.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Project> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        Page<Project> result = projectRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one project by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ProjectDTO findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        Project project = projectRepository.findOneWithEagerRelationships(id);
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);
        return projectDTO;
    }

    /**
     *  Delete the  project by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        projectRepository.delete(id);
        projectSearchRepository.delete(id);
    }

    /**
     * Search for the project corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Project> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Projects for query {}", query);
        return projectSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Transactional
    public void addTaskToProject(final long projectId, final long taskId){
        final Project project = projectRepository.findOne(projectId);
        final Task task = taskRepository.findOne(taskId);
        project.getTasks().add(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getByUser(Long userId, Pageable pageable) {
        final Page<Project> projects = projectRepository.findAllByUsersIdIn(Collections.singletonList(userId), pageable);
        return projects.map(projectMapper::projectToProjectDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getByCurrentUser(Pageable pageable){
        final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        final Page<Project> projects = projectRepository.findAllByUsersIdIn(Collections.singletonList(user.getId()), pageable);
        return projects.map(projectMapper::projectToProjectDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getMembers(Long projectId, Pageable pageable) {
        Page<User> members = userRepository.findAllByProjectsIdIn(Collections.singletonList(projectId), pageable);
        return members.map(userMapper::userToUserDTO);
    }
}
