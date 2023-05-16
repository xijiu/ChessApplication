package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.PlayerRole;
import com.lkn.chess.bean.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 炮
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Cannons extends AbstractChessPiece {
	
	public Cannons(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		this.setFightDefaultVal(285);
		setValues();
		this.setName("炮");
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  6,  4,  0,-10,-12,-10,  0,  4,  6},
				{  2,  2,  0, -4,-14, -4,  0,  2,  2},
				{  2,  2,  0,-10, -8,-10,  0,  2,  2},
				{  0,  0, -2,  4, 10,  4, -2,  0,  0},
				{  0,  0,  0,  2,  8,  2,  0,  0,  0},
				{ -2,  0,  4,  2,  6,  2,  4,  0, -2},
				{  0,  0,  0,  2,  4,  2,  0,  0,  0},
				{  4,  0,  8,  6, 10,  6,  8,  0,  4},
				{  0,  2,  4,  6,  6,  6,  4,  2,  0},
				{  0,  0,  2,  6,  6,  6,  2,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	/**
	 * 炮有两种大的行为模式
	 * 1、不吃子
	 * 2、吃子
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();	// 可达的位置
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> mapX = ChessTools.getAllChess_X(allMap, this.getCurrPosition());
		Map<String, Position> mapY = ChessTools.getAllChess_Y(allMap, this.getCurrPosition());
		reachableMap.putAll(notEatPiece(mapX, mapY, allMap));	// 不吃子的情况下
		reachableMap.putAll(eatPiece(mapX, mapY, allMap));	// 吃子的情况下
		return reachableMap;
	}

	/**
	 * 在炮不吃子的情况下，能够到达的位置
	 * 此种情况下，炮的行为类似车；
	 * 但是炮不会像车那样吃子，故需要把吃子的行为过滤掉
	 * @author:likn1	Jan 7, 2016  4:12:01 PM
	 * @param mapX
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Map<String, Position> notEatPiece(Map<String, Position> mapX, Map<String, Position> mapY, Map<String, Position> allMap) {
		Rooks rooks = new Rooks(null, this.getPLAYER_ROLE());
		Map<String, Position> rooksMap = rooks.rooksOperate(this.getCurrPosition(), allMap);	// 模拟车的行为
		Map<String, Position> reachableMap = new HashMap<String, Position>();	//  没有棋子的map
		Collection<Position> collection = rooksMap.values();
		for (Position position : collection) {
			if(!position.isExistPiece()){
				reachableMap.put(position.getID(), position);
			}
		}
		return reachableMap;
	}
	
	/******************************************************************************************************************************************************************************
	 * 吃子的情况分为4类：上、下、左、右
	 * @author:likn1	Jan 7, 2016  4:14:16 PM
	 * @param mapX
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Map<String, Position> eatPiece(Map<String, Position> mapX, Map<String, Position> mapY, Map<String, Position> allMap) {
		Map<String, Position> reachableEatMap = new HashMap<String, Position>();
		ChessTools.putPositionToMap(eatUP(mapY, allMap), reachableEatMap);	// 向上吃子
		ChessTools.putPositionToMap(eatDOWN(mapY, allMap), reachableEatMap);	// 向下吃子
		ChessTools.putPositionToMap(eatLEFT(mapX, allMap), reachableEatMap);	// 向左吃子
		ChessTools.putPositionToMap(eatRIGHT(mapX, allMap), reachableEatMap);	// 向右吃子
		return reachableEatMap;
	}

	/**
	 * 向右吃子
	 * @author:likn1	Jan 7, 2016  5:19:29 PM
	 * @param mapX
	 * @param allMap
	 * @return
	 */
	private Position eatRIGHT(Map<String, Position> mapX, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currX = this.getCurrPosition().getX();
		Collection<Position> collection = mapX.values();
		for (Position position : collection) {
			if(position.getX() > currX && position.isExistPiece()){
				set.add(position.getX());
			}
		}
		if(set.size() >= 2){
			Integer x = PubTools.getSetIndexEle(set, 1);	// 获取第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(x, this.getCurrPosition().getY()));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向左吃子
	 * @author:likn1	Jan 7, 2016  5:10:06 PM
	 * @param mapX
	 * @param allMap
	 * @return
	 */
	private Position eatLEFT(Map<String, Position> mapX, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currX = this.getCurrPosition().getX();
		Collection<Position> collection = mapX.values();
		for (Position position : collection) {
			if(position.getX() < currX && position.isExistPiece()){
				set.add(position.getX());
			}
		}
		if(set.size() >= 2){
			Integer x = PubTools.getSetIndexEle(set, set.size() - 2);	// 获取倒数第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(x, this.getCurrPosition().getY()));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向下吃子
	 * @author:likn1	Jan 7, 2016  4:50:14 PM
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Position eatDOWN(Map<String, Position> mapY, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = mapY.values();
		for (Position position : collection) {
			if(position.getY() < currY && position.isExistPiece()){
				set.add(position.getY());
			}
		}
		if(set.size() >= 2){
			Integer y = PubTools.getSetIndexEle(set, set.size() - 2);	// 获取倒数第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(this.getCurrPosition().getX(), y));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向上吃子的情况
	 * @author:likn1	Jan 7, 2016  4:18:20 PM
	 * @param mapY
	 * @param allMap
	 */
	private Position eatUP(Map<String, Position> mapY, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = mapY.values();
		for (Position position : collection) {
			if(position.getY() > currY && position.isExistPiece()){
				set.add(position.getY());
			}
		}
		if(set.size() >= 2){
			Integer y = PubTools.getSetIndexEle(set, 1);	// 获取第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(this.getCurrPosition().getX(), y));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
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
