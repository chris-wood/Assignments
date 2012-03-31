import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/*
 * TFTPreader.java
 * 
 * Version: 3/20/12
 */

/**
 * This class is responsible for driving the TFTP protocol
 * behavior in order to download a file specified by the user.
 * 
 * @author Christopher Wood (caw4567@rit.edu)
 */
public class TFTPreader 
{	
	/**
	 * The default timeout value for network transmissions.
	 */
	private static final int DEFAULT_TIMEOUT = 2000;
	
	/**
	 * The default TFTP port.
	 */
	private static final int DEFAULT_PORT = 69;
	
	/**
	 * Validate the parameters used to retrieve the file from the TFTP server.
	 * 
	 * @param host - the specified host server.
	 * @param mode - the specified transfer mode.
	 * 
	 * @return true if valid, false otherwise.
	 */
	public boolean validateParameters(String host, String mode)
	{
		boolean valid = false;
		
		try 
		{
			// Attempt to resolve this host name
			InetAddress.getByName(host);
			
			// Attempt to resolve the transfer mode to a fixed type
			if (TFTPmessage.buildTransferMode(mode) != null)
			{
				valid = true;
			}
		} 
		catch (UnknownHostException e) 
		{
			System.err.println("Error: Invalid machine name.");
		}
		
		return valid;
	}
	
	/**
	 * Append a block of data to the file buffer that is in memory
	 * so that it can be written to the disk once the file 
	 * transmission is complete.
	 * 
	 * @param dataBlocks - the file buffer that maps file blocks to raw data.
	 * @param message - the data message that conains the bytes to add to the buffer.
	 * 
	 * @return true if successful (block number not present), false otherwise.
	 */
	public boolean appendData(Map<Integer, byte[]> dataBlocks, DataMessage message)
	{
		boolean result = false;
		if (!dataBlocks.containsKey(message.blockNumber))
		{
			dataBlocks.put(message.blockNumber, message.data);
			result = true;
		}
		return result;
	}
	
	/**
	 * Attempt to read a file from the TFTP server and write it to the disk.
	 * 
	 * @param mode - the transfer mode to use for the file reception.
	 * @param host - the host server name where the TFTP program is located.
	 * @param fileName - the file to receive.
	 */
	public void receiveFile(TFTPmessage.TransferMode mode, String host, String fileName)
	{
		TFTPclient client = new TFTPclient();
		Map<Integer, byte[]> dataBlocks = new HashMap<Integer, byte[]>();
		try 
		{
			client.open(host, DEFAULT_TIMEOUT);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// TODO
		// 1. verify valid machine/server name (else print another error - add error printing as a private static method)
		// 2. connect to the TFTP server
		// 3. transfer the file using the protocol specified
		
		// Send a request for a new file to the client
		try 
		{
			client.sendMessage(new RequestMessage(fileName, TFTPmessage.Opcode.RRQ, TFTPmessage.TransferMode.OCTET), DEFAULT_PORT);
			try 
			{
				// Continue reading data until we reach a non-full block
				boolean transferComplete = false;
				while (!transferComplete)
				{
					TFTPmessage result = client.getMessage();
					if (result instanceof DataMessage)
					{
						DataMessage msg = (DataMessage)result;
						
						// Attempt to append the message, which will succeed if 
						// we have not seen this block number yet
						if (appendData(dataBlocks, msg))
						{
							// Determine if we should ask for more data or 
							// end the file transfer.
							if (msg.size < TFTPmessage.DATA_BLOCK_SIZE)
							{
								transferComplete = true;
							}
							else
							{
								//System.out.println("Sending ack for block " + msg.blockNumber);
								client.sendMessage(new AckMessage(msg.blockNumber), msg.port);
							}
						}
					}
					else if (result instanceof ErrorMessage)
					{
						// TODO: parse and display this error message
						System.err.println("Error: display contents here...");
						return;
					}
				}
				
				System.out.println("Transfer complete.");
				writeFile(fileName, dataBlocks);
			} 
			catch (MalformedMessageException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (SocketTimeoutException e1) 
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Write the data provided to disk under the file name specified.
	 * 
	 * @param file - the file to store the data.
	 * @param data - the file data, partitioned by block number.
	 */
	private void writeFile(String file, Map<Integer, byte[]> data)
	{
		try 
		{
			// Create (overwrite, if already present) the new file
			FileOutputStream fos = new FileOutputStream(file, false);
			
			// Iterate across the entire data set and write the bytes
			System.out.println("Writing file contents.");
			for (int i = 1; i <= data.size(); i++)
			{
				fos.write(data.get(i));
			}
			
			// Flush and close the stream to finish
			fos.flush();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * The main entry point into the program.
	 * 
	 * @param args - command line arguments containing the transfer
	 *               mode to use, the name of the host on which the
	 *               TFTP server is running, and the name of the 
	 *               file to transfer.
	 */
	public static void main(String[] args)
	{
		// Verify that the correct number of parameters was provided
		if (args.length != 3)
		{
			displayUsage();
		}
		else
		{
			TFTPreader reader = new TFTPreader();
			if (reader.validateParameters(args[0], args[1]))
			{
				// TODO: replace with args[] values
				reader.receiveFile(TFTPmessage.TransferMode.NETASCII, "glados.cs.rit.edu", "test1.txt");
			}		
		}
	}
	
	/**
	 * Simple helper method that displays the usage message that explains
	 * how to run the TFTPreader program.
	 */
	private static void displayUsage()
	{
		System.err.println("Usage: java TFTPreader [netascii|octet] tftp-host file");
	}
}

