package com.mbancer.web.rest.mapper;

import com.mbancer.domain.*;
import com.mbancer.web.rest.dto.CommentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Comment and its DTO CommentDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, UserStoryMapper.class, SprintMapper.class})
public interface CommentMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "userStory.id", target = "userStoryId")
    @Mapping(source = "sprint.id", target = "sprintId")
    CommentDTO commentToCommentDTO(Comment comment);

    List<CommentDTO> commentsToCommentDTOs(List<Comment> comments);

    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "taskId", target = "task")
    @Mapping(source = "userStoryId", target = "userStory")
    @Mapping(source = "sprintId", target = "sprint")
    Comment commentDTOToComment(CommentDTO commentDTO);

    List<Comment> commentDTOsToComments(List<CommentDTO> commentDTOs);

    default Task taskFromId(Long id) {
        if (id == null) {
            return null;
        }
        Task task = new Task();
        task.setId(id);
        return task;
    }

}
