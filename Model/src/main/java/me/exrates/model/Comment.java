package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by ajet on 03.11.2016.
 */
public class Comment {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime commentsTime;
    private User creator;
    private String comment;
    private boolean messageSent;
    private int id;
    private User user;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCommentsTime() {
        return commentsTime;
    }

    public void setCommentsTime(LocalDateTime commentsTime) {
        this.commentsTime = commentsTime;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User commentsCreator) {
        this.creator = commentsCreator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMessageSent() {
        return messageSent;
    }

    public void setMessageSent(boolean messageSent) {
        this.messageSent = messageSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment1 = (Comment) o;

        if (id != comment1.id) return false;
        if (messageSent != comment1.messageSent) return false;
        if (commentsTime != null ? !commentsTime.equals(comment1.commentsTime) : comment1.commentsTime != null)
            return false;
        if (creator != null ? !creator.equals(comment1.creator) : comment1.creator != null) return false;
        if (comment != null ? !comment.equals(comment1.comment) : comment1.comment != null) return false;
        return user != null ? user.equals(comment1.user) : comment1.user == null;

    }

    @Override
    public int hashCode() {
        int result = commentsTime != null ? commentsTime.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (messageSent ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentsTime=" + commentsTime +
                ", creator=" + creator +
                ", comment='" + comment + '\'' +
                ", id=" + id +
                ", user=" + user +
                ", messageSent=" + messageSent +
                '}';
    }
}
