package my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class GameField extends JComponent {

	private static final long serialVersionUID = 1L;
	JFrame frame;
	int cellCount = 8;
	int undoLimit = 5;
	int moveCounter = 0;
	double gameFieldSide, gameFieldX, gamefieldY, cellSide;

	Cell allCells[][] = new Cell[cellCount][cellCount];
	ArrayList<Cell> blackStones = new ArrayList<Cell>();
	ArrayList<Cell> whiteStones = new ArrayList<Cell>();
	ArrayList<Cell> freeCells = new ArrayList<Cell>();
	ArrayList<Cell> possibleCells = new ArrayList<Cell>();
	ArrayList<Cell> changeVector = new ArrayList<Cell>();

	Vector<Cell[][]> undoAllCells = new Vector<Cell[][]>(undoLimit);
	Vector<ArrayList<Cell>> redoPossibleCells = new Vector<ArrayList<Cell>>(
			undoLimit);
	Stack<ArrayList<Cell>> undoPossibleCells = new Stack<ArrayList<Cell>>();
	Vector<Cell[][]> redoAllCells = new Vector<Cell[][]>(undoLimit);

	public GameField(JFrame f) {
		this.frame = f;
		initDimensions();
		initCells();
	}

	private void initCells() {
		for (int i = 0; i < cellCount; i++) {
			for (int j = 0; j < cellCount; j++) {
				allCells[i][j] = new Cell(i, j, cellSide);
				freeCells.add(allCells[i][j]);
			}
		}
	}

	private void initDimensions() {
		double minDimension = Math.min(frame.getHeight(), frame.getWidth());
		gameFieldSide = minDimension - 70;

		gameFieldX = (frame.getWidth() - gameFieldSide) / 2;
		gamefieldY = (frame.getHeight() - gameFieldSide) / 2 - 20;

		cellSide = gameFieldSide / cellCount;
	}

	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(0, gamefieldY, gameFieldSide,
				gameFieldSide));
		g2.setColor(Color.YELLOW);
		g2.fill(new Rectangle2D.Double(1, gamefieldY + 1, gameFieldSide - 1,
				gameFieldSide - 1));

		if (moveCounter == 0)
			possibleCells.add(allCells[cellCount / 2 - 1][cellCount / 2 - 1]);
		setStartStones(cellCount / 2 - 1, cellCount / 2 - 1, 1);
		setStartStones(cellCount / 2, cellCount / 2, 1);
		setStartStones(cellCount / 2, cellCount / 2 - 1, 0);
		setStartStones(cellCount / 2 - 1, cellCount / 2, 0);

		for (Cell[] cv : allCells) {
			for (Cell cg : cv) {
				g2.setColor(Color.BLACK);
				cg.paint(0, gamefieldY, g2);
			}
		}
	}

	private void setStartStones(int i, int j, int descriptor) {

		if (allCells[i][j].free) {
			allCells[i][j].makeBusy(descriptor);
			freeCells.remove(allCells[i][j]);
			possibleCells.remove(allCells[i][j]);
			if (descriptor == 0) {
				blackStones.add(allCells[i][j]);
				setPossibleCells(i, j);
			}
			if (descriptor == 1) {
				whiteStones.add(allCells[i][j]);
				setPossibleCells(i, j);
			}
		}
	}

	public void makeCellBusy(int i, int j) {

		int descriptor = moveCounter % 2;

		allCells[i][j].makeBusy(descriptor);
		freeCells.remove(allCells[i][j]);
		possibleCells.remove(allCells[i][j]);
		if (descriptor == 0) {
			blackStones.add(allCells[i][j]);
			setPossibleCells(i, j);
			setkAdjacentCells(i, j);
		}
		if (descriptor == 1) {
			whiteStones.add(allCells[i][j]);
			setPossibleCells(i, j);
			setkAdjacentCells(i, j);
		}
	}

	private void setPossibleCells(int i, int j) {
		ArrayList<Cell> aroundCells = getAround(i, j);
		for (Cell c : aroundCells) {
			if (c.free) {
				if (!possibleCells.contains(c)) {
					possibleCells.add(c);
				}
			}
		}
	}

	private void setkAdjacentCells(int i, int j) {
		ArrayList<Cell> aroundCells = getAround(i, j);
		for (Cell c : aroundCells) {
			if (c.descriptor != moveCounter % 2) {
				int d = getDirection(c, i, j);
				changeVector = checkDirection(d, i, j);
				if (changeVector != null) {
					for (Cell cell : changeVector) {
						setStoneColor(cell);
						// System.out.println("Color: " + cell.stoneColor);
					}
				}
				changeVector = null;
			}
		}
	}

	private void setStoneColor(Cell cell) {
		int descriptor = moveCounter % 2;
		int i = cell.i_index;
		int j = cell.j_index;

		allCells[i][j].setStoneColor(descriptor);
		// System.out.println("Color changed");
		if (descriptor == 0) {
			whiteStones.remove(allCells[i][j]);
			blackStones.add(allCells[i][j]);
		}
		if (descriptor == 1) {
			blackStones.remove(allCells[i][j]);
			whiteStones.add(allCells[i][j]);
		}
	}

	private boolean checkAdjacentCells(int i, int j) {
		ArrayList<Cell> aroundCells = getAround(i, j);
		for (Cell c : aroundCells) {
			if (c.descriptor != moveCounter % 2) {
				int d = getDirection(c, i, j);
				changeVector = checkDirection(d, i, j);
				if (changeVector != null) {
					// System.out.println("Can move");
					return true;
				}

			}
		}
		return false;
	}

	private ArrayList<Cell> checkDirection(int d, int i, int j) {
		ArrayList<Cell> vector = new ArrayList<Cell>();
		switch (d) {

		case 1: {
			for (int x = i - 1, y = j - 1; x >= 0; x--, y--) {
				if (allCells[x][y].free)
					return null;
				if (allCells[x][y].descriptor != moveCounter % 2) {
					if (y == 0)
						return null;
					if (x == 0)
						return null;
					vector.add(allCells[x][y]);
				}
				if (allCells[x][y].descriptor == moveCounter % 2) {
					// System.out.println("Ender cell:" + x + ", " + y +
					// " Descriptor: " + allCells[x][y].descriptor);
					return vector;
				}
			}
		}

		case 2: {
			for (int x = i - 1; x >= 0; x--) {
				if (allCells[x][j].free)
					return null;
				if (allCells[x][j].descriptor != moveCounter % 2) {
					if (x == 0)
						return null;
					vector.add(allCells[x][j]);
				}
				if (allCells[x][j].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 3: {
			for (int x = i - 1, y = j + 1; x >= 0; x--, y++) {
				if (allCells[x][y].free)
					return null;
				if (allCells[x][y].descriptor != moveCounter % 2) {
					if (y == cellCount - 1)
						return null;
					if (x == 0)
						return null;
					vector.add(allCells[x][y]);
				}
				if (allCells[x][y].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 4: {
			for (int y = j - 1; y >= 0; y--) {
				if (allCells[i][y].free)
					return null;
				if (allCells[i][y].descriptor != moveCounter % 2) {
					if (y == 0)
						return null;
					vector.add(allCells[i][y]);
				}
				if (allCells[i][y].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 5: {
			for (int y = j + 1; y < cellCount; y++) {
				if (allCells[i][y].free)
					return null;
				if (allCells[i][y].descriptor != moveCounter % 2) {
					if (y == cellCount - 1)
						return null;
					vector.add(allCells[i][y]);
				}
				if (allCells[i][y].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 6: {
			for (int x = i + 1, y = j - 1; x < cellCount; x++, y--) {
				if (allCells[x][y].free)
					return null;
				if (allCells[x][y].descriptor != moveCounter % 2) {
					if (y == 0)
						return null;
					if (x == cellCount - 1)
						return null;
					vector.add(allCells[x][y]);
				}
				if (allCells[x][y].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 7: {
			for (int x = i + 1; x < cellCount; x++) {
				if (allCells[x][j].free)
					return null;
				if (allCells[x][j].descriptor != moveCounter % 2) {
					if (x == cellCount - 1)
						return null;
					vector.add(allCells[x][j]);
				}
				if (allCells[x][j].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}

		case 8: {
			for (int x = i + 1, y = j + 1; x < cellCount; x++, y++) {
				if (allCells[x][y].free)
					return null;
				if (allCells[x][y].descriptor != moveCounter % 2) {
					if (y == cellCount - 1)
						return null;
					if (x == cellCount - 1)
						return null;
					vector.add(allCells[x][y]);
				}
				if (allCells[x][y].descriptor == moveCounter % 2) {
					return vector;
				}
			}
		}
		}
		return null;
	}

	private int getDirection(Cell c, int i, int j) {
		if (c.i_index < i) { // top side!
			if (c.j_index < j)
				return 1; // top left corner;
			if (c.j_index == j)
				return 2; // up line;
			return 3; // top right corner;
		}

		if (c.i_index == i) { // left or right line!
			if (c.j_index < j)
				return 4; // left line;
			if (c.j_index > j)
				return 5; // right line;
		}

		if (c.i_index > i) { // down side! This 'if' not necessary, I know...
			if (c.j_index < j)
				return 6; // down left corner;
			if (c.j_index == j)
				return 7; // down line;
			return 8;// down right corner;
		}
		return 0;
	}

	public ArrayList<Cell> getAround(int i, int j) {
		ArrayList<Cell> aroundCells = new ArrayList<Cell>();
		for (int x = i - 1; x < i + 2; x++) {
			for (int y = j - 1; y < j + 2; y++) {
				if ((0 <= x) && (x < cellCount) && (0 <= y) && (y < cellCount))
					aroundCells.add(allCells[x][y]);
			}
		}
		aroundCells.remove(allCells[i][j]);
		return aroundCells;
	}

	public Cell findCell(double x, double y) {
		for (Cell[] cv : allCells) {
			for (Cell cg : cv) {
				if (cg.contains(x, y, gamefieldY))
					return cg;
			}
		}
		return null;
	}

	public boolean canMove(Cell cell) {
		if (!cell.free)
			return false;
		if (!possibleCells.contains(cell))
			return false;
		return checkAdjacentCells(cell.i_index, cell.j_index);
	}

	public boolean moveIsPossible() {
		for (Cell c : possibleCells) {
			if (canMove(c))
				return true;
		}
		return false;
	}

	public void newGame() {
		moveCounter = 0;
		blackStones.clear();
		whiteStones.clear();
		freeCells.clear();
		possibleCells.clear();
		initCells();
	}

	public void undo() {
		moveCounter--;
		possibleCells.clear();
		Cell lastAllCells[][] = undoAllCells.get(moveCounter-1);

		for (int i = 0; i < cellCount; i++) {
			for (int j = 0; j < cellCount; j++) {		
				
				allCells[i][j] = lastAllCells[i][j];
//				System.out.println(lastAllCells[i][j].stoneColor + " complain " + allCells[i][j].stoneColor);
			}
		}

		ArrayList<Cell> lastPossibleCells = undoPossibleCells.pop();
		for (int i = 0; i < lastPossibleCells.size(); i++)
			possibleCells.add(lastPossibleCells.get(i));
		setStoneLists();
		System.out.println("Number of black stones:" + blackStones.size());
		

		System.out.println("Undo");
		repaint();
	}

	private void setStoneLists() {
		blackStones.clear();
		whiteStones.clear();
		freeCells.clear();
		for (Cell[] cv : allCells) {
			for (Cell cg : cv) {
				if (cg.free)
					freeCells.add(allCells[cg.i_index][cg.j_index]);
				if (cg.descriptor == 0)
					blackStones.add(allCells[cg.i_index][cg.j_index]);
				if (cg.descriptor == 1)
					whiteStones.add(allCells[cg.i_index][cg.j_index]);
			}
		}
	}

	public void autosave() {
		System.out.println("autosave");
		Cell lastAllCells[][] = new Cell[cellCount][cellCount];
		int blackCount = 0;
		for (int i = 0; i < cellCount; i++) {
			for (int j = 0; j < cellCount; j++) {
				lastAllCells[i][j] = allCells[i][j];
				if (lastAllCells[i][j].descriptor == 0) blackCount++;
				
			}
		}
		System.out.println(blackCount + " compare " + blackStones.size());

		undoAllCells.add(moveCounter-1, lastAllCells);

		ArrayList<Cell> lastPossibleCells = new ArrayList<Cell>();
		for (int i = 0; i < possibleCells.size(); i++)
			lastPossibleCells.add(possibleCells.get(i));
		undoPossibleCells.add(moveCounter-1, lastPossibleCells);
	}

}
