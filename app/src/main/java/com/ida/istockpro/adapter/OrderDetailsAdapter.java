package com.ida.istockpro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
 * Created by Ahmad Abu Hasan on 02/02/2021
 */

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder> {

    //DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    Context context;
    private List<HashMap<String, String>> orderData;

    public OrderDetailsAdapter(Context context1, List<HashMap<String, String>> orderData1) {
        this.context = context1;
        this.orderData = orderData1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        databaseAccess.open();
       // String weight_unit_id = this.orderData.get(position).get(DatabaseOpenHelper.CART_PRODUCT_WEIGHT_UNIT);
        String weight_unit = this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_WEIGHT);

        //  String weight_unit = databaseAccess.getWeightUnitName(this.orderData.get(position).get(DatabaseOpenHelper.CART_PRODUCT_WEIGHT_UNIT));
        holder.textView_ProductName.setText(this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_NAME));

        //String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit);
        TextView textView = holder.textView_Qty;
        textView.setText(this.context.getString(R.string.quantity) + " : " + this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_QTY) );

        TextView textView1 = holder.textView_Weight;
        textView1.setText(this.context.getString(R.string.weight_order_details) + " : " + " " + weight_unit);
        Log.d("sdsd", String.valueOf(weight_unit));


        String unit_price = this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_PRICE);
        String qty = this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_QTY);

        assert unit_price != null;
        double price = Double.parseDouble(unit_price);
        double parseInt = (double) Integer.parseInt(qty);
        Double.isNaN(parseInt);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();
        TextView textView3 = holder.textView_Price;
        textView3.setText("Sotilish narxi" + " : " + price + " " + "So'm");
        TextView textView2 = holder.textView_TotalCost;
        textView2.setText( NumberFormat.getInstance(Locale.getDefault()).format(price) + " x " + qty + " = "  + NumberFormat.getInstance(Locale.getDefault()).format(parseInt * price) + " " + currency);
    }

    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView textView_ProductName;
        TextView textView_Qty;
        TextView textView_Weight;
        TextView textView_Price;
        TextView textView_TotalCost;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imgProduct = itemView.findViewById(R.id.img_product);
            this.textView_ProductName = itemView.findViewById(R.id.tv_product_name);
            this.textView_Qty = itemView.findViewById(R.id.tv_qty);
            this.textView_Weight = itemView.findViewById(R.id.tv_weight);
            this.textView_Price = itemView.findViewById(R.id.tv_price);
            this.textView_TotalCost = itemView.findViewById(R.id.tv_total_cost);
        }
    }
}