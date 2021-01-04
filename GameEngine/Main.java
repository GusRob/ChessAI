package GameEngine;

import javax.swing.*;

public class Main{

	public static void main(String[] args){
		//Create Window to open chess game, instantiate Board and add to window
		JFrame chessWindow = new JFrame("ChessAI");
		chessWindow.setBounds(400, 100, 512, 534);
		chessWindow.setExtendedState(JFrame.NORMAL);
		chessWindow.setResizable(false);
		chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Board board = new Board();
		chessWindow.add(board);
		chessWindow.setVisible(true);
	}
}
