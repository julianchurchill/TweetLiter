package com.example.tweetliter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.Period;

import com.example.tweetliter.R;

import winterwell.jtwitter.Twitter;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public abstract class TimeLineActivity extends ListActivity {

	private static final String retweetEndTag = ": ";
	private static final String tweetTitleKey = "tweetTitle";
	private static final String tweetTextKey = "text";
	private static final int retweetContextMenuID = 0;
	private static final int favouriteContextMenuID = 1;
	private static final int profileContextMenuID = 1000;
	private static final int profileContextMenuOrder = 1000;
	private static final int characterNotFound = -1;
	private static final String alphaNumericSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private List< Map<String, String> > listRows = null;
	private List<Twitter.Status> statusList = null;
	Map<Integer, String> profileContextMenuItems = new HashMap<Integer, String>();

	protected abstract List<Twitter.Status> getTimeLine();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate( savedInstanceState );
	    registerForContextMenu( getListView() );
	    addItemClickNotifier();
	    updateTimeLine();
	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    Map<String, String> row = listRows.get( info.position );
	    menu.setHeaderTitle( row.get( tweetTitleKey ) );
	    menu.add( Menu.NONE, retweetContextMenuID, 0, "Retweet" );
	    menu.add( Menu.NONE, favouriteContextMenuID, 1, "Favourite" );
	    addTweetMentions( menu, statusList.get( info.position ).text );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	    if( item.getItemId() == retweetContextMenuID )
	    {
			Toast.makeText( getApplicationContext(), "Retweeting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.retweet( statusList.get( info.position ) );
	    }
	    else if( item.getItemId() == favouriteContextMenuID )
	    {
			Toast.makeText( getApplicationContext(), "Favouriting...", Toast.LENGTH_SHORT ).show();
			MainActivity.myTwitter.setFavorite( statusList.get( info.position ), true );
	    }
	    else if( item.getItemId() >= profileContextMenuID )
	    {
			Toast.makeText( getApplicationContext(), "Profile...", Toast.LENGTH_SHORT ).show();
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

	private void addItemClickNotifier()
	{
	    ListView lv = getListView();
	    lv.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				updateTimeLine();
			}
		});
	}

	private void updateTimeLine()
	{
		Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
		listRows = extractStatusFields( statusList = getTimeLine() );
		String[] from = { tweetTitleKey, tweetTextKey };
		int[] to = { R.id.text1, R.id.text2 };
		setListAdapter( new SimpleAdapter( this, listRows, R.layout.list_item, from, to ) );
	}

	private List< Map<String, String> > extractStatusFields(List< Twitter.Status > timeline)
	{
		List< Map<String, String> > items = new ArrayList< Map<String, String> >();
		for( Twitter.Status status : timeline )
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put( tweetTitleKey, createTweetTitle( status ) );
			map.put( tweetTextKey, removeRetweetCredit( status.getText() ) );
			items.add( map );
		}
		return items;
	}

	private String createTweetTitle(Twitter.Status status)
	{
		String retweetedFrom = createRetweetUserCredit( status.getText() );
		String timeSinceTweet = getTimeSinceTweet( status.createdAt ) + " ago";
		return "@" + status.user.screenName + retweetedFrom + timeSinceTweet;
	}

	private String createRetweetUserCredit(String tweetText)
	{
		String retweetCredit = "";
		if( isRetweet( tweetText ) )
		{
			String user = tweetText.substring( tweetText.indexOf( '@' ), tweetText.indexOf( retweetEndTag ) );
			retweetCredit = " (rt " + user + ")";
		}
		return retweetCredit;
	}

	private String removeRetweetCredit(String tweetText)
	{
		String editedTweetText = tweetText;
		if( isRetweet( tweetText ) )
		{
			editedTweetText = tweetText.substring( tweetText.indexOf( retweetEndTag ) + retweetEndTag.length() );
		}
		return editedTweetText;
	}

	private Boolean isRetweet(String tweetText)
	{
		return tweetText.startsWith( "RT" );
	}

	private String getTimeSinceTweet(Date tweetDate)
	{
		Period period = new Period( new Instant( tweetDate.getTime() ), new Instant() );
		String value = period.getSeconds() + "s";;
		if( period.getYears() > 0 )
		{
			value = period.getYears() + "y";
		}
		else if( period.getDays() > 0 )
		{
			value = period.getDays() + "d";
		}
		else if( period.getHours() > 0 )
		{
			value = period.getHours() + "h";
		}
		else if( period.getMinutes() > 0 )
		{
			value = period.getMinutes() + "m";
		}
		return " " + value;
	}
}