package com.mbancer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sprint")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sprint")
public class Sprint implements Serializable{

    private static final long serialVersionUID = 465204124150778990L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "number", nullable = false)
    private Long number;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "sprint_start", nullable = false)
    private LocalDate start = LocalDate.now();

    @Column(name = "sprint_end")
    private LocalDate end;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "sprint")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    @OrderBy("number")
    private Set<UserStory> userStories;

    @OneToMany(mappedBy = "sprint")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private Set<Comment> comments = new HashSet<>();

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setUserStories(Set<UserStory> userStories) {
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

    public Set<UserStory> getUserStories() {
        return userStories;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return Objects.equals(number, sprint.number) &&
            Objects.equals(name, sprint.name) &&
            Objects.equals(project, sprint.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, project);
    }

    public static Builder builder(){
        return new Builder();
    }


    public static final class Builder {
        private Long id;
        private Long number;
        private String name;
        private Project project;
        private Set<UserStory> userStories;
        private LocalDate start;
        private LocalDate end;

        private Builder() {
        }

        public static Builder aSprint() {
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

        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder userStories(Set<UserStory> userStories) {
            this.userStories = userStories;
            return this;
        }

        public Builder start(LocalDate start){
            this.start = start;
            return this;
        }

        public Builder end(LocalDate end){
            this.end = end;
            return this;
        }

        public Sprint build() {
            Sprint sprint = new Sprint();
            sprint.setId(id);
            sprint.setNumber(number);
            sprint.setName(name);
            sprint.setProject(project);
            sprint.setUserStories(userStories);
            sprint.setStart(start);
            sprint.setEnd(end);
            return sprint;
        }
    }
}
