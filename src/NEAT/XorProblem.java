package NEAT;

import java.util.Scanner;

public class XorProblem {
	public static void main(String[] args) {
		Neat neat = new Neat(1000);
		int i = 0;
		double arr[]= {0,0};
		while(i<100) {
			if(!neat.pop.done()) {
				neat.pop.updateAlives();
			}else {
				i++;
				neat.pop.naturalSelection();
			}
		}
		i=0;
		System.out.println(neat.pop.bestPlayer.fitness);
		Scanner scn = new Scanner(System.in); 
		while(i<100) {
			i++;
			System.out.println("sayi giriniz:");
		    arr[0]= scn.nextInt();
		    arr[1]= scn.nextInt();
			System.out.println(neat.pop.bestPlayer.brain.feedForward(arr)[0]);
			
		}
		scn.close();
	}

}
