package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.sdk.request.WWControlParam;

/**
 * 控制娃娃机移动的参数
 */
public class XueBaoWWMoveParam extends WWControlParam
{
    /**
     * 单次调用接口后爪子移动的时长(单位毫秒)，默认300毫秒
     */
    public long moveDuration = 300;
}
