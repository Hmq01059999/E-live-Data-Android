package org.openconnectivity.e_livedatastage1.ui.dashboard;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.openconnectivity.e_livedatastage1.MyMarkerView;
import org.openconnectivity.e_livedatastage1.R;
import org.openconnectivity.e_livedatastage1.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements OnChartValueSelectedListener {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private LineChart chart;

    private PieChart growthRateChart;
    private PieChart conversionRateChart;
    private PieChart lossRateChart;

    private HorizontalBarChart consumeChart;

    private PieChart preferenceChart;

    List<Entry> totalValue = new ArrayList<Entry>();
    List<Entry> activeValue = new ArrayList<Entry>();
    List<Entry> lossValue = new ArrayList<Entry>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chart = binding.overViewChart;//找到折线图

        growthRateChart = binding.growthRateChart;//找到增长率环状图
        conversionRateChart = binding.conversionRateChart;//找到转化率环状图
        lossRateChart = binding.lossRateChart;//找到流失率环状图

        consumeChart = binding.consumeChart;//找到消费区间分析图

        preferenceChart = binding.preferenceChart;//找到用户偏好图

        initOverViewLineChart();

        showPreferChart();
        showGrowthChart();
        showConvertChart();
        showLossChart();

        initBarChart();

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void initOverViewLineChart(){

        for(int i = 0; i < 10; i++){
            float val1 = (float) (Math.random()*500);
            float val2 = (float) (Math.random()*350);
            float val3 = (float) (Math.random()*200);
            totalValue.add(new Entry(i,val1,getResources().getDrawable(R.drawable.tao)));
            activeValue.add(new Entry(i,val2,getResources().getDrawable(R.drawable.tao)));
            lossValue.add(new Entry(i,val3,getResources().getDrawable(R.drawable.tao)));
        }

        LineDataSet totalSet = new LineDataSet(totalValue,"用户总数");
        LineDataSet activeSet = new LineDataSet(activeValue,"活跃用户数");
        LineDataSet lossSet = new LineDataSet(lossValue,"流失用户数");

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
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setExtraRightOffset(20f);

        //设置 坐标轴 轴线的粗细
        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setLabelCount(9);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.enableAxisLineDashedLine(10f,0f,0f);
            //xAxis.setCenterAxisLabels(true);

        }
        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setGranularity(100f);
            //chart.getAxisLeft().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 0f, 0f);
            // axis range
            yAxis.setAxisMaximum(500f);
            yAxis.setAxisMinimum(0f);
        }
        //totalSet曲线设置

        {
            totalSet.setMode(LineDataSet.Mode.LINEAR);
            totalSet.setDrawIcons(false);

            //画虚线
            totalSet.enableDashedLine(10f,0f,0f);

            //黑色线和点
            totalSet.setColor(getResources().getColor(R.color.orange1));
            totalSet.setCircleColor(getResources().getColor(R.color.orange1));

            //线的粗细以及点的大小
            totalSet.setLineWidth(2f);
            totalSet.setCircleRadius(3f);

            //点是实心圆
            totalSet.setDrawCircleHole(true);

            //customize legend entry
            totalSet.setFormLineWidth(1f);
            totalSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            totalSet.setFormSize(10f);

            //数值的文本大小
            totalSet.setValueTextSize(9f);
            totalSet.setDrawValues(false);

            //设置填充区域的颜色
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.orange0_shadow);
                totalSet.setFillDrawable(drawable);
            }else{
                totalSet.setFillColor(Color.BLACK);
            }

            //画选择线，虚线
            totalSet.enableDashedHighlightLine(10f,5f,0f);

            //设置填充区域
            totalSet.setDrawFilled(true);
            totalSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
        }
        //activeSet曲线设置
        {
            activeSet.setMode(LineDataSet.Mode.LINEAR);
            activeSet.setDrawIcons(false);

            //画虚线
            activeSet.enableDashedLine(10f,0f,0f);

            //黑色线和点
            activeSet.setColor(getResources().getColor(R.color.orange2));
            activeSet.setCircleColor(getResources().getColor(R.color.orange2));

            //线的粗细以及点的大小
            activeSet.setLineWidth(2f);
            activeSet.setCircleRadius(3f);

            //点是实心圆
            activeSet.setDrawCircleHole(true);

            //customize legend entry
            activeSet.setFormLineWidth(1f);
            activeSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            activeSet.setFormSize(10f);

            //数值的文本大小
            activeSet.setValueTextSize(9f);
            activeSet.setDrawValues(false);

            //画选择线，虚线
            activeSet.enableDashedHighlightLine(10f,5f,0f);

            //设置填充区域的颜色
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.orange1_shadow);
                activeSet.setFillDrawable(drawable);
            }else{
                activeSet.setFillColor(Color.BLACK);
            }

            //设置填充区域
            activeSet.setDrawFilled(true);
            activeSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
        }

        //lossSet曲线设置
        {
            lossSet.setMode(LineDataSet.Mode.LINEAR);
            lossSet.setDrawIcons(false);

            //画虚线
            lossSet.enableDashedLine(10f,0f,0f);

            //黑色线和点
            lossSet.setColor(getResources().getColor(R.color.orange));
            lossSet.setCircleColor(getResources().getColor(R.color.orange));

            //线的粗细以及点的大小
            lossSet.setLineWidth(2f);
            lossSet.setCircleRadius(3f);

            //点是实心圆
            lossSet.setDrawCircleHole(true);

            //customize legend entry
            lossSet.setFormLineWidth(1f);
            lossSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            lossSet.setFormSize(10f);

            //数值的文本大小
            lossSet.setValueTextSize(9f);
            lossSet.setDrawValues(false);

            //画选择线，虚线
            lossSet.enableDashedHighlightLine(10f,5f,0f);

            //设置填充区域的颜色
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.orange2_shadow);
                lossSet.setFillDrawable(drawable);
            }else{
                lossSet.setFillColor(Color.BLACK);
            }

            //设置填充区域
            lossSet.setDrawFilled(true);
            lossSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
        }

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(totalSet);
        dataSets.add(activeSet);
        dataSets.add(lossSet);

        LineData data = new LineData(dataSets);

        //设置数据
        chart.setData(data);

        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        //chart.getLegend().setEnabled(false);


    }

    private void showPreferChart(){
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(335,"家电"));
        yvals.add(new PieEntry(310,"食品"));
        yvals.add(new PieEntry(234,"美妆"));
        yvals.add(new PieEntry(135,"衣服"));
        yvals.add(new PieEntry(1548,"鞋包"));
        yvals.add(new PieEntry(135,"书籍"));

        //设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ea9518"));
        colors.add(Color.parseColor("#eeb173"));
        colors.add(Color.parseColor("#efb336"));
        colors.add(Color.parseColor("#e98f36"));
        colors.add(Color.parseColor("#f3ca7e"));
        colors.add(Color.parseColor("#ea986c"));

        PieChartManager pieChartManager = new PieChartManager(preferenceChart);
        pieChartManager.showRingPieChart(yvals,colors);

    }

    private void showGrowthChart(){
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(74));
        yvals.add(new PieEntry(26));

        //设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ea9518"));
        colors.add(Color.parseColor("#f5e1ba"));

        PieChartManager pieChartManager = new PieChartManager(growthRateChart);
        growthRateChart.setDrawCenterText(true);
        growthRateChart.setCenterText("增长率");
        growthRateChart.setCenterTextColor(Color.BLACK); //中间问题的颜色
        growthRateChart.setCenterTextSizePixels(45);  //中间文字的大小px
        growthRateChart.setCenterTextRadiusPercent(0f);
        growthRateChart.setCenterTextTypeface(Typeface.DEFAULT); //中间文字的样式
        growthRateChart.setCenterTextOffset(0, 0); //中间文字的偏移量
        growthRateChart.getLegend().setEnabled(false);
        growthRateChart.setExtraOffsets(0, 0, 0, 0);
        growthRateChart.setHoleRadius(75f);//设置中间洞的大小
        pieChartManager.showRingPieChart(yvals,colors);
    }

    private void showConvertChart(){
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(30));
        yvals.add(new PieEntry(70));

        //设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ea9518"));
        colors.add(Color.parseColor("#f5e1ba"));

        PieChartManager pieChartManager = new PieChartManager(conversionRateChart);
        conversionRateChart.setDrawCenterText(true);
        conversionRateChart.setCenterText("转化率");
        conversionRateChart.setCenterTextColor(Color.BLACK); //中间问题的颜色
        conversionRateChart.setCenterTextSizePixels(45);  //中间文字的大小px
        conversionRateChart.setCenterTextRadiusPercent(0f);
        conversionRateChart.setCenterTextTypeface(Typeface.DEFAULT); //中间文字的样式
        conversionRateChart.setCenterTextOffset(0, 0); //中间文字的偏移量
        conversionRateChart.getLegend().setEnabled(false);
        conversionRateChart.setExtraOffsets(0, 0, 0, 0);
        conversionRateChart.setHoleRadius(75f);//设置中间洞的大小
        pieChartManager.showRingPieChart(yvals,colors);
    }

    private void showLossChart(){
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(46));
        yvals.add(new PieEntry(54));

        //设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ea9518"));
        colors.add(Color.parseColor("#f5e1ba"));

        PieChartManager pieChartManager = new PieChartManager(lossRateChart);
        lossRateChart.setDrawCenterText(true);
        lossRateChart.setCenterText("流失率");
        lossRateChart.setCenterTextColor(Color.BLACK); //中间问题的颜色
        lossRateChart.setCenterTextSizePixels(45);  //中间文字的大小px
        lossRateChart.setCenterTextRadiusPercent(0f);
        lossRateChart.setCenterTextTypeface(Typeface.DEFAULT); //中间文字的样式
        lossRateChart.setCenterTextOffset(0, 0); //中间文字的偏移量
        lossRateChart.getLegend().setEnabled(false);
        lossRateChart.setExtraOffsets(0, 0, 0, 0);
        lossRateChart.setHoleRadius(75f);//设置中间洞的大小
        pieChartManager.showRingPieChart(yvals,colors);
    }

    private void initBarChart(){
        consumeChart.getDescription().setEnabled(false);//不显示描述
        consumeChart.getLegend().setEnabled(false);//不显示图例
        consumeChart.setExtraOffsets(10,10,10,10);//设置偏移量，类似于内边距
        consumeChart.setDragEnabled(false);//是否可以拖拽
        consumeChart.setScaleEnabled(false);//是否可以放大
        setBAxis();//设置坐标轴
        setBData();//设置数据
    }

    private void setBData(){
        //C的数据
        List<BarEntry> entryList1 = new ArrayList<>();
        entryList1.add(new BarEntry(0,320f));
        entryList1.add(new BarEntry(1,362f));
        entryList1.add(new BarEntry(2,301f));
        entryList1.add(new BarEntry(3,334f));

        BarDataSet barDataSet1 = new BarDataSet(entryList1,"");
        barDataSet1.setColor(Color.parseColor("#f8dca9"));
        barDataSet1.setValueTextSize(8f);
        barDataSet1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return v + "";
            }
        });

        //A的数据
        List<BarEntry> entryList2 = new ArrayList<>();
        entryList2.add(new BarEntry(0,120f));
        entryList2.add(new BarEntry(1,132f));
        entryList2.add(new BarEntry(2,101f));
        entryList2.add(new BarEntry(3,134f));

        BarDataSet barDataSet2 = new BarDataSet(entryList2,"");
        barDataSet2.setColor(Color.parseColor("#ea9518"));
        barDataSet2.setValueTextSize(8f);
        barDataSet2.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return v + "";
            }
        });

        //B的数据
        List<BarEntry> entryList3 = new ArrayList<>();
        entryList3.add(new BarEntry(0,220f));
        entryList3.add(new BarEntry(1,232f));
        entryList3.add(new BarEntry(2,201f));
        entryList3.add(new BarEntry(3,234f));

        BarDataSet barDataSet3 = new BarDataSet(entryList3,"");
        barDataSet3.setColor(Color.parseColor("#efb336"));
        barDataSet3.setValueTextSize(8f);
        barDataSet3.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return v + "";
            }
        });

        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet3);
        dataSets.add(barDataSet2);
        BarData barData = new BarData(dataSets);
        barData.setBarWidth(0.6f);
        consumeChart.setData(barData);

    }

    private void setBAxis(){
        XAxis xAxis = consumeChart.getXAxis();
        xAxis.setPosition((XAxis.XAxisPosition.BOTTOM));
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setLabelCount(4);
        final String label[] = {"50以下","100-300","300-500","500以上"};
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                try {
                    return label[(int) v];
                } catch (Exception e) {
                    return "";
                }
            }
        });

        YAxis yAxis_left = consumeChart.getAxisLeft();
        yAxis_left.setAxisMinimum(0f);
        yAxis_left.setAxisMaximum(500f);
        yAxis_left.setTextSize(10f);
        yAxis_left.setEnabled(false);
        //yAxis_left.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        //yAxis_right.setGranularity(100f);

        YAxis yAxis_right = consumeChart.getAxisRight();
        yAxis_right.setAxisMinimum(0f);
        yAxis_right.setAxisMaximum(500f);
        yAxis_right.setTextSize(10f);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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