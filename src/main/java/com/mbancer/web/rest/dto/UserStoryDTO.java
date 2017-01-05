package com.mbancer.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class UserStoryDTO {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime created;

    private LocalDateTime updated;

    private Long sprintId;

    private Long priority;

    private List<CommentDTO> comments;

    public UserStoryDTO() {
        created = LocalDateTime.now();
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStoryDTO that = (UserStoryDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated) &&
            Objects.equals(sprintId, that.sprintId) &&
            Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, created, updated, sprintId, priority);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime created;
        private LocalDateTime updated;
        private Long sprintId;

        private Builder() {
        }

        public static Builder anUserStoryDTO() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder created(LocalDateTime created) {
            this.created = created;
            return this;
        }

        public Builder updated(LocalDateTime updated) {
            this.updated = updated;
            return this;
        }

        public Builder sprintId(Long sprintId) {
            this.sprintId = sprintId;
            return this;
        }

        public UserStoryDTO build() {
            UserStoryDTO userStoryDTO = new UserStoryDTO();
            userStoryDTO.setId(id);
            userStoryDTO.setName(name);
            userStoryDTO.setDescription(description);
            userStoryDTO.setCreated(created);
            userStoryDTO.setUpdated(updated);
            userStoryDTO.setSprintId(sprintId);
            return userStoryDTO;
        }
    }
}
