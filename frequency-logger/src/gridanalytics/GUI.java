package gridanalytics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends UI implements ActionListener
{
	private static final long serialVersionUID = -2191074802078732591L;

	private JFrame contentFrame;
	
	private JTextField fileLocationTextField;
	private JTextField updateFrequencyTextField;
	
	private JButton logButton;
	private JButton stopButton;
	
	public GUI(Proxy proxy, AbstractLogger logger, File logFolder, int updateFrequency)
	{
		super(proxy, logger, logFolder, updateFrequency);
	}
	
	public void start()
	{
		try
		{
			// Create a new thread to run the GUI stuff in (in order to be
			// thread-safe)
			javax.swing.SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{					
					// Start program
					createGUI();
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("Failed to start program in independent thread.\n");
			
			e.printStackTrace();
		}
	}
	
	private void createGUI()
	{
		contentFrame = new JFrame();
		
		contentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentFrame.setTitle("Grid Analytics");
		
		JPanel contentPanel = new JPanel();
		
		/*
		 * Filepath text field
		 */
		
		JPanel fileLocationPanel = new JPanel();
		fileLocationPanel.setBorder(BorderFactory.createTitledBorder("Log Folder Location"));
		
		fileLocationTextField = new JTextField(30);
		fileLocationTextField.setText(logFolder.getAbsolutePath());
		
		fileLocationPanel.add(fileLocationTextField);
		
		contentPanel.add(fileLocationPanel);
		
		/*
		 * Update frequency text field
		 */
		
		JPanel updateFrequencyPanel = new JPanel();
		updateFrequencyPanel.setBorder(BorderFactory.createTitledBorder("Update Frequency (ms)"));
		
		updateFrequencyTextField = new JTextField(12);
		updateFrequencyTextField.setText(Integer.toString(updateFrequency));
		
		updateFrequencyPanel.add(updateFrequencyTextField);
		
		contentPanel.add(updateFrequencyPanel);
		
		/*
		 * Buttons
		 */
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		
		logButton = new JButton("Log");
		logButton.setActionCommand("Log");
		logButton.addActionListener(this);
		
		buttonPanel.add(logButton);
		
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("Stop");
		stopButton.addActionListener(this);
		
		// Disabled by default
		stopButton.setEnabled(false);
		
		buttonPanel.add(stopButton);
		
		contentPanel.add(buttonPanel);
		
		/*
		 * Finally, pack it all together
		 */
		
		contentFrame.add(contentPanel);
		
		contentFrame.pack();
		
		contentFrame.setVisible(true);
	}
	
	private boolean checkConnectivity()
	{
		URL url;
		
		try
		{
			url = new URL("http://www.nationalgrid.com/ngrealtime/realtime/systemdata.aspx");
			
			try
			{
				URLConnection connection = url.openConnection(proxy);
				
				if(connection.getInputStream() != null)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			catch(SocketException e)
			{
				e.printStackTrace();
				
				return false;
			}
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
			
			return false;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			
			return false;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Log"))
		{
			if(!checkConnectivity())
			{
				JOptionPane.showMessageDialog(contentFrame, "Unable to connect to National Grid website. Please check your internet connectivity.");
			}
			else
			{
				logFolder = new File(fileLocationTextField.getText());
				updateFrequency = Integer.parseInt(updateFrequencyTextField.getText());
				
				super.start();
				
				Thread thread = new Thread(logger);
				
				thread.start();
				
				// Disable all controls except stop button
				fileLocationTextField.setEnabled(false);
				updateFrequencyTextField.setEnabled(false);
				logButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		}
		else if(command.equals("Stop"))
		{
			logger.setEnabled(false);
			
			// Enable all controls except stop button
			fileLocationTextField.setEnabled(true);
			updateFrequencyTextField.setEnabled(true);
			logButton.setEnabled(true);
			stopButton.setEnabled(false);
		}
	}
}