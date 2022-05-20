package com.droidoxy.easymoneyrewards.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;
import com.google.android.gms.auth.api.Auth;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;

import static com.droidoxy.easymoneyrewards.Config.ENABLE_EMAIL_LOGIN;

/**
 * Created by DroidOXY
 */

public class LoginActivity extends ActivityBase implements GoogleApiClient.OnConnectionFailedListener{

    EditText signinUsername, signinPassword;
    private static final int RC_SIGN_IN = 777;

    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppUtils.setTransparentStatusbar(this);
        AppUtils.hideNavigationBar(this);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        signinUsername = findViewById(R.id.signinUsername);
        signinPassword = findViewById(R.id.signinPassword);

        Button loginBtn = findViewById(R.id.signinBtn);

        TextView mActionForgot = findViewById(R.id.actionForgot);
        TextView register = findViewById(R.id.register);

        if(!ENABLE_EMAIL_LOGIN) {

            signinUsername.setVisibility(View.GONE);
            signinPassword.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            mActionForgot.setVisibility(View.GONE);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = signinUsername.getText().toString();
                password = signinPassword.getText().toString();

                if (!App.getInstance().isConnected()) {

                    AppUtils.toastShort(LoginActivity.this, getResources().getString(R.string.msg_network_error));

                } else if (checkUsername() && checkPassword()) {

                    signIn(username,password);

                }
            }
        });

        mActionForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, RecoveryActivity.class);
                startActivity(i);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

//        App.getInstance().getCountryCode();
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

//        SignInButton btnSignIn = findViewById(R.id.btn_sign_in);
//        if(!ENABLE_GMAIL_LOGIN) {
//
//            btnSignIn.setVisibility(View.GONE);
//
//        }
//
//        // Customizing Google button
//        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
//        btnSignIn.setScopes(gso.getScopeArray());
//
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });

        // facebook
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
//
//        mCallbackManager = CallbackManager.Factory.create();
//
//        LoginButton mLoginButton = findViewById(R.id.login_button);
//        if(!ENABLE_FACEBOOK_LOGIN){
//            mLoginButton.setVisibility(View.GONE);
//        }

//        mLoginButton.setReadPermissions(Arrays.asList(EMAIL));
//
//        // Register a callback to respond to the user
//        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                setResult(RESULT_OK);
//
//                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject jsonObject,GraphResponse response) {
//                                try {
//
//                                    String email =  jsonObject.getString("email");
//                                    String user = getUsername(email);
//
//                                    signIn(user,user);
//
//                                } catch(JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender, birthday");
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//                setResult(RESULT_CANCELED);
//
//                //AppUtils.toastShort(LoginActivity.this,"user cancelled");
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                // Handle exception
//                AppUtils.toastShort(LoginActivity.this,"some error : " + e);
//
//            }
//        });
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in success.
            GoogleSignInAccount acct = result.getSignInAccount();

            String email = acct.getEmail();

            if(email != null){
                String user = getUsername(email);
                signIn(user,user);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//
//            // is Logged in Cache
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//
//        } else {
//
//            // is Logged in Expired
//            showpDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
//                    hidepDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An error has occurred in Google APIs
        if(DEBUG_MODE){ Log.d(TAG, "onConnectionFailed:" + connectionResult);}
    }

    String getUsername(String email){

        String emailtext = email.substring(0,email.lastIndexOf("@"));

        if(emailtext.contains(".")){
            username = emailtext.replace(".", "");
        }else{
            username = emailtext;
        }

        return username;
    }

    public void signIn(final String user, final String password) {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (App.getInstance().authorize(response)) {

                            if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {

                                Intent i = new Intent(LoginActivity.this, AccountActvity.class);
                                startActivity(i);

                                ActivityCompat.finishAffinity(LoginActivity.this);

                            } else {

                                App.getInstance().logout();
                                Dialogs.warningDialog(LoginActivity.this,"", getResources().getString(R.string.msg_account_blocked),false,false,"", getResources().getString(R.string.ok), null);

                            }

                        } else if(App.getInstance().getErrorCode() == 699 || App.getInstance().getErrorCode() == 999){

                            Dialogs.validationError(LoginActivity.this,App.getInstance().getErrorCode());

                        } else {

                            Dialogs.warningDialog(LoginActivity.this,"", getResources().getString(R.string.error_signin),false,false,"", getResources().getString(R.string.ok), null);

                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Dialogs.warningDialog(LoginActivity.this,"", getResources().getString(R.string.error_data_loading),false,false,"", getResources().getString(R.string.ok), null);

                if(DEBUG_MODE){Log.e("Profile", "Malformed JSON: " + error.getMessage());}

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", password);
                params.put("clientId", CLIENT_ID);

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean checkUsername() {

        username = signinUsername.getText().toString();

        signinUsername.setError(null);

        if (username.length() == 0) {

            signinUsername.setError(getString(R.string.error_field_empty));

            return false;
        }

        return  true;
    }

    public Boolean checkPassword() {

        password = signinPassword.getText().toString();

        signinPassword.setError(null);

        if (username.length() == 0) {

            signinPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        return  true;
    }

    @Override
    public void onBackPressed(){

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
