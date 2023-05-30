package com.sportclub.sportclub.service;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.springframework.stereotype.Component;

@Component
public class FingerprintSensorErrorCode {
    public static int ERROR_BEGIN = -1000;
    public static int ERROR_SUCCESS = 0;
    public static int ERROR_FAIL;
    public static int ERROR_OPEN_FAIL;
    public static int ERROR_NOT_OPENED;
    public static int ZKFP_ERR_ALREADY_INIT;
    public static int ZKFP_ERR_OK;
    public static int ZKFP_ERR_INITLIB;
    public static int ZKFP_ERR_INIT;
    public static int ZKFP_ERR_NO_DEVICE;
    public static int ZKFP_ERR_NOT_SUPPORT;
    public static int ZKFP_ERR_INVALID_PARAM;
    public static int ZKFP_ERR_OPEN;
    public static int ZKFP_ERR_INVALID_HANDLE;
    public static int ZKFP_ERR_CAPTURE;
    public static int ZKFP_ERR_EXTRACT_FP;
    public static int ZKFP_ERR_ABSORT;
    public static int ZKFP_ERR_MEMORY_NOT_ENOUGH;
    public static int ZKFP_ERR_BUSY;
    public static int ZKFP_ERR_ADD_FINGER;
    public static int ZKFP_ERR_DEL_FINGER;
    public static int ZKFP_ERR_FAIL;
    public static int ZKFP_ERR_CANCEL;
    public static int ZKFP_ERR_VERIFY_FP;
    public static int ZKFP_ERR_MERGE;
    public static int ZKFP_ERR_NOT_OPENED;
    public static int ZKFP_ERR_NOT_INIT;
    public static int ZKFP_ERR_ALREADY_OPENED;
    public static int ZKFP_ERR_LOADIMAGE;
    public static int ZKFP_ERR_ANALYSE_IMG;
    public static int ZKFP_ERR_TIMEOUT;

    static {
        ERROR_FAIL = ERROR_BEGIN + -1;
        ERROR_OPEN_FAIL = ERROR_BEGIN + -2;
        ERROR_NOT_OPENED = ERROR_BEGIN + -3;
        ZKFP_ERR_ALREADY_INIT = 1;
        ZKFP_ERR_OK = 0;
        ZKFP_ERR_INITLIB = -1;
        ZKFP_ERR_INIT = -2;
        ZKFP_ERR_NO_DEVICE = -3;
        ZKFP_ERR_NOT_SUPPORT = -4;
        ZKFP_ERR_INVALID_PARAM = -5;
        ZKFP_ERR_OPEN = -6;
        ZKFP_ERR_INVALID_HANDLE = -7;
        ZKFP_ERR_CAPTURE = -8;
        ZKFP_ERR_EXTRACT_FP = -9;
        ZKFP_ERR_ABSORT = -10;
        ZKFP_ERR_MEMORY_NOT_ENOUGH = -11;
        ZKFP_ERR_BUSY = -12;
        ZKFP_ERR_ADD_FINGER = -13;
        ZKFP_ERR_DEL_FINGER = -14;
        ZKFP_ERR_FAIL = -17;
        ZKFP_ERR_CANCEL = -18;
        ZKFP_ERR_VERIFY_FP = -20;
        ZKFP_ERR_MERGE = -22;
        ZKFP_ERR_NOT_OPENED = -23;
        ZKFP_ERR_NOT_INIT = -24;
        ZKFP_ERR_ALREADY_OPENED = -25;
        ZKFP_ERR_LOADIMAGE = -26;
        ZKFP_ERR_ANALYSE_IMG = -27;
        ZKFP_ERR_TIMEOUT = -28;
    }

    public FingerprintSensorErrorCode() {
    }
}
