package com.example.tweetliter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.Period;

import winterwell.jtwitter.Twitter;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public abstract class TimeLineActivity extends ListActivity
{
	public static final String tweetTitleKey = "tweetTitle";
	private static final String tweetTextKey = "text";

	private List< Map<String, String> > listRows = null;
	private List<Twitter.Status> statusList = null;
	TimeLineContextMenu timeLineContextMenu = new TimeLineContextMenu();

	protected abstract List<Twitter.Status> getTimeLine();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate( savedInstanceState );
	    timeLineContextMenu.setContext( getApplicationContext() );
	    registerForContextMenu( getListView() );
	    addItemClickNotifier();
	    updateTimeLine();
	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		timeLineContextMenu.onCreateContextMenu( menu, v,
				listRows.get( info.position ), statusList.get( info.position ) );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		return timeLineContextMenu.onContextItemSelected( item, statusList.get( info.position ) );
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
		int endIndex = findIndexAfterRetweetUsername( tweetText );
		if( endIndex != StringUtils.characterNotFound )
		{
			int userStartIndex = tweetText.indexOf( '@' ) + 1;
			String user = tweetText.substring( userStartIndex, endIndex );
			retweetCredit = " (rt @" + user + ")";
		}
		return retweetCredit;
	}

	private String removeRetweetCredit(String tweetText)
	{
		String originalTweetText = tweetText;
		int endIndex = findIndexAfterRetweetUsername( tweetText );
		if( endIndex != StringUtils.characterNotFound )
		{
			int startOfOriginalTweet = endIndex + 1;
			int nextNonWhiteSpace = StringUtils.findFirstCharacterNotIn( 
					StringUtils.whiteSpaceChars, tweetText, startOfOriginalTweet );
			originalTweetText = tweetText.substring( nextNonWhiteSpace );
		}
		return originalTweetText;
	}
	
	private int findIndexAfterRetweetUsername( String tweetText )
	{
		int endIndex = StringUtils.characterNotFound;
		if( isRetweet( tweetText ) )
		{
			int atSymbolIndex = tweetText.indexOf( '@' );
			if( atSymbolIndex != StringUtils.characterNotFound )
			{
				int userStartIndex = atSymbolIndex + 1;
				endIndex = StringUtils.findFirstCharacterNotIn( 
						StringUtils.alphaNumericChars, tweetText, userStartIndex );
			}
		}
		return endIndex;
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