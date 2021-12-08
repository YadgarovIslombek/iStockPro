package com.ida.istockpro.sotuvTarihi;



import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.DashboardActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.adapter.OrderAdapter;
import com.ida.istockpro.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;



public class OrdersActivity extends BaseActivity {

    EditText editText_Search;
    TextView textView_NoProducts;
    ImageView imgNoProduct;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_history);

        this.editText_Search = findViewById(R.id.et_search_order);
        this.textView_NoProducts = findViewById(R.id.tv_no_order);
        this.imgNoProduct = findViewById(R.id.image_no_product);
        this.recyclerView = findViewById(R.id.order_recyclerview);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.recyclerView.setHasFixedSize(true);

        this.textView_NoProducts.setVisibility(View.GONE);
        this.imgNoProduct.setVisibility(View.GONE);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> orderList = databaseAccess.getOrderList();
        if (orderList.size() <= 0) {
            Toasty.info(this, (int) R.string.no_order_found, Toasty.LENGTH_SHORT).show();
            this.recyclerView.setVisibility(View.GONE);
            this.imgNoProduct.setVisibility(View.VISIBLE);
            this.imgNoProduct.setImageResource(R.drawable.not_found);
            this.textView_NoProducts.setVisibility(View.VISIBLE);
        } else {
            OrderAdapter orderAdapter1 = new OrderAdapter(this, orderList);
            this.orderAdapter = orderAdapter1;
            this.recyclerView.setAdapter(orderAdapter1);
        }

        this.editText_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrdersActivity.this);
                databaseAccess.open();
                List<HashMap<String, String>> searchOrder = databaseAccess.searchOrderList(s.toString());
                if (searchOrder.size() <= 0) {
                    OrdersActivity.this.recyclerView.setVisibility(View.GONE);
                    OrdersActivity.this.imgNoProduct.setVisibility(View.VISIBLE);
                    OrdersActivity.this.imgNoProduct.setImageResource(R.drawable.no_data);
                    return;
                }
                OrdersActivity.this.recyclerView.setVisibility(View.VISIBLE);
                OrdersActivity.this.imgNoProduct.setVisibility(View.GONE);
                OrdersActivity.this.recyclerView.setAdapter(new OrderAdapter(OrdersActivity.this, searchOrder));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
//        if (item.equals("Cashier")) {
//            startActivity(new Intent(this, CashierDashboard.class));
//        } else {
//            startActivity(new Intent(this, DashboardActivity.class));
//        }
        finish();

        //super.onBackPressed();
    }
}