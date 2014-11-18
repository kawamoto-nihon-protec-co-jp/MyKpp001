package com.example.kpp.mykpp001;

import java.io.Serializable;

/**
 * WebAPIとのデータ通信用
 * @author T.Kawamoto
 * @version 1.0
 */
public class TransData implements Serializable {
    private static final long serialVersionUID = 1L;

    public String userId;
    public String heartRate;
    public String assayDate;
    public String gpsLatitude;
    public String gpsLongitude;
    public String status = "9";
}
