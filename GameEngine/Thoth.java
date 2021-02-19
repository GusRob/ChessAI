package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


//class is used to make moves as a computer controlled opponent
public class Thoth{

	private PieceHandler pieces;

  private int color;

	public Thoth(PieceHandler init_pieces, int init_color){
		pieces = init_pieces;
    color = init_color;
	}

  public int getColor(){return color;}

  public int choosePromotion(){
    return 0;
  }

  public int[] chooseFirstMove(){
    int[] result = new int[2];
    result[0] = -1;
    result[1] = -1;
    for(int i = (color/6)*63; ((color==0)?(i<64):(i>-1));){
      int pieceColor = pieces.getPieceColor(i);
      int pieceId = pieces.getPieceId(i);
      if(pieceId != -1 && pieceColor == color){
        for(int j = (color/6)*63; ((color==0)?(j<64):(j>-1));){
          if(pieces.moves.validateTurn(j, i)){
            result[0] = i;
            result[1] = j;
          }
          j = (color==0)?(j+1):(j-1);
        }
      }
      i = (color==0)?(i+1):(i-1);
    }
    return result;
  }
}
