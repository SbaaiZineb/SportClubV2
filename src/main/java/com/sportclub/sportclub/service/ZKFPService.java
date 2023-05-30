package com.sportclub.sportclub.service;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



public class ZKFPService {
    static {
        System.loadLibrary("libzkfp");
    }

    public ZKFPService() {
    }

    public static native int Initialize();

    public static native int Finalize();

    public static native int GetDeviceCount();

    public static native long OpenDevice(int var0);

    public static native int CloseDevice(long var0);

    public static native int GetParameter(long var0, int var2, byte[] var3, int[] var4);

    public static native int SetParameter(long var0, int var2, byte[] var3, int var4);

    public static native int GetCapParams(long var0, int[] var2, int[] var3);

    public static native int AcquireTemplate(long var0, byte[] var2, byte[] var3, int[] var4);

    public static native int GenRegFPTemplate(byte[] var0, byte[] var1, byte[] var2, byte[] var3, int[] var4);

    public static native long DBInit();

    public static native int DBFree(long var0);

    public static native int DBAdd(int var0, byte[] var1);

    public static native int DBDel(int var0);

    public static native int DBCount();

    public static native int DBClear();

    public static native int VerifyFPByID(int var0, byte[] var1);

    public static native int MatchFP(byte[] var0, byte[] var1);

    public static native int IdentifyFP(byte[] var0, int[] var1, int[] var2);

    public static native int ExtractFromImage(String var0, int var1, byte[] var2, int[] var3);

    public static native String BlobToBase64(byte[] var0, int var1);

    public static native int Base64ToBlob(String var0, byte[] var1, int var2);

    public static native int AcquireImage(long var0, byte[] var2);

    public static native int DBSetParameter(long var0, int var2, int var3);

    public static native int DBGetParameter(long var0, int var2, int[] var3);
}
