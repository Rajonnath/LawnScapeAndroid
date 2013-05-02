/**
 * MutableLine is a subclass of MutablePolygon that displays vertices as an
 * open shape rather than closed.  It can be used for fences and pipes.
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 * MODIFIED BY TEAM CPU AT MIDWESTERN STATE UNIVERSITY
 *   - Junior Fletcher
 *   - Veronica McClure
 *   - Lauren Rios
 *   - Chase Sawyer
 *   - Matt Swezey
 */
package socs.acad;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class MutableLine extends MutablePolygon
{

	/**
	 * Constructor
	 */
	public MutableLine(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pVertexBufferObjectManager);
		isRotatable = false;
		closedState = false;
		minimumVertexCount = 2;
	}

	/**
	 * Called after MutableLine is attached, used to attach display polygon.
	 */
	@Override
	public void onAttached()
	{
		// Set a default square shape, 120px X 120px (10ft. X 10ft.)
		mVertexX = new float[] { -60f, 60f };
		mVertexY = new float[] { 0f, 0f };

		// Setup the vertex and measurement defaults
		vertexTouchable = new boolean[mVertexX.length];
		measurementVisible = new boolean[mVertexX.length];
		measurement = new float[mVertexX.length];
		for (int i = 0; i < mVertexX.length; i++)
		{
			vertexTouchable[i] = true;
			measurementVisible[i] = true;
		}

		// Add the display shape
		regenerateShape();
		regenerateOutline();
		setState(PolygonState.VIEW);
	}
}
