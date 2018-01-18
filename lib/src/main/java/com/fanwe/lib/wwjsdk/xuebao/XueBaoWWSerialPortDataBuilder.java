package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.log.WWLogger;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPortDataBuilder;
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
        List<Integer> list = buildStart(0);
        // 命令
        list.add(0x31);

        int timeout = getInitParam().timeout;
        int keepCatch = getInitParam().keepCatch;
        int clawForceStart = WWUtils.scaleValue(getInitParam().clawForceStart, 100, 48, 1);
        int clawForceTop = WWUtils.scaleValue(getInitParam().clawForceTop, 100, 48, 1);
        int clawForceMove = WWUtils.scaleValue(getInitParam().clawForceMove, 100, 48, 1);
        int clawForceBig = 0;
        int grabHeight = 0;
        int clawDownTime = 0;
        int speedFrontBack = 0;
        int speedLeftRight = 0;
        int speedUpDown = 0;

        list.add(timeout); // 设置本局游戏超时时间(单位秒)，超时后会自动下爪
        list.add(keepCatch); // 是否保持大爪力(1-保持爪力，如果为1的话，其他控制爪力的参数无效；0-不保持)
        list.add(clawForceStart); // 抓起爪力(1—48)
        list.add(clawForceTop); // 到顶爪力(1—48)
        list.add(clawForceMove); // 移动爪力(1—48)
        list.add(clawForceBig); // 大爪力(1—48)
        list.add(grabHeight); // 抓起高度(0--10)底部到顶部分成10份，爪子到达某个高度就会抓力变小
        list.add(clawDownTime); // 下线长度(10—35)爪子线放到最长的时间
        list.add(speedFrontBack); // 前后电机的速度(1-5)
        list.add(speedLeftRight); // 左右电机的速度(1-5)
        list.add(speedUpDown); // 上下电机的速度(1-5)

        return buildResult(list);
    }

    @Override
    public byte[] buildMove(String jsonString, Direction direction)
    {
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

        final long duration = getInitParam().moveDuration;
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
