package com.mbancer.service;

import com.mbancer.Tasker0App;
import com.mbancer.domain.*;
import com.mbancer.repository.*;
import com.mbancer.service.util.EntityGenerators;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.swing.text.html.parser.Entity;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class TaskServiceIntTest {

    @Inject
    private TaskService taskService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private UserStoryRepository userStoryRepository;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private CommentRepository commentRepository;

    @Test
    public void shouldAddCommentToTask(){
        //given
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = projectRepository.save(EntityGenerators.generateProject(Collections.singleton(user), null));
        final Sprint sprint = sprintRepository.save(EntityGenerators.generateSprint(project));
        final UserStory userStory = userStoryRepository.save(EntityGenerators.generateUserStory(sprint, Collections.emptyList()));
        final Task task = taskRepository.save(EntityGenerators.generateTask(user, project, userStory));
        final Comment comment = commentRepository.save(EntityGenerators.generateComment(user, task));

        //when
        taskService.addCommentToTask(task.getId(), comment.getId());

        //then
        assertTrue(task.getComments().contains(comment));
    }
}
