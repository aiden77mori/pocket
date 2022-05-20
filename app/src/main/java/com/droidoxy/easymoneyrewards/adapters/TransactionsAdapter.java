package com.droidoxy.easymoneyrewards.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.droidoxy.easymoneyrewards.R;
import com.droidoxy.easymoneyrewards.model.Transactions;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private Context context;

    private List<Transactions> offerWallsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tnName,amount;
        LinearLayout SingleItem;


        private MyViewHolder(View view) {
            super(view);

            tnName = itemView.findViewById(R.id.offer_name);
            amount = itemView.findViewById(R.id.offer_money);

        }

    }

    public TransactionsAdapter(Context mainActivityContacts, List<Transactions> listItem) {
        this.offerWallsList = listItem;
        this.context = mainActivityContacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transactions_grid, parent, false);

        return new MyViewHolder(itemView);


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Transactions transaction = offerWallsList.get(position);

        String TnType = transaction.getTnType();
        String status = transaction.getStatus();

        holder.tnName.setText(transaction.getTnName());

        Log.d("Doronin", "amount : " + transaction.getAmount());
        if (TnType.equals("cr")){

            holder.amount.setText("+" + transaction.getAmount());
            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_sucess_background));

        }else if(TnType.equals("db")){

            holder.amount.setText("-" + transaction.getAmount());
            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_reject_background));
        }


        if(status.equals("0")){

            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_process_background));

        }else if(status.equals("2")){

            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_process_background));
        }else if(status.equals("1")){

            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_sucess_background));

        }else if(status.equals("3")){

            ((TextView)holder.amount).setBackground(context.getResources().getDrawable(R.drawable.drawable_transaction_reject_background));

        }

    }

    @Override
    public int getItemCount() {
        return offerWallsList.size();
    }

}


