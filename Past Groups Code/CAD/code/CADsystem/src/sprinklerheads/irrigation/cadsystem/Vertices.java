/*FILENAME- Vertices.java
 * DESCRIPTION -Vertices class is used for drawing object using open GL graphics
 */
//package of class
package sprinklerheads.irrigation.cadsystem;

//importing required packages
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Vertices {
	final GLGraphics gl_graphics;
	final boolean has_color;
	final boolean has_tex_coords;
	final int vertex_size;
	final FloatBuffer vertices;
	final ShortBuffer indices;
	
	/* Vertices Constructor ----------------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vertices(GLGraphics glGraphics, int maxVertices, int maxIndices, 
			        boolean hasColor, boolean hasCoords) {
		gl_graphics    = glGraphics;
		has_color      = hasColor;
		has_tex_coords = hasCoords;
		vertex_size    = (4 + (has_color?4:0) + (has_tex_coords?2:0)) * 4;
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertex_size * Float.SIZE);
		buffer.order(ByteOrder.nativeOrder());
		vertices = buffer.asFloatBuffer();
		
		if ( maxIndices > 0) {
			buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
			buffer.order(ByteOrder.nativeOrder());
			indices = buffer.asShortBuffer();
		} else {
			indices = null;
		}
	}
	
	/* Set Vertices ------------------------------------------------------------
	 -    Set the vertices to draw.
	 -------------------------------------------------------------------------*/
	public void setVertices(float[] v, int offset, int length) {
		vertices.clear();
		vertices.put(v, offset, length);
		vertices.flip();
	}
	
	/* Set Indices -------------------------------------------------------------
	 -    If the vertices are indexed set the order the vertices will be 
	 -    rendered.
	 -------------------------------------------------------------------------*/
	public void setIndices(short[] ndxs, int offset, int length) {
		indices.clear();
		indices.put(ndxs, offset, length);
		indices.flip();
	}
	
	/* Draw --------------------------------------------------------------------
	 -    Draw the 'num_vertices' starting at 'offset' using the OpenGL 
	 -    primitive 'primitive_type'.
	 -------------------------------------------------------------------------*/
	public void draw(int primitive_type, int offset, int num_vertices) {
		GL10 gl = gl_graphics.getGL();
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		vertices.position(0);
		gl.glVertexPointer(2,  GL10.GL_FLOAT, vertex_size, vertices);
		
		if(has_color) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(2);
			gl.glColorPointer(4,  GL10.GL_FLOAT, vertex_size, vertices);
		}
		
		if(has_tex_coords) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(has_color?6:2);
			gl.glColorPointer(2,  GL10.GL_FLOAT, vertex_size, vertices);
		}
		
		if ( indices != null) {
			indices.position(offset);
			gl.glDrawElements(primitive_type, num_vertices, GL10.GL_UNSIGNED_SHORT, indices);
		} else {
			gl.glDrawArrays(primitive_type, offset, num_vertices);
		}
		
		if(has_tex_coords) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		if(has_color) {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
}
