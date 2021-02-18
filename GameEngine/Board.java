package GameEngine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//this class handles drawing to the window and user interaction events
public class Board extends JPanel implements MouseListener, ActionListener{
	Image darkSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/dark_sq.png");
	Image lightSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/images/light_sq.png");
	private boolean isMouseDown = false;
	private boolean isWhiteTurn = true;
	private boolean isWhiteWinner = false;
	private boolean isTie = false;
	private boolean isSelecting = false;
	private int pieceHover = -1;
	private boolean isGameOver = false;

	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//									Black				0				1				2				3				4				5
	//									White				6				7				8				9				10			11
	javax.swing.Timer t = new javax.swing.Timer(10, this);

	PieceHandler pieces = new PieceHandler(this);

	//constructor starts timer t adds mouselistener and calls paint method for first time
	public Board(){
		//constructor initiates game pieces
		t.start();

		addMouseListener(this);
		repaint();
	}

	//paints the dark and light tiles making up the board
	private void paintBoard(Graphics g){
		//draws the board squares every time the game world is updated
		for(int i = 1; i<8; i+=2){
			for(int j = 1; j<8; j+=2){
				g.drawImage(lightSq, 64*i, 64*j, Color.BLACK, this);
				g.drawImage(darkSq, 64*(i-1), 64*j, Color.BLACK, this);
				g.drawImage(lightSq, 64*(i-1), 64*(j-1), Color.BLACK, this);
				g.drawImage(darkSq, 64*i, 64*(j-1), Color.BLACK, this);
			}
		}
	}

	//paints the UI incl. which colour's turn and move list?
	private void paintUI(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(isWhiteTurn ? "White's Turn" : "Black's Turn", 10, 550);
		String checkStr = "";
		if(pieces.getIsInCheck()[isWhiteTurn?1:0]){
			checkStr = "Check!";
			if(pieces.getIsInCheckMate()[isWhiteTurn?1:0]){
				checkStr = "CheckMate!";
			}
		}
		if(isTie){
			checkStr = "StaleMate";
		}
		g.drawString(checkStr, 200, 550);

		if(isSelecting || isGameOver){
			g.setColor(Color.BLACK);
			g2.fillRoundRect(104, 204, 304, 104, 30, 30);
			g.setColor(new Color(210, 150, 0));
			g2.fillRoundRect(106, 206, 300, 100, 25, 25);
			g.setColor(new Color(115, 60, 0));
			g2.fillRoundRect(110, 210, 292, 92, 20, 20);
			g.setColor(new Color(210, 150, 0));
			g2.fillRoundRect(114, 214, 284, 84, 15, 15);
		}
		g.setColor(Color.BLACK);
		String winString = "";
		if(isGameOver){
			if(isTie){
				winString = "Nobody Wins";
			} else {
				if(isWhiteWinner){
					winString = "White Wins";
				} else {
					winString = "Black Wins";
				}
			}
		}
		g.drawString(winString, 180, 270);
		if(isSelecting){
			g.setColor(new Color(0F, 0.0F, 0.0F, 0.2F));
			mouseHover();
			if(pieceHover != -1){
				g2.fillRoundRect(118 + (276/4)*pieceHover, 218, (276/4), 76, 10, 10);
			}
			pieces.paintSelection(g);
		}
		g.setColor(new Color(210, 150, 0));

	}

	//called with repaint() - triggers paint helper functions
	public void paint(Graphics g){
		paintBoard(g);
		pieces.paint(g);
		paintUI(g);
	}

	//called when user is selecting to update which piece the user's mouse is hovering over
	public void mouseHover(){
		int mousePosX = (int)MouseInfo.getPointerInfo().getLocation().getX()-400;
		int mousePosY = (int)MouseInfo.getPointerInfo().getLocation().getY()-100;
		if(mousePosY > 215 && mousePosY < 307){
			for(int i = 0; i < 4; i++){
				if(mousePosX > 118 + i*(276/4) && mousePosX < 118 + (i+1)*(276/4)){
					pieceHover = i;
				}
			}
		}
	}

	//input - MouseEvent  output - integer id of which square the mouse is in, -1 if none
	//called when mouse is pressed and when mouse is released
	public int mouseSquare(MouseEvent e){
		int clickPosX = e.getX();
		int clickPosY = e.getY();
		int result = -1;
		for(int i = 0; i<64; i++){
			boolean inXCol = clickPosX >= 2+64*(i%8)	  && clickPosX <= 66+64*((i%8) );
			boolean inYCol = clickPosY >= 2+64*(int)(i/8) && clickPosY <= 66+64*(int)(i/8);

			if(inXCol && inYCol){
				result = i;
			}
		}
		return result;
	}

	//called when a pawn is at the end of the board, to make the UI request a promotion selection
	public void requestSelection(){isSelecting = true;}

	//called when a selection has been made to close the selection popup
	public void selectionMade(){isSelecting = false;}

	//called when a 'checkmate' is detected, sets gameover etc
	public void declareWinner(boolean isWhiteWinnerVal, boolean isTieVal){
		isWhiteWinner = isWhiteWinnerVal;
		isTie = isTieVal;
		isGameOver = true;
	}

	//getter functions for Board values
	public boolean getTurn(){return isWhiteTurn;}
	public boolean getSelecting(){return isSelecting;}

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		if(!isSelecting && !isGameOver){
			if(!isMouseDown){
				mousePressed(e);
			} else {
				mouseReleased(e);
			}
		} else if (!isGameOver) {
			if(pieceHover != -1){
				int inputTurn = isWhiteTurn?1:0;
				pieces.setProSelect(pieceHover, inputTurn);
				int promotionFile = pieces.promotionAvail(inputTurn);
				pieces.promote(inputTurn, promotionFile);
				selectionMade();
			}
		}
	}
	public void mousePressed(MouseEvent e){
		if(!isSelecting && !isGameOver){
			if(!isMouseDown){
				isMouseDown = true;
				int square = mouseSquare(e);
				if(square != -1){
					int pieceId = pieces.getPieceId(square);
					if(pieceId != -1 && (pieceId>5 == isWhiteTurn)){
						pieces.setHeld(pieceId, square);
					}
				}
			}
		} else {
			mouseClicked(e);
		}
	}
	public void mouseReleased(MouseEvent e){
		isMouseDown = false;
		if(!isGameOver){
			int square = mouseSquare(e);
			if(pieces.getHeldId() != -1){
				if(pieces.placeHeld(square)){
					isWhiteTurn = !isWhiteTurn;
					pieces.queryCheckmate(isWhiteTurn?6:0);
				}
			}
		}
	}

	//overridden actionEvent method
	//called every 10 milliseconds - updates the graphics window
	public void actionPerformed(ActionEvent e){
		repaint();
	}

}
