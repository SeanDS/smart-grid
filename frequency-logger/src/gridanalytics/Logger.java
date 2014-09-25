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

import utilities.time.TimeUtilities;

public class Logger extends AbstractLogger
{	
	public Logger()
	{
		super();
	}

	@Override
	public void run()
	{
		// Create the regex pattern		
		Pattern pattern = Pattern.compile("Demand: (.*?)MW<BR />(.*?) GMT<BR />Frequency: (.*?)Hz<BR/>(.*?) GMT</p><h3>System Transfers</h3><p class='small'>N.Ireland to Great Britain: (.*?)MW<BR />France to Great Britain: (.*?)MW<BR />Netherlands to GB: (.*?)MW<BR />(.*?) GMT<BR /><BR />North-South: (.*?)MW<BR />Scot - Eng: (.*?)MW<BR />(.*?) GMT");
		
		// Setup the calendar
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"), Locale.UK);
		
		// Create a date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm z");
		
		// Set the log filename
		String fileName = dateFormat.format(calendar.getTime()) + ".txt";
		
		// Setup the log file
		File logFile = new File(logFolder.getAbsolutePath() + File.separator + fileName);
		
		// Set the URL to the National Grid data web page
		URL url = null;
		
		try
		{
			url = new URL("http://www.nationalgrid.com/ngrealtime/realtime/systemdata.aspx");
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		while(enabled)
		{
			URLConnection connection;
			long connectionDate = 0;
			String data = "";
			
			try
			{				
				// Open connection to the URL
				connection = url.openConnection(proxy);
				
				// Log the server's reported GPS date
				connectionDate = connection.getDate() / 1000L - TimeUtilities.GPS_UNIX_EPOCH_OFFSET;
				
				// Collect the data
				data = collectInputStreamData(connection.getInputStream());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			Matcher matcher = pattern.matcher(data);
			
			matcher.find();
			
			try
			{
				FileWriter writer = new FileWriter(logFile, true);
				
				if(logFile.length() == 0)
				{
					// Empty, so first print the header
					writer.write("System Timestamp\tDemand (MW)\tDemand Update Time (hh:mm:ss GMT)\tFrequency (Hz)\tFrequency Update Time (hh:mm:ss GMT)\tN.Ireland to GB Transfer (MW)\tFrance to GB Transfer (MW)\tNetherlands to GB Transfer (MW)\tTransfer Update Time (dd-mm-yyyy hh:mm:ss GMT)\tNorth-South Transfer (MW)\tScotland-England Transfer (MW)\tTransfer Update Time (dd-mm-yyyy hh:mm:ss GMT)\r\n");
				}
				
				// The most accurate date and time in the data
				String accurateDate = matcher.group(11);
				
				long demandTime = TimeUtilities.calendarToGPSTime(TimeUtilities.parseTimeUsingAccurateDate(matcher.group(2), accurateDate));
				long frequencyTime = TimeUtilities.calendarToGPSTime(TimeUtilities.parseTimeUsingAccurateDate(matcher.group(4), accurateDate));
				long foreignTransferTime = TimeUtilities.calendarToGPSTime(TimeUtilities.stringToCalendar(matcher.group(8)));
				long domesticTransferTime = TimeUtilities.calendarToGPSTime(TimeUtilities.stringToCalendar(matcher.group(11)));
				
				writer.write(
					connectionDate + "\t" +			// server GPS time
					matcher.group(1) + "\t" +		// demand
					demandTime + "\t" +				// demand time
					matcher.group(3) + "\t" +		// frequency
					frequencyTime + "\t" +			// freqency time
					matcher.group(5) + "\t" +		// ni-gb
					matcher.group(6) + "\t" +		// france-gb
					matcher.group(7) + "\t" +		// netherlands-gb
					foreignTransferTime + "\t" +	// above three transfer times
					matcher.group(9) + "\t" +		// north-south
					matcher.group(10) + "\t" +		// scot-eng
					domesticTransferTime + "\r\n"	// above two transfer times
				);
				
				writer.close();
			}
			catch(IOException e)
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
	
	private String collectInputStreamData(InputStream input)
	{
		String data = "";
		
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String line;
			
			while((line = reader.readLine()) != null)
			{
				data += line;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return data;
	}
}
