package com.droidoxy.easymoneyrewards.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.droidoxy.easymoneyrewards.adapters.OfferWallsAdapter;
import com.droidoxy.easymoneyrewards.adapters.OffersAdapter;
import com.droidoxy.easymoneyrewards.adapters.PayoutsAdapter;
import com.droidoxy.easymoneyrewards.app.App;
import com.droidoxy.easymoneyrewards.model.OfferWalls;
import com.droidoxy.easymoneyrewards.model.Offers;
import com.droidoxy.easymoneyrewards.model.Payouts;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.droidoxy.easymoneyrewards.constants.Constants;

public class RedeemFragment extends Fragment implements Constants{

    public RedeemFragment() {
        // Required empty public constructor
    }

    private RecyclerView payouts;
    private OffersAdapter offersAdapter;
    private PayoutsAdapter offerWallsAdapter;
    private ArrayList<Payouts> offerWalls;
    private ArrayList<Offers> offers;

    private LinearLayout offerWallsTitle,offerWallsTopSpace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redeem, container, false);

        TextView myPoint = (TextView)view.findViewById(R.id.redeem_point);
        myPoint.setText(getString(R.string.app_currency) +" : " + App.getInstance().getBalance());

        /* Offers Walls Listview code is here*/
        payouts = view.findViewById(R.id.payouts);
        offerWalls = new ArrayList<>();

        RecyclerView.LayoutManager offerWallsLayoutManager = new GridLayoutManager(getActivity(),2);

        payouts.setLayoutManager(offerWallsLayoutManager);
        payouts.setItemAnimator(new DefaultItemAnimator());

        offerWallsAdapter = new PayoutsAdapter(getActivity(), offerWalls);
        payouts.setAdapter(offerWallsAdapter);

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

        CustomRequest offerwallsRequest = new CustomRequest(Request.Method.POST, APP_PAYOUTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if (!Response.getBoolean("error")) {

                                JSONArray transactions = Response.getJSONArray("payouts");

                                for (int i = 0; i < transactions.length(); i++) {

                                    JSONObject obj = transactions.getJSONObject(i);

                                    Payouts singlePayout = new Payouts();

                                    singlePayout.setPayoutId(obj.getString("payout_id"));
                                    singlePayout.setPayoutName(obj.getString("payout_title"));
                                    singlePayout.setSubtitle(obj.getString("payout_subtitle"));
                                    singlePayout.setPayoutMessage(obj.getString("payout_message"));
                                    singlePayout.setAmount(obj.getString("payout_amount"));
                                    singlePayout.setReqPoints(obj.getString("payout_pointsRequired"));
                                    singlePayout.setImage(obj.getString("payout_thumbnail"));
                                    singlePayout.setStatus(obj.getString("payout_status"));

                                    if(obj.get("payout_status").equals("Active")){
                                        offerWalls.add(singlePayout);
                                    }

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
                            Log.e("Doronin", e.toString());
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

                    offerWallsTopSpace.setVisibility(View.GONE);
                    offerWallsTitle.setVisibility(View.GONE);
                    payouts.setVisibility(View.GONE);
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

                App.getInstance().store("ADMANTUM_GOT_RESPONSE", true);
                App.getInstance().store("ADMANTUM_RESPONSE", admantum_response);

            }
            offersAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            // do nothin
        }

    }


    private void load_admantum_api_offers(){

        if(App.getInstance().get("ADMANTUM_GOT_RESPONSE", false)){

            try {

                JSONObject response_obj = new JSONObject(App.getInstance().get("ADMANTUM_RESPONSE", ""));
                parse_admantum_offers(response_obj);

            } catch (Throwable t) {
                //do nothin
            }

        }
        CustomRequest adMantumOffersRequest = new CustomRequest(Request.Method.POST, API_ADMANTUM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parse_admantum_offers(response);


                    }}, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // do nothin
            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("country", App.getInstance().getCountryCode());
                params.put("uid", App.getInstance().getUsername());
                params.put("device", "android");
                return params;
            }
        };

        App.getInstance().addToRequestQueue(adMantumOffersRequest);

    }

}
