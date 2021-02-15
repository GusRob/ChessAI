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
		result = result && !check(heldColor);


		return result;
	}

}
