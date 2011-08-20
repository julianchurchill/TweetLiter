package com.example.tweetliter;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewProfileActivity extends Activity
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.viewprofile );

		Twitter.User user = MainActivity.myTwitter.getUser( 
				getIntent().getStringExtra( TweetLiterConstants.IntentExtraKey_UserName ) );
		setUserName( user.screenName );
		setDescription( user.description );
    }

    private void setUserName( String user )
    {
        TextView tv = (TextView)findViewById( R.id.viewProfileTextTitle );
        tv.setText( "@" + user );
    }
    
    private void setDescription( String description )
    {
        TextView tv = (TextView)findViewById( R.id.viewProfileText );
        tv.setText( description );
    }
}
