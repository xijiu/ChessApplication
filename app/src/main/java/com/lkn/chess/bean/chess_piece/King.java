package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.PlayerRole;
import com.lkn.chess.bean.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 将
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class King extends AbstractChessPiece{

	public King(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		setValues();
		this.setFightDefaultVal(1000000);
		if(PLAYER_ROLE == PlayerRole.ON_THE_OFFENSIVE){	// 先手
			this.setName("帅");
		}else {
			this.setName("将");
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
				{  0,  0,  0,-10,-10,-10,  0,  0,  0},
				{  0,  0,  0, -8, -8, -8,  0,  0,  0},
				{  0,  0,  0, -2,  0, -2,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}
	/**
	 * 将的行走
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();	// 最终结果，需要返回的对象
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> couldWalkMapAll = new HashMap<String, Position>();	// 能够走的位置的集合
		if(this.getPLAYER_ROLE().equals(PlayerRole.ON_THE_OFFENSIVE)){	// 先手
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 2)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 2)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 2)), couldWalkMapAll);
		}else if(this.getPLAYER_ROLE().equals(PlayerRole.DEFENSIVE_POSITION)){	// 后手
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 9)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 9)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 9)), couldWalkMapAll);
		}
		
		Integer currX = this.getCurrPosition().getX();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = couldWalkMapAll.values();
		for (Position position : collection) {
			Integer x = position.getX();
			Integer y = position.getY();
			if(currX == x){	// 如果x轴相同
				if((currY == y + 1) || (currY == y - 1)){	// 且y轴只差一步
					if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果当前的位置是可达的
						ChessTools.putPositionToMap(position, reachableMap);	// 将当期的位置放入reachableMap中
					}
				}
			}else if(currY == y){	// 如果y轴相同
				if((currX == x + 1) || (currX == x - 1)){	// 且x轴只差一步
					if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果当前的位置是可达的
						ChessTools.putPositionToMap(position, reachableMap);	// 将当期的位置放入reachableMap中
					}
				}
			}
		}
		operateTwoKingMeeting(reachableMap, board);	// 处理两个帅见面的情况
		return reachableMap;
	}

	/**
	 * 处理两个帅直接见面的情况
	 * @author:likn1	Feb 13, 2016  10:33:06 AM
	 * @param reachableMap
	 * @param board 
	 */
	private void operateTwoKingMeeting(Map<String, Position> reachableMap, ChessBoard board) {
		Set<String> notReachableSet = new HashSet<String>();
		AbstractChessPiece redKing = PubTools.getSetIndexEle(ChessTools.getPieceByName(board, "帅", PlayerRole.ON_THE_OFFENSIVE), 0);
		AbstractChessPiece blackKing = PubTools.getSetIndexEle(ChessTools.getPieceByName(board, "将", PlayerRole.DEFENSIVE_POSITION), 0);
		AbstractChessPiece kingPiece = null;
		if(this.getName().equals("将")){
			kingPiece = redKing;
		} else {
			kingPiece = blackKing;
		}
		for (Position position : reachableMap.values()) {
			if(position.getX().equals(kingPiece.getCurrPosition().getX())){	// 如果在同一列中
				if(judgeTwoKingsMeeting(board, position, kingPiece.getCurrPosition())){
					notReachableSet.add(position.getID());
				}
			}
		}
		for (String str : notReachableSet) {	// 将可能会导致两个“将”见面的情况删掉
			reachableMap.remove(str);
		}
		
		/**以下处理，如果两个将真的见面了，那么走棋方可以吃掉对方的将****************************************************/
		boolean isMeet = judgeTwoKingsMeeting(board, redKing.getCurrPosition(), blackKing.getCurrPosition());	// 判断两个将是否见面
		if(isMeet){
			reachableMap.put(kingPiece.getCurrPosition().getID(), kingPiece.getCurrPosition());
		}
	}

	/**
	 * 判断两个将是否见面
	 * @author:likn1	Feb 24, 2016  11:14:55 AM
	 * @param board
	 * @param position1
	 * @param position2
	 * @return
	 */
	private boolean judgeTwoKingsMeeting(ChessBoard board, Position position1, Position position2) {
		boolean isMeet = true;
		if(position1.getX().equals(position2.getX())){	// 首先需要在同一列中
			int minY = position1.getY() > position2.getY() ? position2.getY() : position1.getY();
			int maxY = position1.getY() > position2.getY() ? position1.getY() : position2.getY();
			Map<String, Position> map = ChessTools.getAllChess_Y(board.getPositionMap(), position1);
			for (Position position : map.values()) {
				if(position.isExistPiece() && position.getPiece().isFight()){
					AbstractChessPiece piece = position.getPiece();
					if(!piece.getName().equals("帅") && !piece.getName().equals("将")){
						Integer pieceY = piece.getCurrPosition().getY();
						if(pieceY > minY && pieceY < maxY){
							isMeet = false;
						}
					}
				}
			}
		} else {	// 如果不在同一列
			isMeet = false;
		}
		
		return isMeet;
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
