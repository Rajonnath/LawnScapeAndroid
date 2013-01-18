/*FILENAME- Camer2D.java
 * DESCRIPTION - To set camera angle in required position 
 */
//package of class
package sprinklerheads.irrigation.cadsystem;

//importing required packages
import javax.microedition.khronos.opengles.GL10;
import android.util.Log;

//camere2D class
public class Camera2D {
	public final Vector2D position;
	public final float frustrum_w;
	public final float frustrum_h;
	final GLGraphics glgraphics;
	
	private float zoom;
	
	/* Camera 2D Default Constructor -------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Camera2D( GLGraphics glg, float f_width, float f_height) {
		glgraphics = glg;
		frustrum_w = f_width;
		frustrum_h = f_height;
		position = new Vector2D(frustrum_w / 2, frustrum_h / 2);
		zoom = 1.0f;
	}
	
	/* Set the Viewport and Matricies -----------------------------------------
	 -    This method sets viewport (Area of the screen to draw), and a 
	 -    orthographic view frustrum for 2D rendering.  The frustrum is set from 
	 -    1.0 to 1.0 on the Z axis so that all 2D objects can be rendered to 
	 -    (x, y, 0.0)
	 -------------------------------------------------------------------------*/
	public void setViewPortAndMatrices() {
		GL10 gl = glgraphics.getGL();
		
		gl.glViewport(0,0, glgraphics.getWidth(), glgraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(position.x - frustrum_w * zoom / 2,
				    position.x + frustrum_w * zoom / 2,
				    position.y - frustrum_h * zoom / 2,
				    position.y + frustrum_h * zoom / 2,
				    1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/* Center ------------------------------------------------------------------
	 -   Center the camera on this point.
	 -------------------------------------------------------------------------*/
	public void center(float x, float y) {
		
	}
	
	/* setZoom -----------------------------------------------------------------
	 -   Zoom in by the factor. (factor = 1.0 does nothing).
	 -------------------------------------------------------------------------*/
	public void setZoom(float factor) {
		zoom = factor;
	}
	
	/* ZoomIn ------------------------------------------------------------------
	 -   Zoom in by the factor. (factor = 1 does nothing).
	 -------------------------------------------------------------------------*/
	public void zoomIn(int factor) {
		if (factor != 0)
			zoom /= factor;
	}
	
	/* ZoomOut ------------------------------------------------------------------
	 -   Zoom out by the factor. (factor = 1 does nothing).
	 -------------------------------------------------------------------------*/
	public void zoomOut(int factor) {
		zoom *= factor;
	}
	
	/* getZoom ------------------------------------------------------------------
	 -   Get the current zoom factor.
	 -------------------------------------------------------------------------*/
	public float getZoom() {
		return zoom;
	}
	
	/* Touch To World -----------------------------------------------------------
	 -   Convert the touched screen coordinates to the world coordinates.
	 -------------------------------------------------------------------------*/
	public void touchToWorld(Vector2D touch) {
		touch.x = (touch.x / (float)glgraphics.getWidth()) * frustrum_w * zoom;
		touch.y = (touch.y / (float)glgraphics.getHeight()) * frustrum_h * zoom;
		touch.add(position).sub(frustrum_w * zoom / 2, frustrum_h * zoom / 2);
	}
}
