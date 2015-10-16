package org.rjg.multisim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("NewApi")
public class DualSimManager {

    private String SIM_VARINT = "";
    private String telephonyClassName = "";
    private SharedPreferences pref;
    private int slotNumber_1 = 0;
    private int slotNumber_2 = 1;
    private String slotName_1 = "null";
    private String slotName_2 = "null";
    private String[] listofClass;

    private String IMEI_1, IMSI_1, NETWORK_TYPE_1, NETWORK_OPERATOR_NAME_1;
    private String SIM_OPERATOR_CODE_1;
    private String SIM_OPERATOR_NAME_1;
    private String SIM_NETWORK_SIGNAL_STRENGTH_1;
    private String IMEI_2, IMSI_2, NETWORK_TYPE_2, NETWORK_OPERATOR_NAME_2;
    private String SIM_OPERATOR_CODE_2;
    private String SIM_OPERATOR_NAME_2;
    private String SIM_NETWORK_SIGNAL_STRENGTH_2;
    private String NETWORK_OPERATOR_CODE_1, isGPRS_1;
    private String NETWORK_OPERATOR_CODE_2, isGPRS_2;
    private String SIMSerial_1, SIMSerial_2;
    boolean isRoaming_1, isRoaming_2;
    private int[] CELL_LOC_1;
    private int[] CELL_LOC_2;

    public final static String m_IMEI = "getDeviceId";
    public final static String m_IMSI = "getSubscriberId";

    public final static String m_SIM_OPERATOR_NAME = "getSimOperatorName";

    public final static String m_NETWORK_OPERATOR = "getNetworkOperatorName";
    public final static String m_NETWORK_OPERATOR_CODE = "getNetworkOperator";

    public final static String m_NETWORK_TYPE_NAME = "getNetworkTypeName";

    public final static String m_CELL_LOC = "getNeighboringCellInfo";
    public final static String m_IS_ROAMING = "isNetworkRoaming";

    public final static String m_SIM_SERIAL = "getSimSerialNumber";
    public final static String m_SIM_SUBSCRIBER = "getSubscriberId";

    public final static String m_SIM_OPERATOR_CODE = "getSimOperator";
    public final static String m_DATA_STATE = "getDataNetworkType";

    protected static CustomTelephony customTelephony;

    public DualSimManager(Context mContext) {
        try {
            if (IMEI_1 == null) {
                customTelephony = new CustomTelephony(mContext);
            } else {
                customTelephony.getCurrentData();
            }

            if (customTelephony.getIMEIList().size() > 0) {
                customTelephony.getDefaultSIMInfo();
            }

            updateOperatorDetails(mContext);
        } catch (Exception e) {
        }

    }

    private void updateOperatorDetails(Context mContext) {
        Editor edit = pref.edit();


        if (!TextUtils.isEmpty(this.getmSimOperatorName(0))) {
            //MeterPreferences.setStringPrefrence(mContext, LocalConstants.SIM_OPERATOR_1, this.getmSimOperatorName(0));
            edit.putString("SIM_OPERATOR_1", this.getmSimOperatorName(0));
        }
        if (!TextUtils.isEmpty(this.getmSimOperatorName(1))) {
            //MeterPreferences.setStringPrefrence(mContext, LocalConstants.SIM_OPERATOR_2, this.getmSimOperatorName(1));
            edit.putString("SIM_OPERATOR_2", this.getmSimOperatorName(1));
        }

        if (!TextUtils.isEmpty(this.getNETWORK_OPERATOR_NAME(0))) {
            //MeterPreferences.setStringPrefrence(mContext, LocalConstants.NETWORK_OPERATOR_1, this.getNETWORK_OPERATOR_NAME(0));
            edit.putString("NETWORK_OPERATOR_1", this.getNETWORK_OPERATOR_NAME(0));
        }
        if (!TextUtils.isEmpty(this.getNETWORK_OPERATOR_NAME(1))) {
            //MeterPreferences.setStringPrefrence(mContext, LocalConstants.NETWORK_OPERATOR_2, this.getNETWORK_OPERATOR_NAME(1));
            edit.putString("NETWORK_OPERATOR_2", this.getNETWORK_OPERATOR_NAME(1));
        }

        //MeterPreferences.putInt(mContext, LocalConstants.SIM_SUPPORTED_COUNT, this.getSupportedSimCount());
        edit.putInt("SIM_SUPPORTED_COUNT", this.getSupportedSimCount());
    }

