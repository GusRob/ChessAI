package Bots;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import GameEngine.*;



//class is used to make moves as a computer controlled opponent
public class Dionysus implements Computer{

	private PieceHandler pieces;

  private int color;

	private ArrayList<int[]> moves = new ArrayList<int[]>();

	public Dionysus(PieceHandler init_pieces, int init_color){
		pieces = init_pieces;
    color = init_color;
	}

  public int getColor(){return color;}

  public int choosePromotion(){
    return 0;
  }

  public int[] chooseMove(){
    int[] result = new int[2];
		boolean[][] bitBoardsCopy = pieces.getBitBoardsCopy();
    moves = getAllMoves(bitBoardsCopy, color);
		System.out.println("found " + moves.size() + " moves");
		moves = reduce(moves, bitBoardsCopy, color);
		System.out.println("reduced to " + moves.size());
		moves = reduceToMax(color==0?false:true, moves);
		System.out.println("further to " + moves.size());
		moves = fillResponse();
		System.out.println("now it is " + moves.size());
		int resultIndex = 0;
		boolean problem = false;
		if(moves.size() == 0){
			pieces.queryCheckmate();
			problem = true;
		} else {
			System.out.println("calculating safest moves");
			resultIndex = chooseMaxVal(color==0?true:false, moves, 3);
		}
		int[] defaultVal = {-1, -1};
		System.out.println("chosen move is " + moves.get(resultIndex)[0] + " to " + moves.get(resultIndex)[1]);
    return problem?defaultVal:moves.get(resultIndex);
  }

	private ArrayList<int[]> fillResponse(){
		ArrayList<int[]> result = new ArrayList<int[]>();
		for(int[] move : moves){
			boolean[][] bitBoards_tmp = pieces.moves.bitBoardsTmp(move[1], color, pieces.getPieceId(move[0]), move[0], pieces.getBitBoardsCopy());
			move[3] = getResponse(bitBoards_tmp);
			result.add(move);
		}
		return result;
	}

	//get the value of the opponent's responding move [Artemis]
	private int getResponse(boolean[][] bitBoards_tmp){
		int result = 0;
		ArrayList<int[]> oppMoves = new ArrayList<int[]>();
		oppMoves = getAllMoves(bitBoards_tmp, color);
		oppMoves = reduce(moves, bitBoards_tmp, color);
		oppMoves = reduceToMax(color==0?false:true, oppMoves);
		if(oppMoves.size() == 0){
			result = color==0?-100:100;
		} else {
			int[] move = oppMoves.get(0);
			boolean[][] bitBoards_tmp2 = pieces.moves.bitBoardsTmp(move[1], color==0?6:0, pieces.getPieceId(move[0]), move[0], bitBoards_tmp);
			result = valueBoard(bitBoards_tmp2);
		}
		return result;
	}

	//reduce the arraylist of moves to only moves that capture pieces [if there are any]
	private ArrayList<int[]> reduce(ArrayList<int[]> input, boolean[][] bitBoards, int colorFor){
		ArrayList<int[]> result = new ArrayList<int[]>();
		for(int[] move : input){
			int colOnSq = pieces.getPieceColor(move[1], bitBoards);
			if(colOnSq == ((colorFor==0)?6:0)){
				result.add(move);
			}
		}
		if(result.size() == 0){
			result = input;
		} else {
		}
		return result;
	}

	//generates an arraylist of moves for the given colour within the given bitBoards
	private ArrayList<int[]> getAllMoves(boolean[][] bitBoards, int colorFor){
		ArrayList<int[]> result = new ArrayList<int[]>();
		for(int i = 0; i < 64; i++){
      int pieceColor = pieces.getPieceColor(i, bitBoards);
      int pieceId = pieces.getPieceId(i, bitBoards);
      if(pieceId != -1 && pieceColor == colorFor){
        for(int j = 0; j<64; j++){
          if(pieces.moves.validateTurn(j, i, bitBoards)){
						int[] newMove = new int[4];
						newMove[0] = i;
            newMove[1] = j;
						boolean[][] bitBoards_tmp = pieces.moves.bitBoardsTmp(j, colorFor, pieceId, i, bitBoards);
						newMove[2] = valueBoard(bitBoards_tmp);
						newMove[3] = colorFor==0?-99:99;
						result.add(newMove);
          }
        }
      }
    }
    return result;
	}

	//calls maxIndex and chooses a random value from the resulting array
	private int chooseMaxVal(boolean high, ArrayList<int[]> input, int val){
		Random r = new Random();
		int randomInt = 0;
		int[] maxVals = maxIndex(high, input, val);
		randomInt = r.nextInt(maxVals.length);
		return maxVals[randomInt];
	}

	private ArrayList<int[]> reduceToMax(boolean high, ArrayList<int[]> input){
		int[] maxVals = maxIndex(high, input, 2);
		ArrayList<int[]> result = new ArrayList<int[]>();
		for(int i = 0; i < maxVals.length; i++){
			result.add(input.get(maxVals[i]));
		}
		return result;
	}

	//gives the index of the move with the highest stored value
	private int[] maxIndex(boolean high, ArrayList<int[]> input, int val){
		int highest = high?-99:99;
		int sameValMoves = 0;
		for(int[] move : input){
			if(high && move[val] != highest){
				if(move[val] > highest){
					highest = move[val];
					sameValMoves = 1;
				}
			} else if (move[val] != highest) {
				if(move[val] < highest){
					highest = move[val];
					sameValMoves = 1;
				}
			} else {
				sameValMoves++;
			}
		}
		int[] result = new int[sameValMoves];
		int j = 0;
		for(int[] move : input){
			if(move[val] == highest){
				result[j] = input.indexOf(move);
				j++;
			}
		}
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
					System.exit(0x21);
				}
			}
		}
		return whPieceScore-blPieceScore;
	}

}
