package my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


import javax.swing.JComponent;
import javax.swing.JFrame;



public class GameField extends JComponent {

	private static final long serialVersionUID = 1L;
	JFrame frame;
	int cellCount = 8;
	double gameFieldSide, gameFieldX, gamefieldY, cellSide;	
	
	ParameterizedArray<Cell> blackStones, whiteStones, freeCells, possibleCells;
	
	ParameterizedArray<Cell> allCells = new ParameterizedArray<Cell>(Cell.class, cellCount);
	int moveCounter = 0;

	public GameField(JFrame f) {
		this.frame = f;
		initDimensions();
		initCells();
	}

	private void initCells() {
		for (int i = 0; i < cellCount; i++) {
			for (int j = 0; j < cellCount; j++){				
//				allCells [i][j] = new Cell(i, j, cellSide);
				allCells.put(i, j, new Cell(i, j, cellSide));
			}
		}
	}

	private void initDimensions() {
		double minDimension = Math.min(frame.getHeight(), frame.getWidth());
		gameFieldSide = minDimension - 70;

		gameFieldX = (frame.getWidth() - gameFieldSide) / 2;
		gamefieldY = (frame.getHeight() - gameFieldSide) / 2 - 10;

		cellSide = gameFieldSide / cellCount;
	}

	public void paintComponent(Graphics g) {
		// System.out.println("width: " + frame.getWidth() + "\n height: " +
		// frame.getHeight() + "\n side:" + gameFieldSide + "\n x: " +
		// gameFieldX);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(0, gamefieldY, gameFieldSide,
				gameFieldSide));
		g2.setColor(Color.YELLOW);
		g2.fill(new Rectangle2D.Double(1, gamefieldY + 1, gameFieldSide - 1,
				gameFieldSide - 1));
	}

}
