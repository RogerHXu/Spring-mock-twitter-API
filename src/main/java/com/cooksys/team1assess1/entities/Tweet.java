package com.cooksys.team1assess1.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User author;

    @CreationTimestamp
    private Timestamp posted;

    private boolean deleted = false;

    private String content;

    @ManyToOne
    @JoinColumn(name = "tweet_id_reply")
    private Tweet inReplyTo;

    @OneToMany(mappedBy = "inReplyTo")
    private List<Tweet> RepliedBy;

    @ManyToOne
    @JoinColumn(name = "tweet_id_repost")
    private Tweet repostOf;

    @OneToMany(mappedBy = "repostOf")
    private List<Tweet> RepostTweets;


    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<User> likes;

    @ManyToMany
    @JoinTable(
            name = "user_mentions",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<User> mentions;

    @ManyToMany(mappedBy = "tweets")
    private List<Hashtag> hashtags;

}
