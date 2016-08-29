package com.mbancer.web.rest.mapper;

import com.mbancer.domain.*;
import com.mbancer.web.rest.dto.ProjectDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Project and its DTO ProjectDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, SprintMapper.class})
public interface ProjectMapper {

    ProjectDTO projectToProjectDTO(Project project);

    List<ProjectDTO> projectsToProjectDTOs(List<Project> projects);

    Project projectDTOToProject(ProjectDTO projectDTO);

    List<Project> projectDTOsToProjects(List<ProjectDTO> projectDTOs);

    default Project projectFromId(Long id){
        if(id == null){
            return null;
        }
        return Project.builder().id(id).build();
    }
}
