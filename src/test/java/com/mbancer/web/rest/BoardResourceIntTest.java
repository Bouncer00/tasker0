package com.mbancer.web.rest;

import com.mbancer.Tasker0App;
import com.mbancer.domain.Board;
import com.mbancer.repository.BoardRepository;
import com.mbancer.repository.search.BoardSearchRepository;
import com.mbancer.service.BoardService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBoardMockMvc;

    private Board board;

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
        board = new Board();
        board.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createBoard() throws Exception {
        int databaseSizeBeforeCreate = boardRepository.findAll().size();

        BoardDTO boardDTO = boardMapper.boardToBoardDTO(board);

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
        boardRepository.saveAndFlush(board);

        restBoardMockMvc.perform(get("/api/boards/{id}", board.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(board.getId().intValue()))
            .andExpect(jsonPath("name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBoard() throws Exception {
        restBoardMockMvc.perform(get("/api/boards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        boardRepository.saveAndFlush(board);
        boardSearchRepository.save(board);
        int databaseSizeBeforeUpdate = boardRepository.findAll().size();

        Board updatedBoard = new Board();
        updatedBoard.setId(board.getId());
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
    public void deleteComment() throws Exception {
        boardRepository.saveAndFlush(board);
        boardSearchRepository.save(board);
        int databaseSizeBeforeDelete = boardRepository.findAll().size();

        restBoardMockMvc.perform(delete("/api/boards/{id}", board.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        boolean boardExistsInEs = boardSearchRepository.exists(board.getId());
        assertThat(boardExistsInEs).isFalse();

        List<Board> boards = boardRepository.findAll();
        assertThat(boards).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchComment() throws Exception {
        boardRepository.saveAndFlush(board);
        boardSearchRepository.save(board);

        restBoardMockMvc.perform(get("/api/_search/boards?query=id:" + board.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(board.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
