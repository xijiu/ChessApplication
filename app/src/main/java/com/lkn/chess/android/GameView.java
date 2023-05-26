package com.lkn.chess.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import com.example.chessapplication.R;
import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.DifficultyLevel;
import com.lkn.chess.GamePlayHigh;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;


public class GameView extends View {

	private int startX ;
	private int startY ;
	private final int LINE_NUM = 10;	// 中国象棋的棋盘共10行
	private final int COLUMN_NUM = 9;	// 中国象棋的棋盘共9列
	private int GRID_WIDTH = GlobalData.getWidth() / (COLUMN_NUM);	// 格子的长宽度
	private Paint paint = null;
	private Context context;
	private SoundPool soundPool;
	private ChessBoard board = null;
	private ChessBoard cloneBoard = null;
	private boolean isPlayerTurn = true;
	/** 玩家按下的旗子 */
	private AbstractChessPiece pressPiece = null;
	/** 玩下按下旗子的位置 */
	private int pressPiecePosition = -1;
	private boolean computerThinking = false;	// 电脑是否在思考中
	private int lastWalkBegin = -1;
	private int lastWalkEnd = -1;
	private boolean gameOver = false;
	private int soundID = 1;
	private Camera camera;
	private Matrix matrix;

	public void setPlayerTurn(boolean isPlayerTurn) {
		this.isPlayerTurn = isPlayerTurn;
	}

	public GameView(Context context, ChessBoard board) {
		super(context);
		camera = new Camera();
		matrix = new Matrix();
		this.context = context;
		soundPool = new SoundPool(5,AudioManager.STREAM_SYSTEM,5);
		soundID = soundPool.load(context, R.raw.go, 1);
		paint = new Paint();//实例化一个画�?
//		paint.setAntiAlias(false);//设置画笔去锯齿，没有此语句，画的线或图片周围不圆�?
		this.board = board;
		
		cloneBoard = board.clone();
		// 为了不让棋盘的边界与屏幕的边界完全重合，�?��让棋盘的边界离屏幕边界一定距离�?



		int totalWidth = GlobalData.getWidth();
		int totalHeight = GlobalData.getHeight();

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.board);
		int imgWidth = bitmap.getWidth();
		int imgHeight = bitmap.getHeight();
		int imgLeft = 50;
		int imgTop = 60;
		int imgGridWidth = 80;

		// x * 8 + x / 8 * 5 * 2 = totalWidth;
		System.out.println("origin GRID_WIDTH is " + GRID_WIDTH);
		System.out.println("origin totalWidth is " + totalWidth);
		GRID_WIDTH = (int) ((float) totalWidth / 9.25f) + 1;
		System.out.println("after GRID_WIDTH is " + GRID_WIDTH);


//		startX = GRID_WIDTH/2;
//		startY = GRID_WIDTH;

		startX = GRID_WIDTH / 8 * 5;
		startY = GRID_WIDTH / 8 * 6;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.board);

//		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		Rect dst = new Rect(0, 0, getWidth(), getHeight());


		int boardWidth = bitmap.getWidth();
		int boardHeight = bitmap.getHeight();

		Rect src = new Rect(0, 0, boardWidth, boardHeight);
		Rect dst = new Rect(0, 0, getWidth(), getWidth() * boardHeight / boardWidth);

		canvas.drawBitmap(bitmap, src, dst, null);


//		canvas.drawColor(0xFFF4E5C2);	//背景颜色
		paint.setColor(Color.BLACK);	//画笔颜色

//		drawMainLine(canvas);
//		drawOutsideLine(canvas);	// 为棋盘的外面画线
//		drawMandarinsLine(canvas);	// 画士的行走路线
//		paintFourCorner(canvas);
//		drawChessWord(canvas);	// 画楚河汉界
		drawChooseButton(canvas);	// 画选择难度的按钮
		
		// 使用maskFilter实现棋子的滤镜效果，使之看起来更有立体感�?
