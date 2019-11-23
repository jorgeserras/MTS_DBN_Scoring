package com.github.tDBN.cli;

import com.github.tDBN.dbn.DynamicBayesNet;
import com.github.tDBN.dbn.LLScoringFunction;
import com.github.tDBN.dbn.MDLScoringFunction;
import com.github.tDBN.dbn.Observations;
import com.github.tDBN.dbn.Scores;
import com.github.tDBN.utils.Utils;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import com.github.tDBN.dbn.OutlierDetection;



// This is used by the R scripts to perform Model Training and Scoring using rJava package

public class Model_training_outlier_detection {
	
	private Double[][] scores;
	
	private Observations observations;
	
	private DynamicBayesNet dbn;
	
//	private int markov_lag;
//	
//	private int previous_parents;
//	
//	private boolean stationary;
//	
//	private String filepath;
	
	// Constructor for a default OutlierDetection algorithm using an existing DBN and observations
	public Model_training_outlier_detection(String filepath, int markov_lag, int previous_parents, boolean stationary, String score_function) {
		

		this.observations = new Observations(filepath, markov_lag);
		
		Scores s = new Scores(observations, previous_parents, stationary, false);

		//ScoringFunction sf;

	
		if (score_function.equals("ll")) { // Log-Likelihood
			//sf = new LLScoringFunction();
			//System.out.println(cmd.getOptionValue("s"));
//			if (verbose)
//				System.out.println("Evaluating network with LL score.");
			s.evaluate(new LLScoringFunction());
		} else {
			if(score_function.equals("mdl")){
				//sf = new MDLScoringFunction();
				//System.out.println(cmd.getOptionValue("s"));
	//			if (verbose)
	//				System.out.println("Evaluating network with MDL score.");
				s.evaluate(new MDLScoringFunction());
			}
		}
		
//		if (verbose) {
//			if (cmd.hasOption("r"))
//				System.out.println("Root node specified: " + root);
//			if (spanning)
//				System.out.println("Finding a maximum spanning tree.");
//			else
//				System.out.println("Finding a maximum branching.");
//		}


//		if(is_bcDBN) {
//			System.out.println("Learning bcDBN networks.");
//			dbn=s.to_bcDBN(sf,intra_ind);
//
//		}
//		
//		else {
//			
//			if(is_bcDBN) {
//				System.out.println("Learning cDBN networks.");
//				dbn=s.to_cDBN(sf,intra_ind);}
//			
//			
//		else {
//			System.out.println("Learning tDBN networks.");
			this.dbn = s.toDBN(-1, false); // root and spanning arguments are the defaults 

//		}
		
//		}


		//if (printParameters)
			this.dbn.learnParameters(observations);
		/////////////////////////////////////////////////
		
		
		///////////////////////////// SCORING, OUTLIER DETECTION
			
			OutlierDetection OD = new OutlierDetection(observations , dbn);
			
			this.scores = OD.Outlierness();  
			
			List<Integer> threshold_subjects = OD.ThresholdSubjects(-5.58, true, "output", true); // first True means average score and second to write on a file
			threshold_subjects.removeAll(Collections.singleton(null)); // removes nulls, sometimes they appear

			
			
			// Write all the scores to a file:
			try {
				OD.Write_to_csv("transition_output"); // The argument is the InputFileName to adapt the output score file name 
			} catch (Exception e2) {
				e2.printStackTrace();
			} 
			
			String output;
		
			output = dbn.toString(true);
			
				try {
					Utils.writeToFile("dbn_structure_output", output); // Write file with structure
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
			output = dbn.toDot(false); ///// .dot file for visualization
			
			try {
				Utils.writeToFile("dbn_structure_dot", output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
		
	}
	
	//// To run the java algorithm just like in the WebApp
	public static void main(String[] args) {
		Model_training_outlier_detection DBNOD = new Model_training_outlier_detection("clean_mortality_normalized_log_discrete.csv", 1, 1, true, "ll");
		
	}
	

	
}
