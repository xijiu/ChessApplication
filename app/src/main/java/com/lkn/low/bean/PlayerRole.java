package com.lkn.low.bean;
/**
 * 象棋角色，分为先手跟后手
 * @author:likn1	Jan 5, 2016  11:29:54 AM
 */
public enum PlayerRole {
	ON_THE_OFFENSIVE,	// 先手
	DEFENSIVE_POSITION;	// 后手
	
	/**
	 * 下一步该走棋的角色
	 * @param currRole
	 * @return
	 */
	public static PlayerRole nextRole(PlayerRole currRole){
		if(currRole.equals(PlayerRole.ON_THE_OFFENSIVE)){
			return PlayerRole.DEFENSIVE_POSITION;
		}else {
			return PlayerRole.ON_THE_OFFENSIVE;
		}
	}
}
