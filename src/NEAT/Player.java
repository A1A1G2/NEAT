package NEAT;

public class Player{
	XorSamples xorSamples;
	int[] xors;//keeping xor samples
	
	double fitness;
	Genome brain;

	double unadjustedFitness;
	int lifespan = 0;
	double bestScore =0;
	boolean dead=false;
	boolean reached=false;
	double score=0;
	int rsc=0;
	int gen = 0;
	
	int trainingNumber = 100;
	
	double error = 0;
	int correct=0;
	int success=0;
	int step = 0;

	int genomeInputs = 2;
	int genomeOutputs = 1;

	double[] vision = new double[genomeInputs];
	double[] decision = new double[genomeOutputs];
	  
	Player(){
	  brain = new Genome(genomeInputs, genomeOutputs);
	  xorSamples= new XorSamples();
	}
	void look(){
		xors=xorSamples.getRandomSample();
		vision[0] = xors[0];
		vision[1] = xors[1];
		step++;
	}
	void update() {
		if(step>=Neat.trainingNumber) {
			if(error == 0) reached=true;
			else {
				dead = true;
			}
			score = 1/(error/step);
			success = correct/step;
		}
	}
	void think(){
	  double max=0;
	  int maxIndex=0;
	  decision = brain.feedForward(vision);
	  for(int i=0;i<decision.length;i++){
	    if(max<decision[i]){
	      max = decision[i];
	      maxIndex = i;
	    }
	  }
	  error+=Math.pow(max-xors[2],2);
	  if(Math.pow(max-xors[2],2) < 0.25) {
		  correct++;
	  }
	  
	}
	protected Player clone() {
	  Player clone = new Player();
	  clone.brain = brain.clone();
	  clone.fitness = fitness;
	  clone.brain.generateNetwork(); 
	  clone.gen = gen;
	  clone.bestScore = score;
	  return clone;
	}
	void calculateFitness() {
	  fitness = score*score;
	}
	Player crossover(Player parent2) {
	  Player child = new Player();
	  child.brain = brain.crossover(parent2.brain);
	  child.brain.generateNetwork();
	  return child;
	}
	double currDistance(){
	return 0;
	}
	double sigmoid(double x) {
	  double y = (double) (1 / (1 + Math.pow((double)Math.E, 5*x)));
	  return y;
	}
}
