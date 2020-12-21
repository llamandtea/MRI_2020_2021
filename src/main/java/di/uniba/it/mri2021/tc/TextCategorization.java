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
    
    Map<String, Map<String, Integer>> confusionMatrix = new HashMap<String, Map<String, Integer>>();

    public abstract void train(List<DatasetExample> trainingset) throws IOException;

    public abstract List<String> test(List<DatasetExample> testingset) throws IOException;

    public Map<String, Map<String, Integer>> generateConfusionMatrix(List<DatasetExample> testingset, List<String> predictions)
            throws IllegalArgumentException {
        
        // se è stata già creata una matrice, ne viene costruita una nuova
        if (confusionMatrix != null) {
            
            confusionMatrix = new HashMap<String, Map<String, Integer>>();
        }
        
        if (testingset.size() != predictions.size()) {
            throw new IllegalArgumentException("Incompatible predictions");
        }
        
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
    
    public double accuracy(List<String> predictions) throws IllegalArgumentException {
        
        double correct = 0;
        for (String cat : confusionMatrix.keySet()) {
            
            correct += confusionMatrix.get(cat).get(cat);
        }
        
        if (confusionMatrix.keySet().isEmpty()) {
            
            return correct;
        } else {
            return correct / (double) predictions.size();
        }
    }
    
        public double getMicroPrecision() {
            
            double truePositives = 0;
            double falsePositives  = 0;
            for (String cat : confusionMatrix.keySet()) {
                
                // Recupero dei veri positivi
                truePositives += (confusionMatrix.get(cat).get(cat) != null) ? confusionMatrix.get(cat).get(cat) : 0d;
                
                // Recupero dei falsi positivi
                for (String c : confusionMatrix.keySet()) {
                    
                    if (!c.equals(cat) && confusionMatrix.get(c).get(cat) != null) {
                        
                            falsePositives += confusionMatrix.get(c).get(cat);
                        }
                    }
                }
                
                return truePositives / (truePositives + falsePositives);
        }
        
        public double getMicroRecall() {
            
            double truePositives = 0d;
            double falseNegatives = 0d;
            for (String cat : confusionMatrix.keySet()) {
                
                // Recupero dei veri positivi
                truePositives += (confusionMatrix.get(cat).get(cat) != null) ? confusionMatrix.get(cat).get(cat) : 0d;
                
                // Recupero dei falsi negativi
                for (String c : confusionMatrix.get(cat).keySet()) {
                    
                    if (!c.equals(cat)) {
                        
                        falseNegatives += confusionMatrix.get(cat).get(c);
                    }
                }
            }
            
            return truePositives / (truePositives + falseNegatives);
        }

        public double getMacroPrecision() {
            
            double num = 0d;
            
            for (String cat : confusionMatrix.keySet()) {
                
                double currPrecision = 0d;
                double denom = 0d;
                
                currPrecision += (confusionMatrix.get(cat).get(cat) != null) ? confusionMatrix.get(cat).get(cat) : 0;
                for (String c : confusionMatrix.keySet()) {
                    
                    if(!c.equals(cat)) {
                        
                        denom += (confusionMatrix.get(c).get(cat) != null) ? confusionMatrix.get(c).get(cat) : 0;
                    }
                }
                currPrecision = currPrecision / (currPrecision + denom);
                num += currPrecision;
            }
            
            return num / confusionMatrix.keySet().size();
        }
        
        public double getMacroRecall() {
            
            double num = 0d;
            
            for (String cat : confusionMatrix.keySet()) {
                
                double currRecall = 0d;
                double denom = 0d;
                
                currRecall += (confusionMatrix.get(cat).get(cat) != null) ? confusionMatrix.get(cat).get(cat) : 0;
                for (String c : confusionMatrix.get(cat).keySet()) {
                    
                    if(!c.equals(cat)) {
                        
                        denom += (confusionMatrix.get(c).get(cat) != null) ? confusionMatrix.get(c).get(cat) : 0;
                    }
                }
                currRecall = currRecall / (currRecall + denom);
                num += currRecall;
            }
            
            return num / confusionMatrix.keySet().size();
        }
        
        public double microFMeasure() {
            
            double precision = getMicroPrecision();
            double recall = getMicroRecall();
            return ((2 * precision * recall) / (precision + recall));
        }
        
        public double microFMeasure(double boost) {
            
            double precision = getMicroPrecision();
            double recall = getMicroRecall();
            return (((1 + Math.pow(boost, 2))) * precision * recall) / ((Math.pow(boost, 2) * precision) + recall);
        }
        
        public double macroFMeasure() {
            
            double precision = getMacroPrecision();
            double recall = getMacroRecall();
            return ((2 * precision * recall) / (precision + recall));
        }
        
        public double macroFMeasure(double boost) {
            
            double precision = getMacroPrecision();
            double recall = getMacroRecall();
            return (((1 + Math.pow(boost, 2))) * precision * recall) / ((Math.pow(boost, 2) * precision) + recall);
        }
}
