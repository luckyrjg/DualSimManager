package org.rjg.multisim;



import android.os.Bundle;

import android.app.Activity;

import android.telephony.TelephonyManager;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView	tv;
	static boolean		isDouble;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv = (TextView) findViewById(R.id.text);

        String mtyb = android.os.Build.BRAND; 
		String mtype = android.os.Build.MODEL;  
		
		// for DualSimManager
        DualSimManager info = new DualSimManager(this);

        String imsiSIM1 = info.getIMSI(0);
        String imsiSIM2 = info.getIMSI(1);

        String imeiSIM1 = info.getIMEI(0);
        String imeiSIM2 = info.getIMEI(1);

        boolean isSIM1Ready = info.isFirstSimActive();
        boolean isSIM2Ready = info.isSecondSimActive();

        String operatorSIM1 = info.getNETWORK_OPERATOR_NAME(0);
        String operatorSIM2 = info.getNETWORK_OPERATOR_NAME(1);
        String NetworkSIM1 = info.getNETWORK_TYPE(0);
        String NetworkSIM2 = info.getNETWORK_TYPE(1);


        isDouble = info.isDualSIMSupported();

        tv.setText(" Brand: " + mtyb + "\n" +
                " Model: " + mtype + "\n" +
                " Version: Android " + android.os.Build.VERSION.RELEASE + "\n" +
                " Support Dual Sim : " + isDouble + "\n" +
                " IMSI1 : " + imsiSIM1 + "\n" +
                " IMSI2 : " + imsiSIM2 + "\n" +
                " IMEI1 : " + imeiSIM1 + "\n" +
                " IMEI2 : " + imeiSIM2 + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n" +
                " Network SIM1 : " + NetworkSIM1 + "\n" +
                " Network SIM2 : " + NetworkSIM2 + "\n" +
                " OperatorSIM1 : " + operatorSIM1 + "\n" +
                " OperatorSIM2 : " + operatorSIM2 + "\n" );

  		
	}
	
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



}
