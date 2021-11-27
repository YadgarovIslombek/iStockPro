package com.ida.istockpro.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import es.dmoral.toasty.Toasty;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    public  static float total_price;
    private Context context;
    private List<HashMap<String,String>> card_productList;
    TextView textView_total_price;
    Button button_SubmitOrder;
    ImageView imgNoProduct, img_change;
    TextView textView_no_product;
    MediaPlayer mediaPlayer;
    private static boolean isTrue = false;
    float value_s2;
    boolean keyDel = false;
    private int BOX_SIZE  = 1;
    private Boolean mIsLastBoxReached  = false;
    float cost = 0;
     float total_cost =0f;
    float value=0.0f;
    float total_price_edit;
    public CartAdapter(Context context1, List<HashMap<String, String>> cart_product1, TextView textView_total_price1, Button button_SubmitOrder1, ImageView imgNoProduct1, TextView textView_no_product1) {
        this.context = context1;
        this.card_productList = cart_product1;
        this.textView_total_price = textView_total_price1;
        this.button_SubmitOrder = button_SubmitOrder1;
        this.imgNoProduct = imgNoProduct1;
        this.textView_no_product = textView_no_product1;
        this.mediaPlayer = MediaPlayer.create(context1, (int) R.raw.delete_sound);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_items, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        databaseAccess.open();
        final String cart_id = this.card_productList.get(position).get(DatabaseOpenHelper.PRODUCT_CART_ID);
        String product_id = this.card_productList.get(position).get(DatabaseOpenHelper.PRODUCT_ID);
        String product_name = databaseAccess.getProductName(product_id);
        String weight_unit_id = this.card_productList.get(position).get(DatabaseOpenHelper.CART_PRODUCT_WEIGHT_UNIT);
        String val = this.card_productList.get(position).get(DatabaseOpenHelper.CART_VAL);
        String price = this.card_productList.get(position).get(DatabaseOpenHelper.PRODUCT_PRICE);
        String qty = this.card_productList.get(position).get(DatabaseOpenHelper.CART_PRODUCT_QTY);
        final int getStock = Integer.parseInt(this.card_productList.get(position).get(DatabaseOpenHelper.CART_PRODUCT_STOCK));
        //final double getStock = Double.parseDouble(this.cart_product.get(position).get(DatabaseOpenHelper.CART_PRODUCT_STOCK));
        String s = "";
        databaseAccess.open();
        final String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        total_price = databaseAccess.getTotalPrice();
        total_cost = total_price;
        holder.textView_ItemName.setText(product_name);
        TextView textView = textView_total_price;
        textView.setText(context.getString(R.string.total_price) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(total_price) + " " + currency);
        float parseDouble = Float.parseFloat(price);
        float parseInt = (float) Float.parseFloat(qty);
        Float.isNaN(parseInt);
        databaseAccess.close();

        TextView textView1 = holder.textView_Weight;
        int ifqty = Integer.parseInt(val);

        TextView olchovBir = holder.olchovBir;
        TextView summa = holder.summa;
        olchovBir.setText("Tovarning o'lchov birligi" + " : " + weight_unit_id);
         textView1.setText("Tovarning narxi" + " : " + NumberFormat.getInstance(Locale.getDefault()).format(parseDouble) + " " + currency);




        if(ifqty == 1) {
            holder.kgLiner.setVisibility(View.GONE);
            holder.grLiner.setVisibility(View.VISIBLE);
            olchovBir.setText("Tovarning o'lchov birligi" + " : " + "gr");
            holder.editText.setText(qty);
            //summa.setText("Tovarning narxi " + " : " + cost);

        } else {

            databaseAccess.open();
            String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);
            //String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);
            holder.kgLiner.setVisibility(View.VISIBLE);
            holder.grLiner.setVisibility(View.GONE);
            olchovBir.setText("Tovarning o'lchov birligi" + " : " + weight_unit_name);
            holder.textView_QtyNumber.setText(qty);
            //summa.setText("Tovarning narxi " + " : " + value_s2);

        }





        holder.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
       holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String get_s = s.toString();
                if (!get_s.isEmpty()) {
                    //int value_s = Integer.parseInt(get_s);
                    value = Float.parseFloat(get_s) / 1000;
                    if (value > 0) {
                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CartAdapter.this.context);
                        databaseAccess.open();
                        total_price_edit = databaseAccess.getTotalPrice();

                        float cost = value * parseDouble;

                        databaseAccess.open();
                        databaseAccess.updateProductQty(cart_id, "" + value);

                        summa.setText(NumberFormat.getInstance(Locale.getDefault()).format(cost) + " " + currency);

                        CartAdapter.total_price = (total_price_edit + cost);
                        TextView textView2 = CartAdapter.this.textView_total_price;
                        textView2.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(total_price) + " " + currency);
                    }
                }
//                        CartAdapter.total_price =total_price_edit-Utils.FLOAT_EPSILON;
//                    TextView textView2 = CartAdapter.this.textView_total_price;
//                    textView2.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " +  NumberFormat.getInstance(Locale.getDefault()).format(CartAdapter.total_price) + " " + currency);

