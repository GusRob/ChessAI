package GameEngine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import Bots.*;

enum Player {
  USER,
  ATHENA {
    public Computer create(PieceHandler p, int c){return new Athena(p, c);}
  },
  ARTEMIS {
    public Computer create(PieceHandler p, int c){return new Artemis(p, c);}
  },
  ARES {
    public Player next(){return USER;}
    public Computer create(PieceHandler p, int c){return new Ares(p, c);}
  };

  public Computer create(PieceHandler p, int c){
    return null;
  }

  public Player next() {
    return values()[ordinal() + 1];
  }
}

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
	private Player playerWhite = Player.USER;
	private Player playerBlack = Player.ATHENA;
  private boolean isPlayerMove = false;
	private boolean isWhiteAtTop = false;
	private boolean botGame = false;
	private int count = 0;
  private int[] wins = {0, 0, 0};

  ArrayList<int[]> movesMade = new ArrayList<int[]>();
  private int turnNo = 0;

  private int gamesLeft = 0;

	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces;
	//									Black				0				1				2				3				4				5
	//									White				6				7				8				9				10			11
	javax.swing.Timer t = new javax.swing.Timer(1, this);

	PieceHandler pieces = new PieceHandler(this);
	Computer botBlack = new Athena(pieces, 0);
	Computer botWhite = new Athena(pieces, 0);

	//constructor starts timer t adds mouselistener and calls paint method for first time
	public Board(int noOfGames, int initBlack, int initWhite){
		//constructor initiates game pieces
		t.start();

    gamesLeft = noOfGames;

    switch(initBlack){
      case 0: playerBlack = Player.ATHENA; break;
      case 1: playerBlack = Player.ARTEMIS; break;
      case 2: playerBlack = Player.ARES; break;
    }
    switch(initWhite){
      case 0: playerWhite = Player.ATHENA; break;
      case 1: playerWhite = Player.ARTEMIS; break;
      case 2: playerBlack = Player.ARES; break;
    }

		addMouseListener(this);
		repaint();
    if(gamesLeft > 0){
      startNewGame();
    }
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
			if(pieces.getIsInCheckMate()[0] || pieces.getIsInCheckMate()[1]){
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
			if(menuHover != -1){
				g2.fillRoundRect(168, 118 +(276/4)*menuHover, 176, (276/4), 10, 10);
			}
			g.setColor(Color.BLACK);
			Rectangle bounds = new Rectangle(168, 118, 176, 276/4);
			Rectangle boundsHi = new Rectangle(168, 118, 176, (int)276/8);
			Rectangle boundsLo = new Rectangle(168, 118 + (int)276/8, 176, (int)276/8);
			drawCenteredString(g, "Start", bounds, new Font("Arial", Font.BOLD, 30));
			bounds.y += 276/4;
			boundsHi.y += 276/4;
			boundsLo.y += 276/4;
			drawCenteredString(g, "Black:", boundsHi, new Font("Arial", Font.PLAIN, 30));
			drawCenteredString(g, playerBlack.name() ,boundsLo, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			boundsHi.y += 276/4;
			boundsLo.y += 276/4;
			drawCenteredString(g, "White:", boundsHi, new Font("Arial", Font.PLAIN, 30));
			drawCenteredString(g, playerWhite.name() ,boundsLo, new Font("Arial", Font.PLAIN, 30));
			bounds.y += 276/4;
			boundsHi.y += 276/4;
			boundsLo.y += 276/4;
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
		pieces.paint(g, !isWhiteAtTop);
		paintUI(g);
		if(botGame && !isMenuOpen && !isGameOver && !isNewGame){
			botGame();
		}
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
		if(isWhiteAtTop && result != -1){
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
    String checkStr = "";
    if(isWhiteWinnerVal){
      checkStr = "White Wins!";
    } else if(!isWhiteWinnerVal){
      checkStr = "Black Wins!";
    }
    if(isTie){
      checkStr = "StaleMate";
    }
    if(checkStr == ""){while(true){}}
    System.out.println(checkStr + " : " + (gamesLeft-1) + " games left");
    if(checkStr.equals("StaleMate")){
      wins[2]++;
    } else if(checkStr.equals("White Wins!")){
      wins[1]++;
    } else if(checkStr.equals("Black Wins!")){
      wins[0]++;
    }
    if(gamesLeft > 0){
      gamesLeft--;
      isNewGame = true;
      pieces.resetAll();
      isWhiteTurn = true;
      isWhiteWinner = false;
      isTie = false;
      isGameOver = false;
      isMenuOpen = false;
      isPlayerMove = false;
      movesMade = new ArrayList<int[]>();
      turnNo = 0;
      startNewGame();
    } else {
      System.out.println("White won: " + wins[1] + " games");
      System.out.println("Black won: " + wins[0] + " games");
      System.out.println("Stalemates: " + wins[2] + " games");
    }
	}

	//calls for a turn from the ai
	public void makeBotTurn(Computer bot){
		int[] move = bot.chooseMove();
		if(move[0] != -1){
			int pieceId = pieces.getPieceId(move[0]);
			if(pieceId != -1 && (pieceId>5 == isWhiteTurn)){
				pieces.setHeld(pieceId, move[0]);
				if(pieces.placeHeld(move[1])){
          movesMade.add(move);
					pieces.queryCheckmate();
          turnNo++;
				}
			}
		}
		if(isSelecting){
			int compHover = bot.choosePromotion();
			int inputTurn = isWhiteTurn?0:1;
			pieces.setProSelect(compHover, inputTurn);
			int promotionFile = pieces.promotionAvail(inputTurn);
			pieces.promote(inputTurn, promotionFile);
      pieces.queryCheckmate();
			selectionMade();
		}
	}

	//called to change isWhiteTurn and call the AI to move if it is their turn
	public void nextTurn(){
		isWhiteTurn = !isWhiteTurn;
		if(isWhiteTurn && playerWhite == Player.USER){
			isPlayerMove = true;
		} else if(!isWhiteTurn && playerBlack== Player.USER){
			isPlayerMove = true;
		} else { //call ai
			makeBotTurn(isWhiteTurn?botWhite:botBlack);
			nextTurn();
		}
		rotateBoard();
	}

	//called when two bots are playing, to take each turn
	public void botGame(){
		botGame = true;
		count++;
		if(count > 1){
			count = 0;
			isWhiteTurn = !isWhiteTurn;
			makeBotTurn(isWhiteTurn?botWhite:botBlack);
		}
	}

  //method to print list of moves made to console for error checking
  public void printGame(){
    for(int i = 0; i < turnNo-1; i++){
      System.out.println(movesMade.get(i)[0] + "->" + movesMade.get(i)[1]);
    }
  }

  //board is flipped if user is both colors
	public void rotateBoard(){
		boolean isWhiteUser = (playerWhite == Player.USER);
		boolean isBlackUser = (playerBlack == Player.USER);
		if(isBlackUser == isWhiteUser){
			isWhiteAtTop = !isWhiteTurn;
		}
		if(isNewGame){
			isWhiteAtTop = false;
		}
	}

	//getter functions for Board values
	public boolean getTurn(){return isWhiteTurn;}
	public boolean getSelecting(){return isSelecting;}

  public void startNewGame(){
    if(playerWhite != Player.USER){
      botWhite = playerWhite.create(pieces, 6);
    }
    if(playerBlack != Player.USER){
      botBlack = playerBlack.create(pieces, 0);
    }
    isNewGame = false;
    isWhiteTurn = false;
    if((playerWhite == Player.USER)){
      isWhiteAtTop = false;
    } else if((playerBlack == Player.USER)){
      isWhiteAtTop = true;
    }
    if(!(playerWhite == Player.USER) && !(playerBlack == Player.USER)){
      botGame();
    } else {
      botGame = false;
      nextTurn();
    }
  }

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		int mouseY = e.getY();
		if(!isSelecting && !isGameOver && !isMenuOpen &&!isNewGame){
			if(!isMouseDown){
				//mousePressed(e);
			} else {
				//mouseReleased(e);
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
				isPlayerMove = false;
        movesMade = new ArrayList<int[]>();
        turnNo = 0;

			} else if(menuHover == 2){ //fun button
				//do nothing
			} else if(menuHover == 3){ //exit game
				System.exit(0);
			}
		} else if(isNewGame){
			if(menuHover == 0){ //Start
        startNewGame();
			} else if(menuHover == 1){ //change black player
				playerBlack = playerBlack.next();
			} else if(menuHover == 2){ //Change white player
				playerWhite = playerWhite.next();
			} else if(menuHover == 3){ //exit game
				System.exit(0);
			}
		}
		if(e.getX() > 437 && mouseY > 512){
			isMenuOpen = !isMenuOpen;
		}
	}
  public void mousePressed(MouseEvent e){
		if(!isSelecting && !isGameOver && !isMenuOpen && isPlayerMove){
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
          int[] move = {pieces.getHeldSquare(), square};
					if(pieces.placeHeld(square)){
						pieces.queryCheckmate();
            movesMade.add(move);
            turnNo++;
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
