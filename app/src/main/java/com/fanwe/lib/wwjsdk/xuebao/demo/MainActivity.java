package com.fanwe.lib.wwjsdk.xuebao.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.fanwe.lib.wwjsdk.sdk.callback.WWControlSDKCallback;
import com.fanwe.lib.wwjsdk.sdk.constants.WWCatchResult;
import com.fanwe.lib.wwjsdk.sdk.constants.WWState;
import com.fanwe.lib.wwjsdk.sdk.proxy.IWWControlSDKProxy;
import com.fanwe.lib.wwjsdk.sdk.response.WWCatchResultData;
import com.fanwe.lib.wwjsdk.sdk.response.WWCheckResultData;
import com.fanwe.lib.wwjsdk.sdk.response.WWHeartBeatData;
import com.fanwe.lib.wwjsdk.xuebao.WWControlSDKProxy;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btn_check, btn_begin, btn_front, btn_back, btn_left, btn_right, btn_catch;

    private IWWControlSDKProxy mControlSDK = new WWControlSDKProxy(); // 创建sdk对象

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_check = findViewById(R.id.btn_check);
        btn_begin = findViewById(R.id.btn_begin);
        btn_front = findViewById(R.id.btn_front);
        btn_back = findViewById(R.id.btn_back);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        btn_catch = findViewById(R.id.btn_catch);

        btn_front.setOnTouchListener(mOnTouchListenerMove);
        btn_back.setOnTouchListener(mOnTouchListenerMove);
        btn_left.setOnTouchListener(mOnTouchListenerMove);
        btn_right.setOnTouchListener(mOnTouchListenerMove);

        btn_begin.setOnClickListener(mOnClickListener);
        btn_catch.setOnClickListener(mOnClickListener);
        btn_check.setOnClickListener(mOnClickListener);

        mControlSDK.setCallback(mCallback); // 设置回调监听
    }

    /**
     * sdk回调监听
     */
    private WWControlSDKCallback mCallback = new WWControlSDKCallback()
    {
        @Override
        public void onDataCatchResult(WWCatchResultData data)
        {
            // 抓取结果通知
            int result = data.result;
            if (WWCatchResult.SUCCESS == result)
            {
                Log.i(TAG, "成功抓到娃娃");
            } else
            {
                Log.i(TAG, "很遗憾，没有抓到娃娃");
            }
        }

        @Override
        public void onDataCheckResult(WWCheckResultData data)
        {
            // 检测娃娃机的状态结果通知
            int state = data.state;
            String stateDesc = data.stateDesc; // 状态描述
            if (WWState.IDLE == state)
            {
                Log.i(TAG, "娃娃机处于空闲 " + stateDesc);
            } else if (WWState.OPERATING == state)
            {
                Log.i(TAG, "娃娃机处于操作中 " + stateDesc);
            } else if (WWState.ERROR == state)
            {
                Log.i(TAG, "娃娃机故障 " + stateDesc);
            }
        }

        @Override
        public void onDataHeartBeat(WWHeartBeatData data)
        {
            // 娃娃机心跳通知
            String mac = data.mac; // 娃娃机mac地址
            Log.i(TAG, "收到娃娃机心跳 " + mac);
        }
    };

    private View.OnTouchListener mOnTouchListenerMove = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if (v == btn_front)
                    {
                        mControlSDK.moveFront(); // 向前移动
                    } else if (v == btn_back)
                    {
                        mControlSDK.moveBack(); // 向后移动
                    } else if (v == btn_left)
                    {
                        mControlSDK.moveLeft(); // 向左移动
                    } else if (v == btn_right)
                    {
                        mControlSDK.moveRight(); // 向右移动
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mControlSDK.stopMove(); // 停止移动爪子
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == btn_begin)
            {
                int keepCatch = 0; // 设置本局游戏下爪后，是否保持足够的爪力把娃娃抓起 1-保持爪力，0-不保持
                mControlSDK.init(keepCatch);

                mControlSDK.begin(); // 开始
            } else if (v == btn_catch)
            {
                mControlSDK.doCatch(); // 下爪
            } else if (v == btn_check)
            {
                mControlSDK.check(); // 检测娃娃机状态，异步操作，会回调WWControlSDKCallback里面的检测结果通知
            }
        }
    };
}
