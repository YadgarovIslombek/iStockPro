package com.ida.istockpro.report;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ida.istockpro.BaseActivity;
import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExpenseGraphActivity extends BaseActivity {
    TextView textView_SelectYear;
    BarChart barChart;
    TextView textView_TotalExpense;
    int mYear = 2021;
    DecimalFormat decimalFormat;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_graph);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.monthly_expense_in_graph);

        this.textView_SelectYear = findViewById(R.id.tv_select_year);
        this.barChart = findViewById(R.id.barChart);
        this.textView_TotalExpense = findViewById(R.id.tv_total_expense);

        this.barChart.setDrawBarShadow(false);
        this.barChart.setDrawValueAboveBar(true);
        this.barChart.setMaxVisibleValueCount(50);
        this.barChart.setPinchZoom(false);
        this.barChart.setDrawGridBackground(true);

        decimalFormat = new DecimalFormat("#0.00");
        String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        TextView textView = this.textView_SelectYear;
        textView.setText(currentYear + " " + getString(R.string.year) );
        getGraphData();
    }

    @SuppressLint("SetTextI18n")
    public void getGraphData() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        String[] monthNumber = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            databaseAccess.open();
            String MonthNum = monthNumber[i];
            barEntries.add(new BarEntry((float) i, databaseAccess.getMonthlyExpenseAmount(MonthNum, "" + this.mYear)));
        }
        XAxis xAxis = this.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Yan", "Fev", "Mar", "Apr", "May", "Iyn", "Iyl", "Aug", "Sen", "Okt", "Noy", "Dek"}));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(12);
        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.monthly_expense_report));
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        this.barChart.setData(barData);
        this.barChart.setScaleEnabled(false);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        double totalExpense = databaseAccess.getTotalExpense(DatabaseOpenHelper.YEARLY);
        TextView textView = this.textView_TotalExpense;
        textView.setText(getString(R.string.total_expense) + ": "  + NumberFormat.getInstance(Locale.getDefault()).format(totalExpense) + " " + currency);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}