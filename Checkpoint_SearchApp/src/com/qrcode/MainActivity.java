package com.qrcode;


import Preferences.Preferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.zxing.activity.CaptureActivity;
import cp.search.R;

public class MainActivity extends Activity {
	private EditText server_ip;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		server_ip = (EditText)findViewById(R.id.server_ip);
		String server_ipStr = Preferences.getConfig(MainActivity.this,
                "Config",
                "server_ip",
                "");
		server_ip.setText(server_ipStr);
		        
        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	String server_ipStr = String.valueOf(server_ip.getText());
            	Preferences.setConfig(MainActivity.this,"Config","server_ip",server_ipStr);
            	//new一個intent物件，並指定Activity切換的class
				Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
				openCameraIntent.putExtra("server_ip", server_ipStr + ":8080");//可放所有基本類別
				startActivityForResult(openCameraIntent, 0);
			}
		});
    }
}