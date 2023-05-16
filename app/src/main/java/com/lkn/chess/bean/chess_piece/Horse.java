package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.PlayerRole;
import com.lkn.chess.bean.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * 马
 * @author:likn1	Jan 5, 2016  3:54:47 PM
 */
public class Horse extends AbstractChessPiece {
	
	public Horse(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		setValues();
		this.setFightDefaultVal(270);
		this.setName("马");
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
				{  0,  2,  4,  4, -2,  4,  4,  2,  0},
				{  0, -4,  0,  0,  0,  0,  0, -4,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}
	
	/**
	 * 马的可达位置集合，共8个方位，且不需要区分玩家的角色
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> reachableMap = new HashMap<String, Position>();	// 可达map，最终需要返回
		jumpUP(allMap, reachableMap);	// 向上跳最多2个方位
		jumpRIGHT(allMap, reachableMap);	// 向右跳最多有2个方位
		jumpLEFT(allMap, reachableMap);	// 向左跳最多有2个方位
		jumpDOWN(allMap, reachableMap);	// 向下跳最多有2个方位
		return reachableMap;
	}

	/**
	 * 向左跳
	 * @author:likn1	Jan 7, 2016  2:03:50 PM
	 * @param allMap
	 * @param reachableMap
	 */
	private void jumpLEFT(Map<String, Position> allMap, Map<String, Position> reachableMap) {
		Integer x = this.getCurrPosition().getX();
		Integer y = this.getCurrPosition().getY();
		Position keyPosition = allMap.get(ChessTools.getPositionID(x - 1 , y));	// 如果此位置有子的话，那么将蹩马腿
		if(keyPosition != null && !keyPosition.isExistPiece()){
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x - 2 , y + 1)), reachableMap);	// 上
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x - 2 , y - 1)), reachableMap);	// 下
		}
	}

	/**
	 * 向右跳
	 * @author:likn1	Jan 7, 2016  2:00:39 PM
	 * @param allMap
	 * @param reachableMap
	 */
	private void jumpRIGHT(Map<String, Position> allMap, Map<String, Position> reachableMap) {
		Integer x = this.getCurrPosition().getX();
		Integer y = this.getCurrPosition().getY();
		Position keyPosition = allMap.get(ChessTools.getPositionID(x + 1 , y));	// 如果此位置有子的话，那么将蹩马腿
		if(keyPosition != null && !keyPosition.isExistPiece()){
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x + 2 , y + 1)), reachableMap);	// 上
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x + 2 , y - 1)), reachableMap);	// 下
		}
	}

	/**
	 * 向上跳
	 * @author:likn1	Jan 7, 2016  10:30:55 AM
	 * @param allMap
	 * @param reachableMap
	 */
	private void jumpUP(Map<String, Position> allMap, Map<String, Position> reachableMap) {
		Integer x = this.getCurrPosition().getX();
		Integer y = this.getCurrPosition().getY();
		Position keyPosition = allMap.get(ChessTools.getPositionID(x , y + 1));	// 如果此位置有子的话，那么将蹩马腿
		if(keyPosition != null && !keyPosition.isExistPiece()){
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x - 1 , y + 2)), reachableMap);	// 左上位置
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x + 1 , y + 2)), reachableMap);	// 右上位置
		}
	}
	
	/**
	 * 向下跳
	 * @author:likn1	Jan 7, 2016  2:04:48 PM
	 * @param allMap
	 * @param reachableMap
	 */
	private void jumpDOWN(Map<String, Position> allMap, Map<String, Position> reachableMap) {
		Integer x = this.getCurrPosition().getX();
		Integer y = this.getCurrPosition().getY();
		Position keyPosition = allMap.get(ChessTools.getPositionID(x , y - 1));	// 如果此位置有子的话，那么将蹩马腿
		if(keyPosition != null && !keyPosition.isExistPiece()){
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x - 1 , y - 2)), reachableMap);	// 左上位置
			ChessTools.putValidPositionToMap(this.getPLAYER_ROLE(), allMap.get(ChessTools.getPositionID(x + 1 , y - 2)), reachableMap);	// 右上位置
		}
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
