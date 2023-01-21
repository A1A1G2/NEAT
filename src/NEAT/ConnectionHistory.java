package NEAT;

import java.util.ArrayList;

class ConnectionHistory {
	int fromNode;
	int toNode;
	int innovationNumber;
	ArrayList<Integer> innovationNumbers = new ArrayList<Integer>(); 
																					
	@SuppressWarnings("unchecked")
	ConnectionHistory(int from, int to, int inno, ArrayList<Integer> innovationNos) {
		fromNode = from;
	    toNode = to;
	    innovationNumber = inno;
	    innovationNumbers = (ArrayList<Integer>)innovationNos.clone();
	}
	  
	boolean matches(Genome genome, Node from, Node to) {
		if (genome.connects.size() != innovationNumbers.size()) {
			return false;
		}
		if (from.number != fromNode || to.number != toNode) {
			return false;
		}
		for (int i = 0; i< genome.connects.size(); i++) {
			if (!innovationNumbers.contains(genome.connects.get(i).innovationNo)) {
				return false;
			}
		}
	  
		return true;
	  
	}
}
