package com.ida.istockpro.setting.categories;

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


public class EditCategoryActivity extends BaseActivity {

    EditText editText_Category;
    TextView textView_Edit;
    TextView textView_Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_category);

        this.editText_Category = findViewById(R.id.et_category_name);
        this.textView_Edit = findViewById(R.id.tv_edit_category);
        this.textView_Update = findViewById(R.id.tv_update_category);

        final String category_id = getIntent().getExtras().getString(DatabaseOpenHelper.CATEGORY_ID);
        this.editText_Category.setText(getIntent().getExtras().getString(DatabaseOpenHelper.CATEGORY_NAME));

        this.editText_Category.setEnabled(false);
        this.textView_Update.setVisibility(View.INVISIBLE);

        this.textView_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCategoryActivity.this.editText_Category.setEnabled(true);
                EditCategoryActivity.this.textView_Update.setVisibility(View.VISIBLE);
                EditCategoryActivity.this.editText_Category.setTextColor(SupportMenu.CATEGORY_MASK);
            }
        });

        this.textView_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = EditCategoryActivity.this.editText_Category.getText().toString().trim();
                if (category_name.isEmpty()) {
                    EditCategoryActivity.this.editText_Category.setError(EditCategoryActivity.this.getString(R.string.enter_category_name));
                    EditCategoryActivity.this.editText_Category.requestFocus();
                    return;
                }
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCategoryActivity.this);
                databaseAccess.open();
                if (databaseAccess.updateCategory(category_id, category_name)) {
                    Toasty.success(EditCategoryActivity.this, (int) R.string.category_updated, Toasty.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCategoryActivity.this, CategoriesActivity.class);
                    //intent.addFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
                    EditCategoryActivity.this.startActivity(intent);
                    return;
                }
                Toasty.error(EditCategoryActivity.this, (int) R.string.failed, Toasty.LENGTH_SHORT).show();
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