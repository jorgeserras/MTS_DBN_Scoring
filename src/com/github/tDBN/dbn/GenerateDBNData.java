package com.github.tDBN.dbn;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.github.tDBN.utils.CSVUtils;
import com.github.tDBN.utils.Edge;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;




public class GenerateDBNData {
	
	/**
	 * Update CSV by row and column
	 * 
	 * @param fileToUpdate CSV file path to update e.g. D:\\chetan\\test.csv
	 * @param replace Replacement for your cell value
	 * @param row Row for which need to update 
	 * @param col Column for which you need to update
	 * @throws IOException
	 */
	public static void updateCSV(String fileToUpdate, String replace,
	    int row, int col) throws IOException {

	File inputFile = new File(fileToUpdate);

	// Read existing file 
	CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
	List<String[]> csvBody = reader.readAll();
	// get CSV row column  and replace with by using row and column
	csvBody.get(row)[col] = replace;
	reader.close();

	// Write to CSV file which is open
	CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
	writer.writeAll(csvBody);
	writer.flush();
	writer.close();
	}
	
	
	
	   
	   
    public static void main(String[] args) throws IOException {
    	
    	///// Parameters:
    	int size = 10; // T
    	String Outlier_DBN_name = new String("B");
        int n_subjects = 100;
        int outlier_percentage = 20;
        
        
        
        int nrOutliers = (int)(n_subjects*outlier_percentage)/100; // Number of Outliers to mix
        int nrObs = n_subjects-nrOutliers;
        
        System.out.println("Normal Subjects: " + nrObs + " Outlier Subjects: " + nrOutliers);
        
        // Name of the output mixed file
        String mixed_filename = new String("");
        mixed_filename = "artificial_" + Outlier_DBN_name + "_" + Integer.toString(outlier_percentage) + "_" + Integer.toString(n_subjects);
        
        // nrObs + nrOutliers = final size
        // Outliers will always be at the end (for now), but this information is never used
       
       
        Attribute a1 = new NominalAttribute();
        a1.setName("X1");
        a1.add("a");
        a1.add("b");
        a1.add("c");
 
        Attribute a2 = new NominalAttribute();
        a2.setName("X2");
        a2.add("a");
        a2.add("b");
        a2.add("c");
 
        Attribute a3 = new NominalAttribute();
        a3.setName("X3");
        a3.add("a");
        a3.add("b");
        a3.add("c");
       
        Attribute a4 = new NominalAttribute();
        a4.setName("X4");
        a4.add("a");
        a4.add("b");
        a4.add("c");
 
        Attribute a5 = new NominalAttribute();
        a5.setName("X5");
        a5.add("a");
        a5.add("b");
        a5.add("c");
 
//        Attribute a6 = new NumericAttribute();
//        a6.setName("X6");
//        a6.add("0");
//        a6.add("1");
//       
//        Attribute a7 = new NumericAttribute();
//        a7.setName("X7");
//        a7.add("0");
//        a7.add("1");
//       
//        Attribute a8 = new NumericAttribute();
//        a8.setName("X8");
//        a8.add("0");
//        a8.add("1");
//       
//        Attribute a9 = new NumericAttribute();
//        a9.setName("X9");
//        a9.add("0");
//        a9.add("1");
//       
//        Attribute a10 = new NumericAttribute();
//        a10.setName("X10");
//        a10.add("0");
//        a10.add("1");
//       
//        Attribute a11 = new NominalAttribute();
//        a11.setName("X11");
//        a11.add("yes");
//        a11.add("no");
// 
//        Attribute a12 = new NumericAttribute();
//        a12.setName("X12");
//        a12.add("10");
//        a12.add("20");
// 
//        Attribute a13 = new NumericAttribute();
//        a13.setName("X13");
//        a13.add("0");
//        a13.add("1");
//       
//        Attribute a14 = new NominalAttribute();
//        a14.setName("X14");
//        a14.add("yes1");
//        a14.add("no1");
// 
//        Attribute a15 = new NumericAttribute();
//        a15.setName("X15");
//        a15.add("101");
//        a15.add("201");    
// 
//        Attribute a16 = new NumericAttribute();
//        a16.setName("X16");
//        a16.add("0");
//        a16.add("1");
//       
//        Attribute a17 = new NumericAttribute();
//        a17.setName("X17");
//        a17.add("0");
//        a17.add("1");
//       
//        Attribute a18 = new NumericAttribute();
//        a18.setName("X18");
//        a18.add("0");
//        a18.add("1");
//       
//        Attribute a19 = new NumericAttribute();
//        a19.setName("X19");
//        a19.add("0");
//        a19.add("1");
//       
//        Attribute a20 = new NumericAttribute();
//        a20.setName("X20");
//        a20.add("0");
//        a20.add("1");
 
       
       // Criar lista de atributos:
        List<Attribute> a = Arrays.asList(a1,a2,a3,a4,a5);
//        List<Attribute> b = Arrays.asList(a1,a2,a3,a4,a5);
       
       
        
        ////////////////////////////////////////////////// THIS WAS THE FIRST ONE
//        Edge e01 = new Edge(0,1);
//        Edge e12 = new Edge(1,2);
//        Edge e23 = new Edge(2,3);
//        Edge e34 = new Edge(3,4);
//        
//        
//        Edge e00 = new Edge(0,0);
//        Edge e11 = new Edge(1,1);
//        Edge e22 = new Edge(2,2);
//        Edge e33 = new Edge(3,3);
//        Edge e44 = new Edge(4,4);
//        Edge e41 = new Edge(4,1); // intra
//       
//        //// Outlier DBN edges:
//        
        ///////////////////////////////// NAO SEI SE ME ENGANEI AQUI BTW:
        
        ///// OUTLIER NETWORK B INTRA:
        Edge eo02 = new Edge(0,2);
        Edge eo24 = new Edge(2,4);
        Edge eo30 = new Edge(3,0);
//        
//        Edge eo11 = new Edge(1,1);
//        Edge eo33 = new Edge(3,3);
        
        
        
        
        
        
        Edge e01 = new Edge(0,1);
        Edge e12 = new Edge(1,2);
        Edge e23 = new Edge(2,3);
        Edge e34 = new Edge(3,4);
        
        
        Edge e00 = new Edge(0,0);
        Edge e11 = new Edge(1,1);
        Edge e22 = new Edge(2,2);
        Edge e33 = new Edge(3,3);
        Edge e44 = new Edge(4,4);
        Edge e41 = new Edge(4,1); // intra
       
        //// Outlier DBN edges:
        
        Edge eo00 = new Edge(0,0);
        Edge eo11 = new Edge(1,1);
        
        Edge eo33 = new Edge(3,3);
        Edge eo44 = new Edge(4,4);
        // NO INTRA
        
        
       
//        Edge f10 = new Edge(1,0);
//        Edge f21 = new Edge(2,1);
//        Edge f32 = new Edge(3,2);
//        Edge f43 = new Edge(4,3);
//        Edge f41 = new Edge(4,1);
//        Edge f12 = new Edge(1,2);
//        Edge f23 = new Edge(2,3);
//        Edge f34 = new Edge(3,4);
//        Edge f01 = new Edge(0,1);
//        Edge f14 = new Edge(1,4);
       
       // Criar a prior network:
        List<Edge> prior = Arrays.asList(e01,e12,e23,e34);
        
        List<Edge> prior_outlier = Arrays.asList(e01,e12,e23,e34);
        
//        List<Edge> prior2 = Arrays.asList(f10,f21,f32,f43);
       
        BayesNet b0 = new BayesNet(a, prior);
        
        BayesNet b_outlier = new BayesNet(a, prior_outlier);
        
//        BayesNet b1 = new BayesNet(b, prior2);
       
//        b1.generateParameters();
        b0.generateParameters();
        b_outlier.generateParameters();
       
       // Criar a transition network:
        List<Edge> inter = Arrays.asList(e00, e11, e22, e33, e44);
        List<Edge> intra = Arrays.asList(e41);
        
        // Criar a transition network outlier:
///////// NETWORK AAAAAAAAAAAAAAAA:
//        List<Edge> inter_outlier = Arrays.asList(eo00, eo11, eo33, eo44);
//        List<Edge> intra_outlier = Arrays.asList();
        
///////// NETWORK BBBBBBBBBBBBBBBB:
        List<Edge> inter_outlier = Arrays.asList(eo11, eo33);
        List<Edge> intra_outlier = Arrays.asList(eo02, eo24, eo30);
        
       
       
//        List<Edge> inter2 = Arrays.asList(f01, f12, f23, f34, f41);
//        List<Edge> intra2 = Arrays.asList(f14);
 
        BayesNet bt = new BayesNet(a, intra, inter);
        bt.generateParameters();
       
        BayesNet bt_outlier = new BayesNet(a, intra_outlier, inter_outlier);
        bt_outlier.generateParameters();
        
//        BayesNet bt2 = new BayesNet(b, intra2, inter2);
//        bt2.generateParameters();
       
       
       
        List<BayesNet> btList = new ArrayList<BayesNet>();
        for(int i = 0; i < size; i++) {
            btList.add(bt);
        }
        
        List<BayesNet> btList_outlier = new ArrayList<BayesNet>();
        for(int i = 0; i < size; i++) {
        	btList_outlier.add(bt_outlier);
        }
       
//        List<BayesNet> btList2 = new ArrayList<BayesNet>();
//        for(int i = 0; i < size; i++) {
//            btList2.add(bt2);
//        }
//       
       // Para gerar a DBN estacionária com prior network b0 e transition network bt, considerando T=length(btList):
        DynamicBayesNet dbn1 = new DynamicBayesNet(a, b0, btList); 
        
        DynamicBayesNet dbn_outlier = new DynamicBayesNet(a, b_outlier, btList_outlier); 
        
//        DynamicBayesNet dbn2 = new DynamicBayesNet(b, b1, btList2);
       
       // Para gerar nrObs observações:
        Observations o = dbn1.generateObservations(nrObs);
        
        Observations outliers = dbn_outlier.generateObservations(nrOutliers);
        
        
//        Observations o2 = dbn2.generateObservations(nrObs);
        
        String normal_data_only = new String("normal_" + Outlier_DBN_name + "_" + outlier_percentage + "_" + Integer.toString(nrObs));
        
        o.writeToFile(normal_data_only); // escrever dados para ficheiro
        
//        o2.writeToFile("output2");        
        
        outliers.writeToFile("outlier_data"); // escrever dados para ficheiro
        
        
        ///////// MIX THE RESULTS OF BOTH NETWORKS AND WRITE THEM TO A FILE:
       /// Subject_Ids for the second file must be updated to continue where the first file ended
        
                
        
        // Charset for read and write
        Charset charset = StandardCharsets.UTF_8;
        
        
        
        // Input files
        List<Path> inputs = Arrays.asList(
                Paths.get(normal_data_only),
                Paths.get("outlier_data")
        );
        
        //String str = new String("SOME");

        // Output file
        Path output = Paths.get(mixed_filename);
        // Clear the output file if it already exists before proceeding
        PrintWriter writer = new PrintWriter(mixed_filename);
        writer.print("");
        writer.close();



        // Join files (lines)
        int i = 1;
        for (Path path : inputs) {
        	
//        	 BufferedReader reader = new BufferedReader(new FileReader(path));
//        	 reader.readLine(); // this will read the first line
//        	 String line1=null;
//        	 while ((line1 = reader.readLine()) != null){ //loop will run from 2nd line
//        	        //some code
//        	 }
        	
            List<String> lines = Files.readAllLines(path, charset);
            
            if(i == 2){ // Remove first line of the second file (because thats another header)
            	Iterator<String> iter = lines.listIterator(); 
            	iter.next();
            	iter.remove();
            }
            
            i = i + 1; // aux variable to check if it is the second file or not
            Files.write(output, lines, charset, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        }
        
        
        
        
        /// Subject_Ids for the second file must be updated to continue where the first file ended
        
        String outlierfilepath = Paths.get(mixed_filename).toAbsolutePath().toString();
        
        FileWriter owriter = new FileWriter(outlierfilepath + ".csv");
        
        List<String> filelines = Files.readAllLines(Paths.get(mixed_filename), charset);
        
        for(String line : filelines){
        	CSVUtils.writeLine(owriter, Arrays.asList(line), ',', ' ', false);
        }
        owriter.flush();
        owriter.close();
        
        
        for(i = nrObs+1; i < nrOutliers+nrObs+1; i++){
        	updateCSV(Paths.get(mixed_filename + ".csv").toAbsolutePath().toString(), Integer.toString(i-1), i, 0);
        }
        
        
        System.out.println("Outputs written");
    }
 
   
   
}
