package com.mbancer.web.rest.mapper;

import com.mbancer.domain.*;
import com.mbancer.web.rest.dto.TaskDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Task and its DTO TaskDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ProjectMapper.class, UserStoryMapper.class, BoardMapper.class, CommentMapper.class})
public interface TaskMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "userStory.id", target = "userStoryId")
    @Mapping(source = "board", target = "board")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(source = "assignee.firstName", target = "assigneeFirstName")
    @Mapping(source = "assignee.lastName", target = "assigneeLastName")
    TaskDTO taskToTaskDTO(Task task);

    List<TaskDTO> tasksToTaskDTOs(List<Task> tasks);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "userStoryId", target = "userStory")
    @Mapping(target = "comments", ignore = true)
    Task taskDTOToTask(TaskDTO taskDTO);

    List<Task> taskDTOsToTasks(List<TaskDTO> taskDTOs);

    default Task taskFromId(Long id) {
        if (id == null) {
            return null;
        }
        return Task.builder().id(id).build();
    }
}
