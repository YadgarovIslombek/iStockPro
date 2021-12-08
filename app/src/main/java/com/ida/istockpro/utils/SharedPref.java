package com.ida.istockpro.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static SharedPref instance = null;
    public SharedPreferences mySharedPref;
    public SharedPref(Context context){
        mySharedPref = context.getSharedPreferences("isActive", Context.MODE_PRIVATE);
    }


    public void setActive(String key,boolean isActive){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean(key, isActive);
        editor.apply();
    }
    //Bu method Tungi rejimni load qiladi
    public Boolean loadLang(){
        boolean isActive =mySharedPref.getBoolean("isActive", false);
        return isActive;
    }
}
