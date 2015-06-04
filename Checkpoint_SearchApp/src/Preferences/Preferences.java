package Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	//�]�w���x�s
    public static void setConfig(Context context,String name,String key,
    String value) 
    {
         SharedPreferences settings = context.getSharedPreferences(name,0);
         SharedPreferences.Editor PE = settings.edit();
         PE.putString(key, value);
         PE.commit();
    }
     
    //�]�w��Ū��
    public static String getConfig(Context context , String name , String 
    key , String def) 
    {
         SharedPreferences settings = context.getSharedPreferences(name,0);
         return settings.getString(key, def);
    }
}
