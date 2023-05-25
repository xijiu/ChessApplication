package com.lkn.chess;

import android.util.Log;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.manual.Manual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认电脑是黑方
 *
 * @author xijiu
 * @since 2023/5/4 上午8:52
 */
public class GamePlayHigh {

    private static int SOURCE_POS = 0;
    private static int TARGET_POS = 0;
    private static int COUNT = 0;
    private static LinkedHashMap<Integer, Integer> posMap = new LinkedHashMap<>();
    /** 最佳路径 key 为 level */
    private static Map<Integer, Integer> bestRouteMap = new HashMap<>();
    private static int TMP_THINK_DEPTH = 0;

    public int[] computerWalk(ChessBoard chessBoard) {
        long begin = System.currentTimeMillis();
        COUNT = 0;
        bestRouteMap.clear();

        Integer from;
        Integer to;

        Integer multiPos = Manual.readManual(chessBoard.snapshot());
        if (multiPos != -1) {
            System.out.println("hit manual");
            from = PubTools.uncompressBegin(multiPos);
            to = PubTools.uncompressEnd(multiPos);
        } else {
            for (int i = 2; i <= Conf.THINK_DEPTH; i++) {
                TMP_THINK_DEPTH = i;
                posMap.clear();
                think(chessBoard, Role.BLACK, 1, Integer.MAX_VALUE);
            }

            System.out.println("----------begin-------");
            System.out.println(sb);
            System.out.println("----------end-------");


            System.out.println("----------begin1-------");
            System.out.println(sb1);
            System.out.println("----------end1-------");


            List<Integer> list = new ArrayList<>(posMap.keySet());
            Collections.shuffle(list);
            from = list.get(0);
            to = posMap.get(from);
        }


        System.out.println("SOURCE_POS is " + from);
        System.out.println("TARGET_POS is " + to);
        chessBoard.walk(from, to);
        System.out.println("考虑情况 " + COUNT + ", time cost " + (System.currentTimeMillis() - begin));
//        printBestPath();
        System.out.println("cccccccccc " + SOURCE_POS);
        return new int[]{from, to};
    }

    private void printBestPath() {
        if (bestRouteMap.size() == 0) {
            return;
        }
        System.out.println("最佳路径： ");
        for (int i = 1; i <= Conf.THINK_DEPTH; i++) {
            Integer pos = bestRouteMap.get(i);
            int begin = PubTools.uncompressBegin(pos);
            int end = PubTools.uncompressEnd(pos);
            System.out.println("level " + i + ", begin " + begin + ", end " + end);
        }
    }

    private static boolean valid = false;
    private static boolean valid1 = false;
    private static StringBuilder sb = new StringBuilder();
    private static StringBuilder sb1 = new StringBuilder();

