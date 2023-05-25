package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

/**
 * 炮
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Cannons extends AbstractChessPiece {
	public static final int RED_NUM = 6;
	public static final int BLACK_NUM = 9;

	public Cannons(Role role) {
		this(null, role);
	}
	
	public Cannons(String id, Role role) {
		super(id, role);
		this.setDefaultVal(240);
		setValues();
		this.setName("炮");
		this.setShowName("炮");
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  6,  4,  0,  0,  0,  0,  0,  4,  6},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0, 20,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  4,  0,  6,  6, 20,  6,  6,  0,  4},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}


	@Override
	public byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level) {
		reachablePositions = reachableHelper[level];
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		addAllCase(currX, currY, board.getAllPiece(), containsProtectedPiece);
		reachablePositions[0] = (byte) reachableNum;
		return reachablePositions;
	}

	/**
	 * 炮跟将中间如果没有棋子，那么威力将大增
	 */
	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (Conf.SIMPLE_VALUE) {
			return defaultVal + arr[x][y];
		}
		int hollowVal = calcHollow(x, y, board.getAllPiece());
		int eatenVal = eatenValue(board, position);
		return Math.max(0, defaultVal + hollowVal + arr[x][y] - eatenVal);
	}

	/**
	 * 计算是否存在空心炮，以及其威力
	 */
	private int calcHollow(int currX, int currY, AbstractChessPiece[][] allPiece) {
		int targetPos = findKingPositionByName(allPiece);
		int targetX = ChessTools.fetchX(targetPos);
		int targetY = ChessTools.fetchY(targetPos);

		if (targetY == currY) {
			int beginX = Math.min(targetX, currX);
			int endX = Math.max(targetX, currX);
			for (int x = beginX + 1; x < endX; x++) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					return 0;
				}
			}
			int abs = Math.abs(targetX - currX);
			if (abs >= 2) {
				return calcHollowValue(abs);
			}
			return 0;
		}
		if (targetX == currX) {
			int beginY = Math.min(targetY, currY);
			int endY = Math.max(targetY, currY);
			for (int y = beginY + 1; y < endY; y++) {
				AbstractChessPiece piece = allPiece[currX][y];
				if (piece != null) {
					return 0;
				}
			}
			int abs = Math.abs(targetY - currY);
			if (abs >= 2) {
				return calcHollowValue(abs);
			}
			return 0;
		}
		return 0;
	}

	private int calcHollowValue(int gap) {
		return 180 + 20 * gap;
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
	public byte type() {
		return (byte) (isRed() ? 6 : 13);
	}

	private void addAllCase(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		boolean hasRack = false;
		// 向上找
		for (int x = currX + 1; x < 10; x++) {
			int position = ChessTools.toPosition(x, currY);
			if (hasRack) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (allPiece[x][currY] == null) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向下找
		hasRack = false;
		for (int x = currX - 1; x >= 0; x--) {
			int position = ChessTools.toPosition(x, currY);
			if (hasRack) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (allPiece[x][currY] == null) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向右找
		hasRack = false;
		for (int y = currY + 1; y < 9; y++) {
			int position = ChessTools.toPosition(currX, y);
			if (hasRack) {
				AbstractChessPiece piece = allPiece[currX][y];
				if (piece != null) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (allPiece[currX][y] == null) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向左找
		hasRack = false;
		for (int y = currY - 1; y >= 0; y--) {
			int position = ChessTools.toPosition(currX, y);
			if (hasRack) {
				AbstractChessPiece piece = allPiece[currX][y];
				if (piece != null) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (allPiece[currX][y] == null) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}
	}
}
