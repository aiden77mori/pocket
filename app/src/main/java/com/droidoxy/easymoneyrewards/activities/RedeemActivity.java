package com.droidoxy.easymoneyrewards.activities;

// import statements
import java.util.Map;
import java.util.Timer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.os.Bundle;
import java.util.HashMap;
import android.os.Handler;
import java.util.TimerTask;

import android.view.MenuItem;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

// External import statements
import android.app.AlertDialog;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
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
import com.droidoxy.easymoneyrewards.adapters.PayoutsAdapter;
import com.droidoxy.easymoneyrewards.adapters.RedeemPagerAdapter;
import com.droidoxy.easymoneyrewards.adapters.ViewPagerAdapter;
import com.droidoxy.easymoneyrewards.constants.Constants;
import com.droidoxy.easymoneyrewards.fragments.InstructionsFragment;
import com.droidoxy.easymoneyrewards.fragments.RedeemFragment;
import com.droidoxy.easymoneyrewards.fragments.ReferFragment;
import com.droidoxy.easymoneyrewards.fragments.TransactionsFragment;
import com.droidoxy.easymoneyrewards.fragments.VideosFragment;
import com.droidoxy.easymoneyrewards.model.Transactions;
import com.droidoxy.easymoneyrewards.utils.Dialogs;

// import statements
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.UtilsMiscellaneous;
import com.droidoxy.easymoneyrewards.utils.SlidingTabLayout;
import com.droidoxy.easymoneyrewards.views.ScrimInsetsFrameLayout;
import com.yashdev.countdowntimer.CountDownTimerView;

import org.json.JSONObject;

/**
 * Created by DroidOXY
 */

public class RedeemActivity extends ActivityBase {

    // View Variables
    private  Menu menu;
    RedeemPagerAdapter adapter;
    RedeemActivity context;
    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        context = this;

        AppUtils.setWindowUI(this, true);

        //handleFragments(getIntent());
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
    void openHomes(){
        Intent transactions = new Intent(context, MainActivity.class);
        transactions.putExtra("show","instructions");
        startActivity(transactions);
    }

    void openRefer(){
        Intent transactions = new Intent(context, ReferActivity.class);
        transactions.putExtra("show","refer");
        startActivity(transactions);
    }

    void openTransactions(){

        Intent transactions = new Intent(context, TransactionActivity.class);
        transactions.putExtra("show","transactions");
        startActivity(transactions);
    }

    void openRedeem(){
        Intent redeem = new Intent(context, RedeemActivity.class);
        startActivity(redeem);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        //menu.findItem(R.id.points).setTitle(getString(R.string.app_currency).toUpperCase()+" : " + App.getInstance().getBalance());
        return super.onPrepareOptionsMenu(menu);
    }

    void updateBalance() {
        final AlertDialog updating = new SpotsDialog(context, R.style.Custom);
        updating.show();
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    ScrimInsetsFrameLayout mScrimInsetsFrameLayout;

    void initViews(){

        int Numboftabs = 1;

        ViewPager pager = findViewById(R.id.redeempager);
        SlidingTabLayout tabs = findViewById(R.id.tabs);
        CharSequence Titles[] = {getResources().getString(R.string.home)};
        adapter = new RedeemPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        if(App.getInstance().get("APP_TABS_ENABLE",false)){

            Numboftabs = 2;
            CharSequence Titles2[] = {getResources().getString(R.string.home), getResources().getString(R.string.transactions)};
            adapter =  new RedeemPagerAdapter(getSupportFragmentManager(),Titles2,Numboftabs);
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
        mDrawerLayout = findViewById(R.id.redeem_activity_DrawerLayout);
    }

    public void Redeem(String title, String subtitle, String message, String amount, String points, final String payoutId, String status, String image){

        if(Integer.parseInt(App.getInstance().getBalance()) >= Integer.parseInt(points)){

            final EditText editText = new EditText(context);
            editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(99)});
            editText.setMinLines(2);

            Dialogs.editTextDialog(context, editText, message, false, true, context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.proceed), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

                    String payoutTo = editText.getText().toString();

                    if(!payoutTo.isEmpty()){

                        if(payoutTo.length() < 4){

                            Dialogs.errorDialog(context,getResources().getString(R.string.error),getResources().getString(R.string.enter_something),true,false,"",getResources().getString(R.string.ok),null);

                        }else{

                            sweetAlertDialog.dismiss();
                            showpDialog();
                            processRedeem(payoutId, payoutTo);
                        }

                    }else{

                        Dialogs.errorDialog(context,getResources().getString(R.string.error),getResources().getString(R.string.enter_something),true,false,"",getResources().getString(R.string.ok),null);
                    }
                }
            });

        }else{

            Dialogs.warningDialog(context,  context.getResources().getString(R.string.oops),context.getResources().getString(R.string.no_enough)+" "+context.getResources().getString(R.string.app_currency)+" "+context.getResources().getString(R.string.to)+" "+context.getResources().getString(R.string.redeem), false, false, "", context.getResources().getString(R.string.ok), null);

        }

    }

    void processRedeem(final String payoutId, final String payoutTo){

        CustomRequest redeemRequest = new CustomRequest(Request.Method.POST, ACCOUNT_REDEEM,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        hidepDialog();

                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                // Success
                                Dialogs.succesDialog(context,getResources().getString(R.string.redeem_success_title),getResources().getString(R.string.redeem_succes_message),false,false,"",getResources().getString(R.string.ok),null);

                                App.getInstance().updateBalance();

                            }else if(Response.getInt("error_code") == 420){

                                // No Enough Balance
                                Dialogs.warningDialog(context,  context.getResources().getString(R.string.oops),context.getResources().getString(R.string.no_enough)+" "+context.getResources().getString(R.string.app_currency)+" "+context.getResources().getString(R.string.to)+" "+context.getResources().getString(R.string.redeem), false, false, "", context.getResources().getString(R.string.ok), null);

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(context,Response.getInt("error_code"));

                            }else if(DEBUG_MODE){

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context,Response.getString("error_code"),Response.getString("error_description"),false,false,"",getResources().getString(R.string.ok),null);

                            }else{

                                // Server error
                                Dialogs.serverError(context, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                    }
                                });
                            }

                        }catch (Exception e){

                            if(!DEBUG_MODE){
                                Dialogs.serverError(context, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                    }
                                });
                            }else{
                                Dialogs.errorDialog(context,"Got Error",e.toString() + ", please contact developer immediately",true,false,"","ok",null);
                            }

                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();

                if(!DEBUG_MODE){
                    Dialogs.serverError(context, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
                }else{
                    Dialogs.errorDialog(context,"Got Error",error.toString(),true,false,"","ok",null);
                }

            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", App.getInstance().getDataCustom(payoutId, payoutTo));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(redeemRequest);

    }



    void initNavDrawer(){
        ImageButton back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ImageButton redeem = (ImageButton)findViewById(R.id.redeem);
        redeem.setBackgroundDrawable(getResources().getDrawable(R.drawable.redeem_active));

        ImageButton transaction = (ImageButton)findViewById(R.id.nav_transactions);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.setAnimation(view);
                openTransactions();
            }
        });

        ImageButton refer = (ImageButton)findViewById(R.id.refer);
        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.setAnimation(view);
                openRefer();
            }
        });

        ImageButton home = (ImageButton)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.setAnimation(view);
                openHomes();
            }
        });

        home.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_normal));

    }

}