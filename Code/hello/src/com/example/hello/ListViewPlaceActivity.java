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
