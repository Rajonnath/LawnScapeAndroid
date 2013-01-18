/*FILENAME- Vector2D.java
 * DESCRIPTION - Used for calculations stores vector positions of all object and used while loading file back. 
 */
//package of class
package sprinklerheads.irrigation.cadsystem;
//class Vector2D
public class Vector2D {
	public static float TO_RADIANS = (1 / 180f) * (float)Math.PI;
	public static float TO_DEGREES = (1 / (float)Math.PI)* 180;
	public float x,y;
	
	/* Vector2D Constructor -------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D() {
	}
	
	/* Vector2D Default Constructor -------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D(float ix, float iy) {
		x = ix;
		y = iy;
	}
	
	/* Vector2D Default Constructor -------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D(Vector2D vec) {
		x = vec.x;
		y = vec.y;
	}
	
	/* Copy a 2D Vector --------------------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D copy() {
		return new Vector2D(x,y);
	}
	
	/* Set 2D Vector -----------------------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D set(float nx, float ny) {
		x = nx;
		y = ny;
		return this;
	}
	/* Set 2D Vector -----------------------------------------------------------
	 - 
	 -------------------------------------------------------------------------*/
	public Vector2D set(Vector2D vec) {
		x = vec.x;
		y = vec.y;
		return this;
	}
	/* Add ---------------------------------------------------------------------
	 -    This method adds the provided values to the vector.
	 -------------------------------------------------------------------------*/
	public Vector2D add(float ax, float ay) {
		x += ax;
		y += ay;
		return this;
	}
	
	/* Add ---------------------------------------------------------------------
	 -    This method adds the vector to this vector.
	 -------------------------------------------------------------------------*/
	public Vector2D add(Vector2D vec) {
		x += vec.x;
		y += vec.y;
		return this;
	}
	
	/* Subtract ----------------------------------------------------------------
	 -    This method subtracts the provided values from the vector.
	 -------------------------------------------------------------------------*/
	public Vector2D sub(float ax, float ay) {
		x -= ax;
		y -= ay;
		return this;
	}
	
	/* Subtract ----------------------------------------------------------------
	 -    This method subtracts the provided vector from the vector.
	 -------------------------------------------------------------------------*/
	public Vector2D sub(Vector2D vec) {
		x -= vec.x;
		y -= vec.y;
		return this;
	}
	
	/* Multiply ----------------------------------------------------------------
	 -    This method multiplies the vector by a scalar.
	 -------------------------------------------------------------------------*/
	public Vector2D multiply(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}
	
	/* Length ----------------------------------------------------------------
	 -    
	 -------------------------------------------------------------------------*/
	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	/* Normalize ----------------------------------------------------------------
	 -    
	 -------------------------------------------------------------------------*/
	public Vector2D normalize() {
		float len = len();
		if(len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}
	
	/* Angle ----------------------------------------------------------------
	 -    This method calculates the angle from zero of the vector.
	 -------------------------------------------------------------------------*/
	public float angle() {
		float angle = (float)Math.atan2(y, x) * TO_DEGREES;
		if(angle < 0) {
			angle += 360;
		}
		return angle;
	}
	
	/* Rotate ----------------------------------------------------------------
	 -    This method rotates the vector by 'angle' degrees.
	 -------------------------------------------------------------------------*/
	public Vector2D rotate(float angle) {
		float rad = angle * TO_RADIANS;
		float cos = (float) Math.cos(rad);
		float sin = (float) Math.sin(rad);
		float nx = x * cos - y * sin;
		float ny = x * sin + y * cos;
		
		x = nx;
		y = ny;
		
		return this;
	}
	
	/* Distance ----------------------------------------------------------------
	 -    This method Calculates the distance from the provided vector.
	 -------------------------------------------------------------------------*/
	public float distance(Vector2D vec) {
		float dx = x - vec.x;
		float dy = y - vec.y;
		
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	/* Distance ----------------------------------------------------------------
	 -    This method calculates the distance from the provided point.
	 -------------------------------------------------------------------------*/
	public float distance(float x2, float y2) {
		float dx = x - x2;
		float dy = y - y2;
		
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
}