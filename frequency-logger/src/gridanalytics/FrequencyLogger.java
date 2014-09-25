package gridanalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utilities.time.TimeUtilities;

public class FrequencyLogger extends AbstractLogger implements Runnable
{
	private File logFolder;
	private int updateFrequency = 120000;
	
	// Proxy to connect via
	private Proxy proxy;
	
	// Whether to log or not
	private boolean enabled = true;
	
	public FrequencyLogger()
	{
		
	}

	@Override
	public void run()
	{
		// Setup the calendar
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"), Locale.UK);
		
		// Create a date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm z");
		
		// Set the log filename
		String fileName = "Frequency.txt";
		
		// Setup the log file
		File logFile = new File(logFolder.getAbsolutePath() + File.separator + fileName);
		
		// Set the URL to the National Grid data web page
		URL url = null;
		
		try
		{
			url = new URL("http://www.bmreports.com/bsp/additional/saveoutput.php?element=rollingfrequencyhistoric&output=XML");
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		while(enabled)
		{
			URLConnection connection;
			long connectionDate;
			
			try
			{				
				// Open connection to the URL
				connection = url.openConnection(proxy);
				
				// Log the server's reported GPS date
				connectionDate = connection.getDate() / 1000L - TimeUtilities.GPS_UNIX_EPOCH_OFFSET;
				
				// Parse the data
				parseFrequencies(connection.getInputStream());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (SAXException e)
			{
				e.printStackTrace();
			}
			catch (ParserConfigurationException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				Thread.sleep(updateFrequency);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void stop()
	{
		this.enabled = false;
	}
	
	private void parseFrequencies(InputStream input) throws SAXException, IOException, ParserConfigurationException
	{
		// Set up XML parser
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		
		// Parse the data
		Document doc = docBuilder.parse(input);
		
		doc.getDocumentElement().normalize();
		
		NodeList frequencies = doc.getElementsByTagName("ST");
		
		// Define some frequently used fields
		Node frequency;
		NodeList children;
		String itemTime;
		String itemFrequency;
		
		for(int n = 0; n < frequencies.getLength(); n++)
		{
			frequency = frequencies.item(n);
			
			if(frequency.getNodeType() == Node.ELEMENT_NODE)
			{
				children = frequency.getChildNodes();
				
				itemTime = children.item(0).getTextContent();
				itemFrequency = children.item(1).getTextContent();
				
				System.out.println(itemTime + " " + itemFrequency);
			}
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public File getLogFolder()
	{
		return logFolder;
	}

	public void setLogFolder(File logFolder)
	{
		this.logFolder = logFolder;
	}

	public int getUpdateFrequency()
	{
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency)
	{
		this.updateFrequency = updateFrequency;
	}

	public Proxy getProxy()
	{
		return proxy;
	}

	public void setProxy(Proxy proxy)
	{
		this.proxy = proxy;
	}
}
