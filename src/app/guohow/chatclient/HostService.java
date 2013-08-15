package app.guohow.chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class HostService extends Service {

	String ipAddress = null;
	String port = null;
	boolean quit = false;
	ServerSocket ss = null;
	List<Client> clients = new ArrayList<Client>();

	// 必须重写的方法
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 开始服务时调用
	@Override
	public void onCreate() {
		new Thread(new serverThread()).start();
	}

	public class serverThread implements Runnable {
		@Override
		public void run() {
			startService();
		}
	}

	public void startService() {
		try {
			ss = new ServerSocket(9999);
			while (!quit) {
				Socket s = ss.accept();
				Client c = new Client(s); // 把当前接收到的Socket传递给一个线程
				new Thread(c).start();
				clients.add(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 包装应对客户端线程处理类
	private class Client implements Runnable {

		Socket s = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		boolean beConnected = false;

		public Client(Socket s2) {
			this.s = s2; // 通过构造器把传进来的soc传递给run方法
			beConnected = true; // 确定可以开始执行读取循环
		}

		@Override
		public void run() {
			String client_id = null;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());

				while (beConnected) {
					String str = dis.readUTF();
					for (int i = 0; i < clients.size(); i++) {

						if (str.startsWith("          ")) {

							client_id = str.trim();
							Client c = clients.get(i);
							c.sent("友情提醒:" + str.trim() + "上线了!");

						} else {

							Client c = clients.get(i);
							if (client_id == null) {
								c.sent("我说:" + str);

							} else {
								c.sent(client_id.trim() + ":" + str);
							}
						}
					}
				}

			} catch (EOFException eof) {

				for (int i = 0; i < clients.size(); i++) {
					Client c = clients.get(i);
					c.sent(client_id + "下线了...");
				}
			} catch (IOException e) {

				e.printStackTrace();
			} finally {

				try {
					dis.close();
					s.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}

		private void sent(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	public String getId() {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		String id = spf.getString("userName_perf", "默认用户名");
		return id;
	}

	public void onDestory() {
		super.onDestroy();
		this.quit = true;
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
