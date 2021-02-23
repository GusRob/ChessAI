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
		int[] input = getCmdLine(args);
		Board board = new Board(input[0], input[1], input[2]);
		chessWindow.add(board);
		chessWindow.setVisible(true);
	}

	public static int[] getCmdLine(String[] args) {
		boolean erro = false;
		int[] result = new int[3];
		if (args.length == 3) {
	    try {
					result[0] = Integer.parseInt(args[0]);
					result[1] = Integer.parseInt(args[1]);
					result[2] = Integer.parseInt(args[2]);
	    } catch (NumberFormatException e) {
				System.err.println("Argument '" + args[0] + "' must be an integer.");
	    }
		} else if(args.length == 0){
			result[0] = 0;
			result[1] = -1;
			result[2] = -1;
		} else {
			System.err.println("Argument format: noOfGames WhiteBot BlackBot");
		}
		if( erro ){
			System.exit(1);
		}
		return result;
	}
}
