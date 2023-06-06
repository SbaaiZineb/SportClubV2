package com.sportclub.sportclub.controller;

import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.Conversion.byteArrayToInt;
@Controller
public class TestController {
    int fpWidth = 0;
    //the height of fingerprint image
    int fpHeight = 0;
    //for verify test
    private byte[] lastRegTemp = new byte[2048];
    //the length of lastRegTemp
    private int cbRegTemp = 0;
    //pre-register template
    private byte[][] regtemparray = new byte[3][2048];
    //Register
    private boolean bRegister = false;
    //Identify
    private boolean bIdentify = true;
    //finger id
    private int iFid = 1;

    private int nFakeFunOn = 1;
    //must be 3
    static final int enroll_cnt = 3;
    //the index of pre-register function
    private int enroll_idx = 0;

    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];

    private WorkThread workThread = null;
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        // "|="鎸変綅鎴栬祴鍊笺��
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }
@GetMapping("/test")
public String  init(){

    if (0 != mhDevice)
    {
        //already inited
        System.out.println("Please close device first!");
    }
    int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
    //Initialize
    cbRegTemp = 0;
    bRegister = false;
    bIdentify = false;
    iFid = 1;
    enroll_idx = 0;
    if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init())
    {
        System.out.println("Init failed!");
    }
    ret = FingerprintSensorEx.GetDeviceCount();
    if (ret < 0)
    {
        System.out.println("no device connected");

    }
    if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
    {
        System.out.println("Open device fail, ret = " + ret + "!");
        FreeSensor();
    }
    if (0 == (mhDB = FingerprintSensorEx.DBInit()))
    {
        System.out.println("Init DB fail, ret = " + ret + "!");
        FreeSensor();
    }
    byte[] paramValue = new byte[4];
    int[] size = new int[1];
    //GetFakeOn
    //size[0] = 4;
    //FingerprintSensorEx.GetParameters(mhDevice, 2002, paramValue, size);
    //nFakeFunOn = byteArrayToInt(paramValue);

    size[0] = 4;
    FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
    fpWidth = byteArrayToInt(paramValue);
    size[0] = 4;
    FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
    fpHeight = byteArrayToInt(paramValue);
    //width = fingerprintSensor.getImageWidth();
    //height = fingerprintSensor.getImageHeight();
    imgbuf = new byte[fpWidth*fpHeight];
    mbStop = false;
    workThread = new WorkThread();
    workThread.start();// 绾跨▼鍚姩
    System.out.println("Open succ!");
    return "test";
}
    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen)))
                {
                    if (nFakeFunOn == 1)
                    {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        //GetFakeStatus
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte)(nFakeStatus & 31) != 31)
                        {
                            System.out.println("Is a fake-finer?");
                            return;
                        }
                    }
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
                    System.out.println("strBase64=" + strBase64);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }}
        private void OnCatpureOK(byte[] imgBuf)
        {

            System.out.println("ok");
        }

        private void OnExtractOK(byte[] template, int len)
        {
            if(bRegister)
            {
                int[] fid = new int[1];
                int[] score = new int [1];
                int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                if (ret == 0)
                {
                    System.out.println("the finger already enroll by " + fid[0] + ",cancel enroll");
                    bRegister = false;
                    enroll_idx = 0;
                    return;
                }
                if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx-1], template) <= 0)
                {
                    System.out.println("please press the same finger 3 times for the enrollment");
                    return;
                }
                System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
                enroll_idx++;
                if (enroll_idx == 3) {
                    int[] _retLen = new int[1];
                    _retLen[0] = 2048;
                    byte[] regTemp = new byte[_retLen[0]];

                    if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                            0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                        iFid++;
                        cbRegTemp = _retLen[0];
                        System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                        String strBase64 = FingerprintSensorEx.BlobToBase64(regTemp, cbRegTemp);
                        //Base64 Template
                        System.out.println("enroll succ");
                    } else {
                        System.out.println("enroll fail, error code=" + ret);                    }
                    bRegister = false;
                } else {
                    System.out.println("You need to press the " + (3 - enroll_idx) + " times fingerprint");                }
            }
            else
            {
                if (bIdentify)
                {
                    int[] fid = new int[1];
                    int[] score = new int [1];
                    int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                    if (ret == 0)
                    {
                        System.out.println("Identify succ, fid=" + fid[0] + ",score=" + score[0]);                    }
                    else
                    {
                        System.out.println("Identify fail, errcode=" + ret);
                    }

                }
                else
                {
                    if(cbRegTemp <= 0)
                    {
                        System.out.println("Please register first!");                    }
                    else
                    {
                        int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
                        if(ret > 0)
                        {
                            System.out.println("Verify succ, score=" + ret);                        }
                        else
                        {
                            System.out.println("Verify fail, ret=" + ret);                        }
                    }
                }
            }
        }
    private void FreeSensor()
    {
        mbStop = true;
        try {		//wait for thread stopping
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (0 != mhDB)
        {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        if (0 != mhDevice)
        {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
    }
@GetMapping("/registre")
    public String regi(){
    if(0 == mhDevice)
    {
        System.out.println("open device first");
    }
    if(!bRegister)
    {
        enroll_idx = 0;
        bRegister = true;
        System.out.println("Please your finger 3 times!");

    }

        return "test";
}
}
