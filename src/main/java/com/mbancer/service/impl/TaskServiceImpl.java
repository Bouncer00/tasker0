package com.mbancer.service.impl;

import com.mbancer.domain.Comment;
import com.mbancer.domain.Project;
import com.mbancer.repository.CommentRepository;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.service.TaskService;
import com.mbancer.domain.Task;
import com.mbancer.repository.TaskRepository;
import com.mbancer.repository.search.TaskSearchRepository;
import com.mbancer.web.rest.dto.TaskDTO;
import com.mbancer.web.rest.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Task.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService{

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TaskMapper taskMapper;

    @Inject
    private TaskSearchRepository taskSearchRepository;

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save
     * @return the persisted entity
     */
    public TaskDTO save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.taskDTOToTask(taskDTO);
        task.setNumber(getNextTaskNumber(taskDTO.getProjectId()));
        task = taskRepository.save(task);
        TaskDTO result = taskMapper.taskToTaskDTO(task);
        taskSearchRepository.save(task);
        return result;
    }

    /**
     *  Get all the tasks.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        Page<Task> result = taskRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one task by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TaskDTO findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        Task task = taskRepository.findOne(id);
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
        return taskDTO;
    }

    /**
     *  Delete the  task by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.delete(id);
        taskSearchRepository.delete(id);
    }

    /**
     * Search for the task corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Task> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tasks for query {}", query);
        return taskSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    @Transactional
    public void addCommentToTask(long taskId, long commentId) {
        final Task task = taskRepository.findOne(taskId);
        final Comment comment = commentRepository.findOne(commentId);
        task.getComments().add(comment);
    }

    private Long getNextTaskNumber(Long projectId){
        final Project project = projectRepository.findOne(projectId);
        return (long) project.getTasks().size();
    }
}
