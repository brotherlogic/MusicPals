package uk.co.brotherlogic.musicpals.lib;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The link down to LastFM
 * @author sat
 *
 */
public class LastFMBridge
{
	public TrackInfo getLastTrackInfo(String username)
	{
		TrackInfo info = new TrackInfo(username);
		try
		{
			info.getTrackInfo(new URL("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="+username+"&api_key=5220fec05183d2e5a8c3f91162f92139").openStream());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return info;
	}
	
	public List<TrackInfo> getFriendsListening(String username)
	{
		final List<TrackInfo> toRet = new LinkedList<TrackInfo>();
		
		//Process the feed
		try
		{
			toRet.add(getLastTrackInfo(username));
			
			InputStream is = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getfriends&user="+username+"&api_key=5220fec05183d2e5a8c3f91162f92139").openStream();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, new DefaultHandler(){

				String characters = "";
				
				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException
				{
					characters += new String(ch,start,length);
				}

				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException
				{
					if (qName.equals("name"))
						toRet.add(getLastTrackInfo(characters));
				}

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException
				{
					characters = "";
				}
				
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Collections.sort(toRet);
		
		return toRet;
	}
	
	public static void main(String[] args)
	{
		LastFMBridge bridge = new LastFMBridge();
		List<TrackInfo> infos = bridge.getFriendsListening("BrotherLogic");
		for (TrackInfo trackInfo : infos)
		{
			System.out.println(trackInfo);
		}
	}
}
