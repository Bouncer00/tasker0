package com.mbancer.service.impl;

import com.mbancer.domain.Board;
import com.mbancer.domain.Project;
import com.mbancer.domain.Task;
import com.mbancer.repository.BoardRepository;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.TaskRepository;
import com.mbancer.repository.search.BoardSearchRepository;
import com.mbancer.service.BoardService;
import com.mbancer.web.rest.dto.BoardDTO;
import com.mbancer.web.rest.dto.TaskDTO;
import com.mbancer.web.rest.mapper.BoardMapper;
import com.mbancer.web.rest.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    @Inject
    private BoardRepository boardRepository;

    @Inject
    private BoardMapper boardMapper;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private BoardSearchRepository boardSearchRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TaskMapper taskMapper;

    @Override
    public BoardDTO save(BoardDTO boardDTO) {
        log.debug("Request to save Board : {}", boardDTO);

        Board board = boardMapper.boardDTOToBoard(boardDTO);
        board = boardRepository.save(board);
        if(null != boardDTO.getProjectId()){
            Project project = projectRepository.findOne(boardDTO.getProjectId());
            project.getBoards().add(board);
        }
        BoardDTO result = boardMapper.boardToBoardDTO(board);
        boardSearchRepository.save(board);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Board> findAll(Pageable pageable) {
        log.debug("Request to get all Boards");
        Page<Board> result = boardRepository.findAll(pageable);
        return result;
    }

    @Override
    public BoardDTO findOne(Long id) {
        log.debug("Request to get Board : {}", id);
        Board board = boardRepository.findOne(id);
        BoardDTO boardDTO = boardMapper.boardToBoardDTO(board);
        return boardDTO;
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Board : {}", id);
        final Board board = boardRepository.findOne(id);
        if(null != board.getProject()){
            final Project project = projectRepository.findOne(board.getProject().getId());
            project.getBoards().remove(board);
        }
        boardRepository.delete(id);
        boardSearchRepository.delete(id);
    }

    @Override
    public Page<Board> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Boards for query {}", query);
        return boardSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    public TaskDTO moveTaskFromSourceBoardToTarget(Long sourceBoardId, @NotNull Long targetBoardId, @NotNull Long taskId) {
        log.debug("Request to move task {} from board {} to board {}", taskId, sourceBoardId, targetBoardId);
        final Board targetBoard = boardRepository.findOne(targetBoardId);
        final Task task = taskRepository.findOne(taskId);
        if(targetBoard == null){
            throw new IllegalArgumentException("Target board " + sourceBoardId + " does not exists");
        }
        if(sourceBoardId == null){
            targetBoard.getTasks().add(task);
            task.setBoard(targetBoard);
        }
        else{
            final Board sourceBoard = boardRepository.findOne(sourceBoardId);
            if(nonNull(sourceBoard.getTasks())) {
                sourceBoard.getTasks().remove(task);
            }
            if(nonNull(targetBoard.getTasks())) {
                targetBoard.getTasks().add(task);
            }
            task.setBoard(targetBoard);
        }
        task.setUpdated(LocalDate.now());
        return taskMapper.taskToTaskDTO(taskRepository.save(task));
    }

    @Override
    public Page<BoardDTO> getByProject(Long projectId, Pageable pageable) {
        Page<Board> boards = boardRepository.findAllByProjectId(projectId, pageable);
        return boards.map(boardMapper::boardToBoardDTO);
    }
}
