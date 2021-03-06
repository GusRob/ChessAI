package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


//class stored infor about all of the pieces and handles their movement
public class PieceHandler{

	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//				pieceIds Black				0				1				2				3				4				5				blackKing is 0 also
	//				pieceIds White				6				7				8				9				10			11			whiteKing is 6 also
	// final bitBoard array is for further board info:
	//	bits 0-15 are for passant flags, one for each pawn [first 8 are for black, second for white] [1 means passant is available]
	//  bits 16-19 are for castles flags, one for each rook [left then right, black then white] [1 means castling is unavailable]
	//  bits 20 and 21 are for check flags [20 is black, 21 is white] [1 means that colour is in check]
	//  bits 22 and 23 are for checkmate flags [22 is black, 23 is white] [1 means that colour is in checkmate]
	//  bit 24 is for stalemates [1 represents a stalemate]
	public boolean bitBoards[][] = new boolean[13][64];

	//king squares, an entire bitboard isnt needed because there can only be one
	private int blKing = 4;
	private int whKing = 60;

	//held piece values
	private int heldId = -1;
	private int heldSquare = -1;

	//promotion selection is the id of the selected piece
	private int proSelectId = -1;

	//flags for check where index 0 = black index 1 = white
	private boolean isInCheck[] =	{false, false};
	private boolean isInCheckMate[] =	{false, false};

	public Board board;
	public MoveHandler moves = new MoveHandler(this);

	Image[] pieceImages = new Image[12];

	public PieceHandler(Board init_board){
		board = init_board;
		loadImages();
		setupBoard();
	}

	//function called from board class to draw pieces to selection box
	public void paintSelection(Graphics g){
		g.drawImage(pieceImages[!board.getTurn()?10:4], 122, 227, board);
		g.drawImage(pieceImages[!board.getTurn()?9:3], 122 + 276/4, 227, board);
		g.drawImage(pieceImages[!board.getTurn()?8:2], 122 + 276/2, 227, board);
		g.drawImage(pieceImages[!board.getTurn()?7:1], 122 + 3*276/4, 227, board);
	}

