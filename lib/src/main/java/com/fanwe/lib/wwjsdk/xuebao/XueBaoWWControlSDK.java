package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.sdk.WWControlSDK;
import com.fanwe.lib.wwjsdk.sdk.serialport.IWWSerialPortDataBuilder;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPort;

/**
 * 雪暴娃娃机控制sdk
 */
public class XueBaoWWControlSDK extends WWControlSDK
{
    @Override
    protected IWWSerialPortDataBuilder provideSerialDataBuilder()
    {
        return new XueBaoWWSerialPortDataBuilder();
    }

    @Override
    protected WWSerialPort provideSerialPort()
    {
        WWSerialPort serialPort = new XueBaoWWSerialPort();
        serialPort.init("/dev/ttyS1", 115200);
        return serialPort;
    }
}
