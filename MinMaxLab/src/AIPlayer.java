import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.lang.Math;
import java.util.Stack;

public class AIPlayer extends Player
{
	
	private int BRANCHING_FACTOR = 14;
	public AIPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	@Override
	public Move getMove(StateTree state)
	{
		//MatrixData data = new MatrixData();
		
		Move move = minimax_wrapper(state, this.timeLimit * 1000);
		if (move == null)
		{
			System.out.println("MOVE WAS NULL ERROR!");
			return new Move(false, 0);
		}
		
		return move;
		
		/*int index1 = 0;
		
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
		return move;*/
	}
	
	private Move minimax_wrapper(StateTree state, long timeLimit)
	{
		long startTime = System.currentTimeMillis();
		
		int depth = 0;
		Move move = null;
		while (System.currentTimeMillis() - startTime < timeLimit /*REMOVE THIS*/ && depth < 2)
		{
			move = minimax(state, depth, timeLimit, startTime);
			depth++;
		}
		
		return move;
		//return new Move(false, maxIndex % state.columns);
	}
	
	//DO NOT CALL DIRECTLY
	private Move minimax(StateTree state, int maxDepth, long timeLimit, long startTime)
	{	
		ArrayList<StateTree> states = new ArrayList<StateTree>((int)Math.pow(BRANCHING_FACTOR, maxDepth));
		ArrayList<ArrayList<Move>> moves = generateMoves(maxDepth);
		
		StateTree s;
		for (ArrayList<Move> move : moves)
		{
			s = new RefereeBoard(state.rows, state.columns, state.winNumber, 1, false, false, null);
			s.boardMatrix = new int[state.rows][state.columns];
			for (int r=0; r<state.rows; r++)
			{
				for (int c=0; c<state.columns; c++)
				{
					s.boardMatrix[r][c] = state.boardMatrix[r][c];
				}
			}
			s.turn = state.turn;
			s.pop1 = state.pop1;
			s.pop2 = state.pop2;
			
			states.add(s);
			for (Move m : move)
			{
				s.makeMove(m);
			}
			
			states.add(s);
		}
		
		boolean maxNotMin = (maxDepth % 2) == 1;
		int statesRemaining = states.size();
		int lastTargetIndex = -1;
		int depth = maxDepth;
		
		while (statesRemaining > 1)
		{
			for (int stateIndex=0; stateIndex<statesRemaining; stateIndex+=BRANCHING_FACTOR)
			{
				int targetValue = 0;
				int targetIndex = 0;
				
				if (maxNotMin)
				{
					targetValue = getHeuristic(states.get(0));
					for (int choice = 1; choice < BRANCHING_FACTOR; choice++)
					{
						int column = (int)(stateIndex / Math.pow(BRANCHING_FACTOR, depth-1));
						if (choice >= BRANCHING_FACTOR /2)
						{
							if (state.boardMatrix[0][column] != this.turn)
							{
								//System.out.println("Cant pop" + this.turn + boardMatrix[0][column]);
								continue;
							}
						}
						else
						{
							if (state.boardMatrix[state.rows-1][column] == this.turn)
							{
								System.out.println("column: " + column);
								continue;
							}
						}
						s = states.get(stateIndex + choice);
						int value = getHeuristic(s);
						if (value > targetValue)
						{
							targetValue = value;
							targetIndex = stateIndex + choice;
						}
					}
					lastTargetIndex = targetIndex;
				}
				else
				{
					targetValue = getHeuristic(states.get(0));
					for (int choice = 1; choice < BRANCHING_FACTOR; choice++)
					{
						int column = (int)(stateIndex / Math.pow(BRANCHING_FACTOR, depth-1));
						if (choice >= BRANCHING_FACTOR /2)
						{
							if (state.boardMatrix[0][column] != this.turn)
							{
								//System.out.println("Cant pop" + this.turn + boardMatrix[0][column]);
								continue;
							}
						}
						else
						{
							if (state.boardMatrix[state.rows-1][column] == this.turn)
							{
								System.out.println("column: " + column);
								continue;
							}
						}
						
						s = states.get(stateIndex + choice);
						int value = getHeuristic(s);
						if (value < targetValue)
						{
							targetValue = value;
							targetIndex = stateIndex + choice;
						}
					}
				}
				
				maxNotMin = !maxNotMin;
				depth--;
				
				states.set(stateIndex / BRANCHING_FACTOR, states.get(stateIndex));
				statesRemaining /= BRANCHING_FACTOR;
			}
		}
		
		Move move = new Move(lastTargetIndex >= (BRANCHING_FACTOR/2), lastTargetIndex % (BRANCHING_FACTOR / 2));
		
		return move;
	}
	
	private ArrayList<ArrayList<Move>> generateMoves(int maxDepth)
	{
		ArrayList<ArrayList<Move>> moves = new ArrayList<ArrayList<Move>>();
		ArrayList<String> substrings = new ArrayList<String>();
		for (int i=0; i<BRANCHING_FACTOR; i++)
		{
			substrings.add("" + (char)(i + 97));
		}
		
		ArrayList<String> combinations = generateCombinations(maxDepth, substrings);
		for (String s : combinations)
		{
			int index = 0;
			moves.add(new ArrayList<Move>());
			for (int c = 0; c<s.length(); c++)
			{
				int value = (int)s.charAt(c) - 97;
				moves.get(index).add(new Move((value < 7), value % 7));
			}
			index++;
		}
		
		return moves;
	}
	
	private ArrayList<String> generateCombinations(int depth, ArrayList<String> possibleValues)
	{
	    int carry;
	    ArrayList<String> combinations = new ArrayList<String>();
	    
	    int[] indices = new int[depth];
	    do
	    {
	        for(int index : indices)
	        	combinations.add(possibleValues.get(index));
	            //System.out.print(possibleValues.get(index) + " ");

	        carry = 1;
	        for(int i = indices.length - 1; i >= 0; i--)
	        {
	            if(carry == 0)
	                break;

	            indices[i] += carry;
	            carry = 0;

	            if(indices[i] == possibleValues.size())
	            {
	                carry = 1;
	                indices[i] = 0;
	            }
	        }
	    }
	    while(carry != 1); // Call this method iteratively until a carry is left over
	    
	    return combinations;
	}
	
	private int getHeuristic(StateTree state)
	{
		Random r = new Random();
		return Math.abs((r.nextInt() * 100) % 100);
	}
	
	//Undoes the last move made so we can recurse back up the minimax tree
	/*private void undoMove(StateTree state, Move move)
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
	}*/
}