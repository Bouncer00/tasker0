package com.mbancer.service;

import com.mbancer.domain.Task;
import com.mbancer.web.rest.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Task.
 */
public interface TaskService {

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save
     * @return the persisted entity
     */
    TaskDTO save(TaskDTO taskDTO);

    /**
     *  Get all the tasks.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Task> findAll(Pageable pageable);

    /**
     *  Get the "id" task.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TaskDTO findOne(Long id);

    /**
     *  Delete the "id" task.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    void addCommentToTask(long taskId, long commentId);

    Page<TaskDTO> getByUser(Long userId, Pageable pageable);

    Page<TaskDTO> getByUserStory(Long userStoryId, Pageable pageable);

    void moveTaskByIdUp(final Long taskId);

    void moveTaskByIdDown(final Long taskId);

    void assignTaskToCurrentUser(final Long taskId);

    List<TaskDTO> getAssignedToCurrentUser();
}
