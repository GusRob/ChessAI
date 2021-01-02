package GameEngine;

import javax.swing.*;

public class Main{

	public static void main(String[] args){

		JFrame chessWindow = new JFrame("ChessAI");
		chessWindow.setBounds(10, 10, 100, 100);
		chessWindow.setVisible(true);
		chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Board board = new Board();
		chessWindow.add(board);

		System.out.println("hello world");
	}
}
