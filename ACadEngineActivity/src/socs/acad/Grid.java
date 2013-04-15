/**
 * Grid class used to draw the blueprint-like grid
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 */
package socs.acad;

import java.util.Vector;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.shape.Shape;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.vbo.IVertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.Constants;

import android.graphics.Color;

public class Grid extends Shape {
	// World information variables
	private float width, height;
	private float oldScale = Float.NaN;
	protected VertexBufferObjectManager vboManager = null;
	
	// Where lines are stored
	private Vector<Line> lines = new Vector<Line>();
	
	// Local variables related to line placement
	protected float scale, pixelScale, minX, minY, maxX, maxY;

	/**
	 * Constructor
	 */
	public Grid(float pX, float pY, float width, float height, ShaderProgram pShaderProgram, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pShaderProgram);
		vboManager = pVertexBufferObjectManager;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Method ran when the object is attached to a parent.
	 */
	@Override
	public void onAttached() {
		update();
	}
	
	/**
	 * Update the line set based on the parent's info.
	 */
	public void update() {
		// First, adjust the scale so that the grid is properly and
		// proportionally spaced for the current scale
		scale = this.getParent().getScaleX();
		if (scale != oldScale) {
			// The scale has changed, so re-draw the lines
			oldScale = scale;

			minX = 0;
			minY = 0;
			maxX = width / scale;
			maxY = height / scale;

			// Decide on a scale to show (numbers are in feet).
			// Decimal numbers will be rendered in inches.
			float[] suitableScale = {1, 2, 5, 10, 20, 50, 100, 150, 200, 250, 500, 1000};
			pixelScale = 1;
			for (int i = 0; i < suitableScale.length && suitableScale[i] * scale < 2.5f; i++) {
				pixelScale = suitableScale[i];
			}

			// Convert pixelscale to inches
			pixelScale *= 12;

			// Create an overlap to reduce recreating lines on small movements
			minX = -pixelScale;
			minY = -pixelScale;
			maxX = (width / scale) + pixelScale;
			maxY = (height / scale) + pixelScale;
			
			removeLines();
			populateLines();
			
			// Update the MapScale
			ACadEngineActivity.getMapScale().update(1 / scale, pixelScale);
		}
		
		// Second, set the position of the grid to fully cover the visible
		// screen area, while holding the grid placement steady relative to the
		// objects on the screen.
		final float[] touchAreaLocalCoordinates = this.getParent().convertSceneToLocalCoordinates(0, 0);
		final float localX = touchAreaLocalCoordinates[Constants.VERTEX_INDEX_X];
		final float localY = touchAreaLocalCoordinates[Constants.VERTEX_INDEX_Y];

		this.setPosition(localX - (localX % pixelScale), localY - (localY % pixelScale));
	}
	
	/**
	 * Remove all lines.
	 */
	public void removeLines() {
		int lineCount;
		// Clear the lines
		lineCount = lines.size();
		for (int i = 0; i < lineCount; i++) {
			this.detachChild(lines.get(i));
		}
		lines.clear();
	}
	
	
	/**
	 * Populate the lines for the grid.
	 */
	public void populateLines() {
		Line l;
		// Populate the Horizontal
		float startY = Math.round(minY / pixelScale);
		for (float y = startY; y < maxY; y += pixelScale) {
			l = new Line(minX, y, maxX, y, vboManager);
			//set line color? Consider implementing change for dark areas
			org.andengine.util.color.Color linecolor = null;
			if(socs.acad.ACadEngineActivity.gridLineColorScheme != 0){
				l.setColor(linecolor.GREEN);
			}	
			else
				l.setColor(1f,1f,1f,.1f);
			
			this.attachChild(l);
			lines.add(l);
		}
		// Populate the Vertical
		float startX = Math.round(minX / pixelScale);
		for (float x = startX; x < maxX; x += pixelScale) {
			l = new Line(x, minY, x, maxY, vboManager);
			//set line color? Consider implementing change for dark areas
			org.andengine.util.color.Color linecolor = null;
			if(socs.acad.ACadEngineActivity.gridLineColorScheme != 0){
				l.setColor(linecolor.GREEN);
			}	
			else
				l.setColor(1f,1f,1f,.1f);
			
			this.attachChild(l);
			lines.add(l);
		}
	}
	
	/**
	 * Must have for class.
	 */
	@Override
	public boolean collidesWith(IShape pOtherShape) {
		return false;
	}

	/**
	 * Must have for class.
	 */
	@Override
	public IVertexBufferObject getVertexBufferObject() {
		return null;
	}

	/**
	 * Must have for class.
	 */
	@Override
	public boolean contains(float pX, float pY) {
		return false;
	}

	/**
	 * Must have for class.
	 */
	@Override
	protected void onUpdateVertices() {
	}
}