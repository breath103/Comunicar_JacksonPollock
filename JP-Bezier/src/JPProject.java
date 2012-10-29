import processing.core.*; 
import processing.xml.*; 
import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.Toolkit;
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

class CGSize{
	public double width;
	public double height;
	public CGSize(double w,double h){
		this.width  = w;
		this.height = h;
	}
	public CGSize(JSONObject json){
		this(json.getDouble("width"),json.getDouble("height"));
	}
}

class PaintInfo {
	private int color;
	public List<Vector> points = new ArrayList<Vector>();	
	public CGSize screenSize;
	public ActionPainting renderer;
	
	public PaintInfo(JSONObject json,int color){
		this.color= color;
		screenSize = new CGSize(json.getJSONObject("screen"));
	}
	
	public int getColor() { return color;}
}


public class JPProject extends PApplet implements TCPClientDelegate{
	private TCPClient client;
	private float powFactor = 0.8f;
	
	private HashMap<String,PaintInfo> paintMap = new HashMap<String,PaintInfo>();
	PImage cloudImage;
	
	int backgroundColor = color(255,255,255,255);
	ColorEntry randomColorEntry = null;
	
	PrintWriter fileWriter;
	public ColorEntry test_AnalyzeImage(){
		cloudImage = loadImage("4.png");
		HashMap<Integer,Integer> colorset = new HashMap();
		for(int x =0;x<cloudImage.width;x++){
			for(int y=0;y<cloudImage.height;y++){
				Integer color = new Integer(cloudImage.get(x, y));
				Integer count = colorset.get(color);
				if(count !=null ){
					count++;
				}
				else{
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
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scrnsize = toolkit.getScreenSize();
		//size(scrnsize.width,scrnsize.height); 
		this.size(1000,1000);
		this.background(backgroundColor);
		smooth();
		try {
			client = new TCPClient("64.23.73.155",7777,this);
			System.out.println(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(cloudImage);
		randomColorEntry = this.test_AnalyzeImage();
		
		try {
			fileWriter = new PrintWriter("log"+System.currentTimeMillis(), "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	}
	
	public void drawRandomCloud(float cx,float cy,float radius){
	
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
				float prevVelocity = 0.0f;
				float prevAcceleration = 0.0f;
				
				for(int i=1;i<paintInfo.points.size();i++)
				{
					Vector cp = paintInfo.points.get(i);
					Vector pp = paintInfo.points.get(i-1);
					float currentVelocity = cp.subtractVector(pp).getVelocity();
					float acceleration = currentVelocity-prevVelocity;
					
					PVector scaledPos = new PVector(cp.x*sx, cp.y*sy);
					
					
					if(paintInfo.renderer == null){
						paintInfo.renderer = new ActionPainting(this,new PVector(cp.x*sx + this.random(-0.5f, 0.5f)*this.screenWidth , 
																				 cp.y*sy + this.random(-0.5f, 0.5f)*this.screenHeight));
					}
					paintInfo.renderer.draw(scaledPos, paintInfo.getColor());
				}
				paintInfo.points.clear();
			}
		}
	}
	public void draw(){ 
		for(PaintInfo paintInfo : paintMap.values()){
			synchronized(paintInfo){
				this.drawPaintInfo(paintInfo);
			}
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
	
	public void stop(){
		super.stop();
		fileWriter.close();
	}
	
	public int toRGB(ColorSample color){
		return color(color.getR(),color.getG(),color.getB());
	}
	
	static int cnt = 0;
	public void onReceiveMessage(String strMsg) {
		for(String data : strMsg.split("\r\n")){
			try{
				JSONObject message = JSONObject.fromObject(data);
				
				fileWriter.println(data);
				
				JSONObject dataJson = message.getJSONObject("data");
				String messageType = message.getString("type");
				
				if( messageType.compareTo("paintStart") == 0){
					PaintInfo newPaint = new PaintInfo(dataJson,toRGB(randomColorEntry.getRandomColor()));
				
					paintMap.put(dataJson.getString("id"), newPaint);
					
					System.out.println("current user : "+paintMap.size());
				}
				else if( messageType.compareTo("deviceMotion") == 0){
					PaintInfo paintInfo = paintMap.get(dataJson.getString("id"));
					synchronized(paintInfo){
						try{
							paintInfo.points.add(new Vector(dataJson) );
						}catch(Exception e){
							e.printStackTrace();
						}	
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
