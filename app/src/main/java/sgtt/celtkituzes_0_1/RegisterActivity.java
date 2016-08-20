package sgtt.celtkituzes_0_1;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;
import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import static android.app.PendingIntent.getActivity;
import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etRePassword;
    private Button bRegister;
    Calendar calendar = Calendar.getInstance();
    TextView display;
    private Date date;
    private int year, month, day;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etFirstName  = (EditText) findViewById(R.id.etFirstName);
        etLastName   = (EditText) findViewById(R.id.etLastName);
        etEmail      = (EditText) findViewById(R.id.etEmail);
        etPassword   = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText) findViewById(R.id.etRePassword);

        Button bRegister = (Button) findViewById(R.id.bRegister);


        display = (TextView) findViewById(R.id.tvDateDisplay);

        Button bDate = (Button) findViewById(R.id.bDate);

        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new DatePickerDialog(RegisterActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        bRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                System.out.println("DEBUG - bRegister onClick");


                String firstname  = etFirstName.getText().toString();
                String lastname   = etLastName.getText().toString();
                String email      = etEmail.getText().toString();
                String password   = etPassword.getText().toString();
                String repassword = etRePassword.getText().toString();

              //  System.out.println("RAW DATE: " + date);

                SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy");
                String dateString = ft.format(date);

               // System.out.println("DATESTRING: " + dateString);

                submitForm();

                //Felhasználó regisztrálása

                BackendlessUser backendlessUser = new BackendlessUser();
                backendlessUser.setPassword(password);
                backendlessUser.setEmail(email);
                backendlessUser.setProperty("datetime",dateString);
                backendlessUser.setProperty("firstname", firstname);
                backendlessUser.setProperty("lastname", lastname);


                Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál!", Toast.LENGTH_SHORT).show();

                        Intent backtoLoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(backtoLoginIntent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(), "Hiba a regisztrációnál", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //Regisztrációs adatok ellenőrzése
    private void submitForm() {

        if (!checkFirstName()) {
            return;
        }
        if (!checkLastName()) {
            return;
        }

        if (!checkEmail()) {
            return;
        }
        if (!checkPassword()) {
            return;
        }



    }

    private boolean checkFirstName() {
        if (etFirstName.getText().toString().trim().isEmpty()) {
            etFirstName.setError("Nem valós Vezetéknév");
            return false;
        }
        return true;
    }

    private boolean checkLastName() {
        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError("Nem valós Keresztnév");
            return false;
        }
        return true;
    }

    private boolean checkEmail() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            etEmail.setError("Nem valós Emailcím");
            requestFocus(etEmail);
            return false;
        }
        return true;
    }

    public boolean checkPassword() {
        String password = etPassword.getText().toString();
        String repassword = etRePassword.getText().toString();
        if (!password.equals(repassword)) {
            etRePassword.setError("Nem egyeznek a jelszavak!");
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View v) {
        if (v.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

          //  System.out.println("DEBUG - DatePickerDialog-OnDateSetListener onDateSet");

            Log.i("dateSetListener","value of year: "+String.valueOf(year));
            Log.i("dateSetListener","value of monthOfYear: "+String.valueOf(monthOfYear+1));
            Log.i("dateSetListener","value of dayOfMonth: "+String.valueOf(dayOfMonth));

            date = new Date();

            Calendar myCal = Calendar.getInstance();
            myCal.set(Calendar.YEAR, year);
            myCal.set(Calendar.MONTH,monthOfYear);
            myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            date = myCal.getTime();
         //   System.out.println("RAW DATE: " + date);

            showDate(year,monthOfYear,dayOfMonth);

         //   System.out.println(date);
        }
    };

    private void showDate(int year, int month, int day) {
        display.setText(new StringBuilder().append(day).append("/")
                .append(month+1).append("/").append(year));
    }

}