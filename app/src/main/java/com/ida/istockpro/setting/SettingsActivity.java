package com.ida.istockpro.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ida.istockpro.DashboardActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.setting.backup.BackupActivity;
import com.ida.istockpro.setting.categories.CategoriesActivity;
import com.ida.istockpro.setting.payment_method.PaymentMethodActivity;
import com.ida.istockpro.setting.shop.ShopInformationActivity;
import com.ida.istockpro.setting.weight_unit.WeightActivity;


public class SettingsActivity extends AppCompatActivity {

    CardView cardView_ShopInfo;
    CardView cardView_Category;
    CardView cardView_WeightUnit;
    CardView cardView_PaymentMethod;
    CardView cardView_Backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        this.cardView_ShopInfo = findViewById(R.id.card_shop_info);
        this.cardView_Category = findViewById(R.id.card_category);
        this.cardView_WeightUnit = findViewById(R.id.card_weight_unit);
        this.cardView_PaymentMethod = findViewById(R.id.card_payment_method);
        this.cardView_Backup = findViewById(R.id.card_backup);
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });
//        new Utils().interstitialAdsShow(this);

        this.cardView_ShopInfo.setOnClickListener(v -> SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, ShopInformationActivity.class)));
        this.cardView_Category.setOnClickListener(v -> SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, CategoriesActivity.class)));
        this.cardView_WeightUnit.setOnClickListener(v -> SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, WeightActivity.class)));
        this.cardView_PaymentMethod.setOnClickListener(v -> SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, PaymentMethodActivity.class)));
         this.cardView_Backup.setOnClickListener(v -> SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, BackupActivity.class)));
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
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}