/**
 * ACadScene is a subclass of Scene that has methods to help with
 * actions specific to the aCAD implementation.
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 */
package socs.acad;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.Color;

public class ACadScene extends Scene {
	// Variables for tracking aspects of the surrounding world
	private float width, height, menuWidth;
	private float minX, minY, maxX, maxY;
	private boolean childrenExist;
	public Entity activeEntity;

	/**
	 * Constructor
	 */
	public ACadScene(float width, float height, float menuWidth) {
		super();
		// Save variables
		this.width = width;
		this.height = height;
		this.menuWidth = menuWidth;
		this.activeEntity = null;
	}

	/**
	 * The next four functions deal with setting the scale.  When the scale is
	 * changed, the child entities must be updated as well.
	 */
	@Override
	public void setScaleX(final float pScaleX) {
		super.setScaleX(pScaleX);
		this.scaleUpdated();
	}

	// See Comment Above
	@Override
	public void setScaleY(final float pScaleY) {
		super.setScaleY(pScaleY);
		this.scaleUpdated();
	}

	// See Comment Above
	@Override
	public void setScale(final float pScale) {
		super.setScale(pScale);
		this.scaleUpdated();
	}

	// See Comment Above
	@Override
	public void setScale(final float pScaleX, final float pScaleY) {
		super.setScale(pScaleX, pScaleY);
		this.scaleUpdated();
	}
	
	/**
	 * After scaling, update the grid and child elements.
	 */
	public void scaleUpdated() {
		int childCount = this.getChildCount();
		int groupCount;
		Entity e, c;
		for (int i = 0; i < childCount; i++) {
			e = (Entity)this.getChildByIndex(i);
			if (e instanceof Grid) {
				((Grid)e).update();
			}
			else if (e instanceof GroupEntity) {
				groupCount = e.getChildCount();
				for (int j = 0; j < groupCount; j++) {
					c = (Entity)e.getChildByIndex(j);
					if (c instanceof MutablePolygon) {
						((MutablePolygon)c).setVertexScale(1 / this.getScaleX());
					}
				}
			}
		}
	}

	/**
	 * The next two functions deal with setting the position.  When the
	 * position is changed, the child entities must be updated as well.
	 */
	@Override
	public void setPosition(final IEntity pOtherEntity) {
		super.setPosition(pOtherEntity);
		positionUpdated();
	}

	// See Comment Above
	@Override
	public void setPosition(final float pX, final float pY) {
		super.setPosition(pX, pY);
		positionUpdated();
	}

	/**
	 * After moving, update the grid layer.
	 */
	public void positionUpdated() {
		int childCount = this.getChildCount();
		Entity e;
		for (int i = 0; i < childCount; i++) {
			e = (Entity)this.getChildByIndex(i);
			if (e instanceof Grid) {
				((Grid)e).update();
			}
		}
	}
	
	/**
	 * Re-scale and re-center so that all items are visible
	 */
	public void resetView() {
		clearTouchAreas();
		// Clear extrema variables
		minX = minY = Float.MAX_VALUE;
		maxX = maxY = Float.MIN_VALUE;
		childrenExist = false;
		// If the scene display has been altered, none of the measurements
		// will be accurate.  The quick fix is to reset the scale and position
		// before performing calculations.
		this.setScale(1);
		this.setPosition(0, 0);
		// Examine all child elements to re-compute extrema variables
		examineChildren(this);
		if (childrenExist) {
			float xRange = Math.abs(maxX - minX) * 1.2f;
			float yRange = Math.abs(maxY - minY) * 1.2f;
			float xCenter = (maxX + minX) / 2f;
			float yCenter = (maxY + minY) / 2f;
			
			// Determine the scale
			float xScale = (width - menuWidth) / xRange;
			float yScale = height / yRange;
			float scale = (xScale < yScale) ? xScale : yScale;

			// Set the new scale and position
			this.setScale(scale);
			this.setPosition((width / 2) - (xCenter * scale) + (menuWidth / 2), (height / 2) - (yCenter * scale));
		}
	}
	
	/**
	 * Recursive method to iterate through all children and sub-children
	 * and determine the extrema of X and Y
	 */
	private void examineChildren(IEntity parent) {
		int count = parent.getChildCount();
		float localMaxima[];
		IEntity child;
		// Loop through the children and use their extrema to determine the
		// bounds of the drawing
		for (int i = 0; i < count; i++) {
			child = parent.getChildByIndex(i);
			if (!(child instanceof Grid)) {
				if (child instanceof MutablePolygon) {
					childrenExist = true;
					// Get the object extrema {minX, minY, maxX, maxY}
					localMaxima = ((MutablePolygon)child).getSceneExtrema();
					if (localMaxima[0] < minX) {
						minX = localMaxima[0];
					}
					if (localMaxima[1] < minY) {
						minY = localMaxima[1];
					}
					if (localMaxima[2] > maxX) {
						maxX = localMaxima[2];
					}
					if (localMaxima[3] > maxY) {
						maxY = localMaxima[3];
					}
				}
				else {
					// Check the children of this entity
					examineChildren(child);
				}
			}
		}
	}
}
