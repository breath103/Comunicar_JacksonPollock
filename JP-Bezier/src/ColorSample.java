import processing.core.PApplet;
public class ColorSample{
	private int r;
	private int g;
	private int b;
	private float weight; // 색 가중치
	public ColorSample(int r, int g, int b, float weight) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.weight = weight;
	}
	public ColorSample(int color,float weight){
		this.r = (color >> 16) & 0xff;     //bitwise shifting
        this.g = (color >> 8) & 0xff;
        this.b = color & 0xff;
		this.weight = weight;
	}
	public int getR() { return r;}
	public void setR(short r) { this.r = r; }
	public int getG() { return g; }
	public void setG(short g) {this.g = g;}
	public int getB() {return b;}
	public void setB(short b) {this.b = b;}
	public float getWeight() {return weight;}
	public void setWeight(float weight) {this.weight = weight;}
	
}