/*FILENAME- Project.java
 * DESCRIPTION -Project class get measurements of objects from different classes and performs required actions.
 */
//package of class
package sprinklerheads.irrigation.cadsystem;
//importing required packages
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

//Project class uses GLGraphics
public class Project {
	
	GLGraphics glgraphics;
	
	//Strings to store details
	public String contact_first;
	public String contact_last;
	public String Phone;
	public String email;
	public String city;
	public String state;
	public String zip_code;
	
	// Project Objects
	Site property;
	ArrayList<Building> buildings;
	ArrayList<Slab> concrete;
	ArrayList<FlowerBed> flower_beds;
	ArrayList<Plant> plants;
	
	/* Project Constructor -----------------------------------------------------
	 -    
	 -------------------------------------------------------------------------*/
	public Project(GLGraphics g) {
		glgraphics = g;
		property = new Site();
		buildings = new ArrayList<Building>();
		concrete = new ArrayList<Slab>();
		flower_beds = new ArrayList<FlowerBed>();
		plants = new ArrayList<Plant>();
		
		Log.v("Project", "Created New Project");
	}
	
	/* Contact Class ***********************************************************
	 -    This method subtracts the provided values from the vector.
	 **************************************************************************/
	public class Contact {
		public String first_name;
		public String last_name;
		public String title;
		public String email;
		public String phone_number;
		public String address;
		public String city;
		public String state;
		public String zip_code;
		
		public Contact () {
			
		}
		
	}

	/* Site ********************************************************************
	 *    - Used to define the property boundaries 
	 **************************************************************************/
	public class Site {		
		Vertices vertices;
		int num_points;
		float line_width;
		
		public float water_meter_x;
		public float water_meter_y;
		public int water_main_psi;
		public float water_main_size;
		public String Name;
		
		/* Default Site Constructor --------------------------------------------
		 | 
		 +--------------------------------------------------------------------*/
		public Site() {
			line_width = 4;
		}
		
		/* Draw Site -----------------------------------------------------------
		 |   - This method renders the site boundaries. 
		 +--------------------------------------------------------------------*/
		public void draw() {
			GL10 gl = glgraphics.getGL();
			
			gl.glLineWidth(line_width);
			gl.glColor4f(0.0f, 0.0f, 0.0f,1.0f);
			
			vertices.draw(GL10.GL_LINE_LOOP,0, num_points);
		}
	}
	
	/* Building ****************************************************************
	 *    Used to describe a building: House, Shed, Pool house, etc
	 **************************************************************************/
	public class Building {
		Vertices vertices;
		int ndx_cnt;
		public String name;
		
		/* Default Building Constructor ---------------------------------------
		 - 
		 +--------------------------------------------------------------------*/
		public Building() {
			
		}
		
		/* Building.setVertices ------------------------------------------------
		 -  This method determines the vertices for the triangles from the  
		 -	corners of the rectangles  provided.
		 +--------------------------------------------------------------------*/
		public void setVertices(Vertices v, int points) {
			vertices = v;
			// 4 points per quad 6 points per pair of triangles
			ndx_cnt = points / 4 * 6;
		}
		
		/* Draw Building -------------------------------------------------------
		 -   This method renders the building. 
    	 +--------------------------------------------------------------------*/
		public void draw() {
			GL10 gl = glgraphics.getGL();
			
			gl.glColor4f(0.0f, 0.0f, 0.0f,1.0f);
			vertices.draw(GL10.GL_TRIANGLES, 0, ndx_cnt);
		}
	}
	
	/* Slab ******************************************************************** 
	 *    Used to describe concrete slabs, such as: Sidewalks, Driveways, 
	 *    and patios.    
	 ***************************************************************************/
	public class Slab {
		Vertices vertices;
		int ndx_cnt;
		
		public float width;
		public float length;
		public int attached;
		
		/* Default Slab Constructor ---------------------------------------
		 - 
		 +*-------------------------------------------------------------------*/
		public Slab() {
			
		}
		
		/* Slab.setVertices ----------------------------------------------------
		 -  This method determines the vertices for the triangles from the  
		 -	corners of the concrete provided.
		 +--------------------------------------------------------------------*/
		public void setVertices(Vertices v, int points) {
			vertices = v;
			// 4 points per quad 6 points per pair of triangles
			ndx_cnt = points / 4 * 6;
		}
		
