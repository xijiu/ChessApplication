package com.lkn.chess.bean;

import android.annotation.SuppressLint;
import com.lkn.chess.BitArray;
import com.lkn.chess.ChessTools;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.bean.chess_piece.Cannons;
import com.lkn.chess.bean.chess_piece.Elephants;
import com.lkn.chess.bean.chess_piece.Horse;
import com.lkn.chess.bean.chess_piece.King;
import com.lkn.chess.bean.chess_piece.Mandarins;
import com.lkn.chess.bean.chess_piece.Pawns;
import com.lkn.chess.bean.chess_piece.Rooks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 棋盘的实体bean
 * 
 * @author:likn1 Jan 5, 2016 2:10:49 PM
 */
public class ChessBoard {
	private Map<String, Position> positionMap = new HashMap<>(90); // 位置的map
	/** 存放全量的旗子，key为旗子的位置，val是对象 */
	private AbstractChessPiece[][] allPiece = new AbstractChessPiece[10][9];
	private AbstractChessPiece[][] redPiece = new AbstractChessPiece[10][9];
	private AbstractChessPiece[][] blackPiece = new AbstractChessPiece[10][9];
	private static final int nextStepLen = 99;
	private int[][] redNextStepPositionArr = new int[nextStepLen][15];
	private int[][] blackNextStepPositionArr = new int[nextStepLen][15];

	public ChessBoard() {
//		for (int i = 0; i < 90; i++) {
//			nextStepPositionArr.add(new int[25]);
//		}
	}

	/** 10行9列的棋盘 */
	private final byte[][] board = new byte[10][9];

	public Map<String, Position> getPositionMap() {
		return positionMap;
	}

	public int[][] getRedNextStepPositionArr() {
		return redNextStepPositionArr;
	}

	public int[][] getBlackNextStepPositionArr() {
		return blackNextStepPositionArr;
	}

	@SuppressLint("NewApi")
	public void genericNextStepPositionMap() {
		for (int i = 0; i < nextStepLen; i++) {
			redNextStepPositionArr[i][0] = 0;
			blackNextStepPositionArr[i][0] = 0;
		}
		for (int x = 0; x < allPiece.length; x++) {
			for (int y = 0; y < allPiece[x].length; y++) {
				AbstractChessPiece piece = allPiece[x][y];
				if (piece == null) {
					continue;
				}
				int piecePosition = ChessTools.toPosition(x, y);
				byte[] positionArr = piece.getReachablePositions(piecePosition, this, true);
				byte size = positionArr[0];
				for (int i = 1; i <= size; i++) {
					int position = positionArr[i];
					if (hasPiece(allPiece, position)) {
						int[][] arr = piece.isRed() ? redNextStepPositionArr : blackNextStepPositionArr;
						int length = arr[position][0];
						arr[position][length + 1] = piece.getDefaultVal();
						arr[position][0] = length + 1;
					}
				}
			}
		}
	}

	private boolean hasPiece(AbstractChessPiece[][] arr, int position) {
		AbstractChessPiece piece = arr[ChessTools.fetchX(position)][ChessTools.fetchY(position)];
		return piece != null;
	}

	/**
	 * 克隆当前对象
	 */
	public ChessBoard clone() {
		ChessBoard clone = new ChessBoard();
		byte[][] original = this.board;
		for (int i = 0; i < original.length; i++) {
			clone.board[i] = Arrays.copyOf(original[i], original[i].length);
		}
		AbstractChessPiece[][] sourceArr = this.getAllPiece();
		AbstractChessPiece[][] targetArr = clone.getAllPiece();
		for (int i = 0; i < sourceArr.length; i++) {
			System.arraycopy(sourceArr[i], 0, targetArr[i], 0, sourceArr[i].length);
		}
		return clone;
	}


