package NEAT;

import java.util.ArrayList;
import java.util.Random;

class Species {
	  Random random = new Random();
	  ArrayList<Player> players = new ArrayList<Player>();
	  double bestFitness = 0;
	  Player champ;
	  double averageFitness = 0;
	  int staleness = 0;//how many generations the species has gone without an improvement
	  Genome rep;

	  
	  double excessCoeff = 1;
	  double weightDiffCoeff =(double) 0.5;
	  double compatibilityThreshold = 3;

	  Species() {
	  }


	  
	  Species(Player p) {
	    players.add(p); 
	    
	    bestFitness = p.fitness; 
	    rep = p.brain.clone();
	    champ = p.clone();
	  }

	  boolean sameSpecies(Genome g) {
	    double compatibility;
	    double excessAndDisjoint = getExcessDisjoint(g, rep);
	    double averageWeightDiff = averageWeightDiff(g, rep);


	    double largeGenomeNormaliser = g.connects.size() - 20;
	    if (largeGenomeNormaliser<1) {
	      largeGenomeNormaliser =1;
	    }

	    compatibility =  (excessCoeff* excessAndDisjoint/largeGenomeNormaliser) + (weightDiffCoeff* averageWeightDiff);
	    return (compatibilityThreshold > compatibility);
	  }

	  void addToSpecies(Player p) {
	    players.add(p);
	  }

	  double getExcessDisjoint(Genome brain1, Genome brain2) {
	    double matching = 0.0f;
	    for (int i =0; i <brain1.connects.size(); i++) {
	      for (int j = 0; j < brain2.connects.size(); j++) {
	        if (brain1.connects.get(i).innovationNo == brain2.connects.get(j).innovationNo) {
	          matching ++;
	          break;
	        }
	      }
	    }
	    return (brain1.connects.size() + brain2.connects.size() - 2*(matching));
	  }
	  
	  double averageWeightDiff(Genome brain1, Genome brain2) {
	    if (brain1.connects.size() == 0 || brain2.connects.size() ==0) {
	      return 0;
	    }


	    double matching = 0;
	    double totalDiff= 0;
	    for (int i =0; i <brain1.connects.size(); i++) {
	      for (int j = 0; j < brain2.connects.size(); j++) {
	        if (brain1.connects.get(i).innovationNo == brain2.connects.get(j).innovationNo) {
	          matching ++;
	          totalDiff += Math.abs(brain1.connects.get(i).weight - brain2.connects.get(j).weight);
	          break;
	        }
	      }
	    }
	    if (matching ==0) {
	      return 100;
	    }
	    return totalDiff/matching;
	  }
	  
	  void sortSpecies() {

	    ArrayList<Player> temp = new ArrayList<Player>();

	    
	    for (int i = 0; i < players.size(); i ++) {
	      double max = 0;
	      int maxIndex = 0;
	      for (int j = 0; j< players.size(); j++) {
	        if (players.get(j).fitness > max) {
	          max = players.get(j).fitness;
	          maxIndex = j;
	        }
	      }
	      temp.add(players.get(maxIndex));
	      players.remove(maxIndex);
	      i--;
	    }

	    players = (ArrayList<Player>)temp.clone();
	    if (players.size() == 0) {
	      System.out.println("BP_ONK!!"); 
	      staleness = 200;
	      return;
	    }
	    
	    if (players.get(0).fitness > bestFitness) {
	      staleness = 0;
	      bestFitness = players.get(0).fitness;
	      rep = players.get(0).brain.clone();
	      champ = players.get(0).clone();
	    } else {
	      staleness ++;
	    }
	  }

	  void setAverage() {

	    double sum = 0;
	    for (int i = 0; i < players.size(); i ++) {
	      sum += players.get(i).fitness;
	    }
	    averageFitness = sum/players.size();
	  }
	  
	  Player giveMeBaby(ArrayList<ConnectionHistory> innovationHistory) {
	    Player baby;
	    if (Math.random() < 0.25) {
	      baby =  selectPlayer().clone();
	    } else {

	      Player parent1 = selectPlayer();
	      Player parent2 = selectPlayer();

	      if (parent1.fitness < parent2.fitness) {
	        baby =  parent2.crossover(parent1);
	      } else {
	        baby =  parent1.crossover(parent2);
	      }
	    }
	    baby.brain.mutate(innovationHistory);
	    return baby;
	  }

	  Player selectPlayer() {
	    double fitnessSum = 0;
	    for (int i =0; i<players.size(); i++) {
	      fitnessSum += players.get(i).fitness;
	    }

	    double rand =(double) Math.random()*(fitnessSum);
	    double runningSum = 0;

	    for (int i = 0; i<players.size(); i++) {
	      runningSum += players.get(i).fitness; 
	      if (runningSum > rand) {
	        return players.get(i);
	      }
	    }
	    return players.get(0);
	  }
	  void cull() {
	    if (players.size() > 2) {
	      for (int i = players.size()/2; i<players.size(); i++) {
	        players.remove(i); 
	        i--;
	      }
	    }
	  }
	  void fitnessSharing() {
	    for (int i = 0; i< players.size(); i++) {
	      players.get(i).fitness/=players.size();
	    }
	  }
	}
