package NEAT;

import java.util.ArrayList;

public class XorSamples {
	
	public static ArrayList<int[]> sam = new ArrayList<>();
	
	static {
		
		sam.add(new int[3]);
		sam.get(0)[0] = 0;
		sam.get(0)[1] = 0;
		sam.get(0)[2] = 0;
		
		sam.add(new int[3]);
		sam.get(1)[0] = 0;
		sam.get(1)[1] = 1;
		sam.get(1)[2] = 1;
		
		sam.add(new int[3]);
		sam.get(2)[0] = 1;
		sam.get(2)[1] = 0;
		sam.get(2)[2] = 1;
		
		sam.add(new int[3]);
		sam.get(3)[0] = 1;
		sam.get(3)[1] = 1;
		sam.get(3)[2] = 0;
	}
	public XorSamples() {
		//do nothing
	}
	public int[] getRandomSample() {
		return sam.get((int)Math.floor(Math.random()*4));
	}

}
