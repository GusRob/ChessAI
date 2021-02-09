package GameEngine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

enum bitWise{
	AND,
	OR
}

public class Board extends JPanel implements MouseListener, ActionListener{
	Image darkSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/dark_sq.png");
	Image lightSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/light_sq.png");
	Image moveDot = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/brown_dot.png");
	Image[] pieceImages = new Image[12];
	boolean mouseDown = false;

	// stores all information about game world
	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//									Black		0						1				2				3				4				5
	//									White		6						7				8				9				10			11
	boolean[][] bitBoard = new boolean[12][64];
	int blKing = 4;
	int whKing = 60;

	int mx = 0;
	int my = 0;
	int pieceHeld = -1;
	int pieceHeldFrom = -1;
	int pieceHeldTo = -1;

	boolean[] pieceHeldMoves = new boolean[64];


	public Board(){
		//constructor initiates game pieces
		javax.swing.Timer t = new javax.swing.Timer(10, this);
		t.start();

		addMouseListener(this);
		generatePieces();
		repaint();

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

	public void paint(Graphics g){
		//draws the board squares then pieces every time the game world is updated
		g.setColor(Color.GREEN);
		for(int i = 1; i<8; i+=2){
			for(int j = 1; j<8; j+=2){
				g.drawImage(lightSq, 64*i, 64*j, Color.BLACK, this);
				g.drawImage(darkSq, 64*(i-1), 64*j, Color.BLACK, this);
				g.drawImage(lightSq, 64*(i-1), 64*(j-1), Color.BLACK, this);
				g.drawImage(darkSq, 64*i, 64*(j-1), Color.BLACK, this);
			}
		}
		drawPieces(g);

		if(pieceHeld != -1) {
			for(int i = 0; i < 64; i++){
				if(pieceHeldMoves[i]){
					g.drawImage(moveDot, 19+64*(i%8), 19+64*(int)(i/8), this);
				}
			}
			g.drawImage(pieceImages[pieceHeld], mx, my, this);
		}

	}

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		if(!mouseDown){
			mouseDown = true;
			int clickPosX = e.getX();
			int clickPosY = e.getY();
			for(int i = 0; i<64; i++){
				boolean inXCol = clickPosX >= 2+64*(i%8)	  && clickPosX <= 66+64*((i%8) );
				boolean inYCol = clickPosY >= 2+64*(int)(i/8) && clickPosY <= 66+64*(int)(i/8);

				if(inXCol && inYCol){
					pieceHeldFrom = i;
					if(i == blKing){
						pieceHeld = 0;
					} else if(i == whKing){
						pieceHeld = 6;
					} else {
						pieceHeld = getPieceIdFromSquare(i);
					}
					if(pieceHeld != -1){
						removePieceFromSquare(pieceHeldFrom, pieceHeld);
						pieceHeldMoves = calculateMoves(pieceHeld, pieceHeldFrom);
						System.out.println(pieceHeldFrom);
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e){
		System.out.println(pieceHeldFrom);

		mouseDown = false;
		int clickPosX = e.getX();
		int clickPosY = e.getY();
		for(int i = 0; i<64; i++){
			boolean inXCol = clickPosX >= 2+64*(i%8)	  && clickPosX <= 66+64*((i%8) );
			boolean inYCol = clickPosY >= 2+64*(int)(i/8) && clickPosY <= 66+64*(int)(i/8);

			if(inXCol && inYCol){
				if(pieceHeldMoves[i] == true){
					if(pieceHeld < 6){ //isblack
						if(bitBoard[0][i] == false && bitBoard[6][i] == true) {
							removePieceFromSquare(i, pieceHeld);
						}
					} else { //iswhite
						if(bitBoard[6][i] == false && bitBoard[0][i] == true) {
							removePieceFromSquare(i, pieceHeld);
						}
					}
					addPieceToSquare(pieceHeld, i);
				} else {
					System.out.println(pieceHeldFrom);
					addPieceToSquare(pieceHeld, pieceHeldFrom);
				}
			}
		}
		pieceHeld = -1;
		pieceHeldFrom = -1;
	}

	//overridden actionEvent methods
	public void actionPerformed(ActionEvent e){
		mx = MouseInfo.getPointerInfo().getLocation().x - 430;
		my = MouseInfo.getPointerInfo().getLocation().y - 130;
		repaint();
	}

	public boolean[] calculateMoves(int pieceId, int square){
		boolean[] result = new boolean[64];

		int pieceType = (pieceId>5)? (pieceId-6) : pieceId;
		if(pieceType == 0){
			result = calculateKingMoves(pieceId, square);
		} else if(pieceType == 1){
			result = calculateRookMoves(pieceId, square);
		} else if(pieceType == 2){
			result = calculateKnightMoves(pieceId, square);
		} else if(pieceType == 3){
			result = calculateBishopMoves(pieceId, square);
		} else if(pieceType == 4){
			result = calculateQueenMoves(pieceId, square);
		} else if(pieceType == 5){
			result = calculatePawnMoves(pieceId, square);
		}
		return result;
	}

	public boolean[] calculateKingMoves(int pieceId, int square){
		boolean[] result = new boolean[64];

		boolean isBlack = pieceId==0;
		int color = isBlack?0:6;
		int x = square%8;
		int y = (int)(square/8);


		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				if(i != 0 && j != 0) {
					if(x + i > 0 && x + i < 8 && y + j > 0 && y + j < 8){
						if(!bitBoard[color][square + i + 8*j]){
							result[square + i + 8*j] = true;
						}
					}
				}
			}
		}

		return result;
	}

	public boolean[] calculateQueenMoves(int pieceId, int square){

		boolean[] result = applyBitwise(calculateRookMoves(pieceId-3, square), calculateBishopMoves(pieceId-1, square), bitWise.OR);

		return result;
	}

	public boolean[] calculateBishopMoves(int pieceId, int square){
		boolean[] result = new boolean[64];

		boolean[] directions = {true, true, true, true};
		boolean isBlack = pieceId==3;
		int color = isBlack?0:6;
		int oppColor = isBlack?6:0;
		int x = square%8;
		int y = (int)(square/8);

		for(int n = 1; n < 8; n++){
			int d = 0;
			for(int i = -1; i < 2; i+=2){
				for(int j = -1; j < 2; j+=2){
					if(directions[d] && x + i*n > -1 && x + i*n < 8 && y + j*n > -1 && y + j*n < 8) {
						if(!bitBoard[color][square + j*n*8 + i*n]){
							result[square + j*n*8 + i*n] = true;
						} else {
							directions[d] = false;
						}
						if(bitBoard[oppColor][square + j*n*8 + i*n]){
							directions[d] = false;
						}
					}
					d++;
				}
			}
		}
		return result;
	}

	public boolean[] calculateKnightMoves(int pieceId, int square){
		boolean[] result = new boolean[64];
		boolean isBlack = pieceId==2;
		int color = isBlack?0:6;
		int x = square%8;
		int y = (int)(square/8);

		for(int i = -1; i < 2; i+=2){
			for(int j = -1; j<2; j+=2){
				if(x + i*2 > -1 && x + i*2 < 8 && y + j > -1 && y + j < 8){
					if(!bitBoard[color][square + j*8 + i*2]){
						result[square + j*8 + i*2] = true;
					}
				}
				if(x + i > -1 && x + i < 8 && y + j*2 > -1 && y + j*2 < 8){
					if(!bitBoard[color][square + j*16 + i]){
						result[square + j*16 + i] = true;
					}
				}
			}
		}
		return result;
	}

	public boolean[] calculateRookMoves(int pieceId, int square){
		boolean[] result = new boolean[64];

		boolean[] directions = {true, true, true, true};
		boolean isBlack = pieceId==1;
		int color = isBlack?0:6;
		int oppColor = isBlack?6:0;
		int x = square%8;
		int y = (int)(square/8);

		for(int n = 1; n < 8; n++){
			int d = 0;
			for(int i = -1; i < 2; i+=2){
				if(directions[d] && x + i*n > -1 && x + i*n < 8) {
					if(!bitBoard[color][square + i*n]){
						result[square + i*n] = true;
					} else {
						directions[d] = false;
					}
					if(bitBoard[oppColor][square + i*n]){
						directions[d] = false;
					}
				}
				d++;

				if(directions[d] && y + i*n > -1 && y + i*n < 8) {
					if(!bitBoard[color][square + i*8*n]){
						result[square + i*8*n] = true;
					} else {
						directions[d] = false;
					}
					if(bitBoard[oppColor][square + i*8*n]){
						directions[d] = false;
					}
				}
				d++;
			}
		}
		return result;
	}

	public boolean[] calculatePawnMoves(int pieceId, int square){
		boolean[] result = new boolean[64];
		boolean isBlack = pieceId==5;
		if(square >= (isBlack?8:48) && square <= (isBlack?15:55)){
			if(!bitBoard[isBlack?0:6][square + (isBlack?16:-16)] && !bitBoard[isBlack?6:0][square + (isBlack?16:-16)]){
				result[square + (isBlack?16:-16)] = true;
			}
		}
		if(!bitBoard[isBlack?0:6][square + (isBlack?8:-8)] && !bitBoard[isBlack?6:0][square + (isBlack?8:-8)]){
			result[square + (isBlack?8:-8)] = true;
		}
		if(square % 8 != 0){
			if(bitBoard[isBlack?6:0][square + (isBlack?9:-9)]){
				result[square + (isBlack?9:-9)] = true;
			}
		}
		if(square % 8 != 7){
			if(bitBoard[isBlack?6:0][square + (isBlack?7:-7)]){
				result[square + (isBlack?7:-7)] = true;
			}
		}
		return result;
	}

	public void drawPieces(Graphics g){
		for(int p = 0; p<12; p++){
			if(p != 0 && p!= 6){
				for(int i = 0; i<64; i++){
					if(bitBoard[p][i]){
						g.drawImage(pieceImages[p], 2+(i%8)*64, 2+(int)(i/8)*64, this);
					}
				}
			}
		}
		g.drawImage(pieceImages[0], 2+(blKing%8)*64, 2+(int)(blKing/8)*64, this);
		g.drawImage(pieceImages[6], 2+(whKing%8)*64, 2+(int)(whKing/8)*64, this);
	}

	public void removePieceFromSquare(int square, int pieceId){
		if(square >= 64) {
			System.exit(0);
		}
		if(bitBoard[pieceId][square]){
			if(pieceId > 6){
				bitBoard[6][square] = false;
			} else {
				bitBoard[0][square] = false;
			}
			bitBoard[pieceId][square] = false;
		} else if (pieceId == 0) {
			bitBoard[0][square] = false;
		} else if (pieceId == 6) {
			bitBoard[1][square] = false;
		} else {
			System.exit(1);
		}
	}

	public void addPieceToSquare(int pieceId, int square){
		if(!bitBoard[pieceId][square]){
			if(pieceId == 6){
				whKing = square;
			} else if(pieceId == 0){
				blKing = square;
			}
			if(pieceId >= 6){
				bitBoard[6][square] = true;
			} else {
				bitBoard[0][square] = true;
			}
			bitBoard[pieceId][square] = true;
		} else if (pieceId == 0) {
			bitBoard[0][square] = true;
		} else if (pieceId == 6) {
			bitBoard[1][square] = true;
		} else {
			System.exit(1);
		}
	}

	public int getPieceIdFromSquare(int n){
		int result = -1;
		for(int i = 1; i<12; i++){
			if(bitBoard[i][n] && i != 6){
				result = i;
				break;
			}
		}
		return result;
	}

	public void generatePieceSets(){
		for(int i = 1; i < 6; i++){
			bitBoard[0] = applyBitwise(bitBoard[0], bitBoard[i], bitWise.OR);
		}
		for(int i = 7; i < 12; i++){
			bitBoard[6] = applyBitwise(bitBoard[6], bitBoard[i], bitWise.OR);
		}
	}

	public boolean[] applyBitwise(boolean[] bitBoard1, boolean[] bitBoard2, bitWise op){
		boolean[] result = new boolean[64];
		if(op == bitWise.AND){
			for(int i = 0; i<64; i++){
				result[i] = bitBoard1[i] && bitBoard2[i];
			}
		} else if(op == bitWise.OR){
			for(int i = 0; i<64; i++){
				result[i] = bitBoard1[i] || bitBoard2[i];
			}
		}
		return result;
	}

	public void generatePieces(){
		//populate bitBoards
		for(int i = 8; i < 16; i++){
			bitBoard[5][i] = true;		//black pawns
		}
		for(int i = 48; i < 56; i++){
			bitBoard[11][i] = true;		//white pawns
		}
		bitBoard[1][0] = true;	//black rooks
		bitBoard[1][7] = true;
		bitBoard[7][56] = true;	//white rooks
		bitBoard[7][63] = true;
		bitBoard[2][1] = true;  //black knights
		bitBoard[2][6] = true;
		bitBoard[8][57] = true; //white knights
		bitBoard[8][62] = true;
		bitBoard[3][2] = true;  //black bishops
		bitBoard[3][5] = true;
		bitBoard[9][58] = true; //white bishops
		bitBoard[9][61] = true;
		bitBoard[4][3] = true; //queens
		bitBoard[10][59] = true;

		generatePieceSets();

	}

}
