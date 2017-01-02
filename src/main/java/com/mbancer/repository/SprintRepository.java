package com.mbancer.repository;

import com.mbancer.domain.Sprint;
import com.mbancer.web.rest.dto.SprintDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long>{


    Page<Sprint> findAllByProjectIdIn(List<Long> longs, Pageable pageable);

}
