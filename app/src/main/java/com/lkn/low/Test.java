package com.lkn.low;

import com.lkn.low.bean.ChessBoard;
import com.lkn.low.bean.ChessWalkBean;
import com.lkn.low.bean.Position;

import java.util.Map;
import java.util.Scanner;

public class Test {
	private ChessWalkBean walkBean = new ChessWalkBean();
	
	
	public static void main(String[] args) {
		System.out.println(Configure.getBlackWinSet().size());
		new Test().personWalk();
	}
	
	private void personWalk() {
		ChessBoard board = new ChessBoard();
		board.init();
		
		GamePlay play = new GamePlay();
		Scanner sc = new Scanner(System.in);
		while(true){
			board.show();
			System.out.println("请输入：");
			String line = sc.nextLine();
			String[] split = line.split(" ");
			personWalk(board, split[0], split[1]);
			play.computerWalk(board);
		}
	}
	
	private void personWalk(ChessBoard board, String beginStr, String endStr) {
		Map<String, Position> map = board.getPositionMap();
		Position begin = map.get(beginStr);
		Position end = map.get(endStr);
		walkBean.walkActual(begin, end, board);
		board.show();
	}
	
	/**
	 * 低效率走法
	 */
	private void personWalkLow() {
		ChessBoard board = new ChessBoard();
		board.init();
		
		GamePlayLow play = new GamePlayLow();
		Scanner sc = new Scanner(System.in);
		while(true){
			board.show();
			System.out.println("请输入：");
			String line = sc.nextLine();
			String[] split = line.split(" ");
			personWalk(board, split[0], split[1]);
			play.computerWalk(board);
		}
	}

}
