// This interface uses Amarino and Gson libraries

package com.sensordataacquisition;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;



public class MainActivity extends Activity {

	private static final String DEVICE_ADDRESS = "00:12:09:25:92:92";
	
	private BluetoothReceiver btr = new BluetoothReceiver();
	
	private Button sensorButton;
	private TextView outputText;
	private Gson gson = new Gson();
	private List<JSONobj> sensorDataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sensorButton = (Button)findViewById(R.id.sensorButton);
		outputText = (TextView)findViewById(R.id.outputText);
		outputText.setMovementMethod(new ScrollingMovementMethod());
		
		sensorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				communicate();
			}
		});
		
	}
	
	protected void communicate() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 's', "");
	}
	
	protected void onStart() {
		super.onStart();
		registerReceiver(btr, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		Amarino.connect(this, DEVICE_ADDRESS);
	}

	protected void onStop() {
		super.onStop();
		Amarino.disconnect(this,  DEVICE_ADDRESS);
		unregisterReceiver(btr);
	}
	
	// class wrapper for json data received from arduino
	public class JSONobj {
		private String SENSORID;
		private String SENSORTYPE;
		private double SENSORVALUE;
		
		public void setSensorID(String sensorID) {
			this.SENSORID = sensorID;
		}
		
		public void setSensorType(String sensorType) {
			this.SENSORTYPE = sensorType;
		}
		
		public void setSensorValue(double sensorValue) {
			this.SENSORVALUE = sensorValue;
		}
		
		public String getSensorID() {
			return SENSORID;
		}
		
		public String getSensorType() {
			return SENSORTYPE;
		}
		
		public double getSensorValue() {
			return SENSORVALUE;
		}
	}
	
	public class BluetoothReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String data = null;
			JSONobj jsonObj = new JSONobj();
			// the device address from which the data was sent
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// check for string data
			if (dataType == AmarinoIntent.STRING_EXTRA) {
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				// begin parsing the json string for all the data
				if (data!= null) {
					JsonReader reader = new JsonReader(new StringReader(data));
					try {
						reader.beginObject();
						while(reader.hasNext()) {
							String temp = reader.nextName();
							if (temp.equals("sensor-id")) {
								jsonObj.setSensorID(reader.nextString());
							}
							else if (temp.equals("sensor-type")) {
								jsonObj.setSensorType(reader.nextString());
							}
							else if (temp.equals("sensor-value")) {
								jsonObj.setSensorValue(reader.nextDouble());
							}
							else reader.skipValue();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// add custom json object type to the sensor data list
					sensorDataList.add(jsonObj);
					
					// output info to the text view
					outputText.append(data + "\n");
					outputText.append(jsonObj.getSensorID() + " " 
							+ jsonObj.getSensorType() + " "
							+ jsonObj.getSensorValue() + "\n");
				}
			}
		}
	}
}
