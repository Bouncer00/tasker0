package com.mbancer.service.impl;

import com.mbancer.domain.Sprint;
import com.mbancer.domain.Task;
import com.mbancer.domain.UserStory;
import com.mbancer.repository.CommentRepository;
import com.mbancer.repository.SprintRepository;
import com.mbancer.repository.UserStoryRepository;
import com.mbancer.repository.search.SprintSearchRepository;
import com.mbancer.repository.search.UserStorySearchRepository;
import com.mbancer.service.SprintService;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private SprintRepository sprintRepository;

    @Inject
    private SprintService sprintService;

    @Inject
    private CommentRepository commentRepository;

    @Override
    public UserStoryDTO save(UserStoryDTO userStoryDTO) {
        log.debug("Request to save UserStory : {}", userStoryDTO);
        UserStory userStory = userStoryMapper.userStoryDTOToUserStory(userStoryDTO);
        userStory.setNumber(getNextUserStoryNumber(userStoryDTO.getSprintId()));
        Sprint sprint = sprintRepository.findOne(userStoryDTO.getSprintId());
        sprint.getUserStories().add(userStory);
        userStory.setSprint(sprint);
        userStory = userStoryRepository.save(userStory);
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
        Sprint sprint = userStory.getSprint();
        List<UserStory> userStories = sprint.getUserStories().stream().sorted(Comparator.comparing(UserStory::getNumber)).collect(Collectors.toList());
        int userStoryToDeleteIndex = userStories.indexOf(userStory);
        if(userStoryToDeleteIndex != -1) {
            for (int i = userStoryToDeleteIndex; i < userStories.size(); i++) {
                userStories.get(i).setNumber(userStories.get(i).getNumber() - 1);
            }
            userStoryRepository.save(userStories);
        }
        userStory.getSprint().getUserStories().remove(userStory);
        userStory.getComments().forEach(commentRepository::delete);
        userStoryRepository.delete(id);
    }

    @Override
    public Page<UserStoryDTO> findBySprintId(Long sprintId, Pageable pageable) {
        log.debug("Request for page of UserStories by sprint : {}", sprintId);
        Page<UserStory> userStories = userStoryRepository.findAllBySprintIdOrderByNumber(sprintId, pageable);
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

    @Override
    public void moveUserStoryByIdUp(final Long userStoryId){
        final UserStory userStory = userStoryRepository.findOne(userStoryId);
        final UserStory previousUserStory = moveUserStoryUp(userStory);
        long userStoryNumber = userStory.getNumber();
        userStory.setNumber(previousUserStory.getNumber());
        previousUserStory.setNumber(userStoryNumber);
    }


    @Override
    public void moveUserStoryByIdDown(final Long userStoryId){
        final UserStory userStory = userStoryRepository.findOne(userStoryId);
        final UserStory nextUserStory = moveUserStoryDown(userStory);
        long userStoryNumber = userStory.getNumber();
        userStory.setNumber(nextUserStory.getNumber());
        nextUserStory.setNumber(userStoryNumber);
    }

    private UserStory moveUserStoryDown(UserStory userStory) {
        if(userStory.getNumber() == userStory.getSprint().getUserStories().size() - 1){
            Sprint sprint = sprintRepository.findOne(userStory.getSprint().getId());
            Sprint nextSprint = sprintService.getNextSprint(sprint);
            if(null != nextSprint && !sprint.equals(nextSprint)) {
                sprint.getUserStories().remove(userStory);
                for (UserStory usUserStory : nextSprint.getUserStories()) {
                    usUserStory.setNumber(usUserStory.getNumber() + 1);
                }
                userStory.setSprint(nextSprint);
                userStory.setNumber(0L);
                nextSprint.getUserStories().add(userStory);
            }
            return userStory;
        }
        Sprint sprint = sprintRepository.findOne(userStory.getSprint().getId());
        return userStoryRepository.findOneBySprintIdAndNumber(sprint.getId(), userStory.getNumber() + 1);
    }

    private UserStory moveUserStoryUp(UserStory userStory) {
        Sprint sprint = sprintRepository.findOne(userStory.getSprint().getId());
        if(userStory.getNumber() == 0){
            Sprint previousSprint = sprintService.getPreviousSprint(sprint);
            if(null != previousSprint) {
                userStory.setSprint(previousSprint);
                userStory.setNumber((long) previousSprint.getUserStories().size());
                sprint.getUserStories().remove(userStory);
                for (UserStory usUserStory : sprint.getUserStories()) {
                    usUserStory.setNumber(usUserStory.getNumber() - 1);
                }
                previousSprint.getUserStories().add(userStory);
            }
            return userStory;
        }

        return userStoryRepository.findOneBySprintIdAndNumber(sprint.getId(), userStory.getNumber() - 1);
    }

    private Long getNextUserStoryNumber(Long sprintId){
        Sprint sprint = sprintRepository.findOne(sprintId);
        return (long) sprint.getUserStories().size();
    }
}
