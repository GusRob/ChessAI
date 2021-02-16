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
		
		return false;
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
		result = result && !check(heldColor); //check if the piece is in check after move?
		return result;
	}

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
