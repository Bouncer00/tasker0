package com.mbancer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mbancer.config.Constants;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "user_story")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user_story")
public class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "priority")
    private Long priority;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "updated")
    private LocalDate updated;

    @ManyToOne
    private Sprint sprint;

    @OneToMany(mappedBy = "userStory")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OrderBy("number")
    @Cascade(CascadeType.DELETE)
    private List<Task> tasks;

    @OneToMany(mappedBy = "userStory")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    @OrderBy("date")
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @NotNull
    private Long number;

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

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStory userStory = (UserStory) o;
        return Objects.equals(id, userStory.id) &&
            Objects.equals(name, userStory.name) &&
            Objects.equals(description, userStory.description) &&
            Objects.equals(created, userStory.created) &&
            Objects.equals(updated, userStory.updated) &&
            Objects.equals(sprint, userStory.sprint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, created, updated, sprint);
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
        private Sprint sprint;
        private List<Task> tasks;
        private Long priority;

        private Builder() {
        }

        public static Builder anUserStory() {
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

        public Builder sprint(Sprint sprint) {
            this.sprint = sprint;
            return this;
        }

        public Builder tasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder priority(Long priority){
            this.priority = priority;
            return this;
        }

        public UserStory build() {
            UserStory userStory = new UserStory();
            userStory.setId(id);
            userStory.setName(name);
            userStory.setDescription(description);
            userStory.setCreated(created);
            userStory.setUpdated(updated);
            userStory.setSprint(sprint);
            userStory.setTasks(tasks);
            userStory.setPriority(priority);
            return userStory;
        }
    }
}
