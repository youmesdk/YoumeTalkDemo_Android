package com.youme.android.rtcdemo;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


import com.youme.voiceengine.mgr.YouMeManager;
import com.youme.voiceengine.*;

public class MainActivity extends AppCompatActivity implements YouMeCallBackInterface,CompoundButton.OnCheckedChangeListener {

    public static final String action = "userList.broadcast.action";
    public static final String action2 = "status.broadcast.action";
    public static final String TAG = "YOUME_DEMO";


    private final int INIT_OK = 0;
    private final int INIT_NOK = 1;

    //记录当前模式,0 未在房间, 1 主播模式, 2  听主播模式, 3 自由通话模式 4.白名单模式  5.控制其他人麦克风扬声器
    private int mMode = 0;
    private final int ANCHOR_MODE = 1;
    private final int LISTEN_ANCHOR_MODE = 2;
    private final int NORMAL_ROOM_MODE = 3;
    private final int WHITE_USER_MODE = 4;
    private final int  CONTROL_OTHER_MIC_SPEAK= 5;

    private final int JOIN_FAIL = 100;

    private TextView mServerTag;
    private TextView mTipsTag;
    private EditText mRoomIDET;
    private EditText mUserIDET;
    private Switch mMobileSwitch;

    private String strTips = "请先初始化";

    private boolean mInit = false;
    private boolean mInRoom = false;
    private String mUserID = "user_1";
    private String mRoomID = "room_1";
    private String AppKey ="YOUME670584CA1F7BEF370EC7780417B89BFCC4ECBF78";
    private String AppSecret ="yYG7XY8BOVzPQed9T1/jlnWMhxKFmKZvWSFLxhBNe0nR4lbm5OUk3pTAevmxcBn1mXV9Z+gZ3B0Mv/MxZ4QIeDS4sDRRPzC+5OyjuUcSZdP8dLlnRV7bUUm29E2CrOUaALm9xQgK54biquqPuA0ZTszxHuEKI4nkyMtV9sNCNDMBAAE=";


