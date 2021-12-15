package com.ida.istockpro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ida.istockpro.R;
import com.ida.istockpro.customers.EditCustomersActivity;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;



public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private Context context;
    private List<HashMap<String, String>> customerData;

    public CustomerAdapter(Context context1, List<HashMap<String, String>> customerData1) {
        this.context = context1;
        this.customerData = customerData1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false));
    }

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String customer_id = this.customerData.get(position).get(DatabaseOpenHelper.CUSTOMER_ID);
        final String hp = this.customerData.get(position).get(DatabaseOpenHelper.CUSTOMER_HP);

        holder.textView_CustomerName.setText(this.customerData.get(position).get(DatabaseOpenHelper.CUSTOMER_NAME));
        holder.textView_Phone.setText(hp);
//        holder.textView_Hp.setText(hp);
//        holder.textView_Wa.setText(this.customerData.get(position).get(DatabaseOpenHelper.CUSTOMER_WA));

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent callIntent = new Intent("android.intent.action.DIAL");
                callIntent.setData(Uri.parse("tel:" + hp));
                CustomerAdapter.this.context.startActivity(callIntent);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(CustomerAdapter.this.context).setMessage(R.string.want_to_delete_supplier).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CustomerAdapter.this.context);
                        databaseAccess.open();
                        if (databaseAccess.deleteCustomer(customer_id)) {
                            Toasty.error(CustomerAdapter.this.context, (int) R.string.supplier_deleted, Toasty.LENGTH_SHORT).show();
                            CustomerAdapter.this.customerData.remove(holder.getAdapterPosition());
                            CustomerAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(CustomerAdapter.this.context, (int) R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.customerData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_CustomerName;
        TextView textView_Phone;
        TextView textView_Hp;
        TextView textView_Wa;

        ImageView imgCall;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textView_CustomerName = itemView.findViewById(R.id.tv_customer_name);
            this.textView_Phone = itemView.findViewById(R.id.tv_customer_phone);
            this.imgDelete = itemView.findViewById(R.id.img_delete);
            this.imgCall = itemView.findViewById(R.id.img_call);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            Intent i = new Intent(CustomerAdapter.this.context, EditCustomersActivity.class);
            i.putExtra(DatabaseOpenHelper.CUSTOMER_ID, (String) ((HashMap) CustomerAdapter.this.customerData.get(getAdapterPosition())).get(DatabaseOpenHelper.CUSTOMER_ID));
            i.putExtra(DatabaseOpenHelper.CUSTOMER_NAME, (String) ((HashMap) CustomerAdapter.this.customerData.get(getAdapterPosition())).get(DatabaseOpenHelper.CUSTOMER_NAME));
            i.putExtra(DatabaseOpenHelper.CUSTOMER_HP, (String) ((HashMap) CustomerAdapter.this.customerData.get(getAdapterPosition())).get(DatabaseOpenHelper.CUSTOMER_HP));
            i.putExtra(DatabaseOpenHelper.CUSTOMER_INFORMATION, (String) ((HashMap) CustomerAdapter.this.customerData.get(getAdapterPosition())).get(DatabaseOpenHelper.CUSTOMER_INFORMATION));
            i.putExtra(DatabaseOpenHelper.CUSTOMER_LAST_UPDATE, (String) ((HashMap) CustomerAdapter.this.customerData.get(getAdapterPosition())).get(DatabaseOpenHelper.CUSTOMER_LAST_UPDATE));
            CustomerAdapter.this.context.startActivity(i);
        }
    }
}