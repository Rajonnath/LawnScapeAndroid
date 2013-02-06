/**
 * GroupEntity is used as the "layers" of the scene
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
import org.andengine.entity.scene.ITouchArea;

public class GroupEntity extends Entity {

	/**
	 * Constructor
	 */
	public GroupEntity() {
	}

	/**
	 * Constructor
	 */
	public GroupEntity(float pX, float pY) {
		super(pX, pY);
	}

	/**
	 * Method called when child is attached
	 */
	@Override
	public void attachChild(final IEntity pEntity) throws IllegalStateException {
		super.attachChild(pEntity);
		registerOnParent(pEntity);
	}

	/**
	 * Method called when child is attached
	 */
	public void attachChild(final IEntity pEntity, final int pIndex) throws IllegalStateException {
	    super.attachChild(pEntity);
		registerOnParent(pEntity);
		
	}
	
	/**
	 * Register the child's touch events on the parent scene.
	 */
    private void registerOnParent(final IEntity pEntity) {
    	IEntity parent = this.getParent();
    	if (parent instanceof Scene && pEntity instanceof MutablePolygon) {
    		((Scene)parent).registerTouchArea((MutablePolygon)pEntity);
    	}
    }

    /**
     * Redirect to Scene's unregisterTouchArea().
     */
	public boolean unregisterTouchArea(final ITouchArea pTouchArea) {
		return ((Scene)this.getParent()).unregisterTouchArea(pTouchArea);
	}
	
	/**
	 * Disable all MutablePolygon child elements.
	 */
	public void disableTouchableChildren() {
		int childCount = this.getChildCount();
		IEntity child;
		for (int i = 0; i < childCount; i++) {
			child = this.getChildByIndex(i);
			if (child instanceof MutablePolygon) {
				((MutablePolygon)child).setTouchableState(false);
			}
		}
	}

	/**
	 * Enable all MutablePolygon child elements.
	 */
	public void enableTouchableChildren() {
		int childCount = this.getChildCount();
		IEntity child;
		for (int i = 0; i < childCount; i++) {
			child = this.getChildByIndex(i);
			if (child instanceof MutablePolygon) {
				((MutablePolygon)child).setTouchableState(true);
			}
		}
	}
}
