package uk.co.brotherlogic.musicpals.lib;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class TrackInfo implements Comparable<TrackInfo>
{
	@Override
	public int compareTo(TrackInfo o)
	{
		return -lastPlayerTime.compareTo(o.lastPlayerTime);
	}

	String username;
	String artist;
	String title;
	Long lastPlayerTime;
	
	public TrackInfo(String uName)
	{
		username = uName;
	}
	
	public String toString()
	{
		return username + ": " + artist + "-" + title + " [" + lastPlayerTime + "]";
	}
	
	public void getTrackInfo(InputStream xmlStream) throws IOException, SAXException, ParserConfigurationException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(xmlStream,new DefaultHandler(){

			String characters;
			boolean reading = true;
			
			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException
			{
				characters += new String(ch,start,length);
			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException
			{
				if (reading)
				if (qName.equals("artist"))
					artist = characters;
				else if (qName.equals("name"))
					title = characters;
				else if (qName.equals("track"))
					reading = false;
			}

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException
			{
				characters = "";
				if(reading)
				if (qName.equals("date"))
					lastPlayerTime = Long.parseLong(attributes.getValue("uts"));
			}
			
		});
	}
}