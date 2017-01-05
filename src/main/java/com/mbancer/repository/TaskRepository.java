package com.mbancer.repository;

import com.mbancer.domain.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("select task from Task task where task.user.login = ?#{principal.username}")
    List<Task> findByUserIsCurrentUser();

    Page<Task> findAllByUserId(Long userId, Pageable pageable);

    Page<Task> findAllByUserStoryId(Long userStoryId, Pageable pageable);

    Task findOneByUserStoryIdAndNumber(long userStoryId, long number);

    Page<Task> findAllByUserStoryIdOrderByNumber(Long userStoryId, Pageable pageable);

    List<Task> findAllByUserId(Long id);

    List<Task> findAllByAssigneeId(Long id);
}
