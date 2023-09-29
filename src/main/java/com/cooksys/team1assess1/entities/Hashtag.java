package com.cooksys.team1assess1.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
public class Hashtag {
    @Id
    @GeneratedValue
    private Long id;
    
    /*@Column(nullable = false, unique = true, columnDefinition = "citext")*/
    private String label;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp firstUsed;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp lastUsed;
    
    @ManyToMany
    @JoinTable(
            name = "tweet_hashtags",
            joinColumns = @JoinColumn(name = "hashtag_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id"))
    @JsonIgnore
    private List<Tweet> tweets;
}
