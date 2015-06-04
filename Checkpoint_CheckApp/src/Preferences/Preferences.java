package Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	//設定檔儲存
    public static void setConfig(Context context,String name,String key,
    String value) 
    {
         SharedPreferences settings = context.getSharedPreferences(name,0);
         SharedPreferences.Editor PE = settings.edit();
         PE.putString(key, value);
         PE.commit();
    }
     
    //設定檔讀取
    public static String getConfig(Context context , String name , String 
    key , String def) 
    {
         SharedPreferences settings = context.getSharedPreferences(name,0);
         return settings.getString(key, def);
    }
}
