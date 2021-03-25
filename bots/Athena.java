package Bots;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import GameEngine.*;



//class is used to make moves as a computer controlled opponent
public class Athena implements Computer{

	private PieceHandler pieces;

  private int color;

	private ArrayList<int[]> moves = new ArrayList<int[]>();

	public Athena(PieceHandler init_pieces, int init_color){
		pieces = init_pieces;
    color = init_color;
	}

  public int getColor(){return color;}

  public int choosePromotion(){
    return 0;
  }

  public int[] chooseMove(){
		return [0,0]
  }

}
