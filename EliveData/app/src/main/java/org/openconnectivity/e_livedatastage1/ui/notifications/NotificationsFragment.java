package org.openconnectivity.e_livedatastage1.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openconnectivity.e_livedatastage1.R;
import org.openconnectivity.e_livedatastage1.databinding.FragmentNotificationsBinding;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsFragment extends Fragment{

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    String responseData;

    TextView edit;
    TextView show_userName;
    TextView show_phoneNumber;
    TextView show_userId;
    TextView show_createTime;
    TextView show_email;
    TextView show_autification;

    public static final int GET_INFO = 11;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            switch(msg.what){
                case GET_INFO:
                    try{
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject data = jsonObject.getJSONObject("data");
                        String username = data.getString("userName");
                        String userId = data.getString("userId");
                        String authentication = data.getString("authentication");
                        String phoneNumber = data.getString("phoneNumber");
                        String email = data.getString("email");
                        String createTime = data.getString("createTime");

                        show_userName.setText(username);
                        show_phoneNumber.setText(phoneNumber);
                        show_createTime.setText(createTime);
                        show_userId.setText(userId);

                        if(authentication.equals("true")){
                            show_autification.setText("已认证");
                        }else{
                            show_autification.setText("未认证");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

            }

        }
    };



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        edit = (TextView) binding.edit;
        show_userName = (TextView) binding.userName;
        show_phoneNumber = (TextView) binding.phoneNumber;
        show_autification = (TextView) binding.autification;
        show_createTime = (TextView) binding.createTime;
        show_userId = (TextView) binding.userId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://elivedate.kdsa.cn/personalCenter")
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("response",""+responseData);

                    Message message = new Message();
                    message.what = GET_INFO;
                    handler.sendMessage(message);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View edit_info = factory.inflate(R.layout.dialog_edit_info,null);
                final EditText userName = (EditText)  edit_info.findViewById(R.id.userName);
                userName.setHint(show_userName.getText());
                final EditText phoneNumber = (EditText) edit_info.findViewById(R.id.phoneNumber);
                phoneNumber.setHint(show_phoneNumber.getText());
                final EditText email = (EditText) edit_info.findViewById(R.id.email);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("修改个人信息")
                        .setIcon(R.drawable.userinfo_icon)
                        .setView(edit_info)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!TextUtils.isEmpty(userName.getText().toString())){
                                     String name = userName.getText().toString();
                                     show_userName.setText(name);
                                }else{
                                    Toast.makeText(getContext(),"用户名不能为空",Toast.LENGTH_SHORT);
                                }

                                if(!TextUtils.isEmpty(userName.getText().toString())){
                                    String number = phoneNumber.getText().toString();
                                    show_phoneNumber.setText(number);
                                }else{
                                    Toast.makeText(getContext(),"电话号码不能为空",Toast.LENGTH_SHORT);
                                }
                            }
                        }).show();
            }
        });

        //final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                //textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}