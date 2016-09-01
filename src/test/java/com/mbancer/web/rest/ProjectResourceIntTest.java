package com.mbancer.web.rest;

import com.google.common.collect.Sets;
import com.mbancer.Tasker0App;
import com.mbancer.domain.Project;
import com.mbancer.domain.User;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.UserRepository;
import com.mbancer.service.ProjectService;
import com.mbancer.repository.search.ProjectSearchRepository;
import com.mbancer.service.util.EntityGenerators;
import com.mbancer.web.rest.dto.ProjectDTO;
import com.mbancer.web.rest.mapper.ProjectMapper;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.doesNotHave;
import static org.hamcrest.Matchers.hasItem;

import org.mockito.AdditionalMatchers;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.UserException;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ProjectResource REST controller.
 *
 * @see ProjectResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
public class ProjectResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_SHORTNAME = "CCCCC";
    private static final String UPDATED_SHORTNAME = "DDDDD";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DEAD_LINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEAD_LINE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectSearchRepository projectSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private UserRepository userRepository;

    private MockMvc restProjectMockMvc;

    private Project project;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectResource projectResource = new ProjectResource();
        ReflectionTestUtils.setField(projectResource, "projectService", projectService);
        ReflectionTestUtils.setField(projectResource, "projectMapper", projectMapper);
        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        projectSearchRepository.deleteAll();
        project = new Project();
        project.setName(DEFAULT_NAME);
        project.setDescription(DEFAULT_DESCRIPTION);
        project.setCreated(DEFAULT_CREATED);
        project.setDeadLine(DEFAULT_DEAD_LINE);
        project.setShortName(DEFAULT_SHORTNAME);
    }

    @Test
    @Transactional
    public void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create the Project
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projects.get(projects.size() - 1);
        assertThat(testProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testProject.getDeadLine()).isEqualTo(DEFAULT_DEAD_LINE);

        // Validate the Project in ElasticSearch
        Project projectEs = projectSearchRepository.findOne(testProject.getId());
        assertThat(projectEs).isEqualToComparingFieldByField(testProject);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(project);

        restProjectMockMvc.perform(post("/api/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isBadRequest());

        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjects() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projects
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
                .andExpect(jsonPath("$.[*].deadLine").value(hasItem(DEFAULT_DEAD_LINE.toString())));
    }

    @Test
    @Transactional
    public void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.deadLine").value(DEFAULT_DEAD_LINE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        projectSearchRepository.save(project);
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project
        Project updatedProject = new Project();
        updatedProject.setId(project.getId());
        updatedProject.setName(UPDATED_NAME);
        updatedProject.setDescription(UPDATED_DESCRIPTION);
        updatedProject.setCreated(UPDATED_CREATED);
        updatedProject.setDeadLine(UPDATED_DEAD_LINE);
        updatedProject.setShortName(UPDATED_SHORTNAME);
        ProjectDTO projectDTO = projectMapper.projectToProjectDTO(updatedProject);

        restProjectMockMvc.perform(put("/api/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projects.get(projects.size() - 1);
        assertThat(testProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testProject.getDeadLine()).isEqualTo(UPDATED_DEAD_LINE);
        assertThat(testProject.getShortName()).isEqualTo(UPDATED_SHORTNAME);

        // Validate the Project in ElasticSearch
        Project projectEs = projectSearchRepository.findOne(testProject.getId());
        assertThat(projectEs).isEqualToComparingFieldByField(testProject);
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        projectSearchRepository.save(project);
        int databaseSizeBeforeDelete = projectRepository.findAll().size();

        // Get the project
        restProjectMockMvc.perform(delete("/api/projects/{id}", project.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean projectExistsInEs = projectSearchRepository.exists(project.getId());
        assertThat(projectExistsInEs).isFalse();

        // Validate the database is empty
        List<Project> projects = projectRepository.findAll();
        assertThat(projects).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        projectSearchRepository.save(project);

        // Search the project
        restProjectMockMvc.perform(get("/api/_search/projects?query=id:" + project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].deadLine").value(hasItem(DEFAULT_DEAD_LINE.toString())));
    }

    @Test
    @Transactional
    public void shouldFindProjectsByUserId() throws Exception {
        final User user = userRepository.findOneByLogin("admin").get();
        final User otherUser = userRepository.findOneByLogin("user").get();

        final Project userProject = EntityGenerators.generateProject(Sets.newHashSet(user), Collections.emptyList());
        final Project otherUserProject = EntityGenerators.generateProject(Sets.newHashSet(otherUser), Collections.emptyList());

        project.getUsers().add(user);
        userProject.getUsers().add(user);
        otherUserProject.getUsers().add(otherUser);

        projectRepository.saveAndFlush(project);
        projectRepository.saveAndFlush(userProject);
        projectRepository.saveAndFlush(otherUserProject);

        restProjectMockMvc.perform(get("/api/projects/byUser/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].id").value(hasItem(userProject.getId().intValue())))
            .andExpect(jsonPath("$.content.[*].id").value(not(hasItem(otherUserProject.getId().intValue()))));

    }
}
