/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.filtering;

import java.util.List;

/**
 *
 * @author pierpaolo
 */
public abstract class CollaborativeIF {

    private final IFDataset dataset;
    
    /**
     * Enumerazione per indicare la similarità da utilizzare durante il
     * calcolo delle predizioni
     */
    public static enum Similarity {
        
        PEARSON,
        COSINE
    }
    
    public CollaborativeIF(IFDataset dataset) {
        this.dataset = dataset;
    }

    public IFDataset getDataset() {
        return dataset;
    }

    public abstract double getPrediction(User user, Item item, Similarity toUse);

    public abstract List<ItemPrediction> getPredictions(User user, Similarity toUse);

}
