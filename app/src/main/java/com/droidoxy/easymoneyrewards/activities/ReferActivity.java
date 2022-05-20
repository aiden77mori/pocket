package com.droidoxy.easymoneyrewards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.activities.ActivityBase;
import com.droidoxy.easymoneyrewards.adapters.ViewPagerAdapter;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.fragments.ReferFragment;
import com.droidoxy.easymoneyrewards.fragments.VideosFragment;
import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thefinestartist.ytpa.utils.YouTubeUrlParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReferActivity extends ActivityBase {

    // View Variables
    private Menu menu;
    ViewPagerAdapter adapter;
    ReferActivity context;
    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Doronin", "ReferActivity");
        setContentView(R.layout.activity_refer);
        context = this;

        AppUtils.setWindowUI(this, true);

        handleFragments(getIntent());

    }

    public void onBackPressed() {

        super.onBackPressed();
    }

    private void handleFragments(Intent intent) {

        String Type = intent.getStringExtra("show");

        if (Type != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (Type) {
                case "refer":
                    transaction.add(R.id.frame_layout, new ReferFragment(), "refer"); break;
                case "webvids":

                    transaction.add(R.id.frame_layout, new VideosFragment(), "webvids"); break;

                default:
                    ActivityCompat.finishAffinity(context);
                    startActivity(new Intent(context, AppActivity.class));

                    break;
            }

            transaction.commit();

        }else{
            finish();
        }

    }

    public void playVideo(String videoId, String videoPoints, String videoURL, String openLink){

        Intent playVideo = new Intent(context, YouTubePlayerActivity.class);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, YouTubeUrlParser.getVideoId(videoURL));
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_REWARDS, videoPoints);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_ID, videoId);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_LINK, openLink);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.ONLY_LANDSCAPE);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, false);
        playVideo.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, false);
        startActivityForResult(playVideo,1);
    }

    void awardVideo(final String Points,final String videoId,final String openLink){
        if(!openLink.equals("none")){ AppUtils.parse(context,openLink); }

        CustomRequest videoRewardRequest = new CustomRequest(Request.Method.POST, APP_VIDEOSTATUS,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                // Video saved Success
                                App.getInstance().store("APPVIDEO_"+videoId,true);
                                AppUtils.toastShort(context,Points+ " " + getResources().getString(R.string.app_currency) + " " + getResources().getString(R.string.successfull_received));

                            }else if(Response.getInt("error_code") == 420) {

                                // 420 - Video watched Already
                                AppUtils.toastShort(context,getResources().getString(R.string.already_watched));
                                App.getInstance().store("APPVIDEO_"+videoId,true);

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(context,Response.getInt("error_code"));

                            }else if(DEBUG_MODE){

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context,Response.getString("error_code"),Response.getString("error_description"),false,false,"",getResources().getString(R.string.ok),null);

                            }else{

                                // Server error
                                AppUtils.toastShort(context,getResources().getString(R.string.msg_server_problem));
                            }

                        }catch (Exception e){

                            if(DEBUG_MODE){

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context,"Got Error",e.toString() + ", please contact developer immediately",false,false,"","ok",null);

                            }else{

                                // Server error
                                AppUtils.toastShort(context,getResources().getString(R.string.msg_server_problem));
                            }

                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(DEBUG_MODE){

                    // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                    Dialogs.errorDialog(context,"Got Error",error.toString(),true,false,"","ok",null);

                }else{

                    // Server error
                    AppUtils.toastShort(context,getResources().getString(R.string.msg_server_problem));
                }

            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", App.getInstance().getDataCustom(videoId,Points));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(videoRewardRequest);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                String videoId = data.getStringExtra("id");
                String Points = data.getStringExtra("points");
                String openLink = data.getStringExtra("openLink");

                if(!videoId.isEmpty() && !Points.isEmpty()){
                    awardVideo(Points,videoId,openLink);
                }

            }
        }
    }

}