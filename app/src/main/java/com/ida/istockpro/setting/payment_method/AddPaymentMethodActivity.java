package com.ida.istockpro.setting.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;

import es.dmoral.toasty.Toasty;



public class AddPaymentMethodActivity extends BaseActivity {

    EditText editText_PaymentMethod;
    TextView textView_Add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_payment_method);

        this.editText_PaymentMethod = findViewById(R.id.et_payment_method_name);
        this.textView_Add = findViewById(R.id.tv_add_payment_method);

        this.textView_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payment_method_name = AddPaymentMethodActivity.this.editText_PaymentMethod.getText().toString().trim();
                if (payment_method_name.isEmpty()) {
                    AddPaymentMethodActivity.this.editText_PaymentMethod.setError(AddPaymentMethodActivity.this.getString(R.string.enter_payment_method_name));
                    AddPaymentMethodActivity.this.editText_PaymentMethod.requestFocus();
                    return;
                }
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddPaymentMethodActivity.this);
                databaseAccess.open();
                if (databaseAccess.addPaymentMethod(payment_method_name)) {
                    Toasty.success(AddPaymentMethodActivity.this, (int) R.string.successfully_added, Toasty.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPaymentMethodActivity.this, PaymentMethodActivity.class);
                    //intent.addFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
                    AddPaymentMethodActivity.this.startActivity(intent);
                    return;
                }
                Toasty.error(AddPaymentMethodActivity.this, (int) R.string.failed, Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}