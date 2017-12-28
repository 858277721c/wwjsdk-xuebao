package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPortDataBuilder;
import com.fanwe.lib.wwjsdk.utils.WWJsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 雪暴娃娃机数据转换实现类
 */
public class XueBaoWWSerialPortDataBuilder extends WWSerialPortDataBuilder
{
    public static final int DATA_LENGTH_INDEX = 6;

    @Override
    protected byte[] onBuildBegin(String jsonString)
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
    protected byte[] onBuildMove(String jsonString, Direction direction)
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
    protected byte[] onBuildStopMove(String jsonString)
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
    protected byte[] onBuildCatch(String jsonString)
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
    protected byte[] onBuildCheck(String jsonString)
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

            arrResult[i] = (byte) (item & 0xff);
            if (i >= DATA_LENGTH_INDEX && i < size - 1)
            {
                total += item;
            }
        }

        // 填充校验值
        int last = total % 100;
        arrResult[size - 1] = (byte) (last & 0xff);

        return arrResult;
    }
}
