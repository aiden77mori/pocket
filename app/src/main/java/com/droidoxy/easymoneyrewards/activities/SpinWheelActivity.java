package com.droidoxy.easymoneyrewards.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;
import com.google.android.gms.ads.LoadAdError;
import com.yashdev.countdowntimer.CountDownTimerView;
import com.yashdev.libspin.SpinWheel;
import com.yashdev.libspin.model.SpinItem;



import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONObject;

/**
 * Created by DroidOXY
 */

public class SpinWheelActivity extends ActivityBase {

    List<SpinItem> data = new ArrayList<>();
    private InterstitialAd interstitial;
    AdRequest adRequest;
    SpinWheelActivity context;
    String Current_Date;
    AnimatorSet animButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        context = this;

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(App.getInstance().get("SPIN_TITLE",getResources().getString(R.string.app_name)));
        }

        final SpinWheel spinWheel = findViewById(R.id.spinWheel);

        ImageView spinLogo = findViewById(R.id.spinLogo);
        Button spinButton = findViewById(R.id.spinButton);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("dd");
        Current_Date = dd.format(c.getTime());

        init_admob();

        SpinItem item1 = new SpinItem();
        item1.topText = "0";
        item1.icon = R.drawable.ic_coins;
        item1.color = 0xffFFF3E0;
        data.add(item1);

        SpinItem item2 = new SpinItem();
        item2.topText = "1";
        item2.icon = R.drawable.ic_coins;
        item2.color = 0xffFFE0B2;
        data.add(item2);

        SpinItem item3 = new SpinItem();
        item3.topText = "2";
        item3.icon = R.drawable.ic_coins;
        item3.color = 0xffFFCC80;
        data.add(item3);

        SpinItem item4 = new SpinItem();
        item4.topText = "3";
        item4.icon = R.drawable.ic_coins;
        item4.color = 0xffFFF3E0;
        data.add(item4);

        SpinItem item5 = new SpinItem();
        item5.topText = "4";
        item5.icon = R.drawable.ic_coins;
        item5.color = 0xffFFE0B2;
        data.add(item5);

        SpinItem item6 = new SpinItem();
        item6.topText = "5";
        item6.icon = R.drawable.ic_coins;
        item6.color = 0xffFFCC80;
        data.add(item6);


        spinWheel.setData(data);
        spinWheel.setRound(5);
        spinWheel.setTouchEnabled(false);

        // bg animation
        List<Animator> animList = new ArrayList<Animator>();

        ObjectAnimator anim = ObjectAnimator.ofFloat(findViewById(R.id.bg), "scaleX", 1.1f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        anim = ObjectAnimator.ofFloat(findViewById(R.id.bg), "scaleY", 1.1f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        animButton = new AnimatorSet();
        animButton.playTogether(animList);
        animButton.setDuration(3000);
        animButton.start();

        // spin button animation
        animList = new ArrayList<Animator>();

        anim = ObjectAnimator.ofFloat(spinButton, "scaleX", 0.95f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        anim = ObjectAnimator.ofFloat(spinButton, "scaleY", 0.95f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        animButton = new AnimatorSet();
        animButton.playTogether(animList);
        animButton.setDuration(500);
        animButton.start();

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int index = getRandomIndex();

                if(App.getInstance().get("spin_done","45").equals(Current_Date)){

                    // Don't edit below line ** it is Required
                    reward("1");

                }else{

                    spinWheel.startSpinWheelWithTargetIndex(index);
                }
            }
        });

        spinWheel.setSpinWheelRoundItemSelectedListener(new SpinWheel.SpinWheelRoundItemSelectedListener() {
            @Override
            public void SpinWheelRoundItemSelected(int index) {

                reward(data.get(index).topText);
            }
        });

    }



    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    void init_admob(){


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.admob_appId), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitial = interstitialAd;
                        // Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        // Log.i(TAG, loadAdError.getMessage());
                        interstitial = null;
                    }
                });

    }

    public void displayInterstitialAd() {
        if (interstitial != null) {
            interstitial.show(context);
        }
    }

    void reward(final String SpinRewardAmount){

        if(Integer.parseInt(SpinRewardAmount) < 1){

            //got 0 Reward on Spin
            Dialogs.normalDialog(context, getResources().getString(R.string.spin_won_nothing), getResources().getString(R.string.spin_won_nothing_desc), false, false, "", getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    displayInterstitialAd();
                    init_admob();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });

        }else{

            showpDialog();

            CustomRequest dailyCheckinRequest = new CustomRequest(Request.Method.POST, ACCOUNT_SPIN,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            hidepDialog();

                            try{

                                JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                                if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                    // Reward received Succesfully

                                    App.getInstance().store("spin_done",Current_Date);

                                    Dialogs.succesDialog(context, getResources().getString(R.string.congratulations), SpinRewardAmount + " " + getResources().getString(R.string.app_currency) + " " + getResources().getString(R.string.successfull_received), false, false, "", getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            displayInterstitialAd();
                                            init_admob();
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    });

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
                    params.put("data", App.getInstance().getDataCustom(SpinRewardAmount,"Spin Reward"));
                    return params;
                }
            };

            App.getInstance().addToRequestQueue(dailyCheckinRequest);

        }

    }

    void showTimerDialog(int TimeLeft){

        CountDownTimerView timerView = new CountDownTimerView(context);
        timerView.setTextSize(getResources().getInteger(R.integer.daily_checkin_timer_size));
        timerView.setPadding(0,0,0,25);
        timerView.setGravity(Gravity.CENTER);
        timerView.setTime(TimeLeft * 1000);
        timerView.startCountDown();
        Dialogs.customDialog(context, timerView, getResources().getString(R.string.spin_reward_taken), false, false, "", getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                displayInterstitialAd();
                init_admob();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

    }
}
