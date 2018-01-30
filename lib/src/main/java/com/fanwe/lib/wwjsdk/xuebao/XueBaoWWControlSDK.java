package com.fanwe.lib.wwjsdk.xuebao;

import com.fanwe.lib.wwjsdk.sdk.WWControlSDK;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPort;
import com.fanwe.lib.wwjsdk.sdk.serialport.WWSerialPortDataBuilder;

/**
 * 雪暴娃娃机控制sdk
 */
public class XueBaoWWControlSDK extends WWControlSDK
{
    @Override
    protected WWSerialPortDataBuilder provideSerialDataBuilder()
    {
        return new XueBaoWWSerialPortDataBuilder();
    }

    @Override
    protected WWSerialPort provideSerialPort(String path, int baudRate)
    {
        WWSerialPort serialPort = new XueBaoWWSerialPort();
        serialPort.init(path, baudRate);
        return serialPort;
    }
}
