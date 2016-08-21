package com.mbancer.service;

import com.mbancer.Tasker0App;
import com.mbancer.domain.Project;
import com.mbancer.domain.Task;
import com.mbancer.domain.User;
import com.mbancer.repository.ProjectRepository;
import com.mbancer.repository.TaskRepository;
import com.mbancer.repository.UserRepository;
import com.mbancer.service.util.EntityGenerators;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Tasker0App.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class ProjectServiceIntTest {

    @Inject
    private ProjectService projectService;

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private UserRepository userRepository;

    @Test
    public void shouldAddTaskToProject(){
        //given
        final User user = userRepository.findOneByLogin("admin").get();
        final Project project = projectRepository.save(EntityGenerators.generateProject(Collections.singleton(user), null));
        final Task task = taskRepository.save(EntityGenerators.generateTask(user, project));

        //when
        projectService.addTaskToProject(project.getId(), task.getId());

        //then
        assertTrue(project.getTasks().contains(task));
    }
}
