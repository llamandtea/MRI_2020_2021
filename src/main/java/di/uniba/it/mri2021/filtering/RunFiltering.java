/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.filtering;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class RunFiltering {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            IFDataset d = new Movielens();
            d.load(new File("resources/IF/ml-1m/"));
            CollaborativeIF userBased = new ItemBasedIF(d);
            String userId = "3162";
            System.out.println("Recommendations for user " + userId);
            long time = System.currentTimeMillis();
            
            CollaborativeIF.Similarity toUse = CollaborativeIF.Similarity.COSINE;
                
            List<ItemPrediction> predictions = userBased.getPredictions(new User(userId), toUse);
            Map<String, Item> itemsMap = IFDatasetUtils.itemListToMap(d.getItems());
            int top = 20;
            for (int i = 0; i < top && i < predictions.size(); i++) {
                Movie movie = (Movie) itemsMap.get(predictions.get(i).getItemid());
                System.out.println(movie.getTitle() + "\t" + predictions.get(i).getScore());
            }
            System.out.println("Time: " + (System.currentTimeMillis() - time));

            userId = "1";
            predictions = userBased.getPredictions(new User(userId), toUse);
            System.out.println("Recommendations for user " + userId);
            time = System.currentTimeMillis();
            itemsMap = IFDatasetUtils.itemListToMap(d.getItems());
            for (int i = 0; i < top && i < predictions.size(); i++) {
                Movie movie = (Movie) itemsMap.get(predictions.get(i).getItemid());
                System.out.println(movie.getTitle() + "\t" + predictions.get(i).getScore());
            }
            System.out.println("Time: " + (System.currentTimeMillis() - time));
            
            userId = "2164";
            predictions = userBased.getPredictions(new User(userId), toUse);
            System.out.println("Recommendations for user " + userId);
            time = System.currentTimeMillis();
            itemsMap = IFDatasetUtils.itemListToMap(d.getItems());
            for (int i = 0; i < top && i < predictions.size(); i++) {
                Movie movie = (Movie) itemsMap.get(predictions.get(i).getItemid());
                System.out.println(movie.getTitle() + "\t" + predictions.get(i).getScore());
            }
            System.out.println("Time: " + (System.currentTimeMillis() - time));
        } catch (IOException ex) {
            Logger.getLogger(RunFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
