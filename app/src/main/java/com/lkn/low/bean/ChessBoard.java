package com.lkn.low.bean;

import com.lkn.low.Configure;
import com.lkn.low.bean.chess_piece.AbstractChessPiece;
import com.lkn.low.bean.chess_piece.Cannons;
import com.lkn.low.bean.chess_piece.ChessTools;
import com.lkn.low.bean.chess_piece.Elephants;
import com.lkn.low.bean.chess_piece.Horse;
import com.lkn.low.bean.chess_piece.King;
import com.lkn.low.bean.chess_piece.Mandarins;
import com.lkn.low.bean.chess_piece.Pawns;
import com.lkn.low.bean.chess_piece.Rooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 棋盘的实体bean
 * 
 * @author:likn1 Jan 5, 2016 2:10:49 PM
 */
public class ChessBoard {
	private Map<String, Position> positionMap = new HashMap<String, Position>(90); // 位置的map
	private PlayerRole currWalkRole = PlayerRole.ON_THE_OFFENSIVE; // 当前该走子的角色，默认为先手
	private StringBuffer chessRecordesBuffer = new StringBuffer();	// 记录棋谱
	private List<RoundTurn> chessRecordesList = new ArrayList<RoundTurn>();	// 棋谱记录的列表

	public List<RoundTurn> getChessRecordesList() {
		return chessRecordesList;
	}

	public Map<String, Position> getPositionMap() {
		return positionMap;
	}

	public PlayerRole getCurrWalkRole() {
		return currWalkRole;
	}

	public void setCurrWalkRole(PlayerRole currWalkRole) {
		this.currWalkRole = currWalkRole;
	}
	
	/**
	 * 棋盘的初始化工作
	 * 
	 * @author:likn1 Jan 5, 2016 2:13:27 PM
	 */
	public void init() {
		chessRecordesBuffer = chessRecordesBuffer.delete(0, chessRecordesBuffer.length());
		positionMap.clear();
		createPosition(); // 首先创建坐标
		createPieces();	// 创建棋子
	}

	/**
	 * 克隆当前对象
	 */
	public ChessBoard clone() {
		ChessBoard clone = new ChessBoard();
		for (Position position : this.getPositionMap().values()) {
			clone.getPositionMap().put(position.getID(), position.clone());
		}
		clone.setCurrWalkRole(this.getCurrWalkRole());
		return clone;
	}

	/**
	 * 创建棋子
	 */
	private void createPieces() {
		createDown();
		createUp();
	}

	/**
	 * 创建上方的棋子
	 */
	private void createUp() {
		/**左上角的车*/
		AbstractChessPiece piece09 = new Rooks("09", PlayerRole.DEFENSIVE_POSITION);
		Position position09 = positionMap.get("09");
		position09.setPiece(piece09);
		/**右上角的车*/
		AbstractChessPiece piece89 = new Rooks("89", PlayerRole.DEFENSIVE_POSITION);
		Position position89 = positionMap.get("89");
		position89.setPiece(piece89);
		/**左上角的马*/
		AbstractChessPiece piece19 = new Horse("19", PlayerRole.DEFENSIVE_POSITION);
		Position position19 = positionMap.get("19");
		position19.setPiece(piece19);
		/**右上角的马*/
		AbstractChessPiece piece79 = new Horse("79", PlayerRole.DEFENSIVE_POSITION);
		Position position79 = positionMap.get("79");
		position79.setPiece(piece79);
		/**左上角的象*/
		AbstractChessPiece piece29 = new Elephants("29", PlayerRole.DEFENSIVE_POSITION);
		Position position29 = positionMap.get("29");
		position29.setPiece(piece29);
		/**右上角的象*/
		AbstractChessPiece piece69 = new Elephants("69", PlayerRole.DEFENSIVE_POSITION);
		Position position69 = positionMap.get("69");
		position69.setPiece(piece69);
		/**左上角的士*/
		AbstractChessPiece piece39 = new Mandarins("39", PlayerRole.DEFENSIVE_POSITION);
		Position position39 = positionMap.get("39");
		position39.setPiece(piece39);
		/**右上角的士*/
		AbstractChessPiece piece59 = new Mandarins("59", PlayerRole.DEFENSIVE_POSITION);
		Position position59 = positionMap.get("59");
		position59.setPiece(piece59);
		/**上的将*/
		AbstractChessPiece piece49 = new King("49", PlayerRole.DEFENSIVE_POSITION);
		Position position49 = positionMap.get("49");
		position49.setPiece(piece49);
		/**左上角的炮*/
		AbstractChessPiece piece17 = new Cannons("17", PlayerRole.DEFENSIVE_POSITION);
		Position position17 = positionMap.get("17");
		position17.setPiece(piece17);
		/**右上角的炮*/
		AbstractChessPiece piece77 = new Cannons("77", PlayerRole.DEFENSIVE_POSITION);
		Position position77 = positionMap.get("77");
		position77.setPiece(piece77);
		/**上方的5个卒*/
		AbstractChessPiece piece06 = new Pawns("06", PlayerRole.DEFENSIVE_POSITION);
		Position position06 = positionMap.get("06");
		position06.setPiece(piece06);
		AbstractChessPiece piece26 = new Pawns("26", PlayerRole.DEFENSIVE_POSITION);
		Position position26 = positionMap.get("26");
		position26.setPiece(piece26);
		AbstractChessPiece piece46 = new Pawns("46", PlayerRole.DEFENSIVE_POSITION);
		Position position46 = positionMap.get("46");
		position46.setPiece(piece46);
		AbstractChessPiece piece66 = new Pawns("66", PlayerRole.DEFENSIVE_POSITION);
		Position position66 = positionMap.get("66");
		position66.setPiece(piece66);
		AbstractChessPiece piece86 = new Pawns("86", PlayerRole.DEFENSIVE_POSITION);
		Position position86 = positionMap.get("86");
		position86.setPiece(piece86);
	}

