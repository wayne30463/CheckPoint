package com.zxing.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import AudioPlayer.AudioPlayer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import cp.search.R;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements Callback {

	private String uri = "/Checkpoint_Server/SearchLogAdd";
	TextView msg_view;
	String server_ipStr;
	Button intent_Cam;
    AudioPlayer ap;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 1.0f;
	private boolean vibrate;
	protected static final int REFRESH_DATA = 0x00000001;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		msg_view = (TextView)findViewById(R.id.msg_view);
		Intent intent = this.getIntent();
		inactivityTimer = new InactivityTimer(this);
		this.server_ipStr = intent.getStringExtra("server_ip");  
		uri = "http://" + this.server_ipStr + uri;
        Log.e("uri:", uri);
        initBeepSound();
    	ap = new AudioPlayer("21014334108.mp3", this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = false;
		/*
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}*/
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		String resultString = result.getText();
		//FIXME
		if (resultString.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			System.out.println("Result:"+resultString);
             final String[][] msg = {
										{"code", resultString}
									};
             Log.e("get_code", resultString);
             Log.e("uri", this.uri);
             if(msg_view.getText().equals("連線中..."))
            	 return;
     		//playBeepSoundAndVibrate();
             msg_view.setText("連線中...");
             Thread t1 = new Thread(new sendPostRunnable(this.uri, msg));
             t1.start();
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	/** 建立UI Thread使用的Handler，來接收其他Thread來的訊息 */
	Handler PostHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 顯示網路上抓取的資料
			case REFRESH_DATA:
				String result = null;
				if (msg.obj instanceof String)
					result = (String) msg.obj;
				if (result != null)
				{
					// 印出網路回傳的文字
					msg_view.setText(result);
		        	Log.e("RETURN", result);
					if(result.compareTo("check_out") == 0){
						ap.playAudio();
						viewfinderView.maskColor = Color.GREEN;
					}
					else if(result.compareTo("check_in") == 0){
						ap.playAudio();
						viewfinderView.maskColor = Color.RED;
					}
					else{
						Resources resources = getResources();
						viewfinderView.maskColor = resources.getColor(R.color.viewfinder_mask);
					}
					viewfinderView.drawViewfinder();
				}
				else
				{
		        	Log.e("RETURN", "false");
		           	msg_view.setText("連線失敗...");
					//Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};
	class sendPostRunnable implements Runnable
	{
		private String[][] strTxt = null;
		private String uri = null;

		// 建構子，設定要傳的字串
		public sendPostRunnable(String uri, String[][] strTxt)
		{
			this.strTxt = strTxt;
			this.uri = uri;
		}

		@Override
		public void run()
		{
			String result = sendPostDataToInternet(this.uri, this.strTxt);
			PostHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
		}

	}

	public static String sendPostDataToInternet(String uri, String[][] strTxt)
	{

		/* 建立HTTP Post連線 */
		HttpPost httpRequest = new HttpPost(uri);
		/*
		 * Post運作傳送變數必須用NameValuePair[]陣列儲存
		 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(int i=0;i<strTxt.length;i++)
			params.add(new BasicNameValuePair(strTxt[i][0], strTxt[i][1]));
		try 
		{

			/* 發出HTTP request */

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 取得HTTP response */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若狀態碼為200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
				/* 取出回應字串 */
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				// 回傳回應字串
				return strResult;
			}
			String strResult = EntityUtils.toString(httpResponse
					.getEntity());
			// 回傳回應字串
			return strResult;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}