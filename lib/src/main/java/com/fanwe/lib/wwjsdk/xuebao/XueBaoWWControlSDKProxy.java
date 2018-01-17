package com.fanwe.lib.wwjsdk.xuebao;

import android.text.TextUtils;

import com.fanwe.lib.wwjsdk.sdk.proxy.WWControlSDKProxy;
import com.fanwe.lib.wwjsdk.utils.WWJsonUtil;

/**
 * 娃娃机控制sdk，对外控制接口
 */
public final class XueBaoWWControlSDKProxy extends WWControlSDKProxy
{
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
    public void moveFront()
    {
        getControlSDK().moveBack(getJsonMove());
    }

    @Override
    public void moveBack()
    {
        getControlSDK().moveFront(getJsonMove());
    }

    @Override
    public void moveLeft()
    {
        getControlSDK().moveLeft(getJsonMove());
    }

    @Override
    public void moveRight()
    {
        getControlSDK().moveRight(getJsonMove());
    }
}
