package smartzero.eightnoteight.nullform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Firebase fbref = null;
    private final String FIREBASE_APP_URL = "https://nullform.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Firebase initial setup for this application.
        Firebase.setAndroidContext(this);
        if (fbref == null) {
            fbref = new Firebase(FIREBASE_APP_URL);
        }

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void attemptRegister(View view) {

        if (mConfirmPasswordView.getVisibility() == View.GONE) {
            mConfirmPasswordView.setVisibility(View.VISIBLE);
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmpassword = mConfirmPasswordView.getText().toString();

        // check if the all the required fields are filled
        if (StringUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");
            mEmailView.requestFocus();
            return;
        }
        if (StringUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");
            mPasswordView.requestFocus();
            return;
        }
        if (StringUtils.isEmpty(confirmpassword)) {
            mConfirmPasswordView.setError("This field is required");
            mConfirmPasswordView.requestFocus();
            return;
        }
        // if there are any invalid entries focus on first invalid entry
        View firstInvalidEntry = null;

        // check for validity of the entries.
        if (!isValidEmail(email)) {
            mEmailView.setError("Invalid Email");
            firstInvalidEntry = mEmailView;
        }
        if (!isValidPassword(password)) {
            mPasswordView.setError("Invalid Password");
            if (firstInvalidEntry == null) {
                firstInvalidEntry = mPasswordView;
            }
        }
        if (!StringUtils.equals(password, confirmpassword)) {
            mConfirmPasswordView.setError("Didn't match with the password");
            if (firstInvalidEntry == null) {
                firstInvalidEntry = mConfirmPasswordView;
            }
        }

        if (firstInvalidEntry != null) {
            // force the user to resolve the Invalid Entries
            firstInvalidEntry.requestFocus();
        }
        else {
            // show a progress till user is created or an error encountered trying the same.
            showProgress(true);
            fbref.createUser(email, password, new RegisterValueResultHandler());
        }
    }

    public void attemptLogin(View view) {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!StringUtils.isEmpty(password) && !isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (StringUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off Firebase Login System
            showProgress(true);
            /*
            System.out.println("< fbref.authWithPassword");

            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    testInternet();
                    return null;
                }
                protected void onPostExecute(Void result) {

                }
            }.execute();
            */
            fbref.authWithPassword(email, password, new LoginAuthResultHandler());
        }
    }

    public void testInternet() {
        try {
            URL yahoo = new URL("http://httpbin.org/get");
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println("internet working> " + inputLine);
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class LoginAuthResultHandler implements Firebase.AuthResultHandler {

        @Override
        public void onAuthenticated(AuthData authData) {
            String uid = authData.getUid();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("uid", uid);
            showProgress(false);
            LoginActivity.this.startActivity(i);
            LoginActivity.this.finish();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
            showProgress(false);
        }
    }

    public class RegisterValueResultHandler implements Firebase.ValueResultHandler<Map<String, Object>> {
        @Override
        public void onSuccess(Map<String, Object> result) {
            String uid = (String)result.get("uid");
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("uid", uid);
            showProgress(false);
            LoginActivity.this.startActivity(i);
            LoginActivity.this.finish();
        }

        @Override
        public void onError(FirebaseError firebaseError) {
            Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
            showProgress(false);
        }

    }
}

