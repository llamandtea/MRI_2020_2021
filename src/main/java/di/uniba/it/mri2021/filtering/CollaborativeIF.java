/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.filtering;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author pierpaolo
 */
public abstract class CollaborativeIF {

    // da eliminare una volta implementata la matrice user item
    private final IFDataset dataset;
    
    // Matrice di valutazioni user-item
    private int[][] userItemMatrix;
    
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
        
        // inizializzazione della matrice
        int userSize = dataset.getUsers().size();
        int itemSize = dataset.getItems().size();
        userItemMatrix = new int[userSize + 1][itemSize + 1];
        
        // fill della matrice con -1, in maniera tale da poter identificare valutazioni
        // mancanti
        for (int i = 0; i < userSize + 1; i++) {
            
            Arrays.fill(userItemMatrix[i], -1);
        }
        
        // avvaloramento della matrice
        int userPos;
        int itemPos;
        /*
        per motivi di efficienza, la matrice verrà trattata come una hashmap
        utente-oggetto. Prima di tutto vengono inizializzate la colonna 0
        (contenente gli hash degli userID) e la riga 0 (contenente gli hash
        degli itemID). Dopo questa operazione, la colonna 0 viene ordinata secondo i
        valori degli hash utente, idem per la riga 0 ma con gli hash item. In questo
        modo sarà possibile accedere all'oggetto desiderato tramite una ricerca
        binaria sul suo hash.
        */
        for (User u : dataset.getUsers()) {
            
            /*
            prima di tutto calcolo gli hash degli ID
            */
            int currentUserHash = u.getUserId().hashCode();
            
            /*
            viene calcolato il modulo per ottenere una posizione
            legale all'interno della matrice. NB: questa operazione
            non assicura che non si scelga una posizione già occupata
            da un altro hash.
            */
            userPos = currentUserHash % (userSize + 1);
            
            // per motivi di implementazione la posizione [0,0] dell'array
            // deve rimanere non avvalorato, in quanto un hash utente potrebbe
            // essere confuso con un hash item. Essendo inizializzato a 0,
            // nel momento dell'ordinamento manterrà comunque la prima posizione 
            while (true) {
                
                if (userPos == 0) { userPos++; }
                // se la posizione è vuota, inserisco l'hash utente
                if (userItemMatrix[userPos][0] == -1)  {
                
                    userItemMatrix[userPos][0] = currentUserHash;
                    break;
                } else {
                    
                    while(userPos < userSize + 1 && userItemMatrix[userPos][0] != -1 )
                        userPos++;
                    
                    if (userPos >= userSize + 1) { userPos = 1; }
                }
            }
        }
        
        for (Item i : dataset.getItems()) {
            
            int currentItemHash = i.getItemID().hashCode();
            itemPos = currentItemHash % (itemSize + 1);
            
            while (true) {
                
                if (itemPos == 0) { itemPos++; }
                if (userItemMatrix[0][itemPos] == -1) {
                    
                    userItemMatrix[0][itemPos] = currentItemHash;
                    break;
                } else {
                    
                    while (itemPos < itemSize + 1 && userItemMatrix[0][itemPos] != -1)
                        itemPos++;
                    
                    if (itemPos >= itemSize + 1) { itemPos = 1; }
                }
            }
        }
        
        // sorting della riga 0 contenente gli hash degli oggetti
        Arrays.sort(userItemMatrix[0]);
        
        // sorting della colonna 0 sugli hash degli userId
        // viene utilizzata una classe anonima per specificare un comparator
        // fra array di interi (implementato come la differenza fra il primo elemento
        // dell'array rispetto il primo elemento dell'array da confrontare).
        Comparator<int[]> userCompare = new Comparator<int[]>() {
        
            public int compare(int[] o1, int[] o2) {
            
                return o1[0] - o2[0];
            }
        }
        Arrays.sort(userItemMatrix, userCompare);
        
        for (Rating r : dataset.getRatings()) {
            
            userPos = Arrays.binarySearch(userItemMatrix, r.getUserId(), userCompare);
            userItemMatrix
        }
    }

    public IFDataset getDataset() {
        return dataset;
    }

    public abstract double getPrediction(User user, Item item, Similarity toUse);

    public abstract List<ItemPrediction> getPredictions(User user, Similarity toUse);

}