	//function called from board class to draw pieces to canvas
	public void paint(Graphics g, boolean isPlayerWhite){
		for(int p = 0; p<12; p++){
			if(p != 0 && p!= 6){
				for(int i = 0; i<64; i++){
					if(bitBoards[p][i] && i != heldSquare){
						if(isPlayerWhite){
							g.drawImage(pieceImages[p], 2+(i%8)*64, 2+(int)(i/8)*64, board);
						} else {
							g.drawImage(pieceImages[p], 2+((63-i)%8)*64, 2+(int)((63-i)/8)*64, board);
						}
					}
				}
			}
		}
		if(heldId != 0){
			if(isPlayerWhite){
				g.drawImage(pieceImages[0], 2+(blKing%8)*64, 2+(int)(blKing/8)*64, board);
			} else {
				g.drawImage(pieceImages[0], 2+((63-blKing)%8)*64, 2+(int)((63-blKing)/8)*64, board);
			}
		}
		if(heldId != 6){
			if(isPlayerWhite){
				g.drawImage(pieceImages[6], 2+(whKing%8)*64, 2+(int)(whKing/8)*64, board);
			} else {
				g.drawImage(pieceImages[6], 2+((63-whKing)%8)*64, 2+(int)((63-whKing)/8)*64, board);

			}
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
	public boolean[] getIsInCheckMate(){return isInCheckMate;}
	public int getKing(int col){return (col==0?blKing:whKing);}
	public int getBlKing(){return blKing;}
	public int getWhKing(){return whKing;}
	public boolean getPassant(int a, int b){return bitBoards[12][a*8+b];}
	public boolean getCastles(int a, int b){return bitBoards[12][16+a*2+b];}

	//function to return a bitBoard duplicate to allow check tested etc without editting current board
	public boolean[][] getBitBoardsCopy(){
		boolean[][] result = new boolean[13][64];
		for(int i = 0; i<13; i++){
			for(int j = 0; j < 64; j++){
				result[i][j] = bitBoards[i][j];
			}
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
	public void setIsInCheckMate(boolean[] newVals){
		isInCheckMate = newVals;
	}
	public void resetHeld(){
		heldId = -1;
		heldSquare = -1;
	}
	public void setProSelect(int selectedId, int turn){
		proSelectId = 10-selectedId - 6*turn;
	}

	//function checks to see if a pawn can be promoted
	public int promotionAvail(int turn){
		int result = -1;
		for(int i = 0; i  < 8; i++){
			if(bitBoards[11-(6*turn)][i + 56*turn]){
				result = i;
			}
		}
		return result;
	}

	//function called to reset all piece values to initial states
	public void resetAll(){
		blKing = 4;
		whKing = 60;
		heldId = -1;
		heldSquare = -1;
		setupBoard();
	}

	//function called to change the pawn being promoted into the piece selected
	public void promote(int turn, int promotingFile){
		bitBoards[11-(6*turn)][promotingFile + 56*turn] = false;
		bitBoards[6-(6*turn)][promotingFile + 56*turn] = false;
		setPieceId(promotingFile + 56*turn, proSelectId);
	}

	//function to call the movehandler checkmate and respond accordingly
	public void queryCheckmate(){
		board.repaint();
		boolean isBlackAbleToMove = moves.ableToMove(0, getBitBoardsCopy());
		boolean isWhiteAbleToMove = moves.ableToMove(6, getBitBoardsCopy());
		isInCheckMate[0] = !isBlackAbleToMove && isInCheck[0];
		isInCheckMate[1] = !isWhiteAbleToMove && isInCheck[1];
		boolean isTie = (!board.getTurn() && !isBlackAbleToMove && !isInCheck[0]) || (board.getTurn() && !isWhiteAbleToMove && !isInCheck[0]);
		int n = 0;
		for(int i = 0; i < 64; i++){
			if(bitBoards[0][i] || bitBoards[6][i]){
				n++;
			}
		}
		if(n == 2){
			isTie = true;
		}
		if(isInCheckMate[0] || isInCheckMate[1] || isTie){
			/*System.out.println(isTie?"tie":((isInCheckMate[0]?"white ":"black ") + "cant go"));
			System.out.println("isBlackAbleToMove " + moves.ableToMove(0, true) );
			System.out.println("isWhiteAbleToMove " + moves.ableToMove(6, true) );
			System.out.println("isInCheckMate[0] " + isInCheckMate[0] );
			System.out.println("isInCheckMate[1] " + isInCheckMate[1] );
			System.out.println("isTie " + isTie );
			System.out.println("Board Dump:");
			for(int i = 0; i < 64; i++){
				System.out.print(getPieceId(i) + " ");
				if(i % 8 == 7){System.out.println();}
			}*/
			board.declareWinner(isInCheckMate[1], isTie);
		}
	}

	//input - int referring to square		output - boolean indicating successful placing
	public boolean placeHeld(int square){
		boolean result = false;
		 int turn = board.getTurn()?0:1;
		if(moves.validateTurn(square, heldSquare, getBitBoardsCopy())){
			movePiece(square);
			for(int i = 0; i < 8; i++){
				bitBoards[12][turn*8+i] = false;
			}
			int promotingFile = promotionAvail(turn);
			if(promotingFile != -1){
				board.requestSelection();
			}
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

	//input - int referring to square and tmp bitBoard  output - integer : 0 represents black, 6 represents white, -1 if empty
	//method for use when testing a potential move
	public int getPieceColor(int square, boolean[][] bitBoards_tmp){
		if(bitBoards_tmp[0][square] && bitBoards_tmp[6][square]){
			//System.exit(0x01);
		}
		int result = -1;
		if(bitBoards_tmp[0][square]){
			result = 0;
		}
		if(bitBoards_tmp[6][square]){
			result = 6;
		}

		return result;
	}

	//input - int refering to square and tmp bitBoard  output - integer : pieceId as outlined after bitboard definition -1 otherwise
	//method for use when testing a potential move
	public int getPieceId(int square, boolean[][] bitBoards_tmp){
		int col = getPieceColor(square, bitBoards_tmp);
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
		//en passant move
		int turn = board.getTurn()?6:0;
		if(heldId == 5 + turn){ // if piece is a pawn
			if(colOnSq == -1){
				//if square moving to is empty
				int direction = turn==0?1:-1; //if white - direction pos // if black - direction neg
				for(int i = -1; i <2; i +=2){
					if( square == heldSquare + 8*direction + i && (int)(square/8) ==(int)((square+i)/8) ){
						//if move is forward diagonal
						bitBoards[5 + (turn==6?0:6)][heldSquare+i] = false; //remove pawn from square behind where pawn is moving to
						bitBoards[turn==6?0:6][heldSquare+i] = false;
					}
				}
				if(square == heldSquare + 16*direction){ // if move is double opening move allow passant for one turn
					bitBoards[12][(turn==0?0:8)+(square%8)] = true;
				}
			}
		}
		//castle move
		int castleIndex = turn==0?0:2;
		if(bitBoards[12][castleIndex] || bitBoards[12][castleIndex + 1]){ // if moving colour is able to castle
			if(heldId == turn){ // if piece is a king
				bitBoards[12][castleIndex] = false;
				bitBoards[12][castleIndex + 1] = false;
				if(Math.abs(heldSquare-square) == 2){//if king moving LorR 2
					int direction = (square>heldSquare)?-1:1;//1 for left, -1 for right
					int corner = (square>heldSquare)?7:0;//0 for left, 7 for right
					int kingRank = (int)(square/8);
					int rookSq = (kingRank*8) + corner;
					//move rook
					bitBoards[turn+1][rookSq] = false;
					bitBoards[turn+1][rookSq] = false;
					setPieceId(square+direction, heldId+1);
				}
			} else if (heldId == 1 + turn){ // if piece is a rook
				if((turn/6)*56 == heldSquare){ // left side rook
					bitBoards[12][castleIndex] = false;
				} else if (7+((turn/6)*56) == heldSquare){ // right side rook
					bitBoards[12][castleIndex +1] = false;
				}
			}
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
			//System.exit(0x03);
			while(true){}
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
		bitBoards = new boolean[13][64];
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

		for(int i = 0; i < 4; i++){
			bitBoards[12][16 + i] = true; //castling flags
		}

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
