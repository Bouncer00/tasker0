package com.mbancer.service.impl;

import com.mbancer.domain.UserStory;
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

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Service
public class UserStoryServiceImpl implements UserStoryService {

    private final Logger log = LoggerFactory.getLogger(UserStoryServiceImpl.class);

    @Inject
    private UserStoryMapper userStoryMapper;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private UserStorySearchRepository userStorySearchRepository;

    @Override
    public UserStoryDTO save(UserStoryDTO userStoryDTO) {
        log.debug("Request to save UserStory : {}", userStoryDTO);
        UserStory userStory = userStoryMapper.userStoryDTOToUserStory(userStoryDTO);
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
        userStoryRepository.delete(id);
        userStorySearchRepository.delete(id);
    }

    @Override
    public Page<UserStory> search(String query, Pageable pageable) {
        log.debug("Request for page of UserStories for query : {]", query);
        return userStorySearchRepository.search(queryStringQuery(query), pageable);
    }
}
