package com.example.gps_project;



import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

//import com.example.android2.R;
import com.example.gps_project.MySQLiteHelper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toast;



import jar_project1.Client;
//import jar_project1.Server;

public class MainActivity extends Activity {


    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    protected LocationManager locationManager;
    private GPSConection task1;
    
 // These three variables will be used to comput the Latitudes and Longitudes. Not computing yet
 // the sensor, sensorID.
    
    int interator=0;//this is defined in here because in the second time, it will just send
    //new things.
    ArrayList<Double> Latitudes = new ArrayList<Double>();
    ArrayList<Double> Longitudes = new ArrayList<Double>();

	private ObjectOutputStream output; // output stream to server
	private ObjectInputStream input; // input stream from server
	public Socket socket;
	int flag=0;//used for connection
	String ip;//will get the ip value from variable value0
    double posa, posb;
    
	MySQLiteHelper db = new MySQLiteHelper(this);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    Log.i("MainActivity","ligou");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
	//Thread for the GPS. Refreshs every 5 seconds (because Dr. Huff said to update every
	//5 seconds the GPS. Here, the values of latitude and longitude are stored in the SQLITE,
	//and in the arrays
		
	  private class GPSConection extends AsyncTask<String, Void, String> {
		
			  @Override
			    protected String doInBackground(String... nothing) {

					  while(true){
						    Log.i("AsyncTask","Entrou");
		
							Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
					        posa=location.getLatitude(); //Store in variable that will go to sqlite
					        posb=location.getLongitude();//Store in variable that will go to sqlite
					        Latitudes.add(posa);//store in array that will be used again in the conection thread
					        Longitudes.add(posb);//strore in the array that will be used again in the conection thread
					        Log.i("AsyncTask","Pegou valores"+posa+posb);
					        db.addBook(new Book(Double.toString(posa),Double.toString(posb)));
							try{
								  Log.i("AsyncTask","Rodou");	  
								  Thread.sleep(5000); // sleep for 5 seconds, because we need this for this project
							}
							  
							catch (Exception e){
								  return "";
							}
					  }				 				  
			    }		
	  }	
	  
	  
	//Determine the conection between the cellphone and the server. 
	  
	  private class socketConection extends AsyncTask<String, Void, String> {
	
		  
		  @Override
		    protected String doInBackground(String... urls) {
			  
            Log.i("AsyncTask", "doInBackground: Creating socket");                       
		    try {
		    	//this flag in here determines when the socket will be created again. So, after
		    	//Initializing a connection, we set flag to 1. And every time we enter the
		    	//Thread again, it will skip this part of creation. If wanted to start another
		    	//connection, just set flag=0 outside the Thread. flag is outside the thread, and
		    	//can be used everywhere inside the MainActivity.
	            if (flag==0){ 
	            	socket = new Socket();
		            Log.i("AsyncTask", "try Create");
		            
		            SocketAddress sockad = new InetSocketAddress(ip, 5555); 
			    	socket.connect(sockad);
			        Log.i("AsyncTask", "Could Create");
		    	 	output = new ObjectOutputStream(socket.getOutputStream());
		            }
	            
	            	//tests if conected
			    	if (socket.isConnected()){
			    		
			    		//this part can be made in a better way. I did the Client and Server
			    		//Files in a way that I only store only one variable of latitude, longitude,
			    		//etc, each time. It would be more inteligent to do an array inside the classes
			    		//Client and Server. Also, the Server is dumbie and has the same parameter of the
			    		//Client. I created the server in a way for making the thread to wait, but it
			    		//isnt used anymore, but still in the imports, because if you want to use it
			    		// it is up to you. Feel free to improve this poor methodology used to send.
			    		for (; interator<Latitudes.size();interator++){
				            Log.i("AsyncTask", "try Send again");
				            //Interator is a global variable. It does not need to be. I was trying another thing
				            // Feel free to fix it. The Latitude.size() or Logitude.size() or other dont make any
				            // difference, because all them have the same size.
				       
				            //Send object with 10 fields (just really using 2. Just need to adapt it to the rest 8. But it wont be
				            //difficult. 20 minuts and you finish it 
							Client sendObj = new Client("a","b","c","d",Latitudes.get(interator),Longitudes.get(interator),"g","h",3,"i");
					    	Log.i("AsyncTask", "Is Connected");
	
					    	output.writeObject(sendObj);
					    	output.flush();
					    	output.reset();
					    	Log.i("AsyncTask", "Sent "+Latitudes.size());
					    	//That is why I believe this fashion isnt interesting. To garantee that
					    	//the conection will happen, I am putting the thread to sleep 500ms. But it
					    	//is better to put the thread to exit, or start for an 'OK' of the server
					    	//to continue sending. The 'OK' is possible with the class Server. (again, that
					    	//is why it continued in the project. even without using it)
					    	Thread.sleep(500);
			    		}
				    
			    	}
			    	
			    	//after the for loop, I close the conection. (Dont need the flag 1, because it
			    	//is closed, but I used this because I dont feel that it is interesting
			    	// to send anymore to the server, sinse I already did it. We can create a 
			    	//button StartOver, or see other solution
			    	flag=1;
			    	output.close();
			    	input.close();
			    	socket.close();
		    		
		    }
		    
		    catch (Exception e){
	            Log.i("AsyncTask", "Coudnt Create");

		    }
		    Log.i("AsyncTask", "Ending");
	  		return null;
		      	
		    }
	
	  }

	  
	  //Button to start gps. I wont coment, not many information in here. The logs will
	  // continue in the program, but we can remove in the final version.
	  public void startgps (View view){
	      Log.i("MainActivity","entroubotao");
	      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		  MINIMUM_TIME_BETWEEN_UPDATES,MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
	      Log.i("MainActivity","vaientrarthread");

	      TextView tv = (TextView)findViewById(R.id.button1);
          tv.setText("Start");

		  task1 = new GPSConection();
		  //the a means nothing.
		  task1.execute("a");
					  
	  }
	  
