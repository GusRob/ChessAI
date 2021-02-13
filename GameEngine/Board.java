package GameEngine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//this class handles drawing to the window and user interaction events
public class Board extends JPanel implements MouseListener, ActionListener{
	Image darkSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/dark_sq.png");
	Image lightSq = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/light_sq.png");
	Image moveDot = Toolkit.getDefaultToolkit().getImage("GameEngine/assets/brown_dot.png");
	boolean isMouseDown = false;
	boolean isWhiteTurn = true;
	boolean isWhiteWinner = false;

	// in order of arrays - Black Pieces; rooks; knights; bishops; queens; pawns; White Pieces; same order
	//									Black				0				1				2				3				4				5
	//									White				6				7				8				9				10			11

	int mx = 0;
	int my = 0;
	javax.swing.Timer t = new javax.swing.Timer(10, this);

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
		//draws the UI components every time the game world is updated
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(isWhiteTurn ? "White's Turn" : "Black's Turn", 10, 550);
	}

	//called with repaint() - triggers paint helper functions
	public void paint(Graphics g){
		paintBoard(g);
		paintUI(g);
		//paintPieces(g);
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

	//overridden mouseEvent methods
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		if(!isMouseDown){
			isMouseDown = true;
			int square = mouseSquare(e);
		}
	}
	public void mouseReleased(MouseEvent e){

		isMouseDown = false;
		int square = mouseSquare(e);

	}

	//overridden actionEvent method
	//called every 10 milliseconds - updates the graphics window
	public void actionPerformed(ActionEvent e){
		mx = MouseInfo.getPointerInfo().getLocation().x - 430;
		my = MouseInfo.getPointerInfo().getLocation().y - 130;
		repaint();
	}

}
