package com.ida.istockpro.adapter;


import static com.ida.istockpro.LoginActivity.item;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView;


import com.ida.istockpro.R;
import com.ida.istockpro.data.EditProductActivity;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.ida.istockpro.kassa.DetailsProduct;
import com.ida.istockpro.kassa.PosActivity;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;



public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {

    public static int count;
    private final Context context;
    private final List<HashMap<String, String>> productData;
    MediaPlayer player;
    String olchov ="";

    public PosProductAdapter(Context context1, List<HashMap<String, String>> productData1) {
        this.context = context1;
        this.productData = productData1;
        this.player = MediaPlayer.create(context1, (int) R.raw.delete_sound);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final float getStock;
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String product_id = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_ID);
        final String product_cat_id = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_CATEGORY);
        databaseAccess.open();
        String cat_name = databaseAccess.getCategoryName(product_cat_id);
        String name = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_NAME);
        //final String product_weight = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_PRICE);
        final String weight_unit_id = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_WEIGHT_UNIT_ID);
        final String product_stock = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_STOCK);
        final String product_price = this.productData.get(position).get(DatabaseOpenHelper.PRODUCT_PRICE);

        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);

        assert product_stock != null;
        float getStock1 = Float.parseFloat(product_stock);
        if (getStock1 > 5.0) {
            TextView textView = holder.textView_Stock;
            getStock = getStock1;
            textView.setText(this.context.getString(R.string.stock1) + " : " + product_stock + " " + weight_unit_name);
        } else {
            getStock = getStock1;
            TextView textView1 = holder.textView_Stock;
            textView1.setText(this.context.getString(R.string.stock1) + " : " + product_stock + " " + weight_unit_name);
            holder.textView_Stock.setTextColor(SupportMenu.CATEGORY_MASK);
        }

        assert product_price != null;
        Double price = Double.parseDouble(product_price);

        holder.textView_ProductName.setText(name);
        TextView textView2 = holder.textView_Weight;
        //textView2.setText(product_weight + " " + weight_unit_name);
        TextView textView3 = holder.textView_Price;
        textView3.setText(this.context.getString(R.string.price) + " : "  + NumberFormat.getInstance(Locale.getDefault()).format(price) + " " + currency );

        holder.cardView_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PosProductAdapter.this.player.start();
                Intent intent;
                if (item.equals("Cashier")) {
                    intent = new Intent(PosProductAdapter.this.context, DetailsProduct.class);
                } else {
                    intent = new Intent(PosProductAdapter.this.context, EditProductActivity.class);
                }
                intent.putExtra(DatabaseOpenHelper.PRODUCT_ID, product_id);
                PosProductAdapter.this.context.startActivity(intent);
            }
        });

        holder.button_AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getStock <= 0) {
                    Toasty.warning(PosProductAdapter.this.context, (int) R.string.stock_is_low_please_update_stock, Toasty.LENGTH_SHORT).show();
                    return;
                }
               else if(cat_name.equalsIgnoreCase("Sabzavotlar") || cat_name.equalsIgnoreCase("Mevalar")){
                    Log.d("w_id", weight_unit_id);
                    Log.d("prod",product_cat_id);
                    Log.d("prodname",cat_name);
                    databaseAccess.open();
                    new AlertDialog.Builder(PosProductAdapter.this.context).setMessage(R.string.tanla)
                            .setPositiveButton(R.string.Gramm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                olchov = "gr";
                            int check = databaseAccess.addToCart(product_id, 0,  weight_unit_id, product_price,  product_stock,"1");
                            databaseAccess.open();
                            int count = databaseAccess.getCartItemCount();
                            if (count == 0) {
                                PosActivity.textView_Count.setVisibility(View.INVISIBLE);
                            } else {
                                PosActivity.textView_Count.setVisibility(View.VISIBLE);
                                PosActivity.textView_Count.setText(String.valueOf(count));
                            }
                            if (check == 1) {
                                Toasty.success(PosProductAdapter.this.context, (int) R.string.product_added_to_cart, Toasty.LENGTH_SHORT).show();
                                Log.d("CARD P", String.valueOf(check));
                                PosProductAdapter.this.player.start();
                            } else if (check == 2) {
                                Toasty.info(PosProductAdapter.this.context, (int) R.string.product_already_added_to_cart, Toasty.LENGTH_SHORT).show();
                            } else {
                                Toasty.error(PosProductAdapter.this.context, (int) R.string.product_added_to_cart_failed_try_again, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.Kg, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            olchov = "kg";
                            databaseAccess.open();
                            int check = databaseAccess.addToCart(product_id, 1,  weight_unit_id, product_price,  product_stock,"2");
                            databaseAccess.open();
                            int count = databaseAccess.getCartItemCount();
                            if (count == 0) {
                                PosActivity.textView_Count.setVisibility(View.INVISIBLE);
                            } else {
                                PosActivity.textView_Count.setVisibility(View.VISIBLE);
                                PosActivity.textView_Count.setText(String.valueOf(count));
                            }
                            if (check == 1) {
                                Toasty.success(PosProductAdapter.this.context, (int) R.string.product_added_to_cart, Toasty.LENGTH_SHORT).show();
                                Log.d("CARD P", String.valueOf(check));
                                PosProductAdapter.this.player.start();
                            } else if (check == 2) {
                                Toasty.info(PosProductAdapter.this.context, (int) R.string.product_already_added_to_cart, Toasty.LENGTH_SHORT).show();
                            } else {
                                Toasty.error(PosProductAdapter.this.context, (int) R.string.product_added_to_cart_failed_try_again, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }).show();
                }else{
                    databaseAccess.open();
                    int check = databaseAccess.addToCart(product_id, 1,  weight_unit_id, product_price,  product_stock,"2");
                    databaseAccess.open();
                    int count = databaseAccess.getCartItemCount();
                    if (count == 0) {
                        PosActivity.textView_Count.setVisibility(View.INVISIBLE);
                    } else {
                        PosActivity.textView_Count.setVisibility(View.VISIBLE);
                        PosActivity.textView_Count.setText(String.valueOf(count));
                    }
                    if (check == 1) {
                        Toasty.success(PosProductAdapter.this.context, (int) R.string.product_added_to_cart, Toasty.LENGTH_SHORT).show();
                        Log.d("CARD P", String.valueOf(check));
                        PosProductAdapter.this.player.start();
                    } else if (check == 2) {
                        Toasty.info(PosProductAdapter.this.context, (int) R.string.product_already_added_to_cart, Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(PosProductAdapter.this.context, (int) R.string.product_added_to_cart_failed_try_again, Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return productData.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView_Product;
        TextView textView_ProductName;
        TextView textView_Stock;
        TextView textView_Weight;
        TextView textView_Price;
        Button button_AddToCart;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.cardView_Product = itemView.findViewById(R.id.card_product);
            this.textView_ProductName = itemView.findViewById(R.id.tv_product_name);
            this.textView_Stock = itemView.findViewById(R.id.tv_stock);
            this.textView_Price = itemView.findViewById(R.id.summa);
            this.button_AddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}