# ChessAI

This repository will consist of a self project in which i will attempt to:
 1 - Create a functioning game engine for a standard 2D chess game
 2 - Develop a basic cpu opponent to play against
 3 - Evolve the cpu opponent to use more and more advanced concepts and as such be harder to beat


 2021/01/02 - basic template made to create board window in dir GameEngine

 2021/01/03 - designed Piece class and added arraylists to store black pieces and white pieces

 2021/01/04 - changed design of program to use boolean bitboards for board information
 			        - this should speed up processing dtl

 2021/01/29 - enabled user to pick up any piece, such that when in hand the available pseudo-legal moves for that piece are calculated and displayed
            - currently no function for the king's moves to be calculated, this will need to be made alongside the function to check for possible check positions

2021/02/12  - new branch 'cleanUp' created after kings moves and piece capture etc were implemented
            - decision was made to change structure of the program
            - new structure is as follows - class Main is entry point, creates window instantiates and adds Board
                                          - class Board is handler for program, receives UI and will draw to window
                                          - classes to be created - class 'PieceHandler' will store the piece locations, check legal moves etc.
                                                                  - class 'ComputerOpp' will be the basis of the AI opponent for the user to play
