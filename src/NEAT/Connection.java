package NEAT;

import java.util.Random;

public class Connection {
	Random random = new Random();
	Node fromNode;
	Node toNode;
	double weight;
	boolean enabled = true;
	int innovationNo;
	
	Connection(Node from, Node to, double w, int inno) {
		fromNode = from;
		toNode = to;
		weight = w;
		innovationNo = inno;
	}

	void mutateWeight() {
		double rand2 = Math.random();
		if (rand2 < 0.1) {
			weight = Math.random()*2-1;
		} else {
			weight += random.nextGaussian()/50;
			if(weight > 1){weight = 1;}
			if(weight < -1){weight = -1;}
		}
	}
	Connection clone(Node from, Node  to) {
		Connection clone = new Connection(from, to, weight, innovationNo);
		clone.enabled = enabled;
		return clone;
	}
}
