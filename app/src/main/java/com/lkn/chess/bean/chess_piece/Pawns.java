package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.PlayerRole;
import com.lkn.chess.bean.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * 兵
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Pawns extends AbstractChessPiece {
	
	public Pawns(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		setValues();
		this.setFightDefaultVal(20);
		if(PLAYER_ROLE == PlayerRole.ON_THE_OFFENSIVE){	// 先手
			this.setName("兵");
		}else {
			this.setName("卒");
		}
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
				{  6, 12, 18, 18, 20, 18, 18, 12,  6},
				{  2,  0,  8,  0,  8,  0,  8,  0,  2},
				{  0,  0, -2,  0,  4,  0, -2,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	/**
	 * 兵的可走路线需要区分是先手方还是后手方
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();	// 获取棋盘的所有的位置集合

		Position position = this.getCurrPosition();
		Integer x = position.getX();
		Integer y = position.getY();
		if(this.getPLAYER_ROLE().equals(PlayerRole.ON_THE_OFFENSIVE)){	// 先手
			if(y < 5){	// 没有过河的情况，当前的兵只能有一个位置可走
				addValidPosition(x, y + 1, reachableMap, allMap);	// 只有向上
			}else {	// 过河的情况
				addValidPosition(x, y + 1, reachableMap, allMap);	// 1、向上
				addValidPosition(x - 1, y, reachableMap, allMap);	// 2、向左
				addValidPosition(x + 1, y, reachableMap, allMap);	// 3、向右
			}
		} else if(this.getPLAYER_ROLE().equals(PlayerRole.DEFENSIVE_POSITION)){	// 后手
			if(y >= 5){	// 没有过河的情况，当前的兵只能有一个位置可走
				addValidPosition(x, y - 1, reachableMap, allMap);	// 只有向下
			}else{	// 过河的情况
				addValidPosition(x, y - 1, reachableMap, allMap);	// 1、向下
				addValidPosition(x - 1, y, reachableMap, allMap);	// 2、向左
				addValidPosition(x + 1, y, reachableMap, allMap);	// 3、向右
			}
		}
		return reachableMap;
	}

	/**
	 * 横坐标为x，纵坐标为y的位置是否可达
	 * 如果可达，那么将其加入reachableMap，否则不做任何操作
	 * @author:likn1	Jan 6, 2016  3:03:04 PM
	 * @param x
	 * @param y
	 * @param reachableMap
	 */
	private void addValidPosition(Integer x, Integer y, Map<String, Position> reachableMap, Map<String, Position> allMap) {
		Position UP_Position = allMap.get(ChessTools.getPositionID(x, y));
		boolean reachable = ChessTools.isPositionReachable(this.getPLAYER_ROLE(), UP_Position);
		if(reachable){	// 如果此位置没有棋子，或者此处的棋子是对方的
			reachableMap.put(UP_Position.getID(), UP_Position);
		}
	}

	@Override
	public String chessRecordes(Position begin, Position end, ChessBoard board) {
		return chessRecordesStraight(begin, end, board);
	}

	@Override
	public Position walkRecorde(ChessBoard board, String third, String forth) {
		return walkRecordeStraight(board, third, forth);
	}

}
