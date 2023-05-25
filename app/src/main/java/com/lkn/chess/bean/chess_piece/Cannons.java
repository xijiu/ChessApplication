package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
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
	public static final int BASE_VAL = 280;


	public Cannons(Role role) {
		this(null, role);
	}
	
	public Cannons(String id, Role role) {
		super(id, role);
		this.setDefaultVal(BASE_VAL);
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
				{  0,  0,  0,  0, 10,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  6,  6, 10,  6,  6,  0,  0},
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
		int hollowVal = calcHollow(x, y, board.getAllPiece());
		int eatenVal = eatenValue(board, position);
//		int eatenVal = 0;
		return Math.max(0, BASE_VAL + hollowVal + arr[x][y] - eatenVal);
	}

	/**
	 * 计算是否存在空心炮，以及其威力
	 */
	private int calcHollow(int currX, int currY, AbstractChessPiece[][] allPiece) {
		int targetPos = findKingPositionByName(allPiece, isRed());
		int targetX = ChessTools.fetchX(targetPos);
		int targetY = ChessTools.fetchY(targetPos);
		if (isRed()) {
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
					return 150 + 10 * abs;
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
					return 150 + 10 * abs;
				}
				return 0;
			}
		}
		return 0;
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

//	@Override
//	public boolean canEat(ChessBoard board, int currPos, int targetPos) {
//		Map<Integer, AbstractChessPiece> allPiece = board.getAllPiece();
//		int currX = ChessTools.fetchX(currPos);
//		int currY = ChessTools.fetchY(currPos);
//		int targetX = ChessTools.fetchX(targetPos);
//		int targetY = ChessTools.fetchY(targetPos);
//		if (currX == targetX) {
//			int minY = Math.min(currY, targetY);
//			int maxY = Math.max(currY, targetY);
//			int num = 0;
//			for (int y = minY + 1; y < maxY; y++) {
//				if (allPiece.containsKey(ChessTools.toPosition(currX, y))) {
//					num++;
//				}
//				if (num > 1) {
//					return false;
//				}
//			}
//			if (num == 1) {
//				return true;
//			}
//		}
//
//		if (currY == targetY) {
//			int minX = Math.min(currX, targetX);
//			int maxX = Math.max(currX, targetX);
//			int num = 0;
//			for (int x = minX + 1; x < maxX; x++) {
//				if (allPiece.containsKey(ChessTools.toPosition(x, currY))) {
//					num++;
//				}
//				if (num > 1) {
//					return false;
//				}
//			}
//			return num == 1;
//		}
//
//		return false;
//	}


	/**
	 * 吃子儿的情况
	 */
	private void addEatCase(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		boolean hasRack = false;
		AbstractChessPiece piece = null;
		// 向上找
		for (int x = currX + 1; x < 10; x++) {
			int position = ChessTools.toPosition(x, currY);
			if ((piece = allPiece[x][currY]) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向下找
		hasRack = false;
		piece = null;
		for (int x = currX - 1; x >= 0; x--) {
			int position = ChessTools.toPosition(x, currY);
			if ((piece = allPiece[x][currY]) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向右找
		hasRack = false;
		piece = null;
		for (int y = currY + 1; y < 9; y++) {
			int position = ChessTools.toPosition(currX, y);
			if ((piece = allPiece[currX][y]) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向左找
		hasRack = false;
		piece = null;
		for (int y = currY - 1; y >= 0; y--) {
			int position = ChessTools.toPosition(currX, y);
			if ((piece = allPiece[currX][y]) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}
	}

	/**
	 * 不吃子儿的情况
	 */
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
