package com.example.tweetliter;

import winterwell.jtwitter.Twitter;

public interface TwitterAuthorizationListener {

	public abstract void onTwitterAuthorization(Twitter twitter);

}