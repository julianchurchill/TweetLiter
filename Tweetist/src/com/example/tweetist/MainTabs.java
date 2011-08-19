package com.example.tweetist;

import com.example.tweetist.R;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabs extends TabActivity
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.tabs );

        getTabHost().getTabWidget().setDividerDrawable( R.drawable.divider_vertical_bright );

        addTab( HomeTimeLineActivity.class, "homeTimeLine", "Home Time line"  );
        addTab( UserTimeLineActivity.class, "userTimeLine", "User Time line" );

        getTabHost().setCurrentTab(0);
    }

    private void addTab( Class<?> classType, String specName, String displayName )
    {
	    TabHost.TabSpec spec = getTabHost().newTabSpec( specName );
	    spec.setIndicator( createTabView( this, displayName ) );
	    spec.setContent( new Intent().setClass( this, classType ) );
	    getTabHost().addTab(spec);
    }
    
    private static View createTabView(final Context context, final String text)
    {
	    View view = LayoutInflater.from( context ).inflate( R.layout.tabs_bg, null );
	    TextView tv = (TextView)view.findViewById( R.id.tabsText );
	    tv.setText( text );
	    return view;
	}
}