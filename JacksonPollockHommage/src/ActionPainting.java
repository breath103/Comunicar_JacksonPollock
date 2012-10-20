import processing.core.PApplet;


public class ActionPainting {

	PApplet mainApplet;

	// VARIABELN F�R SPRITZER
	float distance, new_size;
	float[] splatStartXY = new float[2];
	float[] splatMidXY = new float[2];
	float[] splatEndXY = new float[2];
	//float new_size_influence = (floor(random(100)))-0.5;
	//float mid_point_push = (floor(random(8))/4)-1;
	//float max_line_width = (random(100))+10;
	float new_size_influence = 0.8f; // .55
	float mid_point_push = -0.80f;
	float max_line_width = 80f; // 60
	float splat_range = 1.5f;
	float weight;
	
	int color;
	
	float pinselXY[] = new float[2];
	float oldPinselXY[] = new float[2];
	float acceleration = 0;       
	int farbFillStatus = 750;     

	ActionPainting(PApplet inMainApplet) {
		mainApplet = inMainApplet;
		splatStartXY[0] = inMainApplet.screenWidth/2;
		splatStartXY[1] = inMainApplet.screenHeight/2;
		//curveTightness(1.0); 
		inMainApplet.curveDetail(5);
	}


	//////////////////////////////////////////////////////////
	// LOOP: wird vom Hauptscript aufgerufen
	//////////////////////////////////////////////////////////
	public void update(float x,float y,int color) {
		pinselXY[0] = x;
		pinselXY[1] = y;
		this.color = color;
		
		mainApplet.noFill();
		if ((pinselXY[0] != oldPinselXY[0]) && (pinselXY[1] != oldPinselXY[1])) {
			if (farbFillStatus > 0) {
				splatMidXY[0] = ((splatEndXY[0]-splatStartXY[0])*(1+mid_point_push))+splatStartXY[0];
				splatMidXY[1] = ((splatEndXY[1]-splatStartXY[1])*(1+mid_point_push))+splatStartXY[1];
				splatStartXY[0] = splatEndXY[0];
				splatStartXY[1] = splatEndXY[1];
				splatEndXY[0] = pinselXY[0];
				splatEndXY[1] = pinselXY[1];

				distance = mainApplet.sqrt(mainApplet.pow((splatEndXY[0]-splatStartXY[0]), 2.0f)+mainApplet.pow((splatEndXY[1]-splatStartXY[1]), 2.0f));

				new_size = max_line_width/distance;

				weight = (new_size_influence*new_size)+((1-new_size_influence)*weight);
				weight = weight /1000 * 750 / 255 * acceleration;
				weight = mainApplet.min(20, weight);
				if (weight < 0) {
					weight = 0;
				}

				if (farbFillStatus < 0) {
					farbFillStatus = 0;
				}
				splat(splatStartXY[0], splatStartXY[1], splatEndXY[0], splatEndXY[1], splatMidXY[0], splatMidXY[1], weight);
			} 
		} // end if �nderung erfolgt
	} // end update()


	//////////////////////////////////////////////////////////
	// SPLAT!!! Erstellt die Hauptlinie und die Spritzer
	//////////////////////////////////////////////////////////
	void splat(float x1, float y1, float x2, float y2, float x3, float y3, double d) {
		// tempor�re Variabeln
		float dd, x4, y4, x5, y5;

		mainApplet.strokeWeight((float)d);
		mainApplet.println((float)d);
		mainApplet.stroke(color);

		mainApplet.curve(x3, y3, x1, y1, x2, y2, x3, y3);	

		splat_range = 1; //1
		dd = mainApplet.sqrt(mainApplet.pow((x2 - x1), 2) + mainApplet.pow((y2 - y1), 2));

		int anz = mainApplet.floor(mainApplet.random(20) / 255 * acceleration);
		for (int i = 0; i<anz; i++) {
			x4 = dd * 1 * (mainApplet.pow(mainApplet.random(10)/10, splat_range) - (splat_range/2));
			y4 = dd * 1 * (mainApplet.pow(mainApplet.random(10)/10, splat_range) - (splat_range/2));

			x5 = mainApplet.random(6) - 3;
			y5 = mainApplet.random(6) - 3;
			mainApplet.strokeWeight(((float)d*(0.7f+mainApplet.random(10)/10)));
			mainApplet.line((x1+x4), (y1+y4), (x1+x4+x5), (y1+y4+y5));

			if (farbFillStatus < 0) {
				farbFillStatus = 0;
			}
		} // end for
	} // end splat()
} // END CLASS Actionpainting