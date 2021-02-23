package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;



//class is used to make moves as a computer controlled opponent
public class Thoth implements Computer{

	private PieceHandler pieces;

  private int color;

	private int[][] moves = new int[100][2];

	public Thoth(PieceHandler init_pieces, int init_color){
		pieces = init_pieces;
    color = init_color;
	}

  public int getColor(){return color;}

  public int choosePromotion(){
    return 0;
  }

  public int[] chooseMove(){
		int n = 0;
    int[] result = new int[2];
    for(int i = 0; i < 64; i++){
      int pieceColor = pieces.getPieceColor(i);
      int pieceId = pieces.getPieceId(i);
      if(pieceId != -1 && pieceColor == color){
        for(int j = 0; j<64; j++){
          if(pieces.moves.validateTurn(j, i)){
						if(n < 100){
            	moves[n][0] = i;
            	moves[n][1] = j;
							n++;
						}
          }
        }
      }
    }
		int a = 0;
		for(int i = 0; i < n; i++){
			int col = pieces.getPieceColor(moves[i][1]);
			if(col == ((color==0)?6:0)){
				moves[a] = moves[i];
				a++;
			}
		}
		if(a != 0){
			n = a;
		}
		Random r = new Random();
		boolean problem = false;
		int randomInt = 0;
		if(n == 0){
			pieces.queryCheckmate();
			problem = true;
		} else {
			randomInt = r.nextInt(n);
		}
		int[] defaultVal = {-1, -1};
    return problem?defaultVal:moves[randomInt];
  }
}
