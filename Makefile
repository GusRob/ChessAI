#target entry to build all files
all: GameEngine/Main.java
	javac GameEngine/Main.java GameEngine/Board.java GameEngine/PieceHandler.java GameEngine/MoveHandler.java Bots/Athena.java Bots/Ares.java Bots/Computer.java
	java GameEngine/Main.java

test: GameEngine/Main.java
	javac GameEngine/Main.java GameEngine/Board.java GameEngine/PieceHandler.java GameEngine/MoveHandler.java Bots/Athena.java Bots/Ares.java Bots/Computer.java
	java GameEngine/Main.java 100 0 0
