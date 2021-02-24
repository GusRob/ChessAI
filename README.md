# ChessAI

## This repository will consist of a self project in which i will attempt to:
  *  - [X] Create a functioning game engine for a standard 2D chess game
  *  - [X] Develop a basic cpu opponent to play against human and itself
  *  - [ ] Evolve the cpu opponent to use more and more advanced concepts and as such be harder to beat
  *  - [ ] Allow the user to decide the difficulty of the game by deciding search depth and time limit

## Bot List

Bot Name | Playstyle | Difficulty | Process
 :---:|:---:|:---:|:---:
**Aries** | Captures whenever possible | None | Generates move list, if any moves capture pieces clips list to those moves, and chooses randomly from list
**Athena** | Captures whenever possible and takes the highest scoring piece | Easy | Same as Aries but further clips list to that which leaves the lowest opponent score

## Milestone Log

2021/01/02
  * basic template made to create board window in dir GameEngine

2021/01/03
  * designed Piece class and added arraylists to store black pieces and white pieces

2021/01/04
  * changed design of program to use boolean bitboards for board information
  * this should speed up processing dtl

2021/01/29
  * enabled user to pick up any piece, such that when in hand the available pseudo-legal moves for that piece are calculated and displayed
  * currently no function for the king's moves to be calculated, this will need to be made alongside the function to check for possible check positions

2021/02/12
  * new branch 'cleanUp' created after kings moves and piece capture etc were implemented
  * decision was made to change structure of the program
  * new structure is as follows
    * class Main is entry point, creates window instantiates and adds Board
    * class Board is handler for program, receives UI and will draw to window
    * classes to be created  * class 'PieceHandler' will store the piece locations, check legal moves etc.
    * class 'ComputerOpp' will be the basis of the AI opponent for the user to play

2021/02/17
  * cleanUp has been merged to main
  * structure of classes modified slightly
    * class Main is entry point, creates window instantiates and adds Board
    * class Board is handler for program execution and game constants, will draw UI and board to window
    * class PieceHandler is handler for pieces and team information, will draw pieces to window
    * class MoveHandler is checking aid for moves attempted, returns if moves are possible to PieceHandler
    * class to be created
      * class 'ComputerOpp' will be the basis of the AI opponent for the user to play
  * still to be implemented is
    * piece promotion
    * en passant laws
    * castling laws
    * computer opponent

2021/02/18
  * En passant, castling and piece promotion have all been implemented to the main game
  * Additional UI components, such as a 'pause' menu and a starting menu have been created
  * Implemented the functionality to be able to flip the board, meaning once an opponent has been created, the user can play as either colour
  * initial basis of the project is done, next to implement is an opponent that can generate a list of legal moves and randomly chooses one
  * also to implement is a history/move list of moves made, should make troubleshooting easier in the event of bugs

2021/02/21
  * Created a basic bot that just returns a move it finds that is valid
  * Initial implementation involved the bot being called every witht the ActionEvent to see if it was the bots turn
  * For obvious reasons this is very inefficient, the problem faces was I struggled to find a better way to implement it
  * New branch created called 'botInterface', with the intention of making the bot get called when it was their turn, and creating a computer interface system
  * Using a new interface 'Computer' has several advantages including
    * Allows for two bots to play each other by calling them in a concise way
    * Different versions of bots can be compared by playing against each other
    * Allowing a general 'makeBotTurn' function to call any bot created in different classes to be called
  * Thus, yet to implement is
    * Allowing the user to select a bot to play against
    * Allowing the user to select a bot to play instead of themself
    * Allowing the user to select a speed for the bots to play at, else for an efficient/fast bot the game will be over in < a second, which isnt always useful

2021/02/21
  * Allowed for the user to cycle between bots vs human players for each colour
  * Implemented a 'test' mode for the make file, caled with make test which plays 100 games of the bot against itself
  * The program can be run with command line arguments as 	java GameEngine/Main.java n a b, where n, a, and b are integers corresponding to
    * n - number of games to be played
    * a - int refering to which bot to play as white
    * b - int refering to which bot to play as black
  * This will allow for future bot implementations to play against old versions to compare new techniques when developing the bots
  * Next to implement is
    * A more advanced bot that doesnt just play a random turnbut instead plays the best move it can find within a depth of 1
    * A way of evaluating the state of a board into a value to perhaps decide on which moves are best?
