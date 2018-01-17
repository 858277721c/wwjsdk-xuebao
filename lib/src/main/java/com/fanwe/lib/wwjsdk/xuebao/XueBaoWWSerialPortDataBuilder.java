package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.log.WWLogger;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPortDataBuilder;
import com.fanwe.lib.wwjsdk.utils.WWJsonUtil;
import com.fanwe.lib.wwjsdk.utils.WWUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * 雪暴娃娃机数据转换实现类
 */
public class XueBaoWWSerialPortDataBuilder extends WWSerialPortDataBuilder
{
    public static final int DATA_LENGTH_INDEX = 6;

    @Override
    public byte[] buildBegin(String jsonString)
    {
        XueBaoWWBeginParam param = WWJsonUtil.jsonToObject(jsonString, XueBaoWWBeginParam.class);
        if (param == null)
        {
            param = new XueBaoWWBeginParam();
            param.timeout = getInitParam().timeout;
            param.keepCatch = getInitParam().keepCatch;
        }

        List<Integer> list = buildStart(0);
        // 命令
        list.add(0x31);

        list.add(param.timeout);
        list.add(param.keepCatch);
        list.add(param.clawForceStart);
        list.add(param.clawForceTop);
        list.add(param.clawForceMove);
        list.add(param.clawForceBig);
        list.add(param.grabHeight);
        list.add(param.clawDownTime);
        list.add(param.speedFrontBack);
        list.add(param.speedLeftRight);
        list.add(param.speedUpDown);

        return buildResult(list);
    }

    @Override
    public byte[] buildMove(String jsonString, Direction direction)
    {
        XueBaoWWMoveParam param = WWJsonUtil.jsonToObject(jsonString, XueBaoWWMoveParam.class);
        if (param == null)
        {
            param = new XueBaoWWMoveParam();
        }

        List<Integer> list = buildStart(0);

        // 命令
        list.add(0x32);

        switch (direction)
        {
            case Front:
                list.add(0x00);
                break;
            case Back:
                list.add(0x01);
                break;
            case Left:
                list.add(0x02);
                break;
            case Right:
                list.add(0x03);
                break;
            default:

                break;
        }

        final long duration = param.moveDuration;
        final int dur1 = (int) (duration % 256);
        final int dur2 = (int) (duration / 256);

        list.add(dur1);
        list.add(dur2);

        return buildResult(list);
    }

    @Override
    public byte[] buildStopMove(String jsonString)
    {
        List<Integer> list = buildStart(0);

        // 命令
        list.add(0x32);

        list.add(0x05);
        list.add(0x00);
        list.add(0x00);

        return buildResult(list);
    }

    @Override
    public byte[] buildCatch(String jsonString)
    {
        List<Integer> list = buildStart(0);
        // 命令
        list.add(0x32);

        list.add(0x04);
        list.add(0x00);
        list.add(0x00);

        return buildResult(list);
    }

    @Override
    public byte[] buildCheck(String jsonString)
    {
        List<Integer> list = buildStart(0);

        // 命令
        list.add(0x34);

        return buildResult(list);
    }

    private static List<Integer> buildStart(int pid)
    {
        int value0 = 0xfe;
        int value1 = pid / 255;
        int value2 = pid % 255;

        List<Integer> list = new ArrayList<>();
        list.add(value0);
        list.add(value1);
        list.add(value2);
        list.add(~value0);
        list.add(~value1);
        list.add(~value2);

        // 占位符，最终的值为数据的长度
        list.add(00);

        return list;
    }

    /**
     * 输出之前转换
     *
     * @param list
     * @return
     */
    private static byte[] buildResult(List<Integer> list)
    {
        // 占位符，最终的值为校验值
        list.add(00);

        final int size = list.size();
        // 填充占位符，最终的值为数据的长度
        list.set(DATA_LENGTH_INDEX, size);

        byte[] arrResult = new byte[size];

        int total = 0;
        for (int i = 0; i < size; i++)
        {
            final int item = list.get(i);

            arrResult[i] = (byte) item;
            if (i >= DATA_LENGTH_INDEX && i < size - 1)
            {
                total += item;
            }
        }

        // 填充校验值
        int last = total % 100;
        arrResult[size - 1] = (byte) last;

        if (!checkData(arrResult))
        {
            WWLogger.get().log(Level.SEVERE, "XueBaoWWSerialPortDataBuilder error:" + WWUtils.byte2HexString(arrResult, arrResult.length));
        }

        return arrResult;
    }

    public static boolean checkData(byte[] data)
    {
        if (data == null || data.length < DATA_LENGTH_INDEX)
        {
            return false;
        }
        final int start = DATA_LENGTH_INDEX;
        final int end = data.length - 1;

        int total = 0;
        for (int i = start; i < end; i++)
        {
            total += (data[i] & 0xff);
        }

        if (total % 100 != data[end])
        {
            return false;
        }

        if (data[0] != (byte) (~data[3] & 0xff) ||
                data[1] != (byte) (~data[4] & 0xff) ||
                data[2] != (byte) (~data[5] & 0xff))
        {
            return false;
        }
        return true;
    }
}
