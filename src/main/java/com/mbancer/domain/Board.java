package com.mbancer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "board")
public class Board implements Serializable{

    private static final long serialVersionUID = 389994743626120442L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    @Length(max = 255)
    private String name;

    private Long number;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "board")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//    @OrderBy("priority ASC")
    private List<Task> tasks = new ArrayList<>();

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Board{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", number=" + number +
            '}';
    }

    public static class Builder {
        private Long id;
        private String name;
        private Long number;
        private Project project;
        private List<Task> tasks = new ArrayList<>();

        private Builder() {
        }

        public static Builder aBoard() {
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

        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder tasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder number(final Long number){
            this.number = number;
            return this;
        }

        public Board build() {
            Board board = new Board();
            board.setId(id);
            board.setName(name);
            board.setNumber(number);
            board.setProject(project);
            board.setTasks(tasks);
            return board;
        }
    }
}
