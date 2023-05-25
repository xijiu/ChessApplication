package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.util.Map;

/**
 * 兵
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Pawns extends AbstractChessPiece {
	public static final int RED_NUM = 7;
	public static final int BLACK_NUM = 8;
	public static final int BASE_VAL = 30;

	public Pawns(Role role) {
		this(null, role);
	}

	public Pawns(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(BASE_VAL);
		if(role == Role.RED) {	// 先手
			this.setName("兵");
			this.setShowName("兵");
		}else {
			this.setName("卒");
			this.setShowName("卒");
		}
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  0,  3,  6,  9, 12,  9,  6,  3,  0},
				{ 18, 36, 56, 80,120, 80, 56, 36, 18},
				{ 14, 26, 42, 60, 80, 60, 42, 26, 14},
				{ 10, 20, 30, 34, 40, 34, 30, 20, 10},
				{ 10, 12, 18, 18, 20, 18, 18, 12, 10},
				{  6,  0,  8,  0, 10,  0,  8,  0,  6},
				{  6,  0,  8,  0, 10,  0,  8,  0,  6},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 7 : 14);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);

		int eatenVal = eatenValue(board, position);
//		int eatenVal = 0;
		return Math.max(0, defaultVal + arr[x][y] - eatenVal);
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			x = this.isRed() ? x + num : x - num;
		} else if (cmd1 == '平') {
			y = ChessTools.transLineToY(cmd2, this.getPLAYER_ROLE());
		} else {
			throw new RuntimeException();
		}
		return ChessTools.toPosition(x, y);
	}

	@Override
	public byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level) {
		reachablePositions = reachableHelper[level];
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		findReachablePositions(currX, currY, board.getAllPiece(), containsProtectedPiece);
		reachablePositions[0] = (byte) reachableNum;
		return reachablePositions;
	}

	private void findReachablePositions(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		if (isRed()) {
			if (currX <= 4) {
				tryReach(currX + 1, currY, allPiece, containsProtectedPiece);
			} else {
				tryReach(currX + 1, currY, allPiece, containsProtectedPiece);
				tryReach(currX, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX, currY + 1, allPiece, containsProtectedPiece);
			}
		} else {
			if (currX >= 5) {
				tryReach(currX - 1, currY, allPiece, containsProtectedPiece);
			} else {
				tryReach(currX - 1, currY, allPiece, containsProtectedPiece);
				tryReach(currX, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX, currY + 1, allPiece, containsProtectedPiece);
			}
		}
	}

	private void tryReach(int x, int y, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		if (!isValid(x, y)) {
			return;
		}
		AbstractChessPiece piece = allPiece[x][y];
		if (piece == null || isEnemy(this, piece)) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
		if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
	}
}
