package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth;
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random();


	/*
	 * These two constructors are here for testing purposes. 
	 */
	public Block() {}

	public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
		this.xCoord=x;
		this.yCoord=y;
		this.size=size;
		this.level=lvl;
		this.maxDepth = maxD;
		this.color=c;
		this.children = subBlocks;
	}


	/*
	 * Creates a random block given its level and a max depth. 
	 * 
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		this.level = lvl;
		this.maxDepth = maxDepth;
		if(this.level>this.maxDepth || this.level < 0 || this.maxDepth <= 0){
			throw new IllegalArgumentException("Invalid input");
		}

		if (this.level < this.maxDepth) {
			double ranNum = gen.nextDouble();
			if (ranNum < Math.exp(-0.25 * this.level)) {
				this.children = new Block[4];
				for (int i = 0; i < 4; i++) {
					this.children[i] = new Block(this.level + 1, this.maxDepth);
				}
			} else {
				int colorIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
				this.color = GameColors.BLOCK_COLORS[colorIndex];
				this.children = new Block[0];
			}
		} else {
			int colorIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
			this.color = GameColors.BLOCK_COLORS[colorIndex];
			this.children = new Block[0];
		}
	}


	/*
	  * Updates size and position for the block and all of its sub-blocks, while
	  * ensuring consistency between the attributes and the relationship of the 
	  * blocks. 
	  * 
	  *  The size is the height and width of the block. (xCoord, yCoord) are the 
	  *  coordinates of the top left corner of the block. 
	 */
	public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
		int checker = size;
		for (int i = this.level; i < this.maxDepth; i++) {
			if (checker <= 0 || checker % 2 != 0 || this.xCoord < 0 || this.yCoord < 0 || this.level < 0 || this.maxDepth <= 0) {
				throw new IllegalArgumentException("Invalid input");
			}
			checker = checker/2;
		}

		this.size = size;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

		if (this.children != null && this.children.length == 4) {
			int childSize = this.size / 2;
			children[0].updateSizeAndPosition(childSize, xCoord + childSize, yCoord);
			children[1].updateSizeAndPosition(childSize, xCoord, yCoord);
			children[2].updateSizeAndPosition(childSize, xCoord, yCoord + childSize);
			children[3].updateSizeAndPosition(childSize, xCoord + childSize, yCoord + childSize);
		}
	}


	/*
  	* Returns a List of blocks to be drawn to get a graphical representation of this block.
  	* 
  	* This includes, for each undivided Block:
  	* - one BlockToDraw in the color of the block
  	* - another one in the FRAME_COLOR and stroke thickness 3
  	* 
  	* Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  	*  
  	* The order in which the blocks to draw appear in the list does NOT matter.
  	*/

	public ArrayList<BlockToDraw> getBlocksToDraw() {
		ArrayList<BlockToDraw> blocksToDraw = new ArrayList<>();
		if (this.children == null || this.children.length == 0) {
			blocksToDraw.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
			blocksToDraw.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
		} else {
			for (Block child : this.children) {
				blocksToDraw.addAll(child.getBlocksToDraw());
			}
		}
		return blocksToDraw;
	}


	/*
	 * This method is provided and you should NOT modify it. 
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}


	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than 
	 * the lowest block at the specified location, then return the block 
	 * at the location with the closest level value.
	 * 
	 * The location is specified by its (x, y) coordinates. The lvl indicates 
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
	 * contain the location (x, y) too. This is why we need lvl to identify 
	 * which Block should be returned. 
	 * 
	 * Input validation: 
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	public Block getSelectedBlock(int x, int y, int lvl) {
		if (lvl < this.level || lvl > this.maxDepth) {
			throw new IllegalArgumentException("Invalid input");
		}

		if (x < this.xCoord || x >= this.xCoord + this.size || y < this.yCoord || y >= this.yCoord + this.size) {
			return null;
		}

		if ( this.children == null || this.level == lvl || this.children.length == 0) {
			return this;
		}

		for (Block child : this.children) {
			Block selectedBlock = child.getSelectedBlock(x, y, lvl);
			if (selectedBlock != null) {
				return selectedBlock;
			}
		}
		return null;
	}

	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {
		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("Invalid input");
		}

		if (this.children != null && this.children.length == 4) {
			Block tmp = this.children[0];
			if (direction == 0) {
				this.children[0] = this.children[3];
				this.children[3] = tmp;

				tmp = this.children[1];
				this.children[1] = this.children[2];
				this.children[2] = tmp;

			} else {
				this.children[0] = this.children[1];
				this.children[1] = tmp;

				tmp = this.children[2];
				this.children[2] = this.children[3];
				this.children[3] = tmp;

			}
			for (Block child : this.children) {
				child.reflect(direction);
			}
			updateSizeAndPosition(this.size, this.xCoord, this.yCoord);

		}

	}


	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("Invalid input");
		}

		if (this.children != null && this.children.length == 4) {
			Block tmp = this.children[0];
			if (direction == 1) {
				this.children[0] = this.children[1];
				this.children[1] = this.children[2];
				this.children[2] = this.children[3];
				this.children[3] = tmp;
			} else {
				this.children[0] = this.children[3];
				this.children[3] = this.children[2];
				this.children[2] = this.children[1];
				this.children[1] = tmp;
			}
			for (Block child : this.children) {
				child.rotate(direction);
			}
			updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
		}

	}


	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.  
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block 
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {
		if (this.level == 0 || this.level == this.maxDepth) {
			return false;
		}

		this.children = new Block[4];

		for (int i = 0; i < this.children.length; i++) {
			this.children[i] = new Block(this.level + 1, this.maxDepth);
		}
		updateSizeAndPosition(this.size, this.xCoord, this.yCoord);

		return true;
	}


	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		int arrSize = (int) Math.pow(2, this.maxDepth - this.level);
		Color[][] arr = new Color[arrSize][arrSize];

		if (this.children == null || this.children.length == 0) {
			for (int i = 0; i < arrSize; i++) {
				for (int j = 0; j < arrSize; j++) {
					arr[i][j] = this.color;
				}
			}
		} else {
			Color[][] uR = this.children[0].flatten();
			Color[][] uL = this.children[1].flatten();
			Color[][] lL = this.children[2].flatten();
			Color[][] lR = this.children[3].flatten();

			int halfArrSize = arrSize / 2;

			for (int i = 0; i < halfArrSize; i++) {
				for (int j = 0; j < halfArrSize; j++) {
					arr[i][j + halfArrSize] = uR[i][j];
					arr[i][j] = uL[i][j];
					arr[i + halfArrSize][j] = lL[i][j];
					arr[i + halfArrSize][j + halfArrSize] = lR[i][j];
				}
			}
		}
		return arr;
	}


	// These two get methods have been provided. Do NOT modify them.
	public int getMaxDepth() {
		return this.maxDepth;
	}

	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block. 
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);
		} else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}

	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				} else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}
}