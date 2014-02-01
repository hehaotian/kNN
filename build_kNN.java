import java.io.*;
import java.util.*;

public class build_kNN {
    
    private static String train_path;
    private static String test_path;
    private static int k_val;
    private static int similarity_func;
    private static String sys_output;
    
    public static void main(String[] args) throws IOException {

        train_path = args[0];
        test_path = args[1];
        k_val = Integer.parseInt(args[2]);
        similarity_func = Integer.parseInt(args[3]);
        sys_output = args[4];

        PrintStream sys = new PrintStream(sys_output);
        
        boolean isEuclidean = true;
        if (similarity_func == 1) {
           isEuclidean = true;
        } else if (similarity_func == 2) {
           isEuclidean = false;
        } else {
           System.out.println("Wrong value for similarity_func.");
        }
        
        NearestNeighbors knn = new NearestNeighbors(train_path);
        knn.prediction(test_path, k_val, isEuclidean);
        

    }

}