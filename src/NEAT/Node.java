package NEAT;

import java.util.ArrayList;
import java.lang.Math;
public class Node {
	int number;
	double inputSum = 0;
	double outputValue = 0; 
	ArrayList<Connection> outputConnections = new ArrayList<Connection>();
	int layer = 0;
	
	Node(int no) {
		number = no;
	}

  	void engage() {
  		
  		if (layer!=0) {
  			outputValue = sigmoid(inputSum);
  		}

	    for (int i = 0; i< outputConnections.size(); i++) {
	    	if (outputConnections.get(i).enabled) {
	    		outputConnections.get(i).toNode.inputSum += outputConnections.get(i).weight * outputValue;
	    	}
	    }
  	}
  	
  	double sigmoid(double x) {
  		double y = (double) (1 / (1 + Math.pow(Math.E, -4.9*x)));
  		return y;
  	}
 
  	boolean isConnectedTo(Node node) {
  		if (node.layer == layer) {
	    	return false;
	    }
	    if (node.layer < layer) {
	    	for (int i = 0; i < node.outputConnections.size(); i++) {
	    		if (node.outputConnections.get(i).toNode == this) {
	    			return true;
	    		}
	    	}
	    } else {
	    	for (int i = 0; i < outputConnections.size(); i++) {
	    		if (outputConnections.get(i).toNode == node) {
	    			return true;
	    			}
	    		}
	    	}
	    return false;
  	}
 
  	protected Node clone() {
  		Node clone = new Node(number);
	    clone.layer = layer;
	    return clone;
  	}
  	void setInputSum(double in){
  		inputSum = in;
  	}
  	void setOutputValue(double out){
  		outputValue = out;
  	}
  	double getOutputValue(){
  		return outputValue;
  	}
}
