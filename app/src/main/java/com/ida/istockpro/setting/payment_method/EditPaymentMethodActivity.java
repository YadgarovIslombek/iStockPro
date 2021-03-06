package com.ida.istockpro.setting.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;


import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import es.dmoral.toasty.Toasty;



public class EditPaymentMethodActivity extends BaseActivity {

    EditText editText_PaymentMethod;
    TextView textView_Edit;
    TextView textView_Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.update_payment_method);

        this.editText_PaymentMethod = findViewById(R.id.et_payment_method_name);
        this.textView_Edit = findViewById(R.id.tv_edit_payment_method);
        this.textView_Update = findViewById(R.id.tv_update_payment_method);

        final String payment_method_id = getIntent().getExtras().getString(DatabaseOpenHelper.PAYMENT_METHOD_ID);
        this.editText_PaymentMethod.setText(getIntent().getExtras().getString(DatabaseOpenHelper.PAYMENT_METHOD_NAME));

        this.editText_PaymentMethod.setEnabled(false);
        this.textView_Update.setVisibility(View.INVISIBLE);

        this.textView_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPaymentMethodActivity.this.editText_PaymentMethod.setEnabled(true);
                EditPaymentMethodActivity.this.textView_Update.setVisibility(View.VISIBLE);
                EditPaymentMethodActivity.this.editText_PaymentMethod.setTextColor(SupportMenu.CATEGORY_MASK);
            }
        });

        this.textView_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payment_method_name = EditPaymentMethodActivity.this.editText_PaymentMethod.getText().toString().trim();
                if (payment_method_name.isEmpty()) {
                    EditPaymentMethodActivity.this.editText_PaymentMethod.setError(EditPaymentMethodActivity.this.getString(R.string.payment_method_name));
                    EditPaymentMethodActivity.this.editText_PaymentMethod.requestFocus();
                    return;
                }
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditPaymentMethodActivity.this);
                databaseAccess.open();
                if (databaseAccess.updatePaymentMethod(payment_method_id, payment_method_name)) {
                    Toasty.success(EditPaymentMethodActivity.this, (int) R.string.successfully_updated, Toasty.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditPaymentMethodActivity.this, PaymentMethodActivity.class);
                    //intent.addFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
                    EditPaymentMethodActivity.this.startActivity(intent);
                    return;
                }
                Toasty.error(EditPaymentMethodActivity.this, (int) R.string.failed, Toasty.LENGTH_SHORT).show();
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