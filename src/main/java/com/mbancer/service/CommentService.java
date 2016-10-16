package com.mbancer.service;

import com.mbancer.domain.Comment;
import com.mbancer.web.rest.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Comment.
 */
public interface CommentService {

    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save
     * @return the persisted entity
     */
    CommentDTO save(CommentDTO commentDTO);

    /**
     *  Get all the comments.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Comment> findAll(Pageable pageable);

    /**
     *  Get the "id" comment.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    CommentDTO findOne(Long id);

    /**
     *  Delete the "id" comment.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the comment corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Comment> search(String query, Pageable pageable);

    Page<CommentDTO> getByTaskId(Long taskId, Pageable pageable);
}