	  //Sending to the server button
	  public void Send (View view){
	      Log.i("MainActivity","entrousend");
	      //before creating the connection, we will close the gps because I assumed that
	      //we got to the final spot (the NUC), so we dont need anymore to get values from
	      //the gps and sensors
	      task1.cancel(true);
	      socketConection task2 = new socketConection();
	      TextView tv = (TextView)findViewById(R.id.button1);
	      tv.setText("Continue");//this will change the value of Start button to Restart
	      flag=0;//will start a new conection
	      EditText editText3 = (EditText) findViewById(R.id.value0);
	      ip = editText3.getText().toString();
			
          task2.execute("a");
        
	  }
	  
	  	//It is just to change the view to the table. one button too.
	  public void change (View view){
		  Intent intent = new Intent(this, SQLActivity.class);
		  startActivity(intent);
	  }

	  
	  // It is the button to update the value on the screen. the two big values in the first page.
	  // It will get the last values of Latitude and Logitude
		public void update (View view){

	  		Log.i("MainActivity","Atualiza?");
			try{
		  		Log.i("MainActivity","Sim");
				TextView tv = (TextView)findViewById(R.id.TextView4);
				TextView tv2 = (TextView)findViewById(R.id.TextView3);


				tv.setText(Double.toString(posa));
				tv2.setText(Double.toString(posb));
						
			}
			catch (Exception e){
		  		Log.i("MainActivity","Nao");

			}
		}

		//Erase values on the sqlite
		public void Erase (View view){

		    db.deleteAll();
		}
		
		
//Get Sensor Code HERE!!
		public void GetSensor (View view){


		}		
		
		//USED FOR THE GPS. I removed all the messages inside it (not necessary). I will
		//Exclude the commented parts in the final version
	  private class MyLocationListener implements LocationListener {

	        public void onLocationChanged(Location location) {
	          //  String message = String.format(
	          //          "New Location \n Longitude: %1$s \n Latitude: %2$s",
	          //          location.getLongitude(), location.getLatitude()
	         //   );
	          //  Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
	        }

	        public void onStatusChanged(String s, int i, Bundle b) {
	          //  Toast.makeText(MainActivity.this, "Provider status changed",
	          //          Toast.LENGTH_LONG).show();
	        }

	        public void onProviderDisabled(String s) {
	        //    Toast.makeText(MainActivity.this,
	        //            "Provider disabled by the user. GPS turned off",
	        //            Toast.LENGTH_LONG).show();
	        }

	        public void onProviderEnabled(String s) {
	     //       Toast.makeText(MainActivity.this,
	     //               "Provider enabled by the user. GPS turned on",
	     //               Toast.LENGTH_LONG).show();
	        }
	    }
}
