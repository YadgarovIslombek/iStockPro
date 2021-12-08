package com.ida.istockpro.data;



import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.DashboardActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.adapter.ProductAdapter;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;
import com.ida.istockpro.utils.SharedPref;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductActivity extends BaseActivity {
    EditText editText_Search;
    FloatingActionButton floatingActionButton_fabAdd;
    ImageView imgNoProduct;
    ProgressDialog loading;
    ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    String activate_value = "10000";
    EditText edit_active;
    SharedPref sharedPref;
    private static boolean isDemoActive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
         Date currentTime = Calendar.getInstance().getTime();
        String a = String.valueOf(currentTime.getTime());
        sharedPref = new SharedPref(this);
        isDemoActive =sharedPref.loadLang();



//        a1 = Integer.parseInt(String.valueOf(str));
//        b1 = Integer.parseInt(String.valueOf(str1));


//        Toast.makeText(getApplicationContext(), String.valueOf(a), Toast.LENGTH_SHORT).show();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_product);

        this.floatingActionButton_fabAdd = findViewById(R.id.fab_add);
        this.editText_Search = findViewById(R.id.et_search);
        this.recyclerView = findViewById(R.id.product_recyclerview);
        this.imgNoProduct = findViewById(R.id.image_no_product);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.recyclerView.setHasFixedSize(true);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<HashMap<String, String>> listProductData = databaseAccess.getProducts();
        Log.d("dataa", "" + listProductData.size());
        if(!isDemoActive) {
            if (listProductData.size() == 5) {
                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(ProductActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.diqqat))
                        .withMessage(R.string.bu_demo)
                        .withEffect(Slidetop)
                        .withDialogColor("#03AAF3") //use color code for dialog
                        .withButton1Text(getString(R.string.activate))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                String m1 = (m_androidId.replaceAll("[\\D]", ""));
                                String st = m1;
                                StringBuilder str = new StringBuilder(st);
                                str.delete(4, 20);
                                Log.d("str", str.toString());

                                StringBuilder str1 = new StringBuilder(a);
                                Log.d("str1", str1.toString());
                                str1.reverse();
                                str1.delete(4, 15);


                                Log.d("mBlueOZI", String.valueOf(st));
                                Log.d("timeOZI", String.valueOf(a));

                                Log.d("mBlue", String.valueOf(str));
                                Log.d("time", String.valueOf(str1));

                                int a1, b1;
                                a1 = Integer.parseInt(String.valueOf(str));
                                b1 = Integer.parseInt(String.valueOf(str1));

                                try {
                                    int d1 = a1 + b1 - 2222 + 23 + 22 + 14 - 2214;
                                    activate_value = String.valueOf(d1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //activate_value = String.valueOf(d1);
                                AlertDialog.Builder adb =
                                        new AlertDialog.Builder(ProductActivity.this);

                                ViewGroup subView = (ViewGroup) getLayoutInflater().// inflater view
                                        inflate(R.layout.custom_dial_view, null, false);

                                try {// set data of yore layout

                                    ((TextView) subView.findViewById(R.id.txt_kod))//get element TextView
                                            .setText("Quyidagi ID raqamni bizga yuboring va aktivlashtirish kodini oling. " + " ID" + " : " + st + "|" + a);//set text

                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }
                                try {// set data of yore layout

                                    edit_active = ((EditText) subView.findViewById(R.id.activED));//get element TextView

                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }

//                            adb.setPositiveButton("textPOSITIVE", new DialogInterface.OnClickListener() {//one method go
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //  TODO some code
//                                }
//                            });//one method end

                                final AlertDialog alertDialog =
                                        adb.setTitle("")// set ttile
                                                .setView(subView).create();// add view in AlertDialog.Builder, and create AlertDialog

                                try { //two method go

                                    ((Button) subView.findViewById(R.id.activeBtn))
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //  TODO some code
                                                    String kod_user = edit_active.getText().toString().trim();

                                                    if (kod_user.equalsIgnoreCase(activate_value)) {
                                                        Toast.makeText(getApplicationContext(), "vaavav", Toast.LENGTH_SHORT).show();

                                                        alertDialog.dismiss();
                                                        dialogBuilder.dismiss();
                                                        Intent intent = new Intent(ProductActivity.this, DashboardActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        sharedPref.setActive("isActive",true);

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "xato", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }  //two method end

                                try { //two method go

                                    ((Button) subView.findViewById(R.id.tgBtn))
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //  TODO some code
                                                    Intent intent_tlg = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=ID2214"));
                                                    startActivity(intent_tlg);
                                                }
                                            });

                                } catch (NullPointerException npe) {
                                    npe.printStackTrace();
                                }  //two method end

                                alertDialog.show(); //show in YoreActivity
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            }
        }else {

                this.floatingActionButton_fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductActivity.this.startActivity(new Intent(ProductActivity.this, AddProductActivity.class));
                    }
                });
            }

        if(listProductData.size() <= 0){
            Toasty.info(this,(int) R.string.no_product_fount,Toasty.LENGTH_SHORT).show();
            this.imgNoProduct.setImageResource(R.drawable.no_data);
        }else{
            this.imgNoProduct.setVisibility(View.GONE);
            ProductAdapter productAdapter1 = new ProductAdapter(this,listProductData);
            this.productAdapter = productAdapter1;
            this.recyclerView.setAdapter(productAdapter1);
        }

        //SEARCH PRODUCT
        this.editText_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DatabaseAccess databaseAccess =  DatabaseAccess.getInstance(ProductActivity.this);
                databaseAccess.open();
                List<HashMap<String, String>> searchProductList =  databaseAccess.getSearchProducts(s.toString());
                if(searchProductList.size() <= 0){
                    ProductActivity.this.recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);
                    return;
                }
                ProductActivity.this.recyclerView.setVisibility(View.VISIBLE);
                ProductActivity.this.imgNoProduct.setVisibility(View.GONE);
                ProductActivity productActivity = ProductActivity.this;
                productActivity.productAdapter = new ProductAdapter(productActivity, searchProductList);
                ProductActivity.this.recyclerView.setAdapter(ProductActivity.this.productAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_product_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            onBackPressed();
            return true;
        }else if (itemId != R.id.menu_export){
            return super.onOptionsItemSelected(item);
        }else{
            folderChooser();
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();

        //super.onBackPressed();
    }

    public void folderChooser(){
        new ChooserDialog((Activity) this).disableTitle(true).withFilter(true, false, new String[0]).withChosenListener(new ChooserDialog.Result() {
            @Override
            public void onChoosePath(String dir, File dirFile) {
                ProductActivity.this.onExport(dir);
                Log.d("path", dir);
            }
        }).build().show();
    }

    public void onExport(String path){
        String directory_path = path;
        File file  =  new File(directory_path);
        if(!file.exists()){
            file.mkdirs();
        }
        new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME,directory_path).exportSingleTable("products", "Tovarlar.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {
                ProductActivity.this.loading = new ProgressDialog(ProductActivity.this);
                ProductActivity.this.loading.setMessage(ProductActivity.this.getString(R.string.data_exporting_please_wait));
                ProductActivity.this.loading.setCancelable(false);
                ProductActivity.this.loading.show();
            }

            @Override
            public void onCompleted(String filePath) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ProductActivity.this.loading.dismiss();
                        Toasty.success(ProductActivity.this, (int) R.string.data_successfully_exported, Toasty.LENGTH_SHORT).show();
                    }
                }, 5000);
            }

            @Override
            public void onError(Exception e) {
                ProductActivity.this.loading.dismiss();
                Toasty.error(ProductActivity.this, (int) R.string.data_export_fail, Toasty.LENGTH_SHORT).show();
            }
        });
    }
}