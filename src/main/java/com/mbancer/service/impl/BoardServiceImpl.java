package com.mbancer.service.impl;

import com.mbancer.domain.Board;
import com.mbancer.domain.Task;
import com.mbancer.repository.BoardRepository;
import com.mbancer.repository.search.BoardSearchRepository;
import com.mbancer.service.BoardService;
import com.mbancer.web.rest.dto.BoardDTO;
import com.mbancer.web.rest.mapper.BoardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Optional;

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
    private BoardSearchRepository boardSearchRepository;

    @Override
    public BoardDTO save(BoardDTO boardDTO) {
        log.debug("Request to save Board : {}", boardDTO);
        Board board = boardMapper.boardDTOToBoard(boardDTO);
        board = boardRepository.save(board);
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
        boardRepository.delete(id);
        boardSearchRepository.delete(id);
    }

    @Override
    public Page<Board> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Boards for query {}", query);
        return boardSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    public void moveTaskFromSourceBoardToTarget(Long sourceBoardId, Long targetBoardId, Long taskId) {
        log.debug("Request to move task {} from board {} to board {}", taskId, sourceBoardId, targetBoardId);
        final Board sourceBoard = boardRepository.findOne(sourceBoardId);
        final Board targetBoard = boardRepository.findOne(targetBoardId);
        if(sourceBoard == null){
            throw new IllegalArgumentException("Source board " + sourceBoardId + " does not exists");
        }
        if(targetBoard == null){
            throw new IllegalArgumentException("Target board " + sourceBoardId + " does not exists");
        }
        final Optional<Task> taskOptional = sourceBoard.getTasks().stream().filter(task -> task.getId().equals(taskId)).findFirst();

        taskOptional.map(task -> {
            sourceBoard.getTasks().remove(task);
            return targetBoard.getTasks().add(task);
        }).orElseThrow(() -> new IllegalArgumentException("Source board " + sourceBoard + " does not have task " + taskId));
    }

    @Override
    public Page<BoardDTO> getByProject(Long projectId, Pageable pageable) {
        Page<Board> boards = boardRepository.findAllByProjectId(projectId, pageable);
        return boards.map(boardMapper::boardToBoardDTO);
    }
}
