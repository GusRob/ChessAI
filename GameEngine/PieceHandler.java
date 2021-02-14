package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class PieceHandler{

	private boolean bitBoards[][] = new boolean[12][64];
	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//				pieceIds Black				0				1				2				3				4				5				blackKing is 0 also
	//				pieceIds White				6				7				8				9				10			11			whiteKing is 6 also

	private int blKing = 4;
	private int whKing = 60;

	Image[] pieceImages = new Image[12];

	public PieceHandler(){
		loadImages();
		setupBoard();
	}

	//function called from board class to draw pieces to canvas
	public void paint(Graphics g, Board board){
		for(int p = 0; p<12; p++){
			if(p != 0 && p!= 6){
				for(int i = 0; i<64; i++){
					if(bitBoards[p][i]){
						g.drawImage(pieceImages[p], 2+(i%8)*64, 2+(int)(i/8)*64, board);
					}
				}
			}
		}
		g.drawImage(pieceImages[0], 2+(blKing%8)*64, 2+(int)(blKing/8)*64, board);
		g.drawImage(pieceImages[6], 2+(whKing%8)*64, 2+(int)(whKing/8)*64, board);
	}

	//input - int refering to square  output - integer : 0 represents black, 6 represents white
	public int getPieceColor(int square){
		if(bitBoards[0][square] && bitBoards[6][square]){
			System.exit(0x01);
		}
		int result = -1;
		if(bitBoards[0][square]){
			result = 0;
		}
		if(bitBoards[6][square]){
			result = 6;
		}
		return result;
	}

	//input - int refering to square  output - integer : pieceId as outlined after bitboard definition
	public int getPieceId(int square){
		int col = getPieceColor(square);
		int result = col;
		if(result != -1){
			for(int i = 1; i < 5; i++){
				if(bitBoards[i+col][square]){
					result = i + col;
				}
			}
		}

		return result;
	}

	//function takes two bitboards and returns the result of bitboard1 & bitboard2
	public boolean[] mapAnd(boolean[] arr0, boolean[] arr1){
		boolean[] result = new boolean [64];
		for(int i = 0; i<64; i++){
			result[i] = arr0[i] & arr1[i];
		}
		return result;
	}

	//function to be called when populating bitboards to generate bitboards 0 and 6
	private void generateColorSets(){
		bitBoards[0] = new boolean[64];
		bitBoards[6] = new boolean[64];
		for(int i = 1; i < 5; i++){
			bitBoards[0] = mapAnd(bitBoards[0], bitBoards[i]);
			bitBoards[6] = mapAnd(bitBoards[6], bitBoards[i+6]);
		}
	}

	//function called upon pieceHandler instantiation to populate bitboards with set of pieces
	private void setupBoard(){
		//populate bitBoards
		for(int i = 8; i < 16; i++){
			bitBoards[5][i] = true;		//black pawns
		}
		for(int i = 48; i < 56; i++){
			bitBoards[11][i] = true;		//white pawns
		}
		bitBoards[1][0] = true;	//black rooks
		bitBoards[1][7] = true;
		bitBoards[7][56] = true;	//white rooks
		bitBoards[7][63] = true;
		bitBoards[2][1] = true;  //black knights
		bitBoards[2][6] = true;
		bitBoards[8][57] = true; //white knights
		bitBoards[8][62] = true;
		bitBoards[3][2] = true;  //black bishops
		bitBoards[3][5] = true;
		bitBoards[9][58] = true; //white bishops
		bitBoards[9][61] = true;
		bitBoards[4][3] = true; //queens
		bitBoards[10][59] = true;

		generateColorSets();
	}

	//function called upon pieceHandler instantiation to populate images array
	private void loadImages(){
		pieceImages[0] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/king_black.png");
		pieceImages[1] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/rook_black.png");
		pieceImages[2] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/knight_black.png");
		pieceImages[3] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/bishop_black.png");
		pieceImages[4] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/queen_black.png");
		pieceImages[5] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/pawn_black.png");
		pieceImages[6] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/king_white.png");
		pieceImages[7] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/rook_white.png");
		pieceImages[8] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/knight_white.png");
		pieceImages[9] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/bishop_white.png");
		pieceImages[10] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/queen_white.png");
		pieceImages[11] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/pawn_white.png");
	}
}
