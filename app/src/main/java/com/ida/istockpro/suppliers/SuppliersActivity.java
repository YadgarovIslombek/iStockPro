package com.ida.istockpro.suppliers;


import static com.ida.istockpro.LoginActivity.item;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ida.istockpro.DashboardActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.WarehouseDashboard;
import com.ida.istockpro.adapter.SupplierAdapter;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;



public class SuppliersActivity extends AppCompatActivity {

    EditText editText_Search;
    FloatingActionButton floatingActionButton_fabAdd;
    ImageView imgNoProduct;
    ProgressDialog loading;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_suppliers);

        this.editText_Search = findViewById(R.id.et_supplier_search);
        this.floatingActionButton_fabAdd = findViewById(R.id.fab_add);
        this.imgNoProduct = findViewById(R.id.image_no_product);
        this.recyclerView = findViewById(R.id.supplier_recyclerview);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.recyclerView.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> suppliersData = databaseAccess.getSuppliers();
        Log.d("data", "" + suppliersData.size());
        if (suppliersData.size() <= 0) {
            Toasty.info(this, (int) R.string.no_suppliers_found, Toasty.LENGTH_SHORT).show();
            this.imgNoProduct.setImageResource(R.drawable.no_data);
        } else {
            this.imgNoProduct.setVisibility(View.GONE);
            this.recyclerView.setAdapter(new SupplierAdapter(this, suppliersData));
        }

        this.floatingActionButton_fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuppliersActivity.this.startActivity(new Intent(SuppliersActivity.this, AddSuppliersActivity.class));
            }
        });

        this.editText_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(SuppliersActivity.this);
                databaseAccess.open();
                List<HashMap<String, String>> searchSupplier = databaseAccess.searchSuppliers(s.toString());
                if (searchSupplier.size() <= 0) {
                    SuppliersActivity.this.recyclerView.setVisibility(View.GONE);
                    SuppliersActivity.this.imgNoProduct.setVisibility(View.VISIBLE);
                    SuppliersActivity.this.imgNoProduct.setImageResource(R.drawable.no_data);
                    return;
                }
                SuppliersActivity.this.recyclerView.setVisibility(View.VISIBLE);
                SuppliersActivity.this.imgNoProduct.setVisibility(View.GONE);
                SuppliersActivity.this.recyclerView.setAdapter(new SupplierAdapter(SuppliersActivity.this, searchSupplier));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_suppliers_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() != R.id.menu_export_supplier) {
            return super.onOptionsItemSelected(item);
        } else {
            folderChooser();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (item.equals("Warehouse")) {
            startActivity(new Intent(this, WarehouseDashboard.class));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
        finish();

        //super.onBackPressed();
    }

    public void folderChooser() {
        new ChooserDialog((Activity) this).displayPath(true).withFilter(true, false, new String[0])
                .withChosenListener(new ChooserDialog.Result() {

                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        SuppliersActivity.this.onExport(path);
                        Log.d("path", path);
                    }
                }).build().show();
    }

    public void onExport(String path) {
        String directory_path = path;
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directory_path)
                .exportSingleTable("suppliers", "Dillerlar_royhati.xls", new SQLiteToExcel.ExportListener() {

                    @Override
                    public void onStart() {
                        SuppliersActivity.this.loading = new ProgressDialog(SuppliersActivity.this);
                        SuppliersActivity.this.loading.setMessage(SuppliersActivity.this.getString(R.string.data_exporting_please_wait));
                        SuppliersActivity.this.loading.setCancelable(false);
                        SuppliersActivity.this.loading.show();
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                SuppliersActivity.this.loading.dismiss();
                                Toasty.success(SuppliersActivity.this, (int) R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onError(Exception e) {
                        SuppliersActivity.this.loading.dismiss();
                        Toasty.error(SuppliersActivity.this, (int) R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
                    }
                });
    }
}