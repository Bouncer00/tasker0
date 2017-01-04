package com.mbancer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mbancer.domain.Sprint;
import com.mbancer.domain.UserStory;
import com.mbancer.service.UserStoryService;
import com.mbancer.web.rest.dto.UserStoryDTO;
import com.mbancer.web.rest.mapper.UserStoryMapper;
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
public class UserStoryResource {

    private final Logger log = LoggerFactory.getLogger(SprintResource.class);

    @Inject
    private UserStoryService userStoryService;

    @Inject
    private UserStoryMapper userStoryMapper;

    @RequestMapping(value = "/userStories",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserStoryDTO> createUserStory(@Valid @RequestBody UserStoryDTO userStoryDTO) throws URISyntaxException {
        log.debug("REST request to save Sprint : {}", userStoryDTO);
        if(userStoryDTO.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userStory", "idexists", "A new userStory should not have id")).body(null);
        }
        UserStoryDTO result = userStoryService.save(userStoryDTO);
        return ResponseEntity.created(new URI("/api/userStories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("userStory", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/userStories",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserStoryDTO> updateUserStory(@Valid @RequestBody UserStoryDTO userStoryDTO) throws URISyntaxException {
        if(userStoryDTO.getId() == null){
            return createUserStory(userStoryDTO);
        }
        UserStoryDTO result = userStoryService.save(userStoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("userStory", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(value = "/userStories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserStoryDTO>> getAllUserStories(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of UserStory");
        Page<UserStory> page = userStoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/userStories");
        return new ResponseEntity<>(userStoryMapper.userStoriesToUserStoryDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/userStories/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserStoryDTO> getUserStory(@PathVariable Long id){
        log.debug("REST request to get Sprint : {]", id);
        UserStoryDTO userStoryDTO = userStoryService.findOne(id);
        return Optional.ofNullable(userStoryDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/userStories/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUserStory(@PathVariable Long id){
        log.debug("REST request to delete Sprint : {}", id);
        userStoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("board", id.toString())).build();
    }

    @RequestMapping(value = "/_search/userStories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserStoryDTO>> searchUserStories(@RequestParam String query, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to search for a page of Sprints for query {}", query);
        Page<UserStory> page = userStoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/userStories");
        return new ResponseEntity<>(userStoryMapper.userStoriesToUserStoryDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/userStories/bySprint/{sprintId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Page<UserStoryDTO>> getUserStoriesBySprintId(@PathVariable("sprintId") Long sprintId, Pageable pageable){
        log.debug("REST request to get all Sprints for Project : {}", sprintId);
        Page<UserStoryDTO> sprintDTOs = userStoryService.findBySprintId(sprintId, pageable);
        return ResponseEntity.ok().body(sprintDTOs);
    }

    @RequestMapping(value = "userStories/moveUp/{userStoryId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> moveUserStoryUp(@PathVariable("userStoryId") Long userStoryId){
        log.debug("REST request to move userStory {} up", userStoryId);
        userStoryService.moveUserStoryByIdUp(userStoryId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "userStories/moveDown/{userStoryId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> moveUserStoryDown(@PathVariable("userStoryId") Long userStoryId){
        log.debug("REST request to move userStory {} down", userStoryId);
        userStoryService.moveUserStoryByIdDown(userStoryId);
        return ResponseEntity.ok().build();
    }
}
