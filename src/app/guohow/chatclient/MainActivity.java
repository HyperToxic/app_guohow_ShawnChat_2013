package app.guohow.chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	int client_id = 100;
	private final int UPDATE_UI = 1;
	private static final int NOTIFICATION_ID = 0x1122;
	boolean receiveable = false;
	boolean notify_radio = false;
	boolean connected = false;
	boolean systemNotify = true;
	Socket s = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;

	Handler handler = null;
	Handler btnHandler = null;
	String str_rec = null;
	String str_send = null;

	String id = null;
	String host_ip = null;
	int host_soc = 9999;

	NotificationManager nm = null;
	Notification notify = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main);

		// 获取是否显示通知
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		notify_radio = spf.getBoolean("notification_perf", false);

		id = getId();
		host_ip = getIp();

		new Thread(new Connect()).start();
		new Thread(new Sent()).start();

		handler = new MyHandler();

	}

	public String getIp() {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		host_ip = spf.getString("ip_perf", "192.168.1.100");
		return host_ip;
	}

	public String getId() {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		id = spf.getString("userName_perf", "默认用户名");
		return id;
	}

	private void notifyInit() {

		Intent intent = new Intent();
		intent.setFlags(BIND_NOT_FOREGROUND);
		intent.setClass(MainActivity.this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0,
				intent, 0);
		notify = new Notification();
		notify.icon = R.drawable.notify_icon_middle;
		notify.tickerText = str_rec;
		notify.when = System.currentTimeMillis();
		notify.defaults = Notification.DEFAULT_SOUND;
		notify.defaults = Notification.DEFAULT_ALL;
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.flags = Notification.FLAG_ONLY_ALERT_ONCE;

		notify.setLatestEventInfo(MainActivity.this, "Chater正在运行", "用户:" + id,
				pi);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem about = menu.add("更多");
		about.setIntent(new Intent(this, SettingsActivity.class));

		return true;
	}

	public class MyHandler extends Handler {

		TextView msg_dis = (TextView) findViewById(R.id.msg_recv);


		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_UI: {

				msg_dis.append('\n' + str_rec);
				}
			}
		}
	}

	public class Connect implements Runnable {

		TextView tw1 = (TextView) findViewById(R.id.login_header);

		@Override
		public void run() {
			try {

				s = new Socket(host_ip, host_soc);
				tw1.setText("连接成功...");
				connected = true;
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
				dos.writeUTF("           " + id);
				//
				try {
					notifyInit();
					receiveable = true;
					while (receiveable) {
						str_rec = dis.readUTF();
						handler.sendEmptyMessage(UPDATE_UI);
						// 判断是否通知
						if (notify_radio && systemNotify) {
							nm.notify(NOTIFICATION_ID, notify);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public class Sent implements Runnable {

		EditText et1 = (EditText) findViewById(R.id.et1);

		@Override
		public void run() {

			et1.setOnKeyListener(new android.view.View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keycode, KeyEvent event) {
					if (KeyEvent.KEYCODE_ENTER == keycode
							&& event.getAction() == KeyEvent.ACTION_DOWN) {

						if (connected) {
							try {
								str_send = et1.getText().toString().trim();
								dos.writeUTF(str_send);
								et1.setText("");
								dos.flush();
								// handler.sendEmptyMessage(UPDATE_UI);

							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						// et1.setEnabled(false);
					}
					return false;
				}
			});
		}
	}
}
