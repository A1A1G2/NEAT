package NEAT;

import java.util.ArrayList;

public class XorSamples {
	
	public static ArrayList<int[]> sam = new ArrayList<>();
	
	static {
		for(int i = 0;i<2;i++) {
			for(int j = 0;j<2;j++) {
				sam.add(new int[3]);
				int index = sam.size()-1;
				sam.get(index)[0] = i;
				sam.get(index)[1] = j;
				sam.get(index)[2] = i^j;
			}
		}
		
	}
	public XorSamples() {
		//do nothing
	}
	public int[] getRandomSample() {
		return sam.get((int)Math.floor(Math.random()*4));
	}

}
