package com.ida.istockpro.kassa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.Utils;
import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.adapter.CartAdapter;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.ida.istockpro.sotuvTarihi.OrdersActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductCart extends BaseActivity {
    CartAdapter productCartAdapter;
    LinearLayout linearLayout;

    ArrayAdapter<String> customerAdapter;
    List<String> customerNames;
    ArrayAdapter<String> orderTypeAdapter;
    List<String> orderTypeNames;
    ArrayAdapter<String> paymentMethodAdapter;
    List<String> paymentMethodNames;

    ImageView imgNoProduct;
    TextView textView_no_product;
    TextView textView_total_price;
    Button button_SubmitOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.product_cart);

        RecyclerView recyclerView = findViewById(R.id.cart_recyclerview);
        this.imgNoProduct = findViewById(R.id.image_no_product);
        this.textView_no_product = findViewById(R.id.tv_no_product);
        this.textView_total_price = findViewById(R.id.tv_total_price);
        this.button_SubmitOrder = findViewById(R.id.btn_submit_order);
        this.linearLayout = findViewById(R.id.linear_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        this.textView_no_product.setVisibility(View.GONE);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> cartProductList = databaseAccess.getCartProduct();
        if (cartProductList.isEmpty()) {
            this.imgNoProduct.setImageResource(R.drawable.empty_cart);
            this.imgNoProduct.setVisibility(View.VISIBLE);
            this.textView_no_product.setVisibility(View.VISIBLE);
            this.button_SubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            this.linearLayout.setVisibility(View.GONE);
            this.textView_total_price.setVisibility(View.GONE);
        } else {
            this.imgNoProduct.setVisibility(View.GONE);
            CartAdapter cartAdapter = new CartAdapter(this, cartProductList, this.textView_total_price, this.button_SubmitOrder, this.imgNoProduct, this.textView_no_product);
            this.productCartAdapter = cartAdapter;
            recyclerView.setAdapter(cartAdapter);
        }

        this.button_SubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCart.this.dialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void dialog() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> shopData = databaseAccess.getShopInformation();
        final String shop_currency = shopData.get(0).get(DatabaseOpenHelper.SHOP_CURRENCY);
        String tax = shopData.get(0).get(DatabaseOpenHelper.SHOP_TAX);
        double getTax = Double.parseDouble(tax);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, (ViewGroup) null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialog_btn_close = dialogView.findViewById(R.id.btn_close);
        final Button dialog_btn_submit = dialogView.findViewById(R.id.btn_submit);

        final TextView dialog_customer = dialogView.findViewById(R.id.dialog_customer);
        ImageButton dialog_img_customer = dialogView.findViewById(R.id.img_select_customer);
        final TextView dialog_order_type = dialogView.findViewById(R.id.dialog_order_type);
        ImageButton dialog_img_order_type = dialogView.findViewById(R.id.img_order_type);
        final TextView dialog_order_payment_method = dialogView.findViewById(R.id.dialog_order_status);
        ImageButton dialog_img_order_payment_method = dialogView.findViewById(R.id.img_order_payment_method);

        TextView dialog_text_sub_total = dialogView.findViewById(R.id.dialog_text_sub_total);
        TextView dialog_text_total_tax = dialogView.findViewById(R.id.dialog_text_total_tax);
        final EditText dialog_et_discount = dialogView.findViewById(R.id.et_dialog_discount);
        final TextView dialog_text_total_cost = dialogView.findViewById(R.id.dialog_text_total_cost);

        ((TextView) dialogView.findViewById(R.id.dialog_level_tax)).setText(getString(R.string.total_tax) + " (" + tax + "%) : ");
        final double total_cost = CartAdapter.total_price;
        String sb =
                NumberFormat.getInstance(Locale.getDefault()).format(total_cost) + " " + shop_currency;
        dialog_text_sub_total.setText(sb);

        final double calculated_tax = (total_cost * getTax) / 100.0d;
        dialog_text_total_tax.setText( NumberFormat.getInstance(Locale.getDefault()).format(calculated_tax) + " " + shop_currency);
        double calculated_total_cost = (total_cost + calculated_tax) - Utils.DOUBLE_EPSILON;
        dialog_text_total_cost.setText(NumberFormat.getInstance(Locale.getDefault()).format(calculated_total_cost) + " " + shop_currency);

        dialog_et_discount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        //dialog_et_discount.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
        dialog_et_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String get_discount = s.toString();
                if (!get_discount.isEmpty()) {
                    double discount = Double.parseDouble(get_discount);
                    if (discount > total_cost + calculated_tax) {
                        dialog_et_discount.setError(ProductCart.this.getString(R.string.discount_cant_be_greater_than_total_price));
                        dialog_et_discount.requestFocus();
                        dialog_btn_submit.setVisibility(View.INVISIBLE);
                        return;
                    }
                    dialog_btn_submit.setVisibility(View.VISIBLE);
                    dialog_text_total_cost.setText(NumberFormat.getInstance(Locale.getDefault()).format((total_cost + calculated_tax) - discount) + " " + shop_currency);
                    return;
                }
                double calculated_total_cost = (total_cost + calculated_tax) - Utils.DOUBLE_EPSILON;
                dialog_text_total_cost.setText(NumberFormat.getInstance(Locale.getDefault()).format(calculated_total_cost) + " " + shop_currency);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.customerNames = new ArrayList();
        databaseAccess.open();
        List<HashMap<String, String>> customer = databaseAccess.getCustomers();
        for (int i = 0; i < customer.size(); i++) {
            this.customerNames.add(customer.get(i).get(DatabaseOpenHelper.CUSTOMER_NAME));
        }

        dialog_img_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCart.this.customerAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                ProductCart.this.customerAdapter.addAll(ProductCart.this.customerNames);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = ProductCart.this.getLayoutInflater().inflate(R.layout.dialog_list_search, (ViewGroup) null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                ((TextView) dialogView.findViewById(R.id.dialog_title)).setText(R.string.select_customer);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(ProductCart.this.customerAdapter);
                ((EditText) dialogView.findViewById(R.id.dialog_input)).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ProductCart.this.customerAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                final AlertDialog alertDialog = dialog.create();
                ((Button) dialogView.findViewById(R.id.dialog_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alertDialog.dismiss();
                        dialog_customer.setText(ProductCart.this.customerAdapter.getItem(position));
                    }
                });
            }
        });

        this.orderTypeNames = new ArrayList();
        databaseAccess.open();
        List<HashMap<String, String>> order_type = databaseAccess.getOrderType();
        for (int i1 = 0; i1 < order_type.size(); i1++) {
            this.orderTypeNames.add(order_type.get(i1).get(DatabaseOpenHelper.ORDER_TYPE_NAME));
        }

        dialog_img_order_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCart.this.orderTypeAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                ProductCart.this.orderTypeAdapter.addAll(ProductCart.this.orderTypeNames);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = ProductCart.this.getLayoutInflater().inflate(R.layout.dialog_list_search, (ViewGroup) null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                ((TextView) dialogView.findViewById(R.id.dialog_title)).setText(R.string.select_order_type);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(ProductCart.this.orderTypeAdapter);
                ((EditText) dialogView.findViewById(R.id.dialog_input)).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ProductCart.this.orderTypeAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                final AlertDialog alertDialog = dialog.create();
                ((Button) dialogView.findViewById(R.id.dialog_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alertDialog.dismiss();
                        dialog_order_type.setText(ProductCart.this.orderTypeAdapter.getItem(position));
                    }
                });
            }
        });

        this.paymentMethodNames = new ArrayList();
        databaseAccess.open();
        List<HashMap<String, String>> payment_method = databaseAccess.getPaymentMethod();
        for (int i2 = 0; i2 < payment_method.size(); i2++) {
            this.paymentMethodNames.add(payment_method.get(i2).get(DatabaseOpenHelper.PAYMENT_METHOD_NAME));
        }

        dialog_img_order_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCart.this.paymentMethodAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                ProductCart.this.paymentMethodAdapter.addAll(ProductCart.this.paymentMethodNames);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = ProductCart.this.getLayoutInflater().inflate(R.layout.dialog_list_search, (ViewGroup) null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                ((TextView) dialogView.findViewById(R.id.dialog_title)).setText(R.string.select_payment_method);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(ProductCart.this.paymentMethodAdapter);
                ((EditText) dialogView.findViewById(R.id.dialog_input)).addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        ProductCart.this.paymentMethodAdapter.getFilter().filter(charSequence);
                    }

                    public void afterTextChanged(Editable s) {

                    }
                });
                final AlertDialog alertDialog = dialog.create();
                ((Button) dialogView.findViewById(R.id.dialog_button)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        alertDialog.dismiss();
                        dialog_order_payment_method.setText(ProductCart.this.paymentMethodAdapter.getItem(position));
                    }
                });
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialog_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String discount;
                String order_type = dialog_order_type.getText().toString().trim();
                String order_payment_method = dialog_order_payment_method.getText().toString().trim();
                String customer_name = dialog_customer.getText().toString().trim();
                String discount1 = dialog_et_discount.getText().toString().trim();
                if (discount1.isEmpty()) {
                    discount = "0.00";
                } else {
                    discount = discount1;
                }
                ProductCart.this.proceedOrder(order_type, order_payment_method, customer_name, calculated_tax, discount);
                alertDialog.dismiss();
            }
        });
    }

    public void proceedOrder(String type, String payment_method, String customer_name, double calculated_tax, String discount) {
        JSONException e;
        String productId = DatabaseOpenHelper.PRODUCT_ID;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        if (databaseAccess.getCartItemCount() > 0) {
            databaseAccess.open();
            List<HashMap<String, String>> lines = databaseAccess.getCartProduct();
            if (lines.isEmpty()) {
                Toasty.error(this, (int) R.string.no_product_found, Toasty.LENGTH_SHORT).show();
                return;
            }
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
            String timeStamp = new SimpleDateFormat("yyMMdd-HHmmss", Locale.getDefault()).format(new Date()); // NoInvoice
            Log.d("Time", timeStamp);
            JSONObject obj = new JSONObject();
            try {
                obj.put(DatabaseOpenHelper.ORDER_LIST_DATE, currentDate);
                obj.put(DatabaseOpenHelper.ORDER_LIST_TIME, currentTime);
                obj.put(DatabaseOpenHelper.ORDER_LIST_TYPE, type);
                try {
                    obj.put(DatabaseOpenHelper.ORDER_LIST_PAYMENT_METHOD, payment_method);
                    obj.put(DatabaseOpenHelper.ORDER_LIST_CUSTOMER_NAME, customer_name);
                    try {
                        obj.put(DatabaseOpenHelper.ORDER_LIST_TAX, calculated_tax);
                        obj.put(DatabaseOpenHelper.ORDER_LIST_DISCOUNT, discount);
                        JSONArray array = new JSONArray();
                        int i = 0;
                        while (i < lines.size()) {
                            databaseAccess.open();
                            String product_id = lines.get(i).get(productId);
                            databaseAccess.open();
                            String product_name = databaseAccess.getProductName(product_id);
                            databaseAccess.open();
                            String weight_unit = databaseAccess.getWeightUnitName(lines.get(i).get(DatabaseOpenHelper.CART_PRODUCT_WEIGHT_UNIT));
                            JSONObject objp = new JSONObject();
                            try {
                                objp.put(productId, product_id);
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_NAME, product_name);
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_QTY, lines.get(i).get(DatabaseOpenHelper.CART_PRODUCT_QTY));
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_WEIGHT, weight_unit);
                                objp.put(DatabaseOpenHelper.CART_PRODUCT_STOCK, lines.get(i).get(DatabaseOpenHelper.CART_PRODUCT_STOCK));
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_PRICE, lines.get(i).get(DatabaseOpenHelper.CART_PRODUCT_PRICE));
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_PRODUCT_PRICE_OLD, lines.get(i).get(DatabaseOpenHelper.CART_PRODUCT_PRICE_OLD));
                                objp.put(DatabaseOpenHelper.ORDER_DETAILS_ORDER_DATE, currentDate);
                                array.put(objp);
                                //Log.d("TAG", product_id,product_name,weight_unit);
                                i++;
                            } catch (JSONException e2) {
                                e = e2;
                                e.printStackTrace();
                                saveOrderInOfflineDb(obj);
                                return;
                            }
                        }
                        obj.put("lines", array);
                    } catch (JSONException e3) {
                        e = e3;
                        e.printStackTrace();
                        saveOrderInOfflineDb(obj);
                        return;
                    }
                } catch (JSONException e4) {
                    e = e4;
                    e.printStackTrace();
                    saveOrderInOfflineDb(obj);
                    return;
                }
            } catch (JSONException e5) {
                e = e5;
                e.printStackTrace();
                saveOrderInOfflineDb(obj);
                return;
            }
            saveOrderInOfflineDb(obj);
            return;
        }
        Toasty.error(this, (int) R.string.no_product_in_cart, Toasty.LENGTH_SHORT).show();
    }

    private void saveOrderInOfflineDb(JSONObject obj) {
        String timeStamp = new SimpleDateFormat("yyMMdd-HHmmss", Locale.getDefault()).format(new Date());
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.insertOrder(timeStamp, obj);
        Toasty.success(this, (int) R.string.order_done_successful, Toasty.LENGTH_SHORT).show();
        startActivity(new Intent(this, OrdersActivity.class));
        finish();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            Log.d("if","bu if");
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, PosActivity.class));
        finish();
        Log.d("else","bu else");

        return true;
    }
}
