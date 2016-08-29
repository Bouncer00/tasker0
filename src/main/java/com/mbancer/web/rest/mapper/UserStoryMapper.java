package com.mbancer.web.rest.mapper;

import com.mbancer.domain.UserStory;
import com.mbancer.web.rest.dto.UserStoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SprintMapper.class})
public interface UserStoryMapper {

    @Mapping(source = "sprint.id", target = "sprintId")
    UserStoryDTO userStoryToUserStoryDTO(UserStory userStory);

    List<UserStoryDTO> userStoriesToUserStoryDTOs(List<UserStory> userStories);

    @Mapping(source = "sprintId", target = "sprint")
    UserStory userStoryDTOToUserStory(UserStoryDTO userStoryDTO);

    List<UserStory> userStoryDTOsToUserStories(List<UserStoryDTO> userStoryDTOs);

    default UserStory userStoryFromId(Long id){
        if(id == null){
            return null;
        }
        return UserStory.builder().id(id).build();
    }


}
