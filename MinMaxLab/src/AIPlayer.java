import java.util.ArrayList;

public class AIPlayer extends Player{
	public AIPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	@Override
	public Move getMove(StateTree state)
	{
		MatrixData data = new MatrixData();
		
		//This is how you get user and opponent data.
		MatrixData myCurrentState = data.getMatrixData(state, true, 4);
		MatrixData theirCurrentState = data.getMatrixData(state, false, 4);
		
		/*
		 * Example usage, list all user sequences of length n
		 */
		int n = 3;
		for (InARow seq : data.sequences.get(n-1)) //n-1 because threes are in index 2
		{
			String direction = data.dirToString(seq.dir);
			System.out.println("Sequence of size " + n + " in a row found at row: " + seq.row + ", col: " + seq.col + " and pointing: " + direction + "!");
		}
		
		return null;
	}
}
