package org.openconnectivity.e_livedatastage1.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.openconnectivity.e_livedatastage1.MyMarkerView;
import org.openconnectivity.e_livedatastage1.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryInfoActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private LineChart chart;
    private BarChart barChart;
    private List<TopSale> topSaleList = new ArrayList<>();
    String responseData;
    public static final int GET_ANALYSIS_INFO = 13;

    TextView show_gmv;
    TextView show_pcu;
    TextView show_averageTime_value;
    TextView show_averageTime_lastTime;
    TextView show_acu_value;
    TextView show_acu_lastTime;

    String show_label[];
    String show_barLabel[];
    int seriesData[];
    int barData[];
    int count;
    int barCount;

    int[] topSaleImage = new int[]{
           R.drawable.rank1,
            R.drawable.rank2,
            R.drawable.rank3,
            R.drawable.rank4,
            R.drawable.rank5,
            R.drawable.rank6,
            R.drawable.rank7,
            R.drawable.rank8,
            R.drawable.rank9,
            R.drawable.rank10
    };

    String topSaleName[];

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case GET_ANALYSIS_INFO:
                    try{
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject liveAnalysis = jsonObject.getJSONObject("liveAnalysis");
                        String gmv = liveAnalysis.getString("gmv");
                        String pcu = liveAnalysis.getString("pcu");
                        show_gmv.setText(gmv);
                        show_pcu.setText(pcu);

                        JSONObject acu = liveAnalysis.getJSONObject("acu");
                        String acu_value = acu.getString("value");
                        String acu_lastTime = acu.getString("lastTime");
                        show_acu_value.setText(acu_value);
                        show_acu_lastTime.setText(acu_lastTime);

                        JSONObject averageTime = liveAnalysis.getJSONObject("averageTime");
                        String averageTime_value = averageTime.getString("value");
                        String averageTime_lastTime = averageTime.getString("lastTime");
                        show_averageTime_value.setText(averageTime_value);
                        show_averageTime_lastTime.setText(averageTime_lastTime);

                        JSONObject orderQuantity = jsonObject.getJSONObject("orderQuantity");
                        JSONArray xAxisData = orderQuantity.getJSONArray("xAxisData");
                        show_label = new String[xAxisData.length()];
                        for(int i = 0; i<xAxisData.length(); i++){
                            show_label[i] = xAxisData.getString(i);
                            Log.d("show_label",""+show_label[i]);
                        }

                        JSONArray series = orderQuantity.getJSONArray("seriesData");
                        seriesData = new int[series.length()];
                        count = series.length();
                        for(int i = 0; i<seriesData.length; i++){
                            seriesData[i] = series.getInt(i);
                        }
                        //???????????????????????????
                        initLineChart();

                        JSONObject orderAmount = jsonObject.getJSONObject("orderAmount");
                        JSONArray xAxis = orderAmount.getJSONArray("xAxisData");
                        show_barLabel = new String[xAxis.length()];
                        barCount = xAxis.length();
                        for(int i = 0; i<barCount; i++){
                            show_barLabel[i] = xAxis.getString(i);
                        }

                        JSONArray yAxis = orderAmount.getJSONArray("seriesData");
                        barData = new int[yAxis.length()];
                        for(int i = 0; i<yAxis.length(); i++){
                            barData[i] = yAxis.getInt(i);
                        }
                        //???????????????????????????
                        initBarChart();
                        //??????????????????
                        JSONArray topSale = jsonObject.getJSONArray("topSale");
                        topSaleName = new String[topSale.length()];
                        for(int i = 0; i<topSale.length(); i++){
                            if(i!=(topSale.length()-1)){
                                topSaleName[i] = topSale.getJSONObject(i).getString("tradeName");

                                TopSale rank = new TopSale(topSaleImage[i],topSaleName[i]);
                                topSaleList.add(rank);
                            }
                        }

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_topSale);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(HistoryInfoActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        TopSaleAdapter adapter = new TopSaleAdapter(topSaleList);
                        recyclerView.setAdapter(adapter);


                    }catch(Exception e){
                        e.printStackTrace();
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_content);

        show_gmv = findViewById(R.id.gmv);
        show_pcu = findViewById(R.id.pcu);
        show_averageTime_value = findViewById(R.id.averageTime_value);
        show_averageTime_lastTime = findViewById(R.id.averageTime_lasttime);
        show_acu_value = findViewById(R.id.acu_value);
        show_acu_lastTime = findViewById(R.id.acu_lasttime);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://elivedate.kdsa.cn/liveAnalysis")
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("response",""+responseData);

                    Message message = new Message();
                    message.what = GET_ANALYSIS_INFO;
                    handler.sendMessage(message);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();


    }

    private void initLineChart(){
        //???????????????
        {
            chart = findViewById(R.id.orderQuantity);
            //????????????
            chart.setBackgroundColor(Color.WHITE);
            //????????????????????????
            chart.getDescription().setEnabled(false);
            //??????????????????
            chart.setTouchEnabled(true);
            //?????????????????????
            chart.setOnChartValueSelectedListener(this);
            //????????????????????????
            chart.setDrawGridBackground(false);
            //?????????????????????box
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            mv.setChartView(chart);
            chart.setMarker(mv);

            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setPinchZoom(true);
            //chart.setExtraRightOffset(20f);
        }

        //?????? ????????? ???????????????
        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setLabelCount(4,false);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.enableAxisLineDashedLine(10f,0f,0f);
            //xAxis.setCenterAxisLabels(true);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, AxisBase axisBase) {
                    try {
                        return show_label[(int) v];
                    } catch (Exception e) {
                        return "";
                    }
                }
            });

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

        setData(count);

        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        //Legend l = chart.getLegend();
        chart.getLegend().setEnabled(false);
    }

    private void setData(int count){
        ArrayList<Entry> values = new ArrayList<>();

        for(int i = 0; i < count; i++){
            float val = (float) (seriesData[i]);
            values.add(new Entry(i,val));

        }

        LineDataSet set1;

        if(chart.getData()!=null && chart.getData().getDataSetCount()>0){
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }else{
            //???????????????????????????????????????
            set1 = new LineDataSet(values,"Order Quantity chart");

            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setDrawIcons(false);

            //?????????
            set1.enableDashedLine(10f,0f,0f);

            //???????????????
            set1.setColor(getResources().getColor(R.color.orange));
            set1.setCircleColor(getResources().getColor(R.color.orange));

            //??????????????????????????????
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);

            //???????????????
            set1.setDrawCircleHole(true);
            set1.setDrawValues(false);

            //customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            set1.setFormSize(10f);

            //?????????????????????
            set1.setValueTextSize(9f);

            //?????????????????????
            set1.enableDashedHighlightLine(10f,5f,0f);

            //??????????????????
            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);//???????????????
            //??????????????????????????????
            LineData data = new LineData(dataSets);

            //????????????
            chart.setData(data);

        }
    }

    private void initBarChart(){
        barChart = findViewById(R.id.orderAmount);
        barChart.getDescription().setEnabled(false);//???????????????
        barChart.setExtraOffsets(10,10,10,10);//???????????????
        barChart.setDragEnabled(false);//??????????????????
        barChart.setScaleEnabled(false);//??????????????????
        barChart.setDrawGridBackground(false);//?????????????????????

        //?????????????????????box
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        //??????x???
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//??????x??????????????????
        xAxis.setDrawGridLines(false);//??????????????????????????????
        xAxis.setLabelCount(5);//??????x?????????????????????
        xAxis.setTextSize(10f);//x?????????????????????

        // ??????x????????????????????????
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value < show_barLabel.length) {
                    return show_barLabel[(int) value];
                } else {
                    return "";
                }
            }
        });

        xAxis.setYOffset(15);//???????????????x??????????????????????????????

        //??????y???
        YAxis yAxis_right = barChart.getAxisRight();
        yAxis_right.setEnabled(false);//??????????????????y???

        YAxis yAxis_left = barChart.getAxisLeft();
        yAxis_left.setDrawGridLines(false);
        yAxis_left.setGranularity(50f);
        yAxis_left.setAxisMaximum(250f);
        yAxis_left.setAxisMinimum(0f);
        yAxis_left.setTextSize(10f); // ??????y??????????????????
        barChart.animateY(1400, Easing.EasingOption.EaseInSine);// y?????????

        //????????????
        List<IBarDataSet> barSets = new ArrayList<>();
        //x??????????????????????????????y???????????????????????????
        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i<barCount; i++){
            barEntries.add(new BarEntry(i,barData[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setValueTextSize(10f);
        barDataSet.setColor(Color.parseColor("#ea9518"));//???????????????
        barDataSet.setDrawValues(false);
        barSets.add(barDataSet);



        BarData barData = new BarData(barSets);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.getLegend().setEnabled(false);
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
