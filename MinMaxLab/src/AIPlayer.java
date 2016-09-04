import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.lang.Math;
//import java.util.Stack;

public class AIPlayer extends Player
{
	public AIPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	@Override
	public Move getMove(StateTree state)
	{
		//MatrixData data = new MatrixData();
		
		ArrayList<Integer> heuristics = minimax_wrapper(state, this.timeLimit * 1000);
		int index1 = 0;
		for (Integer i : heuristics)
		{
			System.out.println("Index " + index1 + ": " + i);
			index1++;
		}
		
		int max=0, maxIndex=0;
		for (int index = 0; index < heuristics.size(); index++)
		{
			if (heuristics.get(index) > max)
			{
				max = heuristics.get(index);
				maxIndex = index;
			}
		}
		Move move = new Move(false, maxIndex);
		return move;
	}
	
	private ArrayList<Integer> minimax_wrapper(StateTree state, long timeLimit)
	{
		long startTime = System.currentTimeMillis();
		
		int depth = 0;
		ArrayList<Integer> heuristics = minimax(state, 0, depth, timeLimit, startTime); //Only searches to depth 1 right now
		heuristics = minimax(state, 0, 3, timeLimit, startTime); //Only searches to depth 1 right now
		/*while (System.currentTimeMillis() - startTime < timeLimit && false)
		{
			ArrayList<Integer> heuristics = minimax(state, 0, depth, timeLimit, startTime); //Only searches to depth 1 right now
			depth++;
		}*/
		
		return heuristics;
		//return new Move(false, maxIndex % state.columns);
	}
	
	//DO NOT CALL DIRECTLY
	private ArrayList<Integer> minimax(StateTree state, int depth, int maxDepth, long timeLimit, long startTime)
	{
		boolean userMove = ((depth+state.turn) % 2) == 1;
		System.out.println("Usermove: " + userMove);
		//Recursive base case. Telling us to stop DFS if we are at the iterative depth limit or if time is up
		if (depth <= maxDepth || (System.currentTimeMillis() - startTime) > timeLimit)
		{
			ArrayList<Integer> values = new ArrayList<Integer>(state.columns);
			
			//Return all child node values of this state tree
			for (int choice = 0; choice < state.columns; choice++)
			{
				Move move = new Move(false, choice);
				state.makeMove(move);
				if (userMove)
				{
					values.add(getHeuristic(state));
				}
				else
				{
					values.add(getHeuristic(state));
				}
				undoMove(state, move);
			}
			
			return values;
		}
		
		//values of child nodes
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		//values of child's child nodes (the max(branchValues) will be put into values each loop)
		ArrayList<Integer> branchValues = new ArrayList<Integer>();
		
		//In this loop we iterate over each of the columns and check the minimax value for that tree
		for (int choice = 0; choice < state.columns; choice++)
		{
			//Prepare to make a move and put it into the column $choice
			Move move = new Move(false, choice);
			state.makeMove(move);
			
			//make the move, check the minimax, then undo the move
			//If this level on the tree is our move just check the regular tree
			//If it is their move then we will use our multiple heuristic pruning algorithm
			if (userMove)
			{
				branchValues = minimax(state, depth+1, maxDepth, timeLimit, startTime); //Get values of this new state trees children
			}
			else
			{
				branchValues = minimax_wrapper(state, timeLimit / 1000); //change 3rd param soon. Need variable for defensedepth
				
				pruneBadGuesses(branchValues, .5);
				int index1 = 0;
				for (Integer i : branchValues)
				{
					System.out.println("Prune Index " + index1 + ": " + i);
					index1++;
				}
				
				for (int i=0; i<branchValues.size(); i++)
				{
					if (branchValues.get(i) > Integer.MIN_VALUE)
					{
						Move move2 = new Move(false, i);
						state.makeMove(move2);
						branchValues = minimax(state, depth+1, maxDepth, timeLimit, startTime); //Get values of this new state trees children
						undoMove(state, move2);
					}
				}
			}
			undoMove(state, move);
			
			//Get the max value of all these branch nodes
			int max = 0;
			for (int b : branchValues)
			{
				if (b > max)
				{
					max = b;
				}
			}
			
			values.add(max);
		}
		
		return values;
	}
	
	private int getHeuristic(StateTree state)
	{
		/*MatrixData data = new MatrixData();
		data.getMatrixData(state, this.turn, 0);
		
		int sum = 0;
		int index = 1;
		for (ArrayList<InARow> d : data.sequences)
		{
			System.out.println("index " + index + " " + d.size());
			sum += d.size() * index;
		}
		
		return sum;*/
		Random r = new Random();
		return Math.abs(r.nextInt() % 100);
	}
	
	//Undoes the last move made so we can recurse back up the minimax tree 
	private void undoMove(StateTree state, Move move)
	{
		if(move.pop)
		{
			if(state.turn == 1)
				state.pop1 = false;
			if(state.turn == 2)
				state.pop2 = false;
			for (int i = state.rows-2; i>=0; i--) //(int i=0; i<state.rows-1; i++)
			{
				state.boardMatrix[i][move.column] = state.boardMatrix[i+1][move.column];
			}
			state.boardMatrix[state.rows-1][move.column] = turn;
			state.turn = Math.abs(state.turn-3);
			return;
		}
		else
		{
			for (int i=state.rows -1; i >= 0; i--)
			{
				if (state.boardMatrix[i][move.column] != 0)
				{
					state.boardMatrix[i][move.column] = 0;
					state.turn = Math.abs(state.turn-3);
					return;
				}
			}
		}
	}
	
