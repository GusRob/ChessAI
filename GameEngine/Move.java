package GameEngine;

import java.awt.*;
import java.util.*;
import javax.swing.*;


public class Move{

  //indicates the squares the moving piece ttravels between
  private int squareFrom;
  private int squareTo;
  //indicates whether a piece is being captured in this move
  private boolean capture;
  //indicates what piece a pawn is being promoted to, -1 otherwise
  private int promotion = -1;
  //in the moves O-O and O-O-O, the move will be a king move, with the castling flag set to true
  private boolean castling;

	public Move(int init_from, int init_to, boolean init_capture, int init_prom, boolean init_castle){
    squareFrom = init_from;
    squareTo = init_to;
    capture = init_capture;
    promotion = init_prom;
    castling = init_castle;
	}

}
