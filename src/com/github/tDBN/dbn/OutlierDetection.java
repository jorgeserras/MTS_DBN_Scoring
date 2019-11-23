package com.github.tDBN.dbn;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import com.github.tDBN.utils.CSVUtils;
import com.github.tDBN.utils.IndexSorter;

public class OutlierDetection {

	// Make remove outliers from data for better model???
	
	private Double[][] scores;
	
	private Observations observations;
	
	private DynamicBayesNet dbn;
	
	private Double threshold;
	
	private int window_size;
	
	// Constructor for a default OutlierDetection algorithm using an existing DBN and observations
	public OutlierDetection(Observations observations, DynamicBayesNet dbn) {
		this.observations = observations;
		this.dbn = dbn;
		this.threshold = 0.0;
		Double[][] scores_init = new Double[observations.getObservationsMatrix()[0].length - 1][observations.getObservationsMatrix().length]; ////  sem -1 ///// com -1 
		this.scores = scores_init;
		Outlierness(); // This will create the scores for each subject and transition according to the DBN
		
		this.window_size = 0; // window_size must be even AND CHECK WITH THE NUMBER OF VARIABLES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	// Get Method which returns the scores of each subject and transition according to the DBN and LL score
	public Double[][] GetScores(){
		return this.scores;
	}
	
	// Set Method to change the threshold used for the LogLikelihood scoring
	public void SetThreshold(Double threshold){
		this.threshold = threshold;
	}
	
	// Set Method to change the size of the window
	public void SetWindow(int window_size){ // window_size must be even AND CHECK WITH THE NUMBER OF VARIABLES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if ((window_size & 1) == 0 ){
			this.window_size = window_size;
		}else{
			System.out.println("ERROR: The window_size must be an even number");
		}
	}
	
	
	// Auxiliary Method to print the real values (read from .csv) of a certain subject and from a certain transition
	public void PrintSubject(int transition ,int subject_index){
		int aux = transition + 1 ;
		System.out.println(" Subject " + subject_index + " from transition " + transition + " -> " + aux + ": ");
		int slice = 0;
		int j = 0;
		int num_attributes = dbn.getAttributes().size();
		String s = new String();
		for (int i = 0; i < observations.getObservationsMatrix()[transition][subject_index].length; i++) { 
			int index = observations.getObservationsMatrix()[transition][subject_index][i];
			// index gives the value of a certain attribute (variable) according to the attribute list
			//System.out.println(index);
			//int attribute_timeslice = i - num_attributes*;
			
			// time slice we are currently going trough, because the attributes are the same trough time although at different time slices
			if((i>1) && (i % num_attributes) == 0){
				slice ++;
				//System.out.println("Slice"+slice);
			}
			
			// Bring the value attribute range
			j = i - slice*num_attributes;
			//System.out.println("FOR index: " + j);
			s = s + String.valueOf(observations.getAttributes().get(j).get(index) + "; ");
			
		}
		System.out.print(s);
		System.out.print("\n\n");
	}
	
	
	// The integers should index: observations.getObservationsMatrix()[transition][set_of_attributes]
	// Computes the logLikelihhod for a certain array of data containing the values at t and (t+1)
	// transition is the index corresponding to a transition t -> t+1 and set_of_attributes a row of data (from the .csv)
	// Supports stationary and non-stationary DBNs and MarkovLags > 1
	public Double ComputeLogLikelihood(int transition, int set_of_attributes ) {
		
		//System.out.println("Computing the LogLikelihood score for: ");
		//this.PrintSubject(transition, set_of_attributes);
		
		Inferer inferer = new Inferer();
		
		// Probabilities of positions (t+1)
		List<Double> probabilities = inferer.computeInference(dbn, observations.getObservationsMatrix()[transition][set_of_attributes], transition); 
		
		//////////////////////////////////// SMOOTHING FUCTION
		// FOR NOW LETS JUST REPLACE ymin FOR 0.001
		
		Smoothing(probabilities, 0.001);
		
		//////////////////////////////////////
		
		//Double score = Nijk * (Math.log(Nijk) - Math.log(Nij));
		// Compute the LogLikelihhod:
		List<Double> logs = new ArrayList<Double>(); // Stores the Log of the probability of each position (t+1)
		Double score = 0.0;
		for(int i=0; i < probabilities.size(); i++){
			logs.add(Math.log(probabilities.get(i)));
			score = score + logs.get(i); /////////// estava += , wrong
		}
		
		return score; // A score of 0 is perfect, the less negative the better
	}
	
	
	
