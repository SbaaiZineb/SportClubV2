package com.sportclub.sportclub.controller;


import com.sportclub.sportclub.service.FingerprintSensorErrorCode;
import com.sportclub.sportclub.service.FingerprintSensorEx;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static org.apache.commons.lang3.Conversion.byteArrayToInt;

@Controller
@Data

public class FingerPrint {
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
private final FingerprintSensorEx fingerprintSensorEx;
private final FingerprintSensorErrorCode fingerprintSensorErrorCode;

    public FingerPrint(FingerprintSensorEx fingerprintSensorEx,FingerprintSensorErrorCode fingerprintSensorErrorCode) {
        this.fingerprintSensorEx = fingerprintSensorEx;
        this.fingerprintSensorErrorCode=fingerprintSensorErrorCode;
    }
    @GetMapping("/test")
   public void init(){
        if (fingerprintSensorErrorCode.ZKFP_ERR_OK  != fingerprintSensorEx.Init()){
            System.out.println("Init failed");
        }
       int [] size=new int[1];
       size[0] = 4;
       byte[] paramValue = new byte[4];
        FingerprintSensorEx.GetParameters(2L,1,paramValue,size);
       int fpWidth = 0;
//       fpWidth = byteArrayToInt(paramValue);
       int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
       ret = FingerprintSensorEx.GetDeviceCount();
       if (ret < 0)
       {
           System.out.println("No devices connected!");
           FreeSensor();
           return;
       }
       if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
       {
           System.out.println("Open device fail, ret = " + ret + "!");
           FreeSensor();
           return;
       }
       if (0 == (mhDB = FingerprintSensorEx.DBInit()))
       {
           System.out.println("Init DB fail, ret = " + ret + "!");
           FreeSensor();
           return;
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
}
