package com.lkn.low;

import java.util.Set;

public class PubTools {
	
	/**
	 * 获取set集合中下标为index的元素
	 * @author:likn1	Jan 7, 2016  4:36:19 PM
	 * @param set
	 * @param index
	 * @return
	 */
	public static <T> T getSetIndexEle(Set<T> set, int index){
		T returnT = null;
		if(set != null && index >= 0){
			int i = 0;
			for (T t : set) {
				if(i == index){
					returnT = t;
					break;
				}else {
					i++;
					continue;
				}
			}
		}
		return returnT;
	}
	
	public static Integer getRandom(int begin, int end){
		double random = Math.random();
		return (int)(random * (end - begin) + begin);
	}
	
	/**
	 * 获取随机的set中的某个元素
	 * @param set
	 * @return
	 */
	public static <T> T getRandomEleFromSet(Set<T> set){
		T t = null;
		if(set != null && set.size() > 0){
			Integer index = getRandom(0, set.size() - 1);
			t = getSetIndexEle(set, index);
		}
		return t;
	}
	
	/**
	 * 二维数组在先手与后手方进行反转
	 * @author:likn1	Jan 22, 2016  1:51:17 PM
	 * @param sourceArr
	 * @return
	 */
	public static int[][] arrChessReverse(int[][] sourceArr){
		int[][] target = null;
		if(sourceArr != null && sourceArr.length > 0 && sourceArr[0] != null){
			target = new int[sourceArr.length][sourceArr[0].length];
			for (int i = 0; i < sourceArr.length; i++) {
				for (int j = 0; j < sourceArr[i].length; j++) {
					target[sourceArr.length - i - 1][j] = sourceArr[i][j];
				}
			}
		}
		return target;
	}
	
	/**
	 * 二维数组进行打印
	 * @author:likn1	Jan 22, 2016  1:49:15 PM
	 * @param val
	 */
	public static void arrShow(int[][] val){
		for (int i = 0; i < val.length; i++) {
			for (int j = 0; j < val[i].length; j++) {
				System.out.print(val[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static boolean trueMergefalse(boolean b1, boolean b2){
		boolean returnBoolean = false;
		if(b1 == b2){
			returnBoolean = true;
		}else {
			returnBoolean = false;
		}
		return returnBoolean;
	}
	
	public static String getChooseStr(){
		String str = "未知";
		if(Configure.getThinkingDepth() == 3){
			str = "小白";
		} else if(Configure.getThinkingDepth() == 4){
			str = "新手";
		} else if(Configure.getThinkingDepth() == 5){
			str = "入门";
		}
		return str;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(PubTools.getRandom(4, 5));
		}
	}
	
}
