/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pierpaolo
 */
public class ItemBasedIF extends CollaborativeIF {

    private Map<String, List<Rating>> ratingsByUser = null;
    private Map<String, List<Rating>> ratingsByItem = null;
    private Map<String, Double> averageScore = new HashMap<>();
    
    public ItemBasedIF(IFDataset dataset) {
        super(dataset);
        
        ratingsByUser = IFDatasetUtils.getRatingsByUser(dataset.getRatings());
        ratingsByItem = IFDatasetUtils.getRatingsByItem(dataset.getRatings());
    }

    private double getAverageScore(String userid) {
        Double avg = averageScore.get(userid);
        if (avg == null) {
            avg = IFDatasetUtils.average(ratingsByUser.get(userid));
            averageScore.put(userid, avg);
        }
        return avg;
    }
    
    private double cosineSimilarity(String itemId1, String itemId2) throws IllegalArgumentException {
        
        List<Rating> r1 = ratingsByItem.get(itemId1);
        List<Rating> r2 = ratingsByItem.get(itemId2);
        
        if (r1 == null || r2 == null) {
            
            throw new IllegalArgumentException("Error: item has no ratings");
        }
        
        List<String> sharedUsers = IFDatasetUtils.getSharedUsers(r1, r2);
        if (sharedUsers.isEmpty()) {
            
            return 0d;
        } else {
            
            Map<String, Integer> m1 = IFDatasetUtils.ratingsToMapByUser(r1);
            Map<String, Integer> m2 = IFDatasetUtils.ratingsToMapByUser(r2);
            
            double num = 0d;
            double denum = 0d;
            double n1 = 0d;
            double n2 = 0d;
            
            for (String user : sharedUsers) {
                
                double averageRating = getAverageScore(user);
                num += ((m1.get(user) - averageRating) * (m2.get(user) - averageRating));
                n1 += Math.pow((m1.get(user)) - averageRating, 2d);
                n2 += Math.pow((m2.get(user)) - averageRating, 2d);
            }
            
            if (n1 == 0 || n2 == 0) {
                
                return 0d;
            } else {
                
                return num / (Math.sqrt(n1) * Math.sqrt(n2));
            }
        }
        
    }
    
    @Override
    public double getPrediction(User user, Item item, Similarity toUse) {
        
        List<Rating> userRatings = ratingsByUser.get(user.getUserId());
        if (userRatings == null) {
            
            // Se l'utente non ha espresso preferenze, ritorno il voto
            // medio dell'item
            return IFDatasetUtils.average(ratingsByItem.get(item.getItemID()));
        }
        
        double num = 0d;
        double denum = 0d;
        for (Rating r : userRatings) {
            
            try {
                
                double itemRating = userRatings.stream().filter(e -> e.getItemId().equals(r.getItemId())).findFirst().get().getRating();
                double sim = cosineSimilarity(item.getItemID(), r.getItemId());
                num += (sim * itemRating);
                denum += sim;
            } catch (IllegalArgumentException ex) {
                
//                Logger.getLogger(UserBasedIF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return ((denum == 0d) ? 0d : (num / denum));
    }

    // Uguale a quella dell'user-to-user, magari mettere nella classe parent
    @Override
    public List<ItemPrediction> getPredictions(User user, Similarity toUse) {
        List<ItemPrediction> predictions = new ArrayList<>();
        // get items rated by the user and map them
        List<Rating> userRatings = ratingsByUser.get(user.getUserId());
        Map<String, Integer> ratingsToMap = IFDatasetUtils.ratingsToMapByItem(userRatings);
        for (Item item : getDataset().getItems()) {
            // for each item not rated by the user
            if (!ratingsToMap.containsKey(item.getItemID())) {
                // get prediction
                double prediction = getPrediction(user, item, toUse);
                predictions.add(new ItemPrediction(item.getItemID(), prediction));
            }
        }
        // sort predictions
        Collections.sort(predictions, Collections.reverseOrder());
        return predictions;
    }

}
