package org.openconnectivity.e_livedatastage1.ui.home;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.openconnectivity.e_livedatastage1.MyMarkerView;
import org.openconnectivity.e_livedatastage1.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryInfoActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private LineChart chart;
    private BarChart barChart;
    private List<TopSale> topSaleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_content);

        initLineChart();

        initBarChart();

        initTopSale();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_topSale);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TopSaleAdapter adapter = new TopSaleAdapter(topSaleList);
        recyclerView.setAdapter(adapter);


    }

    private void initLineChart(){
        //设置曲线图
        {
            chart = findViewById(R.id.orderQuantity);
            //设置背景
            chart.setBackgroundColor(Color.WHITE);
            //是否显示坐标数据
            chart.getDescription().setEnabled(false);
            //是否支持双击
            chart.setTouchEnabled(true);
            //值选中回调监听
            chart.setOnChartValueSelectedListener(this);
            //是否绘制网格背景
            chart.setDrawGridBackground(false);
            //显示坐标数据的box
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            mv.setChartView(chart);
            chart.setMarker(mv);

            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setPinchZoom(true);
            chart.setExtraRightOffset(20f);
        }

        //设置 坐标轴 轴线的粗细
        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setLabelCount(4);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.enableAxisLineDashedLine(10f,0f,0f);
            //xAxis.setCenterAxisLabels(true);

        }
        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setGranularity(50f);
            //chart.getAxisLeft().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 0f, 0f);
            // axis range
            yAxis.setAxisMaximum(250f);
            yAxis.setAxisMinimum(0f);
        }

        setData(5,250);

        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        //Legend l = chart.getLegend();
        chart.getLegend().setEnabled(false);
    }

    private void setData(int count, float range){
        ArrayList<Entry> values = new ArrayList<>();

        for(int i = 0; i < count; i++){
            float val = (float) (Math.random()*range);
            values.add(new Entry(i,val,getResources().getDrawable(R.drawable.tao)));

        }

        LineDataSet set1;
        //set1 = new LineDataSet(values,"照度");

        if(chart.getData()!=null && chart.getData().getDataSetCount()>0){
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }else{
            //创建一个数据集并给一个类型
            set1 = new LineDataSet(values,"Order Quantity chart");

            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setDrawIcons(false);

            //画虚线
            set1.enableDashedLine(10f,0f,0f);

            //黑色线和点
            set1.setColor(getResources().getColor(R.color.orange));
            set1.setCircleColor(getResources().getColor(R.color.orange));

            //线的粗细以及点的大小
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);

            //点是实心圆
            set1.setDrawCircleHole(true);

            //customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            set1.setFormSize(10f);

            //数值的文本大小
            set1.setValueTextSize(9f);

            //画选择线，虚线
            set1.enableDashedHighlightLine(10f,5f,0f);

            //设置填充区域
            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            //设置填充区域的颜色
//            if(Utils.getSDKInt() >= 18){
//                Drawable drawable = ContextCompat.getDrawable(this, R.color.orange_shadow);
//                set1.setFillDrawable(drawable);
//            }else{
//                set1.setFillColor(Color.BLACK);
//            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);//添加数据集
            //用数据集创建一个对象
            LineData data = new LineData(dataSets);

            //设置数据
            chart.setData(data);

        }
    }

    private void initBarChart(){
        barChart = findViewById(R.id.orderAmount);
        barChart.getDescription().setEnabled(false);//不显示描述
        barChart.setExtraOffsets(10,10,10,10);//设置内边距
        barChart.setDragEnabled(false);//是否可以拖拽
        barChart.setScaleEnabled(false);//是否可以放大
        barChart.setDrawGridBackground(false);//是否绘制网格线

        //设置x轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴显示在下方
        xAxis.setDrawGridLines(false);//是否绘制该轴的网格线
        xAxis.setLabelCount(5);//设置x轴上的标签个数
        xAxis.setTextSize(10f);//x轴上的标签大小
        final String labelName[] = {"1月","2月","3月","4月","5月"};
        // 设置x轴显示的值的格式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value < labelName.length) {
                    return labelName[(int) value];
                } else {
                    return "";
                }
            }
        });

        xAxis.setYOffset(15);//设置标签对x轴的偏移量，垂直方向

        //设置y轴
        YAxis yAxis_right = barChart.getAxisRight();
        yAxis_right.setEnabled(false);//不显示右边的y轴

        YAxis yAxis_left = barChart.getAxisLeft();
        yAxis_left.setDrawGridLines(false);
        yAxis_left.setGranularity(50f);
        yAxis_left.setAxisMaximum(250f);
        yAxis_left.setAxisMinimum(0f);
        yAxis_left.setTextSize(10f); // 设置y轴的标签大小
        barChart.animateY(1400, Easing.EasingOption.EaseInSine);// y轴动画

        //设置数据
        List<IBarDataSet> barSets = new ArrayList<>();
        //x是横坐标，表示位置，y是纵坐标，表示高度
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0,100f));
        barEntries.add(new BarEntry(1,140f));
        barEntries.add(new BarEntry(2,224f));
        barEntries.add(new BarEntry(3,100f));
        barEntries.add(new BarEntry(4,139f));
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setValueTextSize(10f);
        barDataSet.setColor(Color.parseColor("#ea9518"));//柱子的颜色
        barSets.add(barDataSet);

        BarData barData = new BarData(barSets);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.getLegend().setEnabled(false);
    }

    private void initTopSale(){
        TopSale rank1 = new TopSale(R.drawable.rank1,"iPhone 14");
        topSaleList.add(rank1);

        TopSale rank2 = new TopSale(R.drawable.rank2,"iPhone 14 Pro");
        topSaleList.add(rank2);

        TopSale rank3 = new TopSale(R.drawable.rank3,"iPhone 14 Pro Max");
        topSaleList.add(rank3);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + chart.getLowestVisibleX() + ", high: " + chart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + chart.getXChartMin() + ", xMax: " + chart.getXChartMax() + ", yMin: " + chart.getYChartMin() + ", yMax: " + chart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
