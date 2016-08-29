package com.mbancer.web.rest;

import com.mbancer.Tasker0App;
import com.mbancer.domain.*;
import com.mbancer.domain.UserStory;
import com.mbancer.repository.*;
import com.mbancer.repository.UserStoryRepository;
import com.mbancer.repository.search.UserStorySearchRepository;
import com.mbancer.service.UserStoryService;
import com.mbancer.service.util.EntityGenerators;
import com.mbancer.web.rest.dto.UserStoryDTO;
import com.mbancer.web.rest.mapper.UserStoryMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
public class UserStoryResourceIntTest {

    private static final Long DEFAULT_PRIORITY = 1L;
    private static final String DEFAULT_NAME = "AAAA";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAA";
    private static final LocalDate DEFAULT_CREATED = LocalDate.now().minus(3, ChronoUnit.WEEKS);
    private static final LocalDate DEFAULT_UPDATED = LocalDate.now().minus(1, ChronoUnit.WEEKS);

    private static final Long UPDATED_PRIORITY = 2L;
    private static final String UPDATED_NAME = "BBBB";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBB";
    private static final LocalDate UPDATED_CREATED = LocalDate.now().minus(2, ChronoUnit.WEEKS);
    private static final LocalDate UPDATED_UPDATED = LocalDate.now();

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private UserStoryService userStoryService;

    @Inject
    private UserStoryMapper userStoryMapper;

    @Inject
    private UserStorySearchRepository userStorySearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUserStoryMockMvc;

    private UserStory userStory;

    @PostConstruct
    public void setup(){
        MockitoAnnotations.initMocks(this);
        UserStoryResource userStoryResource = new UserStoryResource();
        ReflectionTestUtils.setField(userStoryResource, "userStoryService", userStoryService);
        ReflectionTestUtils.setField(userStoryResource, "userStoryMapper", userStoryMapper);
        this.restUserStoryMockMvc = MockMvcBuilders.standaloneSetup(userStoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest(){
        userStorySearchRepository.deleteAll();
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = projectRepository.save(EntityGenerators.generateProject(Collections.singleton(user), null));
        final Sprint sprint = sprintRepository.save(EntityGenerators.generateSprint(project));
        userStory = UserStory.builder()
                    .priority(DEFAULT_PRIORITY)
                    .created(DEFAULT_CREATED)
                    .updated(DEFAULT_UPDATED)
                    .name(DEFAULT_NAME)
                    .description(DEFAULT_DESCRIPTION)
                    .sprint(sprint)
                    .build();
    }

    @Test
    @Transactional
    public void createUserStory() throws Exception {
        int databaseSizeBeforeCreate = userStoryRepository.findAll().size();

        // Create the UserStory
        UserStoryDTO userStoryDTO = userStoryMapper.userStoryToUserStoryDTO(userStory);

        restUserStoryMockMvc.perform(post("/api/userStories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userStoryDTO)))
            .andDo(print())
            .andExpect(status().isCreated());

        // Validate the UserStory in the database
        List<UserStory> userStories = userStoryRepository.findAll();
        assertThat(userStories).hasSize(databaseSizeBeforeCreate + 1);
        UserStory testUserStory = userStories.get(userStories.size() - 1);
        assertThat(testUserStory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserStory.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testUserStory.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testUserStory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testUserStory.getPriority()).isEqualTo(DEFAULT_PRIORITY);

        // Validate the UserStory in ElasticSearch
        UserStory userStoryEs = userStorySearchRepository.findOne(testUserStory.getId());
        assertThat(userStoryEs).isEqualToComparingFieldByField(testUserStory);
    }

    @Test
    @Transactional
    public void getAllUserStorys() throws Exception {
        // Initialize the database
        userStoryRepository.saveAndFlush(userStory);

        // Get all the userStories
        restUserStoryMockMvc.perform(get("/api/userStories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userStory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(userStory.getName())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getUserStory() throws Exception {
        // Initialize the database
        userStoryRepository.saveAndFlush(userStory);

        // Get the userStory
        restUserStoryMockMvc.perform(get("/api/userStories/{id}", userStory.getId()))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userStory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(userStory.getName()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserStory() throws Exception {
        // Get the userStory
        restUserStoryMockMvc.perform(get("/api/userStories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserStory() throws Exception {
        // Initialize the database
        userStoryRepository.saveAndFlush(userStory);
        userStorySearchRepository.save(userStory);
        int databaseSizeBeforeUpdate = userStoryRepository.findAll().size();

        // Update the userStory
        UserStory updatedUserStory = new UserStory();
        updatedUserStory.setId(userStory.getId());
        updatedUserStory.setName(UPDATED_NAME);
        updatedUserStory.setCreated(UPDATED_CREATED);
        updatedUserStory.setUpdated(UPDATED_UPDATED);
        updatedUserStory.setPriority(UPDATED_PRIORITY);
        updatedUserStory.setDescription(UPDATED_DESCRIPTION);
        updatedUserStory.setSprint(userStory.getSprint());
        UserStoryDTO userStoryDTO = userStoryMapper.userStoryToUserStoryDTO(updatedUserStory);

        restUserStoryMockMvc.perform(put("/api/userStories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userStoryDTO)))
            .andDo(print())
            .andExpect(status().isOk());

        // Validate the UserStory in the database
        List<UserStory> userStories = userStoryRepository.findAll();
        assertThat(userStories).hasSize(databaseSizeBeforeUpdate);
        UserStory testUserStory = userStories.get(userStories.size() - 1);
        assertThat(testUserStory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserStory.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testUserStory.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testUserStory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUserStory.getPriority()).isEqualTo(UPDATED_PRIORITY);

        // Validate the UserStory in ElasticSearch
        UserStory userStoryEs = userStorySearchRepository.findOne(testUserStory.getId());
        assertThat(userStoryEs).isEqualToComparingFieldByField(testUserStory);
    }

    @Test
    @Transactional
    public void deleteUserStory() throws Exception {
        // Initialize the database
        userStoryRepository.saveAndFlush(userStory);
        userStorySearchRepository.save(userStory);
        int databaseSizeBeforeDelete = userStoryRepository.findAll().size();

        // Get the userStory
        restUserStoryMockMvc.perform(delete("/api/userStories/{id}", userStory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean userStoryExistsInEs = userStorySearchRepository.exists(userStory.getId());
        assertThat(userStoryExistsInEs).isFalse();

        // Validate the database is empty
        List<UserStory> userStories = userStoryRepository.findAll();
        assertThat(userStories).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserStory() throws Exception {
        // Initialize the database
        userStoryRepository.saveAndFlush(userStory);
        userStorySearchRepository.save(userStory);

        // Search the userStory
        restUserStoryMockMvc.perform(get("/api/_search/userStories?query=id:" + userStory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.[*].id").value(hasItem(userStory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(userStory.getName())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
        //TODO fix this, find out exception reason and check also in SprintResourceIntTest same search test ( not all fields are checked )
//            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }
}
