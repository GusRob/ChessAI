#target entry to build all files
all: GameEngine/Main.java
	javac GameEngine/Main.java GameEngine/Board.java GameEngine/Piece.java
	java GameEngine/Main.java