    private int think(ChessBoard chessBoard, Role role, int level, int parentVal) {
        int finalVal = role == Role.RED ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        AbstractChessPiece[][] pieceArr = role == Role.RED ? chessBoard.getRedPiece() : chessBoard.getBlackPiece();
        int firstPos = sortPieceMap(pieceArr, level);
        int firstPosTmp = firstPos;
        for (int x = 0; x < pieceArr.length; x++) {
            for (int y = 0; y < pieceArr[x].length; y++) {
                int sourcePos = ChessTools.toPosition(x, y);
                AbstractChessPiece piece = pieceArr[x][y];
                if (firstPos != -1) {
                    sourcePos = firstPos;
                    piece = pieceArr[ChessTools.fetchX(sourcePos)][ChessTools.fetchY(sourcePos)];
                    firstPos = -1;
                    y--;
                }
                if (piece == null) {
                    continue;
                }
                if (firstPosTmp == sourcePos && y != -1) {
                    continue;
                }

                byte[] reachablePositions = piece.getReachablePositions(sourcePos, chessBoard, false, level);
                exchangeBestPositionToFirst(reachablePositions, level);
                byte size = reachablePositions[0];
                for (int i = 1; i <= size; i++) {
                    COUNT++;
                    byte targetPos = reachablePositions[i];
                    if (level == 1 && sourcePos == 93 && targetPos == 84) {
                        valid = true;
                    }
                    if (valid && level == 2 && sourcePos == 21 && targetPos == 41) {
                        valid1 = true;
                    }
                    AbstractChessPiece eatenPiece = chessBoard.walk(sourcePos, targetPos);
                    boolean isKingEaten = isKingEaten(eatenPiece);
                    Role nextRole = role.nextRole();
                    int newLevel = level + 1;
                    int value;
                    if (newLevel <= TMP_THINK_DEPTH && !isKingEaten) {
                        value = think(chessBoard, nextRole, newLevel, finalVal);
                    } else {
                        if (isKingEaten) {
                            value = role == Role.RED ? Integer.MIN_VALUE : Integer.MAX_VALUE - level;
                        } else {
                            value = computeChessValue(chessBoard);
                        }
                    }

                    if (role == Role.RED) {
                        if (value < finalVal) {
                            bestRouteMap.put(level, PubTools.compress(sourcePos, targetPos));
                        }
                        finalVal = Math.min(value, finalVal);
                    } else {
                        if (level == 1 && TMP_THINK_DEPTH == Conf.THINK_DEPTH) {
                            if (value > finalVal) {
                                posMap.clear();
                                posMap.put(sourcePos, (int) targetPos);
                            } else if (value == finalVal) {
                                posMap.put(sourcePos, (int) targetPos);
                            }
                            Log.e("LEVEL_1_VAL ",piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos);
                        }
                        if (value > finalVal) {
                            bestRouteMap.put(level, PubTools.compress(sourcePos, targetPos));
                        }
                        finalVal = Math.max(value, finalVal);
                    }

                    if (valid && level == 2) {
                        sb.append("LEVEL is " + level + ",   " + piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos).append("\n");
                    }

                    if (valid1 && level == 3) {
                        sb1.append("LEVEL is " + level + ",   " + piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos).append("\n");
                    }

                    chessBoard.unWalk(sourcePos, targetPos, eatenPiece);
                    boolean pruning = needPruning(role, parentVal, value);
                    if (level == 1 && sourcePos == 93 && targetPos == 84) {
                        valid = false;
                    }
                    if (valid && level == 2 && sourcePos == 21 && targetPos == 41) {
                        valid1 = false;
                    }
                    if (pruning) {
                        return role == Role.RED ? Conf.GAME_PLAY_MIN_VAL : Conf.GAME_PLAY_MAX_VAL;
                    }
                }
            }
        }
        return finalVal;
    }

    private boolean isKingEaten(AbstractChessPiece eatenPiece) {
        if (eatenPiece == null) {
            return false;
        }
        String name = eatenPiece.getName();
        return name.equals("帅") || name.equals("将");
    }

    private void exchangeBestPositionToFirst(byte[] reachablePositions, int level) {
//        if (1 == 1) {
//            return;
//        }
        Integer position = bestRouteMap.get(level);
        if (position == null) {
            return;
        }
        int endPos = PubTools.uncompressEnd(position);
        byte reachablePosition = reachablePositions[0];
        for (int i = 1; i <= reachablePosition; i++) {
            if (reachablePositions[i] == endPos) {
                byte tmp = reachablePositions[1];
                reachablePositions[1] = reachablePositions[i];
                reachablePositions[i] = tmp;
                break;
            }
        }
    }

    private int sortPieceMap(AbstractChessPiece[][] pieceArr, int level) {
//        if (1 == 1) {
//            return new HashMap<>(pieceMap);
//        }
        Integer position = bestRouteMap.get(level);
        if (position == null) {
            return -1;
        }
        int beginPos = PubTools.uncompressBegin(position);
        AbstractChessPiece piece = ChessTools.getPiece(pieceArr, beginPos);
        if (piece != null) {
            return beginPos;
        }
        return -1;
    }

    /**
     * 判断是否需要剪枝
     */
    private boolean needPruning(Role role, int parentVal, int currVal) {
//        if (1 == 1) {
//            return false;
//        }
        if (role == Role.RED) {
            if (currVal < parentVal) {
                return true;
            }
        }
        if (role == Role.BLACK) {
            if (currVal > parentVal) {
                return true;
            }
        }
        return false;
    }

    /**
     * 评估棋盘的价值
     *
     * @param chessBoard 棋盘
     * @return  价值
     */
    private int computeChessValue(ChessBoard chessBoard) {
        chessBoard.genericNextStepPositionMap();
        int totalValue = 0;
        AbstractChessPiece[][] allPiece = chessBoard.getAllPiece();
        for (int x = 0; x < allPiece.length; x++) {
            for (int y = 0; y < allPiece[x].length; y++) {
                int position = ChessTools.toPosition(x, y);
                AbstractChessPiece piece = allPiece[x][y];
                if (piece == null) {
                    continue;
                }
                int value = piece.valuation(chessBoard, position);
                if (piece.isRed()) {
                    totalValue -= value;
                } else {
                    totalValue += value;
                }
            }
        }
        return totalValue;
    }


}
