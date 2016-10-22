package com.mbancer.service;

import com.mbancer.domain.Board;
import com.mbancer.web.rest.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    BoardDTO save(BoardDTO boardDTO);

    Page<Board> findAll(Pageable pageable);

    BoardDTO findOne(Long id);

    void delete(Long id);

    Page<Board> search(String query, Pageable pageable);

    void moveTaskFromSourceBoardToTarget(Long sourceBoardId, Long targetBoard, Long taskId);

    Page<BoardDTO> getByProject(Long projectId, Pageable pageable);
}
