package Bots;

//interface for the structure of the chess bots
public interface Computer{

  public int getColor();

  public int choosePromotion();

  public int[] chooseMove();
}
