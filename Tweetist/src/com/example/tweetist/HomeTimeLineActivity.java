package com.example.tweetist;

import java.util.List;

import winterwell.jtwitter.Twitter;

public class HomeTimeLineActivity extends TimeLineActivity
{
	@Override
	protected List<Twitter.Status> getTimeLine()
    {
    	return MainActivity.myTwitter.getHomeTimeline();
    }
}