package com.fanwe.lib.wwjsdk.xuebao;


import com.fanwe.lib.wwjsdk.sdk.request.WWControlParam;

/**
 * 娃娃机开始参数
 */
public class XueBaoWWBeginParam extends WWControlParam
{
    /**
     * 设置本局游戏超时时间(单位秒)，超时后会自动下爪，默认60秒
     */
    public int timeout = 60;
    /**
     * 设置本局游戏下爪后，是否保持足够的爪力把娃娃抓起(1-保持爪力，如果为1的话，其他控制爪力的参数无效；0-不保持)，默认不保持
     */
    public int keepCatch = 0;
    /**
     * 抓起爪力(1—48)
     */
    public int clawForceStart = 0;
    /**
     * 到顶爪力(1—48)
     */
    public int clawForceTop = 0;
    /**
     * 移动爪力(1—48)
     */
    public int clawForceMove = 0;
    /**
     * 大爪力(1—48)
     */
    public int clawForceBig = 0;
    /**
     * 抓起高度(0--10)底部到顶部分成10份，爪子到达某个高度就会抓力变小
     */
    public int grabHeight = 0;
    /**
     * 下线长度(10—35)爪子线放到最长的时间
     */
    public int clawDownTime = 0;
    /**
     * 前后电机的速度(1-5)
     */
    public int speedFrontBack = 0;
    /**
     * 左右电机的速度(1-5)
     */
    public int speedLeftRight = 0;
    /**
     * 上下电机的速度(1-5)
     */
    public int speedUpDown = 0;
}

