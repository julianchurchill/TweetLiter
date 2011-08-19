package com.example.tweetist;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class TwitterAuthorizer
{
	private static final String consumerKey = "ZeSnv7FEz0lmJx7TmYkBKA";
	private static final String consumerSecret = "alME0cAaheGUzmoTqyLtAp7NKJH2rnkMYLqAyYSe0mo";
	private static final String callbackScheme = "x-twoowowi-oauth-twitter";
	private static final String callbackURL = callbackScheme + "://callback";
	private static final String username = "twoowowi";

	private String accessToken = null;
	private String accessTokenSecret = null;
	private OAuthSignpostClient oauthClient = null;
	private OAuthProvider oauthProvider = null;
	private OAuthConsumer oauthConsumer = null;
	private Context context = null;
	private List<TwitterAuthorizationListener> authorizationListeners = new ArrayList<TwitterAuthorizationListener>();

	public TwitterAuthorizer( Context context )
	{
		this.context = context;
	}
	
	public void addListener( TwitterAuthorizationListener authorizationListener )
	{
		this.authorizationListeners.add( authorizationListener );
	}

    public Twitter loginToTwitter()
    {
    	Twitter retVal = null;
    	createOAuthObjects();
    	if( retrieveAccessTokenAndSecret() )
    	{
    		authorizeTwitterClient();
    	}
    	else
    	{
    		new RequestAccessTokensTask().execute();
    	}
    	return retVal;
    }

    public Boolean extractAccessToken( Uri uri )
    {
    	Boolean retVal = false;
    	if( uri != null && uri.getScheme().equals( callbackScheme ) )
    	{
			String verifier = uri.getQueryParameter( OAuth.OAUTH_VERIFIER );
			try {
				oauthProvider.retrieveAccessToken( oauthConsumer, verifier );
				saveAccessTokenAndSecret( oauthConsumer.getToken(), oauthConsumer.getTokenSecret() );
				retVal = true;
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
    	}
    	return retVal;
    }

    public void authorizeTwitterClient()
    {
		oauthConsumer.setTokenWithSecret( accessToken, accessTokenSecret );
		oauthClient = new OAuthSignpostClient( consumerKey, consumerSecret,
    			accessToken, accessTokenSecret );
		for( TwitterAuthorizationListener listener : authorizationListeners )
		{
			listener.onTwitterAuthorization( new Twitter( username, oauthClient ) );
		}
    }

    class RequestAccessTokensTask extends AsyncTask<Void, Void, String>
    {
    	@Override
        protected String doInBackground(Void... params) {
    		String message = null;
    		try {
    			String authUrl = oauthProvider.retrieveRequestToken( oauthConsumer, callbackURL );
    	    	Intent myIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( authUrl.toString() ) );
    	    	context.startActivity( myIntent );
    		} catch (OAuthMessageSignerException e) {
    			message = "OAuthMessageSignerException";
    			e.printStackTrace();
    		} catch (OAuthNotAuthorizedException e) {
    			message = "OAuthNotAuthorizedException";
    			e.printStackTrace();
    		} catch (OAuthExpectationFailedException e) {
    			message = "OAuthExpectationFailedException";
    			e.printStackTrace();
    		} catch (OAuthCommunicationException e) {
    			message = "OAuthCommunicationException";
    			e.printStackTrace();
    		}
    		return message;
    	}
    	
    	@Override
        protected void onPostExecute(String result)
    	{
    		super.onPostExecute( result );
    		if (result != null)
    		{
    			Toast.makeText( context, result, Toast.LENGTH_LONG ).show();
            }
        }
    }
    
    private void createOAuthObjects()
    {
    	oauthConsumer = new DefaultOAuthConsumer( consumerKey, consumerSecret );
    	oauthProvider = new DefaultOAuthProvider(
    	        "https://api.twitter.com/oauth/request_token",
    	        "https://api.twitter.com/oauth/access_token",
    	        "https://api.twitter.com/oauth/authorize");
    }

    private void saveAccessTokenAndSecret( String token, String secret )
    {
    	accessToken = token;
    	accessTokenSecret = secret;

    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
    	prefs.edit().putString("accessToken", accessToken).putString("accessTokenSecret", accessTokenSecret).commit();
    }

    private Boolean retrieveAccessTokenAndSecret()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
        accessToken = prefs.getString("accessToken", null);
        accessTokenSecret = prefs.getString("accessTokenSecret", null);
    	return (accessToken != null && accessTokenSecret != null);
    }
}