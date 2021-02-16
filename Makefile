#target entry to build all files
all: GameEngine/Main.java
	javac GameEngine/Main.java GameEngine/Board.java GameEngine/PieceHandler.java GameEngine/MoveHandler.java
	java GameEngine/Main.java
