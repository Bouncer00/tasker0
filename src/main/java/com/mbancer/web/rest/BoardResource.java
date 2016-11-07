package com.mbancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mbancer.domain.Board;
import com.mbancer.service.BoardService;
import com.mbancer.web.rest.dto.BoardDTO;
import com.mbancer.web.rest.mapper.BoardMapper;
import com.mbancer.web.rest.util.HeaderUtil;
import com.mbancer.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BoardResource {

    private final Logger log = LoggerFactory.getLogger(BoardResource.class);

    @Inject
    private BoardService boardService;

    @Inject
    private BoardMapper boardMapper;

    @RequestMapping(value = "/boards",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody BoardDTO boardDTO) throws URISyntaxException {
        log.debug("REST request to save Board : {}", boardDTO);
        if(boardDTO.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("board", "idexists", "A new board cannot already have an id")).body(null);
        }
        BoardDTO result = boardService.save(boardDTO);
        return ResponseEntity.created(new URI("/api/boards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("board", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/boards",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BoardDTO> updateBoard(@Valid @RequestBody BoardDTO boardDTO) throws URISyntaxException {
        log.debug("REST request to update Board : {]", boardDTO);
        if(boardDTO.getId() == null){
            return createBoard(boardDTO);
        }
        BoardDTO result = boardService.save(boardDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("board", boardDTO.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/boards",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BoardDTO>> getAllBoards(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Boards");
        Page<Board> page = boardService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/boards");
        return new ResponseEntity<>(boardMapper.boardsToBoardDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/boards/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id){
        log.debug("REST request to get Board : {]", id);
        BoardDTO boardDTO = boardService.findOne(id);
        return Optional.ofNullable(boardDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/boards/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id){
        log.debug("REST request to delete Board : {}", id);
        boardService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("board", id.toString())).build();
    }

    @RequestMapping(value = "/_search/boards",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BoardDTO>> searchBoards(@RequestParam String query, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to search for a page of Boards for query {}", query);
        Page<Board> page = boardService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/boards");
        return new ResponseEntity<>(boardMapper.boardsToBoardDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/boards/moveTask",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> moveTaskBetweenBoards(@RequestParam(name = "sourceBoardId", required = false) Long sourceBoardId, @RequestParam("targetBoardId") Long targetBoardId, @RequestParam("taskId") Long taskId){
        log.debug("REST request to move task {} from board {} to board {}", taskId, sourceBoardId, targetBoardId);
        try {
            boardService.moveTaskFromSourceBoardToTarget(sourceBoardId, targetBoardId, taskId);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/boards/byProject/{projectId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<BoardDTO>> getBoardsByProject(@PathVariable("projectId") Long projectId, Pageable pageable){
        log.debug("REST request to get Boards by Project : {}", projectId);
        Page<BoardDTO> boards = boardService.getByProject(projectId, pageable);
        return ResponseEntity.ok(boards);
    }
}
