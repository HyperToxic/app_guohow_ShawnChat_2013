package app.guohow.chatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		final Intent intent = new Intent();
		intent.setClass(Splash.this, MainActivity.class);
		startActivity(intent);
		Splash.this.finish();
	}
}

// need to be finish
// guohow@2013