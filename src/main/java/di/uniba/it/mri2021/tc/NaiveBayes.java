/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.tc;

import di.uniba.it.mri2021.rocchio.BoW;
import di.uniba.it.mri2021.rocchio.BoWUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author pierpaolo
 */
public class NaiveBayes extends TextCategorization {

    // Map contenente le probabilità stimate che un documento appartenga
    // a una categoria
    Map<String, Float> categoryProbability = new HashMap<String, Float>();
    
    // Map contenente l'insieme delle probabilità di una parola del vocabolario di
    // comparire all'interno di una categoria
    Map<String, HashMap<String, Float>> termCategoryProb = new HashMap<String, HashMap<String, Float>>();
    
    Set<String> vocabulary;
    
    @Override
    public void train(List<DatasetExample> trainingset) throws IOException {
        
        Map<String, BoW> categoryDocuments = new HashMap<String, BoW>();
        vocabulary = new HashSet<String>(trainingset.size());
        
        int c = 0;
        for (DatasetExample d : trainingset) {
            
            /*
                Avvaloro tutti gli elementi di categoryProbability con il numero di documenti
                che contengono nel dataset.
            */
            if (!categoryProbability.keySet().contains(d.getCategory())) {
                
                categoryProbability.put(d.getCategory(), 1f);
                categoryDocuments.put(d.getCategory(), new BoW());
            } else {
                
                categoryProbability.put(d.getCategory(), categoryProbability.get(d.getCategory()) + 1);
                
                // Le BoW memorizzano la frequenza dei termini secondo il CSVReader, quindi sommarle
                // equivale a concatenare i docmenti.
                categoryDocuments.put(d.getCategory(), BoWUtils.add(categoryDocuments.get(d.getCategory()), d.getBow()));
            }
            for (String w : d.getBow().getWords()) {
                 
               vocabulary.add(w);
            }
            
            c++;
            if (c % 100 == 0) {
                System.out.println("Trained on: " + c + " /" + trainingset.size());
            }
        }
        for (String cat : categoryProbability.keySet()) {
                
        // Aggiorno la map dlle probabilità dividendo il numero dei documenti per quello dei documenti totali
            categoryProbability.put(cat, categoryProbability.get(cat) / trainingset.size());
        }

        // freq e totalWords sono Float perché le frequenze nelle BoW sono memorizzate così
        Float freq;
        Float totalWords;

        // Viene avviata la fase finale di training: per ogni parola nel vocabolario
        // viene memorizzata una lista di coppie  <categoria, probabilità>
        c = 0;
        for (String word : vocabulary) {

            termCategoryProb.put(word, new HashMap<String, Float>(categoryDocuments.keySet().size()));
            Map<String, Float> prob = termCategoryProb.get(word);

            for (String cat : categoryDocuments.keySet()) {

                // viene applicata la correzione di Laplace durante l'avvaloramento di freq
                freq = categoryDocuments.get(cat).getWeight(word);
                freq = ((freq == null) ? 1f : freq + 1);

                totalWords = (float) vocabulary.size();
                for (String w : categoryDocuments.get(cat).getWords()) {

                    totalWords += categoryDocuments.get(cat).getWeight(w);
                }

                // Aggiunta della coppia <Categoria, probabilità> all'interno della Map che si
                // sta costruendo
                prob.put(cat, freq / totalWords);
            }
            c++;
            if (c % 100 == 0) {
                System.out.println("Calculated probabilities for: " + c + " /" + vocabulary.size() + " words");
            }
        }
    }

    @Override
    public List<String> test(List<DatasetExample> testingset) throws IOException {
        
        int c = 0;
        List<String> out = new ArrayList(testingset.size());
        
        for (DatasetExample d : testingset) {
            
            List<CategoryEntry> cats = new ArrayList(categoryProbability.size());
            float probability = 0f;
            for (String cat : categoryProbability.keySet()) {
                
                probability = (float) Math.log(categoryProbability.get(cat));
                for (String w : d.getBow().getWords()) {
                    
                    // abba perdoname por mi programmazione loca
                    if (termCategoryProb.containsKey(w)) {
                    
                        probability += Math.log(termCategoryProb.get(w).get(cat));
                    } else {
                        
                        probability += Math.log(1 / vocabulary.size());
                    }
                }
                cats.add(new CategoryEntry(cat, probability));
                }
            
            Collections.sort(cats, Collections.reverseOrder());
            out.add(cats.get(0).getCategory());
            c++;
            if (c % 100 == 0) {
                
                System.out.println("Tested documents: " + c + " /" + testingset.size());
            }
            }
        
        return out;
        }
}
