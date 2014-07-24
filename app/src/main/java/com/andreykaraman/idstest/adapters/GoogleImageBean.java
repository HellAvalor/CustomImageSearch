package com.andreykaraman.idstest.adapters;

public class GoogleImageBean 
{
	String thumbUrl;
	String title;
	String fullUrl;

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}


	public String getThumbUrl()
	{
		return thumbUrl;
	}
	
	public void setThumbUrl(String url) 
	{
		this.thumbUrl = url;
	}
	
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
}