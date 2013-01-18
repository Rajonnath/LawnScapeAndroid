/**
 * Mutable Rectangle is a subclass of MutablePolygon that proved a rectangle
 * that does not malform its angles when a vertex is adjusted.
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 */

package socs.acad;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class MutableRectangle extends MutablePolygon {

	/**
	 * Constructor
	 */
	public MutableRectangle(float pX, float pY,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pVertexBufferObjectManager);
		// Override state variables 
		canAddVertex = false;
		canDeleteVertex = false;
	}

	/**
	 * Move a single Vertex as well as its neighbors to maintain the rectangle.
	 */
	@Override
	public void moveVertex(int index, float pX, float pY) {
		// Record the distance change to be applied
		float xChange = pX - mVertexX[index];
		float yChange = pY - mVertexY[index];
		
		// Set the new amount for this vertex and its neighbors
		mVertexX[index] = pX;
		mVertexY[index] = pY;
		switch (index) {
		case 0:
			mVertexX[3] = pX;
			mVertexY[1] = pY;
			break;
		case 1:
			mVertexX[2] = pX;
			mVertexY[0] = pY;
			break;
		case 2:
			mVertexX[1] = pX;
			mVertexY[3] = pY;
			break;
		case 3:
			mVertexX[0] = pX;
			mVertexY[2] = pY;
			break;
		}
		
		// Loop through all vertices and modify them by the Change amount
		// Also change the vertices to match
		for (int i = 0; i < 4; i++) {
			mVertexX[i] -= xChange / 2;
			mVertexY[i] -= yChange / 2;
			// Move the selected vertex to match.
			if (vertexTouchable[i]) {
				vertex[i].setPosition(mVertexX[i], mVertexY[i]);
			}
		}
		
		// Update everything that is generated from the above info
		updateStats();
		updateVertices(mVertexX, mVertexY);
		refreshColor(); // Must be done otherwise it won't show up
		recalculateMeasurements();
		populateMeasurements();
	}
}
