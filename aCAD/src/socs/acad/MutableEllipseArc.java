/**
 * MutableEllipseArc is a horrible name for the rounded shape used by
 * sprinklers and round landscape (such as trees).
 * BY THE SOCS GROUP AT MIDWESTERN STATE UNIVERSITY
 *   - Samantha Tomeï
 *   - O'Neal Georges
 *   - Corey Pennycuff
 *   - Sri Lasya Brundavanam
 */
package socs.acad;
import java.util.Arrays;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.FloatMath;



public class MutableEllipseArc extends MutablePolygon {
	// Variables specific to the circle
	protected int verticesInArc;
	protected int totalVertices;
	protected float arcIncrements = 5;
	protected float[] range = new float[0];
	
	// JSON variables
	double jsonRadius;
	double jsonMaxAngle;
	double jsonTouchX;
	double jsonTouchY;

	/**
	 * Constructor
	 */
	public MutableEllipseArc(float pX, float pY,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pVertexBufferObjectManager);
		// Override state variables 
		canAddVertex = false;
		canDeleteVertex = false;
		verticesInArc = 30;
		totalVertices = verticesInArc + 1;
		// Set the distances to snap to when moving the vertex
		this.setRange(new float[]{96,108,120,132,144,156,168,180,192,204,216,228,240,252,264,276,288,300,312,324,336,348,360,372,384,396,408,420,432,444,456,468,480,492,504,516,528,540,552,564,576,588,600,612,624,636,648,660,672,684,696,708,720,732,744,756,768,780,792,804,816,828,840,852,864,876,888,900,912,924,936,948,960,972,984,996,1008,1020,1032,1044,1056,1068,1080,1092,1104,1116,1128,1140,1152});
	}

	/**
	 * Called after MutableEllipse is attached, used to attach display polygon.
	 */
	@Override
	public void onAttached() {
		// Set an initial size and rotation
		setArcVertices(range[0], 360);
		
		// Setup the vertex and measurement defaults
		vertexTouchable = new boolean[mVertexX.length];
		measurementVisible = new boolean[mVertexX.length];
		measurement = new float[mVertexX.length];
		for (int i = 0; i < mVertexX.length; i++) {
			vertexTouchable[i] = false;
			measurementVisible[i] = false;
		}
		vertexTouchable[verticesInArc] = true;
		measurementVisible[verticesInArc] = true;
		measurementVisible[0] = true;
		
		// Add the display shape
		regenerateShape();
		regenerateOutline();
		setState(PolygonState.VIEW);
	}
	
	/**
	 * Set the vertices for a specific arc measurement.
	 * maxAngle is measured in degrees.
	 */
	public void setArcVertices(double radius, double maxAngle) {
		mVertexX = new float[totalVertices];
		mVertexY = new float[totalVertices];
		float theta;
		// The first point is at the origin
		mVertexX[0] = 0f;
		mVertexY[0] = 0f;
		for (int i = 1; i < totalVertices; i++) {
			theta = (float)Math.toRadians((maxAngle * (i - 1) / (verticesInArc - 1)));
			mVertexX[i] = (float)(radius * FloatMath.cos(theta));
			mVertexY[i] = (float)(radius * FloatMath.sin(theta));
		}
	}
	
	/**
	 * Move a single Vertex but snap it to a grid.
	 */
	@Override
	public void moveVertex(int index, float pX, float pY) {
		float newDistance = FloatMath.sqrt(distanceSquared(0, 0, pX, pY));
		if (range.length != 0) {
			// Snap newDistance to a value in range
			if (newDistance <= range[0]) {
				newDistance = range[0];
			}
			else if (newDistance >= range[range.length - 1]) {
				newDistance = range[range.length - 1];
			}
			else {
				// Logically, the value must exist between elements of the array
				int i = 0;
				while (newDistance > range[i]) {
					i++;
				}
				// Now, newDistance is between [i - 1] and [i]
				// Determine which it is closer to
				if ((newDistance - range[i - 1]) < (range[i] - newDistance)) {
					newDistance = range[i - 1];
				}
				else {
					newDistance = range[i];
				}
			}
		}
		// Snap the arc to a multiple of arcIncrements
		double newArc = Math.toDegrees(Math.atan2(pY, pX));
		if (newArc < 0) {
			// Protect against negative degrees
			newArc += 360;
		}
		newArc = Math.round(newArc / arcIncrements) * arcIncrements;
		if (newArc == 0) {
			// Don't show a non-existent wedge
			newArc = 360;
		}
		
		// calculate the new polygon
		setArcVertices(newDistance, newArc);
		
		// Move the appropriate vertex to match
		if (vertexTouchable[verticesInArc]) {
			vertex[verticesInArc].setPosition(mVertexX[verticesInArc], mVertexY[verticesInArc]);
		}
		
		// Update everything that is generated from the above info
		updateStats();
		updateVertices(mVertexX, mVertexY);
		refreshColor(); // Must be done otherwise it won't show up
		recalculateMeasurements();
		populateMeasurements();
	}

	/**
	 * Set the range snap-to points for this shape.
	 */
	public void setRange(float[] newRange) {
		range = newRange;
		Arrays.sort(range);
	}
	
	/**
	 * Get Vertices as a JSON object
	 */
	@Override
	public JSONObject getJSONVertices() {
		JSONObject object = new JSONObject();
		try {
			// object.put("Type", type);
			object.put("EllipseRadius", jsonRadius);
			object.put("EllipseAngle", jsonMaxAngle);
			object.put("TouchX", this.getX());
			object.put("TouchY", this.getX());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;
	}

	/**
	 * Set the object size, called from JSON reader
	 */
	@Override
	public void setJSONEllipseVertices(double jsonAngle,double jsonInRadius)
	{
		setArcVertices(jsonAngle,jsonInRadius);
		updateStats();
		updateVertices(mVertexX, mVertexY);
		refreshColor(); // Must be done otherwise it won't show up
	}

	/**
	 * Fix the background color.
	 * Unfortunately, a polygon will not show up when it is first added
	 * to the Scene, even if the background color is already set.  It must be
	 * set again, so here is a function to do it all in one place.
	 */
	public void refreshColor() {
		switch(polygonState) {
		case EDIT:
			// Set the background color.
			if (displayShape != null) {
				displayShape.setColor(.8f, .8f, .8f, .6f);
			}
			break;
		case VIEW:
		default:
			// Set the background color
			if (displayShape != null) {
				displayShape.setColor(.8f, .8f, .8f, .4f);
			}
			break;
		}
	}

}
