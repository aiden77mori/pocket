package com.droidoxy.easymoneyrewards.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.activities.LoadWeb;
import com.droidoxy.easymoneyrewards.activities.WebViewActivity;
import com.droidoxy.easymoneyrewards.adapters.OfferWallsAdapter;
import com.droidoxy.easymoneyrewards.adapters.OffersAdapter;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.model.OfferWalls;
import com.droidoxy.easymoneyrewards.model.Offers;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.droidoxy.easymoneyrewards.constants.Constants;

public class HomeFragment extends Fragment implements Constants{

    public HomeFragment() {
        // Required empty public constructor
    }

    private ProgressBar progressBar,progressBarOfferwalls;

    private RecyclerView offerwalls_list;
    private OfferWallsAdapter offerWallsAdapter;
    private ArrayList<OfferWalls> offerWalls;
    private ArrayList<Offers> offers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /* Offers Walls Listview code is here*/
        offerwalls_list = view.findViewById(R.id.offerwalls_list);
        offerWalls = new ArrayList<>();
        offerWallsAdapter = new OfferWallsAdapter(getActivity(), offerWalls);


        RecyclerView.LayoutManager offerWallsLayoutManager = new GridLayoutManager(getActivity(),2);

        offerwalls_list.setLayoutManager(offerWallsLayoutManager);
        offerwalls_list.setItemAnimator(new DefaultItemAnimator());
        offerwalls_list.setAdapter(offerWallsAdapter);

        TextView account_name = view.findViewById(R.id.account_name);
        account_name.setText(App.getInstance().getUsername());

        TextView account_email = view.findViewById(R.id.account_email);
        account_email.setText(App.getInstance().getEmail());

        TextView liveLeadsBtn = view.findViewById(R.id.liveLeads);
        liveLeadsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://sphinx.services/admin");
                startActivity(intent);
            }
        });

        load_offerwalls();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void load_offerwalls(){

        //progressBarOfferwalls.setVisibility(View.GONE);
        CustomRequest offerwallsRequest = new CustomRequest(Request.Method.POST, APP_OFFERWALLS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if (!Response.getBoolean("error")) {

                                JSONArray offerwalls = Response.getJSONArray("offerwalls");

                                if(offerwalls.length() < 1){
                                    //progressBarOfferwalls.setVisibility(View.GONE);
                                    offerwalls_list.setVisibility(View.GONE);
                                }

                                for (int i = 0; i < offerwalls.length(); i++) {

                                    JSONObject obj = offerwalls.getJSONObject(i);

                                    OfferWalls singleOfferWall = new OfferWalls();

                                    singleOfferWall.setOfferid(obj.getString("offer_id"));
                                    singleOfferWall.setTitle(obj.getString("offer_title"));
                                    singleOfferWall.setSubtitle(obj.getString("offer_subtitle"));
                                    singleOfferWall.setImage(obj.getString("offer_thumbnail"));
                                    singleOfferWall.setAmount(obj.getString("offer_points"));
                                    singleOfferWall.setType(obj.getString("offer_type"));
                                    singleOfferWall.setStatus(obj.getString("offer_status"));
                                    singleOfferWall.setUrl(obj.getString("offer_url"));
                                    singleOfferWall.setPartner("offerwalls");

                                    if(obj.get("offer_status").equals("Active")){
                                        offerWalls.add(singleOfferWall);
                                    }

                                    if(obj.get("offer_type").equals("admantum")){
                                        offerWalls.remove(singleOfferWall);
                                    }

                                    //progressBarOfferwalls.setVisibility(View.GONE);

                                }
                                offerWallsAdapter.notifyDataSetChanged();

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(getContext(),Response.getInt("error_code"));

                            }else{

                                if(!DEBUG_MODE){
                                    Dialogs.serverError(getContext(),getResources().getString(R.string.ok),null);
                                }
                            }

                        } catch (JSONException e) {
                            if(!DEBUG_MODE){
                                Dialogs.serverError(getContext(),getResources().getString(R.string.ok),null);
                            }else{
                                Dialogs.errorDialog(getContext(),"Got Error",e.toString() + ", please contact developer immediately",true,false,"","ok",null);
                            }
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(DEBUG_MODE){
                    Dialogs.warningDialog(getContext(),"Got Error",error.toString(),true,false,"","ok",null);
                }else{

                    //progressBarOfferwalls.setVisibility(View.GONE);
                    offerwalls_list.setVisibility(View.GONE);
                }

            }
        });

        App.getInstance().addToRequestQueue(offerwallsRequest);

    }


    private void parse_admantum_offers(JSONObject admantum_response){

        try {

            JSONArray alloffers = admantum_response.getJSONArray("offers");

            for (int i = 0; i < alloffers.length(); i++) {

                JSONObject obj = alloffers.getJSONObject(i);

                String offerid = obj.getString("offer_id");
                String uniq_id = obj.getString("offer_id");
                String title = obj.getString("offer_title");
                String url = obj.getString("offer_link");
                String thumbnail = obj.getString("offer_image");
                String subtitle = obj.getString("offer_description");
                String partner = "admantum";

                String amount = obj.getString("offer_virtual_currency");
                String OriginalAmount = obj.getString("offer_virtual_currency");

                String bg_image = "";
                String instructions_title = "Offer Instructions : ";
                String instruction_one = "1. "+subtitle;
                String instruction_two = "2. Amount will be Credited within 24 hours after verification";
                String instruction_three = "3. Check history for progress";
                String instruction_four = "4. Skip those installed before ( unqualified won't get Rewarded )";

                Offers beanClassForRecyclerView_contacts = new Offers(thumbnail,title,amount,OriginalAmount,url,subtitle,partner,uniq_id,offerid,bg_image,instructions_title,instruction_one,instruction_two,instruction_three,instruction_four,false);
                offers.add(beanClassForRecyclerView_contacts);

                progressBar.setVisibility(View.GONE);

                App.getInstance().store("ADMANTUM_GOT_RESPONSE", true);
                App.getInstance().store("ADMANTUM_RESPONSE", admantum_response);

            }

        } catch (JSONException e) {
            // do nothin
        }

    }


//    private void load_admantum_api_offers(){
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        if(App.getInstance().get("ADMANTUM_GOT_RESPONSE", false)){
//
//            try {
//
//                JSONObject response_obj = new JSONObject(App.getInstance().get("ADMANTUM_RESPONSE", ""));
//                parse_admantum_offers(response_obj);
//
//            } catch (Throwable t) {
//                //do nothin
//            }
//
//        }
//        CustomRequest adMantumOffersRequest = new CustomRequest(Request.Method.POST, API_ADMANTUM, null,
//                new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                parse_admantum_offers(response);
//
//
//            }}, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // do nothin
//            }}){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("country", App.getInstance().getCountryCode());
//                params.put("uid", App.getInstance().getUsername());
//                params.put("device", "android");
//                return params;
//            }
//        };
//
//        App.getInstance().addToRequestQueue(adMantumOffersRequest);
//
//    }
	
}
