package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class MoveHandler{

	private boolean bitBoards[][] = new boolean[12][64];
	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//				pieceIds Black				0				1				2				3				4				5				blackKing is 0 also
	//				pieceIds White				6				7				8				9				10			11			whiteKing is 6 also

	private int blKing = 4;
	private int whKing = 60;

	Image[] pieceImages = new Image[12];

	public MoveHandler(){
	}
}
