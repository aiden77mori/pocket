package com.droidoxy.easymoneyrewards.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.activities.RedeemActivity;
import com.droidoxy.easymoneyrewards.activities.ReferActivity;
import com.droidoxy.easymoneyrewards.adapters.VideosAdapter;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.model.Videos;
import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;
import com.thefinestartist.ytpa.enums.Quality;
import com.thefinestartist.ytpa.utils.YouTubeThumbnail;
import com.thefinestartist.ytpa.utils.YouTubeUrlParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.droidoxy.easymoneyrewards.constants.Constants.APP_PAYOUTS;
import static com.droidoxy.easymoneyrewards.constants.Constants.APP_VIDEOS;
import static com.droidoxy.easymoneyrewards.constants.Constants.DEBUG_MODE;

/**
 * Created by DroidOXY
 */
 
public class VideosFragment extends Fragment {

    TextView emptyText;
    ImageView emptyImage;
    RecyclerView videos;
    VideosAdapter videosAdapter;
    ArrayList<Videos> allvideos;
    ProgressBar progressBar;
    Context ctx;

    public VideosFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getActivity();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_videos, container, false);

        emptyText = view.findViewById(R.id.empty);
        emptyImage = view.findViewById(R.id.emptyImage);
        progressBar = view.findViewById(R.id.progressBar);

        videos = view.findViewById(R.id.videos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        videos.setLayoutManager(layoutManager);
        videos.setItemAnimator(new DefaultItemAnimator());

        allvideos = new ArrayList<>();

        videosAdapter = new VideosAdapter(ctx,allvideos);
        videos.setAdapter(videosAdapter);

        CustomRequest  videosRequest = new CustomRequest(Request.Method.POST, APP_VIDEOS,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));


                            if (!Response.getBoolean("error")) {

                                JSONArray videos = Response.getJSONArray("videos");

                                for (int i = 0; i < videos.length(); i++) {

                                    JSONObject obj = videos.getJSONObject(i);

                                    Videos singleVideoItem = new Videos();

                                    String videoId = obj.getString("video_id");

                                    singleVideoItem.setVideoId(videoId);
                                    singleVideoItem.setTitle(obj.getString("video_title"));
                                    singleVideoItem.setSubtitle(obj.getString("video_subtitle"));
                                    singleVideoItem.setAmount(obj.getString("video_amount"));
                                    singleVideoItem.setDuration(obj.getString("video_duration"));

                                    String videoURL = obj.getString("video_url");
                                    String videoThumbnailUrl = obj.getString("video_thumbnail");

                                    if(videoThumbnailUrl.equals("none")){
                                        singleVideoItem.setImage(YouTubeThumbnail.getUrlFromVideoId(YouTubeUrlParser.getVideoId(videoURL), Quality.HIGH));
                                    }else{
                                        singleVideoItem.setImage(obj.getString("video_thumbnail"));
                                    }

                                    singleVideoItem.setVideoURL(videoURL);
                                    singleVideoItem.setOpenLink(obj.getString("video_open_link"));
                                    singleVideoItem.setStatus(obj.getString("video_status"));

                                    if(obj.get("video_status").equals("Active") && !App.getInstance().get("APPVIDEO_"+videoId,false)){
                                        allvideos.add(singleVideoItem);
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }

                                videosAdapter.notifyDataSetChanged();

                                checkHaveVideos();

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(ctx,Response.getInt("error_code"));

                            }else{

                                if(!DEBUG_MODE){
                                    Dialogs.serverError(ctx, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            finish();
                                        }
                                    });
                                }

                            }


                        }catch (Exception e){

                            if(!DEBUG_MODE){
                                Dialogs.serverError(ctx, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                    }
                                });
                            }else{
                                Dialogs.errorDialog(ctx,"Got Error",e.toString() + ", please contact developer immediately",true,false,"","ok",null);
                            }

                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(!DEBUG_MODE){
                    Dialogs.serverError(ctx, getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
                }else{
                    Dialogs.errorDialog(ctx,"Got Error",error.toString(),true,false,"","ok",null);
                }

            }});

        App.getInstance().addToRequestQueue(videosRequest);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    void checkHaveVideos(){

        if(progressBar.getVisibility() == View.VISIBLE){
            emptyText.setVisibility(View.VISIBLE);
            emptyImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    void finish(){

        Activity close = getActivity();
        if(close instanceof ReferActivity){
            ReferActivity show = (ReferActivity) close;
            show.finish();
        }

    }

}