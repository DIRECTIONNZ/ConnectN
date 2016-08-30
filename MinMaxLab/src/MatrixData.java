import java.util.ArrayList;

public class MatrixData
{
	public final int HORIZONTAL = 0;
	public final int LEFT_DIAGONAL = 1;
	public final int VERTICAL = 2;
	public final int RIGHT_DIAGONAL = 3;
	
	public class InARow
	{
		public int row;
		public int col;
		public int dir;
		public int length;
		
		public InARow(int r, int c, int d, int l) //row, column, direction
		{
			row = r;
			col = c;
			dir = d;
			length = l;
		}
	}
	
	public ArrayList<ArrayList<InARow>> sequences;
	
	public MatrixData() //ones, twos, threes
	{
		sequences = new ArrayList<ArrayList<InARow>>();
	}
	
	public void addData(ArrayList<InARow> seq)
	{
		for (InARow s : seq)
		{
			int length = s.length;
			if (s != null)  sequences.get(length).add(s); //Is this line legal (.get(index) part)?
		}
	}
	
	/*
		This function is VERY important as we must use it to gauge the "score" of a particular node in our minimax tree. Especially sinec it will be unlikely to find an end scenario
	*/
	//Currently taking number of #of 1's*1, #of 2's*2, etc.
	public int getWeight()
	{
		int sum = 0;
		int i=0;
		for (i=0; i<sequences.size(); i++)
		{
			sum += sequences.get(i).size() * (i+1);
		}
		return sum / i; //Definitely probably want a better metric here
	}
	
	public MatrixData getMatrixData(StateTree tree, boolean isUser, int n) //n is board size
	{
		//Do we want this line? I'm not really sure tbh lol. Don't think so tho
		sequences = new ArrayList<ArrayList<InARow>>(n);
		//ArrayList<ArrayList<InARow>> matrixSequences = new ArrayList<ArrayList<InARow>>();
		
		MatrixData data = new MatrixData();
		
		int currentUser = (isUser ? 1 : 0) + 1;
		for (int c = 0; c<tree.columns; c++)
		{	
			for (int r = tree.rows - 1; r >= 0; r--)
			{
				if (tree.boardMatrix[r][c] == 0) //If column is empty, move on to the next one
				{
					r = -1;
					continue;
				}
				
				if (tree.boardMatrix[r][c] == currentUser)
				{
					boolean isNewSequence = true;
					if (r < tree.rows && c > 0) //Check one space below us to make sure we haven't added this sequence before
					{ 
						for (int i=0; i<=1; i++)
						{
							for (int j=-1; j<=1; j++)
							{
								if (i == 0 && j == 0) continue;
								
								if (tree.boardMatrix[r+i][c+j] == currentUser)
								{
									isNewSequence = false;
									i = 2; //break
									j = 2;
								}
							}
						}
					}
					if (isNewSequence)
					{
						//sequences = getSequences(tree, r, c);
						data.addData(getSequences(tree, r ,c));
					}
					
				}
			}
		}
		
		return data;
	}

	//Array[0] is ones, array[1] is twos, array[2] is threes (because that makes total sense to a programmer)
	private ArrayList<InARow> getSequences(StateTree tree, int row, int col)
	{
		ArrayList<InARow> sequences = new ArrayList<InARow>();
		boolean isNode = true; //set to false if there is any sequence involving this node
		
		for (int dir=0; dir<4; dir++)
		{
			if (dir == HORIZONTAL)
			{
				int length = seqLength(tree, row, col, 0, 1);
				if (length > 1)
				{
					isNode = false;
					sequences.add(new InARow(row, col, dir, length));
				}
			}
			else if (dir == LEFT_DIAGONAL)
			{
				int length = seqLength(tree, row, col, -1, -1);
				if (length > 1)
				{
					isNode = false;
					sequences.add(new InARow(row, col, dir, length));
				}
			}
			else if (dir == VERTICAL)
			{
				int length = seqLength(tree, row, col, -1, 0);
				if (length > 1)
				{
					isNode = false;
					sequences.add(new InARow(row, col, dir, length));
				}
			}
			else if (dir == RIGHT_DIAGONAL)
			{
				int length = seqLength(tree, row, col, -1, 1);
				if (length > 1)
				{
					isNode = true;
					sequences.add(new InARow(row, col, dir, length));
				}
			}
			if (isNode)
			{
				sequences.add(new InARow(row, col, -1, 1));
				
			}
		}
		
		return sequences;
	}

	private int seqLength(StateTree tree, int row, int col, int rowAdd, int colAdd)
	{
		int count = 0;
		int user = tree.boardMatrix[row][col];
		for (int r = row; r>=0; r+=rowAdd)
		{
			for (int c = col; c<tree.columns; c+=colAdd)
			{
				if (tree.boardMatrix[r][c] == user)
				{
					count++;
				}
				else
				{
					return count;
				}
			}
		}
		
		return count;
	}
}
