package com.droidoxy.easymoneyrewards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.app.App;

import com.droidoxy.easymoneyrewards.utils.AppUtils;
import com.droidoxy.easymoneyrewards.utils.CustomRequest;
import com.droidoxy.easymoneyrewards.utils.Dialogs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/*
 * Created by DroidOXY
 */

public class AccountActvity extends ActivityBase {

    RelativeLayout layout_proccess, layout_verify;
    TextView didntReceived_otp, resend_otp;
    EditText otp_code;
    AccountActvity context;
    Button btn_verify;
    Boolean otp_resent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        context = this;

        layout_proccess  = findViewById(R.id.layout_proccess);
        layout_verify  = findViewById(R.id.layout_verify);

        // otp resend
        otp_resent = false;
        didntReceived_otp = findViewById(R.id.didntReceivedotp);
        resend_otp = findViewById(R.id.resend);
        otp_code = findViewById(R.id.otp);

        // Bottom Layout
        TextView logged_in_as = findViewById(R.id.logged_in_as);
        TextView contact = findViewById(R.id.contact);
        TextView logout = findViewById(R.id.logout);

        btn_verify  = findViewById(R.id.btn_verify);

        logged_in_as.setText(Html.fromHtml(getString(R.string.logged_in_as) +  " <b>"+ App.getInstance().getEmail()+"</b>"));

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(App.getInstance().get("CONTACT_US_ACTIVE",true)){
                    AppUtils.parse(context, App.getInstance().get("APP_CONTACT_US_URL",""));
                }else{
                    AppUtils.parse(context, "mailto:"+App.getInstance().get("SUPPORT_EMAIL",""));
                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                App.getInstance().logout();

                Intent i = new Intent(getApplicationContext(), AppActivity.class);
                startActivity(i);

                ActivityCompat.finishAffinity(context);

            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                process_otp_code("resend", "123456");

            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = otp_code.getText().toString();
                process_otp_code("verify",code);

            }
        });

        if(App.getInstance().get("acc_status", 0) == 1 || !App.getInstance().get(VERIFY_EMAIL_ACTIVE, false)){

            showProcessLayout();

            startActivity(new Intent(context, MainActivity.class));
            ActivityCompat.finishAffinity(context);

        }else{
            showVerifyLayout();
        }

    }

    void showVerifyLayout(){

        // Layout
        layout_proccess.setVisibility(View.GONE);
        layout_verify.setVisibility(View.VISIBLE);

        //Button
        btn_verify.setVisibility(View.VISIBLE);

        // Resend
        if(otp_resent){
            didntReceived_otp.setVisibility(View.GONE);
            resend_otp.setVisibility(View.GONE);
        }
    }

    void showProcessLayout(){

        // Layout
        layout_proccess.setVisibility(View.VISIBLE);
        layout_verify.setVisibility(View.GONE);

        //Button
        btn_verify.setVisibility(View.GONE);
    }

    private void process_otp_code(String action, String code){

        showpDialog();

        CustomRequest redeemRequest = new CustomRequest(Request.Method.POST, ACCOUNT_VERIFY,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        hidepDialog();

                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                if(action.equals("verify")){

                                    startActivity(new Intent(context, MainActivity.class));
                                    ActivityCompat.finishAffinity(context);

                                    // Success
                                    AppUtils.toastShort(context,getResources().getString(R.string.account_verified));

                                }else{

                                    otp_resent = true;
                                    showVerifyLayout();

                                    // Success
                                    AppUtils.toastShort(context,getResources().getString(R.string.otp_resent));
                                }

                            }else if(Response.getInt("error_code") == 404){

                                otp_resent = false;
                                showVerifyLayout();

                                // Invalid Code or Expired
                                AppUtils.toastShort(context,getResources().getString(R.string.otp_code_invalid));


                            }else if(Response.getInt("error_code") == 410){

                                otp_resent = false;
                                showVerifyLayout();

                                // Invalid Code or Expired
                                AppUtils.toastShort(context,getResources().getString(R.string.otp_code_expired));


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

                params.put("data", App.getInstance().getDataCustom(action,code));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(redeemRequest);

    }

}
