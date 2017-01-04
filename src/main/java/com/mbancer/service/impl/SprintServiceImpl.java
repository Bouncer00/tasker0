package com.mbancer.service.impl;

import com.mbancer.domain.Project;
import com.mbancer.domain.Sprint;
import com.mbancer.repository.CommentRepository;
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
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.Collections;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Service
@Transactional
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

    @Inject
    private CommentRepository commentRepository;

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
        Project project = projectRepository.findOne(sprintDTO.getProjectId());
        project.getSprints().add(sprint);
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);
        SprintDTO result = sprintMapper.sprintToSprintDTO(sprint);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Sprint> findAll(Pageable pageable) {
        log.debug("Request to get all Sprints");
        return sprintRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SprintDTO findOne(Long id) {
        log.debug("Request to get Sprint : {}", id);
        Sprint sprint = sprintRepository.findOne(id);
        return sprintMapper.sprintToSprintDTO(sprint);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sprint : {}", id);
        Sprint sprint = sprintRepository.findOne(id);
        sprint.getProject().getSprints().remove(sprint);
        sprint.getComments().forEach(commentRepository::delete);
        sprintRepository.delete(id);
        sprintSearchRepository.delete(id);
    }

    @Override
    public Page<Sprint> search(String query, Pageable pageable) {
        log.debug("Request for page of Sprints for query {}", query);
        return sprintSearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SprintDTO> findByProjectId(Long projectId, Pageable pageable) {
        log.debug("Request to find Sprints for Project : {}", projectId);
        Page<Sprint> sprints = sprintRepository.findAllByProjectIdIn(Collections.singletonList(projectId), pageable);
        return sprints.map(sprintMapper::sprintToSprintDTO);
    }

    @Override
    public Sprint getNextSprint(Sprint sprint) {
        if(sprint.getNumber() == sprint.getProject().getSprints().size() - 1) return sprint;
        return sprintRepository.findOneByProjectIdAndNumber(sprint.getProject().getId(), sprint.getNumber() + 1);
    }

    @Override
    public Sprint getPreviousSprint(Sprint sprint) {
        if(sprint.getNumber() == 0) return sprint;
        return sprintRepository.findOneByProjectIdAndNumber(sprint.getProject().getId(), sprint.getNumber() - 1);
    }

    private Long getNextSprintNumber(Long projectId){
        return (long) projectRepository.findOne(projectId).getSprints().size();
    }
}
