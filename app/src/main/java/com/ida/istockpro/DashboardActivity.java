package com.ida.istockpro;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.ida.istockpro.customers.CustomersActivity;
import com.ida.istockpro.data.ProductActivity;
import com.ida.istockpro.expense.ExpenseActivity;
import com.ida.istockpro.kassa.PosActivity;
import com.ida.istockpro.report.ReportActivity;
import com.ida.istockpro.setting.SettingsActivity;
import com.ida.istockpro.sotuvTarihi.OrdersActivity;
import com.ida.istockpro.suppliers.SuppliersActivity;
import com.ida.istockpro.utils.Constant;
import com.ida.istockpro.utils.LocaleManager;
import com.ida.istockpro.utils.SharedPref;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class DashboardActivity extends BaseActivity {

    private static long back_pressed;
    // private AdView adView;
    ImageView imageView_Profile;
    SharedPref sharedPref;
    CardView cardView_kaca;
    CardView cardView_pigura;
    CardView cardView_kasir;
    CardView cardView_data;
    CardView cardView_print;
    CardView cardView_settings;
    CardView cardView_expense;
    CardView cardView_report;
    CardView cardView_customers;
    CardView cardView_suppliers;
    CardView cardLogout;
    CardView cardAbout;

    TextView tvUserType;
    ImageView imgLogout;
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    String userType;

    private static final int TIME_DELAY = 2000;
    private static long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle(R.string.app_name);
//        Objects.requireNonNull(getSupportActionBar()).hide();


        this.cardView_kasir = findViewById(R.id.card_kasir);
        this.cardView_data = findViewById(R.id.card_data);
        this.cardView_print = findViewById(R.id.card_print);
        this.cardView_settings = findViewById(R.id.card_settings);
        this.cardView_expense = findViewById(R.id.card_expense);
        this.cardView_report = findViewById(R.id.card_report);
        this.cardView_customers = findViewById(R.id.card_customers);
        this.cardView_suppliers = findViewById(R.id.card_suppliers);
        this.cardLogout = findViewById(R.id.card_logout);
        this.cardAbout = findViewById(R.id.card_about_us);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");
        if (Build.VERSION.SDK_INT >= 23)
        {

            requestPermission();
        }

        this.cardView_kasir.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, PosActivity.class)));
        this.cardView_data.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, ProductActivity.class)));
        this.cardView_print.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, OrdersActivity.class)));
        this.cardView_expense.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, ExpenseActivity.class)));

        this.cardView_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    Toasty.error(DashboardActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();

                }
            }
        });

        this.cardView_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(DashboardActivity.this, ReportActivity.class);
                    startActivity(intent);
                }else {
                    Toasty.error(DashboardActivity.this, R.string.you_dont_have_permission_to_access_this_page, Toast.LENGTH_SHORT).show();

                }
            }
        });

        this.cardView_customers.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, CustomersActivity.class)));
        this.cardView_suppliers.setOnClickListener(v -> DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, SuppliersActivity.class)));
        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(DashboardActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.logout))
                        .withMessage(R.string.want_to_logout_from_app)
                        .withEffect(Slidetop)
                        .withDialogColor("#03AAF3") //use color code for dialog
                        .withButton1Text(getString(R.string.yes))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString(Constant.SP_PHONE, "");
                                editor.putString(Constant.SP_PASSWORD, "");
                                editor.putString(Constant.SP_USER_NAME, "");
                                editor.putString(Constant.SP_USER_TYPE, "");
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                dialogBuilder.dismiss();
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
        });
        cardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
                startActivity(intent);

            }
        });
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.local_uzb:
                setNewLocale(this, LocaleManager.UZB);
                return true;
            case R.id.local_rus:
                setNewLocale(this, LocaleManager.RUSSIAN);
                return true;
            default:
                Log.d("Default", "default");
        }
        return super.onOptionsItemSelected(item);
    }
    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    //double back press to exit
    @Override
    public void onBackPressed() {
        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            Log.d("Dexter", "All permissions are granted!");
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(DashboardActivity.this.getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    /*
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //builder.show();
        AlertDialog alert_show = builder.create();
        alert_show.show();

        Button button_positive = alert_show.getButton(DialogInterface.BUTTON_POSITIVE);
        button_positive.setTextColor(Color.BLACK);

        Button button_negative = alert_show.getButton(DialogInterface.BUTTON_NEGATIVE);
        button_negative.setTextColor(Color.RED);
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}