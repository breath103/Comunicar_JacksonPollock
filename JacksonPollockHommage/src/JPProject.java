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
import java.util.zip.*; 
import java.util.regex.*; 

import java.util.List;

import java.net.*;

import net.sf.json.JSONObject;


class ColorSample{
	int r;
	int g;
	int b;
	float weight;
}
class ColorEntry{
	List<ColorSample> colors;
	ColorEntry() {
		colors = new ArrayList<ColorSample>();
	}
	void addColor(ColorSample color){
		colors.add(color);
	}
	ColorSample getRandomColor() {
		return colors.get((int)Math.floor(Math.random() * colors.size()));
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
	
	
	public void setup(){ 
		size(1000,1000); 
		this.background(0, 0, 0 , 255);
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

		synchronized(paintInfo.points){
			
		float lineInertiaFactor = 40.0f;
		float minlineWeight = 0.0f;
		float maxlineWeight = 40.0f;
		
		if(size>=4)
		{
			for(int i=4;i<=size;i+=4){
				Vector prevPos 	  = paintInfo.points.get(i-2);
				Vector currentPos = paintInfo.points.get(i-1);
				Vector vDelta = currentPos.subtractVector(prevPos);
				
				
				float currentVelocity = vDelta.getVelocity();
				
				if(paintInfo.previousVelocity != null){
				//	currentVelocity = (currentVelocity + paintInfo.previousVelocity.getVelocity()) / 2.0f; 
				}
				
				
				float h = 1;
				float k = 25;
				
				float newLineWeight = //(float)Math.min(Math.max(minlineWeight, 5 / Math.pow(currentVelocity,powFactor)) + this.random( - 2 , 2),maxlineWeight);
						(float)Math.min(Math.max(minlineWeight, 2*(float)this.sqrt(k*vDelta.time/vDelta.length() / (PI*h))) ,maxlineWeight);
				float sx = (float) (width  / paintInfo.screenSize.width);
				float sy = (float) (height / paintInfo.screenSize.height);
			
				strokeWeight( (newLineWeight + paintInfo.previousLineWeight) / 2.0f );
				
				
				noFill();
				stroke(paintInfo.currentColor);
				//이전 선보다 갑자기 1.5배이상 커진 경우, 
				if(paintInfo.previousLineWeight < newLineWeight * 1.5f){ 
					//	stroke(255,0,0);
					//	newLineWeight = (paintInfo.previousLineWeight +  newLineWeight) / 2.0f;
				}
				
				
				
				try{
					this.bezier(
							paintInfo.points.get(i-4).x * sx,paintInfo.points.get(i-4).y * sy,
							paintInfo.points.get(i-3).x * sx +pointNoiseWithVelocity(currentVelocity) + 
								paintInfo.currentVelocity.x, paintInfo.points.get(i-3).y * sy +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.y,
							paintInfo.points.get(i-2).x * sx +pointNoiseWithVelocity(currentVelocity) + 
								paintInfo.currentVelocity.x, paintInfo.points.get(i-2).y * sy  +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.y,
							paintInfo.points.get(i-1).x * sx, paintInfo.points.get(i-1).y * sy
							);
					/*
					beginShape();
					curveVertex(paintInfo.points.get(i-4).x * sx,paintInfo.points.get(i-4).y * sy); // the first control point
					curveVertex(paintInfo.points.get(i-3).x * sx +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.x,
								paintInfo.points.get(i-3).y * sy +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.y); // is also the start point of curve
					curveVertex(paintInfo.points.get(i-2).x * sx +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.x, 
								paintInfo.points.get(i-2).y * sy  +pointNoiseWithVelocity(currentVelocity) + paintInfo.currentVelocity.y); // the last point of curve
					curveVertex(paintInfo.points.get(i-1).x * sx, paintInfo.points.get(i-1).y * sy); // is also the last control point
					endShape();
					*/
					
				}catch(RuntimeException e){
					e.printStackTrace();
				}
				
				
				
				if(Math.random() > 0.4){
					float fDist = currentVelocity * random(0.5f, 1.0f) * 20;
					Vector vDeltaToCircle = Vector.VectorWithAngle(Math.random() * Math.PI * 2).multiplyVector(fDist);
					Vector randomPos = paintInfo.points.get(i-1).addVector(vDeltaToCircle);
					fill(paintInfo.currentColor);
					noStroke();
					
					float factor = 3;
					float circleRadius = factor * currentVelocity * random(0.5f, 1.0f);
			
					this.ellipseMode(CENTER);
					this.ellipse(randomPos.x*sx, randomPos.y*sy,
								 factor * sx,factor * sy);
			}
				
				
				paintInfo.currentVelocity  = vDelta.normalizedVector().multiplyVector(lineInertiaFactor);
				paintInfo.previousVelocity = vDelta;
				
				paintInfo.previousLineWeight = newLineWeight;
			}
			paintInfo.points = paintInfo.points.subList(size - 1,size);
		}
		}
	}
	public void draw(){ 
	/*
		for(PaintInfo paintInfo : paintMap.values()){
			this.drawPaintInfo(paintInfo);
		}
		*/
	} 
	
	
	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "JPProject" });
		
	}
	
	
	
	public void mouseMoved(){
		
	}
	
	@Override 
	public void mouseClicked(){
		this.background(0);
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
					int colorEntry[][] = {
							{255,255,255},
							{0,0,0},
							{255,235,0},
							{1,0,255},
							{0,130,153},
							{250,224,212},
							{0x33,0xCC,0xCC},
							{152,0,0},
							{0x99,0xCC,0xFF}
					};
					
					int colorIndex = (int) Math.floor(this.random(colorEntry.length));//Math.random() * colorEntry.length);
					System.out.println("index : " + colorIndex);
					newPaint.currentColor = color(
							colorEntry[colorIndex][0],
							colorEntry[colorIndex][1],
							colorEntry[colorIndex][2]);//color(random(255),random(255),random(255));
					paintMap.put(dataJson.getString("id"), newPaint);
					System.out.println("current user : "+paintMap.size());
				}
				else if( messageType.compareTo("deviceMotion") == 0){
					PaintInfo paintInfo = paintMap.get(dataJson.getString("id"));
					try{
						paintInfo.points.add(new Vector(dataJson) );
						this.drawPaintInfo(paintInfo);
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
