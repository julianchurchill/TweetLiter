package com.example.tweetliter;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewProfileActivity extends Activity
{
	private static final int UpdatingDialogID = 0;

	private Twitter.User user = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.viewprofile );

        updateUser( getIntent().getStringExtra( TweetLiterConstants.IntentExtraKey_UserName ) );
    }

    private void updateUser( String username )
    {
		user = MainActivity.myTwitter.getUser( username );
		setUserName();
		setDescription();
		setFollowButtonText();
    }

    public void toggleFollow( View view )
    {
		showDialog( UpdatingDialogID );
    	if( user.isFollowedByYou() )
    	{
    		MainActivity.myTwitter.stopFollowing( user );
    	}
    	else
    	{
			MainActivity.myTwitter.follow( user );
    	}
    	updateUser( user.screenName );
		dismissDialog( UpdatingDialogID );
    }

    @Override
    protected Dialog onCreateDialog( int id )
    {
	    ProgressDialog dialog = new ProgressDialog( this );
	    dialog.setMessage( getString( R.string.Updating ) );
	    dialog.setIndeterminate( true );
	    dialog.setCancelable( false );
	    return dialog;
    }

    private void setFollowButtonText()
    {
		Button button = (Button)findViewById( R.id.FollowButton );
		int stringResId = R.string.Follow;
		if( user.isFollowedByYou() )
		{
			stringResId = R.string.Unfollow;
		}
		button.setText( getString( stringResId ) );
    }

    private void setUserName()
    {
        TextView tv = (TextView)findViewById( R.id.viewProfileTextTitle );
        tv.setText( "@" + user.screenName );
    }
    
    private void setDescription()
    {
        TextView tv = (TextView)findViewById( R.id.viewProfileText );
        tv.setText( user.description );
    }
}
