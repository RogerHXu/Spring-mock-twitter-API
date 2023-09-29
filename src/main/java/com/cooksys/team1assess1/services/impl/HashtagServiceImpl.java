package com.cooksys.team1assess1.services.impl;

import java.util.List;

import com.cooksys.team1assess1.entities.Tweet;
import com.cooksys.team1assess1.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import com.cooksys.team1assess1.entities.Hashtag;
import com.cooksys.team1assess1.repositories.HashtagRepository;
import com.cooksys.team1assess1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
	private final HashtagRepository hashtagRepository;
	
	@Override
	public List<Hashtag> getTags() {
		return hashtagRepository.findAll();
	}

	@Override
	public List<Tweet> getTweetsWithTag(String label) {
		Hashtag hashtag = hashtagRepository.findByLabel(label)
				.orElseThrow(() -> new NotFoundException("Tweet not found."));
		return hashtag.getTweets();
	}

}