    private Handler initHandler = new Handler(){
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          switch (msg.what){
              case INIT_OK:
                  mTipsTag.setText(strTips);
                  break;
              case INIT_NOK:
                  mTipsTag.setText(strTips);
                  break;
              default:
                  break;
          }
      }
    };
    /*跳转**/
    private Handler joinHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case ANCHOR_MODE:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, AnchorActivity.class);
                    intent.putExtra("userID",mUserID);
                    intent.putExtra("roomID",mRoomID);
                    startActivity(intent);
                    break;
                case LISTEN_ANCHOR_MODE:
                    Intent intent3 = new Intent();
                    intent3.setClass(MainActivity.this, ListenActivity.class);
                    intent3.putExtra("userID",mUserID);
                    intent3.putExtra("roomID",mRoomID);
                    startActivity(intent3);
                    break;
                case NORMAL_ROOM_MODE:
                    Intent intent4 = new Intent();
                    intent4.setClass(MainActivity.this,NormalActivity.class);
                    intent4.putExtra("userID",mUserID);
                    intent4.putExtra("roomID",mRoomID);
                    startActivity(intent4);
                    break;
                case WHITE_USER_MODE:
                    Intent intent5 = new Intent();
                    intent5.setClass(MainActivity.this,WhIteUserList.class);
                    intent5.putExtra("userID",mUserID);
                    intent5.putExtra("roomID",mRoomID);
                    startActivity(intent5);
                    break;
                case CONTROL_OTHER_MIC_SPEAK:
                    Intent intent6 = new Intent();
                    intent6.setClass(MainActivity.this,ControlOtherMicSpeak.class);
                    intent6.putExtra("userID",mUserID);
                    intent6.putExtra("roomID",mRoomID);
                    startActivity(intent6);
                    break;

                case JOIN_FAIL:
                    mTipsTag.setText(strTips);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler leaveHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case ANCHOR_MODE:
                    mTipsTag.setText("已成功退出主播模式");
                    break;
                case LISTEN_ANCHOR_MODE:
                    mTipsTag.setText("已成功退出听众模式");
                    break;
                case NORMAL_ROOM_MODE:
                    mTipsTag.setText("已成功退出普通房间");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        YouMeManager.Init(this);
        super.onCreate(savedInstanceState);
       // Intent intent = new Intent(this,VoiceEngineService.class);
       // startService(intent);
        //设置回调监听对象,需要implements YouMeCallBackInterface
        api.SetCallback(this);
        setContentView(R.layout.activity_main);

        mServerTag = (TextView)findViewById(R.id.serverTag);
        mServerTag.setText("Now:正服");

        //填入从游密申请到的AppKey和AppSecret（可在游密官网https://console.youme.im/user/register注册账号或者直接联系我方商务获取）
        //api.init(AppKey,AppSecret,0,"cn");


        mTipsTag = (TextView)findViewById(R.id.tipTag);
        mRoomIDET = (EditText)findViewById(R.id.roomID);
        mUserIDET = (EditText)findViewById(R.id.userID);
        //随机生成用户ID并显示,防止串号
        Random randam = new Random();
        int idTmp = randam.nextInt(10000);
        mUserID = "ym"+idTmp;
        mUserIDET.setText(mUserID);
        mMobileSwitch = (Switch)findViewById(R.id.mobileSwitch);
        mMobileSwitch.setOnCheckedChangeListener(this);

        //可以提前申请录音权限
        if(Build.VERSION.SDK_INT >= 23 && this.getApplicationInfo().targetSdkVersion >= 23) {
            int e = ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO");
            if(e != 0) {
                Log.e("AudioMgr", "Request for record permission");
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, 1);
            } else {
                Log.i("AudioMgr", "Already got record permission");
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.mobileSwitch:
                if (compoundButton.isChecked()) {
                    api.setUseMobileNetworkEnabled(true);
                } else {
                    api.setUseMobileNetworkEnabled(false);
                }
                //打印验证设置以及获取状态的接口
                strTips = (api.getUseMobileNetworkEnabled() == true ? "允许" : "禁止") + "使用移动网络";
                mTipsTag.setText(strTips);
                break;
            default:
                break;

        }
    }

    protected void onStart() {
        super.onStart();
        Log.i("TestActivity", "onStart");
    }

    void log(String logStr){
        Log.d(TAG,logStr);
    }

    @Override
    public void onEvent(int eventType, int errorCode, String channelID, Object param){
        log("OnEvent:event "+eventType + ",error " + errorCode + ",channel " + channelID + ",param_" + param.toString());
        Message msg = new Message();
        switch( eventType ){
            case 0: //YOUME_EVENT_INIT_OK:
                log("Talk 初始化成功");
                strTips = "初始化成功";
                mInit = true;
                msg.what = INIT_OK;
                initHandler.sendMessage(msg);
                break;
            case 1://YOUME_EVENT_INIT_FAILED:
                log("Talk 初始化失败");
                strTips = "初始化失败";
                mInit = false;
                msg.what = INIT_NOK;
                initHandler.sendMessage(msg);
                break;
            case 2://YOUME_EVENT_JOIN_OK:
                log("Talk 进入频道成功，频道："+channelID+" 用户id:"+param);
                msg.what = mMode;
                joinHandler.sendMessage(msg);
                break;
            case 3://YOUME_EVENT_JOIN_FAILED:
                log("Talk 进入频道:"+channelID+"失败,code:"+errorCode);
                strTips = "进入房间失败,error code:"+errorCode;
                msg.what = JOIN_FAIL;
                joinHandler.sendMessage(msg);
                mInRoom = false;
                break;
            case 4://YOUME_EVENT_LEAVED_ONE:
                log("Talk 离开单个频道:"+channelID);
                msg.what = mMode;
                leaveHandler.sendMessage(msg);
                mInRoom = false;
                break;
            case 5://YOUME_EVENT_LEAVED_ALL:
                log("Talk 离开所有频道，这个回调channel参数为空字符串");
                msg.what = mMode;
                leaveHandler.sendMessage(msg);
                mInRoom = false;
                break;
            case 6://YOUME_EVENT_PAUSED:
                log("Talk 暂停");
                break;
            case 7://YOUME_EVENT_RESUMED:
                log("Talk 恢复");
                break;
            case 8://YOUME_EVENT_SPEAK_SUCCESS:///< 切换对指定频道讲话成功（适用于多频道模式）
                break;
            case 9://YOUME_EVENT_SPEAK_FAILED:///< 切换对指定频道讲话失败（适用于多频道模式）
                break;
            case 10://YOUME_EVENT_RECONNECTING:///< 断网了，正在重连
                log("Talk 正在重连");
                break;
            case 11://YOUME_EVENT_RECONNECTED:///< 断网重连成功
                log("Talk 重连成功");
                break;
            case 12://YOUME_EVENT_REC_FAILED:///< 通知录音启动失败（此时不管麦克风mute状态如何，都没有声音输出）
                log("录音启动失败，code："+errorCode);
                break;
            case 13://YOUME_EVENT_BGM_STOPPED:///< 通知背景音乐播放结束
                log("背景音乐播放结束,path："+param);
                break;
            case 14://YOUME_EVENT_BGM_FAILED:///< 通知背景音乐播放失败
                log("背景音乐播放失败,code："+errorCode);
                break;
            case 16://YOUME_EVENT_OTHERS_MIC_ON:///< 其他用户麦克风打开
                log("其他用户麦克风打开,userid:"+param);
                break;
            case 17://YOUME_EVENT_OTHERS_MIC_OFF:///< 其他用户麦克风关闭
                log("其他用户麦克风关闭,userid:"+param);
                break;
            case 18://YOUME_EVENT_OTHERS_SPEAKER_ON:///< 其他用户扬声器打开
                log("其他用户扬声器打开,userid:"+param);
                break;
            case 19://YOUME_EVENT_OTHERS_SPEAKER_OFF: ///< 其他用户扬声器关闭
                log("其他用户扬声器关闭,userid:"+param);
                break;
            case 20://YOUME_EVENT_OTHERS_VOICE_ON: ///< 其他用户进入讲话状态
                log("开始讲话,userid:"+param);
                break;
            case 21://YOUME_EVENT_OTHERS_SPEAKER_OFF: ///< 其他用户停止讲话
                log("停止讲话userid:"+param);
                break;
            case 22://YOUME_EVENT_MY_MIC_LEVEL: ///< 自己的麦克风的语音音量级别
                log("我当前讲话的音量级别是,数值："+errorCode);
                break;
            case 23://YOUME_EVENT_MIC_CTR_ON: ///< 自己的麦克风被其他用户打开
                log("自己的麦克风被其他用户打开，userid："+param);
                break;
            case 24://YOUME_EVENT_MIC_CTR_OFF: ///< 自己的麦克风被其他用户关闭
                log("自己的麦克风被其他用户关闭，userid："+param);
                break;
            case 25://YOUME_EVENT_SPEAKER_CTR_ON: ///< 自己的扬声器被其他用户打开
                log("自己的扬声器被其他用户打开，userid："+param);
                break;
            case 26://YOUME_EVENT_SPEAKER_CTR_OFF: ///< 自己的扬声器被其他用户关闭
                log("自己的扬声器被其他用户关闭，userid："+param);
                break;
            case 27://YOUME_EVENT_LISTEN_OTHER_ON: ///< 取消屏蔽某人语音
                log("取消屏蔽某人语音，userid："+param);
                break;
            case 28://YOUME_EVENT_LISTEN_OTHER_OFF: ///< 屏蔽某人语音
                log("屏蔽某人语音,userid："+param);
                break;
            case 62://YOUME_EVENT_SET_WHITE_USER_LIST_OK: ///对指定频道设置白名单成功，但可能有异常用户
                log("对指定频道设置白名单成功");
                if (errorCode==-501){
                    log("设置白名单部分用户异常：已不在房间");
                }
                break;
            case 63://YOUME_EVENT_SET_WHITE_USER_LIST_FAILED: ///对指定频道设置白名单失败
                log("对指定频道设置白名单失败");
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestRestAPI(int requestID, int errorCode, String queryParam, String resultJsonStr){
        log("onRequestRestAPI requestID:"+requestID+" errorCode:"+errorCode+" queryParam"+queryParam);
        log(resultJsonStr);
    }

    @Override
    public void onMemberChange(String channelID, MemberChange[] changeList, boolean b) {
        log("OnMemberChange:"+channelID+" member count:"+changeList.length);
        for(int i = 0 ;i < changeList.length; i++) {
            MemberChange obj = changeList[i];
            log("userid:"+obj.userID+" isJoin:"+obj.isJoin);
        }
    }



    @Override
    public void onBroadcast(int YouMeBroadcastType, String channelID, String param1, String param2, String content){
        //连麦抢麦通知事件
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
    /*初始化按钮**/
    public void onInitClick(View v) {
        //填入从游密申请到的AppKey和AppSecret（可在游密官网https://console.youme.im/user/register注册账号或者直接联系我方商务获取）
        api.init(AppKey,AppSecret,0,"cn");
    }
    /*反初始化按钮**/
    public void onUnInitClick(View v){
       if(0 == api.unInit()){
           mTipsTag.setText("反初始化成功");
           mInit = false;
       }else {
           mTipsTag.setText("反初始化失败");
           //mInit保持原值不变
       }
    }
    /*主播模式按钮执行的逻辑**/
    public void onAnchorClick(View v){
//        if(!mInit) {
//            //还未成功初始化
//            mTipsTag.setText("还未成功初始化,不能进入房间");
//            return;
//        }else{
//            mMode = ANCHOR_MODE;
//            mRoomID = mRoomIDET.getText().toString();
//            mUserID = mUserIDET.getText().toString();
//            //bool needUserList, bool needMic, bool needSpeaker, bool autoSendStatus
//            api.joinConference2(mUserID, mRoomID, false, true, true, false);
//            mTipsTag.setText("正在进入主播模式......");
//        }

        if(!mInit) {
            //还未成功初始化
            mTipsTag.setText("还未成功初始化,不能进入房间");
            return;
        }else if(mInRoom) {
            mTipsTag.setText("暂时不能进入新房间");
            return;
        } else {
            mInRoom = true;
            mMode = ANCHOR_MODE;
            mRoomID = mRoomIDET.getText().toString();
            mUserID = mUserIDET.getText().toString();
            /**
             * 第三个参数，角色身份说明
             YOUME_USER_NONE = 0,         ///< 非法用户，调用API时不能传此参数
             YOUME_USER_TALKER_FREE = 1,      ///< 自由讲话者，适用于小组通话（建议小组成员数最多10个），每个人都可以随时讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_TALKER_ON_DEMAND = 2, ///< 需要通过抢麦等请求麦克风权限之后才可以讲话，适用于较大的组或工会等（比如几十个人），同一个时刻只能有一个或几个人能讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_LISTENER = 3,         ///< 听众，主播/指挥/嘉宾的听众，同一个时刻只能在一个语音频道里面，只听不讲
             YOUME_USER_COMMANDER = 4,        ///< 指挥，国家/帮派等的指挥官，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_HOST = 5,             ///< 主播，广播型语音频道的主持人，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_GUSET = 6,            ///< 嘉宾，主播或指挥邀请的连麦嘉宾，同一个时刻只能在一个语音频道里面， 可以随时讲话
             */
            api.joinChannelSingleMode(mUserID,mRoomID,5);
            mTipsTag.setText("正在进入主播模式......");
        }

    }

    /*听众模式**/
    public void onListenAnchorClick(View v){
//        if(!mInit) {
//            //还未成功初始化
//            mTipsTag.setText("还未成功初始化,不能进入房间");
//            return;
//        }else{
//            mMode = LISTEN_ANCHOR_MODE;
//            mRoomID = mRoomIDET.getText().toString();
//            mUserID = mUserIDET.getText().toString();
//            //bool needUserList, bool needMic, bool needSpeaker, bool autoSendStatus
//            api.joinConference2(mUserID, mRoomID, false, false, true, false);
//            mTipsTag.setText("正在进入听主播模式......");
//        }

        if(!mInit) {
            //还未成功初始化
            mTipsTag.setText("还未成功初始化,不能进入房间");
            return;
        }else if(mInRoom){
             mTipsTag.setText("暂时不能进入新房间");
             return;
        }else{
            mInRoom = true;
            mMode = LISTEN_ANCHOR_MODE;
            mRoomID = mRoomIDET.getText().toString();
            mUserID = mUserIDET.getText().toString();
            /**
             * 第三个参数，角色身份说明
             YOUME_USER_NONE = 0,         ///< 非法用户，调用API时不能传此参数
             YOUME_USER_TALKER_FREE = 1,      ///< 自由讲话者，适用于小组通话（建议小组成员数最多10个），每个人都可以随时讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_TALKER_ON_DEMAND = 2, ///< 需要通过抢麦等请求麦克风权限之后才可以讲话，适用于较大的组或工会等（比如几十个人），同一个时刻只能有一个或几个人能讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_LISTENER = 3,         ///< 听众，主播/指挥/嘉宾的听众，同一个时刻只能在一个语音频道里面，只听不讲
             YOUME_USER_COMMANDER = 4,        ///< 指挥，国家/帮派等的指挥官，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_HOST = 5,             ///< 主播，广播型语音频道的主持人，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_GUSET = 6,            ///< 嘉宾，主播或指挥邀请的连麦嘉宾，同一个时刻只能在一个语音频道里面， 可以随时讲话
             */
            api.joinChannelSingleMode(mUserID,mRoomID,3);
            mTipsTag.setText("正在进入听主播模式......");
        }

    }

    /*狼人杀模式**/
    public  void onsetWhiteUserClick(View view) {

        if (!mInit) {
            //还未成功初始化
            mTipsTag.setText("还未成功初始化,不能进入房间");
            return;
        } else if (mInRoom) {
            mTipsTag.setText("暂时不能进入新房间");
            return;
        } else {
            mInRoom = true;
            mMode = WHITE_USER_MODE;
            mRoomID = mRoomIDET.getText().toString();
            mUserID = mUserIDET.getText().toString();
            mTipsTag.setText("正在进入白名单模式......");


        }


        //已自由名的身份进入频道
        api.joinChannelSingleMode(mUserID,mRoomID,1);
    }





    /*自由通话**/
    public void onNormalClick(View v){

        if(!mInit) {
            //还未成功初始化
            mTipsTag.setText("还未成功初始化,不能进入房间");
            return;
        }else if(mInRoom){
            mTipsTag.setText("暂时不能进入新房间");
            return;
        }else{
            mInRoom = true;
            mMode = NORMAL_ROOM_MODE;
            mRoomID = mRoomIDET.getText().toString();
            mUserID = mUserIDET.getText().toString();
            mTipsTag.setText("正在进入普通房间模式......");
            /**
             * 第三个参数，角色身份说明
             YOUME_USER_NONE = 0,         ///< 非法用户，调用API时不能传此参数
             YOUME_USER_TALKER_FREE = 1,      ///< 自由讲话者，适用于小组通话（建议小组成员数最多10个），每个人都可以随时讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_TALKER_ON_DEMAND = 2, ///< 需要通过抢麦等请求麦克风权限之后才可以讲话，适用于较大的组或工会等（比如几十个人），同一个时刻只能有一个或几个人能讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_LISTENER = 3,         ///< 听众，主播/指挥/嘉宾的听众，同一个时刻只能在一个语音频道里面，只听不讲
             YOUME_USER_COMMANDER = 4,        ///< 指挥，国家/帮派等的指挥官，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_HOST = 5,             ///< 主播，广播型语音频道的主持人，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_GUSET = 6,            ///< 嘉宾，主播或指挥邀请的连麦嘉宾，同一个时刻只能在一个语音频道里面， 可以随时讲话
             */
            api.joinChannelSingleMode(mUserID,mRoomID,1);
        }

    }




    /*控制他人模式**/
    public void onControlOtherMicSpeak(View v){

        if(!mInit) {
            //还未成功初始化
            mTipsTag.setText("还未成功初始化,不能进入房间");
            return;
        }else if(mInRoom){
            mTipsTag.setText("暂时不能进入新房间");
            return;
        }else{
            mInRoom = true;
            mMode = CONTROL_OTHER_MIC_SPEAK;
            mRoomID = mRoomIDET.getText().toString();
            mUserID = mUserIDET.getText().toString();
            mTipsTag.setText("正在进入普通房间模式......");
            /**
             * 第三个参数，角色身份说明
             YOUME_USER_NONE = 0,         ///< 非法用户，调用API时不能传此参数
             YOUME_USER_TALKER_FREE = 1,      ///< 自由讲话者，适用于小组通话（建议小组成员数最多10个），每个人都可以随时讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_TALKER_ON_DEMAND = 2, ///< 需要通过抢麦等请求麦克风权限之后才可以讲话，适用于较大的组或工会等（比如几十个人），同一个时刻只能有一个或几个人能讲话, 同一个时刻只能在一个语音频道里面
             YOUME_USER_LISTENER = 3,         ///< 听众，主播/指挥/嘉宾的听众，同一个时刻只能在一个语音频道里面，只听不讲
             YOUME_USER_COMMANDER = 4,        ///< 指挥，国家/帮派等的指挥官，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_HOST = 5,             ///< 主播，广播型语音频道的主持人，同一个时刻只能在一个语音频道里面，可以随时讲话，可以播放背景音乐，戴耳机情况下可以监听自己语音
             YOUME_USER_GUSET = 6,            ///< 嘉宾，主播或指挥邀请的连麦嘉宾，同一个时刻只能在一个语音频道里面， 可以随时讲话
             */
            api.joinChannelSingleMode(mUserID,mRoomID,1);
        }

    }



}
