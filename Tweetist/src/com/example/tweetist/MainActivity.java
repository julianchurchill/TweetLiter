package com.example.tweetist;

import com.example.tweetist.R;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity implements TwitterAuthorizationListener, SplashScreenListener
{
	public static Twitter myTwitter = null;
	private TwitterAuthorizer twitterAuthorizer = null;
	private Boolean splashScreenFinished = false;

	/** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
        setContentView( R.layout.splash );
        setProgressBarIndeterminateVisibility( true );
        startSplashScreenThread();
        
        showDialog(0);

        twitterAuthorizer = new TwitterAuthorizer( this );
        twitterAuthorizer.addListener( this );
        twitterAuthorizer.loginToTwitter();
    }
    
    @Override
    protected Dialog onCreateDialog( int id )
    {
	    ProgressDialog dialog = new ProgressDialog( this );
	    dialog.setMessage( "Loading..." );
	    dialog.setIndeterminate( true );
	    dialog.setCancelable( false );
	    return dialog;
    }

    /** Called for new intents - we check for our OAuth call back intent */
    @Override
    public void onNewIntent( Intent intent )
    {
    	super.onNewIntent( intent );
    	
    	Uri uri = intent.getData();
    	if( twitterAuthorizer.extractAccessToken( uri ) )
    	{
    		twitterAuthorizer.authorizeTwitterClient();
    	}
    }
    
    public void onTwitterAuthorization( Twitter twitter )
    {
    	synchronized( this )
    	{
	    	MainActivity.myTwitter = twitter;
	    	if( splashScreenFinished )
	    	{
	    		startTabsActivity();
			}
    	}
    }
    
    public void onSplashScreenFinished()
    {
    	synchronized( this )
    	{
	    	splashScreenFinished = true;
	        if( MainActivity.myTwitter != null )
	        {
	        	startTabsActivity();
	        }
    	}
    }
    
    private void startTabsActivity()
    {
        finish();
        Intent intent = new Intent().setClass( getApplicationContext(), MainTabs.class );
    	startActivity( intent );
    }

    private void startSplashScreenThread()
    {
        new SplashScreenThread( this ).start();
    }
}