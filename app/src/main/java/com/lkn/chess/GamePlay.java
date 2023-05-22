package com.lkn.chess;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.ChessWalkBean;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.RoundTurn;
import com.lkn.chess.bean.WalkTrackBean;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LiKangNing
 */
public class GamePlay {
	private Map<ChessWalkBean, Integer> bestResultMap = new HashMap<ChessWalkBean, Integer>();
	private Map<Integer, WalkTrackBean> bestTrackMap = new HashMap<Integer, WalkTrackBean>();	// 最佳行走轨迹
	private Map<Integer, Map<String, ChessWalkBean>> transfMap = new HashMap<Integer, Map<String, ChessWalkBean>>();	// 置换表算法---
	private ChessWalkBean walkBean = new ChessWalkBean();
	private int number = 0;	// 计数器，用来计算电脑思考的情况数目
	

}
