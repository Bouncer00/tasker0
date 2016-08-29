package com.mbancer.web.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

public class UserStoryDTO {

    private Long id;

    private String name;

    private String description;

    private LocalDate created;

    private LocalDate updated;

    private Long sprintId;

    private Long priority;

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

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDate updated) {
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
        private LocalDate created;
        private LocalDate updated;
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

        public Builder created(LocalDate created) {
            this.created = created;
            return this;
        }

        public Builder updated(LocalDate updated) {
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
