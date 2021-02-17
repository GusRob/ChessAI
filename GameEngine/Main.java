package GameEngine;

import javax.swing.*;

//Create Window to open chess game, instantiate Board and add to window
public class Main{

	public static void main(String[] args){
		JFrame chessWindow = new JFrame("ChessAI");
		chessWindow.setBounds(400, 100, 512, 562);
		chessWindow.setExtendedState(JFrame.NORMAL);
		chessWindow.setResizable(false);
		chessWindow.setUndecorated(true);
		chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Board board = new Board();
		chessWindow.add(board);
		chessWindow.setVisible(true);
	}
}
