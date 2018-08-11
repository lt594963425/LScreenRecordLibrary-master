package com.guaju.screenrecordlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.guaju.screenrecorderlibrary.ScreenRecorderHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_start, bt_stop;
    private ScreenRecorderHelper srHelper;

    private Button mRecordBtn;
    int recordWith;
    int recordHeight;
    public int deviceWidth;
    public int deviceHeight;
    public int height;
    public int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        srHelper = MyApplication.getApp().getSRHelper();
        srHelper.initRecordService(this);
        initWindowParas();

    }

    private void initWindowParas() {
        height = dip2px(this, 100);
        width = dip2px(this, 150);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

    }

    private void initView() {
        bt_start = findViewById(R.id.bt_start);
        bt_stop = findViewById(R.id.bt_stop);
        bt_start.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
    }

    private String mNameRecodeStartTime;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_start) {
            //选择清晰度 标清，
            String dirPath = "Aquantum/com.uav.dji.quantum/ScreenRecord/";
            String fileName = mNameRecodeStartTime = System.currentTimeMillis() + "";
            startRecordBtn();
        } else if (v.getId() == R.id.bt_stop) {
            srHelper.stopRecord(new ScreenRecorderHelper.OnRecordStatusChangeListener() {
                @Override
                public void onChangeSuccess() {
                    //当停止成功，做界面变化
                    bt_start.setText("开始录制");
                    Toast.makeText(MainActivity.this, "录屏成功" + srHelper.getRecordFilePath(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChangeFailed() {
                    //不作处理

                }
            });
        }
    }


    /**
     * 开始录屏
     */
    private void startRecordBtn() {
        final Dialog dialog = new Dialog(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.recorde_audio_dialog, null);

        final RadioGroup resolutionGroup = (RadioGroup) view.findViewById(R.id.resolution_group);
        final RadioButton mResBDButton = (RadioButton) view.findViewById(R.id.record_radiobutton1);
        final RadioButton mResHDButton = (RadioButton) view.findViewById(R.id.record_radiobutton2);
        final RadioButton mResSPDButton = (RadioButton) view.findViewById(R.id.record_radiobutton3);
        recordWith = 1280;
        recordHeight = 720;
        resolutionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.record_radiobutton1:
                        recordWith = 960;
                        recordHeight = 540;
                        break;
                    case R.id.record_radiobutton2:
                        recordWith = 1280;
                        recordHeight = 720;
                        break;
                    case R.id.record_radiobutton3:
                        recordWith = deviceWidth;
                        recordHeight = deviceHeight;
                        break;


                }
            }
        });
        final CheckBox checkBoxMute = (CheckBox) view.findViewById(R.id.check_mute);
        mRecordBtn = (Button) view.findViewById(R.id.record_btn);

        if (srHelper.isRunning()) {
            mRecordBtn.setText("正在录制");

        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("");
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams windowParams = dialogWindow.getAttributes();
        windowParams.dimAmount = 0.0f;
        dialogWindow.setAttributes(windowParams);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int mWindowWidth = (int) (displayMetrics.widthPixels * 0.4);
        dialog.setContentView(view, new ViewGroup.LayoutParams(mWindowWidth,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT));
        dialog.show();

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (srHelper.isRunning()) {
                    return;
                }

                mNameRecodeStartTime = System.currentTimeMillis() + "";
                srHelper.startCustomRecord(MainActivity.this
                        , recordWith
                        , recordHeight
                        , "ScreenRecord/"
                        , mNameRecodeStartTime, checkBoxMute.isChecked());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        srHelper.onActivityResult(this, requestCode, resultCode, data, new ScreenRecorderHelper.OnRecordStatusChangeListener() {
            @Override
            public void onChangeSuccess() {
                //控制开始按钮的文字变化
                bt_start.setText("正在录制");
            }

            @Override
            public void onChangeFailed() {
                //如果录制失败，则不作任何变化
                bt_start.setText("开始录制");
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        srHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
