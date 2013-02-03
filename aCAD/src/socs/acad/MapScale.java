/**
 * MapScale is the small widget in the HUD that shows the scale of the grid.
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 */
package socs.acad;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import android.util.FloatMath;

public class MapScale extends Entity {
	// Storage Variables
	private VertexBufferObjectManager vboManager;
	private RoundedRectangle background;
	private float gridScale;
	private float gridIncrement;
	
	// Display constants
	static final float MAXWIDTH = 150;
	static final float MARGIN = 15;

	/**
	 * Constructor
	 */
	public MapScale(float pX, float pY, float pScale, float pGridIncrement, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY);
		gridScale = pScale;
		gridIncrement = pGridIncrement;
		vboManager = pVertexBufferObjectManager;
		repopulate();
	}
	
	/**
	 * Update variables and re-draw the scale
	 */
	public void update(float pGridScale, float pGridIncrement) {
		gridScale = pGridScale;
		gridIncrement = pGridIncrement;
		repopulate();
	}

	/**
	 * Redraw the scale
	 */
	private void repopulate() {
		this.detachChildren();

		// Variables for placement
		float lineHeight = 10;
		float endLineHeight = 30;
		
		// Compute how wide the scale will be
		float scaledGridSize = gridIncrement / gridScale;
		float parts = FloatMath.floor(MAXWIDTH / scaledGridSize);
		float width = scaledGridSize * parts;
		
		background = new RoundedRectangle(0, 0, width + MARGIN + MARGIN, endLineHeight + MARGIN + MARGIN, vboManager);
		this.attachChild(background);
		
		Line l;
		// Add the baseline
		l = new Line(MARGIN, MARGIN + lineHeight, MARGIN + width, MARGIN + lineHeight, vboManager);
		l.setColor(1f, 1f, 1f, .9f);
		l.setLineWidth(3);
		this.attachChild(l);
		
		// Add the Scale Lines
		for (int i = 0; i <= parts; i++) {
			l = new Line(MARGIN + (i * scaledGridSize), MARGIN, MARGIN + (i * scaledGridSize), MARGIN + (i == 0 || i == parts ? endLineHeight : lineHeight), vboManager);
			l.setLineWidth(2);
			this.attachChild(l);
		}
		
		// Add the Scale Measurement
		Text t = new Text(MARGIN + (width / 2), MARGIN + lineHeight + 5, ACadEngineActivity.measurementFont, MutablePolygon.formatDistance(gridIncrement * parts), 30, new Text.TextOptions(HorizontalAlign.CENTER), vboManager);
		t.setPosition(t.getX() - t.getWidth() / 2f, t.getY()); // Center
		t.setAlpha(0);
		t.setColor(1f, 1f, 0f);
		t.setAlpha(1f);
		this.attachChild(t);
	}
}
