package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ArrPool;
import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.util.Map;

/**
 * 将
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class King extends AbstractChessPiece {
	public static final int RED_NUM = 5;
	public static final int BLACK_NUM = 14;

	public King(Role role) {
		this(null, role);
	}

	public King(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(1000000);
		if(role == Role.RED){	// 先手
			this.setName("帅");
			this.setShowName("帅");
		}else {
			this.setName("将");
			this.setShowName("将");
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
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,-10,-10,-10,  0,  0,  0},
				{  0,  0,  0, -8, -8, -8,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 5 : 12);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		return 1000000 + arr[x][y];
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
	public byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece) {
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		findReachablePositions(currX, currY, board.getAllPiece(), containsProtectedPiece);
		reachablePositions[0] = (byte) reachableNum;
		byte[] result = ArrPool.borrow();
		System.arraycopy(reachablePositions, 0, result, 0, reachablePositions.length);
		return result;
	}

	private void findReachablePositions(int currX, int currY, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		tryReach(currX - 1, currY, allPiece, containsProtectedPiece);
		tryReach(currX + 1, currY, allPiece, containsProtectedPiece);
		tryReach(currX, currY - 1, allPiece, containsProtectedPiece);
		tryReach(currX, currY + 1, allPiece, containsProtectedPiece);

		tryReachToEnemyKing(currX, currY, allPiece);
	}

	private void tryReachToEnemyKing(int currX, int currY, AbstractChessPiece[][] allPiece) {
		if (this.isRed()) {
			for (int x = currX + 1; x < 10; x++) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (piece.getName().equals("将")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		} else {
			for (int x = currX - 1; x >= 0; x--) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (piece.getName().equals("帅")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		}
	}

	private void tryReach(int x, int y, AbstractChessPiece[][] allPiece, boolean containsProtectedPiece) {
		if (isInArea(x, y)) {
			AbstractChessPiece piece = allPiece[x][y];
			if (piece == null || isEnemy(this, piece)) {
				recordReachablePosition(ChessTools.toPosition(x, y));
			}
			if (piece != null && isFriend(this, piece) && containsProtectedPiece) {
				recordReachablePosition(ChessTools.toPosition(x, y));
			}
		}
	}

	private boolean isInArea(int x, int y) {
		if (this.isRed()) {
			return x >= 0 && x <= 2 && y >= 3 && y <= 5;
		} else {
			return x >= 7 && x <= 9 && y >= 3 && y <= 5;
		}
	}

}
