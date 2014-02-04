import java.io.*;
import java.util.*;

public class build_kNN {
    
    public static void main(String[] args) throws IOException {

        String train_path = args[0];
        String test_path = args[1];
        int k_val = Integer.parseInt(args[2]);
        int similarity_func = Integer.parseInt(args[3]);
        String sys_output = args[4];

        PrintStream sys = new PrintStream(sys_output);
        
        boolean isEuclidean = true;
        if (similarity_func == 1) {
           isEuclidean = true;
        } else if (similarity_func == 2) {
           isEuclidean = false;
        } else {
           System.out.println("Wrong value for similarity_func.");
        }
        
        kNearestNeighbors knn = new kNearestNeighbors(train_path);
        knn.prediction(test_path, k_val, isEuclidean, sys);
        knn.confusion_matrix();
    }
}