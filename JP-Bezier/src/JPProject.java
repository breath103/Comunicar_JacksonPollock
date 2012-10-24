import processing.core.*; 

import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.Map.Entry;
import java.util.zip.*; 
import java.util.regex.*; 

import java.util.List;

import java.net.*;

import com.sun.tools.javac.util.Pair;

import net.sf.json.JSONObject;


class ColorSample{
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
	public int getR() {
		return r;
	}
	public void setR(short r) {
		this.r = r;
	}
	public int getG() {
		return g;
	}
	public void setG(short g) {
		this.g = g;
	}
	public int getB() {
		return b;
	}
	public void setB(short b) {
		this.b = b;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
}
class ColorEntry{
	List<ColorSample> colors;
	float totalWeight;
	ColorEntry() {
		colors = new ArrayList<ColorSample>();
	}
	void addColor(int i,int j,int k,float weight){
		colors.add(new ColorSample(i,j,k,weight));
	}
	void addColor(ColorSample color){
		colors.add(color);
	}
	
	ColorSample getRandomColor() {
		float totalWeight = 0.0f;
		for(ColorSample color : colors)
		{
			totalWeight += color.getWeight();
		}
		
		double randomValue = Math.random() * totalWeight;
		
		float currentWeight = 0.0f;
		for(ColorSample color : colors)
		{
			currentWeight += color.getWeight();
			if(currentWeight >= randomValue)
			{
				return color;
			}
		}
		
		System.out.println("ERROR!!");
		return null;
		//return colors.get((int)Math.floor(Math.random() * colors.size()));
	}
	
	private void rgb2hsv(int r, int g, int b, int hsv[]) {
		
		int min;    //Min. value of RGB
		int max;    //Max. value of RGB
		int delMax; //Delta RGB value
		
		if (r > g) { min = g; max = r; }
		else { min = r; max = g; }
		if (b > max) max = b;
		if (b < min) min = b;
								
		delMax = max - min;
	 
		float H = 0, S;
		float V = max;
		   
		if ( delMax == 0 ) { H = 0; S = 0; }
		else {                                   
			S = delMax/255f;
			if ( r == max ) 
				H = (      (g - b)/(float)delMax)*60;
			else if ( g == max ) 
				H = ( 2 +  (b - r)/(float)delMax)*60;
			else if ( b == max ) 
				H = ( 4 +  (r - g)/(float)delMax)*60;   
		}
								 
		hsv[0] = (int)(H);
		hsv[1] = (int)(S*100);
		hsv[2] = (int)(V*100);
	}
}



class CGSize{
	public double width;
	public double height;
	public CGSize(double w,double h){
		this.width = w;
		this.height = h;
	}
	public CGSize(JSONObject json){
		this(json.getDouble("width"),json.getDouble("height"));
	}
}


class PaintInfo {
	public int currentColor;
	public List<Vector> points = new ArrayList<Vector>();	
	public CGSize screenSize;
	public Vector currentVelocity = new Vector(0,0,0);
	public Vector previousVelocity = null;
	public float previousLineWeight = 50.0f;
	public PaintInfo(JSONObject json){
		screenSize = new CGSize(json.getJSONObject("screen"));
	}
}


public class JPProject extends PApplet implements TCPClientDelegate{
	private TCPClient client;
	private float powFactor = 0.8f;
	
	private HashMap<String,PaintInfo> paintMap = new HashMap<String,PaintInfo>();
	PImage cloudImage;
	ActionPainting actionPainting = null;
	
	int backgroundColor = color(244,185,79,255);
	ColorEntry randomColorEntry = null;
	
	public ColorEntry test_AnalyzeImage(){
		cloudImage = loadImage("jackson-pollock6.jpeg");
		HashMap<Integer,Integer> colorset = new HashMap();
		for(int x =0;x<cloudImage.width;x++){
			for(int y=0;y<cloudImage.height;y++){
				Integer color = new Integer(cloudImage.get(x, y));
				Integer count = colorset.get(color);
				if(count !=null )
				{
					count++;
				}
				else
				{
					colorset.put(color,new Integer(1));
				}
			}
		}
		
		ColorEntry colorEntry = new ColorEntry();
		for(Entry<Integer,Integer> e : colorset.entrySet()){
			colorEntry.addColor(new ColorSample(e.getKey(),e.getValue()));
		}
		return colorEntry;
	}
	
