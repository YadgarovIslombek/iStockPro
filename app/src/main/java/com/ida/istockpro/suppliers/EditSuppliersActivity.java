package com.ida.istockpro.suppliers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.internal.view.SupportMenu;

import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.ida.istockpro.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class EditSuppliersActivity extends BaseActivity {

    EditText editText_Name;
    EditText editText_Address;
    EditText editText_Contact;
    EditText editText_Fax;
    EditText editText_Sales;
    EditText editText_Hp;
    EditText editText_Account;
    EditText editText_Information;
    EditText editText_LastUpdate;

    TextView textView_Edit;
    TextView textView_Update;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    private String datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_suppliers);

        this.editText_Name = findViewById(R.id.et_supplier_name);
       // this.editText_Address = findViewById(R.id.et_supplier_address);
        this.editText_Contact = findViewById(R.id.et_supplier_contact);
//        this.editText_Fax = findViewById(R.id.et_supplier_fax);
        this.editText_Sales = findViewById(R.id.et_supplier_sales);
        //this.editText_Hp = findViewById(R.id.et_supplier_hp);
        this.editText_Account = findViewById(R.id.et_supplier_account);
        this.editText_Information = findViewById(R.id.et_supplier_information);
        this.editText_LastUpdate = findViewById(R.id.et_supplier_last_update);

        this.textView_Edit = findViewById(R.id.tv_edit_supplier);
        this.textView_Update = findViewById(R.id.tv_update_supplier);

        final String get_supplier_id = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_ID);
        String get_supplier_name = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_NAME);
        String get_supplier_contact = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_CONTACT);
        String get_supplier_sales = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_SALES);
        String get_supplier_account = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_ACCOUNT);
        String get_supplier_information = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_INFORMATION);
        String get_supplier_last_update = getIntent().getExtras().getString(DatabaseOpenHelper.SUPPLIER_LAST_UPDATE);

        this.editText_Name.setText(get_supplier_name);
        this.editText_Contact.setText(get_supplier_contact);
        this.editText_Sales.setText(get_supplier_sales);
        this.editText_Account.setText(get_supplier_account);
        this.editText_Information.setText(get_supplier_information);
        this.editText_LastUpdate.setText(get_supplier_last_update);

        this.editText_Name.setEnabled(false);
        this.editText_Contact.setEnabled(false);
        this.editText_Sales.setEnabled(false);
        this.editText_Account.setEnabled(false);
        this.editText_Information.setEnabled(false);
        this.editText_LastUpdate.setEnabled(false);
        this.textView_Update.setVisibility(View.INVISIBLE);

        this.textView_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditSuppliersActivity.this.editText_Name.setEnabled(true);
                EditSuppliersActivity.this.editText_Contact.setEnabled(true);
                EditSuppliersActivity.this.editText_Sales.setEnabled(true);
                EditSuppliersActivity.this.editText_Account.setEnabled(true);
                EditSuppliersActivity.this.editText_Information.setEnabled(true);
                //EditSuppliersActivity.this.editText_LastUpdate.setEnabled(false);
                EditSuppliersActivity.this.editText_Name.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_Address.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_Contact.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_Sales.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_Account.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_Information.setTextColor(SupportMenu.CATEGORY_MASK);
                EditSuppliersActivity.this.editText_LastUpdate.setTextColor(SupportMenu.CATEGORY_MASK);

                EditSuppliersActivity.this.textView_Edit.setVisibility(View.GONE);
                EditSuppliersActivity.this.textView_Update.setVisibility(View.VISIBLE);
            }
        });

        this.textView_Update.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                datetime = simpleDateFormat.format(calendar.getTime());

                String suppliers_name = EditSuppliersActivity.this.editText_Name.getText().toString().trim();
                String suppliers_contact = EditSuppliersActivity.this.editText_Contact.getText().toString().trim();
                String suppliers_sales = EditSuppliersActivity.this.editText_Sales.getText().toString().trim();
                String suppliers_account = EditSuppliersActivity.this.editText_Account.getText().toString().trim();
                String suppliers_information = EditSuppliersActivity.this.editText_Information.getText().toString().trim();
                String suppliers_last_update = EditSuppliersActivity.this.datetime;
                //String suppliers_last_update = EditSuppliersActivity.this.editText_LastUpdate.getText().toString().trim();

                if (suppliers_name.isEmpty()) {
                    EditSuppliersActivity.this.editText_Name.setError(EditSuppliersActivity.this.getString(R.string.enter_suppliers_name));
                    EditSuppliersActivity.this.editText_Name.requestFocus();
                } else if (suppliers_contact.isEmpty()) {
                    EditSuppliersActivity.this.editText_Contact.setError(EditSuppliersActivity.this.getString(R.string.enter_suppliers_name));
                    EditSuppliersActivity.this.editText_Contact.requestFocus();
                } else if (suppliers_account.isEmpty()) {
                    EditSuppliersActivity.this.editText_Account.setError(EditSuppliersActivity.this.getString(R.string.enter_suppliers_account));
                    EditSuppliersActivity.this.editText_Account.requestFocus();
                } else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditSuppliersActivity.this);
                    databaseAccess.open();
                    if (databaseAccess.updateSuppliers(get_supplier_id, suppliers_name, suppliers_contact, suppliers_sales, suppliers_account, suppliers_information, suppliers_last_update)) {
                        Toasty.success(EditSuppliersActivity.this, (int) R.string.update_successfully, Toasty.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditSuppliersActivity.this, SuppliersActivity.class);
                        //intent.addFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
                        EditSuppliersActivity.this.startActivity(intent);
                        return;
                    }
                    Toasty.error(EditSuppliersActivity.this, (int) R.string.failed, Toasty.LENGTH_SHORT).show();
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