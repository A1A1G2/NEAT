package NEAT;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public class Genome {
	Random random = new Random();
	ArrayList<Connection> connects = new  ArrayList<Connection>();
	ArrayList<Node> nodes = new ArrayList<Node>();//list of nodes
	int inputs;
	int outputs;
	int layers =2;
	int nextNode = 0;
	int biasNode;
	ArrayList<Node> network = new ArrayList<Node>();//sorted useful nodes
	
	Genome(int in, int out) {
		inputs = in;
		outputs = out;

		for (int i = 0; i<inputs; i++) {
			nodes.add(new Node(i));
			nextNode ++;
			nodes.get(i).layer =0;
		}

		for (int i = 0; i < outputs; i++) {
			nodes.add(new Node(nextNode));
			nodes.get(nextNode).layer = 1;
			nextNode++;
		}

		nodes.add(new Node(nextNode));
		biasNode = nextNode; 
		nextNode++;
		nodes.get(biasNode).layer = 0;
	}
	  
	Node getNode(int nodeNumber) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).number == nodeNumber) {
				return nodes.get(i);
			}
		}
		return null;
	}
	  
	void connectNodes() {
		for (int i = 0; i< nodes.size(); i++) {
			nodes.get(i).outputConnections.clear();
		}

		for (int i = 0; i < connects.size(); i++) {
			connects.get(i).fromNode.outputConnections.add(connects.get(i));
		}
	}

	double[] feedForward(double[] inputValues) {
		for (int i = 0; i < inputs; i++) {
			nodes.get(i).setOutputValue(inputValues[i]);
		}
		nodes.get(biasNode).setOutputValue(1);

		for (int i = 0; i< network.size(); i++) {
			network.get(i).engage();
		}

		double[] outs = new double[outputs];
			for (int i = 0; i < outputs; i++) {
				outs[i] = nodes.get(inputs + i).getOutputValue();
			}

		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setInputSum(0);
		}

		return outs;
	}


  void generateNetwork() {
	  connectNodes();
	  network = new ArrayList<Node>();

	  for (int l = 0; l< layers; l++) {
		  for (int i = 0; i< nodes.size(); i++) {
			  if (nodes.get(i).layer == l) {
				  network.add(nodes.get(i));// sorting nodes by their layers
			  }
		  }
	  }
  }
  
  void addRandomNode(ArrayList<ConnectionHistory> innovationHistory) {
	  if (connects.size() ==0) {
		  addRandomConnection(innovationHistory); 
		  return;
	  }
	  
	  int targetConnection;
	  do {//dont disconnect bias
		  targetConnection =random.nextInt(connects.size());
	  }while (connects.get(targetConnection).fromNode == nodes.get(biasNode) && connects.size() !=1 );

	  connects.get(targetConnection).enabled = false;

	  int newNodeNo = nextNode;
	  nodes.add(new Node(newNodeNo));
	  nextNode ++;
	  int connectionInnovationNumber = getInnovationNumber(innovationHistory, connects.get(targetConnection).fromNode, getNode(newNodeNo));
	  connects.add(new Connection(connects.get(targetConnection).fromNode, getNode(newNodeNo), 1, connectionInnovationNumber));


	  connectionInnovationNumber = getInnovationNumber(innovationHistory, getNode(newNodeNo), connects.get(targetConnection).toNode);
	  connects.add(new Connection(getNode(newNodeNo), connects.get(targetConnection).toNode, connects.get(targetConnection).weight, connectionInnovationNumber));
	  getNode(newNodeNo).layer = connects.get(targetConnection).fromNode.layer +1;


	  connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(biasNode), getNode(newNodeNo));

	  connects.add(new Connection(nodes.get(biasNode), getNode(newNodeNo), 0, connectionInnovationNumber));

	  if (getNode(newNodeNo).layer == connects.get(targetConnection).toNode.layer) {
		  for (int i = 0; i< nodes.size() -1; i++) {
			  if (nodes.get(i).layer >= getNode(newNodeNo).layer) {
				  nodes.get(i).layer ++;
			  }
		  }
		  layers ++;
	  }
	  connectNodes();
  }

  
  void addRandomConnection(ArrayList<ConnectionHistory> innovationHistory) {
	  if (fullyConnected()) {
		  System.out.println("connection failed");
		  return;
	  }
	  
	  int randomNode1;
	  int randomNode2;
	  do {
		  randomNode1 =random.nextInt(nodes.size()); 
		  randomNode2 =random.nextInt(nodes.size());
	  }while (badNodes(randomNode1, randomNode2));
	  
	  if (nodes.get(randomNode1).layer > nodes.get(randomNode2).layer) {
		  randomNode1 += randomNode2;
		  randomNode2 = randomNode1-randomNode2;//swapp
		  randomNode1 -=randomNode2; 
	  }    

	  int connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(randomNode1), nodes.get(randomNode2));

	  connects.add(new Connection(nodes.get(randomNode1), nodes.get(randomNode2),Math.random()*2-1, connectionInnovationNumber));
	  connectNodes();
  }
  boolean badNodes(int r1, int r2) {
	  if (nodes.get(r1).layer == nodes.get(r2).layer) return true; // if the nodes are in the same layer 
	  if (nodes.get(r1).isConnectedTo(nodes.get(r2))) return true; //if the nodes are already connected
	  return false;
  }

  int getInnovationNumber(ArrayList<ConnectionHistory> innovationHistory, Node from, Node to) {//TODO: i will change
	  boolean isNew = true;
	  int connectionInnovationNumber = Neat.nextConnectionNo;
	  for (int i = 0; i < innovationHistory.size(); i++) {
		  if (innovationHistory.get(i).matches(this, from, to)) {
			  isNew = false;//its not a new mutation
			  connectionInnovationNumber = innovationHistory.get(i).innovationNumber; 
			  break;
		  }
	  }

    if (isNew) {
      ArrayList<Integer> innoNumbers = new ArrayList<Integer>();
      for (int i = 0; i< connects.size(); i++) {
        innoNumbers.add(connects.get(i).innovationNo);
      }

      innovationHistory.add(new ConnectionHistory(from.number, to.number, connectionInnovationNumber, innoNumbers));
      Neat.nextConnectionNo++;
    }
    return connectionInnovationNumber;
  }

  boolean fullyConnected() {
	  int maxConnections = 0;
	  int[] nodesInLayers = new int[layers];

    //populate array
	  for (int i =0; i< nodes.size(); i++) {
		  nodesInLayers[nodes.get(i).layer] +=1;
	  }

	  for (int i = 0; i < layers-1; i++) {
		  int nodesInFront = 0;
		  for (int j = i+1; j < layers; j++) {
			  nodesInFront += nodesInLayers[j];
		  }

		  maxConnections += nodesInLayers[i] * nodesInFront;
	  }

	  if (maxConnections == connects.size()) {
		  return true;
	  }
	  return false;
  }

  void mutate(ArrayList<ConnectionHistory> innovationHistory) {//TODO i will add simplificity
	  if (connects.size() == 0) {
		  addRandomConnection(innovationHistory);
	  }

	  double rand1 = Math.random();
	  if (rand1<0.8) {
		  for (int i = 0; i< connects.size(); i++) {
			  connects.get(i).mutateWeight();
		  }
	  }
	  
	  double rand2 = Math.random();
	  if (rand2<0.28) {
		  addRandomConnection(innovationHistory);
	  }

	  double rand3 =(double) Math.random();
	  if (rand3<0.02) {
		  addRandomNode(innovationHistory);
	  }
  }

  Genome crossover(Genome parent2) {
	  Genome child = new Genome(inputs, outputs, true);//TODO try with subclasses
	  child.connects.clear();
	  child.nodes.clear();
	  child.layers = layers;
	  child.nextNode = nextNode;
	  child.biasNode = biasNode;
	  ArrayList<Connection> childGenes = new ArrayList<Connection>();
	  ArrayList<Boolean> isEnabled = new ArrayList<Boolean>(); 

	  for (int i = 0; i< connects.size(); i++) {
		  boolean setEnabled = true;

		  int parent2gene = matchingGene(parent2, connects.get(i).innovationNo);
		  if (parent2gene != -1) {//if the genes match
			  if (!connects.get(i).enabled || !parent2.connects.get(parent2gene).enabled) {
				  if (Math.random() < 0.75) {
					  setEnabled = false;
				  }
			  }
			  double rand =(double) Math.random();
			  if (rand<0.5) {
				  childGenes.add(connects.get(i));
			  } else {
				  childGenes.add(parent2.connects.get(parent2gene));
			  }
		  } else {
			  childGenes.add(connects.get(i));
			  setEnabled = connects.get(i).enabled;
		  }
      isEnabled.add(setEnabled);
    }

    for (int i = 0; i < nodes.size(); i++) {
      child.nodes.add(nodes.get(i).clone());
    }


    for ( int i =0; i<childGenes.size(); i++) {
      child.connects.add(childGenes.get(i).clone(child.getNode(childGenes.get(i).fromNode.number), child.getNode(childGenes.get(i).toNode.number)));
      child.connects.get(i).enabled = isEnabled.get(i);
    }

    child.connectNodes();
    return child;
  }

  Genome(int in, int out, boolean crossover) {
    inputs = in; 
    outputs = out;
  }
  int matchingGene(Genome parent2, int innovationNumber) {
    for (int i =0; i < parent2.connects.size(); i++) {
      if (parent2.connects.get(i).innovationNo == innovationNumber) {
        return i;
      }
    }
    return -1;
  }
  void printGenome() {
    System.out.println("Print genome  layers: "+ layers);  
    System.out.print(" bias node: "  + biasNode);
    System.out.println("nodes");
    for (int i = 0; i < nodes.size(); i++) {
    	System.out.print(nodes.get(i).number + ",");
    }
    System.out.println("Genes");
    for (int i = 0; i < connects.size(); i++) {
    	System.out.println("gene " + connects.get(i).innovationNo+ "From node " + connects.get(i).fromNode.number+ "To node " + connects.get(i).toNode.number+ 
        "is enabled " +connects.get(i).enabled+ "from layer " + connects.get(i).fromNode.layer+ "to layer " + connects.get(i).toNode.layer+ "weight: " + connects.get(i).weight);
    }

    System.out.println();
  }

  protected Genome clone() {

    Genome clone = new Genome(inputs, outputs, true);

    for (int i = 0; i < nodes.size(); i++) {
      clone.nodes.add(nodes.get(i).clone());
    }

    for ( int i =0; i<connects.size(); i++) {
      clone.connects.add(connects.get(i).clone(clone.getNode(connects.get(i).fromNode.number), clone.getNode(connects.get(i).toNode.number)));
    }

    clone.layers = layers;
    clone.nextNode = nextNode;
    clone.biasNode = biasNode;
    clone.connectNodes();

    return clone;
  }
  /*void drawGenome(int startX, int startY, int w, int h) {
    ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>();
    ArrayList<PVector> nodePoses = new ArrayList<PVector>();
    ArrayList<Integer> nodeNumbers= new ArrayList<Integer>();


    for (int i = 0; i< layers; i++) {
      ArrayList<Node> temp = new ArrayList<Node>();
      for (int j = 0; j< nodes.size(); j++) {
        if (nodes.get(j).layer == i ) {
          temp.add(nodes.get(j)); 
        }
      }
      allNodes.add(temp);
    }

    for (int i = 0; i < layers; i++) {
      fill(255, 0, 0);
      double x = startX + (double)((i)*w)/(double)(layers-1);
      for (int j = 0; j< allNodes.get(i).size(); j++) {
        double y = startY + ((double)(j + 1.0) * h)/(double)(allNodes.get(i).size() + 1.0);
        nodePoses.add(new PVector(x, y));
        nodeNumbers.add(allNodes.get(i).get(j).number);
      }
    }
    stroke(0);
    strokeWeight(2);
    for (int i = 0; i< gens.size(); i++) {
      if (gens.get(i).enabled) {
        stroke(0);
      } else {
        stroke(100);
      }
      PVector from;
      PVector to;
      from = nodePoses.get(nodeNumbers.indexOf(gens.get(i).fromNode.number));
      to = nodePoses.get(nodeNumbers.indexOf(gens.get(i).toNode.number));
      if (gens.get(i).weight > 0) {
        stroke(255, 0, 0);
      } else {
        stroke(0, 0, 255);
      }
      strokeWeight(map(abs(gens.get(i).weight), 0, 1, 0, 5));
      line(from.x, from.y, to.x, to.y);
    }

    for (int i = 0; i < nodePoses.size(); i++) {
      fill(255);
      stroke(0);
      strokeWeight(1);
      ellipse(nodePoses.get(i).x, nodePoses.get(i).y, 20, 20);
      textSize(10);
      fill(0);
      textAlign(CENTER, CENTER);


      text(nodeNumbers.get(i), nodePoses.get(i).x, nodePoses.get(i).y);
    }
  }*/
}
