/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2021.tc;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author pierpaolo
 */
public class TestTC {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        TextCategorization tc=new NaiveBayes();
        DatasetReader tr=new CSVDatasetReader();
        tc.train(tr.getExamples(new File("resources/TC/train.csv")));
        DatasetReader ts=new CSVDatasetReader();
        List<DatasetExample> testset = ts.getExamples(new File("resources/TC/test.csv"));
        List<String> p = tc.test(testset);
        tc.generateConfusionMatrix(testset, p);
        System.out.println(tc.accuracy(p));
        
        System.out.println("Micro Precision: " + tc.getMicroPrecision());
        System.out.println("Micro Recall: " + tc.getMicroRecall());
        System.out.println("Macro Precision: " + tc.getMacroPrecision());
        System.out.println("Macro Recall: " + tc.getMacroRecall());
        System.out.println("Micro F Measure: " + tc.microFMeasure());
        System.out.println("Micro F Measure (precision boost): " + tc.microFMeasure(0.45d));
        System.out.println("Micro F Measure (recall boost): " + tc.microFMeasure(1.45d));
        System.out.println("Macro F Measure: " + tc.macroFMeasure());
        System.out.println("Macro F Measure (precision boost): " + tc.macroFMeasure(0.45d));
        System.out.println("Macro F Measure (recall boost): " + tc.macroFMeasure(1.45d));
    }
    
}
