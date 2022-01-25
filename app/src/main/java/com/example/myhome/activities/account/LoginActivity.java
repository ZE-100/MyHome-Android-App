package com.example.myhome.activities.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhome.Constants;
import com.example.myhome.api.AccountService;
import com.example.myhome.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.LogDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {
    private FloatingActionButton btn_signIn;
    private Button btn_signUp, btn_forgotPassword;
    private EditText et_email, et_password;
    private SharedPreferences sp;
    private AccountService accountService = new AccountService();
    private JSONArray accountNames = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_signIn         =    findViewById(R.id.btn_log_in_login);
        btn_signUp         =    findViewById(R.id.btn_sign_up_login);
        btn_forgotPassword =    findViewById(R.id.btn_forgot_password_login);
        et_email           =    findViewById(R.id.et_username_login);
        et_password        =    findViewById(R.id.et_password_login);
        sp = getSharedPreferences(Constants.SHAREDPREFNAME, Context.MODE_PRIVATE);


        if (!sp.getString(Constants.TOKEN, Constants.EMPTYSTRING).equals(Constants.EMPTYSTRING)){
            getMembersFromApi();
        }

        btn_signIn.setOnClickListener(view -> {
            getAndSaveToken(et_email.getText().toString(), et_password.getText().toString());

        });
        btn_signUp.setOnClickListener(view -> {
            openRegisterActivity();
        });

        btn_forgotPassword.setOnClickListener(view -> {
            openResetPasswordActivity();
        });
    }



    public void getAndSaveToken(String email, String password){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.PASSWORD, password);
        editor.commit();
        try {
            accountService.getLoginToken(getApplicationContext(),email, password, result ->{
                editor.putString(Constants.TOKEN, result.getString(Constants.TOKEN));
                editor.commit();
                getMembersFromApi();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, Constants.LOGINFAILEDERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void getMembersFromApi(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.SHAREDPREFNAME, Context.MODE_PRIVATE);
        String email = sp.getString(Constants.EMAIL, Constants.EMPTYSTRING);
        String token = sp.getString(Constants.TOKEN, Constants.EMPTYSTRING);
        try {
            accountService.getMembers(getApplicationContext(), email, token, result -> {
                parseMemberNamesAndImages(result);
                openMembersActivity(toStringArray(accountNames));
            });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, Constants.MEMBERFAILEDERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void openResetPasswordActivity() {

    }

    public void openMembersActivity(String[] members) {
        Intent intent = new Intent(this, MembersActivity.class);
        intent.putExtra(Constants.MEMBER, members);
        startActivity(intent);
    }
    public void parseMemberNamesAndImages(JSONArray members) throws JSONException {
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            this.accountNames.put(member.getString(Constants.NAME));
        }
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }



}