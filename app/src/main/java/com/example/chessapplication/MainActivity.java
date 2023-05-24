package com.example.chessapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.PowerManager;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lkn.chess.android.GameView;
import com.lkn.chess.android.GlobalData;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.manual.Manual;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Window window = getWindow();
//        window.setGravity(Gravity.TOP);
//        window.setBackgroundDrawableResource(R.drawable.board);
//        getWindow().setBackgroundDrawableResource(R.drawable.background1);
        readManual();
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        this.powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	// 设置全屏
        Display display = getWindowManager().getDefaultDisplay();
        ChessBoard board = new ChessBoard();
        board.init();
        Point size = new Point();
        display.getSize(size);
        GlobalData.setHeight(size.y);
        GlobalData.setWidth(size.x);
        View vw = new GameView(this, board);
        setContentView(vw);
    }

    private void readManual() {
        try {
            AssetManager assets = this.getAssets();
            InputStream inputStream = assets.open("manual.chess");
            int size = inputStream.available();
            byte[] arr = new byte[size];
            int read = inputStream.read(arr);
            Manual.byteBuffer = ByteBuffer.wrap(arr);
            Manual.byteBuffer.position(arr.length);
            System.out.println("already load manual, size is :::: " + read);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(1, menu);
        return true;
    }
}
