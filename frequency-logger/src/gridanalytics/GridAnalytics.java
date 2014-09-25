package gridanalytics;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Scanner;

public class GridAnalytics
{
	public static void main(String[] args)
	{
		// Load GUI by default
		boolean headless = false;
		
		// Folder to log stuff to
		String logFolderPath = System.getProperty("user.dir") + File.separator;
		
		/*
		 * Parse arguments
		 */
		
		String arguments = "";
		
		for(String string : args)
		{
			arguments += " " + string;
		}
		
		Scanner scanner = new Scanner(arguments);
		
		while(scanner.hasNext())
		{
			String next = scanner.next();
			
			if(next.equals("--headless"))
			{
				headless = true;
			}
			else if(next.equals("--folder"))
			{
				if(scanner.hasNext())
				{
					logFolderPath = scanner.next();
				}
			}
		}
		
		/*
		 * Set up the proxy
		 */
		
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("130.209.6.40", 8080));
		Proxy proxy = Proxy.NO_PROXY;
		
		/*
		 * Create logger
		 */
		
		File logFolder = new File(logFolderPath);
		
		//final AbstractLogger logger = new Logger();
		final AbstractLogger logger = new FrequencyLogger();
		
		/*
		 * Start (graphical) user interface
		 */
		
		if(headless)
		{			
			CLI cli = new CLI(proxy, logger, logFolder, 120000);
			
			cli.start();
		}
		else
		{
			GUI gui = new GUI(proxy, logger, logFolder, 120000);
			
			gui.start();
		}
	}
}
