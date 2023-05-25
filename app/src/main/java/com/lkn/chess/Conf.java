package com.lkn.chess;

import java.io.File;

/**
 * 配置类
 * @author LiKangNing
 *
 */
public class Conf {
	private static File chessFile = new File(System.getProperty("user.dir") + "/ChessSourceFile.txt");	// 棋谱文件
	public static Integer THINK_DEPTH = 3;	// 考虑深度
	/** 是否简单计算棋子的价值 */
	public static final boolean SIMPLE_VALUE = false;
	public final static int GAME_PLAY_MAX_VAL = 100000;
	public final static int GAME_PLAY_MIN_VAL = -GAME_PLAY_MAX_VAL;
	

	public static File getChessFile() {
		return chessFile;
	}
}
