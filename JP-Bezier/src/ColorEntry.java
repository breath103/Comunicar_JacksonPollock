import java.util.ArrayList;
import java.util.List;

public class ColorEntry{
	private List<ColorSample> colors;
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
		for(ColorSample color : colors){
			totalWeight += color.getWeight();
		}
		double randomValue = Math.random() * totalWeight;
		float currentWeight = 0.0f;
		for(ColorSample color : colors){
			currentWeight += color.getWeight();
			if(currentWeight >= randomValue){
				return color;
			}
		}
		
		System.out.println("ERROR!!");
		return null;
	}
}