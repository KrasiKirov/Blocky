package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattened = board.flatten();
		int flattenedHeight = flattened[0].length;
		int flattenedWidth = flattened.length;
		int score = 0;

		for (int i = 0; i < flattenedWidth; i++) {
			for (int j = 0; j < flattenedHeight; j++) {
				if (flattened[i][j].equals(this.targetGoal)) {
					if ((i == 0) || (i == flattenedWidth - 1) || (j == 0) || (j == flattenedHeight - 1)) {
						if ((i == 0 && j == 0) || (i == 0 && j == flattenedHeight - 1) || (i == flattenedWidth - 1 && j == 0) || (i == flattenedWidth - 1 && j == flattenedHeight - 1)){
							score += 2;
						}
						else{
							score += 1;
						}
					}
				}
			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
