package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.Role;

import java.util.Arrays;

/**
 * 象棋中棋子的抽象类，每个旗子都设置一个唯一编号
 *
 *
 * 车10	马11 象12 士13 将14 士13 象12	马11 车10
 *
 *
 *
 *  	炮9 	 	 	 	 	炮9
 *
 *
 * 卒8	 	卒8 	卒8		卒8		卒8
 *
 *
 *
 *
 * 兵7	 	兵7 	兵7 	兵7	    兵7
 *
 *
 *     炮6	 	 	 	 	 	炮6
 *
 *
 *
 * 车1	马2	相3	仕4	帅5	仕4	相3	马2	车1
 */
public abstract class AbstractChessPiece implements Cloneable {
	private String id;			// 棋子唯一的编号
	private String name;		// 棋子名称
	private String showName;		// 棋子名称
	private Integer fightVal;	// 棋子战斗力（某个数值，战斗力会随着棋势的进行发生变更）
	protected Integer defaultVal;	// 棋子默认战斗力，不会发生改变
	private final Role PLAYER_ROLE;	// 象棋先后手
	private boolean isAlive;	// 是否在战斗，默认为true
	private Position currPosition;	// 棋子当前的位置
	// 每种棋子对应唯一一个num值
	protected int num = 0;
	protected int VAL_BLACK[][] = {	// 先手方的位置与权值加成
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0}
		};
	protected int VAL_RED[][] = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成

	/**
	 * 迭代深度不超过10层
	 * 这个对象仅为了辅助使用，每一层提供一个byte[19]，避免重复创建byte数组
	 */
	protected byte[][] reachableHelper = new byte[11][19];

	public AbstractChessPiece(String id, Role PLAYER_ROLE) {
		this.id = id;
		this.PLAYER_ROLE = PLAYER_ROLE;
	}

	/**
	 * 初始化设置num属性
	 *
	 * @param role	当前旗子的角色
	 * @param redNum	先手的值
	 * @param blackNum	后手的值
	 */
	protected void initNum(Role role, int redNum, int blackNum) {
		this.num = role == Role.RED ? 6 : 9;
	}

	/** 第一个位置存放当前可达位置的总数量，后面的element存放具体的position */
	protected byte[] reachablePositions = null;
	protected int reachableNum = 0;
	
	public AbstractChessPiece cloneImpl(Position position){
		AbstractChessPiece clone = clone();
		clone.setCurrPosition(position);
		return clone;
	}
	
	/**
	 * 实现对象克隆
	 */
	public AbstractChessPiece clone(){
		AbstractChessPiece clone = null;
		try {
			clone = (AbstractChessPiece)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}


	/**
	 * 棋子可能被吃掉，例如一个马被4个卒夹击，那这个马的价值可能趋于0了
	 * 	 	卒
	 * 	  卒马卒
	 * 		卒
	 */
	protected int eatenValue(ChessBoard board, int currPosition) {
		Role nextRole = Conf.THINK_DEPTH % 2 == 1 ? Role.RED : Role.BLACK;
		if (this.getPLAYER_ROLE() == nextRole) {
			return 0;
		}
		int[] redValArr = board.getRedNextStepPositionArr()[currPosition];
		int[] blackValArr = board.getBlackNextStepPositionArr()[currPosition];

		int[] ownValArr = isRed() ? redValArr : blackValArr;
		int[] enemyValArr = isRed() ? blackValArr : redValArr;

		int ownLength = ownValArr[0];
		int enemyLength = enemyValArr[0];
		Arrays.sort(ownValArr, 1, ownLength + 1);
		Arrays.sort(enemyValArr, 1, enemyLength + 1);
		return calcEatenValue(ownValArr, enemyValArr);
	}

	private int calcEatenValue(int[] ownValArr, int[] enemyValArr) {
		int ownLength = ownValArr[0];
		int enemyLength = enemyValArr[0];
		int ownI = 1;
		int enemyI = 1;

		int enemyLossVal = 0;
		int ownLossVal = 0;
		int lastVal = this.getDefaultVal();
		while (true) {
			if (enemyI > enemyLength) {
				break;
			}
			int enemyVal = enemyValArr[enemyI++];
			ownLossVal += lastVal;
			lastVal = enemyVal;


			if (ownI > ownLength) {
				break;
			}
			int ownVal = ownValArr[ownI++];
			enemyLossVal += lastVal;
			lastVal = ownVal;
		}

		// 如果敌人损失的超过了我们自己损失的，那么认为当前棋子没有被吃的风险
		if (enemyLossVal >= ownLossVal) {
			return 0;
		} else {
			return ownLossVal - enemyLossVal;
		}
	}


	protected boolean isEnemy(AbstractChessPiece piece1, AbstractChessPiece piece2) {
		return piece1.getPLAYER_ROLE() != piece2.getPLAYER_ROLE();
	}

	protected boolean isFriend(AbstractChessPiece piece1, AbstractChessPiece piece2) {
		return piece1.getPLAYER_ROLE() == piece2.getPLAYER_ROLE();
	}

	/**
	 * 目标棋子可达到的位置点
	 *
	 * @param currPosition	当前棋子位置
	 * @param board	棋盘
	 * @param containsProtectedPiece	返回的结果是否包含其可以保护的己方棋子
	 * @param level 从1开始
	 * @return	可达到的位置点
	 */
	public abstract byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level);

	public abstract int valuation(ChessBoard board, int position);

	public abstract int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2);

	public abstract byte type();

