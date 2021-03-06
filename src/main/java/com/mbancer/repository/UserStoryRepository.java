package com.mbancer.repository;

import com.mbancer.domain.UserStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    Page<UserStory> findOneBySprintId(Long sprintId, Pageable pageable);

    UserStory findOneBySprintIdAndNumber(Long sprintId, Long number);

    Page<UserStory> findAllBySprintIdOrderByNumber(Long sprintId, Pageable pageable);
}
