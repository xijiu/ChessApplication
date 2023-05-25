package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.util.Map;

/**
 * 车
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Rooks extends AbstractChessPiece {
	public static final int RED_NUM = 1;
	public static final int BLACK_NUM = 10;

	public Rooks(Role role) {
		this(null, role);
	}

	public Rooks(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(600);
		this.setName("车");
		this.setShowName("車");
		initNum(role, RED_NUM, BLACK_NUM);
	}

	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{ 14, 14, 12, 18, 16, 18, 12, 14, 14},
				{ 16, 20, 18, 24, 26, 24, 18, 20, 16},
				{ 12, 12, 12, 18, 18, 18, 12, 12, 12},
				{ 12, 18, 16, 22, 22, 22, 16, 18, 12},
				{ 12, 14, 12, 18, 18, 18, 12, 14, 12},
				{ 12, 16, 14, 20, 20, 20, 14, 16, 12},
				{  6, 10,  8, 14, 14, 14,  8, 10,  6},
				{  4,  8,  6, 14, 12, 14,  6,  8,  4},
				{ 10, 10, 10, 16,  8, 16, 10, 10, 10},
				{ -2, 10, 10, 14, 12, 14, 10, 10, -2}
		};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 1 : 8);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		byte num = getReachablePositions(position, board, false, 10)[0];
		int eatenVal = eatenValue(board, position);
//		int eatenVal = 0;
		return Math.max(0, defaultVal + arr[x][y] + num * 2 - eatenVal);
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			x = this.isRed() ? x + num : x - num;
		} else if (cmd1 == '退') {
			x = this.isRed() ? x - num : x + num;
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
		AbstractChessPiece piece = null;
		// 向上找
		for (int i = currX + 1; i < 10; i++) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece[i][currY]) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向下找
		for (int i = currX - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece[i][currY]) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向右找
		for (int y = currY + 1; y < 9; y++) {
			int position = ChessTools.toPosition(currX, y);
			if ((piece = allPiece[currX][y]) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向左找
		for (int y = currY - 1; y >= 0; y--) {
			int position = ChessTools.toPosition(currX, y);
			if ((piece = allPiece[currX][y]) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
	}

}
