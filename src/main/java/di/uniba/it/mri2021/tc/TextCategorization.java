/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.tc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public abstract class TextCategorization {

    public abstract void train(List<DatasetExample> trainingset) throws IOException;

    public abstract List<String> test(List<DatasetExample> testingset) throws IOException;

    public Map<String, Map<String, Integer>> getConfusionMatrix(List<DatasetExample> testingset, List<String> predictions)
            throws IllegalArgumentException {
        
        if (testingset.size() != predictions.size()) {
            throw new IllegalArgumentException("Incompatible predictions");
        }
        
        
        Map<String, Map<String, Integer>> confusionMatrix = new HashMap<String, Map<String, Integer>>();
        int size = predictions.size();
        DatasetExample e = null;
        for (int i = 0; i < size; i++) {
            
            e = testingset.get(i);
            if (!confusionMatrix.containsKey(e.getCategory())) {
                
                confusionMatrix.put(e.getCategory(), new HashMap<String, Integer>());
            }
            
            Integer currValue = confusionMatrix.get(e.getCategory()).get(predictions.get(i));
            if (currValue == null) {
                
                currValue = 0;
            }
            confusionMatrix.get(e.getCategory()).put(predictions.get(i), currValue + 1);
        }
        
        return confusionMatrix;
    }
    
    public double accuracy(List<DatasetExample> testingset, List<String> predicted) throws IllegalArgumentException {
        if (testingset.size() != predicted.size()) {
            throw new IllegalArgumentException("Incompatible predictions");
        }
        double correct = 0;
        for (int i = 0; i < predicted.size(); i++) {
            if (predicted.get(i).equals(testingset.get(i).getCategory())) {
                correct++;
            }
        }
        if (predicted.isEmpty()) {
            return correct;
        } else {
            return correct / (double) predicted.size();
        }
    }
    
    

}
