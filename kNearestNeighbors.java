import java.io.*;
import java.util.*;

public class kNearestNeighbors {
   
   private static String train_path;
   
   private Map<String, Map<String, Integer>> train_data;
   private Map<String, Map<String, Integer>> test_data;
   private static Map<String, Integer> word_counts;
   
   private Map<String, Integer> majority_vote = new HashMap<String, Integer>();
   
   public kNearestNeighbors(String train_path) {
      this.train_path = train_path;      
   }
   
   public void build_vectors() throws IOException {
      
      BufferedReader br = new BufferedReader(new FileReader(train_path));
      
      word_counts = new HashMap<String, Integer>();
      train_data = new HashMap<String, Map<String, Integer>>();
      
      String line = "";
      String classLabel = "";
      
      while ((line = br.readLine()) != null) {
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         
         for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            int count = Integer.parseInt(token.replaceAll("[\\w]+:", ""));
            
            if (!word_counts.containsKey(classLabel)) {
               word_counts.put(classLabel, count);
            } else {
               word_counts.put(classLabel, word_counts.get(classLabel) + count);
            }
            
            if (train_data.get(classLabel).containsKey(word)) {
               train_data.get(classLabel).put(word, train_data.get(classLabel).get(word) + count);
            } else {
               train_data.get(classLabel).put(word, count);
            }
         }
      }
      
   }
   
   public void prediction(String test_path, int k_val, boolean isEuclidean) throws IOException {
      
      BufferedReader br = new BufferedReader(new FileReader(test_path));
      
      test_data = new HashMap<String, Map<String, Integer>>();
      String line = "";
      String classLabel = "";
      
      while ((line = br.readLine()) != null) {
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         
         for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            int count = Integer.parseInt(token.replaceAll("[\\w]+:", "")); 
            
         }
      }     
      
   }
   
}