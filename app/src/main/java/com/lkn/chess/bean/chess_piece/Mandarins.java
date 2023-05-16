package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.PlayerRole;
import com.lkn.chess.bean.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * 士
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Mandarins extends AbstractChessPiece{

	public Mandarins(String id, PlayerRole PLAYER_ROLE) {
		super(id, PLAYER_ROLE);
		setValues();
		this.setFightDefaultVal(120);
		if(PLAYER_ROLE == PlayerRole.ON_THE_OFFENSIVE){	// 先手
			this.setName("仕");
		}else {
			this.setName("士");
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
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  4,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	/**
	 * 士只有5个点可以走
	 * 1、如果当前位置是中心位置，那么有4个位置可走
	 * 2、如果当前位置不是中心位置，那么只有一个中心位置可走
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();
		Position p1 = null;
		Position p2 = null;
		Position p3 = null;
		Position p4 = null;
		Position center = null;
		if(this.getPLAYER_ROLE().equals(PlayerRole.ON_THE_OFFENSIVE)){	// 先手
			p1 = allMap.get(ChessTools.getPositionID(3, 0));
			p2 = allMap.get(ChessTools.getPositionID(5, 0));
			p3 = allMap.get(ChessTools.getPositionID(3, 2));
			p4 = allMap.get(ChessTools.getPositionID(5, 2));
			center = allMap.get(ChessTools.getPositionID(4, 1));
		} else if(this.getPLAYER_ROLE().equals(PlayerRole.DEFENSIVE_POSITION)){	// 后手
			p1 = allMap.get(ChessTools.getPositionID(3, 9));
			p2 = allMap.get(ChessTools.getPositionID(5, 9));
			p3 = allMap.get(ChessTools.getPositionID(3, 7));
			p4 = allMap.get(ChessTools.getPositionID(5, 7));
			center = allMap.get(ChessTools.getPositionID(4, 8));
		}
		
		
		if(this.getCurrPosition().getID().equals(center.getID())){	// 如果当前棋子在中心
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p1)){	// 如果p1可达
				reachableMap.put(p1.getID(), p1);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p2)){	// 如果p2可达
				reachableMap.put(p2.getID(), p2);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p3)){	// 如果p3可达
				reachableMap.put(p3.getID(), p3);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p4)){	// 如果p4可达
				reachableMap.put(p4.getID(), p4);
			}
		} else {	// 如果不在中心
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), center)){	// 如果中心可达
				reachableMap.put(center.getID(), center);
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
