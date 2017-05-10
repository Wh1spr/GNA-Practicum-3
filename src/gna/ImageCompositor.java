package gna;

import java.lang.Math;

import libpract.*;

public class ImageCompositor {
	/** Squared Euclidean distance between pixel values x and y */
	public static int pixelSqDistance(int x, int y)
	{
		int r = (x & 0xFF0000) - (y & 0xFF0000) >> 16;
		int g = (x & 0xFF00)   - (y & 0xFF00) >> 8;
		int b = (x & 0xFF)     - (y & 0xFF);

		return r * r + g * g + b * b;
	}


	private static void flipVertical(int[][] img)
	{
		int h = img.length;
		for (int row = 0; row < h / 2; row++) {
			for (int col = 0; col < img[0].length; col++) {
				int t = img[row][col];
				img[row][col] = img[(h - row - 1)][col];
				img[(h - row - 1)][col] = t;
			}
		}
	}


	private static void flipVertical(Stitch[][] img)
	{
		int h = img.length;
		for (int row = 0; row < h / 2; row++) {
			for (int col = 0; col < img[0].length; col++) {
				Stitch t = img[row][col];
				img[row][col] = img[(h - row - 1)][col];
				img[(h - row - 1)][col] = t;
			}
		}
	}


	public static void main(String[] args)
	{
		String file1, file2;
		int[][] img1, img2;

		//
		// Step 1 - Read input images and process arguments
		//

		if (args.length < 2) {
			System.out.println("ImageCompositor requires (at least) 2 arguments.");
			return;
		}

		int offsetx = 0;
		int offsety = 0;
		if (args.length > 3) {
			offsetx = Integer.parseInt(args[2]);
			offsety = Integer.parseInt(args[3]);
			if (offsetx < 0) {
				System.out.println("Negative offsetx not supported (switch both the images instead)");
				return;
			}
		}

		img1 = Util.readImage(args[0]);
		img2 = Util.readImage(args[1]);
		if (img1 == null) {
			System.out.println("unable to read image: " + args[0]);
			return;
		} else if (img2 == null) {
			System.out.println("unable to read image: " + args[1]);
			return;
		}

		int width1  = img1[0].length;
		int height1 = img1.length;
		int width2  = img2[0].length;
		int height2 = img2.length;


		//
		// Step 2 - Create two images for the overlapping part which we will stitch.
		//

		// FIXME: Check if the images actually overlap
		// Take image1 as reference
		int stitchXstart = offsetx;
		int stitchXend   = Math.min(stitchXstart + width2, width1);
		int stitchYstart = Math.max(offsety, 0);
		int stitchYend   = Math.min(offsety + height2, height1);

		int stitchHeight = stitchYend - stitchYstart;
		int stitchWidth  = stitchXend - stitchXstart;

		int[][] toStitch1 = new int[stitchHeight][stitchWidth];
		int[][] toStitch2 = new int[stitchHeight][stitchWidth];

		for (int row = 0; row < stitchHeight; row++) {
			for (int col = 0; col < stitchWidth; col++) {
				toStitch1[row][col] = img1[stitchYstart + row][stitchXstart + col];
				toStitch2[row][col] = img2[offsety > 0 ? row : row - offsety][col];
			}
		}


		//
		// Step 3 - Create composed image based on offsetx and offxety
		//

		// Assure that seam runs from top-left to bottom-right
		boolean shouldFlip = offsety != 0 && stitchXend == width1 && stitchYend == height1;
		if (shouldFlip) {
			flipVertical(toStitch1);
			flipVertical(toStitch2);
		}
		Stitch[][] mask = new Stitcher().stitch(toStitch1, toStitch2);
		if (shouldFlip) {
			flipVertical(toStitch1);
			flipVertical(toStitch2);
			flipVertical(mask);
		}

		// Calculate dimensions of resulting image
		int resultWidth, resultHeight;
		resultWidth = Math.max(width1, width2 + offsetx);
		if (offsety > 0)
			resultHeight = Math.max(height1, height2 + offsety);
		else
			resultHeight = Math.max(height1 - offsety, height2);

		int[][] result = new int[resultHeight][resultWidth];

		// Copy image1 to resulting image
		for (int row = 0; row < height1; row++) {
			for (int col = 0; col < width1; col++) {
				// For negative offsety we are pushing image1 down
				result[(offsety < 0 ? row - offsety : row)][col] = img1[row][col];
			}
		}

		// Copy image2 to resulting image
		for (int row = 0; row < height2; row++) {
			for (int col = 0; col < width2; col++) {
				// For positive offsety we need to push image2 down
				result[(offsety < 0 ? row : row + offsety)][col + offsetx] = img2[row][col];
			}
		}

		// Copy overlapping part according to the stitch
		for (int row = 0; row < stitchHeight; row++) {
			for (int col = 0; col < stitchWidth; col++) {
				// The value Stitcher.SEAM is replaced by Stitcher.SEAM
				if (mask[row][col] == Stitch.SEAM)
					mask[row][col] = Stitch.IMAGE2;

				// Pick the pixel according to the stitcher.
				if (mask[row][col] == Stitch.IMAGE1)
					result[Math.abs(offsety) + row][offsetx + col] = toStitch1[row][col];
				else
					result[Math.abs(offsety) + row][offsetx + col] = toStitch2[row][col];
			}
		}


		//
		// Step 4 - Display result
		//

		Util.displayImage(result);
	}

}


