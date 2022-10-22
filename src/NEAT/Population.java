package NEAT;

import java.util.ArrayList;

class Population {
	  ArrayList<Player> pop = new ArrayList<Player>();
	  Player bestPlayer;//the best ever player 
	  double bestScore =0;//the score of the best ever player
	  int gen;
	  ArrayList<ConnectionHistory> innovationHistory = new ArrayList<ConnectionHistory>();
	  ArrayList<Player> genPlayers = new ArrayList<Player>();
	  ArrayList<Species> species = new ArrayList<Species>();

	  boolean massExtinctionEvent = false;
	  boolean newStage = false;
	  int populationLife = 0;


	  Population(int size) {

	    for (int i =0; i<size; i++) {
	      pop.add(new Player());
	      pop.get(i).brain.generateNetwork();
	      pop.get(i).brain.mutate(innovationHistory);
	    }
	  }

	  void updateAlives() {
	    populationLife ++;
	    for (int i = pop.size()-1; i>=0; i--) {
	      if (!pop.get(i).dead) {
	        pop.get(i).update();
	        pop.get(i).look();
	        pop.get(i).think();
	      }
	    }
	  }
	  
	  boolean done() {
	    for (int i = 0; i< pop.size(); i++) {
	      if (!pop.get(i).dead) {
	        return false;
	      }
	    }
	    return true;
	  }

	  void setBestPlayer() {
	    Player tempBest =  species.get(0).players.get(0);
	    tempBest.gen = gen;


	    if (tempBest.score > bestScore) {
	      System.out.println("old best:"+ bestScore);
	      System.out.println("new best:"+ tempBest.score);
	      bestScore = tempBest.score;
	      bestPlayer = tempBest;
	    }
	  }

	  void naturalSelection() {
	    speciate();
	    calculateFitness();
	    sortSpecies();
	    if (massExtinctionEvent) { 
	      massExtinction();
	      massExtinctionEvent = false;
	    }
	    cullSpecies();
	    setBestPlayer();
	    killStaleSpecies();
	    killBadSpecies();


	    System.out.println("generation "+ gen+ " Number of mutations: "+ innovationHistory.size()+ " species: " + species.size()+ " <<<<<<<<<<<<<");


	    double averageSum = getAvgFitnessSum();
	    ArrayList<Player> children = new ArrayList<Player>();
	    System.out.println("Species:");               
	    for (int j = 0; j < species.size(); j++) {
	      System.out.println("Species "+j); 
	      System.out.println("best unadjusted fitness:"+ species.get(j).bestFitness);
	      for (int i = 0; i < species.get(j).players.size(); i++) {
	    	System.out.println("player " + i+ ": fitness: "+ " % " +species.get(j).players.get(i).success +" "+  species.get(j).players.get(i).fitness+ " score " + species.get(j).players.get(i).score+ ' ');
	      }
	      children.add(species.get(j).champ.clone());

	      int NoOfChildren =(int) Math.floor(species.get(j).averageFitness/averageSum * pop.size()) -1;
	      for (int i = 0; i< NoOfChildren; i++) {
	        children.add(species.get(j).giveMeBaby(innovationHistory));
	      }
	    }

	    while (children.size() < pop.size()) {
	      children.add(species.get(0).giveMeBaby(innovationHistory));
	    }
	    pop.clear();
	    pop = (ArrayList)children.clone();
	    gen+=1;
	    for (int i = 0; i< pop.size(); i++) {
	      pop.get(i).brain.generateNetwork();
	    }
	    
	    populationLife = 0;
	  }

	  void speciate() {
	    for (Species s : species) {
	      s.players.clear();
	    }
	    for (int i = 0; i< pop.size(); i++) {
	      boolean speciesFound = false;
	      int j=0;
	      while(j<species.size() && !speciesFound){
	        if (species.get(j).sameSpecies(pop.get(i).brain)) {
	          species.get(j).addToSpecies(pop.get(i));
	          speciesFound = true;
	        }
	        j++;
	      }
	      if (!speciesFound) {
	        species.add(new Species(pop.get(i)));
	      }
	    }
	  }

	  void calculateFitness() {
	    for (int i =1; i<pop.size(); i++) {
	      pop.get(i).calculateFitness();
	    }
	  }
	 
	  void sortSpecies() {
	    for (Species s : species) {
	      s.sortSpecies();
	    }
	    ArrayList<Species> temp = new ArrayList<Species>();
	    for (int i = 0; i < species.size(); i ++) {
	      double max = 0;
	      int maxIndex = 0;
	      for (int j = 0; j< species.size(); j++) {
	        if (species.get(j).bestFitness > max) {
	          max = species.get(j).bestFitness;
	          maxIndex = j;
	        }
	      }
	      temp.add(species.get(maxIndex));
	      species.remove(maxIndex);
	      i--;
	    }
	    species = (ArrayList)temp.clone();
	  }
	  
	  void killStaleSpecies() {
	    for (int i = 2; i< species.size(); i++) {
	      if (species.get(i).staleness >= 15) {
	        species.remove(i);
	        i--;
	      }
	    }
	  }
	  
	  void killBadSpecies() {
	    double averageSum = getAvgFitnessSum();

	    for (int i = 1; i< species.size(); i++) {
	      if (species.get(i).averageFitness/averageSum * pop.size() < 1) {
	        species.remove(i);//sad
	        i--;
	      }
	    }
	  }
	  
	  double getAvgFitnessSum() {
	    double averageSum = 0;
	    for (Species s : species) {
	      averageSum += s.averageFitness;
	    }
	    return averageSum;
	  }

	  
	  void cullSpecies() {
	    for (Species s : species) {
	      s.cull(); 
	      s.fitnessSharing();
	      s.setAverage();
	    }
	  }


	  void massExtinction() {
	    for (int i =5; i< species.size(); i++) {
	      species.remove(i);//sad
	      i--;
	    }
	  }
	}
