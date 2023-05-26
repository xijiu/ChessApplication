package com.example.chessapplication;

import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.lkn.chess.android.GameView;
import com.lkn.chess.android.GlobalData;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.manual.Manual;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        readManual();
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(1, menu);
        return true;
    }
}