//	public abstract boolean canEat(ChessBoard board, int currPos, int targetPos);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Integer getFightVal() {
		return fightVal;
	}

	public void setFightVal(Integer fightVal) {
		this.fightVal = fightVal;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isFight) {
		this.isAlive = isFight;
	}

	public Role getPLAYER_ROLE() {
		return PLAYER_ROLE;
	}

	public boolean isRed() {
		return PLAYER_ROLE == Role.RED;
	}

	public Position getCurrPosition() {
		return currPosition;
	}
	
	/**
	 * 设置棋子的默认攻击力，同时为fightVal设置值，一般只在构造方法中调用
	 * @param defaultVal
	 */
	public void setDefaultVal(Integer defaultVal) {
		this.defaultVal = defaultVal;
		this.setFightVal(defaultVal);
	}
	
	public int getDefaultVal() {
		return defaultVal;
	}

	protected void recordReachablePosition(int position) {
		reachableNum++;
		reachablePositions[reachableNum] = (byte) position;
	}

	protected boolean isValid(int x, int y) {
		return x >= 0 && x < 10 && y >= 0 && y < 9;
	}

	protected int findKingPositionByName(AbstractChessPiece[][] allPiece) {
		if (isRed()) {
			for (int x = 7; x <= 9; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 12) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		} else {
			for (int x = 0; x <= 2; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 5) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		}

		System.out.println(" --------- ");
		throw new RuntimeException();
	}

	/**
	 * 设置棋子的位置
	 * @param currPosition
	 * @param changeFightValue	true:改变战斗力	false:不改变战斗力
	 */
	public void setCurrPosition(Position currPosition, boolean changeFightValue) {
		setCurrPosition(currPosition);
		if(changeFightValue){
			pieceValueChange();
		}
	}
	
	/**
	 * 设置棋子的位置
	 * @param currPosition
	 */
	public void setCurrPosition(Position currPosition) {
		this.currPosition = currPosition;
		if(currPosition != null){
			setAlive(true);
			currPosition.setExistPiece(true);
			currPosition.setPiece(this);
		}else {
			setAlive(false);
		}
	}

	/**
	 * 当前的位置变化，可能影响棋子的战斗力、权值变化
	 * @author:likn1	Jan 22, 2016  6:42:56 PM
	 */
	private void pieceValueChange() {
		int changeVal = 0;
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		if(PLAYER_ROLE == Role.RED){	// 先手
			changeVal = VAL_RED[y][x];
		} else {	// 后手
			changeVal = VAL_BLACK[y][x];
		}
		setFightVal(defaultVal + changeVal);	// 设置战斗力
	}
	
}
