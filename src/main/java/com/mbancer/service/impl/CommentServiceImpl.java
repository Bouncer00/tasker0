package com.mbancer.service.impl;

import com.mbancer.domain.*;
import com.mbancer.repository.*;
import com.mbancer.security.SecurityUtils;
import com.mbancer.service.CommentService;
import com.mbancer.web.rest.dto.CommentDTO;
import com.mbancer.web.rest.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

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
    private UserRepository userRepository;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private SprintRepository sprintRepository;

    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save
     * @return the persisted entity
     */
    public CommentDTO save(CommentDTO commentDTO) {
        log.debug("Request to save Comment : {}", commentDTO);
        Comment comment = commentMapper.commentDTOToComment(commentDTO);
        comment = commentRepository.save(comment);
        if(isNull(comment.getAuthor())){
            final String userLogin = SecurityUtils.getCurrentUserLogin();
            final User user = userRepository.findOneByLogin(userLogin).get();
            comment.setAuthor(user);
        }
        if(null != comment.getTask()) {
            Task ts = taskRepository.findOne(comment.getTask().getId());
            ts.setUpdated(LocalDateTime.now());
            ts.getComments().add(comment);
            taskRepository.save(ts);
        }
        if(null != comment.getUserStory()){
            UserStory us = userStoryRepository.findOne(comment.getUserStory().getId());
            us.setUpdated(LocalDateTime.now());
            us.getComments().add(comment);
            userStoryRepository.save(us);
        }
        if(null != comment.getSprint()){
            Sprint sp = sprintRepository.findOne(comment.getSprint().getId());
            sp.getComments().add(comment);
            sprintRepository.save(sp);
        }
        CommentDTO result = commentMapper.commentToCommentDTO(comment);
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
    }

    @Override
    public Page<CommentDTO> getByTaskId(Long taskId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByTaskId(taskId, pageable);
        return comments.map(commentMapper::commentToCommentDTO);
    }
}
