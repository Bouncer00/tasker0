package com.mbancer.service.impl;

import com.mbancer.domain.Sprint;
import com.mbancer.domain.UserStory;
import com.mbancer.repository.SprintRepository;
import com.mbancer.repository.UserStoryRepository;
import com.mbancer.repository.search.UserStorySearchRepository;
import com.mbancer.service.UserStoryService;
import com.mbancer.web.rest.dto.UserStoryDTO;
import com.mbancer.web.rest.mapper.UserStoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Service
@Transactional
public class UserStoryServiceImpl implements UserStoryService {

    private final Logger log = LoggerFactory.getLogger(UserStoryServiceImpl.class);

    @Inject
    private UserStoryMapper userStoryMapper;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private UserStorySearchRepository userStorySearchRepository;

    @Inject
    private SprintRepository sprintRepository;

    @Override
    public UserStoryDTO save(UserStoryDTO userStoryDTO) {
        log.debug("Request to save UserStory : {}", userStoryDTO);
        UserStory userStory = userStoryMapper.userStoryDTOToUserStory(userStoryDTO);
        userStory.setNumber(getNextUserStoryNumber(userStoryDTO.getSprintId()));
        userStory = userStoryRepository.save(userStory);
        userStorySearchRepository.save(userStory);
        return userStoryMapper.userStoryToUserStoryDTO(userStory);
    }

    @Override
    public Page<UserStory> findAll(Pageable pageable) {
        log.debug("Request to get all UserStories");
        return userStoryRepository.findAll(pageable);
    }

    @Override
    public UserStoryDTO findOne(Long id) {
        log.debug("Request to get UserStory : {}", id);
        UserStory userStory = userStoryRepository.findOne(id);
        return userStoryMapper.userStoryToUserStoryDTO(userStory);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserStory : {]", id);
        UserStory userStory = userStoryRepository.findOne(id);
        userStory.getSprint().getUserStories().remove(userStory);
        userStoryRepository.delete(id);
        userStorySearchRepository.delete(id);
    }

    @Override
    public Page<UserStory> search(String query, Pageable pageable) {
        log.debug("Request for page of UserStories for query : {]", query);
        return userStorySearchRepository.search(queryStringQuery(query), pageable);
    }

    @Override
    public Page<UserStoryDTO> findBySprintId(Long sprintId, Pageable pageable) {
        log.debug("Request for page of UserStories by sprint : {}", sprintId);
        Page<UserStory> userStories = userStoryRepository.findOneBySprintId(sprintId, pageable);
        return userStories.map(userStoryMapper::userStoryToUserStoryDTO);
    }

    @Override
    public UserStory getNextUserStory(UserStory userStory) {
        if(userStory.getNumber() == userStory.getSprint().getUserStories().size() - 1) return userStory;
        return userStoryRepository.findOneBySprintIdAndNumber(userStory.getSprint().getId(), userStory.getNumber() + 1);
    }

    @Override
    public UserStory getPreviousUserStory(UserStory userStory) {
        if(userStory.getNumber() == 0) return userStory;
        return userStoryRepository.findOneBySprintIdAndNumber(userStory.getSprint().getId(), userStory.getNumber() - 1);
    }

    private Long getNextUserStoryNumber(Long sprintId){
        Sprint sprint = sprintRepository.findOne(sprintId);
        return (long) sprint.getUserStories().size();
    }
}
