package com.mbancer.web.rest.dto;

import java.util.Objects;

public class BoardDTO {

    public Long id;
    public String name;
    public Long projectId;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardDTO boardDTO = (BoardDTO) o;
        return id == boardDTO.id &&
            projectId == boardDTO.projectId &&
            Objects.equals(name, boardDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, projectId);
    }

    @Override
    public String toString() {
        return "BoardDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", projectId=" + projectId +
            '}';
    }
}