//		float[] dire = new float[]{1,1,1};  //光线方向
//		float light = 0.5f;   //光线强度
//		float spe = 6;
//		float blur = GRID_WIDTH/10;
//		EmbossMaskFilter emboss = new EmbossMaskFilter(dire, light, spe, blur);
//		paint.setMaskFilter(emboss);
		paintPieces(canvas);	// 绘制所有的棋子
	}


	private void drawMainLine(Canvas canvas) {
		paint.setAntiAlias(true);//锯齿不显示
		// 画横线
		for(int i = 0; i < LINE_NUM ; i++) {
			canvas.drawLine(startX, startY+i*GRID_WIDTH, startX+(COLUMN_NUM - 1)*GRID_WIDTH, startY+i*GRID_WIDTH, paint);
		}
		// 画竖线
		for(int i = 0; i < COLUMN_NUM ; i++) {
			if(i == 0 || i == (COLUMN_NUM - 1)){
				canvas.drawLine(startX+i*GRID_WIDTH, startY, startX+i*GRID_WIDTH , startY+(LINE_NUM-1)*GRID_WIDTH, paint);
			} else {
				canvas.drawLine(startX+i*GRID_WIDTH, startY, startX+i*GRID_WIDTH , startY+4*GRID_WIDTH, paint);
				canvas.drawLine(startX+i*GRID_WIDTH, startY+5*GRID_WIDTH, startX+i*GRID_WIDTH , startY+(LINE_NUM-1)*GRID_WIDTH, paint);
			}
		}
	}

	/**
	 * 画选择难度的按钮
	 * @param canvas
	 */
	private void drawChooseButton(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);  
		paint.setColor(Color.BLACK);
		RectF rect = new RectF(startX + 1.25f*GRID_WIDTH, startY+11*GRID_WIDTH, startX + 2.75f*GRID_WIDTH, startY+11.5f*GRID_WIDTH);
		if (Conf.DIFFICULTY_LEVEL == DifficultyLevel.EASY) {
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
		} else {
			paint.setStyle(Paint.Style.STROKE);
		}
        canvas.drawRect(rect, paint);
		
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize((float) (GRID_WIDTH / 3.5));
		textPaint.setTypeface(Typeface.SANS_SERIF);
		FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
	    int baseline = (int)((startY + 11.25*GRID_WIDTH) * 2 - fontMetrics.bottom - fontMetrics.top) / 2;
		canvas.drawText("熊孩子", startX+2*GRID_WIDTH, baseline, textPaint);

		/****************************************************************************************************************************************/
		RectF rect2 = new RectF(startX + 3.25f*GRID_WIDTH, startY+11*GRID_WIDTH, startX + 4.75f*GRID_WIDTH, startY+11.5f*GRID_WIDTH);
		paint.setColor(Color.BLACK);
        if (Conf.DIFFICULTY_LEVEL == DifficultyLevel.MID) {
        	paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
        } else {
			paint.setStyle(Paint.Style.STROKE);
		}
		canvas.drawRect(rect2, paint);
        canvas.drawText("秃头大叔", startX+4*GRID_WIDTH, baseline, textPaint);
		/****************************************************************************************************************************************/
        RectF rect3 = new RectF(startX + 5.25f*GRID_WIDTH, startY+11*GRID_WIDTH, startX + 6.75f*GRID_WIDTH, startY+11.5f*GRID_WIDTH);
        paint.setColor(Color.BLACK);
        if (Conf.DIFFICULTY_LEVEL == DifficultyLevel.HARD) {
        	paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
		} else {
			paint.setStyle(Paint.Style.STROKE);
		}
        canvas.drawRect(rect3, paint);
        canvas.drawText("跛足老头", startX+6*GRID_WIDTH, baseline, textPaint);
	}
	/**
	 * 画楚河汉界四个汉字
	 * @param canvas
	 */
	private void drawChessWord(Canvas canvas) {
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(GRID_WIDTH / 2);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setTextSkewX(0.2f); //float类型参数，负数表示右斜，整数左斜
		
		FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();  
	    int baseline = (int)((startY + 4.5*GRID_WIDTH) * 2 - fontMetrics.bottom - fontMetrics.top) / 2;  
		canvas.drawText("楚河", startX+2*GRID_WIDTH, baseline, textPaint);
		textPaint.setTextSkewX(-0.2f); //float类型参数，负数表示右斜，整数左斜
		canvas.drawText("汉界", startX+6*GRID_WIDTH, baseline, textPaint);
	}
	/**
	 * 四角楼
	 * @param canvas
	 */
	private void paintFourCorner(Canvas canvas) {
		paintForSinglePoint(startX + 1 * GRID_WIDTH, startY + 2 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 7 * GRID_WIDTH, startY + 2 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 0 * GRID_WIDTH, startY + 3 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 2 * GRID_WIDTH, startY + 3 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 4 * GRID_WIDTH, startY + 3 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 6 * GRID_WIDTH, startY + 3 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 8 * GRID_WIDTH, startY + 3 * GRID_WIDTH, canvas);
		
		
		paintForSinglePoint(startX + 1 * GRID_WIDTH, startY + 7 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 7 * GRID_WIDTH, startY + 7 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 0 * GRID_WIDTH, startY + 6 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 2 * GRID_WIDTH, startY + 6 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 4 * GRID_WIDTH, startY + 6 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 6 * GRID_WIDTH, startY + 6 * GRID_WIDTH, canvas);
		paintForSinglePoint(startX + 8 * GRID_WIDTH, startY + 6 * GRID_WIDTH, canvas);
	}

	/**
	 * 画“士”的行走对角线
	 * @param canvas
	 */
	private void drawMandarinsLine(Canvas canvas) {
		int leftUpX1 = startX + GRID_WIDTH * 3;
		int leftUpY1 = startY;
		int rightUpX1 = startX + GRID_WIDTH * 5;
		int rightUpY1 = startY;
		int leftDownX1 = leftUpX1;
		int leftDownY1 = startY + GRID_WIDTH * 2;
		int rightDownX1 = rightUpX1;
		int rightDownY1 = leftDownY1;
		
		canvas.drawLine(leftUpX1, leftUpY1, rightDownX1, rightDownY1, paint);
		canvas.drawLine(leftDownX1, leftDownY1, rightUpX1, rightUpY1, paint);
		
		int leftUpX2 = leftUpX1;
		int leftUpY2 = startY + GRID_WIDTH * 7;
		int rightUpX2 = rightUpX1;
		int rightUpY2 = leftUpY2;
		int leftDownX2 = leftDownX1;
		int leftDownY2 = startY + GRID_WIDTH * 9;
		int rightDownX2 = rightDownX1;
		int rightDownY2 = leftDownY2;
		
		canvas.drawLine(leftUpX2, leftUpY2, rightDownX2, rightDownY2, paint);
		canvas.drawLine(leftDownX2, leftDownY2, rightUpX2, rightUpY2, paint);
	}
	
	/**
	 * 为一个坐标点绘制四角炮楼
	 * @param x
	 * @param y
	 * @param canvas
	 */
	private void paintForSinglePoint(int x, int y, Canvas canvas){
		int size = 4;
		int len = GRID_WIDTH / 4;
		if (x - size > startX) {
			// 左上角
			canvas.drawLine(x - size, y - size, x - size, y - size - len, paint);
			canvas.drawLine(x - size, y - size, x - size - len, y - size, paint);
			// 左下角
			canvas.drawLine(x - size, y + size, x - size, y + size + len, paint);
			canvas.drawLine(x - size, y + size, x - size - len, y + size, paint);
		}
		if(x + size < startX + GRID_WIDTH * 8){
			// 右上角
			canvas.drawLine(x + size, y - size, x + size, y - size - len, paint);
			canvas.drawLine(x + size, y - size, x + size + len, y - size, paint);
			// 右下角
			canvas.drawLine(x + size, y + size, x + size, y + size + len, paint);
			canvas.drawLine(x + size, y + size, x + size + len, y + size, paint);
		}
	}

	/**
	 * 画象棋棋盘外面的像素比较大的四方框
	 * @param canvas
	 */
	private void drawOutsideLine(Canvas canvas) {
		float tempPixel = paint.getStrokeWidth();
		int pixel = 4;	// 设置像素
		paint.setStrokeWidth(pixel);//设置画笔像素的宽度
		int leftUpX = startX - GRID_WIDTH / 4;
		int leftUpY = startY - GRID_WIDTH / 4;
		int rightUpX = 3 * leftUpX + (COLUMN_NUM - 1)*GRID_WIDTH;
		int rightUpY = leftUpY;
		int leftDownX = leftUpX;
		int leftDownY = leftUpY + 2 * (GRID_WIDTH / 4) + (LINE_NUM - 1)*GRID_WIDTH;
		int rightDownX = rightUpX;
		int rightDownY = leftDownY;
		
		canvas.drawLine(leftUpX, leftUpY - pixel / 2, leftDownX, leftDownY + pixel / 2, paint);
		canvas.drawLine(leftUpX - pixel / 2, leftUpY, rightUpX + pixel / 2, rightUpY, paint);
		canvas.drawLine(leftDownX - pixel / 2, leftDownY, rightDownX + pixel / 2, rightDownY, paint);
		canvas.drawLine(rightUpX, rightUpY - pixel / 2, rightDownX, rightDownY + pixel / 2, paint);
		paint.setStrokeWidth(tempPixel);//设置画笔像素的宽度
	}

	//重写View的监听触摸事件的方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isPlayerTurn) {
			return super.onTouchEvent(event);
		}
		
		float touchX = event.getX();
		float touchY = event.getY();
		boolean isComputerTurn = false;
		
		if((touchX < startX - GRID_WIDTH/2) || (touchX > startX+(COLUMN_NUM-1)*GRID_WIDTH + GRID_WIDTH/2) || (touchY < startY - GRID_WIDTH/2) || (touchY > startY + (LINE_NUM-1)*GRID_WIDTH + GRID_WIDTH/2)) {	//点击到棋盘以外的位置
			DifficultyLevel difficultyLevel = null;
			if(touchX >= (startX + 1.25f*GRID_WIDTH) && touchX <= (startX + 2.75f*GRID_WIDTH) && touchY >= (startY+11*GRID_WIDTH) && touchY <= (startY+11.5f*GRID_WIDTH)){	// 小白级
				difficultyLevel = DifficultyLevel.EASY;
			} else if(touchX >= (startX + 3.25f*GRID_WIDTH) && touchX <= (startX + 4.75f*GRID_WIDTH) && touchY >= (startY+11*GRID_WIDTH) && touchY <= (startY+11.5f*GRID_WIDTH)){
				difficultyLevel = DifficultyLevel.MID;
			} else if(touchX >= (startX + 5.25f*GRID_WIDTH) && touchX <= (startX + 6.75f*GRID_WIDTH) && touchY >= (startY+11*GRID_WIDTH) && touchY <= (startY+11.5f*GRID_WIDTH)){
				difficultyLevel = DifficultyLevel.HARD;
			}
			if (difficultyLevel != null) {
				final DifficultyLevel finalDifficultyLevel = difficultyLevel;
				new AlertDialog.Builder(context).setTitle("确认").setMessage("确定选择此难度，并重新开始游戏吗？" ).setPositiveButton("是", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	Conf.changeDifficulty(finalDifficultyLevel);
	    				reBeginGame();
	    				invalidate();
	                }
	            }).setNegativeButton("否" , null).show(); 
			}
		} else {
			if (gameOver) {
				return super.onTouchEvent(event);
			}
			//根据点击的位置，从�?获知在棋盘上的哪个位置，即是数组的脚�?
			int index_x = Math.round((touchX-startX)/GRID_WIDTH);
			int index_y = Math.round((touchY-startY)/GRID_WIDTH);

			int boardX = 9 - index_y;
			int boardY = index_x;

			int pressPosition = ChessTools.toPosition(boardX, boardY);

			if (pressPiece == null) {
				soundPool.play(soundID,1, 1, 0, 0, 1);
				firstPressPiece(pressPosition);	// 第一次按下某个棋子时
			} else {
				byte[] reachablePositions = pressPiece.getReachablePositions(pressPiecePosition, board, false, 10);
				byte size = reachablePositions[0];
				boolean valid = false;
				for (int i = 1; i <= size; i++) {
					byte targetPos = reachablePositions[i];
					if (targetPos == pressPosition) {
						valid = true;
						break;
					}
				}

				if (valid) {
					board.walk(pressPiecePosition, pressPosition);
					lastWalkBegin = pressPiecePosition;
					lastWalkEnd = pressPosition;
					soundPool.play(soundID,1, 1, 0, 0, 1);//播放下棋声音
					judgeGameOver();	// 判断游戏是否结束
					isPlayerTurn = false;
					isComputerTurn = true;
					pressPiece = null;
					cloneBoard = board.clone();
				} else {	// 按照第一次按下某个棋子执行
					soundPool.play(soundID,1, 1, 0, 0, 1);
					firstPressPiece(pressPosition);	// 第一次按下某个棋子时
				}
			}
		}
		invalidate();	//点击完成后，通知重绘即再次执行onDraw方法
		if (isComputerTurn) {
			computerThinking = true;
			new LongTimeTask().execute();
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 重新开始游戏，有一些参数需要被初始化
	 */
	private void reBeginGame(){
		board.init();
		lastWalkBegin = -1;
		lastWalkEnd = -1;
		pressPiece = null;
		pressPiecePosition = -1;
		cloneBoard = board.clone();
		isPlayerTurn = true;
		gameOver = false;
	}
	
	/**
	 * 判断游戏是否结束
	 */
	private void judgeGameOver() {
		boolean redKingExist = false;
		boolean blackKingExist = false;
		AbstractChessPiece[][] allPiece = board.getAllPiece();
		for (AbstractChessPiece[] abstractChessPieces : allPiece) {
			for (AbstractChessPiece piece : abstractChessPieces) {
				if (piece == null) {
					continue;
				}
				if (piece.getName().equals("帅")) {
					redKingExist = true;
				}
				if (piece.getName().equals("将")) {
					blackKingExist = true;
				}
			}
		}
		if (redKingExist && blackKingExist) {
			return;
		}
		String msg = null;
		if (redKingExist) {
			msg = "恭喜，你赢了！";
		} else {
			msg = "很遗憾，你输了";
		}
		new AlertDialog.Builder(context).setTitle("提示").setMessage(msg).setPositiveButton("确定" , null ).show();
//		reBeginGame();
		gameOver = true;
	}
	
	private void firstPressPiece(int pressPosition) {
		AbstractChessPiece piece = ChessTools.getPiece(board.getAllPiece(), pressPosition);
		if (piece != null && piece.isRed()) {
			pressPiece = piece;
			pressPiecePosition = pressPosition;
		}
	}

	private GamePlayHigh play = new GamePlayHigh();
	private class LongTimeTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result){
			judgeGameOver();
			computerThinking = false;
			invalidate();
			setPlayerTurn(true);
		}

		@Override
		protected Object doInBackground(Object... params) {
			if (gameOver) {
				return null;
			}
			int[] walkPath = play.computerWalk(board);
			lastWalkBegin = walkPath[0];
			lastWalkEnd = walkPath[1];
			soundPool.play(soundID,1, 1, 0, 0, 1);//播放下棋声音
			cloneBoard = board.clone();
			return null;
		}
    }
	
	/**
	 * 绘制所有的棋子
	 */
	private void paintPieces(Canvas canvas) {
		AbstractChessPiece[][] allPiece = cloneBoard.getAllPiece();
		for(int x = 0; x < LINE_NUM; x++) {
			for(int y = 0; y < COLUMN_NUM; y++) {
				int position = ChessTools.toPosition(x, y);
				drawComputerWalkFoot(position, x, y, canvas);
				AbstractChessPiece piece = allPiece[x][y];
				if (piece != null) {
					drawPiece(piece, x, y, canvas);
				}
			}
		}
	}
	
	
	private void drawComputerWalkFoot(int position, int x, int y, Canvas canvas) {
		x = 9 - x;
		position = ChessTools.toPosition(x, y);
		x = 9 - x;
		if (position == lastWalkBegin || position == lastWalkEnd){
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			int size = 20;
			PathEffect effects = new DashPathEffect(new float[]{size,size,size,size},1);
			paint.setPathEffect(effects);
			RectF rect = new RectF(startX+y*GRID_WIDTH - GRID_WIDTH/2 + 3, startY+x*GRID_WIDTH - GRID_WIDTH/2 + 3, startX+y*GRID_WIDTH + GRID_WIDTH/2 - 3, startY+x*GRID_WIDTH + GRID_WIDTH/2 - 3);
			canvas.drawRect(rect, paint);
//			canvas.drawCircle(startX+j*GRID_WIDTH, startY+i*GRID_WIDTH, GRID_WIDTH/2, paint);
		}
	}

	private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG); {
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(GRID_WIDTH / 2);
	}

	/**
	 * 画棋子
	 */
	private void drawPiece(AbstractChessPiece piece, int x, int y, Canvas canvas) {
		x = 9 - x;

		// 设置字体颜色
		if (piece.isRed()) {
			textPaint.setColor(Color.RED);
		} else {
			textPaint.setColor(Color.BLACK);
		}
		textPaint.setTypeface(Typeface.MONOSPACE);

		// 设置棋子背景颜色
		if (pressPiece != null && pressPiecePosition == ChessTools.toPosition(9 - x, y)) {
			paint.setColor(Color.rgb(237, 105, 53));
		} else {
			paint.setColor(Color.rgb(209, 208, 235));
		}

		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
//		paint.setColor(Color.WHITE);
//		paint.setColor(Color.rgb(209, 208, 235));
		canvas.drawCircle(startX + y * GRID_WIDTH, startY + x * GRID_WIDTH, GRID_WIDTH/2 - 3, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.5f);
		paint.setAntiAlias(true);
		paint.setColor(piece.isRed() ? Color.RED : Color.BLACK);
		canvas.drawCircle(startX + y * GRID_WIDTH, startY + x * GRID_WIDTH, GRID_WIDTH/2 - 3, paint);

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.5f);
		paint.setAntiAlias(true);
		paint.setColor(piece.isRed() ? Color.RED : Color.BLACK);
		canvas.drawCircle(startX + y * GRID_WIDTH, startY + x * GRID_WIDTH, GRID_WIDTH/2 - 3 - 8, paint);

		
		FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();  
	    int baseline = (startY + x * GRID_WIDTH + startY + x * GRID_WIDTH - fontMetrics.bottom - fontMetrics.top) / 2;
		canvas.drawText(piece.getShowName(), startX + y * GRID_WIDTH, baseline, textPaint);
	}
}
