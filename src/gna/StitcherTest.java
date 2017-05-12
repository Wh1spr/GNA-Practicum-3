package gna;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import org.junit.*;

import libpract.Position;
import libpract.Stitch;

public class StitcherTest {
	Stitcher stitcher = new Stitcher();
	//These values are set in such a way that the seam will run accross "100" cells in Image1
	private final int[][] Image1 = {
			{100,001,001,001,001},
			{000,100,001,001,001},
			{000,000,100,100,001},
			{000,000,000,000,100},
			{000,000,000,000,100}};
	private final int[][] Image2 = {
			{100,000,000,000,000},
			{000,100,000,000,000},
			{000,000,100,100,000},
			{000,000,100,100,100},
			{000,000,100,100,100}};
	
	private final Stitch[][] MaskComplete = new Stitch[5][5];
	private final Stitch[][] MaskToFlood = new Stitch[5][5];
	
	private List<Position> seam = null;
	
	@Before
	public void setUp(){
		seam = stitcher.seam(Image1, Image2);
		for(int i = 0; i < Image1.length; i++) {
			for (int j = 0; j < Image1[0].length; j++) {
				if (Image1[i][j] == 0) MaskComplete[i][j] = Stitch.IMAGE1;
				if (Image1[i][j] == 1) MaskComplete[i][j] = Stitch.IMAGE2;
				if (Image1[i][j] == 100) {
					MaskComplete[i][j] = Stitch.SEAM;
					MaskToFlood[i][j] = Stitch.SEAM;
				}
			}
		}
	}
	
	@Test
	public void testSeam_Adjacency() {
		
		boolean adjacent = true;
		
		for (int i = 1; i < seam.size(); i++){
			if (!seam.get(i).isAdjacentTo(seam.get(i-1))) {
				adjacent = false;
			}
		}
		assertTrue("The positions on the seam are not adjacent.",adjacent);
	}
	
	@Test
	public void testSeam_correctEnding() {
		assertEquals("The ending position of the seam is incorrect.",new Position(4,4),seam.get(seam.size()-1));
	}
	
	@Test
	public void testSeam_correctStart() {
		assertEquals("The starting position of the seam is incorrect.",new Position(0,0),seam.get(0));
	}
	
	@Test
	public void testFloodFill() {
		stitcher.floodfill(MaskToFlood);
		
		for (int i = 0; i < MaskToFlood.length; i++) {
			assertArrayEquals("floodfill did not fill the mask correctly.",MaskComplete[i],MaskToFlood[i]);
		}
	}
	
	@Test
	public void testStitch() {
		Stitch[][] endMask = stitcher.stitch(Image1, Image2);
		for (int i = 0; i < MaskComplete.length; i++) {
			assertArrayEquals("The stitch method did not return the expected mask.",MaskComplete[i],endMask[i]);
		}
	}
	
	@Test
	public void testNeighbors_Middle() {
		HashSet<Position> Neighbors = Stitcher.getNeighbors(new Position(1,1), 3, 3);
		Position[] neighbors = {new Position(0,0),new Position(1,0),new Position(2,0),new Position(0,1),
				new Position(0,2),new Position(1,2),new Position(2,1),new Position(2,2)};
		assertEquals("The incorrect amount of neighbors was returned (diagonal, open).",8, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (diagonal, open).",Neighbors.contains(e));
		}
	}
	
	@Test
	public void testNeighbors_Side() {
		HashSet<Position> Neighbors = Stitcher.getNeighbors(new Position(1,0), 3, 3);
		Position[] neighbors = {new Position(0,0),new Position(2,0),new Position(0,1),new Position(1,1),new Position(2,1)};
		assertEquals("The incorrect amount of neighbors was returned (diagonal, side).",5, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (diagonal, side).",Neighbors.contains(e));
		}
	}
	
	@Test
	public void testNeighbors_Corner() {
		HashSet<Position> Neighbors = Stitcher.getNeighbors(new Position(0,0), 3, 3);
		Position[] neighbors = {new Position(0,1),new Position(1,0),new Position(1,1)};
		assertEquals("The incorrect amount of neighbors was returned (diagonal, corner).",3, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (diagonal, corner).",Neighbors.contains(e));
		}
	}
	
	@Test
	public void testNonDiagonalNeigbors_Middle() {
		HashSet<Position> Neighbors = Stitcher.getNonDiagonalNeighbors(new Position(1,1), 3, 3);
		Position[] neighbors = {new Position(0,1),new Position(1,0),new Position(2,1),new Position(1,2)};
		assertEquals("The incorrect amount of neighbors was returned (non diagonal, open).",4, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (non diagonal, open).",Neighbors.contains(e));
		}
	}
	
	@Test
	public void testNonDiagonalNeigbors_Side() {
		HashSet<Position> Neighbors = Stitcher.getNonDiagonalNeighbors(new Position(1,0), 3, 3);
		Position[] neighbors = {new Position(0,0),new Position(2,0),new Position(1,1)};
		assertEquals("The incorrect amount of neighbors was returned (non diagonal, side).",3, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (non diagonal, side).",Neighbors.contains(e));
		}
	}
	
	@Test
	public void testNonDiagonalNeigbors_Corner() {
		HashSet<Position> Neighbors = Stitcher.getNonDiagonalNeighbors(new Position(0,0), 3, 3);
		Position[] neighbors = {new Position(0,1),new Position(1,0)};
		assertEquals("The incorrect amount of neighbors was returned (non diagonal, corner).",2, Neighbors.size());
		for (Position e : neighbors) {
			assertTrue("A neighbor was returned that is not adjacant to the position (non diagonal, corner).",Neighbors.contains(e));
		}
	}
}
