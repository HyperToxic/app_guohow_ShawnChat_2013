package app.guohow.chatclient;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ServerActivity extends Activity {

	Switch server_switch = null;
	TextView server_info = null;
	TextView server_list = null;
	boolean serState = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// ������
		server_switch = (Switch) findViewById(R.id.server_switch);
		server_info = (TextView) findViewById(R.id.server_info);
		server_list = (TextView) findViewById(R.id.server_list);
		// �½�һ��AM��
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// ����AM������ȡ���з����б�
		List<ActivityManager.RunningServiceInfo> serList = am
				.getRunningServices(50);
		// �жϱ��ط����Ƿ��������б�
		final String myServiceName = "app.guohow.chatclient.HostService";
		serState = myServiceStarted(serList, myServiceName);

		if (serState) {
			server_switch.setChecked(true);
			server_list.setText(testGetList(serList));
		} else {
			server_info.setText(" ����δ����,�������ؿɿ���");
		}

		// ����Ҫ�����ķ���
		final Intent intent = new Intent();
		intent.setAction("app.guohow.chatclient.HOST_SERVICE");
		// ��ʼ����switch
		server_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (server_switch.isChecked()) {

					startService(intent);
					Toast.makeText(ServerActivity.this, "��ʾ:Chat���������ѿ���",
							Toast.LENGTH_SHORT).show();
					server_info.setText("  �����˿�:9999" + "\n" + "  ����״̬:�ѿ���");

					// ���»�ȡ�б�
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningServiceInfo> serList3 = am
							.getRunningServices(50);
					server_list.setText(testGetList(serList3));

				} else {
					// ֹͣ����
					stopService(intent);
					Toast.makeText(ServerActivity.this, "��ʾ:Chat���������ѹر�",
							Toast.LENGTH_SHORT).show();
					server_info.setText("  ����״̬:�ѹر�");

					// ��ȡ�б�,��ʾ��TW�����
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningServiceInfo> serList2 = am
							.getRunningServices(50);
					server_list.setText(testGetList(serList2));
				}
			}
		});
	}

	private boolean myServiceStarted(
			List<ActivityManager.RunningServiceInfo> serList, String className) {
		
		//�������
		for (int i = 0; i < serList.size(); i++) {

			if (className.equalsIgnoreCase(serList.get(i).service
					.getClassName())) {

				return true;
			}
		}
		return false;
	}

	// ��ӡ��ϵͳ���з���
	public String testGetList(List<ActivityManager.RunningServiceInfo> serList1) {
		String tmp = "    system services ongoing------>";

		for (int i = 0; i < serList1.size(); i++) {

			//׷���ı�,Ҳ����append()
			tmp = tmp + "\n    " + serList1.get(i).service.getClassName();
		}
		return tmp;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this,
					new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}