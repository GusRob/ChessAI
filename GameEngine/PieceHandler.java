package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class PieceHandler{

	private boolean bitBoards[][] = new boolean[12][64];
	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//				pieceIds Black				0				1				2				3				4				5				blackKing is 0 also
	//				pieceIds White				6				7				8				9				10			11			whiteKing is 6 also

	public PieceHandler(){

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

	public boolean[] mapAnd(boolean[] arr0, boolean[] arr1){
		boolean[] result = new boolean [64];
		for(int i = 0; i<64; i++){
			result[i] = arr0[i] & arr1[i];
		}
		return result;
	}

	private void generateColorSets(){
		bitBoards[0] = new boolean[64];
		bitBoards[6] = new boolean[64];
		for(int i = 1; i < 5; i++){
			bitBoards[0] = mapAnd(bitBoards[0], bitBoards[i]);
			bitBoards[6] = mapAnd(bitBoards[6], bitBoards[i+6]);
		}
	}

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
}
