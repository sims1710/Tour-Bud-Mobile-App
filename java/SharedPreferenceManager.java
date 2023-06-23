package com.example.tourbud5;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferenceManager {
    private static String USERKEY="Uid";
    private static String AUTHKEY="Logged In";
    private static String TAG="sharedPref";

    public static void storeUserId(Context context,String uid){
        //pass in with +65
        //context from getApplicationContext()
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USERKEY,uid);
        editor.commit();
        Log.d(TAG,"putStringUID:  "+uid);

    }
    public static String getUserId(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        Log.d(TAG,pref.getString(USERKEY, "noVal"));
        return pref.getString(USERKEY, null);

    }



}
