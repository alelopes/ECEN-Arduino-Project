package com.google.api.services.samples.fusiontables.cmdline;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jar_project1.Client;
import jar_project1.Server;


//import com.google.api.services.samples.fusiontables.cmdline.Client;
public class Java3server extends JFrame {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField enterField; // inputs message from user
    private JTextArea displayArea; // display information to user
    

    private ObjectOutputStream output; // output stream to client
    private ObjectInputStream input; // input stream from client	
	private ServerSocket server; // server socket
	private Socket connection; // connection to client
	int a,b;
	
	public Java3server()
	 {
	 super( "Java3server" );
	
	 enterField = new JTextField(); // create enterField
	 enterField.setEditable( false );
	 enterField.addActionListener(
	 new ActionListener()
	 {
	// send message to client
		 public void actionPerformed( ActionEvent event )
		 {
			 sendData( event.getActionCommand() );
			 enterField.setText( "" );
		 } // end method actionPerformed
	  } // end anonymous inner class
	  ); // end call to addActionListener
		
     add( enterField, BorderLayout.NORTH );
		
     displayArea = new JTextArea(); // create displayArea
   	 add( new JScrollPane( displayArea ), BorderLayout.CENTER );
   	 
   	 setSize( 300, 150 ); // set size of window
   	 setVisible(true);
	 } // end Server constructor
	
	
	
	
	
	
	//FIRST PART OF THE SERVER IT WILL COME HERE BEFORE.
	public void runServer()
	{
		try // set up server to receive connections; process connections
		{
			java.net.InetAddress addr = java.net.InetAddress.getLocalHost();  

			server = new ServerSocket( 5555, 100); // create ServerSocket
			displayMessage( "\nServer Started" );
			
			while(true){
				try
				{
				
					waitForConnection(); // wait for a connection
					getStreams(); // get input & output streams
					processConnection(); // process connection
				} // end try
				catch ( EOFException eofException )
				{
					//closeConnection();
				} // end catch
				finally
				{
				 // close connection
				} // end finally
			}
			 // end while
		} // end try
		catch ( IOException ioException )
		{
			ioException.printStackTrace();
		} // end catch
	} // end method runServer
	
	private void waitForConnection() throws IOException
	{
		//Actually just make it accept connections.
		displayMessage( "\nWaiting for connection\n" );
		connection = server.accept(); // allow server to accept connection
	}
	
	private void getStreams() throws IOException
	{		
		displayMessage( "\nGoing...\n" );
	    input = new ObjectInputStream( connection.getInputStream() );
	    output = new ObjectOutputStream (connection.getOutputStream());
	} // end method getStreams
	
	
	private void processConnection() throws IOException
	{
		
		//CREATES THE FUSION TABLE and 2 arrays (Again, this connection can be improved.
		//It isnt that smart like this.
		FusiontablesSample Fusion = new FusiontablesSample();

	    ArrayList<Double> Latitudes = new ArrayList<Double>();
	    ArrayList<Double> Longitudes = new ArrayList<Double>();

		String clientID, RunID, TimeStamp, Date, SensorID, SensorType, Attribute;
		double SensorValue;	
		setTextFieldEditable( true );

		//I already have all the variables, but Im just using the Arrays Latitude and
		//Logitudes
		while ( true )
		{
			
			try {

				displayMessage( "\ntrying to get...\n" );

				Client recobj = (Client) input.readObject();
				displayMessage("\nValues:");
 
				//As you can see, all the variables are subscribing, but the Logitude and
				//Latitude are arrays, so they are stored.
				Attribute=recobj.getAttribute();
				clientID=recobj.getClientID();
				Date=recobj.getDate();
				Latitudes.add(recobj.getLatitude());
				Longitudes.add(recobj.getLongitude());
				RunID=recobj.getRunID();
				SensorID=recobj.getSensorID();
				SensorType=recobj.getSensorType();
				SensorValue=recobj.getSensorValue();
				TimeStamp=recobj.getTimeStamp();
				System.out.println(Attribute+clientID+Date);	 //just to see some conection

				//I tried to send an 'OK' but got an error. So, I the conection with the 
				//Thread, as explaned in the android code.
			//	Server sendObj = new Server("a","b","c","d",1,2,"g","h",3,"i");

		    //	output.writeObject(sendObj);
		    //	output.flush();
		    //	output.reset();
				
				
			} catch (Exception e) {
				
				displayMessage( "\nClient Closed\n" );

				output.close(); // close output stream
				input.close(); // close input stream
				break;
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}	
		
		//After the client closes the conection, it comes here to send the Logitudes and Latitudes
		//to the Fusion Tables
		for (int i=0;i<Longitudes.size();i++){
			Fusion.Start(Longitudes.get(i),Latitudes.get(i));
			
		}
		
	} // end method processConnection
	
	
	
	private void closeConnection()
	{
		//Not used. But could. Just stays here because it is a already made method.
		displayMessage( "\nTerminating connection\n" );
		
		setTextFieldEditable( false ); // disable enterField
		try
		{
			output.close(); // close output stream
			input.close(); // close input stream
			connection.close(); // close socket
		}
		catch ( IOException ioException )
		{
			ioException.printStackTrace();
		} // end catch
	} // end method closeConnection
	
	
	//The same situation. Not used anymore. Just stays here because of the little Server Display
	private void sendData( String message )
	{
	} // end method sendData

	private void displayMessage( final String messageToDisplay )
	//This is the display part. Dont need to worry.
	{
		SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run() // updates displayArea
			{
				displayArea.append( messageToDisplay ); // append message
			} // end method run
		} // end anonymous inner class
		); // end call to SwingUtilities.invokeLater
	} // end method displayMessage
	
	
	
	
	private void setTextFieldEditable( final boolean editable )
	{
		SwingUtilities.invokeLater(
		new Runnable()
		{
			public void run() // sets enterField's editability
			{
				enterField.setEditable( editable );
			} // end method run
		} // end inner class
	); // end call to SwingUtilities.invokeLater
	}


}
