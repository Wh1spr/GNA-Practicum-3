package gna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import libpract.*;


/**
 * Implement the methods stitch, seam and floodfill.
 */
public class Stitcher
{	
	/**
	 * Return the sequence of positions on the seam. The first position in the
	 * sequence is (0, 0) and the last is (width - 1, height - 1). Each position
	 * on the seam must be adjacent to its predecessor and successor (if any).
	 * Positions that are diagonally adjacent are considered adjacent.
	 * 
	 * image1 and image2 are both non-null and have equal dimensions.
	 *
	 * Remark: Here we use the default computer graphics coordinate system,
	 *   illustrated in the following image:
	 * 
	 *        +-------------> X
	 *        |  +---+---+
	 *        |  | A | B |
	 *        |  +---+---+
	 *        |  | C | D |
	 *        |  +---+---+
	 *      Y v 
	 * 
	 *   The historical reasons behind using this layout is explained on the following
	 *   website: http://programarcadegames.com/index.php?chapter=introduction_to_graphics
	 * 
	 *   Position (y, x) corresponds to the pixels image1[y][x] and image2[y][x]. This
	 *   convention also means that, when an automated test mentioned that it used the array
	 *   {{A,B},{C,D}} as a test image, this corresponds to the image layout as shown in
	 *   the illustration above.
	 */
	public List<Position> seam(int[][] image1, int[][] image2) {
		Comparator<State> comp = new Comparator<State>(){

			@Override
			public int compare(State o1, State o2) {
				return Integer.compare(o1.getTotalCost(), o2.getTotalCost());
			}
		};
		
		PriorityQueue<State> pQueue = new PriorityQueue<State>(comp);
		HashMap<Position, State> queue = new HashMap<Position, State>();
		HashSet<Position> closed = new HashSet<Position>();
		
		int height = image1.length;
		int width = image1[0].length;
		Position start = new Position(0,0);
		Position finish = new Position(width - 1, height - 1);
		
		State init = new State(null, start, ImageCompositor.pixelSqDistance(image1[0][0], image2[0][0]));
		pQueue.add(init);
		queue.put(start, init);
		
		while(!pQueue.peek().getPosition().equals(finish)) {
			State current = pQueue.poll();
			queue.remove(current.getPosition());
			
			if (!closed.contains(current.getPosition())) {
				//posities die nog niet closed zijn
				closed.add(current.getPosition());
				
				for (Position pos : getNeighbors(current.getPosition(), height, width)) {
					State neighbor = new State(current, pos, ImageCompositor.pixelSqDistance(image1[pos.getY()][pos.getX()],image2[pos.getY()][pos.getX()]));
					if (!closed.contains(pos)) {
						if (queue.containsKey(pos)) {
							//Deze neighbor zit al in de queue, nu kijken of het pad naar deze neighbor 
							//korter is dan wat er al in de queue zit.
							if (neighbor.getTotalCost() < queue.get(pos).getTotalCost()) {
								//pad is korter, vervangen in de queue
								queue.put(pos, neighbor);
								pQueue.remove(queue.get(pos));
								pQueue.add(neighbor);
							}
						} else {
							queue.put(pos, neighbor);
							pQueue.add(neighbor);
						}
					}
				}
			}
		}
		
		State end = pQueue.poll();
		ArrayList<Position> seam = new ArrayList<Position>();
		while(end != null) {
			seam.add(end.getPosition());
			end = end.getPrevious();
		}
		
		Collections.reverse(seam);
		return seam;
	}

	/**
	 * Apply the floodfill algorithm described in the assignment to mask. You can assume the mask
	 * contains a seam from the upper left corner to the bottom right corner. The seam is represented
	 * using Stitch.SEAM and all other positions contain the default value Stitch.EMPTY. So your
	 * algorithm must replace all Stitch.EMPTY values with either Stitch.IMAGE1 or Stitch.IMAGE2.
	 *
	 * Positions left to the seam should contain Stitch.IMAGE1, and those right to the seam
	 * should contain Stitch.IMAGE2. You can run `ant test` for a basic (but not complete) test
	 * to check whether your implementation does this properly.
	 */
	public void floodfill(Stitch[][] mask) {
		int height = mask.length;
		int width = mask[0].length;

		HashSet<Position> closed = new HashSet<Position>();
		Stack<Position> todo = new Stack<Position>();
		
		Position start = new Position(0,height-1); //linksonder beginnen met IMAGE1 te vullen
		
		todo.add(start);
		while(!todo.isEmpty()) {
			Position current = todo.pop();
			mask[current.getY()][current.getX()] = Stitch.IMAGE1;
			closed.add(current);
			for (Position e : getNonDiagonalNeighbors(current, height, width)) {
				if (!closed.contains(e)) {
					if (mask[e.getY()][e.getX()] != Stitch.SEAM) {
						todo.add(e);
					}
				}
			}
		}
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (mask[i][j] != Stitch.SEAM && mask[i][j] != Stitch.IMAGE1) 
					mask[i][j] = Stitch.IMAGE2;
		
		
	}

	/**
	 * Return the mask to stitch two images together. The seam runs from the upper
	 * left to the lower right corner, where in general the rightmost part comes from
	 * the second image (but remember that the seam can be complex, see the spiral example
	 * in the assignment). A pixel in the mask is Stitch.IMAGE1 on the places where
	 * image1 should be used, and Stitch.IMAGE2 where image2 should be used. On the seam
	 * record a value of Stitch.SEAM.
	 * 
	 * ImageCompositor will only call this method (not seam and floodfill) to
	 * stitch two images.
	 * 
	 * image1 and image2 are both non-null and have equal dimensions.
	 */
	public Stitch[][] stitch(int[][] image1, int[][] image2) {
		List<Position> seam = seam(image1,image2);
		Stitch[][] result = new Stitch[image1.length][image1[0].length];
		
		for (Position pos : seam) {
			result[pos.getY()][pos.getX()] = Stitch.SEAM;
		}
		
		floodfill(result);
		return result;
	}
	
	public static HashSet<Position> getNeighbors(Position pos, int height, int width) {
		HashSet<Position> neighbors = new HashSet<Position>();
		
		for (int x = pos.getX()-1; x <= pos.getX()+1; x++) {
			for (int y = pos.getY()-1; y <= pos.getY()+1; y++) {
				if (x >= 0 && y >= 0 && x < width && y < height && !(x == pos.getX() && y == pos.getY())) {
					neighbors.add(new Position(x,y));
				}
			}
		}
		return neighbors;
	}
	
	public static HashSet<Position> getNonDiagonalNeighbors(Position pos, int height, int width) {
		HashSet<Position> neighbors = new HashSet<Position>();
		
		if (pos.getX() != 0) neighbors.add(new Position(pos.getX()-1, pos.getY()));
		if (pos.getY() != 0) neighbors.add(new Position(pos.getX(), pos.getY()-1));
		if (pos.getX() != width-1) neighbors.add(new Position(pos.getX()+1, pos.getY()));
		if (pos.getY() != height-1) neighbors.add(new Position(pos.getX(), pos.getY()+1));
		
		return neighbors;
	}
}


