package connect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
 
public class ConnectionDetector {
 
    private Context _context;
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;
 
    public ConnectionDetector(Context context){
        this._context = context;
    }
 
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        	  NetworkInfo wifiNetwork = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	    if (wifiNetwork != null && wifiNetwork.isConnected()) {
        	      return true;
        	    }

        	    NetworkInfo mobileNetwork = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        	    if (mobileNetwork != null && mobileNetwork.isConnected()) {
        	      return true;
        	    }

        	    NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        	    if (activeNetwork != null && activeNetwork.isConnected()) {
        	      return true;
        	    }
        	    return false;
    }
    

}
