package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 马
 * @author:likn1	Jan 5, 2016  3:54:47 PM
 */
public class Horse extends AbstractChessPiece {
	public static final int RED_NUM = 2;
	public static final int BLACK_NUM = 11;

	public Horse(Role role) {
		this(null, role);
	}
	
	public Horse(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(270);
		this.setName("马");
		this.setShowName("馬");
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  4,  8, 16, 12,  4, 12, 16,  8,  4},
				{  4, 10, 28, 16,  8, 16, 28, 10,  4},
				{ 12, 14, 16, 20, 18, 20, 16, 14, 12},
				{  8, 24, 18, 24, 20, 24, 18, 24,  8},
				{  6, 16, 14, 18, 16, 18, 14, 16,  6},
				{  4, 12, 16, 14, 12, 14, 16, 12,  4},
				{  2,  6,  8,  6, 10,  6,  8,  6,  2},
				{  4,  2,  8,  8,  4,  8,  8,  2,  4},
				{  0,  2,  4,  4,-20,  4,  4,  2,  0},
				{  0, -4,  0,  0,  0,  0,  0, -4,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 2 : 9);
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int targetY = ChessTools.transLineToY(cmd2, this.isRed() ? Role.RED : Role.BLACK);
		if (cmd1 == '进') {
			if (this.isRed()) {
				if (Math.abs(y - targetY) == 1) {
					x = x + 2;
				} else {
					x = x + 1;
				}
			} else {
				if (Math.abs(y - targetY) == 1) {
					x = x - 2;
				} else {
					x = x - 1;
				}
			}
		} else if (cmd1 == '退') {
			if (this.isRed()) {
				if (Math.abs(y - targetY) == 1) {
					x = x - 2;
				} else {
					x = x - 1;
				}
			} else {
				if (Math.abs(y - targetY) == 1) {
					x = x + 2;
				} else {
					x = x + 1;
				}
			}
		} else {
			throw new RuntimeException();
		}
		return ChessTools.toPosition(x, targetY);
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

	/**
	 * 8个方向均需要检查
	 */
	private void findReachablePositions(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		int targetX = currX - 1;
		int targetY = currY - 2;
		int legX = currX;
		int legY = currY - 1;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX + 1;
		targetY = currY + 2;
		legX = currX;
		legY = currY + 1;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX - 2;
		targetY = currY - 1;
		legX = currX - 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX + 2;
		targetY = currY + 1;
		legX = currX + 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX - 1;
		targetY = currY + 2;
		legX = currX;
		legY = currY + 1;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX - 2;
		targetY = currY + 1;
		legX = currX - 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX + 1;
		targetY = currY - 2;
		legX = currX;
		legY = currY - 1;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);

		targetX = currX + 2;
		targetY = currY - 1;
		legX = currX + 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, allPiece, containsProtectedPiece);
	}

	private void tryJump(int targetX, int targetY, int legX, int legY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		if (isValid(targetX, targetY) && isValid(legX, legY)) {
			// 不拌马腿
			if (allPiece[legX][legY] == null) {
				AbstractChessPiece targetPiece = allPiece[targetX][targetY];
				if (targetPiece == null || isEnemy(this, targetPiece)) {
					recordReachablePosition(ChessTools.toPosition(targetX, targetY));
				}
				if (targetPiece != null && isFriend(this, targetPiece) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(targetX, targetY));
				}
			}
		}
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);

		byte num = getReachablePositions(position, board, false, 10)[0];

		int eatenVal = eatenValue(board, position);
//		int eatenVal = 0;
		return Math.max(0, 250 + arr[x][y] + num * 5 - eatenVal);
	}


}