//                float cost1 = ((CartAdapter.total_price)-Utils.FLOAT_EPSILON);
//                TextView textView2 = CartAdapter.this.textView_total_price;
//                textView2.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " +  NumberFormat.getInstance(Locale.getDefault()).format(cost1) + " " + currency);

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CartAdapter.this.context);
                databaseAccess.open();
                if (databaseAccess.deleteProductFromCart(cart_id)) {
                    Toasty.success(CartAdapter.this.context, CartAdapter.this.context.getString(R.string.product_removed_from_cart), Toasty.LENGTH_SHORT).show();
                    CartAdapter.this.mediaPlayer.start();
                    CartAdapter.this.card_productList.remove(holder.getAdapterPosition());
                    CartAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                    databaseAccess.open();
                    CartAdapter.total_price = databaseAccess.getTotalPrice();
                    TextView textView = CartAdapter.this.textView_total_price;
                    textView.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(CartAdapter.total_price) + " " + currency);
                } else {
                    Toasty.error(CartAdapter.this.context, CartAdapter.this.context.getString(R.string.failed), Toasty.LENGTH_SHORT).show();
                }
                databaseAccess.open();
                int itemCount = databaseAccess.getCartItemCount();
                Log.d("itemCount", "" + itemCount);
                if (itemCount <= 0) {
                    CartAdapter.this.textView_total_price.setVisibility(View.GONE);
                    CartAdapter.this.button_SubmitOrder.setVisibility(View.GONE);
                    CartAdapter.this.imgNoProduct.setVisibility(View.VISIBLE);
                    CartAdapter.this.textView_no_product.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.textView_Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float qty2 = 0;
                float get_qty = Float.parseFloat(holder.textView_QtyNumber.getText().toString());
                if (get_qty >= 2) {
                    qty2 = get_qty - 1;
                    float parseDouble = Float.parseFloat(price);
                    float d = (float) qty2;
                    Double.isNaN(d);
                    cost = parseDouble * d;
                    summa.setText(NumberFormat.getInstance(Locale.getDefault()).format(cost) + " " + currency);
                    holder.textView_QtyNumber.setText("" + qty2);
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CartAdapter.this.context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + qty2);
                    CartAdapter.total_price = CartAdapter.total_price - parseDouble;
                    CartAdapter.this.textView_total_price.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " + NumberFormat.getInstance(Locale.getDefault()).format(CartAdapter.total_price) + " " + currency);
                }
            }
        });
        holder.textView_Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float get_qty = Float.parseFloat(holder.textView_QtyNumber.getText().toString());
                if (get_qty >= getStock) {
                    Context context = CartAdapter.this.context;
                    Toasty.error(context, CartAdapter.this.context.getString(R.string.available_stock) + " " + getStock, Toasty.LENGTH_SHORT).show();
                    return;
                }
                float qty2 = 0;
                qty2= get_qty + 1;
                float parseDouble = Float.parseFloat((price));
                float d = qty2;
                Double.isNaN(d);
                cost = parseDouble * d;
                summa.setText(NumberFormat.getInstance(Locale.getDefault()).format(cost) + " " + currency);
                TextView textView1 = holder.textView_QtyNumber;
                textView1.setText("" + qty2);
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CartAdapter.this.context);
                databaseAccess.open();
                String cartId = cart_id;
                databaseAccess.updateProductQty(cartId, "" + qty2);
                CartAdapter.total_price = CartAdapter.total_price + parseDouble;
                TextView textView2 = CartAdapter.this.textView_total_price;
                textView2.setText(CartAdapter.this.context.getString(R.string.total_price) + " : " +  NumberFormat.getInstance(Locale.getDefault()).format(CartAdapter.total_price) + " " + currency);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.card_productList.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct,img_change;
        TextView textView_ItemName;
        TextView textView_Weight;
        TextView summa;
        TextView olchovBir;
        ImageView imgDelete;
        TextView textView_Minus;
        TextView textView_QtyNumber;
        TextView textView_Plus;
        LinearLayout grLiner, kgLiner;
        public EditText editText;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.imgProduct = itemView.findViewById(R.id.cart_product_image);
            this.img_change = itemView.findViewById(R.id.img_change);
            this.textView_ItemName = itemView.findViewById(R.id.tv_item_name);
            this.textView_Weight = itemView.findViewById(R.id.tv_weight);
            this.summa = itemView.findViewById(R.id.summa);
            this.olchovBir = itemView.findViewById(R.id.olchovBir);
            this.imgDelete = itemView.findViewById(R.id.img_delete);
            this.textView_Minus = itemView.findViewById(R.id.tv_minus);
            this.textView_QtyNumber = itemView.findViewById(R.id.tv_number);
            this.textView_Plus = itemView.findViewById(R.id.tv_plus);
            this.grLiner = itemView.findViewById(R.id.grLiner);
            this.kgLiner = itemView.findViewById(R.id.kgLiner);
            this.editText = itemView.findViewById(R.id.grEdit);
        }
    }
}