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
import com.ida.istockpro.R;
import com.ida.istockpro.database.DatabaseAccess;
import com.ida.istockpro.database.DatabaseOpenHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SalesGraphActivity extends AppCompatActivity {
    TextView textView_SelectYear;
    BarChart barChart;
    TextView textView_TotalSales;
    TextView textView_TotalTax;
    TextView textView_TotalDiscount;
    TextView textView_NetSales;
    int mYear = 2021;
    DecimalFormat decimalFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_graph);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.monthly_sales_graph);

        this.textView_SelectYear = findViewById(R.id.tv_select_year);
        this.barChart = findViewById(R.id.barChart);
        this.textView_TotalSales = findViewById(R.id.tv_total_sales);
        this.textView_TotalTax = findViewById(R.id.tv_total_tax);
        this.textView_TotalDiscount = findViewById(R.id.tv_total_discount);
        this.textView_NetSales = findViewById(R.id.tv_net_sales);
        this.barChart.setDrawBarShadow(false);
        this.barChart.setDrawValueAboveBar(true);
        this.barChart.setMaxVisibleValueCount(50);
        this.barChart.setPinchZoom(false);
        this.barChart.setDrawGridBackground(true);

        decimalFormat = new DecimalFormat("#0.00");
        String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
        TextView textView = this.textView_SelectYear;
        textView.setText(currentYear +  " " + getString(R.string.year));
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
            barEntries.add(new BarEntry((float) i, databaseAccess.getMonthlySalesAmount(MonthNum, "" + this.mYear)));
        }
        XAxis xAxis = this.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Yan", "Fev", "Mar", "Apr", "May", "Iyn", "Iyl", "Aug", "Sen", "Oct", "Noy", "Dek"}));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(12);
        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.monthly_sales_report));
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        this.barChart.setData(barData);
        this.barChart.setScaleEnabled(false);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        double sub_total = databaseAccess.getTotalOrderPrice(DatabaseOpenHelper.YEARLY);
        TextView textView = this.textView_TotalSales;
        textView.setText(getString(R.string.total_sales) +  " " + NumberFormat.getInstance(Locale.getDefault()).format(sub_total) + " " + currency);

        databaseAccess.open();
        double get_tax = databaseAccess.getTotalTax(DatabaseOpenHelper.YEARLY);
        TextView textView1 = this.textView_TotalTax;
        textView1.setText(getString(R.string.total_tax) + " (+) : " + NumberFormat.getInstance(Locale.getDefault()).format(get_tax) + " " + currency);

        databaseAccess.open();
        double get_discount = databaseAccess.getTotalDiscount(DatabaseOpenHelper.YEARLY);
        TextView textView2 = this.textView_TotalDiscount;
        textView2.setText(getString(R.string.total_discount) + " (-) : " + NumberFormat.getInstance(Locale.getDefault()).format(get_discount) + " " + currency);

        TextView textView3 = this.textView_NetSales;
        textView3.setText(getString(R.string.net_sales) + ": " + NumberFormat.getInstance(Locale.getDefault()).format((sub_total + get_tax) - get_discount) + " " + currency);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }
}