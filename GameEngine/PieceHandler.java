package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


//class stored infor about all of the pieces and handles their movement
public class PieceHandler{

	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//				pieceIds Black				0				1				2				3				4				5				blackKing is 0 also
	//				pieceIds White				6				7				8				9				10			11			whiteKing is 6 also
	public boolean bitBoards[][] = new boolean[12][64];

	//king squares, an entire bitboard isnt needed because there can only be one
	private int blKing = 4;
	private int whKing = 60;

	//held piece values
	private int heldId = -1;
	private int heldSquare = -1;

	//flags to represent castling - if the king moves both are set to false, if a rook moves the corr. is
	//		addressed by castles[colour][side]	where black = 0 white = 1, left = 0, right = 1
	//		left and right will be according to the user's perspective on the screen
	private boolean castles[][] =	{{true, true}, {true, true}};

	//flags for check where index 0 = black index 1 = white
	private boolean isInCheck[] =	{false, false};

	Board board;
	MoveHandler moves = new MoveHandler(this);

	Image[] pieceImages = new Image[12];

	public PieceHandler(Board init_board){
		board = init_board;
		loadImages();
		setupBoard();
	}

	//function called from board class to draw pieces to canvas
	public void paint(Graphics g){
		for(int p = 0; p<12; p++){
			if(p != 0 && p!= 6){
				for(int i = 0; i<64; i++){
					if(bitBoards[p][i] && i != heldSquare){
						g.drawImage(pieceImages[p], 2+(i%8)*64, 2+(int)(i/8)*64, board);
					}
				}
			}
		}
		if(heldId != 0){
			g.drawImage(pieceImages[0], 2+(blKing%8)*64, 2+(int)(blKing/8)*64, board);
		}
		if(heldId != 6){
			g.drawImage(pieceImages[6], 2+(whKing%8)*64, 2+(int)(whKing/8)*64, board);
		}
		if(heldId != -1){
			int mouseX = (int)Math.round(MouseInfo.getPointerInfo().getLocation().getX()) - 432;
			int mouseY = (int)Math.round(MouseInfo.getPointerInfo().getLocation().getY()) - 132;
			g.drawImage(pieceImages[heldId], mouseX, mouseY, board);
		}
	}

	//getter functions for pieceHandler values
	public int getHeldId(){return heldId;}
	public int getHeldSquare(){return heldSquare;}
	public boolean[] getIsInCheck(){return isInCheck;}
	public int getKing(int col){return (col==0?blKing:whKing);}
	public int getBlKing(){return blKing;}
	public int getWhKing(){return whKing;}
	public boolean[][] getBitBoardsCopy(){
		boolean[][] result = new boolean[12][64];
		for(int i = 0; i<bitBoards.length; i++){
			result[i] = Arrays.copyOf(bitBoards[i], 64);
		}
		return result;
	}

	//setter functions for pieceHandler values
	public void setHeld(int id, int square){
		heldId = id;
		heldSquare = square;
	}
	public void setIsInCheck(boolean[] newVals){
		isInCheck = newVals;
	}
	private void resetHeld(){
		heldId = -1;
		heldSquare = -1;
	}

	//input - int referring to square		output - boolean indicating successful placing
	public boolean placeHeld(int square){
		boolean result = false;
		if(moves.validateTurn(square)){
			movePiece(square);
			moves.updateCheck();
			result = true;
		} else {
			result = false;
		}
		resetHeld();

		return result;

	}

	//input - int referring to square  output - integer : 0 represents black, 6 represents white, -1 if empty
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

	//input - int refering to square  output - integer : pieceId as outlined after bitboard definition -1 otherwise
	public int getPieceId(int square){
		int col = getPieceColor(square);
		int result = col;
		if(result != -1){
			for(int i = 1; i < 6; i++){
				if(bitBoards[i+col][square]){
					result = i + col;
				}
			}
		}
		return result;
	}

	//input - int refering to square and bitBoard  output - integer : pieceId as outlined after bitboard definition -1 otherwise
	//method for use when testing a potential move
	public int getPieceId(int square, boolean[][] bitBoards_tmp){
		int col = getPieceColor(square);
		int result = col;
		if(result != -1){
			for(int i = 1; i < 6; i++){
				if(bitBoards_tmp[i+col][square]){
					result = i + col;
				}
			}
		}
		return result;
	}

	//input - int refering to color and bitBoard  output - integer : square containing king of that col
	//method for use when testing a potential move
	public int getKing(int col, boolean[][] bitBoards_tmp){
		boolean[] resultBoard = Arrays.copyOf(bitBoards_tmp[col], 64);
		for(int n = 1; n < 6; n++){
			for(int i = 0; i<64; i++){
				resultBoard[i] = resultBoard[i] ^ bitBoards_tmp[col+n][i];
			}
		}
		int result = -1;
		for(int i = 0; i<64; i++){
			if(resultBoard[i]){
				result = i;
			}
		}
		return result;
	}

	//ifunction takes the square the class scoped held piece is moving to and puts the piece there
	private void movePiece(int square){
		bitBoards[heldId][heldSquare] = false;
		int colOnSq = getPieceColor(square);
		if(colOnSq != -1){
			bitBoards[getPieceId(square)][square] = false;
			bitBoards[colOnSq][square] = false;
		}
		setPieceId(square, heldId);
	}

	//function takes ints square and pieceId and adds piece to bitboard or throws
	private void setPieceId(int square, int pieceId){
		int result = 0;
		if(pieceId == 0){
			if(getPieceColor(square) == -1){
				blKing = square;
			} else { result = -1; }
		} else if(pieceId == 6){
			if(getPieceColor(square) == -1){
				whKing = square;
			} else { result = -1; }
		} else {
			if(getPieceColor(square) == -1){
				bitBoards[pieceId][square] = true;
			} else { result = -1; }
		}
		if(result == -1){
			System.exit(0x03);
		}
		generateColorSets();
	}

	//function takes two bitboards and returns the result of bitboard1 XOR bitboard2 - overlap throws
	private boolean[] mapXor(boolean[] arr0, boolean[] arr1){
		boolean[] result = new boolean [64];
		for(int i = 0; i<64; i++){
			if(arr0[i] && arr1[i]){
				System.exit(0x02);
			}
			result[i] = arr0[i] ^ arr1[i];
		}
		return result;
	}

	//function to be called when populating bitboards to generate bitboards 0 and 6
	private void generateColorSets(){
		bitBoards[0] = new boolean[64];
		bitBoards[6] = new boolean[64];
		for(int i = 1; i < 6; i++){
			bitBoards[0] = mapXor(bitBoards[0], bitBoards[i]);
			bitBoards[6] = mapXor(bitBoards[6], bitBoards[i+6]);
		}
		if(bitBoards[0][blKing] || bitBoards[6][whKing]){
			System.exit(0x02);
		} else {
			bitBoards[0][blKing] = true;
			bitBoards[6][whKing] = true;
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
		pieceImages[0] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/king_black.png");
		pieceImages[1] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/rook_black.png");
		pieceImages[2] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/knight_black.png");
		pieceImages[3] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/bishop_black.png");
		pieceImages[4] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/queen_black.png");
		pieceImages[5] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/pawn_black.png");
		pieceImages[6] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/king_white.png");
		pieceImages[7] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/rook_white.png");
		pieceImages[8] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/knight_white.png");
		pieceImages[9] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/bishop_white.png");
		pieceImages[10] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/queen_white.png");
		pieceImages[11] = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/pawn_white.png");
	}
}
