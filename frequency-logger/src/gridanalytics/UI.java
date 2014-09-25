package gridanalytics;

import java.io.File;
import java.net.Proxy;

public class UI
{
	protected Proxy proxy;
	protected AbstractLogger logger;
	protected File logFolder;
	protected int updateFrequency;
	
	public UI(Proxy proxy, AbstractLogger logger, File logFolder, int updateFrequency)
	{
		this.proxy = proxy;
		this.logger = logger;
		this.logFolder = logFolder;
		this.updateFrequency = updateFrequency;
	}
	
	/**
	 * Gets the logger ready for running
	 */
	public void start()
	{
		logger.setProxy(proxy);
		logger.setLogFolder(logFolder);
		logger.setUpdateFrequency(updateFrequency);
		
		logger.setEnabled(true);
	}
}
