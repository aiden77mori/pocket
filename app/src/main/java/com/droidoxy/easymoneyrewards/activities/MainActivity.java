package com.droidoxy.easymoneyrewards.activities;

// import statements
import java.util.Map;
import java.util.Timer;

import android.animation.Animator;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.os.Bundle;
import java.util.HashMap;
import android.os.Handler;
import java.util.TimerTask;

import android.content.Intent;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

// External import statements
import android.app.AlertDialog;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

// Google import statements
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.adapters.ViewPagerAdapter;
import com.droidoxy.easymoneyrewards.constants.Constants;
import com.droidoxy.easymoneyrewards.fragments.TransactionsFragment;
import com.droidoxy.easymoneyrewards.utils.Dialogs;

// import statements
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.SlidingTabLayout;
import com.droidoxy.easymoneyrewards.views.ScrimInsetsFrameLayout;
import com.yashdev.countdowntimer.CountDownTimerView;

import org.json.JSONObject;

/**
 * Created by DroidOXY
 */

public class MainActivity extends ActivityBase {

    // View Variables
    private  Menu menu;
    ViewPagerAdapter adapter;
    MainActivity context;
    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        AppUtils.setWindowUI(this, true);
        init_v3();

    }

    void init_v3(){

        initViews();
        initNavDrawer();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // Main fuctions
    void openInstructions(){
        Intent transactions = new Intent(context, RedeemActivity.class);
        transactions.putExtra("show","instructions");
        startActivity(transactions);
    }

    void openRefer(){
        Intent transactions = new Intent(context, ReferActivity.class);
        transactions.putExtra("show","refer");
        startActivity(transactions);
    }

    void openAbout(){
        startActivity(new Intent(context, AboutActivity.class));
    }

    void openSpinWheel(){
        Intent spin = new Intent(context, SpinWheelActivity.class);
        spin.putExtra("show","spin");
        startActivityForResult(spin,1);
    }

    void openTransactions(){

        Intent transactions = new Intent(context, TransactionActivity.class);
        transactions.putExtra("show","transactions");
        startActivity(transactions);
    }

    void openRedeem(){
        Intent redeem = new Intent(context, RedeemActivity.class);
        redeem.putExtra("show","redeem");
        startActivity(redeem);
    }

    public void dailyCheckin(String Title, String Message){

        if(App.getInstance().get("NEWINSTALL",true)){

            hidepDialog();
            Dialogs.normalDialog(context, Title, Message, false, true, getResources().getString(R.string.cancel), getResources().getString(R.string.proceed), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    App.getInstance().store("NEWINSTALL",false);
                    dailyChekinReward();
                }
            });

        }else{
            hidepDialog();
            dailyChekinReward();
        }

    }

    // AdNetworks
    void openAdMantumOfferWall(){

        String OfferWall_Url = "https://admantum.com/offers/?appid="+App.getInstance().get(AdMantumAppId,"")+"&uid="+App.getInstance().getUsername();

        if(App.getInstance().get(AdMantumActive,true)){

            Intent wallActivity = new Intent(context, WallActivity.class);
            wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
            startActivityForResult(wallActivity, 111);

        }else{
            Dialogs.normalDialog(context,getResources().getString(R.string.adnetwork_disabled),getResources().getString(R.string.adnetwork_disabled_mesage),true,false,"",getResources().getString(R.string.ok),null);

        }

    }

    public void openOfferDaddyOfferWall(){

        String OfferWall_Url = "https://www.offerdaddy.com/wall/"+App.getInstance().get(OfferDaddy_AppId,"")+"/"+App.getInstance().getUsername()+"/";

        Intent wallActivity = new Intent(context, WallActivity.class);
        wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
        startActivityForResult(wallActivity, 111);

    }

    public void openKiwiWallOfferWall(){

        String OfferWall_Url = "https://www.kiwiwall.com/wall/"+App.getInstance().get(KiwiWallWallId,"")+"/"+App.getInstance().getUsername();

        Intent wallActivity = new Intent(context, WallActivity.class);
        wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
        startActivityForResult(wallActivity, 111);

    }

    public void openCustomOfferWall(String offerwall_type, String url) {

        if(offerwall_type.toLowerCase().contains("custom_offerwall_")){

            String OfferWall_Url = url.replace("{user_id}", App.getInstance().getUsername());

            Intent wallActivity = new Intent(context, WallActivity.class);
            wallActivity.putExtra(Constants.OFFER_WALL_URL, OfferWall_Url);
            startActivityForResult(wallActivity, 111);

        }else{

            parseURL(url);

        }

    }

    public void openCpaLeadOfferWall(){

        String OfferWall_Url = App.getInstance().get("CpaLead_DirectLink","")+"&subid="+App.getInstance().getUsername()+"&subid2="+App.getInstance().getUsername();

        if(App.getInstance().get("CpaLeadActive",true)){

            Intent wallActivity = new Intent(context, WallActivity.class);
            wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
            startActivityForResult(wallActivity, 111);

        }else{
            Dialogs.normalDialog(context,getResources().getString(R.string.adnetwork_disabled),getResources().getString(R.string.adnetwork_disabled_mesage),true,false,"",getResources().getString(R.string.ok),null);

        }
    }

    public void openWannadsOfferWall(){

        String OfferWall_Url = "https://wall.wannads.com/wall?apiKey="+App.getInstance().get("WannadsApiKey","")+"&userId="+App.getInstance().getUsername();

        if(App.getInstance().get("WannadsActive",true)){

            Intent wallActivity = new Intent(context, WallActivity.class);
            wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
            startActivityForResult(wallActivity, 111);

        }else{
            Dialogs.normalDialog(context,getResources().getString(R.string.adnetwork_disabled),getResources().getString(R.string.adnetwork_disabled_mesage),true,false,"",getResources().getString(R.string.ok),null);

        }
    }

    public void openWebVideos(){

        Intent webvids = new Intent(context, ReferActivity.class);
        webvids.putExtra("show","webvids");
        startActivityForResult(webvids,1);

    }

    public void openAdScendMediaOfferWall(){

        String OfferWall_Url = "https://asmwall.com/adwall/publisher/"+App.getInstance().get("AdScendMedia_PubId", "")+"/profile/"+App.getInstance().get("AdScendMedia_AdwallId", "")+"?subid1="+App.getInstance().getUsername();

        if(App.getInstance().get("AdScendMediaActive",true)){

            Intent wallActivity = new Intent(context, WallActivity.class);
            wallActivity.putExtra(Constants.OFFER_WALL_URL,OfferWall_Url);
            startActivityForResult(wallActivity, 111);

        }else{
            Dialogs.normalDialog(context,getResources().getString(R.string.adnetwork_disabled),getResources().getString(R.string.adnetwork_disabled_mesage),true,false,"",getResources().getString(R.string.ok),null);
        }

    }

    public void openFyberOfferWall(){

        if(App.getInstance().get("FyberActive",true)){

            Dialogs.warningDialog(context,"Fyber Removed !","Fyber has been Removed from v3.5 onwards. So, please disable this AdNetwork from your Admin Panel",true,false,"",getResources().getString(R.string.ok),null);

        }else{
            Dialogs.normalDialog(context,getResources().getString(R.string.adnetwork_disabled),getResources().getString(R.string.adnetwork_disabled_mesage),true,false,"",getResources().getString(R.string.ok),null);

        }
    }

    void parseURL(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

     private void setOptionTitle(String title){
        Log.d("Doronin", "Points : " + title);
        TextView points = findViewById(R.id.points);
        points.setText(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        //menu.findItem(R.id.points).setTitle(getString(R.string.app_currency).toUpperCase()+" : " + App.getInstance().getBalance());
        return super.onPrepareOptionsMenu(menu);
    }

    void updateBalance() {
        final AlertDialog updating = new SpotsDialog(context, R.style.Custom);
        updating.show();
        updateBalanceInBg();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updating.dismiss();
            }
        }, 1000);

        Animation animation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setRepeatCount(2);
        animation.setDuration(2000);

        findViewById(R.id.sync).setAnimation(animation);
    }

    void updateBalanceInBg() {

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, ACCOUNT_BALANCE,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            if(!response.getBoolean("error")){

                                setOptionTitle(getString(R.string.app_currency).toUpperCase()+" : " +response.getString("user_balance"));
                                App.getInstance().store("balance",response.getString("user_balance"));

                            }else if(response.getInt("error_code") == 699 || response.getInt("error_code") == 999){

                                Dialogs.validationError(context,response.getInt("error_code"));

                            }else if(response.getInt("error_code") == 799) {

                                Dialogs.warningDialog(context, getResources().getString(R.string.update_app), getResources().getString(R.string.update_app_description), false, false, "", getResources().getString(R.string.update), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        AppUtils.gotoMarket(context);
                                    }
                                });

                            }

                        }catch (Exception e){
                            // do nothin
                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("data", App.getInstance().getData());
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);
    }

    // Linked Functions
    void dailyChekinReward(){

        showpDialog();

        CustomRequest dailyCheckinRequest = new CustomRequest(Request.Method.POST, ACCOUNT_CHECKIN,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        hidepDialog();

                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                // Reward received Succesfully
                                Dialogs.succesDialog(context,getResources().getString(R.string.congratulations),App.getInstance().get("DAILY_REWARD","") + " " + getResources().getString(R.string.app_currency) + " " + getResources().getString(R.string.successfull_received),false,false,"",getResources().getString(R.string.ok),null);
                                updateBalanceInBg();

                            }else if(Response.getInt("error_code") == 410){

                                // Reward Taken Today - Try Again Tomorrow
                                showTimerDialog(Response.getInt("error_description"));

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(context,Response.getInt("error_code"));

                            }else if(DEBUG_MODE){

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context,Response.getString("error_code"),Response.getString("error_description"),false,false,"",getResources().getString(R.string.ok),null);

                            }else{

                                // Server error
                                Dialogs.serverError(context, getResources().getString(R.string.ok), null);

                            }

                        }catch (Exception e){

                            if(!DEBUG_MODE){
                                Dialogs.serverError(context, getResources().getString(R.string.ok), null);
                            }else{
                                Dialogs.errorDialog(context,"Got Error",e.toString() + ", please contact developer immediately",false,false,"",getResources().getString(R.string.ok),null);
                            }

                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();

                if(!DEBUG_MODE){
                    Dialogs.serverError(context, getResources().getString(R.string.ok), null);
                }else{
                    Dialogs.errorDialog(context,"Got Error",error.toString(),true,false,"",getResources().getString(R.string.ok),null);
                }

            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("data", App.getInstance().getData());
                return params;
            }
        };

        App.getInstance().addToRequestQueue(dailyCheckinRequest);

    }

    public void openOfferWall(String Title, String SubTitle, String Type, String URL){
        Log.d("Doronin", Type);
        switch (Type) {

            case "checkin":

                showpDialog();
                dailyCheckin(Title, SubTitle);

                break;

            case "redeem":

                openRedeem();

                break;

            case "refer":

                openRefer();

                break;

            case "about":

                openAbout();

                break;

            case "spin":

                openSpinWheel();

                break;

            case "instructions":

                openInstructions();

                break;

            case "transactions":

                openTransactions();

                break;

            case "share":

                AppUtils.shareApplication(context);

                break;

            case "rate":

                AppUtils.gotoMarket(context);

                break;

            case "webvids":

                openWebVideos();

                break;

            case "admantum":

                openAdMantumOfferWall();

                break;

            case "cpalead":

                openCpaLeadOfferWall();

                break;

            case "wannads":

                openWannadsOfferWall();

                break;

            case "kiwiwall":

                openKiwiWallOfferWall();

                break;

            case "offerdaddy":

                openOfferDaddyOfferWall();

                break;

            case "fyber":

                openFyberOfferWall();

                break;

            case "adscendmedia":

                openAdScendMediaOfferWall();

                break;

            default:

                openCustomOfferWall(Type,URL);

                break;
        }
    }

    void showTimerDialog(int TimeLeft){

        CountDownTimerView timerView = new CountDownTimerView(context);
        timerView.setTextSize(getResources().getInteger(R.integer.daily_checkin_timer_size));
        timerView.setPadding(0,0,0,25);
        timerView.setGravity(Gravity.CENTER);
        timerView.setTime(TimeLeft * 1000);
        timerView.startCountDown();
        Dialogs.customDialog(context, timerView,getResources().getString(R.string.daily_reward_taken),false,false,"",getResources().getString(R.string.ok),null);

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) { finish(); return; }
        context.doubleBackToExitPressedOnce = true;

        AppUtils.toastShort(context,getString(R.string.click_back_again));

        new Handler().postDelayed(new Runnable() { @Override public void run() { doubleBackToExitPressedOnce = false; }}, 1500);

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateBalanceInBg();

    }

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    ScrimInsetsFrameLayout mScrimInsetsFrameLayout;

    void initViews(){

        int Numboftabs = 1;

        ViewPager pager = findViewById(R.id.homepager);
        SlidingTabLayout tabs = findViewById(R.id.tabs);
        CharSequence Titles[] = {getResources().getString(R.string.home)};
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        if(App.getInstance().get("APP_TABS_ENABLE",false)){

            Numboftabs = 2;
            CharSequence Titles2[] = {getResources().getString(R.string.home), getResources().getString(R.string.transactions)};
            adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles2,Numboftabs);
            tabs.setVisibility(View.VISIBLE);

            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                tabs.setElevation(4);
            }

        }

        pager.setAdapter(adapter);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);

        // Navigation Drawer
        mDrawerLayout = findViewById(R.id.main_activity_DrawerLayout);
    }

    void initNavDrawer() {

        invalidateOptionsMenu();

        // Refer & Earn
        ImageButton refer = findViewById(R.id.refer);
        if(!App.getInstance().get("REFER_ACTIVE",true)){ refer.setVisibility(View.GONE); }
        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRefer();
                AppUtils.setAnimation(view);
                mDrawerLayout.closeDrawers();
            }
        });

        // Redeem
        ImageButton redeem = findViewById(R.id.redeem);
        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRedeem();
                AppUtils.setAnimation(view);
                mDrawerLayout.closeDrawers();
            }
        });

        ImageButton transaction = findViewById(R.id.nav_transactions);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTransactions();
                AppUtils.setAnimation(view);
                mDrawerLayout.closeDrawers();

            }
        });


        ImageButton sync = findViewById(R.id.sync);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateBalance();
            }
        });

        TextView points = findViewById(R.id.points);
        points.setText(getString(R.string.app_currency).toUpperCase()+" : " + App.getInstance().getBalance());

        ImageButton home = (ImageButton)findViewById(R.id.home);
        home.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_active));

//        ImageButton transaction = (ImageButton)findViewById(R.id.nav_transactions);
//        transaction.setBackgroundDrawable(getResources().getDrawable(R.drawable.calendar_normal));
//
//        ImageButton refer = (ImageButton)findViewById(R.id.refer);
//        refer.setBackgroundDrawable(getResources().getDrawable(R.drawable.alarm_normal));

    }
}