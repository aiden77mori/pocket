package com.droidoxy.easymoneyrewards.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.droidoxy.easymoneyrewards.BuildConfig;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.constants.Constants;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.LruBitmapCache;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yashdev.easyprefs.EasyPreferences;
import com.yashdev.easyprefs.EasyPrefs;


public class App extends MultiDexApplication implements Constants {

    public static final String TAG = App.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static App mInstance;

    private SharedPreferences sharedPref;
    EasyPrefs easyPrefs;

    private String username, fullname, accessToken, email;
    private long id;
    private int state, errorCode;

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;

        easyPrefs = new EasyPreferences(mInstance,getString(R.string.app_name));

        sharedPref = this.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        this.readData();
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        }

        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void logout() {

        final String accessToken;
        final Long accountId;

        accessToken = this.getAccessToken();
        accountId = this.getId();

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if(!response.getBoolean("error")) {

                                    //Logout success
                                    String j = "kk";
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    App.getInstance().removeData();
                    App.getInstance().readData();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(accountId));
                    params.put("accessToken", accessToken);

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);

        }

        App.getInstance().removeData();
        App.getInstance().readData();
    }

    public void updateBalance(){

        final String data = this.getData();

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, ACCOUNT_BALANCE,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            if(!response.getBoolean("error")){

                                easyPrefs.store("balance",response.getString("user_balance"));
                            }

                        }catch (Exception e){
                            // do nothin
                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", data);
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);

    }

    public Boolean authorize(JSONObject authObj) {

        try {

            if (authObj.has("error_code")) {

                this.setErrorCode(authObj.getInt("error_code"));
            }

            if (!authObj.has("error")) {

                return false;
            }

            if (authObj.getBoolean("error")) {

                return false;
            }

            if (!authObj.has("account")) {

                return false;
            }

            if (!authObj.has("config")) {

                return false;
            }

            JSONArray accountArray = authObj.getJSONArray("account");

            if (accountArray.length() > 0) {

                JSONObject accountObj = (JSONObject) accountArray.get(0);

                this.setFullname(accountObj.getString("fullname"));
                this.setUsername(accountObj.getString("username"));
                this.setEmail(accountObj.getString("email"));
                this.setState(accountObj.getInt("state"));

                easyPrefs.store("acc_status",accountObj.getInt("status"));
                easyPrefs.store("ip_addr", accountObj.getString("ip_addr"));
                easyPrefs.store("mobile", accountObj.getString("mobile"));
                easyPrefs.store("balance", accountObj.getString("points"));
                easyPrefs.store("refer_code", accountObj.getString("refer"));

                if(accountObj.getString("refered").equals("1")){
                    easyPrefs.store("refer_status", true);
                }else{
                    easyPrefs.store("refer_status", false);
                }

            }

            this.setId(authObj.getLong("accountId"));
            this.setAccessToken(authObj.getString("accessToken"));

            this.saveData();

            JSONArray configArray = authObj.getJSONArray("config");

            if (configArray.length() > 0) {

                JSONObject configObj = (JSONObject) configArray.get(0);

                // Company
                easyPrefs.store("COMPANY_NAME", configObj.getString("COMPANY_NAME"));
                easyPrefs.store("COMPANY_SITE", configObj.getString("COMPANY_SITE"));
                easyPrefs.store("SUPPORT_EMAIL", configObj.getString("SUPPORT_EMAIL"));

                // Email Verification
                easyPrefs.store(VERIFY_EMAIL_ACTIVE, configObj.getBoolean("VERIFY_EMAIL_ACTIVE"));

                // Refer
                easyPrefs.store("REFER_ACTIVE", configObj.getBoolean("REFER_ACTIVE"));
                easyPrefs.store("REFER_REWARD", configObj.getString("REFER_REWARD"));

                // Daily checkin
                easyPrefs.store("DAILY_ACTIVE", configObj.getBoolean("DAILY_ACTIVE"));
                easyPrefs.store("DAILY_REWARD", configObj.getString("DAILY_REWARD"));

                // Policy
                easyPrefs.store("POLICY_ACTIVE", configObj.getBoolean("POLICY_ACTIVE"));
                easyPrefs.store("APP_POLICY_URL", configObj.getString("APP_POLICY_URL"));

                // Transactions
                easyPrefs.store("APP_PREFIX", configObj.getString("TRANSACTION_PREFIX"));

                // Contact
                easyPrefs.store("CONTACT_US_ACTIVE", configObj.getBoolean("CONTACT_US_ACTIVE"));
                easyPrefs.store("APP_CONTACT_US_URL", configObj.getString("APP_CONTACT_US_URL"));

                // App
                easyPrefs.store("PACKAGE_NAME", configObj.getString("PACKAGE_NAME"));
                easyPrefs.store("APP_DEBUG_MODE", configObj.getBoolean("APP_DEBUG_MODE"));

                // UI/UX
                easyPrefs.store("APP_TABS_ENABLE", configObj.getBoolean("APP_TABS_ENABLE"));
                easyPrefs.store("APP_NAVBAR_ENABLE", configObj.getBoolean("APP_NAVBAR_ENABLE"));

                // Rate
                easyPrefs.store("RATE_APP_ACTIVE", configObj.getBoolean("RATE_APP_ACTIVE"));

                // Instrutions
                easyPrefs.store("INSTRUCTIONS_ACTIVE", configObj.getBoolean("INSTRUCTIONS_ACTIVE"));

                // Vendor
                easyPrefs.store("VENDOR_SUPPORT_URL", configObj.getString("VENDOR_SUPPORT_URL"));
                easyPrefs.store("APP_LICENSE_URL", configObj.getString("APP_LICENSE_URL"));
                easyPrefs.store("WEB_LICENSE_URL", configObj.getString("WEB_LICENSE_URL"));

                // Share
                easyPrefs.store("SHARE_APP_ACTIVE", configObj.getBoolean("SHARE_APP_ACTIVE"));
                easyPrefs.store("APP_SHARE_TEXT", configObj.getString("APP_SHARE_TEXT"));

				// API Offers
                easyPrefs.store("API_OFFERS_ACTIVE", configObj.getBoolean("API_OFFERS_ACTIVE"));

                // OfferDaddy
                easyPrefs.store(OfferDaddy_AppId, configObj.getString("OfferDaddy_AppId"));
                easyPrefs.store(OfferDaddy_AppKey, configObj.getString("OfferDaddy_AppKey"));

                // AdGateMedia
                easyPrefs.store("AdGateMediaActive", configObj.getBoolean("AdGateMediaActive"));
                easyPrefs.store("AdGate_Media_WallId", configObj.getString("AdGate_Media_WallId"));

                // CpaLead
                easyPrefs.store("CpaLeadActive", configObj.getBoolean("CpaLeadActive"));
                easyPrefs.store("CpaLead_DirectLink", configObj.getString("CpaLead_DirectLink"));

                // AdMantum
                easyPrefs.store(AdMantumActive, configObj.getBoolean("AdMantumActive"));
                easyPrefs.store(AdMantumPubId, configObj.getString("AdMantum_PubId"));
                easyPrefs.store(AdMantumAppId, configObj.getString("AdMantum_AppId"));

                // Wannads
                easyPrefs.store("WannadsActive", configObj.getBoolean("WannadsActive"));
                easyPrefs.store("WannadsApiKey", configObj.getString("WannadsApiKey"));

                // KiwiWall
                easyPrefs.store(KiwiWallActive, configObj.getBoolean("KiwiWallActive"));
                easyPrefs.store(KiwiWallWallId, configObj.getString("KiwiWall_WallId"));
                easyPrefs.store(KiwiWallAPIKEY, configObj.getString("KiwiWall_APIKEY"));

                // AdScendMedia
                easyPrefs.store("AdScendMediaActive", configObj.getBoolean("AdScendMediaActive"));
                easyPrefs.store("AdScendMedia_PubId", configObj.getString("AdScendMedia_PubId"));
                easyPrefs.store("AdScendMedia_AdwallId", configObj.getString("AdScendMedia_AdwallId"));

                // Spin & Win
                easyPrefs.store("SPIN_TITLE", configObj.getString("SPIN_TITLE"));

            }

            return true;

        } catch (JSONException e) {
            //AppUtils.toastLong(this,e.toString());
            return false;
        }
    }

    public void store(String name, Object value) {

        easyPrefs.store(name, value);

    }

    public String getCountryCode(){

        if(!easyPrefs.get("gotCountry",false)){

            CustomRequest balanceRequest = new CustomRequest(Request.Method.GET, "http://ip-api.com/json",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                easyPrefs.store("city",response.getString("city"));
                                easyPrefs.store("country",response.getString("country"));
                                easyPrefs.store("countryCode",response.getString("countryCode"));
                                easyPrefs.store("gotCountry",true);

                            } catch (JSONException e) { //e.printStackTrace();
                            }

                        }},new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}});

            App.getInstance().addToRequestQueue(balanceRequest);
        }

        return easyPrefs.get("countryCode","US");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T value) {

        Object result;
        result = easyPrefs.get(name, value);
        return (T) result;

    }

    public long getId() {

        return this.id;
    }

    public String getBalance() {

        return easyPrefs.get("balance","0");
    }

    public String getData() {

        JSONObject Jsonroot = new JSONObject();
        try{
            Jsonroot.put("clientId", CLIENT_ID);
            Jsonroot.put("accountId", Long.toString(this.getId()));
            Jsonroot.put("accessToken", this.getAccessToken());
            Jsonroot.put("user", this.getUsername());
            Jsonroot.put("ver", BuildConfig.VERSION_NAME);
            Jsonroot.put("pckg", getPackageName());

        }catch (JSONException e){ // do nothin
        }

        return enData(Jsonroot.toString());
    }

    public String getDataCustom(String name, String value) {

        String deviceName = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;

        JSONObject Jsonroot = new JSONObject();
        try{
            Jsonroot.put("clientId", CLIENT_ID);
            Jsonroot.put("accountId", Long.toString(this.getId()));
            Jsonroot.put("accessToken", this.getAccessToken());
            Jsonroot.put("user", this.getUsername());
            Jsonroot.put("name", name);
            Jsonroot.put("value", value);
            Jsonroot.put("dev_name", deviceName);
            Jsonroot.put("dev_man", deviceMan);
            Jsonroot.put("ver", BuildConfig.VERSION_NAME);
            Jsonroot.put("pckg", getPackageName());

        }catch (JSONException e){ // do nothin
        }

        return enData(Jsonroot.toString());
    }

    public String getAuthorize() {

        String fcm_token = App.getInstance().get("FireBaseToken", "");

        JSONObject Jsonroot = new JSONObject();
        try{
            Jsonroot.put("clientId", CLIENT_ID);
            Jsonroot.put("accountId", Long.toString(this.getId()));
            Jsonroot.put("accessToken", this.getAccessToken());
            Jsonroot.put("user", this.getUsername());
            Jsonroot.put("fcm", fcm_token);
            Jsonroot.put("ver", BuildConfig.VERSION_NAME);
            Jsonroot.put("pckg", getPackageName());

        }catch (JSONException e){ // do nothin
        }

        return enData(Jsonroot.toString());
    }

    public void setId(long id) {

        this.id = id;
    }

    public String enData(String Data){ return Data; }
    public String deData(String Data){ return Data; }

    public void setState(int state) {

        this.state = state;
    }

    public int getState() {

        return this.state;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }

    public int getErrorCode() {

        return this.errorCode;
    }

    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }

    public String getFullname() {

        return this.fullname;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getEmail() {

        return this.email;
    }

    public void readData() {

        this.setId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.setUsername(sharedPref.getString(getString(R.string.settings_account_username), ""));
        this.setAccessToken(sharedPref.getString(getString(R.string.settings_account_access_token), ""));
    }

    public void saveData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), this.getId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), this.getUsername()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), this.getAccessToken()).apply();
    }

    private void removeData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), "").apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), "").apply();

        easyPrefs.reset();
        store("isFirstTimeLaunch",false); //no need to show the app into again for logged out user
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}