	public void setup(){ 
		size(1000,1000); 
		this.background(backgroundColor);
		smooth();
		try {
			client = new TCPClient("64.23.73.155",7777,this);
			System.out.println(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		cloudImage = loadImage("brush_1.png");
		System.out.println(cloudImage);
		actionPainting = new ActionPainting(this);
		randomColorEntry = this.test_AnalyzeImage();
	}
	
	
	public void keyReleased() {
		if (key == CODED) {
		    if (keyCode == UP) {
		    	powFactor += 0.05f;
		    } else if (keyCode == DOWN) {
		    	powFactor -= 0.05f;
		    } 
		} else {
		
		}
		System.out.println("factor : " + powFactor);
	}
	
	
	float pointNoiseWithVelocity(float v){
		float k = 5.0f;
		return (float)this.random((float)-Math.pow(v,1) * k,(float)Math.pow(v,1) * k);
//		return 0.0f;
	}
	
	public void drawRandomCloud(float cx,float cy,float radius){
		/*
		translate(cx,cy);
		rotate( random(2 * PI) );
		scale(radius / cloudImage.width);
		
		
		imageMode(CENTER);
		image(cloudImage,0,0);
		
		translate(0,0);
		scale(1);
		rotate(0);
		*/
	}
	
	public void DEBUG_drawPaintInfoPoints(PaintInfo paintInfo){
		for(Vector pos : paintInfo.points){
			float sx = (float) (width / paintInfo.screenSize.width);
			float sy = (float) (height / paintInfo.screenSize.height);
				
			strokeWeight( 10.0f);//(prevLineWeight + newLineWeight) / 2.0f );
			
			this.noFill();
			this.stroke(color(0));
			this.point(pos.x * sx, pos.y * sy);
		}
		
	}
	
	public void drawPaintInfo(PaintInfo paintInfo){
		int size = paintInfo.points.size();
		float sx = (float) (width  / paintInfo.screenSize.width);
		float sy = (float) (height / paintInfo.screenSize.height);

		synchronized(paintInfo.points){
			if(paintInfo.points.size() > 1)
			{
			//	System.out.println(paintInfo.points.size());
				float prevVelocity = 0.0f;
				float prevAcceleration = 0.0f;
				
				//actionPainting.reset();
				for(int i=1;i<paintInfo.points.size();i++)
				{
					Vector cp = paintInfo.points.get(i);
					Vector pp = paintInfo.points.get(i-1);
					float currentVelocity = cp.subtractVector(pp).getVelocity();
					float acceleration = currentVelocity-prevVelocity;
					
					
					PVector scaledPos = new PVector(cp.x*sx, cp.y*sy);
					actionPainting.draw(scaledPos, paintInfo.currentColor);
				}
				paintInfo.points.clear();
			}
		}
	}
	public void draw(){ 
		for(PaintInfo paintInfo : paintMap.values()){
			this.drawPaintInfo(paintInfo);
		}
	} 
	
	
	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "JPProject" });
		
	}
	
	
	
	public void mouseMoved(){
		
	}
	
	@Override 
	public void mouseClicked(){
		this.background(backgroundColor);
	}
	
	static int cnt = 0;
	public void onReceiveMessage(String strMsg) {
		for(String data : strMsg.split("\r\n")){
			try{
				JSONObject message = JSONObject.fromObject(data);
				JSONObject dataJson = message.getJSONObject("data");
				String messageType = message.getString("type");
				if( messageType.compareTo("paintStart") == 0){
					PaintInfo newPaint = new PaintInfo(dataJson);
				
					ColorSample color = randomColorEntry.getRandomColor();
					System.out.println(color.getB() + " " + color.getG() + " " +  color.getB());
					newPaint.currentColor = color(color.getR(),color.getG(),color.getB());
					paintMap.put(dataJson.getString("id"), newPaint);
					System.out.println("current user : "+paintMap.size());
				}
				else if( messageType.compareTo("deviceMotion") == 0){
					PaintInfo paintInfo = paintMap.get(dataJson.getString("id"));
					try{
						paintInfo.points.add(new Vector(dataJson) );
					}catch(Exception e){
						System.out.println("DeviceMotionParse Error  "+ dataJson);
						e.printStackTrace();
					}	
				}
				else if( messageType.compareTo("paintEnd") == 0){
					paintMap.remove(dataJson.getString("id"));
					System.out.println("current user : "+paintMap.size());
				}
				else {
					System.out.println("unhandled message : "+message);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
