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

        String initialText = getIntent().getStringExtra( TweetLiterConstants.IntentExtraKey_RetweetText );
    	getEditText().setText( initialText );
    }

    private EditText getEditText()
    {
    	return (EditText)findViewById( R.id.TweetText );
    }
    
    public void sendTweet( View view )
    {
    	MainActivity.myTwitter.setStatus( getEditText().getText().toString() );
    	finish();
    }
}