    public boolean isSIMSupported() {
        if (!TextUtils.isEmpty(IMEI_1) || !TextUtils.isEmpty(IMEI_2)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isDualSIMSupported() {
        if (!TextUtils.isEmpty(IMEI_1) && !TextUtils.isEmpty(IMEI_2)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstSimActive() {
        if (IMSI_1 == null || TextUtils.isEmpty(IMSI_1)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSecondSimActive() {
        if (IMSI_2 == null || TextUtils.isEmpty(IMSI_2)) {
            return false;
        } else if (IMSI_1 != null && IMSI_1.equalsIgnoreCase(IMSI_2)) {
            return false;
        } else {
            return true;
        }

    }

    public int getSupportedSimCount() {
        if (isSIMSupported()) {
            if (isDualSIMSupported()) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public String getIMEI(int slotnumber) {
        if (slotnumber == 0)
            return IMEI_1;
        else
            return IMEI_2;
    }

    public String getIMSI(int slotnumber) {
        if (slotnumber == 0)
            return IMSI_1;
        else
            return IMSI_2;
    }


    public String getNETWORK_OPERATOR_NAME(int slotnumber) {
        //return NETWORK_OPERATOR_NAME_1;
        if (slotnumber == 0)
            return NETWORK_OPERATOR_NAME_1;
        else {
            return NETWORK_OPERATOR_NAME_2;
        }
    }

    public String getmSimOperatorCode(int slotnumber) {
        if (slotnumber == 0)
            return SIM_OPERATOR_CODE_1;
        else
            return SIM_OPERATOR_CODE_2;
    }

    public String getmSimOperatorName(int slotnumber) {
        if (slotnumber == 0)
            return SIM_OPERATOR_NAME_1;
        else
            return SIM_OPERATOR_NAME_2;
    }

    public static String getmSimSubscriber() {
        return m_SIM_SUBSCRIBER;
    }

    public static String getmSimSerial() {
        return m_SIM_SERIAL;
    }

    public String getSIM_NETWORK_SIGNAL_STRENGTH(int slotnumber) {
        if (slotnumber == 0)
            return SIM_NETWORK_SIGNAL_STRENGTH_1;
        else
            return SIM_NETWORK_SIGNAL_STRENGTH_2;
    }

    public boolean isGPRS(int slotnumber) {
        boolean isGPRS = false;
        try {
            String GPRS_FLAG = isGPRS_1;
            if (slotnumber == 1) {
                GPRS_FLAG = isGPRS_2;
            }
            int gprsInt = Integer.parseInt(GPRS_FLAG);
            if (gprsInt == TelephonyManager.DATA_CONNECTING || gprsInt == TelephonyManager.DATA_CONNECTED) {
                isGPRS = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return isGPRS;
    }


    public void setSIM_NETWORK_SIGNAL_STRENGTH_1(
            String sIM_NETWORK_SIGNAL_STRENGTH_1) {
        SIM_NETWORK_SIGNAL_STRENGTH_1 = sIM_NETWORK_SIGNAL_STRENGTH_1;
    }


    public int getSIM_CELLID(int slotnumber) {
        if (slotnumber == 0)
            return CELL_LOC_1[0];
        else
            return CELL_LOC_2[0];
    }


    public int getSIM_LOCID(int slotnumber) {
        if (slotnumber == 0)
            return CELL_LOC_1[1];
        else
            return CELL_LOC_2[1];
    }

    public String getNETWORK_TYPE(int slotnumber) {

        if (slotnumber == 0)
            return NETWORK_TYPE_1;
        else
            return NETWORK_TYPE_2;
    }

    public int[] getNETWORK_OPERATOR_CODE(int slotnumber) {
        int OperatorCode[] = new int[2];
        String code = NETWORK_OPERATOR_CODE_1;
        if (slotnumber == 0)
            if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_1) && !TextUtils.isEmpty(SIM_OPERATOR_CODE_1)) {
                code = SIM_OPERATOR_CODE_1;
            } else {
                code = NETWORK_OPERATOR_CODE_1;
            }
        else {
            if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_2) && !TextUtils.isEmpty(SIM_OPERATOR_CODE_2)) {
                code = SIM_OPERATOR_CODE_2;
            } else {
                code = NETWORK_OPERATOR_CODE_2;
            }
        }

		/*if(slotnumber==1){
            code = NETWORK_OPERATOR_CODE_2;
		}*/
        OperatorCode[0] = -1;
        OperatorCode[1] = -1;
        try {
            if (code != null) {
                OperatorCode[0] = Integer.parseInt(code.substring(0, 3));
                OperatorCode[1] = Integer.parseInt(code.substring(3));
            }
        } catch (Exception e) {
        }
        return OperatorCode;
    }

    public boolean isRoaming(int slotnumber) {
        if (slotnumber == 0) {
            return isRoaming_1;
        } else {
            return isRoaming_2;
        }
    }

    class CustomTelephony {
        Context mContext;
        TelephonyManager telephony;

        public CustomTelephony(Context mContext) {
            try {
                this.mContext = mContext;
                telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                telephonyClassName = pref.getString("dualsim_telephonycls", "");
                SIM_VARINT = pref.getString("SIM_VARINT", "");
                slotName_1 = pref.getString("SIM_SLOT_NAME_1", "");
                slotName_2 = pref.getString("SIM_SLOT_NAME_2", "");
                slotNumber_1 = pref.getInt("SIM_SLOT_NUMBER_1", 0);
                slotNumber_2 = pref.getInt("SIM_SLOT_NUMBER_2", 1);
                fetchClassInfo();
                if (telephonyClassName.equalsIgnoreCase("")) {
                    fetchClassInfo();
                } else if (!isValidMethod(telephonyClassName)) {
                    fetchClassInfo();
                }
                gettingAllMethodValues();

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        /**
         * This method returns the class name in which we fetch dual sim details
         */
        public void fetchClassInfo() {
            try {
                telephonyClassName = "android.telephony.TelephonyManager";
                listofClass = new String[]{
                        "com.mediatek.telephony.TelephonyManagerEx",
                        "android.telephony.TelephonyManager",
                        "android.telephony.MSimTelephonyManager",
                        "com.android.internal.telephony.Phone",
                        "com.android.internal.telephony.PhoneFactory"};
                for (int index = 0; index < listofClass.length; index++) {
                    if (isTelephonyClassExists(listofClass[index])) {
                        if (isMethodExists(listofClass[index], "getDeviceId")) {
                            //System.out.println("getDeviceId method found");
                            if (!SIM_VARINT.equalsIgnoreCase("")) {
                                break;
                            }
                        }
                        if (isMethodExists(listofClass[index],
                                "getNetworkOperatorName")) {
							/*System.out
									.println("getNetworkOperatorName method found");*/
                            break;
                        } else if (isMethodExists(listofClass[index],
                                "getSimOperatorName")) {
							/*System.out.println("getSimOperatorName method found");*/
                            break;
                        }
                    }
                }
                for (int index = 0; index < listofClass.length; index++) {
                    try {
                        if (slotName_1 == null || slotName_1.equalsIgnoreCase("")) {
                            getValidSlotFields(listofClass[index]);
                            // if(slotName_1!=null || !slotName_1.equalsIgnoreCase("")){
                            getSlotNumber(listofClass[index]);
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }

                Editor edit = pref.edit();
                edit.putString("dualsim_telephonycls", telephonyClassName);
                edit.putString("SIM_VARINT", SIM_VARINT);
                edit.putString("SIM_SLOT_NAME_1", slotName_1);
                edit.putString("SIM_SLOT_NAME_2", slotName_2);
                edit.putInt("SIM_SLOT_NUMBER_1", slotNumber_1);
                edit.putInt("SIM_SLOT_NUMBER_2", slotNumber_2);
                edit.commit();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }


        /**
         * Check Method is found in class
         */
        public boolean isValidMethod(String className) {
            boolean isValidMail = false;
            try {
                if (isMethodExists(className, "getDeviceId")) {
                    isValidMail = true;
                } else if (isMethodExists(className, "getNetworkOperatorName")) {
                    isValidMail = true;
                } else if (isMethodExists(className, "getSimOperatorName")) {
                    isValidMail = true;
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return isValidMail;
        }

        public String getMethodValue(String className, String compairMethod, int slotNumber_1) {
            String value = "";
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                StringBuffer sbf = new StringBuffer();
                Method[] methodList = telephonyClass.getDeclaredMethods();
                for (int index = methodList.length - 1; index >= 0; index--) {
                    sbf.append("\n\n" + methodList[index].getName());
                    if (methodList[index].getReturnType().equals(String.class)) {
                        String methodName = methodList[index].getName();
                        if (methodName.contains(compairMethod)) {
                            Class<?>[] param = methodList[index]
                                    .getParameterTypes();
                            if (param.length > 0) {
                                if (param[0].equals(int.class)) {
                                    try {
                                        SIM_VARINT = methodName.substring(
                                                compairMethod.length(),
                                                methodName.length());
                                        if (!methodName.equalsIgnoreCase(compairMethod + "Name") && !methodName.equalsIgnoreCase(compairMethod + "ForSubscription")) {
                                            value = invokeMethod(telephonyClassName, slotNumber_1, compairMethod, SIM_VARINT);
                                            if (!TextUtils.isEmpty(value)) {
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                } else if (param[0].equals(long.class)) {
                                    try {
                                        SIM_VARINT = methodName.substring(
                                                compairMethod.length(),
                                                methodName.length());
                                        if (!methodName.equalsIgnoreCase(compairMethod + "Name") && !methodName.equalsIgnoreCase(compairMethod + "ForSubscription")) {
                                            value = invokeLongMethod(telephonyClassName, slotNumber_1, compairMethod, SIM_VARINT);
                                            if (!TextUtils.isEmpty(value)) {
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return value;
        }

        /**
         * Check method with sim variant
         */
        public boolean isMethodExists(String className, String compairMethod) {
            boolean isExists = false;
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                StringBuffer sbf = new StringBuffer();
                Method[] methodList = telephonyClass.getDeclaredMethods();
                for (int index = methodList.length - 1; index >= 0; index--) {
                    sbf.append("\n\n" + methodList[index].getName());
                    if (methodList[index].getReturnType().equals(String.class)) {
                        String methodName = methodList[index].getName();
                        if (methodName.contains(compairMethod)) {
                            Class<?>[] param = methodList[index]
                                    .getParameterTypes();
                            if (param.length > 0) {
                                if (param[0].equals(int.class)) {
                                    try {
                                        SIM_VARINT = methodName.substring(
                                                compairMethod.length(),
                                                methodName.length());
                                        telephonyClassName = className;
                                        isExists = true;
                                        break;
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                } else {
                                    telephonyClassName = className;
                                    isExists = true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return isExists;
        }

        public ArrayList<String> getIMSIList() {
            ArrayList<String> imeiList = new ArrayList<>();
            IMSI_1 = invokeMethod(telephonyClassName, slotNumber_1, m_IMSI, SIM_VARINT);
            if (TextUtils.isEmpty(IMSI_1)) {
                IMSI_1 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_1);
            }
            if (IMSI_1 == null || IMSI_1.equalsIgnoreCase("")) {
                IMSI_1 = telephony.getSubscriberId();
            }
            IMSI_2 = invokeMethod(telephonyClassName, slotNumber_2, m_IMSI, SIM_VARINT);
            if (TextUtils.isEmpty(IMSI_2)) {
                IMSI_2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2);
                if (TextUtils.isEmpty(IMSI_2)) {
                    IMSI_2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2 + 1);
                }
            }
            if (!TextUtils.isEmpty(IMSI_2) && !TextUtils.isEmpty(IMSI_1)) {
                if (IMSI_1.equalsIgnoreCase(IMSI_2)) {
                    IMSI_1 = "";
                }
            }
            if (IMSI_1 != null && IMSI_2 != null && IMSI_1.equalsIgnoreCase("")) {
                String IMSI2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2 + 1);
                if (!TextUtils.isEmpty(IMSI2)) {
                    Editor edit = pref.edit();
                    edit.putString("SIM_SLOT_NAME_1", slotName_1);
                    edit.putString("SIM_SLOT_NAME_2", slotName_2);

                    IMSI_1 = IMSI_2;
                    IMSI_2 = IMSI2;
                    slotNumber_1 = slotNumber_2;
                    slotNumber_2 = slotNumber_2 + 1;
                }
            }
            if (!TextUtils.isEmpty(IMSI_1)) {
                imeiList.add(IMSI_1);
            }
            if (!TextUtils.isEmpty(IMSI_2)) {
                imeiList.add(IMSI_2);
            }
            return imeiList;
        }

        public ArrayList<String> getIMEIList() {
            ArrayList<String> imeiList = new ArrayList<>();
            try {
                IMEI_1 = invokeMethod(telephonyClassName, slotNumber_1, m_IMEI, SIM_VARINT);
                if (TextUtils.isEmpty(IMEI_1)) {
                    IMEI_1 = getMethodValue(telephonyClassName, m_IMEI, slotNumber_1);
                }
                if (IMEI_1 == null || IMEI_1.equalsIgnoreCase("")) {
                    IMEI_1 = telephony.getDeviceId();
                }
                IMEI_2 = invokeMethod(telephonyClassName, slotNumber_2, m_IMEI, SIM_VARINT);
                if (TextUtils.isEmpty(IMEI_2)) {
                    IMEI_2 = getMethodValue(telephonyClassName, m_IMEI, slotNumber_1);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (!TextUtils.isEmpty(IMEI_2)) {
                imeiList.add(IMEI_1);
            }
            if (!TextUtils.isEmpty(IMEI_2)) {
                imeiList.add(IMEI_2);
            }
            return imeiList;
        }

        public void getDefaultSIMInfo() {
            telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephony.getSubscriberId();
            if (TextUtils.isEmpty(IMSI_1) || IMSI_1.equalsIgnoreCase(IMSI)) {
                IMEI_1 = telephony.getDeviceId();
                isGPRS_1 = String.valueOf(telephony.isNetworkRoaming());
                SIM_OPERATOR_CODE_1 = telephony.getSimOperator();
                SIM_OPERATOR_NAME_1 = telephony.getSimOperatorName();
                NETWORK_OPERATOR_CODE_1 = telephony.getNetworkOperator();
                NETWORK_OPERATOR_NAME_1 = telephony.getNetworkOperatorName();
                NETWORK_TYPE_1 = getNetworkTypeName(telephony.getNetworkType());
            } else if (isSecondSimActive() && IMSI_2.equalsIgnoreCase(IMSI)) {
                IMEI_2 = telephony.getDeviceId();
                isGPRS_2 = String.valueOf(telephony.isNetworkRoaming());
                SIM_OPERATOR_CODE_2 = telephony.getSimOperator();
                SIM_OPERATOR_NAME_2 = telephony.getSimOperatorName();
                NETWORK_OPERATOR_CODE_2 = telephony.getNetworkOperator();
                NETWORK_OPERATOR_NAME_2 = telephony.getNetworkOperatorName();
                NETWORK_TYPE_2 = getNetworkTypeName(telephony.getNetworkType());
            }

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


        public void gettingAllMethodValues() {
            try {
                IMEI_1 = invokeMethod(telephonyClassName, slotNumber_1, m_IMEI, SIM_VARINT);
                if (TextUtils.isEmpty(IMEI_1)) {
                    IMEI_1 = getMethodValue(telephonyClassName, m_IMEI, slotNumber_1);
                }
                if (IMEI_1 == null || IMEI_1.equalsIgnoreCase("")) {
                    IMEI_1 = telephony.getDeviceId();
                }
                IMEI_2 = invokeMethod(telephonyClassName, slotNumber_2, m_IMEI, SIM_VARINT);
                if (TextUtils.isEmpty(IMEI_2)) {
                    IMEI_2 = getMethodValue(telephonyClassName, m_IMEI, slotNumber_1);
                }
                IMSI_1 = invokeMethod(telephonyClassName, slotNumber_1, m_IMSI, SIM_VARINT);
                if (TextUtils.isEmpty(IMSI_1)) {
                    IMSI_1 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_1);
                }
                if (IMSI_1 == null || IMSI_1.equalsIgnoreCase("")) {
                    IMSI_1 = telephony.getSubscriberId();
                }
                IMSI_2 = invokeMethod(telephonyClassName, slotNumber_2, m_IMSI, SIM_VARINT);
                if (TextUtils.isEmpty(IMSI_2)) {
                    IMSI_2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2);
                    if (TextUtils.isEmpty(IMSI_2)) {
                        IMSI_2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2 + 1);
                    }
                }
                if (!TextUtils.isEmpty(IMSI_2) && !TextUtils.isEmpty(IMSI_1)) {
                    if (IMSI_1.equalsIgnoreCase(IMSI_2)) {
                        IMSI_1 = "";
                    }
                }
                if (IMSI_1 != null && IMSI_2 != null && IMSI_1.equalsIgnoreCase("")) {
                    String IMSI2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber_2 + 1);
                    if (!TextUtils.isEmpty(IMSI2)) {

                        Editor edit = pref.edit();
                        edit.putString("SIM_SLOT_NAME_1", slotName_1);
                        edit.putString("SIM_SLOT_NAME_2", slotName_2);
                        IMSI_1 = IMSI_2;
                        IMSI_2 = IMSI2;
                        slotNumber_1 = slotNumber_2;
                        slotNumber_2 = slotNumber_2 + 1;
                    }
                }
                SIM_OPERATOR_NAME_1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, 0);
                SIM_OPERATOR_NAME_2 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, 1);
                if (TextUtils.isEmpty(SIM_OPERATOR_NAME_1))
                    SIM_OPERATOR_NAME_1 = invokeMethod(telephonyClassName, slotNumber_1, m_SIM_OPERATOR_NAME, SIM_VARINT);
                if (TextUtils.isEmpty(SIM_OPERATOR_NAME_1)) {
                    SIM_OPERATOR_NAME_1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, slotNumber_1);
                }
                if (TextUtils.isEmpty(SIM_OPERATOR_NAME_2))
                    SIM_OPERATOR_NAME_2 = invokeMethod(telephonyClassName, slotNumber_2, m_SIM_OPERATOR_NAME, SIM_VARINT);
                if (TextUtils.isEmpty(SIM_OPERATOR_NAME_2)) {
                    SIM_OPERATOR_NAME_2 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, slotNumber_2);
                }

                SIM_OPERATOR_CODE_1 = invokeMethod(telephonyClassName, slotNumber_1, m_SIM_OPERATOR_CODE, SIM_VARINT);
                if (TextUtils.isEmpty(SIM_OPERATOR_CODE_1)) {
                    SIM_OPERATOR_CODE_1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_CODE, slotNumber_1);
                }
                SIM_OPERATOR_CODE_2 = invokeMethod(telephonyClassName, slotNumber_2, m_SIM_OPERATOR_CODE, SIM_VARINT);

                NETWORK_OPERATOR_NAME_1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, 0);
                NETWORK_OPERATOR_NAME_2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, 1);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_NAME_1))
                    NETWORK_OPERATOR_NAME_1 = invokeMethod(telephonyClassName, slotNumber_1, m_NETWORK_OPERATOR, SIM_VARINT);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_NAME_1)) {
                    NETWORK_OPERATOR_NAME_1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber_1);
                }

                if (TextUtils.isEmpty(NETWORK_OPERATOR_NAME_2))
                    NETWORK_OPERATOR_NAME_2 = invokeMethod(telephonyClassName, slotNumber_2, m_NETWORK_OPERATOR_CODE, SIM_VARINT);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_NAME_2)) {
                    NETWORK_OPERATOR_NAME_2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber_2);
                }
                if (NETWORK_OPERATOR_NAME_1.equalsIgnoreCase(""))
                    NETWORK_OPERATOR_NAME_1 = invokeMethod(telephonyClassName, slotNumber_1, m_NETWORK_OPERATOR, SIM_VARINT);

                if (NETWORK_OPERATOR_NAME_2.equalsIgnoreCase(""))
                    NETWORK_OPERATOR_NAME_2 = invokeMethod(telephonyClassName, slotNumber_2, m_NETWORK_OPERATOR, SIM_VARINT);

                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_1)) {
                    NETWORK_OPERATOR_NAME_1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber_1);
                }

                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_1)) {
                    NETWORK_OPERATOR_NAME_2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber_2);
                }
                NETWORK_OPERATOR_CODE_1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, 0);
                NETWORK_OPERATOR_CODE_2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, 1);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_1))
                    NETWORK_OPERATOR_CODE_1 = invokeMethod(telephonyClassName, slotNumber_1, m_NETWORK_OPERATOR_CODE, SIM_VARINT);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_1)) {
                    NETWORK_OPERATOR_CODE_1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber_1);
                }

                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_2))
                    NETWORK_OPERATOR_CODE_2 = invokeMethod(telephonyClassName, slotNumber_2, m_NETWORK_OPERATOR_CODE, SIM_VARINT);
                if (TextUtils.isEmpty(NETWORK_OPERATOR_CODE_2)) {
                    NETWORK_OPERATOR_CODE_2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber_2);
                }
                SIMSerial_1 = getSimSerialNumber(slotNumber_1);
                SIMSerial_2 = getSimSerialNumber(slotNumber_2);
                if (SIMSerial_1.equalsIgnoreCase(SIMSerial_2)) {
                    SIMSerial_2 = getSimSerialNumber(slotNumber_2 + 1);
                }
                getCurrentData();
                if (NETWORK_OPERATOR_NAME_1 == null || NETWORK_OPERATOR_NAME_1.equalsIgnoreCase("") || NETWORK_OPERATOR_NAME_1.equalsIgnoreCase("UNKNOWN")) {
                    NETWORK_OPERATOR_NAME_1 = telephony.getSimOperatorName();
                }
                if (NETWORK_OPERATOR_CODE_1 == null || NETWORK_OPERATOR_CODE_1.equalsIgnoreCase("") || NETWORK_OPERATOR_CODE_1.equalsIgnoreCase("UNKNOWN")) {
                    NETWORK_OPERATOR_CODE_1 = telephony.getSimOperator();
                }
                if (TextUtils.isEmpty(m_IS_ROAMING)) {
                    isRoaming_1 = telephony.isNetworkRoaming();
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public String getSimSerialNumber(int slotNumber) {
            String SIMSerial = invokeMethod(telephonyClassName, slotNumber, m_SIM_SUBSCRIBER, SIM_VARINT);
            if (TextUtils.isEmpty(SIMSerial)) {
                SIMSerial = getMethodValue(telephonyClassName, m_SIM_SUBSCRIBER, slotNumber);
                if (TextUtils.isEmpty(SIMSerial)) {
                    SIMSerial = getMethodValue(telephonyClassName, m_SIM_SERIAL, slotNumber);
                }
                if (TextUtils.isEmpty(SIMSerial)) {
                    SIMSerial = getMethodValue(telephonyClassName, m_SIM_SERIAL, slotNumber);
                }
            }
            return SIMSerial;
        }

        private void getCurrentData() {
            try {
                telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                NETWORK_TYPE_1 = getNetworkType(0);
                NETWORK_TYPE_2 = getNetworkType(1);
                if (NETWORK_TYPE_1 == null || NETWORK_TYPE_1.equalsIgnoreCase("") || NETWORK_TYPE_1.equalsIgnoreCase("UNKNOWN")) {
                    NETWORK_TYPE_1 = getNetworkTypeName(telephony.getNetworkType());
                }
                isRoaming_1 = invokeMethodboolean(telephonyClassName, slotNumber_1, m_IS_ROAMING, SIM_VARINT);
                if (!isRoaming_1) {
                    isRoaming_1 = telephony.isNetworkRoaming();
                }
                isRoaming_2 = invokeMethodboolean(telephonyClassName, slotNumber_2, m_IS_ROAMING, SIM_VARINT);

                isGPRS_1 = invokeMethod(telephonyClassName, slotNumber_1, m_DATA_STATE, SIM_VARINT);
                isGPRS_2 = invokeMethod(telephonyClassName, slotNumber_2, m_DATA_STATE, SIM_VARINT);

                CELL_LOC_1 = getCellLocation(slotNumber_1);
                CELL_LOC_2 = getCellLocation(slotNumber_2);


            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public String getNetworkType(int slotnumber) {
            String networkType = "UNKNOWN";
            try {
                if (slotnumber == 0) {
                    networkType = invokeMethod(telephonyClassName, slotNumber_1, m_NETWORK_TYPE_NAME, SIM_VARINT);
                } else {
                    networkType = invokeMethod(telephonyClassName, slotNumber_2, m_NETWORK_TYPE_NAME, SIM_VARINT);
                }
                if (networkType.equalsIgnoreCase("")) {
                    try {
                        networkType = getDeviceIdBySlot("getNetworkType", slotnumber);
                    } catch (Exception e) {
                    }
                    if (networkType.equalsIgnoreCase("")) {
                        try {
                            networkType = getDeviceIdBySlotOld("getNetworkTypeGemini", slotnumber);
                        } catch (Exception e) {
                        }
                    }
                }
                ConnectivityInfo connInfo = new ConnectivityInfo(mContext);
                networkType = connInfo.getNetworkTypeName(Integer.parseInt(networkType));
                if (slotnumber == 0 && !TextUtils.isEmpty(networkType)) {
                    networkType = connInfo.getNetworkTypeName(telephony.getNetworkType());
                }
            } catch (Exception e) {
                ////e.printStackTrace();
            }
            return networkType;
        }

        /**
         * get neighboring cell information of the device
         *
         */
        public int[] getCellLocation(int slot) {
            int[] cellLoc = new int[]{-1, -1};
            try {
                if (slot == slotNumber_1) {
                    if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                        GsmCellLocation location = (GsmCellLocation) telephony
                                .getCellLocation();
                        if (location == null) {
                            location = (GsmCellLocation) telephony
                                    .getCellLocation();
                        }
                        if (location != null) {
                            cellLoc[0] = location.getCid();
                            cellLoc[1] = location.getLac();
                        }
                    }
                } else {
                    Object cellInfo = (Object) getObjectBySlot("getNeighboringCellInfo" + SIM_VARINT, slot);
                    if (cellInfo != null) {
                        List<NeighboringCellInfo> info = (List<NeighboringCellInfo>) cellInfo;
                        cellLoc[0] = info.get(0).getCid();
                        cellLoc[1] = info.get(0).getLac();
                    }
                }
            } catch (Exception e) {
                ////e.printStackTrace();
            }
            return cellLoc;
        }

        private Object getObjectBySlot(String predictedMethodName, int slotID) {
            Object ob_phone = null;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                Object[] obParameter = new Object[1];
                obParameter[0] = slotID;
                ob_phone = getSimID.invoke(telephony, obParameter);
            } catch (Exception e) {
                ////e.printStackTrace();
            }
            return ob_phone;
        }

        private boolean invokeMethodboolean(String className,
                                            int slotNumber, String methodName, String SIM_variant) {
            boolean val = false;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName
                            + SIM_variant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName
                                + SIM_variant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                    //e.printStackTrace();
                }

                if (ob_phone != null) {
                    val = (boolean) Boolean.parseBoolean(ob_phone.toString());
                }
            } catch (Exception e) {
                invokeOldMethod(className, slotNumber, methodName, SIM_variant);
            }

            return val;
        }

        public boolean isTelephonyClassExists(String className) {

            boolean isClassExists = false;
            try {
                Class<?> telephonyClass = Class.forName(className);
                isClassExists = true;
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return isClassExists;
        }

        /**
         * Here we are identify sim slot number
         */
        public void getValidSlotFields(String className) {

            String value = null;
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                StringBuffer sbf = new StringBuffer();
                Field[] fieldList = telephonyClass.getDeclaredFields();
                for (int index = 0; index < fieldList.length; index++) {
                    sbf.append("\n\n" + fieldList[index].getName());
                    Class<?> type = fieldList[index].getType();
                    Class<?> type1 = int.class;
                    if (type.equals(type1)) {
                        String variableName = fieldList[index].getName();
                        if (variableName.contains("SLOT")
                                || variableName.contains("slot")) {
                            if (variableName.contains("1")) {
                                slotName_1 = variableName;
                            } else if (variableName.contains("2")) {
                                slotName_2 = variableName;
                            } else if (variableName.contains("" + slotNumber_1)) {
                                slotName_1 = variableName;
                            } else if (variableName.contains("" + slotNumber_2)) {
                                slotName_2 = variableName;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        /**
         * Some device assign different slot number so here code execute
         * to get slot number
         */
        public void getSlotNumber(String className) {
            try {
                Class<?> c = Class.forName(className);
                Field fields1 = c.getField(slotName_1);
                fields1.setAccessible(true);
                slotNumber_1 = (Integer) fields1.get(null);
                Field fields2 = c.getField(slotName_2);
                fields2.setAccessible(true);
                slotNumber_2 = (Integer) fields2.get(null);
            } catch (Exception e) {
                slotNumber_1 = 0;
                slotNumber_2 = 1;
                // //e.printStackTrace();
            }
        }

        private String invokeMethod(String className, int slotNumber,
                                    String methodName, String SIM_variant) {
            String value = "";

            try {
                Class<?> telephonyClass = Class.forName(className);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName
                            + SIM_variant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName
                                + SIM_variant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    value = ob_phone.toString();
                }
            } catch (Exception e) {
                invokeOldMethod(className, slotNumber, methodName, SIM_variant);
            }

            return value;
        }

        private String invokeLongMethod(String className, long slotNumber,
                                        String methodName, String SIM_variant) {
            String value = "";

            try {
                Class<?> telephonyClass = Class.forName(className);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = long.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName
                            + SIM_variant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName
                                + SIM_variant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    value = ob_phone.toString();
                }
            } catch (Exception e) {

            }

            return value;
        }

        public String invokeOldMethod(String className, int slotNumber,
                                      String methodName, String SIM_variant) {
            String val = "";
            try {
                Class<?> telephonyClass = Class
                        .forName("android.telephony.TelephonyManager");
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName
                            + SIM_variant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName
                                + SIM_variant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    val = ob_phone.toString();
                }
            } catch (Exception e) {

            }
            return val;
        }

        /*
        *  GSM (IMEI), CDMA(MEID)
        * Return null if device ID is not available.
        */
        public String getDeviceIdBySlot(String predictedMethodName, int slotID) {

            String imei = null;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName,
                        parameter);
                Object[] obParameter = new Object[1];
                obParameter[0] = slotID;
                Object ob_phone = getSimID.invoke(obj, obParameter);

                if (ob_phone != null) {
                    imei = ob_phone.toString();
                }
            } catch (Exception e) {
                ////e.printStackTrace();
            }

            return imei;
        }

        public String getDeviceIdBySlotOld(String predictedMethodName, int slotID) {

            String value = null;
            try {
                Class<?> telephonyClass = Class.forName(telephony.getClass()
                        .getName());

                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName,
                        parameter);

                Object[] obParameter = new Object[1];
                obParameter[0] = slotID;
                Object ob_phone = getSimID.invoke(telephony, obParameter);

                if (ob_phone != null) {
                    value = ob_phone.toString();

                }
            } catch (Exception e) {
                ////e.printStackTrace();
            }

            return value;
        }
    }


}
