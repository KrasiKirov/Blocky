package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattened = board.flatten();
		int flattenedHeight = flattened[0].length;
		int flattenedWidth = flattened.length;
		int score = 0;
		boolean[][] visited = new boolean[flattenedWidth][flattenedHeight];


		for (int i = 0; i < flattenedWidth; i++) {
			for (int j = 0; j < flattenedHeight; j++) {
				if (flattened[i][j].equals(this.targetGoal) && !visited[i][j]) {
					int blobSize = undiscoveredBlobSize(i, j, flattened, visited);
					score = Math.max(score, blobSize);
				}
			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		int columns = unitCells[0].length;
		int rows = unitCells.length;

		if (i < 0 || i >= rows || j < 0 || j >= columns || visited[i][j] || !unitCells[i][j].equals(this.targetGoal)) {
			return 0;
		}

		visited[i][j] = true;
		int blobSize = 1;

		blobSize += undiscoveredBlobSize(i + 1, j, unitCells, visited);
		blobSize += undiscoveredBlobSize(i - 1, j, unitCells, visited);
		blobSize  += undiscoveredBlobSize(i, j + 1, unitCells, visited);
		blobSize  += undiscoveredBlobSize(i, j - 1, unitCells, visited);

		return blobSize ;
	}

}
