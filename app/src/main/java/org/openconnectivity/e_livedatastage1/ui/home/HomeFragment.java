package org.openconnectivity.e_livedatastage1.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openconnectivity.e_livedatastage1.MyMarkerView;
import org.openconnectivity.e_livedatastage1.R;
import org.openconnectivity.e_livedatastage1.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LineChart chart;
    String responseData;
    int count = 0;

    TextView show_liveTimes;
    TextView show_followQuantity;
    TextView show_views;

    String show_label[];
    int seriesData[];
    String Ydata[];
    public static final int GET_LIVE_INFO = 12;

    private List<History> historyList = new ArrayList<>();

    private List<TopSale> topSaleList = new ArrayList<>();

    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case GET_LIVE_INFO:
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject user = jsonObject.getJSONObject("user");
                        String liveTimes = user.getString("liveTimes");
                        String followQuantity = user.getString("followQuantity");
                        String views = user.getString("views");

                        show_liveTimes.setText(liveTimes);
                        show_followQuantity.setText(followQuantity);
                        show_views.setText(views);

                        JSONObject uv = jsonObject.getJSONObject("uv");

                        String labels = uv.getString("xAxisData");
                        labels = labels.replaceAll("\"","");
                        labels = labels.replace("[","");
                        labels = labels.replace("]","");
                        Log.d("label",""+labels);
                        show_label = labels.split(",");
                        count = show_label.length;

                        JSONArray jsonArray = uv.getJSONArray("seriesData");
                        seriesData = new int[jsonArray.length()];

                        for(int i = 0; i < jsonArray.length();i++){
                            seriesData[i] = jsonArray.getInt(i);
                        }
                        //画出UV曲线
                        initUVChart();

                        JSONArray historyLive = jsonObject.getJSONArray("historyLive");
                        for(int i = 0; i < historyLive.length(); i++){
                            JSONObject object = historyLive.getJSONObject(i);
                            History history = new History(object.getString("liveContent"),object.getString("liveTime"),object.getString("liveDuration"));
                            historyList.add(history);
                        }

                        RecyclerView recyclerView = binding.recyclerHistory;
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);
                        HistoryAdapter adapter = new HistoryAdapter(historyList);
                        recyclerView.setAdapter(adapter);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }

        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        historyList.clear();

        show_liveTimes = (TextView) binding.liveTimes;
        show_followQuantity = (TextView) binding.followQuantity;
        show_views = (TextView) binding.views;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://elivedate.kdsa.cn/historylive")
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("response",""+responseData);

                    Message message = new Message();
                    message.what = GET_LIVE_INFO;
                    handler.sendMessage(message);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();


//        RecyclerView recyclerView = binding.recyclerHistory;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        HistoryAdapter adapter = new HistoryAdapter(historyList);
//        recyclerView.setAdapter(adapter);

        return root;
    }

    private void initUVChart(){
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
            //xAxis.setLabelCount(10);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.enableAxisLineDashedLine(10f,0f,0f);

            //label[] = {"50以下","100-300","300-500","500以上"};
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

        setData(12);

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