		/* Slab.draw ----------------------------------------------------------
		 -   - This method renders the building. 
   	 	+*-------------------------------------------------------------------*/
		public void draw() {
			GL10 gl = glgraphics.getGL();
			
			gl.glColor4f(0.5f, 0.5f, 0.5f,1.0f);
			vertices.draw(GL10.GL_TRIANGLES, 0, ndx_cnt);
		}
	}
	
	/* Flower Bed **************************************************************
	 *    Used to describe flower beds
	 **************************************************************************/
	public class FlowerBed {
		Vertices vertices;
		int ndx_cnt;
		public Boolean drip;
		public int attached;
		
		/* Default Flower_Bed Constructor --------------------------------------
		 - 
		 +*-------------------------------------------------------------------*/
		public FlowerBed() {
			
		}
		
		/* Flower_Bed.setVertices ------------------------------------------------
		 -  This method determines the vertices for the triangles from the  
		 -	corners of the flower bed provided.
		 +--------------------------------------------------------------------*/
		public void setVertices(Vertices v, int points) {
			vertices = v;
			// 4 points per quad 6 points per pair of triangles
			ndx_cnt = points / 4 * 6;
		}
		
		/* Flower_Bed.draw ----------------------------------------------------
		 -   This method renders the flower bed. 
  	 	+*-------------------------------------------------------------------*/
		public void draw() {
			Log.v("FlowerBed", "Drawing Flower Bed");
			GL10 gl = glgraphics.getGL();
			
			gl.glColor4f(0.486275f, 0.988235f, 0.0f,1.0f);
			vertices.draw(GL10.GL_TRIANGLES, 0, ndx_cnt);
		}
	}
	
	/* Plant 
	 *    - Used to describe Trees, Shrubs, Perennials, and other plants. 
	 */
	public class Plant {
		public Boolean drip;
		public float size;
		public String type;
		public int container;
		public float x;
		public float y;
	}
	
	/* Pipe
	 *    - Used to describe a pipe
	 */
	public class Pipe {
		Vertices vertices;
		
		public int type;
		public int zone;
		public float size;
	}
	
	/* Sprinkler
	 *    - Used to describe a Sprinkler
	 */
	public class Sprinkler {
		public float x;
		public float y;
		public float orientation;
		public float spray;
		public float distance;
		public int zone;
	}
	
	/* Plumbing_Object
	 *    - Used to describe a pipe
	 */
	public class Plumbing_Object {
		Vertices vertices;
		
		public float x;
		public float y;
		public String Type;
	}
	
	/* draw Method -------------------------------------------------------------
	 *    This method draws the project
	 *-------------------------------------------------------------------------*/	
	public void draw(GL10 gl) {
		property.draw();
		
		for (Building bldg : buildings) {
			bldg.draw();
		}
		
		for (Slab slab : concrete) {
			slab.draw();
		}
		
		for (FlowerBed fb : flower_beds) {
			fb.draw();
		}
	}
	
	/* Set Site Method ---------------------------------------------------------
	 *    This method sets the corner points that define the property
	 *    boundaries.
	 *-------------------------------------------------------------------------*/	
	public void setSite(float[] corners, int points) {
		float[] verts = new float[points * 4];
		
		property.vertices = new Vertices(glgraphics, points, 0, false, false);
		
		for (int i = 0; i < points; i++) {
			int j = i * 4;
			int k = i *2;
			verts[j]   = corners[k];     // X
			verts[j+1] = corners[k+1];   // Y
			verts[j+2] = 0.0f;           // Z
			verts[j+3] = 1.0f;           // Alpha
			
			Log.v("Project", "Site Index : " + j + ", " + (j+1) + ", " + (j+2) + ", " + (j+3));
			Log.v("Project", "Site Vertex: " + verts[j] + ", " + verts[j+1] + ", " + verts[j+2] + ", " + verts[j+3]);
		}
		
		property.num_points = points;
		property.vertices.setVertices(verts, 0, points * 4);
	}
	
