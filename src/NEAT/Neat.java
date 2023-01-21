package NEAT;

public class Neat {
	public static int nextConnectionNo =1000;
	public static int trainingNumber = 100;
	
	public Population pop;
	
	public Neat(int size) {
		pop = new Population(size);
	}

}
