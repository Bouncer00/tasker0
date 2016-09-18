package com.mbancer.web.rest;

import com.google.common.collect.Sets;
import com.mbancer.Tasker0App;
import com.mbancer.domain.Project;
import com.mbancer.domain.Sprint;
import com.mbancer.domain.User;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.SprintRepository;
import com.mbancer.repository.UserRepository;
import com.mbancer.repository.UserStoryRepository;
import com.mbancer.repository.search.SprintSearchRepository;
import com.mbancer.service.SprintService;
import com.mbancer.service.util.EntityGenerators;
import com.mbancer.web.rest.dto.SprintDTO;
import com.mbancer.web.rest.mapper.SprintMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
public class SprintResourceIntTest {

    private static final long DEFAULT_NUMBER = 1L;
    private static final String DEFAULT_NAME = "AAAA";
    private static final LocalDate DEFAULT_START = LocalDate.now().minus(3, ChronoUnit.WEEKS);
    private static final LocalDate DEFAULT_END = LocalDate.now().minus(1, ChronoUnit.WEEKS);

    private static final long UPDATED_NUMBER = 2L;
    private static final String UPDATED_NAME = "BBBB";
    private static final LocalDate UPDATED_START = LocalDate.now().minus(2, ChronoUnit.WEEKS);
    private static final LocalDate UPDATED_END = LocalDate.now();

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private SprintService sprintService;

    @Inject
    private SprintMapper sprintMapper;

    @Inject
    private SprintSearchRepository sprintSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSprintMockMvc;

    private Sprint sprint;

    @PostConstruct
    public void setup(){
        MockitoAnnotations.initMocks(this);
        SprintResource sprintResource = new SprintResource();
        ReflectionTestUtils.setField(sprintResource, "sprintService", sprintService);
        ReflectionTestUtils.setField(sprintResource, "sprintMapper", sprintMapper);
        this.restSprintMockMvc = MockMvcBuilders.standaloneSetup(sprintResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest(){
        sprintSearchRepository.deleteAll();
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = projectRepository.save(EntityGenerators.generateProject(Collections.singleton(user), null));
        sprint = Sprint.builder()
            .start(DEFAULT_START)
            .end(DEFAULT_END)
            .name(DEFAULT_NAME)
            .number(DEFAULT_NUMBER)
            .project(project)
            .build();
    }

    @Test
    @Transactional
    public void createSprint() throws Exception {
        int databaseSizeBeforeCreate = sprintRepository.findAll().size();

        // Create the Sprint
        SprintDTO sprintDTO = sprintMapper.sprintToSprintDTO(sprint);

        restSprintMockMvc.perform(post("/api/sprints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sprintDTO)))
            .andDo(print())
            .andExpect(status().isCreated());

        // Validate the Sprint in the database
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeCreate + 1);
        Sprint testSprint = sprints.get(sprints.size() - 1);
        assertThat(testSprint.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSprint.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testSprint.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testSprint.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testSprint.getEnd()).isEqualTo(DEFAULT_END);

        // Validate the Sprint in ElasticSearch
        Sprint sprintEs = sprintSearchRepository.findOne(testSprint.getId());
        assertThat(sprintEs).isEqualToComparingFieldByField(testSprint);
    }

    @Test
    @Transactional
    public void getAllSprints() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);

        // Get all the sprints
        restSprintMockMvc.perform(get("/api/sprints?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(sprint.getName())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    public void getSprint() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);

        // Get the sprint
        restSprintMockMvc.perform(get("/api/sprints/{id}", sprint.getId()))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sprint.getId().intValue()))
            .andExpect(jsonPath("$.name").value(sprint.getName()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSprint() throws Exception {
        // Get the sprint
        restSprintMockMvc.perform(get("/api/sprints/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSprint() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);
        sprintSearchRepository.save(sprint);
        int databaseSizeBeforeUpdate = sprintRepository.findAll().size();

        // Update the sprint
        Sprint updatedSprint = new Sprint();
        updatedSprint.setId(sprint.getId());
        updatedSprint.setName(UPDATED_NAME);
        updatedSprint.setNumber(UPDATED_NUMBER);
        updatedSprint.setStart(UPDATED_START);
        updatedSprint.setEnd(UPDATED_END);
        updatedSprint.setProject(sprint.getProject());
        SprintDTO sprintDTO = sprintMapper.sprintToSprintDTO(updatedSprint);

        restSprintMockMvc.perform(put("/api/sprints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sprintDTO)))
            .andDo(print())
            .andExpect(status().isOk());

        // Validate the Sprint in the database
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeUpdate);
        Sprint testSprint = sprints.get(sprints.size() - 1);
        assertThat(testSprint.getStart()).isEqualTo(UPDATED_START);
        assertThat(testSprint.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testSprint.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSprint.getNumber()).isEqualTo(UPDATED_NUMBER);

        // Validate the Sprint in ElasticSearch
        Sprint sprintEs = sprintSearchRepository.findOne(testSprint.getId());
        assertThat(sprintEs).isEqualToComparingFieldByField(testSprint);
    }

    @Test
    @Transactional
    public void deleteSprint() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);
        sprintSearchRepository.save(sprint);
        int databaseSizeBeforeDelete = sprintRepository.findAll().size();

        // Get the sprint
        restSprintMockMvc.perform(delete("/api/sprints/{id}", sprint.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean sprintExistsInEs = sprintSearchRepository.exists(sprint.getId());
        assertThat(sprintExistsInEs).isFalse();

        // Validate the database is empty
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSprint() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);
        sprintSearchRepository.save(sprint);

        // Search the sprint
        restSprintMockMvc.perform(get("/api/_search/sprints?query=id:" + sprint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(sprint.getName())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    public void shouldGetAllSprintsForGivenProject() throws Exception {
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = EntityGenerators.generateProject(Sets.newHashSet(user), Collections.emptyList());
        final Sprint sprint0 = EntityGenerators.generateSprint(project);
        final Sprint sprint1 = EntityGenerators.generateSprint(project);
        final Sprint sprint2 = EntityGenerators.generateSprint(project);

        projectRepository.save(project);
        sprintRepository.save(sprint0);
        sprintRepository.save(sprint1);
        sprintRepository.save(sprint2);

        restSprintMockMvc.perform(get("/api/sprints/byProject/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.content.[*].id").value(hasItem(sprint0.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].name").value(hasItem(sprint0.getName())))
            .andExpect(jsonPath("$.content.[*].start").value(hasItem(sprint0.getStart().toString())))
            .andExpect(jsonPath("$.content.[*].end").value(hasItem(sprint0.getEnd().toString())))
            .andExpect(jsonPath("$.content.[*].projectId").value(hasItem(project.getId().intValue())))

            .andExpect(jsonPath("$.content.[*].id").value(hasItem(sprint1.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].name").value(hasItem(sprint1.getName())))
            .andExpect(jsonPath("$.content.[*].start").value(hasItem(sprint1.getStart().toString())))
            .andExpect(jsonPath("$.content.[*].end").value(hasItem(sprint1.getEnd().toString())))

            .andExpect(jsonPath("$.content.[*].id").value(hasItem(sprint2.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].name").value(hasItem(sprint2.getName())))
            .andExpect(jsonPath("$.content.[*].start").value(hasItem(sprint2.getStart().toString())))
            .andExpect(jsonPath("$.content.[*].end").value(hasItem(sprint2.getEnd().toString())));
    }
}
