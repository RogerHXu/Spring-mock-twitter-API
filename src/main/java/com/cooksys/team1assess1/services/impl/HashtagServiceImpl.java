package com.cooksys.team1assess1.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.cooksys.team1assess1.dtos.HashtagDto;
import com.cooksys.team1assess1.dtos.TweetResponseDto;
import com.cooksys.team1assess1.entities.Tweet;
import com.cooksys.team1assess1.exceptions.NotFoundException;
import com.cooksys.team1assess1.mappers.HashtagMapper;
import com.cooksys.team1assess1.mappers.TweetMapper;
import org.springframework.stereotype.Service;

import com.cooksys.team1assess1.entities.Hashtag;
import com.cooksys.team1assess1.repositories.HashtagRepository;
import com.cooksys.team1assess1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
	private final HashtagRepository hashtagRepository;
	private final TweetMapper tweetMapper;
	private final HashtagMapper hashtagMapper;
	
	@Override
	public List<HashtagDto> getTags() {
		return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
	}

	@Override
	public List<TweetResponseDto> getTweetsWithTag(String label) {
		Hashtag hashtag = hashtagRepository.findByLabel(label)
				.orElseThrow(() -> new NotFoundException("Hashtag not found."));
		return tweetMapper.entitiesToDtos(hashtag.getTweets());
	}

}
