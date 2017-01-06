package com.mbancer.service;

import com.mbancer.domain.Sprint;
import com.mbancer.web.rest.dto.SprintDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SprintService {

    SprintDTO save(SprintDTO sprintDTO);

    Page<Sprint> findAll(Pageable pageable);

    SprintDTO findOne(Long id);

    void delete(Long id);

    Page<SprintDTO> findByProjectId(Long projectId, Pageable pageable);

    Sprint getNextSprint(Sprint sprint);

    Sprint getPreviousSprint(Sprint sprint);
}
