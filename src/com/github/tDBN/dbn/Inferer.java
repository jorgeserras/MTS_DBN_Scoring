package com.github.tDBN.dbn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.tDBN.dbn.Attribute;
import com.github.tDBN.dbn.BayesNet;
import com.github.tDBN.dbn.Configuration;
import com.github.tDBN.dbn.DynamicBayesNet;

public class Inferer {
	
	// Receives a dbn (trained using the data) and an array "data" containing indexes for the true values in the .csv 
	// If the DBN is non-stationary, the time transition (from 0 to number of time instants) must be supplied in order to retrieve the appropriate transition network
	// Need to solve the problem of predicting the first values (t=0)
	// Outputs the probability of each position in time slice (t+1), i.e: the second half of the data array
	public List<Double> computeInference(DynamicBayesNet dbn, int[] data, int transition){
		
		////////////////////// CHECK IF SIZE OF DATA IS APPROPRIATE FOR THE MARKOV LAG
		
		// Check if the DBN is stationary or not:
		BayesNet tBN;
		if(dbn.transitionNets.size()==1){ // stationary
			tBN = dbn.transitionNets.get(0);
		}else{
			tBN = dbn.transitionNets.get(transition); // Need to retrieve the Transition net for the specific time transition
		}
		
		int nrAtts = dbn.getAttributes().size();
        List<List<Integer>> parentList = tBN.getParents();
        List<Map<Configuration,List<Double>>> paraMapList = tBN.getParameters();
       
       
        List<Configuration> configList = new ArrayList<Configuration>();
        List<List<Double>> valueList = new ArrayList<List<Double>>();
        List<int[]> configArrayList = new ArrayList<int[]>();
       
        for(int i=0; i<nrAtts; i++) {
            Map<Configuration, List<Double>> parentValuesMap = paraMapList.get(i);
            Iterator<Entry<Configuration, List<Double>>> iter = parentValuesMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Configuration, List<Double>> e = iter.next();
                configList.add(e.getKey());
                valueList.add(e.getValue());
                configArrayList.add(e.getKey().getConfiguration());
            }
        }
       
       
        List<Double[]> pepePls = new ArrayList<Double[]>();
        Double auxDouble=0.0;
        for(int i=0; i<configList.size(); i++) {
            Double[] newValues1 = new Double[valueList.get(i).size()+1];
            Double[] newValues = newValues1.clone();
            Arrays.fill(newValues, 0.0);

            for(int j=0; j<valueList.get(i).size(); j++) {
                newValues[j] = valueList.get(i).get(j);
                auxDouble+=valueList.get(i).get(j);
            }
            newValues[valueList.get(i).size()]= 1 - auxDouble;
            pepePls.add(newValues);
            auxDouble=0.0;
        }
       
        int arrayLength = data.length;     
        int aux = 0;
        int[] presentArrayWanted = new int[arrayLength];   
       
        List<Double> retList = new ArrayList<Double>();
        List<Integer> auxList;
        //List<ArrayList<Configuration>> organizedConfig = this.organizeChilds();
       
        int MarkovLag = dbn.getMarkovLag();
        
        int[] indexArray = new int[nrAtts * (MarkovLag + 1)]; // nrAtts * (MarkovLag + 1)
        for(int i = 0; i < nrAtts; i++) {
            aux = presentArrayWanted[i];
            auxList = parentList.get(i);
           
           
            Arrays.fill(indexArray, -1);
            indexArray[i+(nrAtts*MarkovLag)] = 0; // i+(nrAtts*MarkovLag)
            for(int j = 0; j < auxList.size(); j++) {
                indexArray[(int)auxList.get(j)] = data[(int)auxList.get(j)];
            }
            for(int j = 0; j < configArrayList.size(); j++) {
                if(Arrays.equals(configArrayList.get(j), indexArray)) {
                    aux=j;
                    break;
                }
            }
            retList.add(pepePls.get(aux)[data[i+nrAtts]]);
           
        }
        return retList;
		
		
	}
	
	

}
