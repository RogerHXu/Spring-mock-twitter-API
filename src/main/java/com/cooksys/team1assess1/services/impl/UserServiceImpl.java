package com.cooksys.team1assess1.services.impl;

import com.cooksys.team1assess1.dtos.*;
import com.cooksys.team1assess1.entities.Credentials;
import com.cooksys.team1assess1.entities.Tweet;
import com.cooksys.team1assess1.entities.User;
import com.cooksys.team1assess1.exceptions.BadRequestException;
import com.cooksys.team1assess1.exceptions.NotFoundException;
import com.cooksys.team1assess1.mappers.CredentialsMapper;
import com.cooksys.team1assess1.mappers.TweetMapper;
import com.cooksys.team1assess1.mappers.UserMapper;
import com.cooksys.team1assess1.repositories.TweetRepository;
import com.cooksys.team1assess1.repositories.UserRepository;
import com.cooksys.team1assess1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	
	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final CredentialsMapper credentialsMapper;

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
		            .orElseThrow(() -> new NotFoundException("User not found or has been deleted."));
		List<User> authors = new ArrayList<>(user.getFollowing());
		authors.add(user);
		    
		return tweetMapper.entitiesToDtos(tweetRepository.findByAuthorInAndDeletedFalseOrderByPostedDesc(authors));
	}

	@Override
	public List<TweetResponseDto> getUserMentions(String username) {
		User user =  userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User not found or has been deleted."));
		return tweetMapper.entitiesToDtos(user.getMentionedBy());
	}


    @Override
    public UserResponseDto getUserByUsername(String username) {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User not found or has been deleted."));
        return userMapper.entityToDto(user);
    }

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.getAllNonDeletedUsers());
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) throws BadRequestException {

		CredentialsDto credentials = userRequestDto.getCredentials();

		ProfileDto profile = userRequestDto.getProfile();

		if (credentials == null || profile == null || credentials.getUsername() == null || credentials.getPassword() == null || profile.getEmail() == null) {
			throw new BadRequestException("missing required fields.");
		}

		String username = userRequestDto.getCredentials().getUsername();
		if (userRepository.existsByCredentialsUsername(username)) {
			User reactivatingUser = userRepository.findByCredentialsUsername(username);
			if(! reactivatingUser.isDeleted()) throw new BadRequestException("Username already taken.");
			else {
				reactivatingUser.setDeleted(false);
				userRepository.saveAndFlush(reactivatingUser);
				return userMapper.entityToDto(reactivatingUser);
			}
		}

		User user = userMapper.dtoToEntity(userRequestDto);
		userRepository.saveAndFlush(user);
		return userMapper.entityToDto(user);
	}

	@Override
	public UserResponseDto updateProfile(UserRequestDto userRequestDto, String username) throws BadRequestException, NotFoundException {

		User userToUpdate = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		User updatedUser = userMapper.dtoToEntity(userRequestDto);


		if (updatedUser.getProfile() == null) {
			throw new BadRequestException("User has no profile");
		}

		if (!userToUpdate.getCredentials().equals(updatedUser.getCredentials())) {
			throw new BadRequestException("Credentials don't match");
		}

		if (updatedUser.getProfile().getEmail() != null) {
			userToUpdate.getProfile().setEmail(updatedUser.getProfile().getEmail());
		}

		if (updatedUser.getProfile().getFirstName() != null) {
			userToUpdate.getProfile().setFirstName(updatedUser.getProfile().getFirstName());
		}

		if (updatedUser.getProfile().getLastName() != null) {
			userToUpdate.getProfile().setLastName(updatedUser.getProfile().getLastName());
		}

		if (updatedUser.getProfile().getPhone() != null) {
			userToUpdate.getProfile().setPhone(updatedUser.getProfile().getPhone());
		}

		userRepository.saveAndFlush(userToUpdate);

		return userMapper.entityToDto(userToUpdate);
	}

	@Override
	public UserResponseDto deleteUser(CredentialsDto credentialsDto, String username) throws BadRequestException, NotFoundException {

		User userToUpdate = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		User updatedUser = userRepository.findByCredentials(credentialsMapper.dtoToEntity(credentialsDto));
		if(updatedUser == null) throw new NotFoundException("User does not exist or is deleted.");
				//.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		if (!userToUpdate.getCredentials().equals(updatedUser.getCredentials())) {
			throw new BadRequestException("Credentials don't match");
		}

		userToUpdate.setDeleted(true);
//		userRepository.delete();
		userRepository.saveAndFlush(userToUpdate);

		return userMapper.entityToDto(userToUpdate);
	}

	@Override
	public List<UserResponseDto> getUserFollowers(String username) throws NotFoundException {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		List<User> followers = user.getFollowers();

		List<UserResponseDto> activeFollowers = new ArrayList<>();

		for (User follower : followers) {
			if (!follower.isDeleted()) {
				activeFollowers.add(userMapper.entityToDto(follower));
			}
		}
		return activeFollowers;
	}

	@Override
	public List<UserResponseDto> getUserFollowing(String username) throws NotFoundException {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		List<User> following = user.getFollowing();

		List<UserResponseDto> activeFollowing = new ArrayList<>();

		for (User follower : following) {
			if (!follower.isDeleted()) {
				activeFollowing.add(userMapper.entityToDto(follower));
			}
		}
		return activeFollowing;
	}

	@Override
	public List<TweetResponseDto> getUserTweets(String username) throws NotFoundException {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		List<Tweet> tweets = user.getTweets();

		List<TweetResponseDto> activeTweets = new ArrayList<>();

		for (int i = tweets.size() - 1; i >= 0; i--) {
			if (!tweets.get(i).isDeleted()) {
				activeTweets.add(tweetMapper.entityToDto(tweets.get(i)));
			}
		}

		return activeTweets;
	}

	@Override
	public void follow(Credentials credentials, String username) throws BadRequestException, NotFoundException {
		User follower = userRepository.findByCredentialsUsernameAndDeletedFalse(credentials.getUsername())
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		if (!follower.getCredentials().getPassword().equals(credentials.getPassword())) {
			throw new BadRequestException("Wrong password.");
		}
		User followee = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("The user you're trying to follow doesn't exist or is deleted."));

		if (followee.getFollowers().contains(follower)) {
			throw new BadRequestException("You're already following that user.");
		}

		follower.getFollowing().add(followee);

		userRepository.saveAndFlush(follower);
	}

	@Override
	public void unfollow(Credentials credentials, String username) throws BadRequestException, NotFoundException {
		User follower = userRepository.findByCredentialsUsernameAndDeletedFalse(credentials.getUsername())
				.orElseThrow(() -> new NotFoundException("User does not exist or is deleted."));

		if (!follower.getCredentials().getPassword().equals(credentials.getPassword())) {
			throw new BadRequestException("Wrong password.");
		}
		User followee = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
				.orElseThrow(() -> new NotFoundException("The user you're trying to follow doesn't exist or is deleted."));

		System.out.println(followee.getFollowers().contains(follower));
		if (!followee.getFollowers().contains(follower)) {
			throw new BadRequestException("You're not following that user.");
		}

		follower.getFollowing().remove(followee);

		userRepository.saveAndFlush(follower);
	}

}
