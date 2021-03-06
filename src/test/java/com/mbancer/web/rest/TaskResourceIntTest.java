package com.mbancer.web.rest;

import com.google.common.collect.Sets;
import com.mbancer.Tasker0App;
import com.mbancer.domain.*;
import com.mbancer.repository.*;
import com.mbancer.security.SecurityUtils;
import com.mbancer.service.TaskService;
import com.mbancer.repository.search.TaskSearchRepository;
import com.mbancer.service.util.EntityGenerators;
import com.mbancer.web.rest.dto.TaskDTO;
import com.mbancer.web.rest.mapper.TaskMapper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TaskResource REST controller.
 *
 * @see TaskResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
public class TaskResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDateTime DEFAULT_CREATED = LocalDateTime.of(1, Month.FEBRUARY, 1, 1, 1);
    private static final LocalDateTime UPDATED_CREATED = LocalDateTime.now(ZoneId.systemDefault());

    private static final LocalDateTime DEFAULT_UPDATED = LocalDateTime.of(1, Month.FEBRUARY, 1, 1, 1);
    private static final LocalDateTime UPDATED_UPDATED = LocalDateTime.now(ZoneId.systemDefault());

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TaskMapper taskMapper;

    @Inject
    private TaskService taskService;

    @Inject
    private TaskSearchRepository taskSearchRepository;

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTaskMockMvc;

    private User user;

    private Task task;

    private Project project;

    private String mort = ":*";

    private Sprint sprint;

    private UserStory userStory;

    private UserStory updatedUserStory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaskResource taskResource = new TaskResource();
        ReflectionTestUtils.setField(taskResource, "taskService", taskService);
        ReflectionTestUtils.setField(taskResource, "taskMapper", taskMapper);
        this.restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        taskSearchRepository.deleteAll();

        user = userRepository.findOneByLogin("admin").get();
        project = projectRepository.saveAndFlush(EntityGenerators.generateProject(Collections.singleton(user), null));
        sprint = sprintRepository.saveAndFlush(EntityGenerators.generateSprint(project));
        userStory = userStoryRepository.saveAndFlush(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));

        updatedUserStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));

        task = new Task();
        task.setTitle(DEFAULT_TITLE);
        task.setDescription(DEFAULT_DESCRIPTION);
        task.setCreated(DEFAULT_CREATED);
        task.setUpdated(DEFAULT_UPDATED);
        task.setProject(project);
        task.setUserStory(userStory);
        task.setUser(user);
        task.setAssignee(user);
        task.setNumber(0L);

        SecurityUtils.setCurrentUserLogin("admin");
    }

    @Test
    @Transactional
    @Ignore
    public void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        task.setUserStory(userStory);

        // Create the Task
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);

        restTaskMockMvc.perform(post("/api/tasks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
                .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = tasks.get(tasks.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testTask.getUpdated()).isEqualTo(DEFAULT_UPDATED);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setTitle(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);

        restTaskMockMvc.perform(post("/api/tasks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
                .andExpect(status().isBadRequest());

        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the tasks
        restTaskMockMvc.perform(get("/api/tasks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
                .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
                .andExpect(jsonPath("$.[*].userId").value(hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.[*].assigneeId").value(hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.[*].userStoryId").value(hasItem(userStory.getId().intValue())));
    }

    @Test
    @Transactional
    public void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc.perform(get("/api/tasks/{id}", task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()))
            .andExpect(jsonPath("$.userId").value(user.getId().intValue()))
            .andExpect(jsonPath("$.assigneeId").value(user.getId().intValue()))
            .andExpect(jsonPath("$.userStoryId").value(userStory.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get("/api/tasks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Ignore
    public void updateTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = new Task();
        updatedTask.setId(task.getId());
        updatedTask.setTitle(UPDATED_TITLE);
        updatedTask.setDescription(UPDATED_DESCRIPTION);
        updatedTask.setCreated(UPDATED_CREATED);
        updatedTask.setUpdated(UPDATED_UPDATED);
        updatedTask.setUserStory(updatedUserStory);
        updatedTask.setProject(project);
        updatedTask.setNumber(2L);
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(updatedTask);

        restTaskMockMvc.perform(put("/api/tasks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
                .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeUpdate);
        Task testTask = tasks.get(tasks.size() - 1);
        assertThat(testTask.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testTask.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void deleteTask() throws Exception {
        // Initialize the database
        task.setUserStory(userStory);
        taskRepository.saveAndFlush(task);
        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Get the task
        restTaskMockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void shouldFindTasksByUserId() throws Exception {
        final User user = userRepository.findOneByLogin("admin").get();
        final User otherUser = userRepository.findOneByLogin("user").get();

        final Sprint sprint = sprintRepository.save(EntityGenerators.generateSprint(project));
        final UserStory userStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));
        final Task userTask0 = taskRepository.save(EntityGenerators.generateTask(user, project, userStory, user));
        final Task userTask1 = taskRepository.save(EntityGenerators.generateTask(user, project, userStory, user));
        final Task otherUserTask = taskRepository.save(EntityGenerators.generateTask(otherUser, project, userStory, user));


        restTaskMockMvc.perform(get("/api/tasks/byUser/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[*].id").value(hasItem(userTask0.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].title").value(hasItem(userTask0.getTitle())))
            .andExpect(jsonPath("$.content.[*].description").value(hasItem(userTask0.getDescription())))
            .andExpect(jsonPath("$.content.[*].created").value(hasItem(userTask0.getCreated().toString())))
            .andExpect(jsonPath("$.content.[*].userId").value(hasItem(userTask0.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].assigneeId").value(hasItem(userTask0.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].projectId").value(hasItem(userTask0.getProject().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].userStoryId").value(hasItem(userTask0.getUserStory().getId().intValue())))

            .andExpect(jsonPath("$.content.[*].id").value(hasItem(userTask1.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].title").value(hasItem(userTask1.getTitle())))
            .andExpect(jsonPath("$.content.[*].description").value(hasItem(userTask1.getDescription())))
            .andExpect(jsonPath("$.content.[*].created").value(hasItem(userTask1.getCreated().toString())))
            .andExpect(jsonPath("$.content.[*].userId").value(hasItem(userTask1.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].assigneeId").value(hasItem(userTask1.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].projectId").value(hasItem(userTask1.getProject().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].userStoryId").value(hasItem(userTask1.getUserStory().getId().intValue())))

            .andExpect(jsonPath("$.content.[*].id").value(not(hasItem(otherUserTask.getId().intValue()))));

    }

    @Test
    @Transactional
    public void shouldFindTasksByUserStoryId() throws Exception {
        final User user = userRepository.findOneByLogin("admin").get();

        final Sprint sprint = sprintRepository.save(EntityGenerators.generateSprint(project));
        final UserStory userStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));
        final UserStory otherUserStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));
        final Task userTask0 = taskRepository.save(EntityGenerators.generateTask(user, project, userStory, user));
        final Task userTask1 = taskRepository.save(EntityGenerators.generateTask(user, project, userStory, user));
        final Task otherUserTask = taskRepository.save(EntityGenerators.generateTask(user, project, otherUserStory, user));


        restTaskMockMvc.perform(get("/api/tasks/byUserStory/{id}", userStory.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[*].id").value(hasItem(userTask0.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].title").value(hasItem(userTask0.getTitle())))
            .andExpect(jsonPath("$.content.[*].description").value(hasItem(userTask0.getDescription())))
            .andExpect(jsonPath("$.content.[*].created").value(hasItem(userTask0.getCreated().toString())))
            .andExpect(jsonPath("$.content.[*].userId").value(hasItem(userTask0.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].assigneeId").value(hasItem(userTask0.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].projectId").value(hasItem(userTask0.getProject().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].userStoryId").value(hasItem(userTask0.getUserStory().getId().intValue())))

            .andExpect(jsonPath("$.content.[*].id").value(hasItem(userTask1.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].title").value(hasItem(userTask1.getTitle())))
            .andExpect(jsonPath("$.content.[*].description").value(hasItem(userTask1.getDescription())))
            .andExpect(jsonPath("$.content.[*].created").value(hasItem(userTask1.getCreated().toString())))
            .andExpect(jsonPath("$.content.[*].userId").value(hasItem(userTask1.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].assigneeId").value(hasItem(userTask1.getUser().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].projectId").value(hasItem(userTask1.getProject().getId().intValue())))
            .andExpect(jsonPath("$.content.[*].userStoryId").value(hasItem(userTask1.getUserStory().getId().intValue())))

            .andExpect(jsonPath("$.content.[*].userStoryId").value(not(hasItem(otherUserStory.getId().intValue()))));

    }
}
