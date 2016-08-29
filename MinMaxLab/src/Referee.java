/**
 * This is the referee, it will manage the game and decide
 * if there is a winner or a disqualification.
 * Feel free to use any of the code provided however
 * you see fit.
 * 
 * @author Ethan Prihar
 *
 */
public class Referee
{
	// CHANGE THESE VALUES
	static int boardRows = 6; // rows the board has
	static int boardColumns = 7; // columns the board has
	static int winNumber = 4; // how many pieces you need in a row to win
	static int timeLimit = 10; // the time, in seconds, allowed for each player to provide a move
	static Player player1 = new SimplePlayer("Beep", 1, timeLimit); // you should create your own player class and use it here
	static Player player2 = new SimplePlayer("Boop", 2, timeLimit);
	// STOP CHANGING THINGS
	static StateTree board;
	
	public static void main(String[] args)
	{
		// Make the board and initialize variables
		board = new RefereeBoard(boardRows, boardColumns, winNumber, 1, false, false, null);
		Move move = null;
		int winner = 0;
		// This while loop runs until there is a winner
		while(winner == 0)
		{
			if(board.turn == 1) // Player 1's turn
			{
				System.out.println(player1.name + "'s turn:");
				long start = System.currentTimeMillis();
				move = player1.getMove(board);
				long stop = System.currentTimeMillis();
				double timePassed = (double)(stop - start) / 1000.0;
				System.out.println(player1.name + " took " + timePassed + " seconds to move.");
				if(timePassed > timeLimit)
				{
					System.out.println(player1.name + " took too long.");
					System.out.println(player2.name +" wins.");
					return;
				}
				if(!board.validMove(move))
				{
					System.out.println(player1.name + " made an invalid move.");
					System.out.println(player2.name +" wins.");
					return;
					
				}
				String action;
				if(move.pop)
					action = " popped a piece from column ";
				else
					action = " placed a piece in column ";
				System.out.println(player1.name + action + move.column + ".");
			}
			else if(board.turn == 2) // Player 2's turn
			{
				System.out.println(player2.name + "'s turn:");
				long start = System.currentTimeMillis();
				move = player2.getMove(board);
				long stop = System.currentTimeMillis();
				double timePassed = (double)(stop - start) / 1000.0;
				System.out.println(player2.name + " took " + timePassed + " seconds to move.");
				if(timePassed > timeLimit)
				{
					System.out.println(player2.name + " took too long.");
					System.out.println(player1.name +" wins.");
					return;
				}
				if(!board.validMove(move))
				{
					System.out.println(player2.name + " made an invalid move.");
					System.out.println(player1.name +" wins.");
					return;
				}
				String action;
				if(move.pop)
					action = " popped a piece from column ";
				else
					action = " placed a piece in column ";
				System.out.println(player2.name + action + move.column + ".");
			}
			board.makeMove(move); // Makes the move after checking if it was valid
			board.display(); // Prints the board
			winner = checkForWinner(board); // Checks to see if anybody has won
		}
		switch(winner) // Displays appropriate win messages
		{
		case 1:
			System.out.println(player1.name +" wins.");
			return;
		case 2:
			System.out.println(player2.name +" wins.");
			return;
		case 3:
			System.out.println("Tie game.");
			return;
		}
	}
	
	public static int checkForWinner(StateTree board)
	{
		int points = checkConnect(board); // see how many each player has in a row
		if(points > 0) // if player 1 has more in a row they win
			return 1;
		else if(points < 0) // if player 2 has more in a row they win
			return 2;
		else if(checkFull(board)) // if the board is full than it's a tie
			return 3;
		else // otherwise keep playing
			return 0;

	}
	
	// This counts how many n-in-a-rows each player has
	public static int checkConnect(StateTree board)
	{
		int winner = 0;
		int[] count = new int[4];
		int winTotal = 0;
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.boardMatrix[i][j] == 0)
				{
					winner = 0;
					for(int x=0; x<4; x++)
					{
						count[x] = 0;
					}
				}
				else
				{
					winner = board.boardMatrix[i][j];
					for(int x=0; x<board.winNumber; x++)
					{
						if((j+x < board.columns) && (board.boardMatrix[i][j+x] == winner))
							count[0]++;
						else
							count[0] = 0;
						if((i+x < board.rows) && (board.boardMatrix[i+x][j] == winner))
							count[1]++;
						else
							count[1] = 0;
						if((i+x < board.rows) && (j+x < board.columns) && (board.boardMatrix[i+x][j+x] == winner))
							count[2]++;
						else
							count[2] = 0;
						if((i-x >= 0) && (j+x < board.columns) && (board.boardMatrix[i-x][j+x] == winner))
							count[3]++;
						else
							count[3] = 0;
					}
				}
				for(int x=0; x<4; x++)
				{
					if(count[x] == board.winNumber)
					{
						if(winner == 1)
							winTotal++;
						else if(winner == 2)
							winTotal--;
					}
					count[x] = 0;
				}
				winner = 0;
			}
		}
		return winTotal;
	}
	
	public static boolean checkFull(StateTree board)
	{
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.boardMatrix[i][j] == 0)
					return false;
			}
		}
		return true;
	}
}
