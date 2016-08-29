package com.mbancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mbancer.domain.Sprint;
import com.mbancer.service.SprintService;
import com.mbancer.web.rest.dto.SprintDTO;
import com.mbancer.web.rest.dto.SprintDTO;
import com.mbancer.web.rest.mapper.SprintMapper;
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
public class SprintResource {

    private final Logger log = LoggerFactory.getLogger(SprintResource.class);

    @Inject
    private SprintService sprintService;

    @Inject
    private SprintMapper sprintMapper;

    @RequestMapping(value = "/sprints",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SprintDTO> createSprint(@Valid @RequestBody SprintDTO sprintDTO) throws URISyntaxException {
        log.debug("REST request to save Sprint : {}", sprintDTO);
        if(sprintDTO.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sprint", "idexists", "A new sprint should not have id")).body(null);
        }
        SprintDTO result = sprintService.save(sprintDTO);
        return ResponseEntity.created(new URI("/api/sprints/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sprint", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/sprints",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SprintDTO> updateBoard(@Valid @RequestBody SprintDTO sprintDTO) throws URISyntaxException {
        if(sprintDTO.getId() == null){
            return createSprint(sprintDTO);
        }
        SprintDTO result = sprintService.save(sprintDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sprint", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/sprints",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SprintDTO>> getAllComments(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sprint");
        Page<Sprint> page = sprintService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sprints");
        return new ResponseEntity<>(sprintMapper.sprintsToSprintDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/sprints/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SprintDTO> getBoard(@PathVariable Long id){
        log.debug("REST request to get Sprint : {]", id);
        SprintDTO sprintDTO = sprintService.findOne(id);
        return Optional.ofNullable(sprintDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/sprints/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id){
        log.debug("REST request to delete Sprint : {}", id);
        sprintService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("board", id.toString())).build();
    }

    @RequestMapping(value = "/_search/sprints",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SprintDTO>> searchBoards(@RequestParam String query, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to search for a page of Sprints for query {}", query);
        Page<Sprint> page = sprintService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sprints");
        return new ResponseEntity<>(sprintMapper.sprintsToSprintDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
