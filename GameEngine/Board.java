package GameEngine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Board extends JPanel implements MouseListener{
	Image darkSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/dark_sq.png");
	Image lightSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/light_sq.png");
	boolean mouseDown = false;

	// stores all information about game world
	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	boolean[][] bitBoard = new boolean[12][64];
	int blKing = 4;
	int whKing = 60;


	public Board(){
		//constructor initiates game pieces
		generatePieces();
		repaint();
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
	}

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		mouseDown = true;

	}
	public void mouseReleased(MouseEvent e){
		mouseDown = false;
	}
	public void mouseClicked(MouseEvent e){}

	public void drawPieces(Graphics g){
		Image[] pieceImages = new Image[12];
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

	public void generatePieces(){
		//populate bitBoards
		for(int i = 0; i < 16; i++){
			bitBoard[0][i] = true;		//black pieces
		}
		for(int i = 48; i < 64; i++){
			bitBoard[6][i] = true;		//white pieces
		}
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


	}

}
