package org.openconnectivity.e_livedatastage1.ui.home;

import android.graphics.Color;
import android.graphics.DashPathEffect;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.openconnectivity.e_livedatastage1.MyMarkerView;
import org.openconnectivity.e_livedatastage1.R;
import org.openconnectivity.e_livedatastage1.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LineChart chart;

    private List<History> historyList = new ArrayList<>();

    private List<TopSale> topSaleList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        historyList.clear();

        //设置历史记录
        initHistory();
        RecyclerView recyclerView = binding.recyclerHistory;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        HistoryAdapter adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);


        //设置曲线图
        {
            chart = binding.UVChart;
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
            MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
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
            xAxis.setLabelCount(9);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.enableAxisLineDashedLine(10f,0f,0f);
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

        setData(10,250);

        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        //Legend l = chart.getLegend();
        chart.getLegend().setEnabled(false);

        // draw legend entries as lines
        //l.setForm(Legend.LegendForm.LINE);





        //final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                //textView.setText(s);
//            }
//        });
        return root;
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
            set1 = new LineDataSet(values,"UV chart");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            //设置填充区域的颜色
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.orange_shadow);
                set1.setFillDrawable(drawable);
            }else{
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);//添加数据集
            //用数据集创建一个对象
            LineData data = new LineData(dataSets);

            //设置数据
            chart.setData(data);

        }
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

    private void initHistory(){
        History history1 = new History("疯狂星期四宠粉大促","2022-10-1","16:30-18:47");
        historyList.add(history1);

        History history2 = new History("疯狂星期五宠粉大促","2022-10-2","16:00-18:47");
        historyList.add(history2);
    }
}