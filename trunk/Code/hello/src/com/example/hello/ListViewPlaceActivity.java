/*Show place on listView*/

package com.example.hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jsonparser.DirectionsJSONParser;
import jsonparser.PlaceDetailsJSONParser;

import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import model.*;

import connect.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class ListViewPlaceActivity extends Activity {

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	GoogleMap mGoogleMap;

	// Places List
	PlacesList nearPlaces;

	// GPS Location
	GPSTracker gps;

	// Progress dialog
	ProgressDialog pDialog;

	// Places Listview
	ListView lv;

	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();

	// KEY Strings
	public static String KEY_REFERENCE = "reference";
	public static String KEY_NAME = "name";
	public static String KEY_VICINITY = "vicinity";

	private static final String API_KEY_Place = "AIzaSyB42A0MqBlXb3dEPSk9saKUhDq7OQ6GAMQ";
	private static final String PLACES_DETAIL = "https://maps.googleapis.com/maps/api/place/details/json?";

	String keyWord;
	String reference;
	int Mode = 0;
	Location location;
	Double tdLat, tdLng;
	double latk, lngk;
	Boolean choose3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view_place);

		Intent i = getIntent();
		keyWord = i.getStringExtra("key");
		tdLat = i.getExtras().getDouble("tdLat");
		tdLng = i.getExtras().getDouble("tdLng");
		choose3 = i.getExtras().getBoolean("choose3");
		if (choose3 == true) {

			latk = i.getExtras().getDouble("latk");
			lngk = i.getExtras().getDouble("lngk");
		}

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(ListViewPlaceActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// creating GPS Class object
		gps = new GPSTracker(this);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(ListViewPlaceActivity.this, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);

			// stop executing code by return
			return;
		}

		//if (ktWifi() == true && ktGps() == true) {
			// Getting listview
			lv = (ListView) findViewById(R.id.list);
			new LoadPlaces().execute();
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					reference = ((TextView) view.findViewById(R.id.reference))
							.getText().toString();
					show(reference);
				}
			});
//		} else {
//			alert.showAlertDialog(ListViewPlaceActivity.this,
//					"Internet of GPS Connection Error",
//					"Please connect to working Internet or GPS connection",
//					false);
//		}
	}

	public void show(String reference) {
		StringBuilder sb = new StringBuilder(PLACES_DETAIL);
		sb.append("reference=" + reference);
		sb.append("&sensor=true");
		sb.append("&key=" + API_KEY_Place);
		PlacesTask_Details_Place placesTask = new PlacesTask_Details_Place();
		placesTask.execute(sb.toString());
	}

	/*
	 * ASYNCTASK DETAILS PLACE
	 */
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}

	/** A class, to download Google Place Details */
	private class PlacesTask_Details_Place extends
			AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {
			ParserTask_Details_Place parserTask = new ParserTask_Details_Place();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Place Details in JSON format */
	private class ParserTask_Details_Place extends
			AsyncTask<String, Integer, HashMap<String, String>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected HashMap<String, String> doInBackground(String... jsonData) {

			HashMap<String, String> hPlaceDetails = null;
			PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Start parsing Google place details in JSON format
				hPlaceDetails = placeDetailsJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return hPlaceDetails;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(HashMap<String, String> hPlaceDetails) {

			String name = hPlaceDetails.get("name");
			String lat = hPlaceDetails.get("lat");
			String lng = hPlaceDetails.get("lng");
			String formatted_address = hPlaceDetails.get("formatted_address");
			String formatted_phone = hPlaceDetails.get("formatted_phone");
			String website = hPlaceDetails.get("website");
			// Setting the data in WebView
			final Dialog dialog = new Dialog(ListViewPlaceActivity.this,
					R.style.cust_dialog);
			dialog.setContentView(R.layout.dialog_single_place);
			dialog.setTitle("Single Place");
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = dialog.getWindow();
			lp.copyFrom(window.getAttributes());
			// This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

			TextView tvname = (TextView) dialog.findViewById(R.id.name);
			TextView tvaddress = (TextView) dialog.findViewById(R.id.address);
			TextView tvphone = (TextView) dialog.findViewById(R.id.phone);
			TextView tvlocation = (TextView) dialog.findViewById(R.id.location);
			TextView tvwebs = (TextView) dialog.findViewById(R.id.website);
			tvname.setText(name);

			tvaddress.setText(formatted_address);
			tvwebs.setText(website);
			tvphone.setText(Html.fromHtml("<b>Phone:</b> " + formatted_phone));
			tvlocation.setText(Html.fromHtml("<b>Latitude:</b> " + lat
					+ ", <b>Longitude:</b> " + lng));

			Button btnHide = (Button) dialog.findViewById(R.id.btn_hide_map);
			btnHide.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dialog.show();
		}

	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListViewPlaceActivity.this);
			pDialog.setMessage(Html
					.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();
			String types = "";

			try {
				if (keyWord == "") {
					types = "atm";
				} else {
					types = keyWord;
				}
				double radius = 3000;// 3km
				if (choose3 == true) {
					nearPlaces = googlePlaces.search(latk, lngk, radius, types);
				} else {
					nearPlaces = googlePlaces.search(gps.getLatitude(),
							gps.getLongitude(), radius, types);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = nearPlaces.status;

					// Check for all possible status
					if (status.equals("OK")) {
						// Successfully got places details
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();

								// Place reference won't display in listview -
								// it will be hidden
								// Place reference is used to get
								// "place full details"

								map.put(KEY_REFERENCE, p.reference);

								// Place name
								map.put(KEY_NAME, p.name);

								// adding HashMap to ArrayList
								placesListItems.add(map);
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(
									ListViewPlaceActivity.this,
									placesListItems, R.layout.list_item,
									new String[] { KEY_REFERENCE, KEY_NAME },
									new int[] { R.id.reference, R.id.name });

							// Adding data into listview
							lv.setAdapter(adapter);
						}
					} else if (status.equals("ZERO_RESULTS")) {
						// Zero results found
						alert.showAlertDialog(
								ListViewPlaceActivity.this,
								"Near Places",
								"Sorry no places found. Try to change the types of places",
								false);
					} else if (status.equals("UNKNOWN_ERROR")) {
						alert.showAlertDialog(ListViewPlaceActivity.this,
								"Places Error", "Sorry unknown error occured.",
								false);
					} else if (status.equals("OVER_QUERY_LIMIT")) {
						alert.showAlertDialog(
								ListViewPlaceActivity.this,
								"Places Error",
								"Sorry query limit to google places is reached",
								false);
					} else if (status.equals("REQUEST_DENIED")) {
						alert.showAlertDialog(ListViewPlaceActivity.this,
								"Places Error",
								"Sorry error occured. Request is denied", false);
					} else if (status.equals("INVALID_REQUEST")) {
						alert.showAlertDialog(ListViewPlaceActivity.this,
								"Places Error",
								"Sorry error occured. Invalid Request", false);
					} else {
						alert.showAlertDialog(ListViewPlaceActivity.this,
								"Places Error", "Sorry error occured.", false);
					}
				}
			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_view_place, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.backHome) {
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	public boolean ktWifi() {
		cd = new ConnectionDetector(getApplicationContext());

		// KT Quan net co mo cua khong
		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent) {
			return true;
		} else {
			return false;
		}
	}

	public boolean ktGps() {
		gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			return true;
		} else {
			return false;
		}
	}
}
