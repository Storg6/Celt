package sgtt.celtkituzes_0_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LoginActivity extends AppCompatActivity {

    public static final String APP_ID = "19F954A6-2211-7ED0-FF1B-BD05FFD80200";
    public static final String SECRET_KEY = "F445C642-63BF-0D19-FFBB-B06809011C00";
    public static final String VERSION = "v1";

  //  private BroadcastReceiver mRegistrationBroadcastReceiver;

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        saveLoginCheckBox = (CheckBox)findViewById(R.id.checkBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);
        final TextView passwordLink = (TextView) findViewById(R.id.tvRePasswod);



        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            etEmail.setText(loginPreferences.getString("email", ""));
            etPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

  /*      mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!!", Toast.LENGTH_LONG).show();
                }
            }
        };*/

     /*   //Check status on google play device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode){
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }else{
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        }else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }*/

        //regisztráció
        registerLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
        //jelszó emlékeztető
        passwordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String mailforpsw = etEmail.getText().toString();
                Backendless.UserService.restorePassword(mailforpsw, new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        Toast.makeText(getApplicationContext(),"Az új jelszó elküldve a megadott email címre!", Toast.LENGTH_SHORT).show();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(getApplicationContext(),"Hiba!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //bejelentkezés
        bLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

               Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                   @Override
                   public void handleResponse(BackendlessUser response) {
                       Toast.makeText(getApplicationContext(),"Sikeres bejelentlezés",Toast.LENGTH_SHORT).show();

                       Intent loginIntent = new Intent(LoginActivity.this, UserAreaActivity.class);
                       LoginActivity.this.startActivity(loginIntent);
                   }

                   @Override
                   public void handleFault(BackendlessFault fault) {
                       Toast.makeText(getApplicationContext(),"Sikertelen bejelentlezés",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });

        Backendless.initApp(this, APP_ID, SECRET_KEY, VERSION);

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        Log.w("UserAreaActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("UserAreaActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }*/
}