	/**
	 * 创建下方的棋子
	 */
	private void createDown() {
		/**左下角的车*/
		AbstractChessPiece piece00 = new Rooks("00", PlayerRole.ON_THE_OFFENSIVE);
		Position position00 = positionMap.get("00");
		position00.setPiece(piece00);
		/**右下角的车*/
		AbstractChessPiece piece80 = new Rooks("80", PlayerRole.ON_THE_OFFENSIVE);
		Position position80 = positionMap.get("80");
		position80.setPiece(piece80);
		/**左下角的马*/
		AbstractChessPiece piece10 = new Horse("10", PlayerRole.ON_THE_OFFENSIVE);
		Position position10 = positionMap.get("10");
		position10.setPiece(piece10);
		/**右下角的马*/
		AbstractChessPiece piece70 = new Horse("70", PlayerRole.ON_THE_OFFENSIVE);
		Position position70 = positionMap.get("70");
		position70.setPiece(piece70);
		/**左下角的象*/
		AbstractChessPiece piece20 = new Elephants("20", PlayerRole.ON_THE_OFFENSIVE);
		Position position20 = positionMap.get("20");
		position20.setPiece(piece20);
		/**右下角的象*/
		AbstractChessPiece piece60 = new Elephants("60", PlayerRole.ON_THE_OFFENSIVE);
		Position position60 = positionMap.get("60");
		position60.setPiece(piece60);
		/**左下角的士*/
		AbstractChessPiece piece30 = new Mandarins("30", PlayerRole.ON_THE_OFFENSIVE);
		Position position30 = positionMap.get("30");
		position30.setPiece(piece30);
		/**右下角的士*/
		AbstractChessPiece piece50 = new Mandarins("50", PlayerRole.ON_THE_OFFENSIVE);
		Position position50 = positionMap.get("50");
		position50.setPiece(piece50);
		/**下方的将*/
		AbstractChessPiece piece40 = new King("40", PlayerRole.ON_THE_OFFENSIVE);
		Position position40 = positionMap.get("40");
		position40.setPiece(piece40);
		/**左下角的炮*/
		AbstractChessPiece piece12 = new Cannons("12", PlayerRole.ON_THE_OFFENSIVE);
		Position position12 = positionMap.get("12");
		position12.setPiece(piece12);
		/**右下角的炮*/
		AbstractChessPiece piece72 = new Cannons("72", PlayerRole.ON_THE_OFFENSIVE);
		Position position72 = positionMap.get("72");
		position72.setPiece(piece72);
		/**下方的5个卒*/
		AbstractChessPiece piece03 = new Pawns("03", PlayerRole.ON_THE_OFFENSIVE);
		Position position03 = positionMap.get("03");
		position03.setPiece(piece03);
		AbstractChessPiece piece23 = new Pawns("23", PlayerRole.ON_THE_OFFENSIVE);
		Position position23 = positionMap.get("23");
		position23.setPiece(piece23);
		AbstractChessPiece piece43 = new Pawns("43", PlayerRole.ON_THE_OFFENSIVE);
		Position position43 = positionMap.get("43");
		position43.setPiece(piece43);
		AbstractChessPiece piece63 = new Pawns("63", PlayerRole.ON_THE_OFFENSIVE);
		Position position63 = positionMap.get("63");
		position63.setPiece(piece63);
		AbstractChessPiece piece83 = new Pawns("83", PlayerRole.ON_THE_OFFENSIVE);
		Position position83 = positionMap.get("83");
		position83.setPiece(piece83);
	}

	/**
	 * 创建坐标
	 */
	private void createPosition() {
		for (int x = 0; x <= 8; x++) { // 横坐标
			for (int y = 0; y <= 9; y++) { // 纵坐标
				Position p = new Position(x, y, false, null);
				positionMap.put(p.getID(), p);
			}
		}
	}

