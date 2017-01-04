package com.mbancer.web.rest.mapper;

import com.mbancer.domain.Sprint;
import com.mbancer.web.rest.dto.SprintDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, UserStoryMapper.class, CommentMapper.class})
public interface SprintMapper {


    @Mapping(source = "project.id", target = "projectId")
    SprintDTO sprintToSprintDTO(Sprint sprint);

    List<SprintDTO> sprintsToSprintDTOs(List<Sprint> sprints);

    @Mapping(source = "projectId", target = "project")
    Sprint sprintDTOToSprint(SprintDTO sprintDTO);

    List<Sprint> sprintDTOsToSprints(List<SprintDTO> sprintDTOs);

    default Sprint sprintFromId(Long id){
        if(id == null){
            return null;
        }
        Sprint sprint = new Sprint();
        sprint.setId(id);
        return sprint;
    }
}
