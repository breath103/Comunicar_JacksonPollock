import processing.core.PApplet;
import processing.core.*;


public class ActionPainting {
	private PApplet app;
	
	PVector vStart,vMid,vEnd;
	boolean parity;
	float size;
	

	float new_size_influence, mid_point_push, max_line_width;
	public ActionPainting(PApplet app){
		this.app = app;
		this.reset();
	}
	public void reset(){
		vStart = new PVector(app.width/2,app.height/2);
		vMid = new PVector(app.width/2,app.height/2);
		vEnd = new PVector(app.width/2,app.height/2);
		parity = false;
		size = 0;
		
	//	new_size_influence = (float) ((Math.floor(Math.random() * 20) / 10) - 0.5);
	//	mid_point_push 	   = (float) ((Math.floor(Math.random() * 8) / 4) - 1);
		new_size_influence = 0.5f;
		mid_point_push = 0.5f;
		max_line_width = 200.0f;//(float) ((Math.random() * 50) + 50);
	}
	public void draw(PVector vNew,int color){
		vMid = PVector.add(PVector.mult(PVector.sub(vEnd, vStart),(1 + mid_point_push)),vStart);
		
		vStart = vEnd;
		vEnd = vNew;
		float distance = vStart.dist(vEnd);
		float new_size = max_line_width / distance * 3;
		size = (new_size_influence * new_size) + ((1 - new_size_influence) * size);
		splat(vStart,vEnd,vMid, size,color);
		parity = false;
}
	public void splat(PVector v1,PVector v2,PVector v3,float d,int color){
		
		app.stroke(color /* app.random(0.9f, 1.1f)*/ ,app.random(200,255));
		app.strokeWeight(d);
		app.noFill();
		
		app.bezier(v1.x,v1.y,
				  v3.x,v3.y,
				  v3.x,v3.y,
				  v2.x,v2.y);
		
		/*
		app.curveDetail(5);
		app.curve(v3.x,v3.y,
				  v1.x,v1.y,
				  v2.x,v2.y,
				  v3.x,v3.y);
		*/
		
		float dd = v1.dist(v2);
		for (int i = 0; i < Math.floor(25*Math.pow(Math.random(), 4)); i++) {	
			// positioning of splotch varies between ±4dd, tending towards 0
			float splat_range = 1;
			PVector v4 = new PVector((float)( dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2))),
									 (float)( dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2))));
			PVector v5 = new PVector((float)(Math.random() - 0.5),
									 (float)(Math.random() - 0.5));
			float d_ = (float) (d*(0.5+Math.random()));
			
			app.stroke(color /* app.random(0.9f, 1.1f) */,app.random(200,255));
			app.strokeWeight(d_);
			app.noFill();
		
			PVector vLineStart = PVector.add(v1,v4);
			PVector vLineEnd   = PVector.add(vLineStart, v5);
			
			app.line(vLineStart.x, vLineStart.y, vLineEnd.x, vLineEnd.y);
		}
		/*
		 	obj.lineStyle(d, 0x000000, 100);
	obj.moveTo(x1, y1);
	obj.curveTo(x3, y3, x2, y2);
	_root.curves.push([x1, y1, x3, y3, x2, y2, d]);
	obj.
	// splotch
	dd = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

 	for (var i = 0; i<Math.floor(5*Math.pow(Math.random(), 4)); i++) {	
	// positioning of splotch varies between ±4dd, tending towards 0
		splat_range = 1;
		var x4 = dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2));
		var y4 = dd * 1 * (Math.pow(Math.random(), splat_range) - (splat_range/2));
		// direction of splotch varies between ±0.5
		var x5 = Math.random() - 0.5;
		var y5 = Math.random() - 0.5;
		var d_ = d*(0.5+Math.random());
		obj.lineStyle()Style(d_, 0x000000, 100);
		obj.moveTo((x1+x4), (y1+y4));
		obj.lineTo((x1+x4+x5), (y1+y4+y5));
		_root.curves.push([(x1+x4), (y1+y4), (x1+x4+x5), (y1+y4+y5), (x1+x4+x5), (y1+y4+y5), d_]);
	
	 }
		 */
	}
} // END CLASS Actionpainting