package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class MoveHandler{

	private PieceHandler pieces;

	public MoveHandler(PieceHandler init_pieces){
		pieces = init_pieces;
	}

	//function called to update the piece's isInCheck value
	public void updateCheck(){
		boolean[] isInCheck = {false, false};
		isInCheck[0] = check(0);
		isInCheck[1] = check(6);
		pieces.setIsInCheck(isInCheck);
	}

	//function to calculate, for one side, if the king is in check
	public boolean check(int col){
		boolean result = false;
		int oppCol = col==0?6:0;
		int kingX = pieces.getKing(col)%8;
		int kingY = (int)(pieces.getKing(col)/8);
		int direction = col==0?-1:1;

		int oppKingX = pieces.getKing(oppCol)%8;
		int oppKingY = (int)(pieces.getKing(oppCol)/8);

		//check kings arent checking each other throw error
		if(Math.abs(kingX - oppKingX) < 2 && Math.abs(kingY - oppKingY) < 2 ){
			System.exit(0x10);
		}

		//check for linear moving pieces
		boolean[][] directions = new boolean[3][3];
		for(int n = 1; n<7; n++){
			for(int i = -1; i<2; i++){
				for(int j = -1; j<2; j++){
					if(!directions[i+1][j+1]){
						int testX = (kingX+i*n);
						int testY = (kingY+j*n);
						if(testX >= 0 && testX <= 7 && testY >= 0 && testY <= 7){
							int midSquare = testX+8*testY;
							int pieceId = pieces.getPieceId(midSquare);
							if(pieceId != -1){
								if(i != 0 && j != 0){
									if(pieceId == 3 + oppCol || pieceId == 4 + oppCol){
										result = true;
									}
								} else if(i == 0 || j == 0){
									if(pieceId == 1 + oppCol || pieceId == 4 + oppCol){
										result = true;
									}
								}
								directions[i+1][j+1] = true;
							}
						} else {
							directions[i+1][j+1] = false;
						}
					}
				}
			}
		}
		//check for knight
		for(int i = -2; i < 3; i++){
			for(int j = -2; j < 3; j++){
				if(i != 0 && j != 0 && Math.abs(i) != Math.abs(j)){
					if(kingX + i >= 0 && kingX + i <= 7 && kingY + j >= 0 && kingY + j <= 7){
						int test = ((kingX+i)+8*(kingY+j));
						int pieceId = pieces.getPieceId(test);
						if(pieceId == 2 + oppCol){
							result = true;
						}
					}
				}
			}
		}
		//check for pawns
		int pawnSq1x = kingX + 1;
		int pawnSq2x = kingX - 1;
		int pawnSqY = kingY - direction;
		System.out.println("(" + pawnSq1x + ", " + pawnSqY  +")");
		System.out.println("(" + pawnSq2x + ", " + pawnSqY  +")");
		if(pawnSqY >= 0 && pawnSqY <= 7){
			if(pawnSq1x >= 0 && pawnSq1x <= 7){
				if(pieces.getPieceId(pawnSq1x + 8*pawnSqY) == 5 + oppCol){
					result = true;
				}
			}
			if(pawnSq2x >= 0 && pawnSq2x <= 7){
				if(pieces.getPieceId(pawnSq2x + 8*pawnSqY) == 5 + oppCol){
					result = true;
				}
			}
		}

		return result;
	}

	//input - square that the held piece, stored in this classes reference to piecehandler is to move to
	//output - true if that move is legal, false if that move isnt
	public boolean validateTurn(int square){
		boolean result = true;
		int turn = pieces.board.getTurn()?6:0;
		int heldSquare = pieces.getHeldSquare();
		int heldColor = pieces.getPieceColor(heldSquare);
		int heldId = pieces.getPieceId(heldSquare);
		if(square == heldSquare || heldColor == pieces.getPieceColor(square)){ //basic move laws
			result = false;
		}
		//specific piece movement laws
		if(heldId == 5 || heldId == 11){
			result = result && validatePawn(square, heldSquare, heldColor);
		} else if(heldId == 4 || heldId == 10){
			result = result && validateQueen(square, heldSquare);
		} else if(heldId == 3 || heldId == 9){
			result = result && validateBishop(square, heldSquare);
		} else if(heldId == 2 || heldId == 8){
			result = result && validateKnight(square, heldSquare);
		} else if(heldId == 1 || heldId == 7){
			result = result && validateRook(square, heldSquare);
		} else if(heldId == 0 || heldId == 6){
			result = result && validateKing(square, heldSquare);
		}
		result = result && !check(heldColor); //check if the piece is in check after move? this currently does it for before position so deadlock when someone is in check
		return result;
	}

	//functions for specific piece moves
	//input - square piece is moving to, from, and, where necessary the colour of the pieces
	//output - a boolean answering can the piece make that move
	private boolean validatePawn(int squareTo, int squareFrom, int color){
		boolean result = false;
		int direction = color==0?1:-1; //if white - direction pos // if black - direction neg
		int oppColor = color==0?6:0;

		if(squareTo == squareFrom + 8*direction){
			if(pieces.getPieceColor(squareTo) == -1){
				// if move is one square forwards && square is empty
				result = true;
			} else {result = false;}
		} else if(squareTo == squareFrom + 16*direction){
			if(pieces.getPieceColor(squareTo) == -1 && pieces.getPieceColor(squareFrom+8*direction) == -1 && (int)(squareFrom/8) == (color==0?1:6)){
				// if move is two squares forwards && both squares are empty && piece is moving from second rank on their side
				result = true;
			} else {result = false;}
		} else if((squareTo == squareFrom + 8*direction + 1 && (int)(squareTo/8) ==(int)((squareTo+1)/8)) || squareTo == squareFrom + 8*direction - 1 && (int)(squareTo/8) ==(int)((squareTo-1)/8)){
			if(pieces.getPieceColor(squareTo) == oppColor){
				//if move is a forward diagonal, and square contains an opponent piece
				result = true;
			} else {result = false;}
		} else {result = false;}
		return result;
	}

	private boolean validateKnight(int squareTo, int squareFrom){
		boolean result = false;
		int xDiff = Math.abs( squareTo%8 - squareFrom%8 );
		int yDiff = Math.abs( (int)(squareTo/8) - (int)(squareFrom/8) );

		if((xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2)){
			//if it moves (2 L or R and 1 U or D) or (1 L or R and 2 U or D)
			result = true;
		} else {result = false;}

		return result;
	}

	private boolean validateBishop(int squareTo, int squareFrom){
		boolean result = false;
		int xDiff = Math.abs( squareTo%8 - squareFrom%8 );
		int yDiff = Math.abs( (int)(squareTo/8) - (int)(squareFrom/8) );
		int xDirection = xDiff==0?0:(squareTo%8>squareFrom%8)?1:-1;
		int yDirection = yDiff==0?0:((int)(squareTo/8)>(int)(squareFrom/8))?1:-1;
		if(xDiff == yDiff){
			result = true;
			for(int i = 1; i<xDiff; i++){
				int midSquare = squareFrom+xDirection*i+yDirection*8*i;
				if(pieces.getPieceColor(midSquare) != -1){
					result = false;
				}
			}
		}
		return result;
	}

	private boolean validateRook(int squareTo, int squareFrom){
		boolean result = false;
		int xDiff = Math.abs( squareTo%8 - squareFrom%8 );
		int yDiff = Math.abs( (int)(squareTo/8) - (int)(squareFrom/8) );
		int xDirection = xDiff==0?0:(squareTo%8>squareFrom%8)?1:-1;
		int yDirection = yDiff==0?0:((int)(squareTo/8)>(int)(squareFrom/8))?1:-1;
		if(xDiff == 0 || yDiff == 0){
			result = true;
			for(int i = 1; i<Math.max(xDiff, yDiff); i++){
				int midSquare = squareFrom+xDirection*i+yDirection*8*i;
				if(pieces.getPieceColor(midSquare) != -1){
					result = false;
				}
			}
		}
		return result;
	}

	private boolean validateKing(int squareTo, int squareFrom){
		boolean result = false;
		int xDiff = Math.abs( squareTo%8 - squareFrom%8 );
		int yDiff = Math.abs( (int)(squareTo/8) - (int)(squareFrom/8) );
		if(xDiff <=1 && yDiff <=1){
			result = true;
		}
		return result;
	}

	private boolean validateQueen(int squareTo, int squareFrom){
		return validateBishop(squareTo, squareFrom) || validateRook(squareTo, squareFrom);
	}



}
