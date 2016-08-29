package com.mbancer.service.impl;

import com.mbancer.domain.Sprint;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.SprintRepository;
import com.mbancer.repository.search.SprintSearchRepository;
import com.mbancer.service.SprintService;
import com.mbancer.web.rest.dto.SprintDTO;
import com.mbancer.web.rest.mapper.SprintMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Service
public class SprintServiceImpl implements SprintService {

    private final Logger log = LoggerFactory.getLogger(SprintServiceImpl.class);

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private SprintSearchRepository sprintSearchRepository;

    @Inject
    private SprintMapper sprintMapper;

    @Inject
    private ProjectRepository projectRepository;

    @Override
    public SprintDTO save(SprintDTO sprintDTO) {
        log.debug("Request to save Sprint : {}", sprintDTO);
        if(sprintDTO.getNumber() == null) {
            sprintDTO.setNumber(getNextSprintNumber(sprintDTO.getProjectId()));
        }
        if(sprintDTO.getStart() == null){
            sprintDTO.setStart(LocalDate.now());
        }
        Sprint sprint = sprintMapper.sprintDTOToSprint(sprintDTO);
        sprint = sprintRepository.save(sprint);
        sprintSearchRepository.save(sprint);
        SprintDTO result = sprintMapper.sprintToSprintDTO(sprint);
        return result;
    }

    @Override
    public Page<Sprint> findAll(Pageable pageable) {
        log.debug("Request to get all Sprints");
        return sprintRepository.findAll(pageable);
    }

    @Override
    public SprintDTO findOne(Long id) {
        log.debug("Request to get Sprint : {}", id);
        Sprint sprint = sprintRepository.findOne(id);
        return sprintMapper.sprintToSprintDTO(sprint);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sprint : {}", id);
        sprintRepository.delete(id);
        sprintSearchRepository.delete(id);
    }

    @Override
    public Page<Sprint> search(String query, Pageable pageable) {
        log.debug("Request for page of Sprints for query {}", query);
        return sprintSearchRepository.search(queryStringQuery(query), pageable);
    }

    private Long getNextSprintNumber(Long projectId){
        return (long) projectRepository.findOne(projectId).getSprints().size();
    }
}
