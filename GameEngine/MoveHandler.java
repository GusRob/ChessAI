package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class MoveHandler{

	private PieceHandler pieces;

	//flags to represent check for each colour isInCheck[0] = black isInCheck[1] = white
	private boolean[] isInCheck = {false, false};

	//arraylist containing available moves
	private ArrayList<Move> availableMoves = new ArrayList<Move>();

	//flags to represent whether values need to be updated
	//		update[0] represents isBlInCheck flag
	//		update[1] represents isWhInCheck flag
	//		update[2] represents availableMoves flag
	private boolean[] update = {false, false, false};

	public MoveHandler(PieceHandler init_pieces){
		updatePieces(init_pieces);
	}

	//update the piecehandler to get most recent state of the board
	public void updatePieces(PieceHandler new_pieces){
		pieces = new_pieces;
		update[0] = false;
		update[1] = false;
		update[2] = false;
	}

	public boolean[] updateCheck(){
		return isInCheck;
	}
}
