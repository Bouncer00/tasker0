package com.mbancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mbancer.domain.Task;
import com.mbancer.service.TaskService;
import com.mbancer.web.rest.util.HeaderUtil;
import com.mbancer.web.rest.util.PaginationUtil;
import com.mbancer.web.rest.dto.TaskDTO;
import com.mbancer.web.rest.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    @Inject
    private TaskService taskService;

    @Inject
    private TaskMapper taskMapper;

    /**
     * POST  /tasks : Create a new task.
     *
     * @param taskDTO the taskDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new taskDTO, or with status 400 (Bad Request) if the task has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) throws URISyntaxException {
        log.debug("REST request to save Task : {}", taskDTO);
        if (taskDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("task", "idexists", "A new task cannot already have an ID")).body(null);
        }
        TaskDTO result = taskService.save(taskDTO);
        return ResponseEntity.created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("task", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tasks : Updates an existing task.
     *
     * @param taskDTO the taskDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated taskDTO,
     * or with status 400 (Bad Request) if the taskDTO is not valid,
     * or with status 500 (Internal Server Error) if the taskDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskDTO taskDTO) throws URISyntaxException {
        log.debug("REST request to update Task : {}", taskDTO);
        if (taskDTO.getId() == null) {
            return createTask(taskDTO);
        }
        TaskDTO result = taskService.save(taskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("task", taskDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tasks : get all the tasks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tasks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<List<TaskDTO>> getAllTasks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Tasks");
        Page<Task> page = taskService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tasks");
        return new ResponseEntity<>(taskMapper.tasksToTaskDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /tasks/:id : get the "id" task.
     *
     * @param id the id of the taskDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the taskDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tasks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        TaskDTO taskDTO = taskService.findOne(id);
        return Optional.ofNullable(taskDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tasks/:id : delete the "id" task.
     *
     * @param id the id of the taskDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tasks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);
        taskService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("task", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tasks?query=:query : search for the task corresponding
     * to the query.
     *
     * @param query the query of the task search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tasks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Tasks for query {}", query);
        Page<Task> page = taskService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tasks");
        return new ResponseEntity<>(taskMapper.tasksToTaskDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/{taskId}/addComment/{commentId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> addCommentToTask(@PathVariable long taskId, @PathVariable long commentId){
        log.debug("REST request to add comment : {} to Task : {}", commentId, taskId);
        taskService.addCommentToTask(taskId, commentId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/tasks/byUser/{userId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TaskDTO>> tasksByUser(@PathVariable("userId") Long userId, Pageable pageable){
        log.debug("REST request to get tasks by User : {}", userId);
        return ResponseEntity.ok(taskService.getByUser(userId, pageable));
    }

    @RequestMapping(value = "tasks/byUserStory/{userStoryId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TaskDTO>> tasksByUserStory(@PathVariable("userStoryId") Long userStoryId, Pageable pageable){
        log.debug("REST request to get tasks by UserStory : {}", userStoryId);
        return ResponseEntity.ok(taskService.getByUserStory(userStoryId, pageable));
    }

    @RequestMapping(value = "tasks/assignToCurrentUser/{taskId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignTaskToCurrentUser(@PathVariable("taskId") Long taskId){
        log.debug("REST request to assign task to current user");
        taskService.assignTaskToCurrentUser(taskId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "tasks/moveUp/{taskId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> moveTaskUp(@PathVariable("taskId") Long taskId){
        log.debug("REST request to assign task to current user");
        taskService.moveTaskByIdUp(taskId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "tasks/moveDown/{taskId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> moveTaskDown(@PathVariable("taskId") Long taskId){
        log.debug("REST request to assign task to current user");
        taskService.moveTaskByIdDown(taskId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "tasks/assignedToCurrentUser",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDTO>> getAssignedToCurrentUser(){
        log.debug("REST request to get tasks assigned to current user");
        List<TaskDTO> taskDTOs = taskService.getAssignedToCurrentUser();
        return ResponseEntity.ok(taskDTOs);
    }

}
