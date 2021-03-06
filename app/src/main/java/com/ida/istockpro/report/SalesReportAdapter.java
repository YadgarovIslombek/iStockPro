package com.ida.istockpro.report;

import android.content.Context;
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

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.MyViewHolder> {
    Context context;
    private List<HashMap<String, String>> orderData;
    public SalesReportAdapter(Context context1, List<HashMap<String, String>> orderData1) {
        this.context = context1;
        this.orderData = orderData1;
    }
    @Override
    public SalesReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_report_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SalesReportAdapter.MyViewHolder holder, int position) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        holder.textView_Name.setText(this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_NAME));
        TextView textView = holder.textView_Date;
        textView.setText(this.context.getString(R.string.Sana) + " : " + this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_ORDER_DATE));

        TextView textView1 = holder.textView_Qty;
        textView1.setText(this.context.getString(R.string.quantity) + " : " + this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_QTY));

        TextView textView2 = holder.textView_Weight;
        textView2.setText(this.context.getString(R.string.weight_order_details) + " : " + this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_WEIGHT));

        String unit_price = this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_PRICE);
        String qty = this.orderData.get(position).get(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_QTY);
        double price = Double.parseDouble(unit_price);
        double parseInt = (double) Float.parseFloat(qty);
        Double.isNaN(parseInt);

        TextView textView3 = holder.textView_TotalCost;
        textView3.setText(NumberFormat.getInstance(Locale.getDefault()).format(price) + " x " + qty + " = " +  NumberFormat.getInstance(Locale.getDefault()).format(parseInt * price) + " " + currency);
    }


    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_Name;
        TextView textView_Date;
        TextView textView_Qty;
        TextView textView_Weight;
        TextView textView_Price;
        TextView textView_TotalCost;
        ImageView imageView_Sales;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textView_Name = itemView.findViewById(R.id.tv_product_name);
            this.textView_Date = itemView.findViewById(R.id.tv_date);
            this.textView_Qty = itemView.findViewById(R.id.tv_qty);
            this.textView_Weight = itemView.findViewById(R.id.tv_weight);
            this.textView_Price = itemView.findViewById(R.id.tv_price);
            this.textView_TotalCost = itemView.findViewById(R.id.tv_total_cost);
            this.imageView_Sales = itemView.findViewById(R.id.img_sales);
        }
    }
}
