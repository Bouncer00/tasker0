package com.mbancer.service.impl;

import com.mbancer.domain.User;
import com.mbancer.repository.UserRepository;
import com.mbancer.security.SecurityUtils;
import com.mbancer.service.CommentService;
import com.mbancer.domain.Comment;
import com.mbancer.repository.CommentRepository;
import com.mbancer.repository.search.CommentSearchRepository;
import com.mbancer.web.rest.dto.CommentDTO;
import com.mbancer.web.rest.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Comment.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService{

    private final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentSearchRepository commentSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save
     * @return the persisted entity
     */
    public CommentDTO save(CommentDTO commentDTO) {
        log.debug("Request to save Comment : {}", commentDTO);
        Comment comment = commentMapper.commentDTOToComment(commentDTO);
        if(isNull(comment.getAuthor())){
            final String userLogin = SecurityUtils.getCurrentUserLogin();
            final User user = userRepository.findOneByLogin(userLogin).get();
            comment.setAuthor(user);
        }
        comment = commentRepository.save(comment);
        comment.getTask().setUpdated(LocalDate.now());
        CommentDTO result = commentMapper.commentToCommentDTO(comment);
        commentSearchRepository.save(comment);
        return result;
    }

    /**
     *  Get all the comments.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> findAll(Pageable pageable) {
        log.debug("Request to get all Comments");
        return commentRepository.findAll(pageable);
    }

    /**
     *  Get one comment by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public CommentDTO findOne(Long id) {
        log.debug("Request to get Comment : {}", id);
        Comment comment = commentRepository.findOne(id);
        return commentMapper.commentToCommentDTO(comment);
    }

    /**
     *  Delete the  comment by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Comment : {}", id);
        commentRepository.delete(id);
        commentSearchRepository.delete(id);
    }

    /**
     * Search for the comment corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Comments for query {}", query);
        return commentSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    public Page<CommentDTO> getByTaskId(Long taskId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByTaskId(taskId, pageable);
        return comments.map(commentMapper::commentToCommentDTO);
    }
}
