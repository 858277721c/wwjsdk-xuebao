package com.fanwe.lib.wwjsdk.xuebao;

import android.text.TextUtils;

import com.fanwe.lib.wwjsdk.sdk.IWWControlSDK;
import com.fanwe.lib.wwjsdk.sdk.callback.WWControlSDKCallback;
import com.fanwe.lib.wwjsdk.sdk.proxy.IWWControlSDKProxy;
import com.fanwe.lib.wwjsdk.sdk.request.WWInitParam;
import com.fanwe.lib.wwjsdk.utils.WWJsonUtil;

/**
 * 娃娃机控制sdk，对外控制接口
 */
public final class WWControlSDKProxy implements IWWControlSDKProxy
{
    private IWWControlSDK mControlSDK = new XueBaoWWControlSDK();
    private String mJsonMove;

    private String getJsonMove()
    {
        if (TextUtils.isEmpty(mJsonMove))
        {
            XueBaoWWMoveParam param = new XueBaoWWMoveParam();
            param.moveDuration = 5000;

            mJsonMove = WWJsonUtil.objectToJson(param);
        }
        return mJsonMove;
    }

    @Override
    public void init(int keepCatch)
    {
        WWInitParam param = new WWInitParam();
        if (keepCatch == 1)
        {
            param.keepCatch = 1;
        } else
        {
            param.keepCatch = 0;
        }

        String jsonString = WWJsonUtil.objectToJson(param);
        mControlSDK.init(jsonString);
    }

    @Override
    public void setCallback(WWControlSDKCallback callback)
    {
        mControlSDK.setCallback(callback);
    }

    @Override
    public boolean begin()
    {
        return mControlSDK.begin(null);
    }

    //---------- move start ----------

    @Override
    public boolean moveFront()
    {
        return mControlSDK.moveBack(getJsonMove());
    }

    @Override
    public boolean moveBack()
    {
        return mControlSDK.moveFront(getJsonMove());
    }

    @Override
    public boolean moveLeft()
    {
        return mControlSDK.moveLeft(getJsonMove());
    }

    @Override
    public boolean moveRight()
    {
        return mControlSDK.moveRight(getJsonMove());
    }

    //---------- move end ----------

    @Override
    public boolean stopMove()
    {
        return mControlSDK.stopMove(null);
    }

    @Override
    public boolean doCatch()
    {
        return mControlSDK.doCatch(null);
    }

    @Override
    public boolean check()
    {
        return mControlSDK.check(null);
    }
}
