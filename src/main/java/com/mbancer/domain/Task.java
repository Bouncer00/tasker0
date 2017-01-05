package com.mbancer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    public Task(){
        created = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Long number;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    @ManyToOne
    private User user;

    @ManyToOne
    private User assignee;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Board board;

    @ManyToOne
    private UserStory userStory;

    @OneToMany(mappedBy = "task")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private Set<Comment> comments = new HashSet<>();

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public UserStory getUserStory() {
        return userStory;
    }

    public void setUserStory(UserStory userStory) {
        this.userStory = userStory;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) &&
            Objects.equals(description, task.description) &&
            Objects.equals(created, task.created) &&
            Objects.equals(updated, task.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, created, updated);
    }

    @Override
    public String toString() {
        return "Task{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", created='" + created + "'" +
            ", updated='" + updated + "'" +
            '}';
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime created;
        private LocalDateTime updated;
        private User user;
        private User assignee;
        private Project project;
        private Set<Comment> comments = new HashSet<>();
        private Board board;
        private UserStory userStory;
        private Long number;

        private Builder() {
        }

        public static Builder aTask() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
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

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder comments(Set<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public Builder board(Board board){
            this.board = board;
            return this;
        }

        public Builder number(Long number){
            this.number = number;
            return this;
        }

        public Builder userStory(UserStory userStory){
            this.userStory = userStory;
            return this;
        }

        public Builder assignee(User assignee){
            this.assignee = assignee;
            return this;
        }

        public Task build() {
            Task task = new Task();
            task.setId(id);
            task.setTitle(title);
            task.setDescription(description);
            task.setCreated(created);
            task.setUpdated(updated);
            task.setUser(user);
            task.setProject(project);
            task.setComments(comments);
            task.setBoard(board);
            task.setUserStory(userStory);
            task.setNumber(number);
            task.setAssignee(assignee);
            return task;
        }
    }
}
