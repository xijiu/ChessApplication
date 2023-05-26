package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

/**
 * 象
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Elephants extends AbstractChessPiece {
	public static final int RED_NUM = 3;
	public static final int BLACK_NUM = 12;

	public Elephants(Role role) {
		this(null, role);
	}

	public Elephants(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(100);
		if (role == Role.RED) {    // 先手
			this.setName("相");
			this.setShowName("相");
		} else {
			this.setName("象");
			this.setShowName("象");
		}
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,-10,  0,  0,  0,-10,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{ -10, 0,  0,  0, 20,  0,  0,  0,  -10},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 3 : 10);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (Conf.SIMPLE_VALUE) {
			return defaultVal + arr[x][y];
		}
		int eatenVal = eatenValue(board, position);
		return Math.max(0, defaultVal + arr[x][y] - eatenVal);
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			if (this.isRed()) {
				x = x + 2;
				y = 9 - num;
			} else {
				x = x - 2;
				y = num - 1;
			}
		} else if (cmd1 == '退') {
			if (this.isRed()) {
				x = x - 2;
				y = 9 - num;
			} else {
				x = x + 2;
				y = num - 1;
			}
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


	/**
	 * 检查4个点，并查看是否堵象眼了
	 */
	private void findReachablePositions(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		int tmpX = currX - 2;
		int tmpY = currY - 2;
		if (isValid(tmpX, tmpY) && isInOwnArea(tmpX)) {
			if (allPiece[currX - 1][currY - 1] == null) {
				AbstractChessPiece piece = allPiece[tmpX][tmpY];
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
				if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}

		tmpX = currX + 2;
		tmpY = currY + 2;
		if (isValid(tmpX, tmpY) && isInOwnArea(tmpX)) {
			if (allPiece[currX + 1][currY + 1] == null) {
				AbstractChessPiece piece = allPiece[tmpX][tmpY];
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
				if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}

		tmpX = currX - 2;
		tmpY = currY + 2;
		if (isValid(tmpX, tmpY) && isInOwnArea(tmpX)) {
			if (allPiece[currX - 1][currY + 1] == null) {
				AbstractChessPiece piece = allPiece[tmpX][tmpY];
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
				if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}


		tmpX = currX + 2;
		tmpY = currY - 2;
		if (isValid(tmpX, tmpY) && isInOwnArea(tmpX)) {
			if (allPiece[currX + 1][currY - 1] == null) {
				AbstractChessPiece piece = allPiece[tmpX][tmpY];
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
				if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}
	}

	private boolean isInOwnArea(int x) {
		if (isRed()) {
			return x <= 4;
		} else {
			return x >= 5;
		}
	}

}
