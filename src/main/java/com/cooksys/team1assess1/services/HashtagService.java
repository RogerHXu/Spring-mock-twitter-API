package com.cooksys.team1assess1.services;

import java.util.List;

import com.cooksys.team1assess1.dtos.HashtagDto;
import com.cooksys.team1assess1.dtos.TweetResponseDto;
import com.cooksys.team1assess1.entities.Hashtag;
import com.cooksys.team1assess1.entities.Tweet;

public interface HashtagService {

	List<HashtagDto> getTags();

	List<TweetResponseDto> getTweetsWithTag(String label);

}
