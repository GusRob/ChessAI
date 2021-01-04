package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;

enum pieceType {
	PAWN,
	ROOK,
	BISHOP,
	KNIGHT,
	QUEEN,
	KING
}

enum pieceColor {
	BLACK,
	WHITE
}


public class Piece{

	int squareX;
	int squareY;
	final pieceColor side;
	pieceType type;
	Image pieceIm;
	boolean inHand = false;


	public Piece(int startX, int startY, pieceColor init_side, pieceType init_type, Image startIm){
		squareX = startX;
		squareY = startY;
		side = init_side;
		type = init_type;
		pieceIm = startIm;
	}

}



/*Image blPawn = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/pawn_black.png");
Image blRook = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/rook_black.png");
Image blBish = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/bishop_black.png");
Image blKnig = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/knight_black.png");
Image blKing = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/king_black.png");
Image blQuee = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/queen_black.png");
Image whPawn = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/pawn_white.png");
Image whRook = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/rook_white.png");
Image whBish = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/bishop_white.png");
Image whKnig = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/knight_white.png");
Image whKing = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/king_white.png");
Image whQuee = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/queen_white.png");
blackSet.add(new Piece(0, 0, pieceColor.BLACK, pieceType.ROOK, blRook));
blackSet.add(new Piece(7, 0, pieceColor.BLACK, pieceType.ROOK, blRook));
blackSet.add(new Piece(1, 0, pieceColor.BLACK, pieceType.KNIGHT, blKnig));
blackSet.add(new Piece(6, 0, pieceColor.BLACK, pieceType.KNIGHT, blKnig));
blackSet.add(new Piece(2, 0, pieceColor.BLACK, pieceType.BISHOP, blBish));
blackSet.add(new Piece(5, 0, pieceColor.BLACK, pieceType.BISHOP, blBish));
blackSet.add(new Piece(4, 0, pieceColor.BLACK, pieceType.KING, blKing));
blackSet.add(new Piece(3, 0, pieceColor.BLACK, pieceType.QUEEN, blQuee));
whiteSet.add(new Piece(0, 7, pieceColor.WHITE, pieceType.ROOK, whRook));
whiteSet.add(new Piece(7, 7, pieceColor.WHITE, pieceType.ROOK, whRook));
whiteSet.add(new Piece(1, 7, pieceColor.WHITE, pieceType.KNIGHT, whKnig));
whiteSet.add(new Piece(6, 7, pieceColor.WHITE, pieceType.KNIGHT, whKnig));
whiteSet.add(new Piece(2, 7, pieceColor.WHITE, pieceType.BISHOP, whBish));
whiteSet.add(new Piece(5, 7, pieceColor.WHITE, pieceType.BISHOP, whBish));
whiteSet.add(new Piece(4, 7, pieceColor.WHITE, pieceType.KING, whKing));
whiteSet.add(new Piece(3, 7, pieceColor.WHITE, pieceType.QUEEN, whQuee));
for(int i = 0; i<8; i++){
	whiteSet.add(new Piece(i, 6, pieceColor.WHITE, pieceType.QUEEN, whPawn));
	blackSet.add(new Piece(i, 1, pieceColor.BLACK, pieceType.QUEEN, blPawn));
}*/
