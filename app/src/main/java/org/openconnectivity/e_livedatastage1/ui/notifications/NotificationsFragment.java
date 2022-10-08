package org.openconnectivity.e_livedatastage1.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.openconnectivity.e_livedatastage1.R;
import org.openconnectivity.e_livedatastage1.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment{

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView edit = (TextView) binding.edit;
        TextView show_userName = (TextView) binding.userName;
        TextView show_phoneNumber = (TextView) binding.phoneNumber;

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View edit_info = factory.inflate(R.layout.dialog_edit_info,null);
                final EditText userName = (EditText)  edit_info.findViewById(R.id.userName);
                final EditText phoneNumber = (EditText) edit_info.findViewById(R.id.phoneNumber);
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