	/**
	 * 获取当前数字的个位数
	 * 
	 * @author:likn1 Jan 5, 2016 4:34:34 PM
	 * @param source
	 * @return
	 */
	public Integer getUnitNum(Integer source) {
		Integer target = null;
		if (source >= 0) {
			if (source >= 0 && source <= 9) {
				target = source;
			} else {
				String sourceStr = String.valueOf(source);
				char[] charArray = sourceStr.toCharArray();
				target = Integer.parseInt(String.valueOf(charArray[charArray.length - 1]));
			}
		}
		return target;
	}

	/**
	 * 获取某一角色角色下的所有棋子
	 * 
	 * @param role
	 * @return
	 */
	public Set<AbstractChessPiece> getPiecesByPlayRole(PlayerRole role) {
		Set<AbstractChessPiece> set = new HashSet<AbstractChessPiece>();
		Collection<Position> collection = positionMap.values();
		for (Position position : collection) {
			if (position.isExistPiece() && position.getPiece().getPLAYER_ROLE().equals(role) && position.getPiece().isFight()) {
				set.add(position.getPiece());
			}
		}
		return set;
	}

	/**
	 * 获取某一角色比对方角色高出多少
	 * 
	 * @param role
	 * @return
	 */
	public Integer getHigherFightValByRole(PlayerRole role) {
		int myselfTotal = 0; // 我方战斗力
		int opponentTotal = 0; // 对方战斗力
		Collection<Position> collection = positionMap.values();
		for (Position position : collection) {
			AbstractChessPiece piece = position.getPiece();
			if (piece != null) { // 此位置有棋子的前提下
				if (position.isExistPiece() && piece.isFight()) {
					if (piece.getPLAYER_ROLE().equals(role)) {
						myselfTotal = myselfTotal + piece.getFightVal();
					} else {
						opponentTotal = opponentTotal + piece.getFightVal();
					}
				}
			}
		}
		return myselfTotal - opponentTotal;
	}
	
	/**
	 * 棋盘输出
	 * @author:likn1	Jan 12, 2016  10:07:53 AM
	 */
	public void show(){
		for (int y = 9; y >= 0; y--) {
			for (int x = 0; x <= 8; x++) {
				Position position = positionMap.get(ChessTools.getPositionID(x, y));
				if(position.isExistPiece()){
					System.out.print(position.getPiece().getName() + "\t");
				}else {
					System.out.print(" " + "\t");
				}
			}
			System.out.println("\r\n\r\n");
		}
	}
	
	/**
	 * 记录棋谱
	 * @param begin
	 * @param end
	 */
	public void chessRecordes(Position begin, Position end){
		AbstractChessPiece piece = end.getPiece();
		String recorde = piece.chessRecordes(begin, end, this);
		chessRecordesBuffer.append(recorde).append(" ");
		recordesToList(begin, end);
	}

	/**
	 * 向list中加入走棋的路径
	 * @author:likn1	Feb 22, 2016  3:17:39 PM
	 * @param begin
	 * @param end
	 */
	private void recordesToList(Position begin, Position end) {
		PlayerRole role = null;
		if(begin.isExistPiece() && begin.getPiece().isFight()){
			role = begin.getPiece().getPLAYER_ROLE();
		} else {
			role = end.getPiece().getPLAYER_ROLE();
		}
		if(role == PlayerRole.ON_THE_OFFENSIVE){
			RoundTurn turn = new RoundTurn(begin, end, null, null);
			chessRecordesList.add(turn);
		} else {
			RoundTurn turn = chessRecordesList.get(chessRecordesList.size() - 1);
			turn.setBlackPosition(begin, end);
		}
	}
	
	public StringBuffer getChessRecordesBuffer() {
		return chessRecordesBuffer;
	}
	
	/**
	 * 当前棋盘情况转换为一个长度为90的字符串
	 * @author:likn1	Feb 1, 2016  2:32:13 PM
	 * @return
	 */
	public String currPiecesStr(){
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x <= 8; x++) {
			for (int y = 0; y <= 9; y++) {
				Position position = positionMap.get(ChessTools.getPositionID(x, y));
				if(position.isExistPiece() && position.getPiece().isFight()){
					sb.append(Configure.getPieceNumberAndFlagMap().get(position.getPiece().getId()));
				} else {
					sb.append("0");
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 判断将是否被吃掉
	 * 如果将被吃掉，意味着游戏结束
	 * @author:likn1	Feb 19, 2016  3:04:45 PM
	 * @return
	 */
	public boolean isKingEaten(){
		boolean isEaten = true;
		Set<AbstractChessPiece> set1 = ChessTools.getPieceByName(this, "帅", PlayerRole.ON_THE_OFFENSIVE);
		Set<AbstractChessPiece> set2 = ChessTools.getPieceByName(this, "将", PlayerRole.DEFENSIVE_POSITION);
		if(set1 != null && set1.size() == 1 && set2 != null && set2.size() == 1){
			isEaten = false;
		}
		return isEaten;
	}
}
