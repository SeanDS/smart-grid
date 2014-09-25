package gridanalytics;

import java.io.File;
import java.net.Proxy;

public abstract class AbstractLogger implements Runnable
{
	// Whether to log or not
	protected boolean enabled = true;
	
	protected File logFolder;
	protected int updateFrequency = 120000;
	
	// Proxy to connect via
	protected Proxy proxy;
	
	public AbstractLogger()
	{
		
	}
	
	@Override
	public abstract void run();
	
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
