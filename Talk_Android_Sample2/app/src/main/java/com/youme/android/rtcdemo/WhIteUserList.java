package com.youme.android.rtcdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.youme.voiceengine.api;
/*
* 白名单模式
* **/
public class WhIteUserList extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = "YOUME";


    private TextView mTitleTV;
    private TextView mTipsTV;
    private EditText editText;
    private SeekBar mMicSB;
    private TextView mVolumeTV;


    private Switch mSpeakerSwitch;
    private Switch mMicSwitch;
    private Switch mCspeakerSwitch;
    private Switch mCmicSwitch;
    private Switch mCavoidSwitch;
    private Switch mPauseSwitch;

    private String mRoomID;
    private String mUserID;
    private int mVolume = 100;
    private String strTips;

    private Spinner spinner = null;
    private ArrayAdapter<String> adapter = null;
    private String mCuserID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_user);
        mRoomID = this.getIntent().getStringExtra("roomID");
        mUserID = this.getIntent().getStringExtra("userID");
        IntentFilter filter = new IntentFilter(MainActivity.action);
        registerReceiver(broadcastReceiver, filter);

        IntentFilter filter2 = new IntentFilter(MainActivity.action2);
        registerReceiver(broadcastReceiver2, filter2);

        initComponent();
        seekbarView();

        //普通房间模式状态初始化
        //扬声器打开
        api.setSpeakerMute(false);
        //麦克风打开
        api.setMicrophoneMute(false);
        //音量初始化为100
        api.setVolume(100);

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String [] userList = intent.getExtras().getStringArray("userList");

            adapter = new ArrayAdapter<String>(WhIteUserList.this,android.R.layout.simple_spinner_item,userList);
            //设置下拉列表风格
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将适配器添加到spinner中去
            spinner.setAdapter(adapter);
        }
    };

    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String strTmp = intent.getExtras().getString("status");
            mTipsTV.setText(strTmp);
        }
    };



    /*初始化组建**/
    private void initComponent(){
        mTitleTV = (TextView)findViewById(R.id.titleTag3);
        String strTmp = api.getSdkInfo();
        String strTitle = mUserID+"进入普通房间"+mRoomID+strTmp;
        mTitleTV.setText(strTitle);
        mTipsTV = (TextView)findViewById(R.id.tipsTag3);


        mSpeakerSwitch = (Switch)findViewById(R.id.speakerSwitch31);
        mMicSwitch = (Switch)findViewById(R.id.micSwitch31);
        mPauseSwitch = (Switch)findViewById(R.id.pauseSwitch3);
        //输入的白名单
        editText=(EditText)findViewById(R.id.EditText);

        mSpeakerSwitch.setOnCheckedChangeListener(this);
        mMicSwitch.setOnCheckedChangeListener(this);



    }



/*调节音量**/
    private void seekbarView(){
        mMicSB = (SeekBar) findViewById(R.id.micSeekBar31);
        mVolumeTV = (TextView) findViewById(R.id.volumeTag3);

        mMicSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVolume = progress;
                mVolumeTV.setText(""+mVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                api.setVolume(mVolume);
                //打印验证设置以及获取状态的接口
                int volumeTmp = api.getVolume();
                mTipsTV.setText("当前音量为"+volumeTmp);

            }
        });
    }

    /*点击了提交白名单**/
    public  void onClickWhiteList(View view){

        String tempUserID = editText.getText().toString();
        if ( tempUserID == null || tempUserID.isEmpty())
        {

            //提示控制用户ID不能为空!!!!!!!!!!!
            mTipsTV.setText("白名单用户选择请不要为空");
            return;
        }
        int error=   api.setWhiteUserList(mRoomID,tempUserID);
        if (error==0){
            mTipsTV.setText("白名单用户:"+tempUserID+"设置同步码成功，是否成功请查看OnEvent里面的62以及63");
        }else{
            mTipsTV.setText("白名单用户设置同步码失败，error:"+error);
        }






    }


    public void onLeaveRoom(View v)
    {
        api.leaveChannelAll();
        WhIteUserList.this.finish();
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
