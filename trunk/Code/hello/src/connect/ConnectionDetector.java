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
 
    
    

}
