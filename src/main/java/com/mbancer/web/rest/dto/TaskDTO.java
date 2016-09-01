package com.mbancer.web.rest.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Task entity.
 */
public class TaskDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private LocalDate created;

    private LocalDate updated;

    private Long userId;

    private Long projectId;

    private Long userStoryId;

    private BoardDTO boardDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(Long userStoryId) {
        this.userStoryId = userStoryId;
    }

    public BoardDTO getBoardDTO() {
        return boardDTO;
    }

    public void setBoardDTO(BoardDTO boardDTO) {
        this.boardDTO = boardDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;

        if ( ! Objects.equals(id, taskDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", created='" + created + "'" +
            ", updated='" + updated + "'" +
            '}';
    }
}
