package com.unbounce.scraper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unbounce.scraper.provided.database.DatabaseClient;
import com.unbounce.scraper.provided.eventbroadcast.EventBroadcaster;
import com.unbounce.scraper.provided.htmlparser.Document;
import com.unbounce.scraper.provided.htmlparser.SimpleHtmlParser;
import com.unbounce.scraper.provided.httpclient.SimpleHttpClient;
import com.unbounce.scraper.provided.jsontool.JsonMapParser;
import com.unbounce.scraper.provided.jsontool.JsonMapWriter;

public class UrlSizeCounterService {
	 public long getUrlSize(final String message) {
		long size = 0;
		 
		JsonMapParser jParser = new JsonMapParser();
	    Map<String, Object> jsonMap = jParser.parseJson(message);         
	        
        String urlStr = (String) jsonMap.get("url");
        String urlID = (String) jsonMap.get("id");
        
        //SimpleHttpClient httpClient = new SimpleHttpClient(20000);	        
        SimpleHtmlParser htmlParser = new SimpleHtmlParser();
		        
        size += getResourceSize(urlStr);
        //Document d= htmlParser.parseDocument(urlStr);
        Document d;
		try {
			d = htmlParser.parseDocument(urlStr);
			List<String> l = d.getResourceURLs();
		
			for(String item : l) {
				size += getResourceSize(item);					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
        
	     //System.out.println("tadaaaaa! " + urlStr + " >> " + size);		
		 broadcastSize(urlID, urlStr, size);
		 writeSizeToDB(urlStr, size);
		 return size;
	 }
	 
	 private long getResourceSize(String urlStr) {
		long size = 0;
		try {
			//SimpleHttpClient httpClient = new SimpleHttpClient(1000);
			URL url;
	        URLConnection conn;
	        //System.out.println("URL STR: " + urlStr);
	        if(urlStr.startsWith("//")) {
	        	urlStr = "http:" + urlStr;
	        }
	        url = new URL(urlStr);
			conn = url.openConnection();
			
			//size = httpClient.getContentLength(urlStr);
			size = conn.getContentLength();
			if ( size < 0) {
				return 0;
			} 
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}

		return size;
		
	 }
	 
	 private void broadcastSize(String urlID, String urlStr, long size) {
		 Map <String, Object> sizeInfo = new HashMap <String, Object>();
		 sizeInfo.put("id", urlID);
		 sizeInfo.put("url", urlStr);
		 sizeInfo.put("size", size);
		 
		 JsonMapWriter jsonMapper = new JsonMapWriter();
		 
		 String eventMessage = jsonMapper.writeJson(sizeInfo);
		 
		 System.out.println(eventMessage);
		 
		 EventBroadcaster eventBroadcaster = new EventBroadcaster(urlID);
		 try {
			eventBroadcaster.broadcastEvent(eventMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 private void writeSizeToDB(String urlStr, long size) {
		 final String dbURL = "sql://localhost/scraper";

	     final DatabaseClient databaseClient = new DatabaseClient(dbURL);
	     
	     try {
			databaseClient.recordPageSize(urlStr, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
