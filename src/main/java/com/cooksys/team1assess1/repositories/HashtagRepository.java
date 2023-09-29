package com.cooksys.team1assess1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.team1assess1.entities.Hashtag;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	boolean existsByLabel(String label);

	Optional<Hashtag> findByLabel(String label);
}
