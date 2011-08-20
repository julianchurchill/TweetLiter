package com.example.tweetliter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Twitter;
import android.content.Context;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TimeLineContextMenu
{
	private static final int retweetContextMenuID = 0;
	private static final int favouriteContextMenuID = 1;
	private static final int profileContextMenuID = 1000;
	private static final int profileContextMenuOrder = 1000;
	private static final int characterNotFound = -1;
	private static final String alphaNumericSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public Map<Integer, String> profileContextMenuItems = new HashMap<Integer, String>();
	private Context applicationContext = null;

	public TimeLineContextMenu( Context applicationContext )
	{
		this.applicationContext = applicationContext;
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v, Map<String, String> row, Twitter.Status tweet )
	{
	    menu.setHeaderTitle( row.get( TimeLineActivity.tweetTitleKey ) );
	    menu.add( Menu.NONE, retweetContextMenuID, 0, "Retweet" );
	    menu.add( Menu.NONE, favouriteContextMenuID, 1, "Favourite" );
	    addTweetMentions( menu, tweet.text );
	}

	public boolean onContextItemSelected( MenuItem item, Twitter.Status tweet )
	{
	    if( item.getItemId() == retweetContextMenuID )
	    {
			Toast.makeText( applicationContext, "Retweeting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.retweet( tweet );
	    }
	    else if( item.getItemId() == favouriteContextMenuID )
	    {
			Toast.makeText( applicationContext, "Favouriting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.setFavorite( tweet, true );
	    }
	    else if( item.getItemId() >= profileContextMenuID )
	    {
			Toast.makeText( applicationContext, "Profile...", Toast.LENGTH_SHORT ).show();
	    }
	    return true;
	}

	private void addTweetMentions( ContextMenu menu, String tweet )
	{
		int id = profileContextMenuID;
		for( String user : extractUsers( tweet ) )
		{
			profileContextMenuItems.put( id, user );
			menu.add( Menu.NONE, id, profileContextMenuOrder, "Profile " + user );
			id++;
		}
	}

	private List<String> extractUsers( String tweet )
	{
		List<String> users = new ArrayList<String>();
		int startIndex = tweet.indexOf( '@' );
		while( startIndex != characterNotFound )
		{
			int endIndex = findOnePastEndOfUser( tweet, startIndex );
			if( endIndex == characterNotFound )
			{
				endIndex = tweet.length();
			}
			users.add( tweet.substring( startIndex, endIndex ) );
			startIndex = tweet.indexOf( '@', endIndex );
		}
		return users;
	}

	private int findOnePastEndOfUser( String tweet, int userAtSymbolIndex )
	{
		int index = -1;
		int userNameStartIndex = userAtSymbolIndex + 1;
		for( int i = userNameStartIndex; i < tweet.length(); i++ )
		{
			if( alphaNumericSet.indexOf( tweet.charAt( i ) ) == characterNotFound )
			{
				index = i;
				break;
			}
		}
		return index;
	}
}