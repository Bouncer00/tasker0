package com.mbancer.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    public Project(){
        created = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "dead_line")
    private LocalDateTime deadLine;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "project_users",
               joinColumns = @JoinColumn(name="projects_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OrderBy("number DESC")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private List<Board> boards;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDateTime deadLine) {
        this.deadLine = deadLine;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) &&
            Objects.equals(shortName, project.shortName) &&
            Objects.equals(description, project.description) &&
            Objects.equals(created, project.created) &&
            Objects.equals(deadLine, project.deadLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, description, created, deadLine);
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", created='" + created + "'" +
            ", deadLine='" + deadLine + "'" +
            '}';
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String shortName;
        private String description;
        private LocalDateTime created;
        private LocalDateTime deadLine;
        private Set<User> users = new HashSet<>();
        private List<Task> tasks = new ArrayList<>();
        private List<Sprint> sprints = new ArrayList<>();
        private List<Board> boards = new ArrayList<>();

        private Builder() {
        }

        public static Builder aProject() {
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

        public Builder shortName(String shortName){
            this.shortName = shortName;
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

        public Builder deadLine(LocalDateTime deadLine) {
            this.deadLine = deadLine;
            return this;
        }

        public Builder users(Set<User> users) {
            this.users = users;
            return this;
        }

        public Builder tasks(List<Task> tasks){
            this.tasks = tasks;
            return this;
        }

        public Builder boards(List<Board> boards){
            this.boards = boards;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.setId(id);
            project.setName(name);
            project.setShortName(shortName);
            project.setDescription(description);
            project.setCreated(created);
            project.setDeadLine(deadLine);
            project.setUsers(users);
            project.setSprints(sprints);
            project.setTasks(tasks);
            project.setBoards(boards);
            return project;
        }
    }
}
