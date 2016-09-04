import java.util.ArrayList;

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
		
		int[] diagonalBot = new int[winNumber];
		for (int i = 0; i < diagonalBot.length; i++){
			if (i == (diagonalBot.length - 1)){
				diagonalBot[i] = Integer.MAX_VALUE;
			}
			else{
				diagonalBot[i] = 2 * (i + 1);
			}
		}
		
		int[] diagonalTop = new int[winNumber];
		for (int i = 0; i < diagonalTop.length; i++){
			if (i == (diagonalTop.length - 1)){
				diagonalTop[i] = Integer.MAX_VALUE;
			}
			else{
				diagonalTop[i] = 2 * (i + 1);
			}
		}
		
		int[] diagonal_Underneath = new int[winNumber];
		for (int i = 0; i < diagonal_Underneath.length; i++){
			if (i == (diagonal_Underneath.length - 1)){
				diagonal_Underneath[i] = Integer.MAX_VALUE;
			}
			else{
				diagonal_Underneath[i] = 3 * (i + 1);
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
		
		
		//check diagonal with negative slope
		for(int i = winNumber - 1; i < state.rows; i++){
			int continuous1 = 0;
			int continuous2 = 0;
			int underneath1 = 0;
			int underneath2 = 0;
			ArrayList<Integer> list = new ArrayList<>();
			int rowsBelow = 0;
			int columnIndex = 0;
			while(i - rowsBelow >= 0){
				list.add(state.boardMatrix[i-rowsBelow][columnIndex]);
				columnIndex++;
				rowsBelow++;
			}
			
			//check bottom
			if (list.get(list.size() - 1) == 0){
				for (int index = (list.size() - 2); index >= 0; index--){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[i - index - 1][index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[i - index - 1][index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalBot[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalBot[continuous2 - 1];
					}
				}
			}
			
			//check top
			if (list.get(0) == 0){
				continuous1 = 0;
				continuous2 = 0;
				underneath1 = 0;
				underneath2 = 0;
				for (int index = 1; index < list.size(); index++){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (i - index - 1 >= 0 && state.boardMatrix[i - index - 1][index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (i - index - 1 >= 0 && state.boardMatrix[i - index - 1][index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalTop[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalTop[continuous2 - 1];
					}
				}
			}
			
		}
		for(int i = 1; i <= state.columns - winNumber; i++){
			int continuous1 = 0;
			int continuous2 = 0;
			int underneath1 = 0;
			int underneath2 = 0;
			ArrayList<Integer> list = new ArrayList<>();
			int columnProgress = 0;
			while(i + columnProgress < state.columns){
				list.add(state.boardMatrix[state.rows - columnProgress - 1][i + columnProgress]);
				columnProgress++;
			}
			//check bottom
			if (list.get(list.size() - 1) == 0){
				for (int index = (list.size() - 2); index >= 0; index--){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[state.rows - index - 2][i + index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[state.rows - index - 2][i + index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalBot[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalBot[continuous2 - 1];
					}
				}
			}
			
			//check top
			if (list.get(0) == 0){
				continuous1 = 0;
				continuous2 = 0;
				underneath1 = 0;
				underneath2 = 0;
				for (int index = 1; index < list.size(); index++){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[state.rows - index - 2][i + index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[state.rows - index - 2][i + index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalTop[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalTop[continuous2 - 1];
					}
				}
			}
			
		}
		
		//check positive slope
		for(int i = winNumber - 1; i < state.rows; i++){
			int continuous1 = 0;
			int continuous2 = 0;
			int underneath1 = 0;
			int underneath2 = 0;
			ArrayList<Integer> list = new ArrayList<>();
			int rowsBelow = 0;
			int columnIndex = state.columns - 1;
			while(i - rowsBelow >= 0){
				list.add(state.boardMatrix[i-rowsBelow][columnIndex]);
				columnIndex--;
				rowsBelow++;
			}
			
			//check bottom
			if (list.get(list.size() - 1) == 0){
				for (int index = (list.size() - 2); index >= 0; index--){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[i - index - 1][state.columns - 1 - index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[i - index - 1][state.columns - 1 - index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalBot[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalBot[continuous2 - 1];
					}
				}
			}
			
			//check top
			if (list.get(0) == 0){
				continuous1 = 0;
				continuous2 = 0;
				underneath1 = 0;
				underneath2 = 0;
				for (int index = 1; index < list.size(); index++){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (i - index - 1 >= 0 && state.boardMatrix[i - index - 1][state.columns - 1 - index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (i - index - 1 >= 0 && state.boardMatrix[i - index - 1][state.columns - 1 - index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalTop[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalTop[continuous2 - 1];
					}
				}
			}
			
		}
		for(int i = state.columns - 2; i >= winNumber - 1; i--){
			int continuous1 = 0;
			int continuous2 = 0;
			int underneath1 = 0;
			int underneath2 = 0;
			ArrayList<Integer> list = new ArrayList<>();
			int columnProgress = 0;
			while(i - columnProgress >= 0){
				list.add(state.boardMatrix[state.rows - columnProgress - 1][i - columnProgress]);
				columnProgress++;
			}
			//check bottom
			if (list.get(list.size() - 1) == 0){
				for (int index = (list.size() - 2); index >= 0; index--){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[state.rows - index - 2][i - index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[state.rows - index - 2][i - index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalBot[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalBot[continuous2 - 1];
					}
				}
			}
			
			//check top
			if (list.get(0) == 0){
				continuous1 = 0;
				continuous2 = 0;
				underneath1 = 0;
				underneath2 = 0;
				for (int index = 1; index < list.size(); index++){
					if (list.get(index) == 1){
						if(continuous2 == 0){
							continuous1 += 1;
						}
						else{
							break;
						}
						if(continuous1 == 1){
							if (state.boardMatrix[state.rows - index - 2][i - index] != 0){
								underneath1 = 1;
							}
						}
					}
					else if (list.get(index) == 2){
						if(continuous1 == 0){
							continuous2 += 1;
						}
						else{
							break;
						}
						if(continuous2 == 1){
							if (state.boardMatrix[state.rows - index - 2][i - index] != 0){
								underneath2 = 1;
							}
						}
					}
				}
				if (underneath1 == 1){
					utility += diagonal_Underneath[continuous1 - 1];
				}
				else if(underneath2 == 1){
					utility -= diagonal_Underneath[continuous2 - 1];
				}
				else{
					if (continuous1 != 0){
						utility += diagonalTop[continuous1 - 1];
					}
					if (continuous2 != 0){
						utility -= diagonalTop[continuous2 - 1];
					}
				}
			}
			
		}
		
		return utility;
	}
}
