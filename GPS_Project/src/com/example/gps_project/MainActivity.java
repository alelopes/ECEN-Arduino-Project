package com.example.gps_project;

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
import android.widget.TextView;
//import android.widget.Toast;






public class MainActivity extends Activity {


    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    protected LocationManager locationManager;
    double posa, posb;
    
	MySQLiteHelper db = new MySQLiteHelper(this);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    Log.i("MainActivity","ligou");

	    db.deleteAll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
		
	  private class socketConection extends AsyncTask<String, Void, String> {
		
			  
			  @Override
			    protected String doInBackground(String... nothing) {

				  while(true){
				    Log.i("AsyncTask","Entrou");

					Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			        posa=location.getLatitude();
			        posb=location.getLongitude();
			        Log.i("AsyncTask","Pegou valores"+posa+posb);
			        db.addBook(new Book(Double.toString(posa),Double.toString(posb)));
					  try{
					  Log.i("AsyncTask","Rodou");	  
					  Thread.sleep(5000); // sleep for 10 seconds
					  }
					  
					  catch (Exception e){
						  return "";

					  }
				  }				 				  
			    }
	
		
	  }	
	  
	  public void startgps (View view){
	      Log.i("MainActivity","entroubotao");
	      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		  MINIMUM_TIME_BETWEEN_UPDATES,MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
	      Log.i("MainActivity","vaientrarthread");


		  socketConection task = new socketConection();
		  task.execute("a");
					  
	  }
	  
	  public void change (View view){
		  Intent intent = new Intent(this, SQLActivity.class);
		  startActivity(intent);

		  

	  }

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
