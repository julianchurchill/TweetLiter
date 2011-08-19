package com.example.tweetliter;

public class SplashScreenThread extends Thread
{
	private SplashScreenListener listener = null;

	public SplashScreenThread( SplashScreenListener listener )
	{
		super();
		this.listener = listener;
	}

	@Override
    public void run()
    {
       try
       {
    	   waitForNSeconds( 3 );
    	   // Wait for another 5s or until twitter has registered
           int waited = 0;
           while( waited < 5000 && MainActivity.myTwitter == null )
           {
        	   sleep( 100 );
               waited += 100;
           }
       }
       catch (InterruptedException e)
       {
       }
       listener.onSplashScreenFinished();
    }
	
	private void waitForNSeconds( int seconds ) throws InterruptedException
	{
        int waited = 0;
        final int maxMilliseconds = seconds * 1000;
        while( waited < maxMilliseconds )
        {
           sleep( 100 );
           waited += 100;
        }
	}
}
