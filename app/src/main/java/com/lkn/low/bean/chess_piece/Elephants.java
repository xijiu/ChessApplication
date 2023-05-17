package com.lkn.low.bean.chess_piece;

import com.lkn.low.PubTools;
import com.lkn.low.bean.ChessBoard;
import com.lkn.low.bean.PlayerRole;
import com.lkn.low.bean.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 象
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Elephants extends AbstractChessPiece {

	public Elephants(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		setValues();
		this.setFightDefaultVal(125);
		if(PLAYER_ROLE == PlayerRole.ON_THE_OFFENSIVE){	// 先手
			this.setName("相");
		}else {
			this.setName("象");
		}
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
				{  2,  0,  0,  0,  4,  0,  0,  0,  2},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}
	/**
	 * 象可走的点为7个
	 * 只有两个点的x、y坐标分别相减的绝对值等于2时，才为可达点
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> cloudWalkMap = new HashMap<String, Position>();
		if(this.getPLAYER_ROLE().equals(PlayerRole.ON_THE_OFFENSIVE)){	// 先手情况
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 0)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 0)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(0, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(8, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 4)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 4)), cloudWalkMap);
		}else if(this.getPLAYER_ROLE().equals(PlayerRole.DEFENSIVE_POSITION)){	// 后手情况
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 9)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 9)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(0, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(8, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 5)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 5)), cloudWalkMap);
		}
		Integer currX = this.getCurrPosition().getX();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = cloudWalkMap.values();
		for (Position position : collection) {
			Integer x = position.getX();
			Integer y = position.getY();
			if(Math.abs(currX - x) == 2 && Math.abs(currY - y) == 2){
				int eyeX = currX > x ? (x + 1) : (currX + 1);
				int eyeY = currY > y ? (y + 1) : (currY + 1);
				Position eyePosition = allMap.get(ChessTools.getPositionID(eyeX, eyeY));
				if(!eyePosition.isExistPiece()){	// 如果象眼不存在棋子的话，那么此位置可达
					ChessTools.putPositionToMap(position, reachableMap);
				}
				
			}
		}
		return reachableMap;
	}

	@Override
	public String chessRecordes(Position begin, Position end, ChessBoard board) {
		return chessRecordesCurved(begin, end, board);
	}

	@Override
	public Position walkRecorde(ChessBoard board, String third, String forth) {
		return walkRecordeCurved(board, third, forth);
	}

}
