import processing.core.PApplet;
import processing.core.*;


public class ActionPainting {
	private PApplet app;
	
	PVector vStart,vMid,vEnd;
	boolean parity;
	float size;
	float new_size_influence, mid_point_push, max_line_width;
	public ActionPainting(PApplet app,PVector vStartPos){
		this.app = app;
		this.reset(vStartPos);
	}
	public void reset(PVector vStartPos){
		/*
		vStart = new PVector(app.width/2,app.height/2);
		vMid   = new PVector(app.width/2,app.height/2);
		vEnd   = new PVector(app.width/2,app.height/2);
		*/
		vStart = new PVector(vStartPos.x,vStartPos.y);
		vMid   = new PVector(vStartPos.x,vStartPos.y);
		vEnd   = new PVector(vStartPos.x,vStartPos.y);
		
		parity = false;
		size = 0;
		
		new_size_influence = 0.5f;
		mid_point_push = 0.5f;
		max_line_width = 200.0f;
	}
	public void draw(PVector vNew,int color){
		vMid = PVector.add(PVector.mult(PVector.sub(vEnd, vStart),(1 + mid_point_push)),vStart);
		vStart = vEnd;
		vEnd = vNew;
		
		float distance = Math.max(vStart.dist(vEnd),1);
		float new_size = max_line_width / distance * 1.3f;
		size = (new_size_influence * new_size) + ((1 - new_size_influence) * size);
		splat(vStart,vEnd,vMid,size,color);
		parity = false;
	}
	public void splat(PVector v1,PVector v2,PVector v3,float d,int color){
		app.stroke(color,app.random(200,255)); //투명도를 랜덤으로 준다
		app.strokeWeight(d);
		app.noFill();
		
		app.bezier(v1.x,v1.y,
				   v3.x,v3.y,
				   v3.x,v3.y,
				   v2.x,v2.y);
		
		float dd = v1.dist(v2);
		for (int i = 0; i < Math.floor(20*Math.pow(Math.random(), 4)); i++) {	
			// positioning of splotch varies between ±4dd, tending towards 0
			float splat_range = 1;
			PVector v4 = new PVector((float)( dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2)) ),
									 (float)( dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2)) ) );
			PVector v5 = new PVector((float)( Math.random() - 0.5 ),
									 (float)( Math.random() - 0.5 ));
			float d_ = (float) (d*(0.5f+Math.random()));
			
			app.stroke(color,app.random(200,255));
			app.strokeWeight(d_);
			app.noFill();
		
			PVector vLineStart = PVector.add(v1,v4);
			PVector vLineEnd   = PVector.add(vLineStart, v5);
			
			app.line(vLineStart.x, vLineStart.y, vLineEnd.x, vLineEnd.y);
		}
	}
} // END CLASS Actionpainting