package com.droidoxy.easymoneyrewards.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidoxy.easymoneyrewards.Config;
import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.activities.MainActivity;
import com.droidoxy.easymoneyrewards.activities.RedeemActivity;
import com.droidoxy.easymoneyrewards.constants.Constants;
import com.droidoxy.easymoneyrewards.model.OfferWalls;
import com.droidoxy.easymoneyrewards.model.Payouts;

import java.util.List;

public class PayoutsAdapter extends RecyclerView.Adapter<PayoutsAdapter.MyViewHolder> {

    private Context context;

    private List<Payouts> offerWallsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, money;
        LinearLayout single_item;


        private MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            money = view.findViewById(R.id.money);
            single_item = view.findViewById(R.id.single_item);

        }

    }

    public PayoutsAdapter(Context mainActivityContacts, List<Payouts> listItem) {
        this.offerWallsList = listItem;
        this.context = mainActivityContacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_redeem_grid, parent, false);

        return new MyViewHolder(itemView);


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Payouts payout = offerWallsList.get(position);
        final String title = payout.getPayoutName();
        final String subtitle = payout.getSubtitle();
        final String message = payout.getPayoutMessage();
        final String amount = payout.getAmount();
        final String points = payout.getReqPoints();
        final String payoutId = payout.getPayoutId();
        final String status = payout.getStatus();
        final String image = payout.getImage();

        holder.name.setText(points + " Points");
        holder.money.setText(amount);
//        holder.sub_title.setText(offerWalls.getSubtitle());
//
//        holder.sub_title.setVisibility(View.GONE);

        // loading image using Glide library
        Glide.with(context).load(Constants.API_DOMAIN_IMAGES+payout.getImage())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(holder.image);

        holder.single_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((RedeemActivity)context).Redeem(title, subtitle, message, amount, points, payoutId, status, image);

            } // END ON CLICK
        });

    }

    @Override
    public int getItemCount() {
        return offerWallsList.size();
    }

}


