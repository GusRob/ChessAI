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
	private boolean isMenuOpen = false;
	private int menuHover = -1;
	private boolean isNewGame = true;
	private boolean isPlayerWhite = true;


	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces;
	//									Black				0				1				2				3				4				5
	//									White				6				7				8				9				10			11
	javax.swing.Timer t = new javax.swing.Timer(10, this);

	PieceHandler pieces = new PieceHandler(this);
	Thoth thoth = new Thoth(pieces, 0);

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
			paintPopupWindow(g, 100, 200, 300, 100);
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
		g.fill3DRect(437, 512, 75, 50, true);
		g.fill3DRect(362, 512, 75, 50, true);
		g.setColor(Color.BLACK);
		g.fillRect(460, 522, 10, 30);
		g.fillRect(480, 522, 10, 30);
		if(isMenuOpen){
			paintPopupWindow(g, 150, 100, 200, 300);
			g.setColor(new Color(0F, 0.0F, 0.0F, 0.2F));
			mouseHover();
			if(menuHover != -1){
				g2.fillRoundRect(168, 118 +(276/4)*menuHover, 176, (276/4), 10, 10);
			}
			g.setColor(Color.BLACK);
			Rectangle bounds = new Rectangle(168, 118, 176, 276/4);
			drawCenteredString(g, "Return", bounds, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			drawCenteredString(g, "New Game", bounds, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			drawCenteredString(g, "Fun Button", bounds, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			drawCenteredString(g, "Exit", bounds, new Font("Arial", Font.PLAIN, 30));
		}
		if(isNewGame){
			paintPopupWindow(g, 150, 100, 200, 300);
			g.setColor(new Color(0F, 0.0F, 0.0F, 0.2F));
			mouseHover();
			if(Math.abs(menuHover) != 1){
				g2.fillRoundRect(168, 118 +(276/4)*menuHover, 176, (276/4), 10, 10);
			}
			g.setColor(Color.BLACK);
			Rectangle bounds = new Rectangle(168, 118, 176, 276/4);
			drawCenteredString(g, "Start", bounds, new Font("Arial", Font.BOLD, 30));
			bounds.y += 276/4;
			drawCenteredString(g, "Color:", bounds, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			drawCenteredString(g, isPlayerWhite?"White":"Black", bounds, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			drawCenteredString(g, "Exit", bounds, new Font("Arial", Font.PLAIN, 30));
		}
	}

	//helper function to draw centered strings concisely
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
    FontMetrics metrics = g.getFontMetrics(font);
    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
    g.setFont(font);
    g.drawString(text, x, y);
}

	//called as part of paintUI to draw a rounded recangle popup on top of board and pieces
	public void paintPopupWindow(Graphics g, int x, int y, int width, int height){
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g2.fillRoundRect(x + 4, y + 4, width + 4, height + 4, 30, 30);
		g.setColor(new Color(210, 150, 0));
		g2.fillRoundRect(x + 6, y + 6, width, height, 25, 25);
		g.setColor(new Color(115, 60, 0));
		g2.fillRoundRect(x + 10, y + 10, width - 8, height - 8, 20, 20);
		g.setColor(new Color(210, 150, 0));
		g2.fillRoundRect(x + 14, y + 14, width - 16, height - 16, 15, 15);
	}

	//called with repaint() - triggers paint helper functions
	public void paint(Graphics g){
		paintBoard(g);
		pieces.paint(g, isPlayerWhite);
		paintUI(g);
	}

	//called when user is selecting to update which piece the user's mouse is hovering over
	public void mouseHover(){
		int mousePosX = (int)MouseInfo.getPointerInfo().getLocation().getX()-400;
		int mousePosY = (int)MouseInfo.getPointerInfo().getLocation().getY()-100;
		boolean isMouseInBox = false;
		if(mousePosY > 215 && mousePosY < 307){
			for(int i = 0; i < 4; i++){
				if(mousePosX > 118 + i*(276/4) && mousePosX < 118 + (i+1)*(276/4)){
					pieceHover = i;
					isMouseInBox = true;
				}
			}
		}
		if(!isMouseInBox){
			pieceHover = -1;
		}
		isMouseInBox = false;
		if(mousePosX > 165 && mousePosX < 357){
			for(int i = 0; i < 4; i++){
				if(mousePosY > 118 + i*(276/4) && mousePosY < 118 + (i+1)*(276/4)){
					menuHover = i;
					isMouseInBox = true;
				}
			}
		}
		if(!isMouseInBox){
			menuHover = -1;
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

		if(!isPlayerWhite && result != -1){
			result = 63-result;
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

	//calls for a turn from the ai
	public void makeBotTurn(Computer bot){
		int[] move = bot.chooseFirstMove();
		if(move[0] != -1){
			int pieceId = pieces.getPieceId(move[0]);
			if(pieceId != -1 && (pieceId>5 == isWhiteTurn)){
				pieces.setHeld(pieceId, move[0]);
				if(pieces.placeHeld(move[1])){
					pieces.queryCheckmate(isWhiteTurn?6:0);
				}
			}
		}
		if(isSelecting){
			int compHover = bot.choosePromotion();
			int inputTurn = isWhiteTurn?0:1;
			pieces.setProSelect(compHover, inputTurn);
			int promotionFile = pieces.promotionAvail(inputTurn);
			pieces.promote(inputTurn, promotionFile);
			selectionMade();
		}
		nextTurn();
	}

	//called to change isWhiteTurn and call the AI to move if it is their turn
	public void nextTurn(){
		isWhiteTurn = !isWhiteTurn;
		if(isWhiteTurn != isPlayerWhite){
			//call ai to make a turn
			makeBotTurn(thoth);
		}
	}

	//getter functions for Board values
	public boolean getTurn(){return isWhiteTurn;}
	public boolean getSelecting(){return isSelecting;}
	public boolean getIsPlayerWhite(){return isPlayerWhite;}

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		int mouseY = e.getY();
		if(!isSelecting && !isGameOver && !isMenuOpen &&!isNewGame){
			if(!isMouseDown){
				mousePressed(e);
			} else {
				mouseReleased(e);
			}
		} else if (!isGameOver && !isMenuOpen && !isNewGame) {
			if(pieceHover != -1){
				int inputTurn = isWhiteTurn?1:0;
				pieces.setProSelect(pieceHover, inputTurn);
				int promotionFile = pieces.promotionAvail(inputTurn);
				pieces.promote(inputTurn, promotionFile);
				selectionMade();
			}
		} else if(isMenuOpen && !isNewGame){
			if(menuHover == 0){ //resume
				isMenuOpen = false;
			} else if(menuHover == 1){ //newGame
				isNewGame = true;
				pieces.resetAll();
				isWhiteTurn = true;
				isWhiteWinner = false;
				isTie = false;
				isGameOver = false;
				isMenuOpen = false;
			} else if(menuHover == 2){ //fun button
				//do nothing
			} else if(menuHover == 3){ //exit game
				System.exit(0);
			}
		} else if(isNewGame){
			if(menuHover == 0){ //Start
				thoth = new Thoth(pieces, isPlayerWhite?0:6);
				isNewGame = false;
				if(isPlayerWhite != isWhiteTurn){
					makeBotTurn(thoth);
				}
			} else if(menuHover == 1){ //not a button
				//do nothing
			} else if(menuHover == 2){ //Change color
				isPlayerWhite = !isPlayerWhite;
				//thoth = new Thoth(pieces, isPlayerWhite?0:6);
			} else if(menuHover == 3){ //exit game
				System.exit(0);
			}
		}
		if(e.getX() > 437 && mouseY > 512){
			isMenuOpen = !isMenuOpen;
		}
	}
	public void mousePressed(MouseEvent e){
		if(!isSelecting && !isGameOver && !isMenuOpen && (isWhiteTurn == isPlayerWhite)){
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
		}
	}
	public void mouseReleased(MouseEvent e){
		isMouseDown = false;
		if(!isGameOver && !isMenuOpen){
			int square = mouseSquare(e);
			if(square != -1){
				if(pieces.getHeldId() != -1){
					if(pieces.placeHeld(square)){
						pieces.queryCheckmate(isWhiteTurn?0:6);
						nextTurn();
					}
				}
			}
		}
	}

	//overridden actionEvent method
	//called every 10 milliseconds - updates the graphics window
	//also calls computer opponent to make move when it is their turn
	public void actionPerformed(ActionEvent e){
		repaint();
	}
}
