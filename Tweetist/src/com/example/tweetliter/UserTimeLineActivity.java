package com.example.tweetliter;

import java.util.List;

import winterwell.jtwitter.Twitter;

public class UserTimeLineActivity extends TimeLineActivity
{
	@Override
	protected List<Twitter.Status> getTimeLine()
    {
    	return MainActivity.myTwitter.getUserTimeline();
    }
}