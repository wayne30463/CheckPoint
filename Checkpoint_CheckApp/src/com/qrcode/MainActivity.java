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
import cp.check.R;

public class MainActivity extends Activity {
	private EditText server_ip,client_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		server_ip = (EditText)findViewById(R.id.server_ip);
		client_id = (EditText)findViewById(R.id.client_id);
		String server_ipStr = Preferences.getConfig(MainActivity.this,
                "Config",
                "server_ip",
                "");
		String client_idStr = Preferences.getConfig(MainActivity.this,
		                "Config",
		                "client_id",
		                "");
		server_ip.setText(server_ipStr);
		client_id.setText(client_idStr);
		        
        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

            	String server_ipStr = String.valueOf(server_ip.getText());
            	String client_idStr = String.valueOf(client_id.getText());
            	if(client_idStr.length() == 0)
            		client_idStr = "00";
            	else if(client_idStr.length() < 2)
            		client_idStr = "0" + client_idStr;
            	else
            		client_idStr = client_idStr.substring(0, 2);
            	Preferences.setConfig(MainActivity.this,"Config","server_ip",server_ipStr);
            	Preferences.setConfig(MainActivity.this,"Config","client_id",client_idStr);
            	//new一個intent物件，並指定Activity切換的class
				Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
				openCameraIntent.putExtra("server_ip", server_ipStr + ":8080");//可放所有基本類別
				openCameraIntent.putExtra("client_id", client_idStr);
				startActivityForResult(openCameraIntent, 0);
			}
		});
    }
}