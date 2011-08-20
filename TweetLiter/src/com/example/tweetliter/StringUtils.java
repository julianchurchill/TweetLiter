package com.example.tweetliter;

public class StringUtils
{
	public static final int characterNotFound = -1;
	public static final String alphaNumericChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final String whiteSpaceChars = " \t\n\r";
	
	public static int findFirstCharacterNotIn( String charsToFind, String text, int startIndex )
	{
		int index = -1;
		for( int i = startIndex; i < text.length(); i++ )
		{
			if( charsToFind.indexOf( text.charAt( i ) ) == characterNotFound )
			{
				index = i;
				break;
			}
		}
		return index;
	}
}
