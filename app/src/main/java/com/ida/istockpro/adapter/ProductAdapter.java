package com.ida.istockpro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ida.istockpro.R;
import com.ida.istockpro.data.EditProductActivity;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.ida.istockpro.utils.Constant;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    private List<HashMap<String, String>> productData;
    String productWeight = "";
    public ProductAdapter(Context context1, List<HashMap<String, String>> productData1) {
        this.context = context1;
        this.productData = productData1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        final String product_id = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_ID);

        sp = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");


        Double buy = Double.parseDouble(this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_BUY));
        Double price = Double.parseDouble(this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_PRICE));
        String weight = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_WEIGHT_UNIT_ID);
        final String weight_unit_id = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_WEIGHT_UNIT_ID);
        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        holder.textView_ProductName.setText(this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_NAME));
        TextView textView = holder.textView_Buy;
        textView.setText(this.context.getString(R.string.olingan_narxi) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(buy) + " " + currency);
        TextView textView1 = holder.textView_Stock;
        textView1.setText(this.context.getString(R.string.stock) + " : " + this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_STOCK) + " " + weight_unit_name);
        TextView textView2 = holder.textView_Price;
        textView2.setText(this.context.getString(R.string.buy) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(price) + " " + currency);


        holder.imageView_Delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userType.equalsIgnoreCase("admin")) {
                    new AlertDialog.Builder(ProductAdapter.this.context).setMessage(R.string.want_to_delete_product).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            databaseAccess.open();
                            if (databaseAccess.deleteProduct(product_id)) {
                                Toasty.error(ProductAdapter.this.context, (int) R.string.product_deleted, Toasty.LENGTH_SHORT).show();
                                ProductAdapter.this.productData.remove(holder.getAdapterPosition());
                                ProductAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                            } else {
                                Toast.makeText(ProductAdapter.this.context, (int) R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                } else {
                    Toasty.error(context, R.string.ochirishga_ruxsat_yoq, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return this.productData.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_ProductName;
        TextView textView_Buy;
        TextView textView_Stock;
        TextView textView_Price;
        ImageView imageView_Delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_ProductName = itemView.findViewById(R.id.tv_product_name);
            this.textView_Buy = itemView.findViewById(R.id.tv_buy);
            this.textView_Stock = itemView.findViewById(R.id.tv_stock);
            this.textView_Price = itemView.findViewById(R.id.summa);
            this.imageView_Delete = itemView.findViewById(R.id.img_delete);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ProductAdapter.this.context, EditProductActivity.class);
            i.putExtra(DatabaseOpenHelper.PRODUCT_ID, (String) ((HashMap) ProductAdapter.this.productData.get(getAdapterPosition())).get(DatabaseOpenHelper.PRODUCT_ID));
            ProductAdapter.this.context.startActivity(i);
        }
    }
}
