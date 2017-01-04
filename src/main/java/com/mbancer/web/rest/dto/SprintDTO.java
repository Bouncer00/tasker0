package com.mbancer.web.rest.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SprintDTO {

    private Long id;

    private Long number;

    private String name;

    private Long projectId;

    private LocalDate start;

    private LocalDate end;

    private Set<UserStoryDTO> userStories = new HashSet<>();

    private List<CommentDTO> comments;

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<UserStoryDTO> getUserStories() {
        return userStories;
    }

    public void setUserStories(Set<UserStoryDTO> userStories) {
        this.userStories = userStories;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SprintDTO sprintDTO = (SprintDTO) o;
        return Objects.equals(id, sprintDTO.id) &&
            Objects.equals(number, sprintDTO.number) &&
            Objects.equals(name, sprintDTO.name) &&
            Objects.equals(projectId, sprintDTO.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, name, projectId);
    }

    public static Builder builder(){
        return new Builder();
    }


    public static final class Builder {
        private Long id;
        private Long number;
        private String name;
        private Long projectId;
        private Set<UserStoryDTO> tasks = new HashSet<>();
        private LocalDate start;
        private LocalDate end;

        private Builder() {
        }

        public static Builder aSprintDTO() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder number(Long number) {
            this.number = number;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder project(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder tasks(Set<UserStoryDTO> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder start(LocalDate start) {
            this.start = start;
            return this;
        }

        public Builder end(LocalDate end){
            this.end = end;
            return this;
        }

        public SprintDTO build() {
            SprintDTO sprintDTO = new SprintDTO();
            sprintDTO.setId(id);
            sprintDTO.setNumber(number);
            sprintDTO.setName(name);
            sprintDTO.setProjectId(projectId);
            sprintDTO.setUserStories(tasks);
            sprintDTO.setStart(start);
            sprintDTO.setEnd(end);
            return sprintDTO;
        }
    }
}
