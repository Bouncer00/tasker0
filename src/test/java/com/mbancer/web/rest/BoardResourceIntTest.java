package com.mbancer.web.rest;

import com.mbancer.Tasker0App;
import com.mbancer.domain.*;
import com.mbancer.repository.*;
import com.mbancer.repository.search.BoardSearchRepository;
import com.mbancer.service.BoardService;
import com.mbancer.service.util.EntityGenerators;
import com.mbancer.web.rest.dto.BoardDTO;
import com.mbancer.web.rest.mapper.BoardMapper;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
public class BoardResourceIntTest {


    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private BoardRepository boardRepository;

    @Inject
    private BoardMapper boardMapper;

    @Inject
    private BoardService boardService;

    @Inject
    private BoardSearchRepository boardSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBoardMockMvc;

    private Board board0;

    private Board board1;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BoardResource boardResource = new BoardResource();
        ReflectionTestUtils.setField(boardResource, "boardService", boardService);
        ReflectionTestUtils.setField(boardResource, "boardMapper", boardMapper);
        this.restBoardMockMvc = MockMvcBuilders.standaloneSetup(boardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest(){
        boardRepository.deleteAll();
        board0 = new Board();
        board0.setName(DEFAULT_NAME);
        board1 = new Board();
        board1.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createBoard() throws Exception {
        int databaseSizeBeforeCreate = boardRepository.findAll().size();

        BoardDTO boardDTO = boardMapper.boardToBoardDTO(board0);

        restBoardMockMvc.perform(post("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(boardDTO))
        ).andExpect(status().isCreated());

        List<Board> boards = boardRepository.findAll();
        assertThat(boards).hasSize(databaseSizeBeforeCreate + 1);
        Board testBoard = boards.get(boards.size() - 1);
        assertThat(testBoard.getName()).isEqualTo(DEFAULT_NAME);

        Board boardES = boardSearchRepository.findOne(testBoard.getId());
        assertThat(boardES).isEqualToComparingFieldByField(testBoard);
    }

    @Test
    @Transactional
    public void getBoard() throws Exception {
        boardRepository.saveAndFlush(board0);

        restBoardMockMvc.perform(get("/api/boards/{id}", board0.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(board0.getId().intValue()))
            .andExpect(jsonPath("name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getAllBoards() throws Exception {
        boardRepository.saveAndFlush(board0);

        restBoardMockMvc.perform(get("/api/boards?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(board0.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getNonExistingBoard() throws Exception {
        restBoardMockMvc.perform(get("/api/boards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBoard() throws Exception {
        boardRepository.saveAndFlush(board0);
        boardSearchRepository.save(board0);
        int databaseSizeBeforeUpdate = boardRepository.findAll().size();

        Board updatedBoard = new Board();
        updatedBoard.setId(board0.getId());
        updatedBoard.setName(UPDATED_NAME);
        BoardDTO commentDTO = boardMapper.boardToBoardDTO(updatedBoard);

        restBoardMockMvc.perform(put("/api/boards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isOk());

        List<Board> boards = boardRepository.findAll();
        assertThat(boards).hasSize(databaseSizeBeforeUpdate);
        Board testBoard = boards.get(boards.size() - 1);
        assertThat(testBoard.getName()).isEqualTo(UPDATED_NAME);

        Board boardES = boardSearchRepository.findOne(testBoard.getId());
        assertThat(boardES).isEqualToComparingFieldByField(testBoard);
    }

    @Test
    @Transactional
    public void deleteBoard() throws Exception {
        boardRepository.saveAndFlush(board0);
        boardSearchRepository.save(board0);
        int databaseSizeBeforeDelete = boardRepository.findAll().size();

        restBoardMockMvc.perform(delete("/api/boards/{id}", board0.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        boolean boardExistsInEs = boardSearchRepository.exists(board0.getId());
        assertThat(boardExistsInEs).isFalse();

        List<Board> boards = boardRepository.findAll();
        assertThat(boards).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBoard() throws Exception {
        boardRepository.saveAndFlush(board0);
        boardSearchRepository.save(board0);

        restBoardMockMvc.perform(get("/api/_search/boards?query=id:" + board0.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(board0.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void shouldMoveTaskFromSourceBoardToTarget() throws Exception {
        //given
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = projectRepository.save(EntityGenerators.generateProject(Collections.singleton(user), null));
        final Sprint sprint = sprintRepository.save(EntityGenerators.generateSprint(project));
        final UserStory userStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));
        final Task task = taskRepository.save(EntityGenerators.generateTask(user, project, userStory));
        project.getTasks().add(task);
        board0.getTasks().add(task);
        boardRepository.save(board0);
        boardRepository.save(board1);

        //when
        restBoardMockMvc.perform(
            put("/api/boards/moveTask/{sourceBoardId}/{targetBoardId}/{taskId}",
                board0.getId(), board1.getId(), task.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertThat(board0.getTasks()).doesNotContain(task);
        assertThat(board1.getTasks()).contains(task);
    }
}
