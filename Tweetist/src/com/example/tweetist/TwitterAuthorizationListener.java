package com.example.tweetist;

import winterwell.jtwitter.Twitter;

public interface TwitterAuthorizationListener {

	public abstract void onTwitterAuthorization(Twitter twitter);

}