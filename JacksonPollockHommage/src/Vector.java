import net.sf.json.JSONObject;

class Vector{
	public float x;
	public float y;
	public long time;
	public Vector(float x,float y){
		this.x = x;
		this.y = y;
	}
	public Vector(float x,float y,long time){
		this.x = x;
		this.y = y;
		this.time = time;
	}
	public Vector(JSONObject json){
		this.x 	  = (float)json.getDouble("x");
		this.y 	  = (float)json.getDouble("y");
		this.time = json.getLong("time");
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
	public Vector(double x, double y) {
		// TODO Auto-generated constructor stub
		this.x = (float)x;
		this.y = (float)y ;
	}
	float length()
	{
		return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	void subtract(Vector v2)
	{
		x -= v2.x;
		y -= v2.y;
		time -= v2.time;
	}
	void add(Vector v2){
		x += v2.x;
		y += v2.y;
	}
	void Multiply(float f){
		this.x *= f;
		this.y *= f;
	}
	
	
	float getVelocity(){
		return this.length()/this.time;
	}
	
	float timeTo(Vector v2){
		return v2.time - this.time;
	}
	
	Vector subtractVector(Vector v)
	{
		return new Vector(this.x - v.x,this.y - v.y,this.time-v.time);
	}
	Vector addVector(Vector v){
		return new Vector(this.x + v.x,this.y + v.y);
	}
	Vector multiplyVector(float f){
		return new Vector(this.x*f,this.y*f);
	}
	Vector normalizedVector(){
		return this.multiplyVector(1/this.length());
	}
	
	void Normalize(){
		this.Multiply(1/this.length());
	}
	
	public static Vector VectorWithAngle(double angle){
		return new Vector(Math.cos(angle),Math.sin(angle));
	}
}