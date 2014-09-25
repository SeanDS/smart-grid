package gridanalytics;

import java.io.File;
import java.net.Proxy;

public class CLI extends UI
{	
	public CLI(Proxy proxy, AbstractLogger logger, File logFolder, int updateFrequency)
	{
		super(proxy, logger, logFolder, updateFrequency);
	}
	
	public void start()
	{
		super.start();
		
		Thread thread = new Thread(logger);
		
		thread.start();
	}
}