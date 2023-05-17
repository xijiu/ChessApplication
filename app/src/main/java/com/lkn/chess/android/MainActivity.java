//package com.lkn.chess.android;
//
//import android.content.Context;
//import android.content.pm.ActivityInfo;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Display;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import com.lkn.chess.R;
//import com.lkn.low.bean.ChessBoard;
//
//public class MainActivity extends ActionBarActivity {
//	private PowerManager powerManager = null;
//	private WakeLock wakeLock = null;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        this.powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        this.wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	// 设置全屏
//		Display display = getWindowManager().getDefaultDisplay();
//		ChessBoard board = new ChessBoard();
//		board.init();
//		Point size = new Point();
//		display.getSize(size);
//		GlobalData.setHeight(size.y);
//		GlobalData.setWidth(size.x);
//		View vw = new GameView(this, board);
//		setContentView(vw);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		this.wakeLock.acquire();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		this.wakeLock.release();
//	}
//}
