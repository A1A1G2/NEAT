package NEAT;
import java.awt.*;  
import javax.swing.JFrame;  
public class XorProblem extends Canvas{
	public static void main(String[] args) {
		Neat neat = new Neat();
		while(true) {
			if(!neat.pop.done()) {
				neat.pop.updateAlives();
			}else {
				neat.pop.naturalSelection();
			}
		}
	}

}
