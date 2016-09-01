
public class AIPlayer extends Player{
	public AIPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	@Override
	public Move getMove(StateTree state) {
		// TODO Auto-generated method stub
		return null;
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
		
		
		//check vertically with free space on top
		for (int i = 0; i < state.columns; i++){
			int continuous1 = 0;
			int continuous2 = 0;
			for (int j = 0; j < state.rows; j++){
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
		return utility;
	}
}