	/* Add Building ------------------------------------------------------------
	 *   This method adds a new building to the project.
	 *------------------------------------------------------------------------*/
	public void addBuilding(String name, float[] verts, int points) {
		Building bldg = new Building();
		Vertices vertices = toTriangles(verts, points);
		
		bldg.name = name;
		bldg.setVertices(vertices, points);
		
		buildings.add(bldg);
	}
	
	/* Add Slab ----------------------------------------------------------------
	 *   This method adds a new slab of concrete to the project.
	 *------------------------------------------------------------------------*/
	public void addSlab(float[] verts, int points) {
		Slab slab = new Slab();
		Vertices vertices = toTriangles(verts, points);
		
		slab.setVertices(vertices, points);
		
		concrete.add(slab);
	}
	
	/* Add Flower Bed ----------------------------------------------------------
	 *   This method adds a new flower bed to the project.
	 *------------------------------------------------------------------------*/
	public void addFlowerBed(float[] verts, int points) {
		Log.v("Project.addFlowerBed", "Adding Flower Bed");
		FlowerBed flowerbed = new FlowerBed();
		Vertices vertices = toTriangles(verts, points);
		
		flowerbed.setVertices(vertices, points);
		
		flower_beds.add(flowerbed);
		Log.v("Project.addFlowerBed", "Done");
	}
	
	/* load Method -------------------------------------------------------------
	 *    This method loads the project from the database
	 *-------------------------------------------------------------------------*/
	public void load(String project_file) {
		
	}
	
	/* create Method -----------------------------------------------------------
	 *    This method creates an empty project
	 *------------------------------------------------------------------------*/
	public void create(String project_file) {
		
	}
	
	/* save Method -------------------------------------------------------------
	 *    This method saves the project to a database file
	 *-------------------------------------------------------------------------*/
	public void save(String project_file) {
		
	}
	
	/* setGLGraphics -------------------------------------------------------
	 |    This method sets the glGraphics object for rendering. 
	 +--------------------------------------------------------------------*/
	public void setGLGraphics(GLGraphics glg) {
		glgraphics = glg;
	}
	
	/* toTriangles -------------------------------------------------------------
	 *    This method converts a set of quads defined by sets of four x,y 
	 *    coordinates into pairs of triangles read to be drawn by OpenGL.
	 *    The resulting Vertices object should be ready to be rendered.
	 *-------------------------------------------------------------------------*/
	public Vertices toTriangles(float[] corners, int points) {
		Vertices v;
		int quad_cnt = points / 4;
		int ndx_cnt  = quad_cnt * 6;
		
		// For each quad there will be four points
		// and six indices.
		float[] verts = new float[points * 4];
		short[] indices = new short[quad_cnt * 6];
		
		for ( int i = 0; i < points; i++) {
			int j = i * 4;
			int k = i * 2;
			verts[j]   = corners[k];	// X
			verts[j+1] = corners[k+1];	// Y
			verts[j+2] = 0.0f;			// Z
			verts[j+3] = 1.0f;			// Alpha
			
			Log.v("Building", "Vertex " + (i) + " : " + verts[j] + ", " + verts[j+1] + ", " + verts[j+2] + ", " + verts[j+3]);
		}
		
		for ( int i = 0; i < quad_cnt; i++) {
			int j = i * 6;
			short k = (short) (i * 4);
			
			indices[j]   = k;					// Corner 0
			indices[j+1] = (short) (k + 1);		// Corner 1
			indices[j+2] = (short) (k + 2);		// Corner 2
			indices[j+3] = indices[j+2];		// Corner 2
			indices[j+4] = (short) (k + 3);		// Corner 3
			indices[j+5] = indices[j]; 			// Corner 0
			
			Log.v("Building", "Quad " + i + " : " + indices[k] + ", " + indices[k+1] + ", " + indices[k+2] + ", " + indices[k+3] + ", " + indices[k+4] + ", " + indices[k+5]);
		}
		
		for( int i = 0; i < indices.length; i++) {
			Log.v("Building", "Index " + i + ": " + indices[i]);
		}
		
		Log.v("Building", "Vertices : " + points + " : Indices " + ndx_cnt);
		
		v = new Vertices(glgraphics, points, ndx_cnt, false, false);
		v.setVertices(verts, 0, points * 4);
		v.setIndices(indices, 0, ndx_cnt);
		
		return v;
	}
}
