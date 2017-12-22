package com.fanwe.lib.wwjsdk.xuebao;

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

    @Override
    public boolean moveFront()
    {
        return mControlSDK.moveFront(null);
    }

    @Override
    public boolean moveBack()
    {
        return mControlSDK.moveBack(null);
    }

    @Override
    public boolean moveLeft()
    {
        return mControlSDK.moveLeft(null);
    }

    @Override
    public boolean moveRight()
    {
        return mControlSDK.moveRight(null);
    }

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
