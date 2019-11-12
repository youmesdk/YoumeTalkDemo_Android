package com.youme.android.rtcdemo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.youme.voiceengine.*;


public class AnchorActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private TextView mTitleTV;
    private TextView mTipsTV;

    private SeekBar mMicSB;
    private TextView mVolumeTV;

    private SeekBar mBgmSB;
    private TextView mBgmVolumeTV;

    private SeekBar mDelaySB;
    private TextView mDelayTV;

    private Switch mMicSwitch;
    private Switch mBgmSwitch;
    private Switch mSpeakerSwitch;
    private Switch mPauseSwitch;

    private String mRoomID;
    private String mUserID;
    private int mVolume = 100;
    private int mBgmVolume = 100;
    private int mDelay = 440;
    private String strTips;


    private void initComponent(){
        mTitleTV = (TextView)findViewById(R.id.titleTag1);
        String strTmp = api.getSdkInfo();
        String strTitle = mUserID+"进入主播模式"+mRoomID+strTmp;
        mTitleTV.setText(strTitle);
        mTipsTV = (TextView)findViewById(R.id.tipsTag1);


        mMicSwitch = (Switch)findViewById(R.id.micSwitch1);
        mBgmSwitch = (Switch)findViewById(R.id.bgmSwitch1);
        mSpeakerSwitch = (Switch)findViewById(R.id.speakerSwitch1);
        mPauseSwitch = (Switch)findViewById(R.id.pauseSwitch1);

        mMicSwitch.setOnCheckedChangeListener(this);
        mBgmSwitch.setOnCheckedChangeListener(this);
        mSpeakerSwitch.setOnCheckedChangeListener(this);
        mPauseSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor);
        mRoomID = this.getIntent().getStringExtra("roomID");
        mUserID = this.getIntent().getStringExtra("userID");
        initComponent();
        seekbarView();
        bgmSeekbarView();
        delaySeekbarView();

        //主播模式状态初始化
        //监听关闭
        api.setHeadsetMonitorOn(false);
        //麦克风开启
        api.setMicrophoneMute(false);
        //音量设为100
        api.setVolume(100);
        //背景音量设为100
        api.setBackgroundMusicVolume(100);
        //背景音乐关闭(不确定此步是否多余)
        api.stopBackgroundMusic();
        //扬声器开启
        api.setSpeakerMute(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.monitorSwitch1:
                // 已经废弃
                if(compoundButton.isChecked()){
//                    api.setMicBypassToSpeaker(true);
                } else {
//                    api.setMicBypassToSpeaker(false);
                }
                break;
            case R.id.micSwitch1:
                if(compoundButton.isChecked()){
                    api.setMicrophoneMute(false);
                } else {
                    api.setMicrophoneMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips =(api.getMicrophoneMute() ?"已关闭":"已打开") + "麦克风";
                mTipsTV.setText(strTips);
                break;
            case R.id.bgmSwitch1:
                if(compoundButton.isChecked()){
                    api.playBackgroundMusic("/sdcard/backmusic/test.mp3", true);
                } else {
                    api.stopBackgroundMusic();
                }
                break;
            case R.id.speakerSwitch1:
                if(compoundButton.isChecked()){
                    api.setSpeakerMute(false);
                } else {
                    api.setSpeakerMute(true);
                }
                //打印验证设置以及获取状态的接口
                strTips =(api.getSpeakerMute()==true?"已关闭":"已打开") + "扬声器";
                mTipsTV.setText(strTips);
                break;
            case R.id.pauseSwitch1:
                if(compoundButton.isChecked()){
                    api.pauseChannel();
                    strTips = "pause 通话";
                    mTipsTV.setText(strTips);
                } else {
                    api.resumeChannel();
                    strTips = "resume 通话";
                    mTipsTV.setText(strTips);
                }
                break;
            default:
                break;

        }
    }

    // 监听Back键按下事件,退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            System.exit(0);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public void onLeaveRoom(View v)
    {
        api.leaveChannelAll();
        AnchorActivity.this.finish();
    }

    private void seekbarView(){
        mMicSB = (SeekBar) findViewById(R.id.micSeekBar1);
        mVolumeTV = (TextView) findViewById(R.id.volumeTag1);

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

    private void bgmSeekbarView(){
        mBgmSB = (SeekBar) findViewById(R.id.bgmSeekBar1);
        mBgmVolumeTV = (TextView) findViewById(R.id.bgmVolumeTag1);

        mBgmSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBgmVolume = progress;
                mBgmVolumeTV.setText(""+mBgmVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                api.setBackgroundMusicVolume( mBgmVolume);

            }
        });
    }

    private void delaySeekbarView(){
        mDelaySB = (SeekBar) findViewById(R.id.delaySeekBar1);
        mDelayTV = (TextView) findViewById(R.id.delayTag1);

        mDelaySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //UI上设计拖动条25个单位,一个单位代表20ms
                mDelay = progress*20;
                mDelayTV.setText(mDelay+"ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 废弃，改为使用后台配置
                //api.setBackgroundMusicDelay(mDelay);
            }
        });
    }

}
