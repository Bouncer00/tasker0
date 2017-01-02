package com.mbancer.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Notification {

    private String authorFirstName;

    private String authorLastName;

    private LocalDate date;

    private String text;

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(authorFirstName, that.authorFirstName) &&
            Objects.equals(authorLastName, that.authorLastName) &&
            Objects.equals(date, that.date) &&
            Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorFirstName, authorLastName, date, text);
    }

    @Override
    public String toString() {
        return "Notification{" +
            "authorFirstName='" + authorFirstName + '\'' +
            ", authorLastName='" + authorLastName + '\'' +
            ", date=" + date +
            ", text='" + text + '\'' +
            '}';
    }
}
