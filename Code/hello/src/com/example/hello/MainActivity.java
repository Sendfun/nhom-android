package com.example.hello;

import adapter.NavDrawerListAdapter;
import model.NavDrawerItem;
import model.PlaceDetails;
import jsonparser.*;
import connect.*;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends FragmentActivity implements LocationListener {

	int dem = 0;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	// DECLARE VARIABLE _ MAP
	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;
	// GPS Location
	GPSTracker gps;
	// Connection detector class
	ConnectionDetector cd;
	// flag for Internet connection status
	Boolean isInternetPresent = false;
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;

	double mLatitude = 0;
	double mLongitude = 0;

	int Dem = 0;
	LatLng vitri1, vitri2;
	int Mode = 0;
	public static String KEY_REFERENCE = "reference";
	public static String KEY_NAME = "name";
	public static String KEY_VICINITY = "vicinity";

	private static final String API_KEY_Place = "AIzaSyB42A0MqBlXb3dEPSk9saKUhDq7OQ6GAMQ";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String PLACES_DETAIL = "https://maps.googleapis.com/maps/api/place/details/json?";
	String diadiem1, diadiem2;

	// Google Places
	GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces;
	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> m_reference = new HashMap<String, String>();

	// Progress dialog
	ProgressDialog pDialog;
	Button btnShowOnMap;
	String Key = "";

	// Place Details
	PlaceDetails placeDetails;
	String reference;
	ListAdapter adapterAddPlace;
	Location location;
	LatLng Cur_location, vitriATM, vitriNhi;
	int choose, choose1, choose2;
	Boolean choose3 = false;
	int look;
	String namePlace = "";
	Boolean flag;
	Boolean cam;
	Polyline line = null;
	public static Double Alat;
	public static Double Alng;
	int demLocation = 0;
	String radius_atm = "", name_atm = "";

	String name_ne = "";
	String radius_ne;
	String key;
	String okhoa = "";
	double latk = 0;
	double lngk = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setLocale("en");

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Communities, Will add a counter here
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
		// .getResourceId(3, -1)));
		// // Pages
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
		// .getResourceId(4, -1)));
		// // What's hot, We will add a counter here
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
		// .getResourceId(5, -1), true, "50+"));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			// displayView(0);
		}
		// GOOGLE MAP
		cd = new ConnectionDetector(getApplicationContext());

		// KT Quan net co mo cua khong
		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent) {
			// showInternetSetting();

		} else if (!isInternetPresent) {
			showInternetSetting();

		}

		// KT Quan net co chia khoa khoa xe lai khong
		gps = new GPSTracker(this);

		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude()
					+ ", longitude: " + gps.getLongitude());

		} else {
			gps.showSettingsAlert();

		}

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 25;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Loading map
			initilizeMap();

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location From GPS
			location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);
			locationManager.requestLocationUpdates(
					locationManager.NETWORK_PROVIDER, 20000, 0, this);
		}

		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				LatLng latLnga = arg0.getPosition();

				// Setting the latitude
				Double lata = latLnga.latitude;

				Double lnga = latLnga.longitude;
				LatLng n = new LatLng(lata, lnga);

				if (flag == true) {
					if (line != null) {
						line.remove();
						line = null;
					}
					if (look == 0) {
						if (ktWifi() == true && ktGps() == true) {
							Atm_location(n, Cur_location);
						} else {
							alert.showAlertDialog(
									MainActivity.this,
									"Internet of GPS Connection Error",
									"Please connect to working Internet or GPS connection",
									false);
						}
					} else if (look == 1) {
						if (ktWifi() == true) {
							Atm_location(n, vitriNhi);
						} else {
							alert.showAlertDialog(
									MainActivity.this,
									"Internet Connection Error",
									"Please connect to working Internet connection",
									false);
						}
					}
				}
				arg0.showInfoWindow();
				return true;
			}
		});

		mGoogleMap
				.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker arg0) {
						if (cam == true) {
							Intent intent = new Intent(getBaseContext(),
									DetailSinglePlaceActivity.class);
							String reference = m_reference.get(arg0.getId());
							if (reference == "" || reference == null) {

							} else {
								intent.putExtra("reference", reference);
								startActivity(intent);
							}
						}
					}
				});

		/**
		 * SET ON MAP CLICK, SHOW MARKER AND LATLNG
		 * */

		// mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng latLng) {
		//
		// // Creating a marker
		// MarkerOptions markerOptions = new MarkerOptions();
		//
		// // Setting the position for the marker
		// markerOptions.position(latLng);
		//
		// // Setting the title for the marker.
		// // This will be displayed on taping the marker
		// markerOptions.title(latLng.latitude + " : " + latLng.longitude)
		// .getTitle();
		//
		// LatLng teoem = new LatLng(latLng.latitude, latLng.longitude);
		// Atm_location(teoem, Cur_location);
		//
		// // Clears the previously touched position
		// mGoogleMap.clear();
		//
		// // Animating to the touched position
		// mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		//
		// // Placing a marker on the touched position
		// mGoogleMap.addMarker(markerOptions);
		// }
		// });

	}

	public void showInternetSetting() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this, R.style.cust_dialog);

		// Setting Dialog Title
		alertDialog.setTitle("Internet is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("Wifi is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_WIFI_SETTINGS);
						startActivityForResult(intent, RESULT_CANCELED);
						;
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.searchWay:
			FindStreetDialog();
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.refresh:
			SearchPlace();
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.normal_map:
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.satellite_map:
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.terrain_map:
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.hybrid_map:
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	@SuppressWarnings("unused")
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			nhixinhdep();

			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 1:
			Intent in = new Intent(getApplicationContext(),
					ListViewPlaceActivity.class);
			in.putExtra("key", Key);
			Double tdLat = Cur_location.latitude;
			Double tdLng = Cur_location.longitude;
			in.putExtra("tdLat", tdLat);
			in.putExtra("tdLng", tdLng);
			if (choose3 == true) {
				latk = vitriNhi.latitude;
				lngk = vitriNhi.longitude;

				in.putExtra("latk", latk);
				in.putExtra("lngk", lngk);
			}
			in.putExtra("choose3", choose3);
			startActivity(in);
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 2:
			show_atm();
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 3:
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame, fragment)
					.commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);

		Cur_location = new LatLng(location.getLatitude(),
				location.getLongitude());
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(12).tilt(30).build();
		mGoogleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		// demLocation++;
		// if (demLocation < 2) {
		// mGoogleMap.addMarker(new MarkerOptions()
		// .position(Cur_location)
		// .title("That's me")
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.meflag)));
		// }
		// Log.d("log", "Toa do: " + Cur_location);
		flag = false;
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

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

	/*
	 * ATM + CURRENT LOCATION
	 */
	public void Atm_location(LatLng vitri1, LatLng vitri2) {

		String url = getDirectionsUrl(vitri1, vitri2);
		DownloadTask_LinkWay downloadTask = new DownloadTask_LinkWay();
		// Start downloading json data from Google Directions API
		downloadTask.execute(url);
	}

	/*
	 * AUTOCOMPLETE
	 */

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		try {
			input = URLEncoder.encode(input, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY_Place);
			sb.append("&components=country:vn");
			sb.append("&input=" + input);

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			// Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			// Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						resultList = autocomplete(constraint.toString());
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	/*
	 * STREET
	 */

	public void FindStreetDialog() {
		final Dialog dialog = new Dialog(MainActivity.this, R.style.cust_dialog);
		dialog.setContentView(R.layout.activity_search);
		dialog.setTitle("Find Street");
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		final AutoCompleteTextView StartS = (AutoCompleteTextView) dialog
				.findViewById(R.id.txtStart);
		final AutoCompleteTextView FinishS = (AutoCompleteTextView) dialog
				.findViewById(R.id.txtFinish);
		Button btnOk = (Button) dialog.findViewById(R.id.btnSearch);
		Button btnCan = (Button) dialog.findViewById(R.id.btnCancel);
		final RadioButton rdDriving = (RadioButton) dialog
				.findViewById(R.id.rdDriving);
		final RadioButton rdWalker = (RadioButton) dialog
				.findViewById(R.id.rdWalker);
		final RadioButton rdBus = (RadioButton) dialog.findViewById(R.id.rdBus);
		StartS.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1));
		FinishS.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1));
		rdDriving.setChecked(true);
		choose = 3;
		ImageButton ibtnCur1 = (ImageButton) dialog.findViewById(R.id.btnCurr1);
		ImageButton ibtnCur2 = (ImageButton) dialog.findViewById(R.id.btnCurr2);
		ibtnCur1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FinishS.setText("My Location");
				choose = 0;
			}
		});
		ibtnCur2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartS.setText("My Location");
				choose = 1;
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rdDriving.isChecked()) {
					Mode = 0;
				} else if (rdWalker.isChecked()) {
					Mode = 1;
				} else if (rdBus.isChecked()) {
					Mode = 2;
				}

				if (choose == 0) {
					mGoogleMap.clear();
					if (ktWifi() == true && ktGps() == true) {
						GettingPlaceFrom_CurLocation(StartS);
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet or GPS Connection Error",
								"Please connect to working Internet or GPS connection",
								false);
					}
				} else if (choose == 1) {
					mGoogleMap.clear();
					if (ktWifi() == true && ktGps() == true) {
						GettingPlaceFrom_CurLocation(FinishS);
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet or GPS Connection Error",
								"Please connect to working Internet or GPS connection",
								false);
					}
				} else if (choose == 3) {
					mGoogleMap.clear();
					if (ktWifi() == true) {
						GettingPlace(StartS);
						GettingPlace(FinishS);
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet Connection Error",
								"Please connect to working Internet connection",
								false);
					}

				}
				dialog.dismiss();
			}
		});
		btnCan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
		window.setAttributes(lp);
	}

	/*
	 * ASYNCTASK STREET FROM CURRENT LOCATION
	 */
	public void GettingPlaceFrom_CurLocation(AutoCompleteTextView tv) {
		String location = tv.getText().toString();
		if (location == null || location.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			location = URLEncoder.encode(location, "utf-8");
			// location2 = URLEncoder.encode(location2, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String address1 = "address=" + location;
		String sensor = "sensor=false";
		String url1 = url + address1 + "&" + sensor;
		DownloadTask_FindWay_Location downloadTask = new DownloadTask_FindWay_Location();
		downloadTask.execute(url1);
	}

	/** A class, to download Places from Geocoding webservice */
	private class DownloadTask_FindWay_Location extends
			AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask_FindWay_Location parserTask = new ParserTask_FindWay_Location();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask_FindWay_Location extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				final String name = hmPlace.get("formatted_address");
				LatLng latLng = new LatLng(lat, lng);
				CameraUpdate camUpdate = CameraUpdateFactory
						.newLatLng(new LatLng(lat, lng));
				mGoogleMap.animateCamera(camUpdate);
				mGoogleMap.addMarker(new MarkerOptions().position(latLng)
						.title(name));

				String url = getDirectionsUrl(latLng, Cur_location);
				DownloadTask_LinkWay downloadTask = new DownloadTask_LinkWay();
				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
				cam = false;

			}
		}
	}

	/*
	 * ASYNCTASK STREET mui
	 */
	public void GettingPlace(AutoCompleteTextView tv) {
		String location = tv.getText().toString();
		if (location == null || location.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			location = URLEncoder.encode(location, "utf-8");
			// location2 = URLEncoder.encode(location2, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String address1 = "address=" + location;
		String sensor = "sensor=false";
		String url1 = url + address1 + "&" + sensor;
		DownloadTask_FindWay downloadTask = new DownloadTask_FindWay();
		downloadTask.execute(url1);
	}

	/** A class, to download Places from Geocoding webservice */
	private class DownloadTask_FindWay extends
			AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask_FindWay parserTask = new ParserTask_FindWay();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask_FindWay extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				final String name = hmPlace.get("formatted_address");
				LatLng latLng = new LatLng(lat, lng);
				CameraUpdate camUpdate = CameraUpdateFactory
						.newLatLng(new LatLng(lat, lng));
				mGoogleMap.animateCamera(camUpdate);
				Log.d("test", name + ": ");
				Dem++;
				if (Dem == 1) {
					mGoogleMap.clear();
					diadiem1 = name;
					vitri1 = new LatLng(lat, lng);
					mGoogleMap.addMarker(new MarkerOptions().position(vitri1)
							.title(diadiem1));
				}
				if (Dem == 2) {
					Dem = 0;
					diadiem2 = name;
					vitri2 = new LatLng(lat, lng);
					mGoogleMap.addMarker(new MarkerOptions().position(vitri2)
							.title(diadiem2));

					String url = getDirectionsUrl(vitri1, vitri2);
					DownloadTask_LinkWay downloadTask = new DownloadTask_LinkWay();
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
				flag = false;
				cam = false;

				// mGoogleMap
				// .setOnMarkerClickListener(new OnMarkerClickListener() {
				//
				// @Override
				// public boolean onMarkerClick(Marker arg0) {
				// if (arg0.getTitle().equals(diadiem1)) // if
				// // marker
				// // source
				// // is
				// // clicked
				// {
				// Toast.makeText(getApplicationContext(),
				// "Address: " + diadiem1,
				// Toast.LENGTH_SHORT).show();// display
				// // toast
				//
				// } else if (arg0.getTitle().equals(diadiem2)) // if
				// // marker
				// // source
				// // is
				// // clicked
				// {
				// Toast.makeText(getApplicationContext(),
				// "Address: " + diadiem2,
				// Toast.LENGTH_SHORT).show();// display
				// // toast
				//
				// }
				//
				// return true;
				// }
				//
				// });

			}
		}
	}

	/*
	 * DIRECTION, ASYNCTASK LINK
	 */

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Travelling Mode
		String mode = "mode=driving";
		if (Mode == 0) {
			mode = "mode=driving";
		} else if (Mode == 1) {
			mode = "mode=walking";
		} else if (Mode == 2) {
			mode = "mode=bus";
		}
		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		Log.v("Point", url);
		return url;
	}

	// Fetches data from url passed
	private class DownloadTask_LinkWay extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask_LinkWay parserTask = new ParserTask_LinkWay();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask_LinkWay extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			pDialog.dismiss();

			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);

				lineOptions.width(6);

				// Changing the color polyline according to the mode
				if (Mode == 0)
					lineOptions.color(Color.RED);
				else if (Mode == 1)
					lineOptions.color(Color.GREEN);
				else if (Mode == 2)
					lineOptions.color(Color.BLUE);

			}

			if (result.size() < 1) {
				Toast.makeText(getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}

			line = mGoogleMap.addPolyline(lineOptions);
			// mGoogleMap.addPolyline(lineOptions);
		}
	}

	/*
	 * PLACE
	 */
	public void SearchPlace() {
		final Dialog dialog = new Dialog(MainActivity.this, R.style.cust_dialog);
		dialog.setContentView(R.layout.dialog_search_place);
		dialog.setTitle("Place");
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		final AutoCompleteTextView Start = (AutoCompleteTextView) dialog
				.findViewById(R.id.txtStart);
		Button btnOk = (Button) dialog.findViewById(R.id.btnSearch);
		Button btnCan = (Button) dialog.findViewById(R.id.btnCancel);
		Start.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1));
		mGoogleMap.clear();
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ktWifi() == true) {
					GettingPlace_Search(Start);
				} else {
					alert.showAlertDialog(MainActivity.this,
							"Internet Connection Error",
							"Please connect to working Internet connection",
							false);
				}
				dialog.dismiss();
			}
		});
		btnCan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
		window.setAttributes(lp);
	}

	public void GettingPlace_Search(AutoCompleteTextView tv) {
		String location = tv.getText().toString();
		if (location == null || location.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// if (location2 == null || location2.equals("")) {
		// Toast.makeText(getBaseContext(), "No Place is entered",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			location = URLEncoder.encode(location, "utf-8");
			// location2 = URLEncoder.encode(location2, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String address1 = "address=" + location;
		// String address2 = "address=" + location2;
		String sensor = "sensor=false";
		String url1 = url + address1 + "&" + sensor;
		// String url2 = url + address2 + "&" + sensor;
		DownloadTask_Place downloadTask = new DownloadTask_Place();
		downloadTask.execute(url1);
		// Log.d("test", url1);
	}

	/*
	 * ASYNCTASK PLACE
	 */
	/** A class, to download Places from Geocoding webservice */
	private class DownloadTask_Place extends AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask_Place parserTask = new ParserTask_Place();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask_Place extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {
			pDialog.dismiss();
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				final String name = hmPlace.get("formatted_address");
				mGoogleMap.clear();
				LatLng latLng = new LatLng(lat, lng);
				vitri1 = new LatLng(lat, lng);
				CameraUpdate camUpdate = CameraUpdateFactory
						.newLatLng(new LatLng(lat, lng));
				mGoogleMap.animateCamera(camUpdate);
				mGoogleMap.addMarker(new MarkerOptions().position(latLng)
						.title(name));

				cam = false;

				// mGoogleMap
				// .setOnMarkerClickListener(new
				// GoogleMap.OnMarkerClickListener() {
				// @Override
				// public boolean onMarkerClick(Marker arg0) {
				// // TODO Auto-generated method stub
				//
				// Toast.makeText(getApplicationContext(),
				// "Address: " + name, Toast.LENGTH_SHORT)
				// .show();
				// return false;
				// }
				// });

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	/**
	 * function to load map If map is not created it will create it
	 * */
	private void initilizeMap() {
		if (mGoogleMap == null) {
			// Getting reference to the SupportMapFragment
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			// Getting Google Map
			mGoogleMap = fragment.getMap();

			// Enabling MyLocation in Google Map
			mGoogleMap.setMyLocationEnabled(true);
			// Enable / Disable zooming controls
			mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

			// Enable / Disable my location button
			mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			mGoogleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
			mGoogleMap.getUiSettings().setCompassEnabled(true);

			// check if map is created successfully or not
			if (mGoogleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// SHOW PLACE NEARBY atm
	/** A class, to download Google Places */
	private class PlacesTask_atm extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
				Log.v("lg", data);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {
			ParserTask_atm parserTask = new ParserTask_atm();

			// Start parsing the Google places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}

	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask_atm extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a List construct */
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
			// mGoogleMap.clear();
			if (list.size() == 0) {
				Toast.makeText(getApplicationContext(), "No places",
						Toast.LENGTH_SHORT).show();
			} else {

				for (int i = 0; i < list.size(); i++) {

					// Creating a marker
					MarkerOptions markerOptions = new MarkerOptions();

					// Getting a place from the places list
					HashMap<String, String> hmPlace = list.get(i);

					// Getting latitude of the place
					final double lat = Double.parseDouble(hmPlace.get("lat"));

					// Getting longitude of the place
					final double lng = Double.parseDouble(hmPlace.get("lng"));

					String icon = hmPlace.get("icon");

					// Getting name
					final String name = hmPlace.get("place_name");

					// Getting vicinity
					final String vicinity = hmPlace.get("vicinity");

					final LatLng latLng = new LatLng(lat, lng);
					CameraUpdate camUpdate = CameraUpdateFactory
							.newLatLng(new LatLng(lat, lng));
					mGoogleMap.animateCamera(camUpdate);

					// Setting the position for the marker
					markerOptions.position(latLng);
					markerOptions.title(name + " : Click here");

					// markerOptions.icon(BitmapDescriptorFactory
					// .defaultMarker(BitmapDescriptorFactory.HUE_RED));
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.yourflag));

					// Placing a marker on the touched position
					// mGoogleMap.addMarker(markerOptions);
					Marker m = mGoogleMap.addMarker(markerOptions);

					// Linking Marker id and place reference
					m_reference.put(m.getId(), hmPlace.get("reference"));
					flag = true;
					cam = true;
				}

			}
		}

	}

	/*
	 * SHOW ATM(PLACE)
	 */
	public void show_atm() {

		final Dialog dialog = new Dialog(MainActivity.this, R.style.cust_dialog);
		dialog.setContentView(R.layout.dialog_search_special_key);
		dialog.setTitle("Search");
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		final AutoCompleteTextView tvNam = (AutoCompleteTextView) dialog
				.findViewById(R.id.tvName);
		final AutoCompleteTextView tvRadius = (AutoCompleteTextView) dialog
				.findViewById(R.id.tvRadius);
		final AutoCompleteTextView tvPlace = (AutoCompleteTextView) dialog
				.findViewById(R.id.tvPlace);

		ImageButton btnCur = (ImageButton) dialog.findViewById(R.id.btnCurr);
		String[] key_atm = getResources().getStringArray(R.array.key_atm);
		String[] key_radius = getResources().getStringArray(R.array.radius_atm);
		ArrayAdapter<String> adapterName = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, key_atm);
		ArrayAdapter<String> adapteRadius = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, key_radius);

		tvNam.setAdapter(adapterName);
		tvRadius.setAdapter(adapteRadius);
		tvNam.setThreshold(1);
		tvRadius.setThreshold(1);
		tvPlace.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1));
		Button btnFind = (Button) dialog.findViewById(R.id.btnSearch);
		Button btnFo = (Button) dialog.findViewById(R.id.btnFo);
		choose1 = 1;
		btnCur.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvPlace.setText("My Location");
				choose1 = 0;
			}
		});
		btnFind.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				radius_ne = tvRadius.getText().toString();
				key = tvNam.getText().toString();
				if (choose1 == 1) {
					if (ktWifi() == true) {
						GettingPlace_atmplace(tvPlace);
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet Connection Error",
								"Please connect to working Internet connection",
								false);
					}
					look = 1;
				} else if (choose == 0) {
					if (ktWifi() == true && ktGps() == true) {
						search_atm_cur();
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet Or GPS Connection Error",
								"Please connect to working Internet or GPS connection",
								false);
					}
					look = 0;
				}
				dialog.dismiss();
			}
		});
		btnFo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
		window.setAttributes(lp);
	}

	/*
	 * AUTOCOMPLETE PLACE(ATM)
	 */

	public void GettingPlace_atmplace(AutoCompleteTextView tv) {
		String location = tv.getText().toString();
		if (location == null || location.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			location = URLEncoder.encode(location, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String address1 = "address=" + location;
		String sensor = "sensor=false";
		String url1 = url + address1 + "&" + sensor;
		// Log.d("log", url1);
		DownloadTask_Place_ATM downloadTask = new DownloadTask_Place_ATM();
		downloadTask.execute(url1);
	}

	/*
	 * ASYNCTASK PLACE(ATM)
	 */
	/** A class, to download Places from Geocoding webservice */
	private class DownloadTask_Place_ATM extends
			AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask_Place_ATM parserTask = new ParserTask_Place_ATM();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask_Place_ATM extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {
			pDialog.dismiss();
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				final String name = hmPlace.get("formatted_address");
				mGoogleMap.clear();
				vitriNhi = new LatLng(lat, lng);
				CameraUpdate camUpdate = CameraUpdateFactory
						.newLatLng(new LatLng(lat, lng));
				mGoogleMap.animateCamera(camUpdate);
				mGoogleMap.addMarker(new MarkerOptions().position(vitriNhi)
						.title(name));

				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder(
						"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
				sb.append("location=" + lat + "," + lng);
				sb.append("&radius=" + radius_ne);
				sb.append("&types=atm");
				if (key.equalsIgnoreCase("DongA") == true) {
					name_ne = key.replace("DongA", "Dong%20A");

				} else if (key.equalsIgnoreCase("AChau") == true) {
					name_ne = key.replace("AChau", "A%20chau");
				} else {
					name_ne = key;
				}
				sb.append("&name=" + name_ne);
				sb.append("&sensor=true");
				sb.append("&key=AIzaSyD1EmhtNXN1GpOFOBrfe_pp_pfVzeQViF4");
				Log.d("log", sb.toString());
				PlacesTask_atm placesTask = new PlacesTask_atm();
				placesTask.execute(sb.toString());
			}
			flag = true;
			cam = false;
			// look = true;
		}
	}

	public void search_atm_cur() {
		mGoogleMap.clear();
		StringBuilder sb = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location=" + mLatitude + "," + mLongitude);
		sb.append("&radius=" + radius_ne);
		sb.append("&types=atm");
		if (key.equalsIgnoreCase("DongA") == true) {
			name_ne = key.replace("DongA", "Dong%20A");

		} else if (key.equalsIgnoreCase("AChau") == true) {
			name_ne = key.replace("AChau", "A%20chau");
		} else {
			name_ne = key;
		}
		sb.append("&name=" + name_ne);
		sb.append("&sensor=true");
		sb.append("&key=AIzaSyD1EmhtNXN1GpOFOBrfe_pp_pfVzeQViF4");
		Log.d("log", sb.toString());
		PlacesTask_atm placesTask = new PlacesTask_atm();
		placesTask.execute(sb.toString());
		// look = false;
	}

	/***
	 * NHI XINH DEP
	 * **/

	public void nhixinhdep() {

		final Dialog dialog = new Dialog(MainActivity.this, R.style.cust_dialog);
		dialog.setContentView(R.layout.dialog_nearby_search);
		dialog.setTitle("Information");
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		final AutoCompleteTextView tvPlace = (AutoCompleteTextView) dialog
				.findViewById(R.id.tvPlace);
		tvPlace.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this,
				android.R.layout.simple_list_item_1));

		Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
		Button btnC = (Button) dialog.findViewById(R.id.btnCancel);

		// Array of place types
		mPlaceType = getResources().getStringArray(R.array.key_google);

		// Array of place type names
		mPlaceTypeName = getResources().getStringArray(R.array.key_name);

		// Creating an array adapter with an array of Place types
		// to populate the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

		// Getting reference to the Spinner

		mSprPlaceType = (Spinner) dialog.findViewById(R.id.spr_place_type);

		// Setting adapter on Spinner to set place types
		mSprPlaceType.setAdapter(adapter);

		choose2 = 1;

		ImageButton btnCur = (ImageButton) dialog.findViewById(R.id.btnCurr);

		btnCur.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvPlace.setText("My Location");
				choose2 = 0;
			}
		});

		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int selectedPosition = mSprPlaceType.getSelectedItemPosition();
				okhoa = mPlaceType[selectedPosition];
				Key = okhoa;
				if (choose2 == 1) {
					if (ktWifi() == true) {
						GettingPlace_nhi(tvPlace);
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet Connection Error",
								"Please connect to working Internet connection",
								false);
					}
					look = 1;

				} else if (choose2 == 0) {
					if (ktWifi() == true && ktGps() == true) {
						search_nhi();
					} else {
						alert.showAlertDialog(
								MainActivity.this,
								"Internet or GPS Connection Error",
								"Please connect to working Internet of GPS connection",
								false);
					}
					look = 0;

				}
				dialog.dismiss();
				dem = 0;

			}
		});
		btnC.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		placesListItems.clear();
		dialog.show();
		window.setAttributes(lp);
	}

	/*
	 * AUTOCOMPLETE PLACE (KEY)
	 */

	public void GettingPlace_nhi(AutoCompleteTextView tv) {
		String location = tv.getText().toString();
		if (location == null || location.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			location = URLEncoder.encode(location, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String address1 = "address=" + location;
		String sensor = "sensor=false";
		String url1 = url + address1 + "&" + sensor;
		// Log.d("log", url1);
		DownloadTask_nhi downloadTask = new DownloadTask_nhi();
		downloadTask.execute(url1);
		choose3 = true;
	}

	/*
	 * ASYNCTASK PLACE(KEY)
	 */
	/** A class, to download Places from Geocoding webservice */
	private class DownloadTask_nhi extends AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask_Place_nhi parserTask = new ParserTask_Place_nhi();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask_Place_nhi extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {
			pDialog.dismiss();
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hmPlace = list.get(i);
				double lat = Double.parseDouble(hmPlace.get("lat"));
				double lng = Double.parseDouble(hmPlace.get("lng"));
				final String name = hmPlace.get("formatted_address");
				mGoogleMap.clear();
				vitriNhi = new LatLng(lat, lng);
				CameraUpdate camUpdate = CameraUpdateFactory
						.newLatLng(new LatLng(lat, lng));
				mGoogleMap.animateCamera(camUpdate);
				mGoogleMap.addMarker(new MarkerOptions().position(vitriNhi)
						.title(name));

				// TODO Auto-generated method stub
				StringBuilder sb = new StringBuilder(
						"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
				sb.append("location=" + lat + "," + lng);
				sb.append("&radius=3000");
				sb.append("&types=" + okhoa);
				sb.append("&sensor=true");
				sb.append("&key=AIzaSyD1EmhtNXN1GpOFOBrfe_pp_pfVzeQViF4");
				Log.d("log", sb.toString());
				PlacesTask_atm placesTask = new PlacesTask_atm();
				placesTask.execute(sb.toString());
			}
			flag = true;
			cam = false;
			// look = true;
		}
	}

	public void search_nhi() {
		mGoogleMap.clear();
		StringBuilder sb = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location=" + mLatitude + "," + mLongitude);
		sb.append("&radius=3000");
		sb.append("&types=" + okhoa);
		sb.append("&sensor=true");
		sb.append("&key=AIzaSyD1EmhtNXN1GpOFOBrfe_pp_pfVzeQViF4");
		Log.d("log", sb.toString());
		PlacesTask_atm placesTask = new PlacesTask_atm();
		placesTask.execute(sb.toString());
		choose3 = false;
		// look = false;
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
