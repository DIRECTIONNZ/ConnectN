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
}
