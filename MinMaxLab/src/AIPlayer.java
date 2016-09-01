import java.util.ArrayList;
import java.util.Stack;

public class AIPlayer extends Player
{
	public AIPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	@Override
	public Move getMove(StateTree state)
	{
		MatrixData data = new MatrixData();
		
		Move move = minimax_wrapper(state, 2000); //tree then time limit
		
		return move;
	}
	
	private Move minimax_wrapper(StateTree state, long timeLimit)
	{
		long startTime = System.currentTimeMillis();
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Integer> heuristics = minimax(state, 0, 0, timeLimit, startTime); //Change this time limit! Only searches to depth 1 right now
		
		int max=0, maxIndex=0;
		for (int index = 0; index < heuristics.size(); index++)
		{
			if (heuristics.get(index) > max)
			{
				max = heuristics.get(index);
				maxIndex = index;
			}
		}
		
		return new Move(false, maxIndex % 7);
	}
	
	//DO NOT CALL DIRECTLY
	private ArrayList<Integer> minimax(StateTree state, int depth, int maxDepth, long timeLimit, long startTime)
	{
		if (depth <= maxDepth || (System.currentTimeMillis() - startTime) > timeLimit)
		{
			int decay = 1;///(depth + 1)
			ArrayList<Integer> values = new ArrayList<Integer>(state.columns);
			
			for (int choice = 0; choice < state.columns; choice++, decay -= 0)
			{
				Move move = new Move(false, choice);
				state.makeMove(move);
				values.add(getHeuristic(state) * decay);
				undoMove(state, move);
			}
			
			return values;
		}
		
		ArrayList<Integer> values = new ArrayList<Integer>();
		ArrayList<Integer> branchValues = new ArrayList<Integer>();
		
		for (int choice = 0; choice < state.columns; choice++)
		{
			Move move = new Move(false, choice);
			state.makeMove(move);
			branchValues = minimax(state, depth+1, maxDepth, timeLimit, startTime);
			undoMove(state, move);
			
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
		MatrixData data = new MatrixData();
		data.getMatrixData(state, true, 0);
		
		return data.sequences.get(0).size() + data.sequences.get(1).size() * 2 + data.sequences.get(2).size() * 3;
	}
	
	private void undoMove(StateTree state, Move move)
	{
		if(move.pop)
		{
			if(turn == 1)
				state.pop1 = false;
			if(turn == 2)
				state.pop2 = false;
			for(int i=0; i<state.rows-1; i++)
			{
				state.boardMatrix[i][move.column] = state.boardMatrix[i-1][move.column];
			}
			state.boardMatrix[state.rows-1][move.column] = turn;
			turn = Math.abs(turn-3);
			return;
		}
		else
		{
			for (int i=0; i<state.rows; i++)
			{
				if (state.boardMatrix[i][move.column] != 0)
				{
					state.boardMatrix[i][move.column] = 0;
					turn = Math.abs(turn-3);
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
}