	// Smoothes variable probabilities according to their alphabet size and parameter ymin, normally ymin = 0.001
	// Receives a List of the probabilities of each variable at time slice (t+1) and parameter ymin
	public void Smoothing(List<Double> probabilities, Double ymin){
		int alphabet_size;
		for(int i=0; i < probabilities.size(); i++){ 
			//if(probabilities.get(i)==0){
			alphabet_size = dbn.getAttributes().get(i).size(); // i gives the index of the variable at slice (t+1)
			probabilities.set(i, (1 - alphabet_size * ymin)* probabilities.get(i) + ymin );
			//}
		}
	}
	
	
	
	
	// Returns a 2D Matrix with the LogLikelihhod Score (Outlierness) of each time slice of every subject according to a DBN
	// First position is the transition (t -> t+1), the second is the subject (.csv row). The LogLikelihood score of that transition (t+1 positions) is stored.
	
	//////////////////////////////////////////////////////////////////////////////////// FUTURE: INITIAL NET !!!!!!
	
	// Supports stationary and non-stationary DBNs and MarkovLags > 1
	public Double[][] Outlierness() {
		
		int n_subjects = observations.getObservationsMatrix()[0].length - 1;// number_of_subjects
		int n_transitions = observations.getObservationsMatrix().length;// number_of_transitions
		Double[][] scores = new Double[n_transitions][n_subjects]; // Array of scores for each Subject (row of .csv)
		
		//System.out.println("Number of subjects: " + n_subjects);
		//System.out.println("Number of transitions: " + n_transitions);
		
		for(int j=0; j < n_transitions; j++){ // For Transitions
			
			for(int i=0; i < n_subjects; i++){ // For Subjects
				Double score = ComputeLogLikelihood(j, i); // Transition = j, subject i (.csv row)
				scores[j][i] = score;
			}
			
		}
		
		this.scores = scores;
		return(scores);
		
	}
	
	
	////////////////////////////////////////////////////////////////////////// OUTLIERNESS IN THESE METHODS ARE ONLY COMPUTED FOR (t=1) POSITIONS AND NOT t=0 !!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	// Outputs the Sorted Outliers in the Observations according to the LogLikelihood Score for each subject
	// Outputs the positions of the outliers in the observations and not their score, must index the score matrix for that result
	// The scores used are the ones stored in the object. To update them call method Outlierness(), by default, the scores were computed in the constructor
	public Integer[][] SortedOutliers() {
		
		Integer[][] sorted_indexes = new Integer[this.scores.length][this.scores[0].length-1];
		
		for(int i = 0; i < this.scores.length ;i++){
			
			IndexSorter<Double> is = new IndexSorter<Double>(this.scores[i]);
			is.sort();
			
			sorted_indexes[i] = is.getIndexes();
		}
		
		return sorted_indexes;
		
	}
	
	
	// Outputs the Outlier's positions for each subject below the object's threshold according to the LogLikelihood Score
	// First List is for the subject while the array (inside each list) store the indexes of the transitions with outlierness above the threshold
	// The scores used are the ones stored in the object. To update them call method Outlierness(), by default, the scores were computed in the constructor
	public List<Integer>[] ThresholdOutliers() {
			
		Integer[][] sorted_indexes = SortedOutliers();
			
		//List<List<Integer>> threshold_indexes = new ArrayList<List<Integer>>(this.scores.length);
		
		List<Integer>[] threshold_indexes = new List[this.scores.length];
		for (int i = 0; i < this.scores.length; i++) {
			threshold_indexes[i] = new ArrayList<>();
		}
			
		for(int i=0; i < this.scores.length; i++){ // For each subject
				
			for(int j=0; j < this.scores[0].length; j++){ // For each transition
					
				if(this.scores[i][sorted_indexes[i][j]] < threshold){ // Most outlier first (for each subject)
						threshold_indexes[i].add(sorted_indexes[i][j]); // Add the index of the position of that subject
				}else{
					break; // Since the scores are sorted, we are sure we will not find transitions with worse score
				}
			}
		}
		return threshold_indexes;
			
	}

