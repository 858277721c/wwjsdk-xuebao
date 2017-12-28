package com.fanwe.lib.wwjsdk.xuebao;

import android.util.Log;

import com.fanwe.lib.serialport.FInputStreamReadThread;
import com.fanwe.lib.wwjsdk.log.WWLogger;
import com.fanwe.lib.wwjsdk.sdk.constants.WWCatchResult;
import com.fanwe.lib.wwjsdk.sdk.constants.WWState;
import com.fanwe.lib.wwjsdk.sdk.response.WWCatchResultData;
import com.fanwe.lib.wwjsdk.sdk.response.WWCheckResultData;
import com.fanwe.lib.wwjsdk.sdk.response.WWHeartBeatData;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPort;
import com.fanwe.lib.wwjsdk.utils.WWUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 * 雪暴娃娃机串口通信
 */
public class XueBaoWWSerialPort extends WWSerialPort
{
    /**
     * 娃娃机数据开始标识
     */
    private static final byte DATA_HEAD = (byte) 0xfe;
    /**
     * 标识本条消息的数据长度值的位置
     */
    private static final int DATA_LENGTH_INDEX = 6;
    /**
     * 娃娃机抓取的结果数据
     */
    private static final int DATA_CATCH_RESULT = 0x33;
    /**
     * 检查娃娃机的结果数据
     */
    private static final int DATA_CHECK_RESULT = 0x34;
    /**
     * 心跳的结果数据
     */
    private static final int DATA_HEART_BEAT = 0x35;

    private List<Byte> mListData = new ArrayList<>();
    private int mCurrentIndex;
    private int mDataEndIndex;

    private void reset()
    {
        mCurrentIndex = 0;
        mDataEndIndex = -1;
        mListData.clear();
    }

    private void saveData(int data)
    {
        mListData.add((byte) data);
        final int size = mListData.size();

        if (size == DATA_LENGTH_INDEX + 1)
        {
            // 找到本条消息数据结束的位置
            mDataEndIndex = data - 1;
        }
    }

    @Override
    protected FInputStreamReadThread.ReadConfig provideReadConfig()
    {
        FInputStreamReadThread.ReadConfig config = new FInputStreamReadThread.ReadConfig();
        config.buffer = new byte[1];
        return config;
    }

    @Override
    protected void onReadData(byte[] data, int readSize)
    {
        if (data.length != 1)
        {
            throw new IllegalArgumentException(XueBaoWWSerialPort.class.getSimpleName() + " data.length must be 1");
        }

        if (data[0] == DATA_HEAD)
        {
            // 消息头
            reset();
            saveData(data[0]);
            mCurrentIndex++;
        } else
        {
            if (!mListData.isEmpty())
            {
                saveData(data[0]);
                if (mCurrentIndex == mDataEndIndex)
                {
                    onReadWWData(mListData);
                    reset();
                }
                mCurrentIndex++;
            }
        }
    }

    /**
     * 读取到娃娃机数据
     *
     * @param listData
     */
    private void onReadWWData(List<Byte> listData)
    {
        final byte[] data = WWUtils.listToArray(listData);
        Log.e(XueBaoWWSerialPort.class.getSimpleName(), "onDataReceived:" + WWUtils.byte2HexString(data, data.length));
        filterData(data);
    }

    /**
     * 过滤数据
     *
     * @param data
     */
    private void filterData(byte[] data)
    {
        if (XueBaoWWSerialPortDataBuilder.checkData(data))
        {
            Log.i(XueBaoWWSerialPort.class.getSimpleName(), "filter data success:" + WWUtils.byte2HexString(data, data.length));

            final int type = data[7];
            switch (type)
            {
                case DATA_CATCH_RESULT:
                    WWCatchResultData catchResultData = new WWCatchResultData();
                    catchResultData.dataOriginal = data;

                    final int result = data[8];
                    if (result == 1)
                    {
                        catchResultData.result = WWCatchResult.SUCCESS;
                    } else
                    {
                        catchResultData.result = WWCatchResult.FAIL;
                    }

                    WWLogger.get().log(Level.WARNING, "receive data (catch result): " + catchResultData.result);
                    getCallback().onDataCatchResult(catchResultData);
                    break;
                case DATA_CHECK_RESULT:
                    WWCheckResultData checkResultData = new WWCheckResultData();
                    checkResultData.dataOriginal = data;

                    final int state = data[8];
                    switch (state)
                    {
                        case 0:
                            checkResultData.state = WWState.IDLE;
                            checkResultData.stateDesc = "idle";
                            break;
                        case 1:
                        case 2:
                            checkResultData.state = WWState.OPERATING;
                            checkResultData.stateDesc = "operating";
                            break;
                        case 101:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "上下电机故障或者天车未接或者上升微动故障";
                            break;
                        case 103:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "左右移动电机故障";
                            break;
                        case 104:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "前后移动电机故障或者后移微动故障";
                            break;
                        case 105:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "下降微动损坏或者上下电机故障";
                            break;
                        case 106:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "上升微动故障";
                            break;
                        case 108:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "前后移动电机故障或者前移";
                            break;
                        case 109:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "检测礼品的光眼堵住了";
                            break;
                        default:
                            checkResultData.state = WWState.ERROR;
                            checkResultData.stateDesc = "unknow error";
                            break;
                    }

                    WWLogger.get().log(Level.INFO, "receive data (check result): " + checkResultData.state + " " + checkResultData.stateDesc);
                    getCallback().onDataCheckResult(checkResultData);
                    break;
                case DATA_HEART_BEAT:
                    WWHeartBeatData heartBeatData = new WWHeartBeatData();
                    heartBeatData.dataOriginal = data;

                    heartBeatData.mac = getMacAddress();

                    WWLogger.get().log(Level.INFO, "receive data (heart beat): " + heartBeatData.mac);
                    getCallback().onDataHeartBeat(heartBeatData);
                    break;
                default:
                    break;
            }
        } else
        {
            Log.e(XueBaoWWSerialPort.class.getSimpleName(), "filter data error:" + WWUtils.byte2HexString(data, data.length));
        }
    }

    public static String getMacAddress()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface item = interfaces.nextElement();

                byte[] arrAddress = item.getHardwareAddress();
                if (arrAddress == null || arrAddress.length <= 0)
                {
                    continue;
                } else
                {
                    StringBuilder sb = new StringBuilder();
                    for (byte address : arrAddress)
                    {
                        sb.append(address).append(":");
                    }
                    if (sb.length() > 0)
                    {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                }
            }
        } catch (SocketException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
