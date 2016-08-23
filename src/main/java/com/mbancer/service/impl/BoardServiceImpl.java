package com.mbancer.service.impl;

import com.mbancer.domain.Board;
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
}
