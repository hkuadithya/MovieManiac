package com.adithyaupadhya.moviemaniac.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adithyaupadhya.database.DBConstants;
import com.adithyaupadhya.database.sharedpref.AppPreferenceManager;
import com.adithyaupadhya.moviemaniac.R;
import com.adithyaupadhya.moviemaniac.base.Utils;
import com.adithyaupadhya.moviemaniac.navdrawer.NavigationActivity;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.AppIntentConstants;
import com.adithyaupadhya.newtorkmodule.volley.networkconstants.NetworkConstants;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        FacebookCallback<LoginResult>,
        GraphRequest.GraphJSONObjectCallback {

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mApiClient;
    private CallbackManager mFacebookCallbackManager;
    private View mProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mProgressLayout = findViewById(R.id.progressLayout);

        FacebookSdk.sdkInitialize(getApplicationContext());
        initializeGoogleSignIn();
        initializeFacebookSignIn();
    }

    //  HANDLING GOOGLE PLUS SIGN IN:

    private void initializeGoogleSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.google_sign_in);
        //signInButton.setColorScheme(SignInButton.COLOR_DARK);

        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setScopes(new Scope[]{new Scope(Scopes.PLUS_LOGIN)});

        for (int i = 0; i < googleSignInButton.getChildCount(); i++)
            if (googleSignInButton.getChildAt(i) instanceof TextView) {
                TextView textView = (TextView) googleSignInButton.getChildAt(i);
                textView.setTextSize(15);
                textView.setText("Sign in with Google");
                textView.setPadding(0, 0, 20, 0);
                break;
            }

        googleSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in:
                googlePlusLogin();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Log.d("MMVOLLEY", connectionResult + "");
        Toast.makeText(SignInActivity.this, "Google Play connection error", Toast.LENGTH_SHORT).show();
        mProgressLayout.setVisibility(View.GONE);
    }

    private void googlePlusLogin() {
        mApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googlePlusLogout() {
        // If logged in
        if (mApiClient.isConnected())
            Auth.GoogleSignInApi.signOut(mApiClient);
    }


    //  HANDLING FACEBOOK SIGN IN:
    private void initializeFacebookSignIn() {
        LoginButton facebookSignInButton = (LoginButton) findViewById(R.id.facebook_sign_in);
        facebookSignInButton.setReadPermissions("public_profile", "email");
        mFacebookCallbackManager = CallbackManager.Factory.create();
        facebookSignInButton.registerCallback(mFacebookCallbackManager, this);
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), this);
        Bundle params = new Bundle();
        params.putString(AppIntentConstants.FIELDS, "id,name,picture,email");
        request.setParameters(params);
        request.executeAsync();
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {

        // Log.d("MMVOLLEY ", object.toString());

        try {
            String profilePic = NetworkConstants.FACEBOOK_PROFILE_PIC_URL.replace("userid", object.getString("id"));
            setAppPreferenceData(object.getString("id"), object.getString("email"), profilePic, object.getString("name"));
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
        mProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(this, "Facebook/Network Error! Please try again.", Toast.LENGTH_SHORT).show();
        mProgressLayout.setVisibility(View.GONE);
    }

    // ON ACTIVITY RESULT FOR GOOGLE AND FACEBOOK LOG IN:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgressLayout.setVisibility(View.VISIBLE);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result != null && result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                setAppPreferenceData(account.getId(), account.getEmail(), String.valueOf(account.getPhotoUrl()), account.getDisplayName());

                startActivity(new Intent(this, NavigationActivity.class));

                // logout after successful authentication
                // googlePlusLogout();
                finish();
            } else {
                mProgressLayout.setVisibility(View.GONE);

                if (Utils.isNetworkAvailable(this))
                    Toast.makeText(SignInActivity.this, "Google Authentication Failed! Please try again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SignInActivity.this, "Network Error! Please connect to your wifi.", Toast.LENGTH_LONG).show();
            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setAppPreferenceData(String userId, String email, String profilePic, String name) {
        AppPreferenceManager preference = AppPreferenceManager.getAppPreferenceInstance(this);
        // Log.d("PROFILE ", userId + " " + email + " " + profilePic + " " + name);
        preference.setPreferenceData(DBConstants.USER_ID, userId);
        preference.setPreferenceData(DBConstants.USER_EMAIL, email);
        preference.setPreferenceData(DBConstants.USER_PROFILE_PIC, profilePic);
        preference.setPreferenceData(DBConstants.USER_NAME, name);

        //Fabric.with(getApplicationContext(), Crashlytics.getInstance());
        Crashlytics.setUserName(name);
        Crashlytics.setUserEmail(email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
        //Auth.GoogleSignInApi.signOut(mApiClient);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mApiClient.isConnected())
            mApiClient.disconnect();
    }
}
