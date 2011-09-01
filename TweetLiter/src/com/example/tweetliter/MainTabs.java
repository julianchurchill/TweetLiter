package com.example.tweetliter;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabs extends TabActivity
{
	private ViewPager tabPager = null;
	private TabPagerAdapter tabPagerAdapter = null;
	private List<String> tabNames = new ArrayList<String>();
	private Context context = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.tabs );

        getTabHost().getTabWidget().setDividerDrawable( R.drawable.divider_vertical_bright );

        addTab( HomeTimeLineActivity.class, "homeTimeLine", "Home Time line"  );
        addTab( UserTimeLineActivity.class, "userTimeLine", "User Time line" );

        getTabHost().setCurrentTab(0);

        tabPagerAdapter = new TabPagerAdapter();
        tabPager = (ViewPager)findViewById( R.id.tabpager );
        tabPager.setAdapter( tabPagerAdapter );
        context = this;
    }

    private void addTab( Class<?> classType, String specName, String displayName )
    {
	    TabHost.TabSpec spec = getTabHost().newTabSpec( specName );
	    spec.setIndicator( createTab( displayName ) );
	    spec.setContent( new Intent().setClass( this, classType ) );
	    getTabHost().addTab(spec);
	    tabNames.add( displayName );
    }
    
    private View createTab( final String text )
    {
	    View view = LayoutInflater.from( this ).inflate( R.layout.tabs_bg, null );
	    TextView tv = (TextView)view.findViewById( R.id.tabsText );
	    tv.setText( text );
	    return view;
	}
    
    private class TabPagerAdapter extends PagerAdapter
    {
    	private int NUM_VIEWS = 2;

    	@Override
    	public int getCount()
    	{
    		return NUM_VIEWS;
    	}

    	@Override
        public Object instantiateItem(View collection, int position)
    	{
            TextView tv = new TextView(context);
            tv.setText("Bonjour PAUG " + position);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(30);

//    		View tv = createTab( tabNames.get( position ) );
            ((ViewPager) collection).addView(tv,0);
            
            return tv;
        }

		@Override
		public void destroyItem(View collection, int position, Object view)
		{
			((ViewPager) collection).removeView((TextView)view);
		}

		@Override
		public void finishUpdate(View arg0)
		{
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == ((TextView)object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
		}
	}
}