	public int heuristic(StateTree state){
		int utility = 0;
		int winNumber = state.winNumber;
		
		//assign weight
		int[] verticalWeight = new int[winNumber];
		for (int i = 0; i < verticalWeight.length; i++){
			if (i == (verticalWeight.length - 1)){
				verticalWeight[i] = Integer.MAX_VALUE;
			}
			else{
				verticalWeight[i] = 2 * (i + 1);
			}
		}
		
		int[] horizontalOneFree = new int[winNumber];
		for (int i = 0; i < horizontalOneFree.length; i++){
			if (i == (horizontalOneFree.length - 1)){
				horizontalOneFree[i] = Integer.MAX_VALUE;
			}
			else{
				horizontalOneFree[i] = 2 * (i + 1);
			}
		}
		
		int[] horizontalTwoFree = new int[winNumber];
		for (int i = 0; i < horizontalTwoFree.length; i++){
			if (i == (horizontalTwoFree.length - 1)){
				horizontalTwoFree[i] = Integer.MAX_VALUE;
			}
			else{
				horizontalTwoFree[i] = 3 * (i + 1);
			}
		}
		
		
		//check vertically with free space on top
		for (int j = 0; j < state.columns; j++){
			int continuous1 = 0;
			int continuous2 = 0;
			for (int i = 0; i < state.rows; i++){
				if (state.boardMatrix[i][j] == 0){
					if (continuous1 > 0){
						utility += verticalWeight[continuous1 - 1];
					}
					if (continuous2 > 0){
						utility -= verticalWeight[continuous2 - 1];
					}
					break;
				}
				if (state.boardMatrix[i][j] == 1){
					continuous2 = 0;
					continuous1 += 1;
				}
				if (state.boardMatrix[i][j] == 2){
					continuous1 = 0;
					continuous2 += 1;
				}
			}
		}
		
		//check horizontally
		for (int i = 0; i < state.rows; i++){
			int continuous1 = 0;
			int continuous2 = 0;
			for (int j = 0; j < state.columns; j++){
				if (state.boardMatrix[i][j] == 1){
					continuous2 = 0;
					continuous1 += 1;
					while (j < (state.columns - 1) && state.boardMatrix[i][j+1] == 1){
						continuous1 += 1;
						j += 1;
					}
					if (j < state.columns - 1 && state.boardMatrix[i][j+1] == 0 && state.boardMatrix[i][j-continuous1] == 0){
						utility += horizontalTwoFree[continuous1 - 1];
					}
					else if (j < state.columns - 1 && state.boardMatrix[i][j+1] == 0){
						utility += horizontalOneFree[continuous1 -1];
					}
					else if (state.boardMatrix[i][j-continuous1] == 0){
						utility += horizontalOneFree[continuous1 -1];
					}
				}
				else if (state.boardMatrix[i][j] == 2){
					continuous1 = 0;
					continuous2 += 1;
					while (j < (state.columns - 1) && state.boardMatrix[i][j+1] == 1){
						continuous2 += 1;
						j += 1;
					}
					if (j < state.columns - 1 && state.boardMatrix[i][j+1] == 0 && state.boardMatrix[i][j-continuous2] == 0){
						utility -= horizontalTwoFree[continuous2 - 1];
					}
					else if (j < state.columns - 1 && state.boardMatrix[i][j+1] == 0){
						utility -= horizontalOneFree[continuous2 -1];
					}
					else if (state.boardMatrix[i][j-continuous2] == 0){
						utility -= horizontalOneFree[continuous2 -1];
					}
				}
			}
		}
		
		
		//check diagonally
		for(int i = winNumber - 1; i < state.rows; i++){
			ArrayList<Integer> list = new ArrayList<>();
			int rowsBelow = 0;
			int columnIndex = 0;
			while(i - rowsBelow >= 0){
				list.add(state.boardMatrix[i-rowsBelow][columnIndex]);
				columnIndex++;
				rowsBelow++;
			}
			//TODO
		}
		
		return utility;
	}
	
	private void pruneBadGuesses(ArrayList<Integer> branches, double aggressiveness)
	{
		aggressiveness = Math.max(0, Math.min(1,  aggressiveness));
		ArrayList<Integer> sorted = new ArrayList<Integer>(branches);
		Collections.sort(sorted);
		
		int size = sorted.size();
		int cutoffIndex = (int)Math.round(aggressiveness * (size - 1));
		int cutoff = branches.get(cutoffIndex);
		int count = 0;
		for (int i=0; i<branches.size(); i++)
		{
			if (branches.get(i) < cutoff)
			{
				count++;
				branches.set(i, Integer.MIN_VALUE);
			}
		}
		
		System.out.println("pruned " + count + " guesses");
	}
}