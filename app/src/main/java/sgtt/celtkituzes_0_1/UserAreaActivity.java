package sgtt.celtkituzes_0_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.Format;
import java.util.Date;

import weborb.client.ant.wdm.Table;

public class UserAreaActivity extends AppCompatActivity {
    EditText etFirstName, etLastName, etEmail, etDate;
    int year,month,day;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        BackendlessUser backendlessUser=Backendless.UserService.CurrentUser();
        String email=backendlessUser.getEmail();
        String firstname= (String) backendlessUser.getProperty( "firstname" );
        String lastname= (String) backendlessUser.getProperty( "lastname" );
     //   Date date = (Date) backendlessUser.getProperty("datetime");


        final TextView welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMessage);
        etFirstName  = (EditText) findViewById(R.id.etFirstName);
        etLastName   = (EditText) findViewById(R.id.etLastName);
        etEmail      = (EditText) findViewById(R.id.etEmail);
       // etDate = (EditText) findViewById(R.id.etAge);

        Button bLogout = (Button) findViewById(R.id.bLogout);

        etFirstName.setText(firstname);
        etLastName.setText(lastname);
        etEmail.setText(email);
      //  etDate.setText(s);
      /*  SimpleDateFormat f = new SimpleDateFormat("MM dd yyyy");
        etDate.setText(f.format(new java.util.Date()));
       // etFirstName.setText(et);*/

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!!", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Check status on google play device
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
        }



        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Intent logoutIntent = new Intent(UserAreaActivity.this, LoginActivity.class);
                        UserAreaActivity.this.startActivity(logoutIntent);
                        finish();
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(),"Nem sikerült kijelentkezni!",Toast.LENGTH_SHORT).show();
                    }
                });
                Intent logoutIntent = new Intent(UserAreaActivity.this, LoginActivity.class);
                UserAreaActivity.this.startActivity(logoutIntent);
                finish();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.w("UserAreaActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.w("UserAreaActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
