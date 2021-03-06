package com.ida.istockpro.customers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;


import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;



public class EditCustomersActivity extends BaseActivity {

    EditText editText_CustomerName;
    EditText editText_Address;
    EditText editText_Hp;
    EditText editText_Wa;
    EditText editText_Account;
    EditText editText_Information;
    EditText editText_LastUpdate;

    String getCustomer_id;
    String getCustomer_name;
    String getCustomer_address;
    String getCustomer_hp;
    String getCustomer_wa;
    String getCustomer_account;
    String getCustomer_information;
    String getCustomer_last_update;

    ImageView imageView_Copy;
    TextView textView_Edit;
    TextView textView_Update;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    private String datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customers);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_customer);

        this.editText_CustomerName = findViewById(R.id.et_customer_name);
        this.editText_Hp = findViewById(R.id.et_customer_hp);
        this.editText_Information = findViewById(R.id.et_customer_information);
        this.editText_LastUpdate = findViewById(R.id.et_customer_last_update);
        this.textView_Edit = findViewById(R.id.tv_edit_customer);
        this.textView_Update = findViewById(R.id.tv_update_customer);

        this.getCustomer_id = getIntent().getExtras().getString(DatabaseOpenHelper.CUSTOMER_ID);
        this.getCustomer_name = getIntent().getExtras().getString(DatabaseOpenHelper.CUSTOMER_NAME);
        this.getCustomer_hp = getIntent().getExtras().getString(DatabaseOpenHelper.CUSTOMER_HP);
        this.getCustomer_information = getIntent().getExtras().getString(DatabaseOpenHelper.CUSTOMER_INFORMATION);
        this.getCustomer_last_update = getIntent().getExtras().getString(DatabaseOpenHelper.CUSTOMER_LAST_UPDATE);

        this.editText_CustomerName.setText(this.getCustomer_name);
        this.editText_Hp.setText(this.getCustomer_hp);
        this.editText_Information.setText(this.getCustomer_information);
        this.editText_LastUpdate.setText(this.getCustomer_last_update);

        this.editText_CustomerName.setEnabled(false);
        this.editText_Hp.setEnabled(false);
        this.editText_Information.setEnabled(false);
        this.editText_LastUpdate.setEnabled(false);
        this.textView_Update.setVisibility(View.INVISIBLE);

        this.textView_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCustomersActivity.this.editText_CustomerName.setEnabled(true);
                EditCustomersActivity.this.editText_Hp.setEnabled(true);
                EditCustomersActivity.this.editText_Information.setEnabled(true);
                //EditCustomersActivity.this.editText_LastUpdate.setEnabled(false);
                EditCustomersActivity.this.editText_CustomerName.setTextColor(SupportMenu.CATEGORY_MASK);
                EditCustomersActivity.this.editText_Hp.setTextColor(SupportMenu.CATEGORY_MASK);
                EditCustomersActivity.this.editText_Information.setTextColor(SupportMenu.CATEGORY_MASK);
                EditCustomersActivity.this.editText_LastUpdate.setTextColor(SupportMenu.CATEGORY_MASK);

                EditCustomersActivity.this.textView_Edit.setVisibility(View.GONE);
                EditCustomersActivity.this.textView_Update.setVisibility(View.VISIBLE);

//                EditCustomersActivity.this.imageView_Copy.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        editText_Wa.setText(editText_Hp.getText().toString());
//                    }
//                });
            }
        });

        this.textView_Update.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                datetime = simpleDateFormat.format(calendar.getTime());

                String customer_name = EditCustomersActivity.this.editText_CustomerName.getText().toString().trim();
                String customer_hp = EditCustomersActivity.this.editText_Hp.getText().toString().trim();
                String customer_information = EditCustomersActivity.this.editText_Information.getText().toString().trim();
                String customer_last_update = EditCustomersActivity.this.datetime;
                //String customer_last_update = EditCustomersActivity.this.editText_LastUpdate.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    EditCustomersActivity.this.editText_CustomerName.setError(EditCustomersActivity.this.getString(R.string.enter_customer_name));
                    EditCustomersActivity.this.editText_CustomerName.requestFocus();
                }  else if (customer_hp.isEmpty()) {
                    EditCustomersActivity.this.editText_Hp.setError(EditCustomersActivity.this.getString(R.string.enter_customer_address));
                    EditCustomersActivity.this.editText_Hp.requestFocus();
                }  else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCustomersActivity.this);
                    databaseAccess.open();
                    if (databaseAccess.updateCustomer(EditCustomersActivity.this.getCustomer_id, customer_name, customer_hp, customer_information, customer_last_update)) {
                        Toasty.success(EditCustomersActivity.this, (int) R.string.customer_successfully_added, Toasty.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCustomersActivity.this, CustomersActivity.class);
                        //intent.addFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
                        EditCustomersActivity.this.startActivity(intent);
                        finish();
                        return;
                    }
                    Toasty.error(EditCustomersActivity.this, (int) R.string.failed, Toasty.LENGTH_SHORT).show();
                }
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