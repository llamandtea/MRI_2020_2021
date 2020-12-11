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
import java.util.List;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public class Rocchio extends TextCategorization {

    private Map<String, BoW> centroids = new HashMap();
    private float alfa = 0.8f;
    private float beta = 0.2f;

    @Override
    public void train(List<DatasetExample> trainingset) throws IOException {
        Map<String, Integer> count = new HashMap<>();
        for (DatasetExample e : trainingset) {
            BoW c = centroids.get(e.getCategory());
            if (c == null) {
                centroids.put(e.getCategory(), e.getBow());
                count.put(e.getCategory(), 1);
            } else {
                centroids.put(e.getCategory(), BoWUtils.add(c, e.getBow()));
                count.put(e.getCategory(), count.get(e.getCategory()) + 1);
            }
        }
        for (String c:centroids.keySet()) {
            BoWUtils.scalarProduct(1/count.get(c).floatValue(), centroids.get(c));
        }
        
        // Implementare il calcolo del centroide direttamente nel while precedente
        // per efficienza
        for (String c : centroids.keySet()) {
         
                BoW toSubtract = new BoW();
                for (DatasetExample e : trainingset) {
                    
                    if  (!e.getCategory().equals(c)) {
                        
                        toSubtract = BoWUtils.add(toSubtract, e.getBow());
                    }
                }
                BoWUtils.scalarProduct(1/(1 - count.get(c).floatValue()), toSubtract);
                
                BoWUtils.scalarProduct(alfa, centroids.get(c));
                BoWUtils.scalarProduct(beta, toSubtract);
                centroids.put(c, BoWUtils.subtract(centroids.get(c), toSubtract));
        }
    }

        @Override
    public List<String> test(List<DatasetExample> testingset) throws IOException {
        
        int c = 0;
        int numdocs = testingset.size();
        List<String> out = new ArrayList<String>(testingset.size());
        for (DatasetExample testingExample : testingset) {
            
            List<CategoryEntry> currDocCat = new ArrayList<CategoryEntry>(centroids.keySet().size());
            for (String cat : centroids.keySet()) {
                
                double sim = BoWUtils.sim(centroids.get(cat), testingExample.getBow());
                currDocCat.add(new CategoryEntry(cat, (float) sim));
            }
            Collections.sort(currDocCat, Collections.reverseOrder());
            out.add(currDocCat.get(0).getCategory());
            
            c++;
            if (c % 100 == 0) {
                
                System.out.println("Tested documents: " + c + " /" + numdocs);
        }
    }
        
        return out;
}

}
