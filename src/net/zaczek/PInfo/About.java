package net.zaczek.PInfo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView webview = new WebView(this);
		setContentView(webview);
		
		webview.loadUrl("file:///android_asset/html/about.html");
	}
}
