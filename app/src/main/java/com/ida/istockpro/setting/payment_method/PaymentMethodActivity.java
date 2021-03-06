package com.ida.istockpro.setting.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.adapter.PaymentMethodAdapter;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.setting.SettingsActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;



public class PaymentMethodActivity extends BaseActivity {

    EditText editText_Search;
    FloatingActionButton floatingActionButton_fabAdd;
    ImageView imgNoPaymentMethod;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_payment_method);

        this.editText_Search = findViewById(R.id.et_payment_method_search);
        this.floatingActionButton_fabAdd = findViewById(R.id.fab_add);
        this.imgNoPaymentMethod = findViewById(R.id.image_no_payment_method);
        this.recyclerView = findViewById(R.id.payment_method_recyclerview);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.recyclerView.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> paymentMethodData = databaseAccess.getPaymentMethod();
        Log.d("data", "" + paymentMethodData.size());
        if (paymentMethodData.size() <= 0) {
            Toasty.info(this, (int) R.string.no_data_found, Toasty.LENGTH_SHORT).show();
            this.imgNoPaymentMethod.setImageResource(R.drawable.no_data);
        } else {
            this.imgNoPaymentMethod.setVisibility(View.GONE);
            this.recyclerView.setAdapter(new PaymentMethodAdapter(this, paymentMethodData));
        }

        this.floatingActionButton_fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentMethodActivity.this.startActivity(new Intent(PaymentMethodActivity.this, AddPaymentMethodActivity.class));
            }
        });

        this.editText_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(PaymentMethodActivity.this);
                databaseAccess.open();
                List<HashMap<String, String>> searchPaymentMethodList = databaseAccess.searchPaymentMethod(s.toString());
                if (searchPaymentMethodList.size() <= 0) {
                    PaymentMethodActivity.this.recyclerView.setVisibility(View.GONE);
                    PaymentMethodActivity.this.imgNoPaymentMethod.setVisibility(View.VISIBLE);
                    PaymentMethodActivity.this.imgNoPaymentMethod.setImageResource(R.drawable.no_data);
                    return;
                }
                PaymentMethodActivity.this.recyclerView.setVisibility(View.VISIBLE);
                PaymentMethodActivity.this.imgNoPaymentMethod.setVisibility(View.GONE);
                PaymentMethodActivity.this.recyclerView.setAdapter(new PaymentMethodAdapter(PaymentMethodActivity.this, searchPaymentMethodList));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
        //super.onBackPressed();
    }
}