package androidumps.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by admin on 6/14/2015.
 */
public class CallBlockerReciever extends BroadcastReceiver {

    SharedPreferences SHARED_PREFERENCES;
    private static final String SHARED_PREF_NAME = "BLACKLIST";
    public String INCOMING_NUMBER;
    private Map<String,?> MAP_BLOCKED_NUMBERS;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        SHARED_PREFERENCES = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        INCOMING_NUMBER = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        MAP_BLOCKED_NUMBERS = SHARED_PREFERENCES.getAll();

        //this block will check whether incoming call number is present in the blocklist or not
        //if blocklist contains incoming call number,then call will get disconnected automatically
        if( MAP_BLOCKED_NUMBERS.containsValue(INCOMING_NUMBER)){
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (!killCall(context)) {
                    Log.d("TAG","PhoneStateReceiver **Unable to kill incoming call");
                }
                }
            }
        }

    // this method is used to disconnect call
    public boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
            //  Toast.makeText(context, "success", Toast.LENGTH_LONG).show();

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d("", "PhoneStateReceiver **" + ex.toString());
            // Toast.makeText(context, "failure", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
