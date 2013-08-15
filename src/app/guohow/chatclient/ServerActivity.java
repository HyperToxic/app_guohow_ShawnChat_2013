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
		// 获得组件
		server_switch = (Switch) findViewById(R.id.server_switch);
		server_info = (TextView) findViewById(R.id.server_info);
		server_list = (TextView) findViewById(R.id.server_list);
		// 新建一个AM类
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 调用AM方法获取运行服务列表
		List<ActivityManager.RunningServiceInfo> serList = am
				.getRunningServices(50);
		// 判断本地服务是否在上述列表
		final String myServiceName = "app.guohow.chatclient.HostService";
		serState = myServiceStarted(serList, myServiceName);

		if (serState) {
			server_switch.setChecked(true);
			server_list.setText(testGetList(serList));
		} else {
			server_info.setText(" 服务未开启,抚摸开关可开启");
		}

		// 设置要启动的服务
		final Intent intent = new Intent();
		intent.setAction("app.guohow.chatclient.HOST_SERVICE");
		// 开始监听switch
		server_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (server_switch.isChecked()) {

					startService(intent);
					Toast.makeText(ServerActivity.this, "提示:Chat主机服务已开启",
							Toast.LENGTH_SHORT).show();
					server_info.setText("  主机端口:9999" + "\n" + "  运行状态:已开启");

					// 重新获取列表
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningServiceInfo> serList3 = am
							.getRunningServices(50);
					server_list.setText(testGetList(serList3));

				} else {
					// 停止服务
					stopService(intent);
					Toast.makeText(ServerActivity.this, "提示:Chat主机服务已关闭",
							Toast.LENGTH_SHORT).show();
					server_info.setText("  服务状态:已关闭");

					// 获取列表,显示在TW组件上
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
		
		//挨个检查
		for (int i = 0; i < serList.size(); i++) {

			if (className.equalsIgnoreCase(serList.get(i).service
					.getClassName())) {

				return true;
			}
		}
		return false;
	}

	// 打印出系统运行服务
	public String testGetList(List<ActivityManager.RunningServiceInfo> serList1) {
		String tmp = "    system services ongoing------>";

		for (int i = 0; i < serList1.size(); i++) {

			//追加文本,也可用append()
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