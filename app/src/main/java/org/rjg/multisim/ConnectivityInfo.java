package org.rjg.multisim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.HashMap;

@SuppressLint("NewApi")
public class ConnectivityInfo {

    private SharedPreferences pref;

    private String operator1, operator2;
    Context mContext;

    public static final String APN_NAME = "APN_NAME";
    public static final String OPERATOR_NAME = "OPERATOR";
    public static final String SUB_TYPE = "SUB_TYPE";
    public static final String NETWORK_INTERFACE = "NETWORK_INTERFACE";
    public static final String SIM_ID = "SIMID";

    /*MeterPreferences is a class for SharePref
    you can replace your SharePref
    */
    public ConnectivityInfo(Context mContext) {
        try {
            this.mContext = mContext;
            pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            operator1 = pref.getString("SIM_OPERATOR_1", "");
            operator2 = pref.getString("SIM_OPERATOR_2", "");
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    /**
     * Check the targeted Devices is connected to internet
     */

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getNetworkTypeName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }


	/*
     * Method returs all network informations
	 * */


    public HashMap<String, String> getNetworkDetails() {
        HashMap<String, String> networkInfo = new HashMap<String, String>();
        try {
            DualSimManager dualSimDetails = new DualSimManager(mContext);
            if (isNetworkAvailable()) {
                boolean is3g = false, isWifi = false;
                NetworkInfo mMobile = null;
                NetworkInfo mWifi = null;
                ConnectivityManager connManager = (ConnectivityManager) mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                try {
                    mMobile = connManager
                            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    is3g = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                } catch (Exception e) {
                    ////e.printStackTrace();
                }
                try {
                    mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    isWifi = connManager.getNetworkInfo(
                            ConnectivityManager.TYPE_WIFI)
                            .isConnectedOrConnecting();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (isWifi) {
                    networkInfo.put(APN_NAME, mWifi.getExtraInfo());
                    if (mWifi.getTypeName().equalsIgnoreCase("UNKNOWN")) {
                        networkInfo.put(SUB_TYPE, "Wifi");
                    } else {
                        networkInfo.put(SUB_TYPE, mWifi.getTypeName());
                    }
                    networkInfo.put(OPERATOR_NAME, operator1);
                    networkInfo.put(NETWORK_INTERFACE, "WIFI");
                    networkInfo.put(SIM_ID, "0");
                } else if (is3g) {
                    networkInfo.put(APN_NAME, mMobile.getExtraInfo());
                    String networkInterface = getNetworkTypeName(mMobile.getSubtype());
                    String subType = mMobile.getSubtypeName();
                    if (networkInterface.equalsIgnoreCase("UNKNOWN")) {
                        if (mMobile.getSubtypeName().equalsIgnoreCase("EDGE") || mMobile.getSubtypeName().contains("HSPA"))
                            networkInterface = mMobile.getSubtypeName();
                    }
                    try {
                        int simId = 0;
                        String data = mMobile.toString();
                        if (data.contains("simId")) {
                            int startIndex = data.indexOf("simId:") + 6;
                            data = data.substring(startIndex, data.length());
                            if (data.contains(",")) {
                                data = data.substring(0, data.indexOf(","));
                            }
                            data = data.trim();
                            simId = Integer.parseInt(data);
                        }
                        if ((!dualSimDetails.getNETWORK_TYPE(0).equalsIgnoreCase("") && !dualSimDetails.getNETWORK_TYPE(0).equalsIgnoreCase("UNKNOWN")) && simId == 0) {
                            networkInfo.put(SIM_ID, "0");
                            networkInfo.put(OPERATOR_NAME, operator1);
                            if (networkInterface.equalsIgnoreCase("UNKNOWN")) {
                                networkInterface = dualSimDetails.getNETWORK_TYPE(0);
                            }
                            if (subType.equalsIgnoreCase("UNKNOWN")) {
                                subType = dualSimDetails.getNETWORK_TYPE(0);
                            }
                        } else if ((!dualSimDetails.getNETWORK_TYPE(1).equalsIgnoreCase("") && !dualSimDetails.getNETWORK_TYPE(1).equalsIgnoreCase("UNKNOWN")) || simId == 1) {
                            networkInfo.put(SIM_ID, "1");
                            networkInfo.put(OPERATOR_NAME, operator2);
                            if (networkInterface.equalsIgnoreCase("UNKNOWN")) {
                                networkInterface = dualSimDetails.getNETWORK_TYPE(1);
                            }
                            if (subType.equalsIgnoreCase("UNKNOWN")) {
                                subType = dualSimDetails.getNETWORK_TYPE(1);
                            }
                        }
                    } catch (Exception e) {
                        try {
                            if ((!dualSimDetails.getNETWORK_TYPE(0).equalsIgnoreCase("") && !dualSimDetails.getNETWORK_TYPE(0).equalsIgnoreCase("UNKNOWN")) || dualSimDetails.isGPRS(1)) {
                                networkInfo.put(SIM_ID, "1");
                                networkInfo.put(OPERATOR_NAME, operator1);
                                if (networkInterface.equalsIgnoreCase("UNKNOWN")) {
                                    networkInterface = dualSimDetails.getNETWORK_TYPE(0);
                                }
                            } else if ((!dualSimDetails.getNETWORK_TYPE(1).equalsIgnoreCase("") && !dualSimDetails.getNETWORK_TYPE(1).equalsIgnoreCase("UNKNOWN")) || dualSimDetails.isGPRS(0)) {
                                networkInfo.put(SIM_ID, "0");
                                networkInfo.put(OPERATOR_NAME, operator2);
                                if (networkInterface.equalsIgnoreCase("UNKNOWN")) {
                                    networkInterface = dualSimDetails.getNETWORK_TYPE(1);
                                }
                            }
                        } catch (Exception e1) {
                            networkInfo.put(OPERATOR_NAME, dualSimDetails.getNETWORK_OPERATOR_NAME(0));
                            e1.printStackTrace();
                        }
                        //e.printStackTrace();
                    }
                    if (subType.equalsIgnoreCase("UNKNOWN") || subType.equalsIgnoreCase("Wifi")) {
                        subType = networkInterface;
                    }

                    networkInfo.put(SUB_TYPE, subType);
                    networkInfo.put(NETWORK_INTERFACE, networkInterface);
                }
            } else {
                networkInfo.put(SIM_ID, "0");
                networkInfo.put(APN_NAME, "NA");
                networkInfo.put(SUB_TYPE, "NA");
                networkInfo.put(OPERATOR_NAME, operator1);
                networkInfo.put(NETWORK_INTERFACE, "NA");
            }
            if (networkInfo.get(APN_NAME).equalsIgnoreCase("www")) {
                networkInfo.put(APN_NAME, "www " + networkInfo.get(OPERATOR_NAME));
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return networkInfo;
    }

    public boolean isEmpty(String value) {
        if (TextUtils.isEmpty(value)) {
            return true;
        } else {
            return false;
        }
    }

}
