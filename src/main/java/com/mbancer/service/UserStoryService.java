package com.mbancer.service;

import com.mbancer.domain.UserStory;
import com.mbancer.web.rest.dto.UserStoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserStoryService {

    UserStoryDTO save(UserStoryDTO userStoryDTO);

    Page<UserStory> findAll(Pageable pageable);

    UserStoryDTO findOne(Long id);

    void delete(Long id);

    Page<UserStory> search(String query, Pageable pageable);

    Page<UserStoryDTO> findBySprintId(Long sprintId, Pageable pageable);

    UserStory getNextUserStory(UserStory userStory);

    UserStory getPreviousUserStory(UserStory userStory);
}
