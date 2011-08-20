package com.example.tweetliter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Twitter;
import android.content.Context;
import android.content.Intent;
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

	public Map<Integer, String> profileContextMenuItems = new HashMap<Integer, String>();
	private Context context = null;

	public TimeLineContextMenu()
	{
	}
	
	public void setContext( Context context )
	{
		this.context = context;
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v, Map<String, String> row, Twitter.Status tweet )
	{
	    menu.setHeaderTitle( row.get( TimeLineActivity.tweetTitleKey ) );
	    menu.add( Menu.NONE, retweetContextMenuID, 0, "Retweet" );
	    menu.add( Menu.NONE, favouriteContextMenuID, 1, "Favourite" );
	    addTweetMentions( menu, tweet.text );
	}

	public Intent onContextItemSelected( MenuItem item, Twitter.Status tweet )
	{
		Intent intent = null;
	    if( item.getItemId() == retweetContextMenuID )
	    {
			Toast.makeText( context, "Retweeting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.retweet( tweet );
	    }
	    else if( item.getItemId() == favouriteContextMenuID )
	    {
			Toast.makeText( context, "Favouriting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.setFavorite( tweet, true );
	    }
	    else if( item.getItemId() >= profileContextMenuID )
	    {
			Toast.makeText( context, "Viewing profile...", Toast.LENGTH_SHORT ).show();
	        intent = new Intent().setClass( context, ViewProfileActivity.class );
			intent.putExtra( TweetLiterConstants.IntentExtraKey_UserName, profileContextMenuItems.get( item.getItemId() ) );
	    }
	    return intent;
	}

	private void addTweetMentions( ContextMenu menu, String tweet )
	{
		int id = profileContextMenuID;
		for( String user : extractUsers( tweet ) )
		{
			profileContextMenuItems.put( id, user );
			menu.add( Menu.NONE, id, profileContextMenuOrder, "Profile @" + user );
			id++;
		}
	}

	private List<String> extractUsers( String tweet )
	{
		List<String> users = new ArrayList<String>();
		int atSymbolIndex = tweet.indexOf( '@' );
		while( atSymbolIndex != StringUtils.characterNotFound )
		{
			int userStartIndex = atSymbolIndex + 1;
			int endIndex = StringUtils.findFirstCharacterNotIn( StringUtils.alphaNumericChars, tweet, userStartIndex );
			if( endIndex == StringUtils.characterNotFound )
			{
				endIndex = tweet.length();
			}
			users.add( tweet.substring( userStartIndex, endIndex ) );
			atSymbolIndex = tweet.indexOf( '@', endIndex );
		}
		return users;
	}
}