	/**
	 * 返回棋盘快照
	 * [1-90]   :  用来标记每个点是否有棋子
	 * [91-218] :  每个点的具体类型
	 *
	 * 车    1
	 * 马    2
	 * 象    3
	 * 士    4
	 * 帅    5
	 * 炮    6
	 * 兵    7
	 *
	 * 黑方
	 * 车    8
	 * 马    9
	 * 象    10
	 * 士    11
	 * 将    12
	 * 炮    13
	 * 兵    14
	 */
	public byte[] snapshot() {
		BitArray bitArray = new BitArray(218);
		int index = 0;
		int dataIndex = 90;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece[x][y];
				boolean hasPiece = piece != null;
				bitArray.set(index++, hasPiece);
				if (hasPiece) {
					storePieceType(bitArray, dataIndex, piece);
					dataIndex += 4;
				}
			}
		}
		return bitArray.toByteArray();
	}

	public byte[] snapshotConvert() {
		BitArray bitArray = new BitArray(218);
		int index = 0;
		int dataIndex = 90;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece[x][8 - y];
				boolean hasPiece = piece != null;
				bitArray.set(index++, hasPiece);
				if (hasPiece) {
					storePieceType(bitArray, dataIndex, piece);
					dataIndex += 4;
				}
			}
		}
		return bitArray.toByteArray();
	}

	private void storePieceType(BitArray bitArray, int dataIndex, AbstractChessPiece piece) {
		byte type = piece.type();
		StringBuilder finalStr = new StringBuilder();
		String s = Integer.toBinaryString(type);
		if (s.length() < 4) {
			int gap = 4 - s.length();
			for (int i = 0; i < gap; i++) {
				finalStr.append('0');
			}
		}
		finalStr.append(s);
		char[] chars = finalStr.toString().toCharArray();
		for (char aChar : chars) {
			bitArray.set(dataIndex++, aChar == '1');
		}
	}

	/**
	 * 棋盘的初始化工作
	 * 
	 * @author:likn1 Jan 5, 2016 2:13:27 PM
	 */
	public void init() {
		initArr(allPiece);
		initArr(redPiece);
		initArr(blackPiece);

		initBoard();
//		initForTest();
	}

	private void initArr(AbstractChessPiece[][] arr) {
		for (AbstractChessPiece[] abstractChessPieces : arr) {
			Arrays.fill(abstractChessPieces, null);
		}
	}

	private void initForTest() {
		putPiece(0, new Rooks(Role.RED));
		putPiece(1, new Horse(Role.RED));
		putPiece(2, new Elephants(Role.RED));
		putPiece(3, new Mandarins(Role.RED));
		putPiece(4, new King(Role.RED));
		putPiece(5, new Mandarins(Role.RED));
		putPiece(6, new Elephants(Role.RED));
		putPiece(7, new Rooks(Role.RED));


		putPiece(21, new Cannons(Role.RED));
		putPiece(24, new Cannons(Role.RED));
		putPiece(26, new Horse(Role.RED));


		putPiece(30, new Pawns(Role.RED));
		putPiece(32, new Pawns(Role.RED));
		putPiece(34, new Pawns(Role.RED));
		putPiece(36, new Pawns(Role.RED));
		putPiece(38, new Pawns(Role.RED));


		// --------------------------以下是后手的旗子----------------------------


		putPiece(60, new Pawns(Role.BLACK));
		putPiece(62, new Pawns(Role.BLACK));
		putPiece(64, new Pawns(Role.BLACK));
		putPiece(66, new Pawns(Role.BLACK));
		putPiece(68, new Pawns(Role.BLACK));


		putPiece(74, new Cannons(Role.BLACK));
		putPiece(77, new Cannons(Role.BLACK));
		putPiece(78, new Horse(Role.BLACK));


		putPiece(90, new Rooks(Role.BLACK));
		putPiece(91, new Horse(Role.BLACK));
		putPiece(92, new Elephants(Role.BLACK));
		putPiece(93, new Mandarins(Role.BLACK));
		putPiece(94, new King(Role.BLACK));
		putPiece(95, new Mandarins(Role.BLACK));
		putPiece(96, new Elephants(Role.BLACK));
		putPiece(97, new Rooks(Role.BLACK));
	}

	private void initBoard() {
		putPiece(0, new Rooks(Role.RED));
		putPiece(1, new Horse(Role.RED));
		putPiece(2, new Elephants(Role.RED));
		putPiece(3, new Mandarins(Role.RED));
		putPiece(4, new King(Role.RED));
		putPiece(5, new Mandarins(Role.RED));
		putPiece(6, new Elephants(Role.RED));
		putPiece(7, new Horse(Role.RED));
		putPiece(8, new Rooks(Role.RED));


		putPiece(21, new Cannons(Role.RED));
		putPiece(27, new Cannons(Role.RED));


		putPiece(30, new Pawns(Role.RED));
		putPiece(32, new Pawns(Role.RED));
		putPiece(34, new Pawns(Role.RED));
		putPiece(36, new Pawns(Role.RED));
		putPiece(38, new Pawns(Role.RED));


		// --------------------------以下是后手的旗子----------------------------


		putPiece(60, new Pawns(Role.BLACK));
		putPiece(62, new Pawns(Role.BLACK));
		putPiece(64, new Pawns(Role.BLACK));
		putPiece(66, new Pawns(Role.BLACK));
		putPiece(68, new Pawns(Role.BLACK));


		putPiece(71, new Cannons(Role.BLACK));
		putPiece(77, new Cannons(Role.BLACK));


		putPiece(90, new Rooks(Role.BLACK));
		putPiece(91, new Horse(Role.BLACK));
		putPiece(92, new Elephants(Role.BLACK));
		putPiece(93, new Mandarins(Role.BLACK));
		putPiece(94, new King(Role.BLACK));
		putPiece(95, new Mandarins(Role.BLACK));
		putPiece(96, new Elephants(Role.BLACK));
		putPiece(97, new Horse(Role.BLACK));
		putPiece(98, new Rooks(Role.BLACK));
	}

	private void putPiece(int position, AbstractChessPiece piece) {
		setPieceToBoard(position, piece);
		ChessTools.putPiece(allPiece, position, piece);
		if (piece.isRed()) {
			ChessTools.putPiece(redPiece, position, piece);
		} else {
			ChessTools.putPiece(blackPiece, position, piece);
		}
	}

	private void setPieceToBoard(int position, AbstractChessPiece piece) {
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (piece instanceof Cannons) {
			board[x][y] = (byte) (piece.isRed() ? Horse.RED_NUM : Horse.BLACK_NUM);
		} else if (piece instanceof Elephants) {
			board[x][y] = (byte) (piece.isRed() ? Elephants.RED_NUM : Elephants.BLACK_NUM);
		} else if (piece instanceof Horse) {
			board[x][y] = (byte) (piece.isRed() ? Horse.RED_NUM : Horse.BLACK_NUM);
		} else if (piece instanceof King) {
			board[x][y] = (byte) (piece.isRed() ? King.RED_NUM : King.BLACK_NUM);
		} else if (piece instanceof Mandarins) {
			board[x][y] = (byte) (piece.isRed() ? Mandarins.RED_NUM : Mandarins.BLACK_NUM);
		} else if (piece instanceof Pawns) {
			board[x][y] = (byte) (piece.isRed() ? Pawns.RED_NUM : Pawns.BLACK_NUM);
		} else if (piece instanceof Rooks) {
			board[x][y] = (byte) (piece.isRed() ? Rooks.RED_NUM : Rooks.BLACK_NUM);
		}
	}

	/**
	 * 悔棋，walk的逆过程
	 */
	public void unWalk(int sourcePosition, int targetPosition, AbstractChessPiece eatenPiece) {
//		if (1 == 1) {
//			return;
//		}
		AbstractChessPiece piece = ChessTools.getPiece(allPiece, targetPosition);
		changePiecePosition(piece, targetPosition, sourcePosition);
		if (eatenPiece != null) {
			addPiece(eatenPiece, targetPosition);
		}

	}

	/**
	 * 将某个棋子放入棋盘
	 *
	 * @param position	目标位置
	 */
	private void addPiece(AbstractChessPiece piece, int position) {
		ChessTools.putPiece(allPiece, position, piece);

		AbstractChessPiece[][] arr = piece.isRed() ? redPiece : blackPiece;
		ChessTools.putPiece(arr, position, piece);

	}

	/**
	 * 执行走棋的动作
	 *
	 * @param sourcePosition	起始位置
	 * @param targetPosition	目标位置
	 * @return	如果发生了吃子儿行为，那么返回吃掉的子儿，否则返回null
	 */
	public AbstractChessPiece walk(int sourcePosition, int targetPosition) {
		AbstractChessPiece piece = ChessTools.getPiece(allPiece, sourcePosition);
		AbstractChessPiece targetPiece = ChessTools.getPiece(allPiece, targetPosition);
		if (targetPiece != null) {
			removePiece(targetPosition);
		}
		changePiecePosition(piece, sourcePosition, targetPosition);
		return targetPiece;
	}

	/**
	 * 将目标位置的棋子从棋盘上拿下
	 *
	 * @param removePosition	目标位置
	 */
	private void removePiece(int removePosition) {
		AbstractChessPiece piece = ChessTools.removePiece(allPiece, removePosition);

		AbstractChessPiece[][] arr = piece.isRed() ? redPiece : blackPiece;
		ChessTools.removePiece(arr, removePosition);
	}

	/**
	 * 更换棋子位置
	 *
	 * @param piece	目标棋子
	 * @param sourcePosition	原位置
	 * @param targetPosition	目标位置
	 */
	private void changePiecePosition(AbstractChessPiece piece, int sourcePosition, int targetPosition) {
		ChessTools.removePiece(allPiece, sourcePosition);
		ChessTools.putPiece(allPiece, targetPosition, piece);

		AbstractChessPiece[][] arr = piece.isRed() ? redPiece : blackPiece;
		ChessTools.removePiece(arr, sourcePosition);
		ChessTools.putPiece(arr, targetPosition, piece);
	}


	/**
	 * 获取某一角色角色下的所有棋子
	 * 
	 * @param role
	 * @return
	 */
	public Set<AbstractChessPiece> getPiecesByPlayRole(Role role) {
		Set<AbstractChessPiece> set = new HashSet<>();
		Collection<Position> collection = positionMap.values();
		for (Position position : collection) {
			if (position.isExistPiece() && position.getPiece().getPLAYER_ROLE().equals(role) && position.getPiece().isAlive()) {
				set.add(position.getPiece());
			}
		}
		return set;
	}

	public void print() {
		for (int i = 9; i >= 0; i--) {
			for (int j = 0; j < 9; j++) {
				AbstractChessPiece piece = allPiece[i][j];
				if (piece != null) {
					System.out.print(piece.getName() + "\t");
				} else {
					System.out.print(" " + "\t");
				}
			}
			System.out.println("\r\n");
		}
	}

	public AbstractChessPiece[][] getAllPiece() {
		return allPiece;
	}

	public AbstractChessPiece[][] getRedPiece() {
		return redPiece;
	}

	public AbstractChessPiece[][] getBlackPiece() {
		return blackPiece;
	}
}
