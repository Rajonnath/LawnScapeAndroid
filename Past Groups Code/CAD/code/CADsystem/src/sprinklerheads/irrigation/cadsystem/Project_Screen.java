/*FILENAME- ProjectScreen.java
 * DESCRIPTION -Project Screen class used for handling openGL graphics and camera angles
 */
//package of class
package sprinklerheads.irrigation.cadsystem;

//importing required packages
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

//Project_Screen class
public class Project_Screen {
	GLGraphics glgraphics;
	Camera2D camera;
	//to hanlde vertices
	FloatBuffer verts;
	Vertices vertices;
	
	/* Project_Screen Constructor ----------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Project_Screen(GLGraphics glg, float site_width, float site_height) {
	
		glgraphics = glg;
		
		float x_zoom = site_width / glgraphics.getWidth();
		float y_zoom = site_height / glgraphics.getHeight();
		
		camera = new Camera2D(glgraphics, glgraphics.getWidth(), glgraphics.getHeight());
		
		if( x_zoom > y_zoom) {
			camera.setZoom(x_zoom);
		} else {
			camera.setZoom(y_zoom);
		}
		
		vertices = new Vertices(glgraphics, 12, 18, false, false);
		
		float x0 = glg.getWidth()/2;
		float y0 = glg.getHeight()/2;		
		
		
		
		vertices.setVertices(new float[] {	0.0f	, 0.0f	, 0.0f	, 1.0f,		// Quad 1
											40.0f	, 0.0f	, 0.0f	, 1.0f,
											40.0f	, 40.0f	, 0.0f	, 1.0f,
											0.0f	, 40.0f	, 0.0f	, 1.0f,
				
											0.0f	, 40.0f	, 0.0f	, 1.0f,		// Quad 2
											80.0f	, 40.0f	, 0.0f	, 1.0f,
											80.0f	, 60.0f	, 0.0f	, 1.0f,
											0.0f	, 60.0f	, 0.0f	, 1.0f,
				
											60.0f	, 20.0f	, 0.0f	, 1.0f,		// Quad 3
											80.0f	, 20.0f	, 0.0f	, 1.0f,
											80.0f	, 40.0f	, 0.0f	, 1.0f,
											60.0f	, 40.0f , 0.0f	, 1.0f           	
			                               }, 0, 48);
		
		vertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4, 8, 9, 10, 10, 11, 8}, 0, 18);
	}
	
	/* Update Camera -----------------------------------------------------------
	 -    This method resets the camera. 
	 -------------------------------------------------------------------------*/
	public void updateCamera() {		
		camera.setViewPortAndMatrices();
	}
	
	/* Center Camera -----------------------------------------------------------
	 -    This method centers the camera on the point (x,y). 
	 -------------------------------------------------------------------------*/
	public void centerCamera(float x, float y) {
		Vector2D cam_pos = new Vector2D(x, y);
		
		camera.position.set(x,y);
		camera.setViewPortAndMatrices();
	}
	
	/* Draw Project ------------------------------------------------------------
	 -    This method draws the provided project to the screen.
	 -------------------------------------------------------------------------*/
	public void draw(Project p) {
		GL10 gl = glgraphics.getGL();
		
		camera.setViewPortAndMatrices();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		//Log.v("Project_Screen", "Screen Draw Project");
		//drawGrid(gl);
		p.draw(gl);
		
		//vertices.draw(GL10.GL_TRIANGLES, 0, 18);
	}
	
	/* Draw Grid ---------------------------------------------------------------
	 -   This method draws a grid on the screen to help with object placement. 
	 -------------------------------------------------------------------------*/
	private void drawGrid(GL10 gl) {
		float zoom = camera.getZoom();
		int x_lines = (int) (glgraphics.getWidth() / zoom);
		int y_lines = (int) (glgraphics.getHeight() / zoom);
		int vert_size = x_lines + y_lines * 8; // 2 X 4 value points per line
		float [] verts = new float[vert_size];
		
		gl.glColor4f(0.0f, 0.0f, 0.5f, 1.0f);
		
		for ( int i = 0; i < x_lines; i++) {
			int k = i * 4;
			verts[i] = i;
		}
		
		for ( int j = 0; j < y_lines; j++) {
			
		}
	}
}
