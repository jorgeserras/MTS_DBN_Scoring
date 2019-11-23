package com.github.tDBN.dbn;

import java.util.ArrayList;

/******************************************************************************
 *  Compilation:  javac MarkovChain.java
 *  Execution:    java MarkovChain
 *  
 *  Computes the expected time to go from state N-1 to state 0
 *
 *  Data taken from Glass and Hall (1949) who distinguish 7 states
 *  in their social mobility study:
 * 
 *   1. Professional, high administrative
 *   2. Managerial
 *   3. Inspectional, supervisory, non-manual high grade
 *   4. Non-manual low grade
 *   5. Skilled manual
 *   6. Semi-skilled manual
 *   7. Unskilled manual
 *
 *   See also Happy Harry, 2.39.
 *
 ******************************************************************************/


public class MarkovChain {
	
	
	public int Steps_to_zero(){
		
		// the state transition matrix
	      double[][] transition = { { 0.386, 0.147, 0.202, 0.062, 0.140, 0.047, 0.016},
	                                { 0.107, 0.267, 0.227, 0.120, 0.207, 0.052, 0.020},
	                                { 0.035, 0.101, 0.188, 0.191, 0.357, 0.067, 0.061},
	                                { 0.021, 0.039, 0.112, 0.212, 0.431, 0.124, 0.061},
	                                { 0.009, 0.024, 0.075, 0.123, 0.473, 0.171, 0.125},
	                                { 0.000, 0.103, 0.041, 0.088, 0.391, 0.312, 0.155},
	                                { 0.000, 0.008, 0.036, 0.083, 0.364, 0.235, 0.274}
	                              };

	      int N = 7;                        // number of states
	      int state = N - 1;                // current state
	      int steps = 0;                    // number of transitions

	      // run Markov chain
	      while (state > 0) {
	         //System.out.println(state);
	         steps++;
	         double r = Math.random();
	         double sum = 0.0;

	         // determine next state
	         for (int j = 0; j < N; j++) {
	            sum += transition[state][j];
	            if (r <= sum) {
	               state = j;
	               break;
	            }
	         }

	      }
	      //System.out.println(state);
	      //System.out.println("The number of steps =  " + steps);
	      return steps;
	   }

	
	// n is the size of the sequence to simulate and state the initial state
	public static ArrayList<Integer> Simulate_nsize_sequence(int n, int state){
		
		// the state transition matrix
	      double[][] transition = { { 0.386, 0.147, 0.202, 0.062, 0.140, 0.047, 0.016},
	                                { 0.107, 0.267, 0.227, 0.120, 0.207, 0.052, 0.020},
	                                { 0.035, 0.101, 0.188, 0.191, 0.357, 0.067, 0.061},
	                                { 0.021, 0.039, 0.112, 0.212, 0.431, 0.124, 0.061},
	                                { 0.009, 0.024, 0.075, 0.123, 0.473, 0.171, 0.125},
	                                { 0.000, 0.103, 0.041, 0.088, 0.391, 0.312, 0.155},
	                                { 0.000, 0.008, 0.036, 0.083, 0.364, 0.235, 0.274}
	                              };

	      int N = 7;                        // number of states
	      //int state = N - 1;                // current state
	      int steps = 0;                    // number of transitions
	      
	      ArrayList<Integer> sequence = new ArrayList<Integer>();

	      // run Markov chain
	      while (steps < n) {
	         //System.out.println(state);
	    	 sequence.add(state);
	         steps++;
	         double r = Math.random();
	         double sum = 0.0;

	         // determine next state
	         for (int j = 0; j < N; j++) {
	            sum += transition[state][j];
	            if (r <= sum) {
	               state = j;
	               System.out.println(state);
	               break;
	            }
	         }

	      }

	      //System.out.println("The number of steps =  " + steps);
	      return sequence;
	   }
	
	
	
	

   public static void main(String[] args) { 

      // the state transition matrix
      double[][] transition = { { 0.386, 0.147, 0.202, 0.062, 0.140, 0.047, 0.016},
                                { 0.107, 0.267, 0.227, 0.120, 0.207, 0.052, 0.020},
                                { 0.035, 0.101, 0.188, 0.191, 0.357, 0.067, 0.061},
                                { 0.021, 0.039, 0.112, 0.212, 0.431, 0.124, 0.061},
                                { 0.009, 0.024, 0.075, 0.123, 0.473, 0.171, 0.125},
                                { 0.000, 0.103, 0.041, 0.088, 0.391, 0.312, 0.155},
                                { 0.000, 0.008, 0.036, 0.083, 0.364, 0.235, 0.274}
                              };

      int N = 7;                        // number of states
      int state = N - 1;                // current state
      int steps = 0;                    // number of transitions

      // run Markov chain
      while (state > 0) {
         System.out.println(state);
         steps++;
         double r = Math.random();
         double sum = 0.0;

         // determine next state
         for (int j = 0; j < N; j++) {
            sum += transition[state][j];
            if (r <= sum) {
               state = j;
               break;
            }
         }

      }
      System.out.println(state);
      System.out.println("The number of steps =  " + steps);
      
      
      ArrayList<Integer> sequence = Simulate_nsize_sequence(4, 6);
   
      System.out.println(sequence);
      
   }
   
   
   

   
   
}
