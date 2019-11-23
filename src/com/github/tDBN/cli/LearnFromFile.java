package com.github.tDBN.cli;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.GnuParser;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
//import org.apache.commons.cli.DefaultOptionBuilder;
//import org.apache.commons.cli.OptionBuilder;
//import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.tDBN.dbn.BayesNet;
import com.github.tDBN.dbn.Configuration;
import com.github.tDBN.dbn.DynamicBayesNet;
import com.github.tDBN.dbn.LLScoringFunction;
import com.github.tDBN.dbn.MDLScoringFunction;
import com.github.tDBN.dbn.ScoringFunction;
import com.github.tDBN.dbn.Observations;
import com.github.tDBN.dbn.Scores;
import com.github.tDBN.utils.Utils;

import com.github.tDBN.dbn.Attribute;
import com.github.tDBN.dbn.OutlierDetection;

public class LearnFromFile {

	@SuppressWarnings({ "static-access" })
	public static void main(String[] args) {

		// create Options object
		Options options = new Options();


		Option inputFile = Option.builder("i")
				.longOpt("file")
				.desc("Input CSV file to be used for network learning.")
				.hasArg()
				.argName("file")
				.build();

		Option numParents = Option.builder("p")
				.longOpt("numParents")
				.desc("Maximum number of parents from preceding time-slice(s).")
				.hasArg()
				.argName("int")
				.build();

		Option outputFile = Option.builder("o")
				.longOpt("outputFile")
				.desc("Writes output to <file>. If not supplied, output is written to terminal.")
				.hasArg()
				.argName("file")
				.build();

		Option rootNode = Option.builder("r")
				.longOpt("root")
				.desc("Root node of the intra-slice tree. By default, root is arbitrary.")
				.hasArg()
				.argName("int")
				.build();

		Option scoringFunction = Option.builder("s")
				.longOpt("scoringFunction")
				.desc("Scoring function to be used, either MDL or LL. MDL is used by default.")
				.hasArg()
				.build();

		Option dotFormat = Option.builder("d")
				.longOpt("dotFormat")
				.desc("Outputs network in dot format, allowing direct redirection into Graphviz to visualize the graph.")
				.build();


		Option compact = Option.builder("c")
				.longOpt("compact")
				.desc("Outputs network in compact format, omitting intra-slice edges. Only works if specified together with -d and with --markovLag 1.")
				.build();

		Option maxMarkovLag = Option.builder("m")
				.longOpt("markovLag")
				.desc("Maximum Markov lag to be considered, which is the longest distance between connected time-slices. Default is 1, allowing edges from one preceding slice.")
				.hasArg()
				.argName("int")
				.build();

		Option spanningTree = Option.builder("sp")
				.longOpt("nonStationary")
				.desc("Forces intra-slice connectivity to be a tree instead of a forest, eventually producing a structure with a lower score.")
				.build();

		Option nonStationary = Option.builder("ns")
				.longOpt("nonStationary")
				.desc("Learns a non-stationary network (one transition network per time transition). By default, a stationary DBN is learnt.")
				.build();

		Option parameters= Option.builder("pm")
				.longOpt("parameters")
				.desc("Learns and outputs the network parameters.")
				.build();


		Option bcDBN= Option.builder("bcDBN")
				.longOpt("bcDBN")
				.desc("Learns a bcDBN structure.")
				.build();
		
		Option cDBN= Option.builder("cDBN")
				.longOpt("cDBN")
				.desc("Learns a cDBN structure.")
				.build();

		Option intra_in= Option.builder("ind")
				.longOpt("intra_in")
				.desc("In-degree of the intra-slice network")
				.hasArg()
				.argName("int")
				.build();

		options.addOption(inputFile);
		options.addOption(numParents);
		options.addOption(outputFile);
		options.addOption(rootNode);
		options.addOption(scoringFunction);
		options.addOption(dotFormat);
		options.addOption(compact);
		options.addOption(maxMarkovLag);
		options.addOption(spanningTree);
		options.addOption(nonStationary);
		options.addOption(parameters);
		options.addOption(bcDBN);
		options.addOption(cDBN);
		options.addOption(intra_in);

		CommandLineParser parser = new DefaultParser();
		try {

			
			CommandLine cmd = parser.parse(options, args);



			boolean verbose = !cmd.hasOption("d");
			boolean stationary = !cmd.hasOption("nonStationary");
			boolean spanning = cmd.hasOption("spanning");
			boolean printParameters = cmd.hasOption("parameters");
			boolean is_bcDBN = cmd.hasOption("bcDBN");
			boolean is_cDBN = cmd.hasOption("cDBN");
			int intra_ind = Integer.parseInt(cmd.getOptionValue("ind","2"));

			// TODO: check sanity
			int markovLag = Integer.parseInt(cmd.getOptionValue("m", "1"));
			int root = Integer.parseInt(cmd.getOptionValue("r", "-1"));

			Observations o = new Observations(cmd.getOptionValue("i"), markovLag);

			Scores s = new Scores(o, Integer.parseInt(cmd.getOptionValue("p")), stationary, verbose);



			ScoringFunction sf;

		
			if (cmd.hasOption("s") && cmd.getOptionValue("s").equalsIgnoreCase("ll")) {
				sf = new LLScoringFunction();
				//System.out.println(cmd.getOptionValue("s"));
				if (verbose)
					System.out.println("Evaluating network with LL score.");
				s.evaluate(new LLScoringFunction());
			} else {
				sf = new MDLScoringFunction();
				//System.out.println(cmd.getOptionValue("s"));
				if (verbose)
					System.out.println("Evaluating network with MDL score.");
				s.evaluate(new MDLScoringFunction());
			}






			// if (verbose)
			// System.out.println(s);

			DynamicBayesNet dbn;

			if (verbose) {
				if (cmd.hasOption("r"))
					System.out.println("Root node specified: " + root);
				if (spanning)
					System.out.println("Finding a maximum spanning tree.");
				else
					System.out.println("Finding a maximum branching.");
			}


			if(is_bcDBN) {
				System.out.println("Learning bcDBN networks.");
				dbn=s.to_bcDBN(sf,intra_ind);

			}
			
			else {
				
				if(is_bcDBN) {
					System.out.println("Learning cDBN networks.");
					dbn=s.to_cDBN(sf,intra_ind);}
				
				
			else {
				System.out.println("Learning tDBN networks.");
				dbn = s.toDBN(root, spanning);

			}

				
			}


			if (printParameters)
				dbn.learnParameters(o);

			
			
			/*  MY CODE NOW */
			
			
			
			System.out.println("░░░░░░░▄░░░░░░░░░░░░");
			System.out.println("░░░░▄▄░██▓██▓▓▄░░░░░");
			System.out.println("░░░░▓███▓▓██▓██▄▄░░░");
			System.out.println("░░▄█▓██▓▓██▓██▓▓█▄░░");
			System.out.println("░▄▓▓█▓▓██████████▓▄░");
			System.out.println("▀█▓████▓██████████▓▌");
			System.out.println("░▓█▓███▒▒▀░░▀▀█████▌");
			System.out.println("▐█████▀▒▒░░░░▒▒▐████");
			System.out.println("░░▀▀▀▌▒▄▀▄░░░▒▒▐██▌░");
			System.out.println("░░░▒▐▒░▒▓▒░░░▄▀█▀█▌░");
			System.out.println("░░░░▌▒▒░░░░░▌▒▓▐░░░░");
			System.out.println("░░░░▐▒▒▒▒▒▄▄▐░░▐░░░░");
			System.out.println("░░░░░▌▒▄▓▓▒▓▄▒▓░░░░░");
			System.out.println("░░░░░▀▓▐▀▀▀█▒▓░░░░░░");
			System.out.println("░░░░░░▓░▀▄▄▌▓░░░░░░░");
			System.out.println("░░░░░░░▓▓▓▓▓░░░░░░░░");
			
			
			System.out.println(o);
			
			
			System.out.println("Possible Values of each variable:");
			
			System.out.println(dbn.getAttributes());
			
			System.out.println("Transition nets (List of BayesNet objects):");
			
			
			BayesNet initialNet = dbn.getInit(); // initial network: slice 0
			List<BayesNet> TransNets = dbn.getTrans(); // Transition Networks
			
			//System.out.println("Initial Net: " );
			//System.out.println(initialNet.getParameters());
			
			int size = TransNets.size();
			System.out.println("How Many Transition Nets: " + size);
			for (int i = 0; i < size; i++) { 
				System.out.println("Index " + i + ":");
				System.out.println(TransNets.get(i).toString());
				//System.out.println(TransNets.get(i).getParameters().get(1).keySet());
				System.out.println("Only an Example:");
				
				
				// First get is for the Initial Net, second get is for Node
				/*
				for ( Configuration key : initialNet.getParameters().get(0).keySet() ) {
					System.out.println(key);
				    System.out.println(initialNet.getParameters().get(0).get(key));
				}
				*/
				
				
				System.out.println(TransNets.get(0).getParents());
				System.out.println(TransNets.get(i).getParameters().get(0).keySet());
				
				
				// First get is for the Transition Net, second get is for Node belonging to that Transition Net
				for ( Configuration key : TransNets.get(i).getParameters().get(0).keySet() ) {
				    System.out.println(key);
				    System.out.println(TransNets.get(i).getParameters().get(0).get(key));
				    
				}
			
				if (i == (size - 1)) {
			        // Last item...
					
			    }
				
			}
			
			
			
			int[][][] UsefulObservations = o.getObservationsMatrix();
			int MarkovLag = o.getMarkovLag();
			
			System.out.println(" ");
			System.out.println("Number of Transitions: " + o.numTransitions());
			System.out.println("Number of observations: " + o.numObservations(0));
			
			
			List<Attribute> attributesList = o.getAttributes();
			int num_attributes = attributesList.size();
			
			System.out.println("Number of Attributes: " + num_attributes);
			
			System.out.println(attributesList);
			
			System.out.println("MarkovLag = " + MarkovLag);
			System.out.println(" ");
			
			int slice = 0;
			int j = 0;
			for (int i = 0; i < UsefulObservations[0][0].length; i++) { 
				int index = UsefulObservations[0][0][i];
				// index gives the value of a certain attribute (variable) according to the attribute list
				//System.out.println(index);
				//int attribute_timeslice = i - num_attributes*;
				
				// time slice we are currently going trough, because the attributes are the same trough time although at different time slices
				if((i>1) && (i % num_attributes) == 0){
					slice ++;
				}
				
				// Bring the value attribute range
				j = i - slice*num_attributes;
				
				//System.out.println("FOR index: " + j);
				System.out.println(attributesList.get(j).get(index));
				
				
			}
			
			
			//System.out.println(dbn.getTrans());
			
			
			// To obtain the data (Observations) use the usefulObservations matrix:
			// usefulObservations[t][numSubjects[t]][j] = attribute.getIndex(value);
			
			
			
			//////////////////////////////////////////////////////
			/////   OUTLIER DETECTION CODE
			/////
			/////////////////////////////////////////////////////
			
			
			OutlierDetection OD = new OutlierDetection(o , dbn);
			
			//////////////////// THIS AFFECTS THE RESULTS OF THE NEXT PROCEDURES, THE OBSERVATIONS ARE CHANGED !!!!!!!!!!
			
			
			/////// THE DBN IS ALREADY TRAINED WITH THIS INSERTION!!!!!!!!
			
			//int number_outliers = 500; // Should be a certain percentage of the total transitions
			//List<Integer>[] outlier_indexes = OD.Insert_random_TransOutliers(number_outliers); //////// ADD RANDOM OUTLIERS
			
			
			Double[][] scores = OD.Outlierness();  
			
			Integer[][] top_outliers = OD.SortedOutliers(); // First positions of each subject are the most outlier, this returns the indexes
			
			OD.SetThreshold(-4.17);
			
			List<Integer>[]  threshold_outliers = OD.ThresholdOutliers(); // Outputs the outlier's indexes with scores below the threshold
			
			
			///// JUST FOR MORTALITY DATA SET: PRINT THE DETECTED YEARS
			int iter = 0;
			int year = 0;
			for(List<Integer> elem : threshold_outliers){
				iter++;
				if(elem.isEmpty()){
				}else{
					year = iter + 1816 + markovLag - 1;
					System.out.println(year);
				}
				
			}
			System.out.println(iter);
			
			//Double accuracy = OD.Accuracy(threshold_outliers, outlier_indexes, number_outliers); // Outputs the accur37acy of the algorithm according to the randomly generated transition outliers 
			
			
			List<Integer> threshold_subjects = OD.ThresholdSubjects(-7.12, true, "Threshold_Subjects_Output", true); // first True means average score and second to write on a file
			threshold_subjects.removeAll(Collections.singleton(null)); // removes nulls, sometimes they appear
			System.out.println("Number of Subject Outliers: " + threshold_subjects.size());

			
			// Write all the scores to a file:
			try {
				OD.Write_to_csv("Scores_Output"); // The argument is the InputFileName to adapt the output score file name 
			} catch (Exception e2) {
				e2.printStackTrace();
			} 
			
			
			System.out.println("Computing the LogLikelihood score for: ");
			
//			OD.PrintSubject(0, 119);
//			
//			System.out.println(scores[0][119]);
//			OD.PrintSubject(1, 119);
//			System.out.println(scores[1][119]);
//			OD.PrintSubject(2, 119);
//			System.out.println(scores[2][119]);
//			OD.PrintSubject(3, 119);
//			System.out.println(scores[3][119]);
//			OD.PrintSubject(4, 119);
//			System.out.println(scores[4][119]);
			
			
			//OD.PrintSubject(5, 119);
			//System.out.println(scores[5][119]);
			
			
			
			
			/*
			try {
				
				OD.Write_to_csv(threshold_outliers);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			*/
			
			
			////////////////////// FILENAME SHOULD BE AUTOMATIC !!!!!!!!!!!!!!!!!!
			try {
				OD.Write_to_file(threshold_outliers);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			

			
			String output;
			

			if (cmd.hasOption("d")) {
				if (cmd.hasOption("c") && markovLag == 1)
					output = dbn.toDot(true);
				else
					output = dbn.toDot(false);
			} else
				output = dbn.toString(printParameters);

			if (cmd.hasOption("o")) {
				try {
					Utils.writeToFile(cmd.getOptionValue("o"), output);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				if (verbose) {
					System.out.println();
					System.out.println("-----------------");
					System.out.println();
				}
				System.out.println(output);
			}
			
			

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cDBN", options);
			System.out.println(e);
		}

		

		
		
		
}
}





