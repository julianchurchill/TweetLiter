package com.example.tweetliter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TweetActivity extends Activity
{
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tweet );
    }
    
    public void sendTweet( View view )
    {
    	EditText editText = (EditText)findViewById( R.id.TweetText );
    	String tweetText = editText.getText().toString();
    	MainActivity.myTwitter.setStatus( tweetText );
    	finish();
    }
}
