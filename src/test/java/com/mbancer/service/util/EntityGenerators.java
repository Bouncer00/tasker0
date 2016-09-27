package com.mbancer.service.util;

import com.mbancer.domain.*;
import org.apache.commons.lang.RandomStringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EntityGenerators {

    public static Set<Task> generateTasks(final int number, final User user, final Project project, final UserStory userStory){
        final Set<Task> tasks = new HashSet<>();
        final Random random = new Random();
        for(int i = 0 ; i < number ; i++){
            tasks.add(
                Task.builder()
                    .user(user)
                    .created(LocalDate.now())
                    .description(RandomStringUtils.randomAlphabetic(10))
                    .project(project)
                    .userStory(userStory)
                    .number(random.nextLong())
                    .title(RandomStringUtils.randomAlphabetic(10))
                    .assignee(user)
                    .build()
            );
        }
        return tasks;
    }

    public static Set<Project> generateProjects(final int number, final Set<User> users, final List<Sprint> sprints){
        final Set<Project> projects = new HashSet<>();
        final Project.Builder builder = Project.builder();

        for(int i = 0 ; i < number ; i++){
            final Project project = builder
                .name(RandomStringUtils.randomAlphabetic(10))
                .description(RandomStringUtils.randomAlphabetic(10))
                .created(LocalDate.now())
                .shortName(RandomStringUtils.randomAlphabetic(4))
                .deadLine(LocalDate.now().plus(2, ChronoUnit.YEARS))
                .build();

            if(users != null){
                project.setUsers(users);
            }
            if(sprints != null){
                project.setSprints(sprints);
            }

            projects.add(project);
        }
        return projects;
    }

    public static Set<Comment> generateComments(final int number, final User author, final Task task){
        final Set<Comment> comments = new HashSet<>();
        final Comment.Builder builder = Comment.builder();
        for(int i = 0 ; i < number ; i++){
            final Comment comment =
                Comment.builder()
                    .date(LocalDate.now())
                    .text(RandomStringUtils.randomAlphabetic(10))
                    .date(LocalDate.now())
                    .build();
            if(author != null){
                comment.setAuthor(author);
            }
            if(task != null){
                comment.setTask(task);
            }
            comments.add(comment);
        }
        return comments;
    }

    public static Set<Sprint> generateSprints(final int number, final Project project){
        final Set<Sprint> sprints = new HashSet<>();
        final Random random = new Random();
        for(int i = 0 ; i < number ; i++){
            final Sprint sprint = Sprint.builder()
                .project(project)
                .name(RandomStringUtils.randomAlphabetic(10))
                .number(random.nextLong())
                .start(LocalDate.now().minus(10, ChronoUnit.DAYS))
                .end(LocalDate.now())
                .userStories(Collections.emptySet())
                .build();
            sprints.add(sprint);
        }
        return sprints;
    }

    public static Set<UserStory> generateUserStories(final int number, final Sprint sprint, final List<Task> tasks){
        final Set<UserStory> userStories = new HashSet<>();
        final Random random = new Random();
        for(int i = 0 ; i < number ; i++){
            final UserStory userStory = UserStory.builder()
                .created(LocalDate.now().minus(10, ChronoUnit.DAYS))
                .updated(LocalDate.now())
                .description(RandomStringUtils.randomAlphabetic(10))
                .name(RandomStringUtils.randomAlphabetic(5))
                .priority(random.nextLong())
                .sprint(sprint)
                .tasks(tasks)
                .build();
            userStories.add(userStory);
        }
        return userStories;
    }

    public static Task generateTask(final User user, final Project project, final UserStory userStory){
        return generateTasks(1, user, project, userStory).iterator().next();
    }

    public static Project generateProject(final Set<User> users, final List<Sprint> sprints){
        return generateProjects(1, users, sprints).iterator().next();
    }

    public static Comment generateComment(final User author, final Task task){
        return generateComments(1, author, task).iterator().next();
    }

    public static Sprint generateSprint(final Project project){
        return generateSprints(1, project).iterator().next();
    }

    public static UserStory generateUserStory(final Sprint sprint, final List<Task> tasks){
        return generateUserStories(1, sprint, tasks).iterator().next();
    }
}
