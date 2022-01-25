package com.example.myhome.activities.kitchen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.myhome.Constants;
import com.example.myhome.R;
import com.example.myhome.api.KitchenService;

import org.json.JSONException;

public class KitchenActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private KitchenService kitchenService = new KitchenService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
        sp = getSharedPreferences(Constants.SHAREDPREFNAME, Context.MODE_PRIVATE);
        try {
            kitchenService.getRecipe(KitchenActivity.this,   sp.getString(Constants.EMAIL, Constants.EMPTYSTRING),   sp.getString(Constants.TOKEN, Constants.EMPTYSTRING), result -> {
                Log.d(Constants.TAG, result.toString());
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}