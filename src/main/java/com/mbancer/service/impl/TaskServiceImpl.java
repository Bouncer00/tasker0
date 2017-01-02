package com.mbancer.service.impl;

import com.mbancer.domain.*;
import com.mbancer.repository.*;
import com.mbancer.security.SecurityUtils;
import com.mbancer.service.TaskService;
import com.mbancer.repository.search.TaskSearchRepository;
import com.mbancer.service.UserStoryService;
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
    private UserStoryRepository userStoryRepository;

    @Inject
    private TaskMapper taskMapper;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TaskSearchRepository taskSearchRepository;

    @Inject
    private UserStoryService userStoryService;

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save
     * @return the persisted entity
     */
    public TaskDTO save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.taskDTOToTask(taskDTO);
        task.setNumber(getNextTaskNumber(taskDTO.getUserStoryId()));
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        UserStory userStory = userStoryRepository.findOne(taskDTO.getUserStoryId());
        userStory.getTasks().add(task);
        task.setUser(user);
        user.getTasks().add(task);
        task = taskRepository.save(task);
        TaskDTO result = taskMapper.taskToTaskDTO(task);
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
    public void addCommentToTask(long taskId, long commentId) {
        final Task task = taskRepository.findOne(taskId);
        final Comment comment = commentRepository.findOne(commentId);
        task.getComments().add(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> getByUser(Long userId, Pageable pageable) {
        final Page<Task> tasks = taskRepository.findAllByUserId(userId, pageable);
        return tasks.map(taskMapper::taskToTaskDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> getByUserStory(Long userStoryId, Pageable pageable) {
        final Page<Task> tasks = taskRepository.findAllByUserStoryIdOrderByNumber(userStoryId, pageable);
        return tasks.map(taskMapper::taskToTaskDTO);
    }

    @Override
    public void moveTaskByIdUp(final Long taskId){
        final Task task = taskRepository.findOne(taskId);
        final Task previousTask = getPreviousTask(task);
        long taskNumber = task.getNumber();
        task.setNumber(previousTask.getNumber());
        previousTask.setNumber(taskNumber);
    }


    @Override
    public void moveTaskByIdDown(final Long taskId){
        final Task task = taskRepository.findOne(taskId);
        final Task nextTask = getNextTask(task);
        long taskNumber = task.getNumber();
        task.setNumber(nextTask.getNumber());
        nextTask.setNumber(taskNumber);
    }


    public void assignTaskToCurrentUser(final Long taskId){
        final Task task = taskRepository.findOne(taskId);
        final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        task.setAssignee(user);
        taskRepository.save(task);
    }

    private Task getNextTask(Task task) {
        if(task.getNumber() == task.getUserStory().getTasks().size() - 1){
            UserStory userStory = userStoryRepository.findOne(task.getUserStory().getId());
            UserStory nextUserStory = userStoryService.getNextUserStory(userStory);
            userStory.getTasks().remove(task);
            for(Task usTask: nextUserStory.getTasks()){
                usTask.setNumber(usTask.getNumber() + 1);
            }
            task.setUserStory(nextUserStory);
            task.setNumber(0L);
            nextUserStory.getTasks().add(task);
            return task;
        }
        UserStory userStory = userStoryRepository.findOne(task.getUserStory().getId());
        return taskRepository.findOneByUserStoryIdAndNumber(userStory.getId(), task.getNumber() + 1);
    }

    private Task getPreviousTask(Task task) {
        UserStory userStory = userStoryRepository.findOne(task.getUserStory().getId());
        if(task.getNumber() == 0){
            UserStory previousUserStory = userStoryService.getPreviousUserStory(userStory);
            task.setUserStory(previousUserStory);
            task.setNumber((long)previousUserStory.getTasks().size());
            userStory.getTasks().remove(task);
            for(Task usTask: userStory.getTasks()){
                usTask.setNumber(usTask.getNumber() - 1);
            }
            previousUserStory.getTasks().add(task);
            return task;
        }

        return taskRepository.findOneByUserStoryIdAndNumber(userStory.getId(), task.getNumber() - 1);
    }

    private Long getNextTaskNumber(Long userStoryId){
        final UserStory userStory = userStoryRepository.findOne(userStoryId);
        return (long) userStory.getTasks().size();
    }
}