	// Outputs the Subjects that have an outlier score above the specified threshold (argument)
	// A Subject Outlierness is the sum of all the transition scores belonging to that subject
	// If average=true, the average score of each subject is compared, otherwise the sum of the scores is used
	// If filewrite is true, the scores selected are written to a .csv file with a name adapted from InputFileName
	public List<Integer> ThresholdSubjects(Double Subject_threshold, boolean average, String InputFileName, boolean filewrite) {
		
		List<Integer> subject_indexes = new ArrayList<Integer>();
		
		//System.out.println(scores.length);
		//System.out.println(scores[0].length);
		
		Double[] subject_scores = new Double[scores[0].length];
		
		Double aux = 0.0; // Computes the sum of all the transitions scores for each subject
		Double mean = 0.0;
		for(int subject=0; subject < scores[0].length; subject++){
			aux = 0.0;
			for(int transition=0; transition < scores.length; transition++){
				aux = aux + scores[transition][subject];
			}
			mean = aux/scores.length;
			if(average){
				subject_scores[subject] = mean;
			}else{
				subject_scores[subject] = aux;
			}
			if(mean < Subject_threshold && average){
				subject_indexes.add(subject); // Save the subject that has an high outlier score
			}else{
				if(aux < Subject_threshold && !average){
					subject_indexes.add(subject); // Save the subject that has an high outlier score
				}
			}
		}
		if(filewrite){
			try {
				Write_SubjectScores_to_csv(subject_scores, InputFileName, average);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return subject_indexes;
	}
	
	
	
	
	// Write all the scores for every attribute from every transition of every subject, in the same format as input data .csv
	public void Write_to_csv(String InputFileName) throws Exception{
		
		
		
		
		
//		try (
//	            Writer writer = Files.newBufferedWriter(Paths.get(InputFileName));
//
//	            CSVWriter csvWriter = new CSVWriter(writer,
//	                    CSVWriter.DEFAULT_SEPARATOR,
//	                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
//	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//	                    CSVWriter.DEFAULT_LINE_END);
//	        ) {
//	            String[] headerRecord = {"Name", "Email", "Phone", "Country"};
//	            csvWriter.writeNext(headerRecord);
//
//	            csvWriter.writeNext(new String[]{"Sundar Pichai ♥", "sundar.pichai@gmail.com", "+1-1111111111", "India"});
//	            csvWriter.writeNext(new String[]{"Satya Nadella", "satya.nadella@outlook.com", "+1-1111111112", "India"});
//	        }
//		
		
		
        // Name of the output mixed file
        String scores_filename = new String("");
        scores_filename = "scores_" + InputFileName + ".csv";
		
        
        List<String> line = new ArrayList<String>();
        
        // This is the construction of the header:
        line.add("subject_id");
        for(int j=0; j < this.scores.length; j++){ // for each transition index
        	line.add("t_" + String.valueOf(j));
        }
        

        // Clear the output file if it already exists before proceeding
        PrintWriter writer = new PrintWriter(scores_filename);
        writer.print("");
        writer.close();
        
        // FilePath to put the scores:
        String outlierfilepath = Paths.get(scores_filename).toAbsolutePath().toString();
        
        FileWriter owriter = new FileWriter(outlierfilepath);
        
        
        // Write the header:
        CSVUtils.writeLine(owriter,line, ',', '"', false); ////////// ISTO ESTÁ CORRECTO ?????
        line.clear();
        
        //Build the line to write on output file according to the scores matrix
        for(int subject = 0; subject < this.scores[0].length; subject++){
        	line.add(String.valueOf(subject));
        	for(int transition = 0; transition < this.scores.length; transition++){	
        		line.add(String.valueOf(this.scores[transition][subject]));
        	}
        	CSVUtils.writeLine(owriter,line, ',', '"', false); ////////// ISTO ESTÁ CORRECTO ?????
        	line.clear();
        }
        owriter.flush();
        owriter.close();
        
        //System.out.println("Scores file: " + scores_filename + " written");

		
	}
	
	// Write all the subject scores, in the same format as input data .csv
	// The boolean is only to affect the name of the file to write
		public void Write_SubjectScores_to_csv(Double[] subject_score, String InputFileName, boolean average) throws Exception{
			
			
	        // Name of the output mixed file
	        String scores_filename = new String("");
	        scores_filename = "subject_scores_" + InputFileName + ".csv";
			
	        
	        List<String> line = new ArrayList<String>();
	        
	        // This is the construction of the header:
	        line.add("subject_id");
	        line.add("score");
	        
	        
	        // Clear the output file if it already exists before proceeding
	        PrintWriter writer = new PrintWriter(scores_filename);
	        writer.print("");
	        writer.close();
	        
	        // FilePath to put the scores:
	        String outlierfilepath = Paths.get(scores_filename).toAbsolutePath().toString();
	        
	        FileWriter owriter = new FileWriter(outlierfilepath);
	        
	        
	        // Write the header:
	        CSVUtils.writeLine(owriter,line, ',', '"', false); ////////// ISTO ESTÁ CORRECTO ?????
	        line.clear();
	        
	        //Build the line to write on output file according to the scores matrix
	        for(int subject = 0; subject < subject_score.length; subject++){
	        	line.add(String.valueOf(subject));

	        	line.add(String.valueOf(subject_score[subject]));

	        	CSVUtils.writeLine(owriter,line, ',', '"', false); ////////// ISTO ESTÁ CORRECTO ?????
	        	line.clear();
	        }
	        owriter.flush();
	        owriter.close();
	        
	        //System.out.println("Subject Scores file: " + scores_filename + " written");
	
		}
	
	
	// Used to write the time stamps that are outliers for each subject (row) according to some criteria
	
	public static void Write_to_file(List<Integer>[] indexes) throws Exception{
		
		// http://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/
		
        String FILENAME = "threshold_ECG_m2_-15_alphabet4_ns.txt";
        
        
        BufferedWriter bw = null;
		FileWriter fw = null;
		
		try {

			String content = "index\n";
			
			for(int i=0; i < indexes.length; i++){ // For each transition
	        	List<String> newList = new ArrayList<String>(indexes[i].size());
	        	for (Integer myInt : indexes[i]) { 
	        		newList.add(String.valueOf(myInt)); 
	        	}
	        	
	            //for(int j=0; j < indexes[i].size(); j++){ // For each transition
	            if(newList.isEmpty()){
	            	content = content + "NA\n";  // "NA" means that the subject has no threshold outliers
	            }else{
		        	StringBuilder sb = new StringBuilder();
		        	for (String s : newList)
		        	{
		        	    sb.append(s);
		        	    sb.append(",");
		        	}
		        	sb.append("\n");
	            	content = content + sb.toString();	// Line index indicates the subject and the values the time stamps that are threshold outliers
	            }
	            
	            //}
	        }

			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			bw.write(content);

			//System.out.println("Done writing on file");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}

	
	// This will change the original Observations object and returns the matrix of the locations of each affected transition
	// Affects the attributes that are generated at a transition (window), at time t (rightmost attributes)
	
		public List<Integer>[] Insert_random_TransOutliers(int how_many){
			// Need Attributes indexes, need to return the positions affected
			
			Observations original = this.observations; // This will change the observations object 
			
			// First index is the transition and the others the subjects
			List<Integer>[] outlier_indexes = new List[original.getObservationsMatrix().length];
			for (int i = 0; i < this.scores.length; i++) {
				outlier_indexes[i] = new ArrayList<>();
			}
			
			
			List<Integer>[] threshold_indexes = new List[this.scores.length];
			Random rand = new Random();
			
			int MarkovLag = this.dbn.getMarkovLag();
			int t = 0;
			int s = 0;
			int v = 0;
			int variable_size = 0;
			int old_value = 0;
			boolean flag = true; // used to make sure the random values are not equal to the original ones
			int[] new_values =  new int[original.getObservationsMatrix()[0][0].length]; // will store the new attribute indexes
			
			for(int i=0; i < how_many; i++){
				t = rand.nextInt(original.getObservationsMatrix().length); // random number between 0 and argument for transition
				s = rand.nextInt(original.getObservationsMatrix()[0].length); // random number between 0 and argument for subject
				
				outlier_indexes[t].add(s); // The subjects added have that specific transition as an outlier
				new_values = original.getObservationsMatrix()[t][s].clone();
				
				for(int j = 0; j < this.dbn.getAttributes().size(); j++){ // for each attribute
					variable_size = this.dbn.getAttributes().get(j).size();
					old_value = original.getObservationsMatrix()[t][s][j+MarkovLag*this.dbn.getAttributes().size()]; // we only want to change the rightmost attributes
							
					while(flag){
						v = rand.nextInt(variable_size); // going to be the new value
						if(v!=old_value){
							new_values[j+MarkovLag*this.dbn.getAttributes().size()] = v; // change one of the rightmost attributes to the new random value
							flag=false;
						}
					}
					flag=true;
					
				}
				
				original.SetObservationsMatrix(t, s, new_values);
				
			}
				
			return outlier_indexes;
		}
	
		
		
	// Will compute the accuracy of the outlier detection method according to randomly generated outliers in known transitions
		
	public Double Accuracy(List<Integer>[]  threshold_outliers, List<Integer>[] outlier_indexes, int how_many){
		Double accuracy = 0.0;
		
		int hit = 0;
		int others = 0; // Detected Outliers that were not inserted
		for(int t=0; t < threshold_outliers.length; t++){ //transition
			for(int s : threshold_outliers[t]){
				if(outlier_indexes[t].contains(s)){ // If the transition was detected for that subject
					hit++;
				}else{
					others++;
				}
			}
		}
		accuracy = ((double)hit/(double)how_many);	// Percentage of correctly detected transition outliers	
		
		int false_positive = others;
		
		int total_detected = hit + others;
		
		System.out.println("Total number of Outlier Transitions detected: " + total_detected);
		System.out.println("Number of Outliers that were inserted: " + how_many);
		System.out.println("Number of Outliers inserted detected: " + hit);
		System.out.println("Number of False Positives: " + false_positive);
		System.out.println("Threshold used: " + this.threshold);
		System.out.println("Accuracy: " + accuracy*100 + "%");
				
		return accuracy;
	}
	
	
	// Will preform Outlier Detection for every data stored in Observations using the dbn and parameters of the object.
	// Returns a List of the windows with more outlierness
	/*
	public List<Double> DetectOutliers() {
		
		
		return score;
	}
	
	*/
	
	@Override
	public String toString() {
		return "TODO"; // Maybe return the parameters being used
	}
	
	
}
