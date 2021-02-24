package Bots;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import GameEngine.*;



//class is used to make moves as a computer controlled opponent
public class Athena implements Computer{

	private PieceHandler pieces;

  private int color;

	private int[][] moves = new int[100][3];

	public Athena(PieceHandler init_pieces, int init_color){
		pieces = init_pieces;
    color = init_color;
	}

  public int getColor(){return color;}

  public int choosePromotion(){
    return 0;
  }

  public int[] chooseMove(){
		int z = valueBoard(pieces.getBitBoardsCopy());
		int n = 0;
    int[] result = new int[2];
    for(int i = 0; i < 64; i++){
      int pieceColor = pieces.getPieceColor(i);
      int pieceId = pieces.getPieceId(i);
      if(pieceId != -1 && pieceColor == color){
        for(int j = 0; j<64; j++){
          if(pieces.moves.validateTurn(j, i)){
						if(n < 100){
            	moves[n][0] = i;
            	moves[n][1] = j;
							boolean[][] bitBoards_tmp = pieces.moves.bitBoardsTmp(j, color, pieceId, i);
							moves[n][2] = valueBoard(bitBoards_tmp);
							n++;
						}
          }
        }
      }
    }
		System.out.println("found " + n + " moves");
		int a = 0;
		for(int i = 0; i < n; i++){
			int col = pieces.getPieceColor(moves[i][1]);
			if(col == ((color==0)?6:0)){
				moves[a] = moves[i];
				a++;
			}
		}
		if(a != 0){
			n = a;
			System.out.println("cut to " + n + " moves");
		}
		int resultIndex = 0;
		boolean problem = false;
		if(n == 0){
			pieces.queryCheckmate();
			problem = true;
		} else {
			resultIndex = chooseMaxVal(n, color==0?false:true);
		}
		int[] defaultVal = {-1, -1};
		System.out.println("chosen move is " + moves[resultIndex][0] + " to " + moves[resultIndex][1]);
    return problem?defaultVal:moves[resultIndex];
  }

	private int chooseMaxVal(int n, boolean high){
		Random r = new Random();
		int randomInt = 0;
		int[] maxVals = maxIndex(n, high);
		randomInt = r.nextInt(maxVals.length);
		return maxVals[randomInt];
	}

	//gives the index of the move with the highest stored value
	private int[] maxIndex(int n, boolean high){
		int highest = moves[0][2];
		int sameValMoves = 1;
		for(int i = 1; i < n; i++){
			if(high && moves[i][2] != highest){
				if(moves[i][2] > highest){
					highest = moves[i][2];
					sameValMoves = 1;
				}
			} else if (moves[i][2] != highest) {
				if(moves[i][2] < highest){
					highest = moves[i][2];
					sameValMoves = 1;
				}
			} else {
				sameValMoves++;
			}
		}
		int[] result = new int[sameValMoves];
		int j = 0;
		for(int i = 0; i < n; i++){
			if(moves[i][2] == highest){
				result[j] = i;
				j++;
			}
		}
		System.out.println("cut to " + sameValMoves + " moves");
		return result;
	}

	//gives a value to the input board according to the formula
	//sum(white piece values) - sum(black piece values)
	private int valueBoard(boolean[][] bitBoardsTmp){
		int blPieceScore = 0;
		int whPieceScore = 0;
		for(int i = 0; i <64; i++){
			int pieceId = pieces.getPieceId(i, bitBoardsTmp);
			int pieceCol = pieces.getPieceColor(i, bitBoardsTmp);
			if(pieceId != -1 && pieceId != 0 && pieceId != 6){ //all pieces except kings
				if(pieceId == 4+pieceCol){ //queen
					blPieceScore += (pieceCol==0?9:0);
					whPieceScore += (pieceCol==6?9:0);
				} else if(pieceId == 3+pieceCol || pieceId == 2+pieceCol){ //knights ang bishops
					blPieceScore += (pieceCol==0?3:0);
					whPieceScore += (pieceCol==6?3:0);
				} else if(pieceId == 1+pieceCol){ //rooks
					blPieceScore += (pieceCol==0?5:0);
					whPieceScore += (pieceCol==6?5:0);
				} else if(pieceId == 5+pieceCol){ //rooks
					blPieceScore += (pieceCol==0?1:0);
					whPieceScore += (pieceCol==6?1:0);
				} else {
					System.out.println(pieceId);
					System.exit(0x21);
				}
			}
		}
		return whPieceScore-blPieceScore;
	}

}
