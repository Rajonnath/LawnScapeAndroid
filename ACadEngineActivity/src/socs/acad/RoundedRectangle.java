/**
 * Rounded Rectangle is a helper class that creates a rectangle with rounded
 * corners.  It is mainly cosmetic and is used, for example, behind the map
 * scale widget.
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

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Polygon;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.FloatMath;

public class RoundedRectangle extends Entity
{
	// Size and status variables
	float mWidth, mHeight;
	float mRadius = 20;
	int mParts = 15;
	Polygon polygon = null;
	float[] mVertexX, mVertexY;
	VertexBufferObjectManager vboManager;

	/**
	 * Constructor
	 */
	public RoundedRectangle(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY);
		mWidth = pWidth;
		mHeight = pHeight;
		vboManager = pVertexBufferObjectManager;
		calculateVertices();
	}

	/**
	 * Calculate the vertices of the rounded corners. I hope your trigonometry
	 * is up to date...
	 */
	public void calculateVertices()
	{
		mVertexX = new float[mParts * 4];
		mVertexY = new float[mParts * 4];
		float radius = mRadius;
		float angle;
		// Make the radius smaller if the height and width won't support it
		if (radius > mWidth / 2 || radius > mHeight / 2)
		{
			radius = Math.min(mWidth, mHeight) / 2;
		}
		// Compute all the vertices
		float halfPI = (float) (Math.PI / 2);
		int j;
		for (int i = 0; i < mParts; i++)
		{
			// We don't want to have to duplicate the trigonometric functions
			// repeatedly, so set the value for 4 points at a time
			float xOffset, yOffset;
			angle = i * halfPI / (mParts - 1);
			xOffset = radius * FloatMath.cos(angle);
			yOffset = radius * FloatMath.sin(angle);
			// Upper Right
			j = i;
			mVertexX[j] = mWidth - radius + xOffset;
			mVertexY[j] = radius - yOffset;
			// Upper Left
			j = mParts + mParts;
			mVertexX[j - 1 - i] = radius - xOffset;
			mVertexY[j - 1 - i] = radius - yOffset;
			// LowerLeft
			mVertexX[j + i] = radius - xOffset;
			mVertexY[j + i] = mHeight - radius + yOffset;
			// LowerRight
			j += j;
			mVertexX[j - 1 - i] = mWidth - radius + xOffset;
			mVertexY[j - 1 - i] = mHeight - radius + yOffset;
		}
		this.detachChild(polygon);
		polygon = new Polygon(0, 0, mVertexX, mVertexY, vboManager);
		polygon.setColor(0f, 0f, 0f, .6f);
		this.attachChild(polygon);
	}
}
