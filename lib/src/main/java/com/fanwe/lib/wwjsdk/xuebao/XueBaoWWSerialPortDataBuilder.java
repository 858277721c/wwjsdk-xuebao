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

        List<Byte> list = buildStart(0);
        // 命令
        list.add((byte) 0x31);

        list.add((byte) param.timeout);
        list.add((byte) param.keepCatch);
        list.add((byte) param.clawForceStart);
        list.add((byte) param.clawForceTop);
        list.add((byte) param.clawForceMove);
        list.add((byte) param.clawForceBig);
        list.add((byte) param.grabHeight);
        list.add((byte) param.clawDownTime);
        list.add((byte) param.speedFrontBack);
        list.add((byte) param.speedLeftRight);
        list.add((byte) param.speedUpDown);

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

        List<Byte> list = buildStart(0);

        // 命令
        list.add((byte) 0x32);

        switch (direction)
        {
            case Front:
                list.add((byte) 0x00);
                break;
            case Back:
                list.add((byte) 0x01);
                break;
            case Left:
                list.add((byte) 0x02);
                break;
            case Right:
                list.add((byte) 0x03);
                break;
            default:

                break;
        }

        final long duration = param.moveDuration;
        list.add((byte) (duration % 256));
        list.add((byte) (duration / 256));

        return buildResult(list);
    }

    @Override
    protected byte[] onBuildStopMove(String jsonString)
    {
        List<Byte> list = buildStart(0);

        // 命令
        list.add((byte) 0x32);

        list.add((byte) 0x05);
        list.add((byte) 0x00);
        list.add((byte) 0x00);

        return buildResult(list);
    }

    @Override
    protected byte[] onBuildCatch(String jsonString)
    {
        List<Byte> list = buildStart(0);
        // 命令
        list.add((byte) 0x32);

        list.add((byte) 0x04);
        list.add((byte) 0x00);
        list.add((byte) 0x00);

        return buildResult(list);
    }

    @Override
    protected byte[] onBuildCheck(String jsonString)
    {
        List<Byte> list = buildStart(0);

        // 命令
        list.add((byte) 0x34);

        return buildResult(list);
    }

    private static List<Byte> buildStart(int pid)
    {
        byte byte0 = (byte) 0xfe;
        byte byte1 = (byte) (pid / 255);
        byte byte2 = (byte) (pid % 255);

        List<Byte> list = new ArrayList<>();
        list.add(byte0);
        list.add(byte1);
        list.add(byte2);
        list.add((byte) ~byte0);
        list.add((byte) ~byte1);
        list.add((byte) ~byte2);

        // 占位符，最终的值为数据的长度
        list.add((byte) 00);

        return list;
    }

    /**
     * 输出之前转换
     *
     * @param list
     * @return
     */
    private static byte[] buildResult(List<Byte> list)
    {
        // 占位符，最终的值为校验值
        list.add((byte) 00);

        final int size = list.size();
        // 填充占位符，最终的值为数据的长度
        list.set(DATA_LENGTH_INDEX, (byte) size);

        byte[] arrResult = new byte[size];

        int total = 0;
        for (int i = 0; i < size; i++)
        {
            byte data = list.get(i);
            arrResult[i] = data;

            if (i >= DATA_LENGTH_INDEX && i < size - 1)
            {
                total += data;
            }
        }

        // 填充校验值
        arrResult[size - 1] = (byte) (total % 100);

        return arrResult;
    }
}
