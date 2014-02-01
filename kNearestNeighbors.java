import java.io.*;
import java.util.*;
import java.math.*;

public class kNearestNeighbors {
   
   private static String train_path;
   
   private Map<String, Map<String, Integer>> train_data;
   private static Map<String, Integer> word_counts;
   
   private Map<String, Map<String, Integer>> test_matrix;
   private Set<String> classLabs = new TreeSet<String>();
   
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
   
   public void prediction(String test_path, int k_val, boolean isEuclidean, PrintStream sys) throws IOException {
      
      BufferedReader test_br = new BufferedReader(new FileReader(test_path));
      
      String line = "";
      String classLabel = "";
      
      int array_num = -1;
      while ((line = test_br.readLine()) != null) {
         
         array_num ++;
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         
         sys.println("%%%%% test data:");
         sys.print("array:" + array_num + " " + classLabel);
         
         Map<String, Double> dist_tally = new HashMap<String, Double>();
                  
         for (int i = 1; i < tokens.length; i++) {

            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            int count = Integer.parseInt(token.replaceAll("[\\w]+:", "")); 
            
            BufferedReader train_br = new BufferedReader(new FileReader(test_path));
            
            String trainLine = "";
            String trainClassLabel = "";
            
            int train_line_num = 0;
            while ((line = train_br.readLine()) != null) {
               
               train_line_num ++;
               String[] trainTokens = line.split(" ");
               trainClassLabel = trainTokens[0];
               
               double dist = 0.0;
               double cos_mult = 0.0;
               double cos_ik = 0.0;
               double cos_jk = 0.0;
               
               for (int j = 1; j < trainTokens.length; j++) {
                  
                  String trainToken = trainTokens[j];
                  String trainWord = trainToken.replaceAll(":[\\d]+", "");
                  int trainCount = Integer.parseInt(token.replaceAll("[\\w]+:", ""));
                  
                  if (isEuclidean) {
                     double euc = (count / count + word_counts.get(word)) - (trainCount / count + word_counts.get(word));
                     double w_dist = euc * euc;
                     dist += w_dist;
                  } else {
                     cos_mult += (count / count + word_counts.get(word)) * (trainCount / count + word_counts.get(word));
                     cos_ik += (count / count + word_counts.get(word)) * (count / count + word_counts.get(word));
                     cos_jk += (trainCount / count + word_counts.get(word)) * (trainCount / count + word_counts.get(word));
                  }
               }
               if (isEuclidean) {
                  dist = Math.sqrt(dist);
               } else {
                  dist = cos_mult / (Math.sqrt(cos_ik) * Math.sqrt(cos_jk));
               }
// CHANGE NEEDING HERE
               String key = train_line_num + trainClassLabel;
               
               
               dist_tally.put(key, dist); 
            }           
         }
         Map<String, Integer> votes = pick_instances(dist_tally, k_val); 
         print(votes, sys, classLabel);
      }     
   }
   
   private Map<String, Integer> pick_instances(Map<String, Double> dist_tally, int k_val) {   
      Map<String, Integer> votes = new HashMap<String, Integer>();
      return votes;
   }
   
// CHANGE NEEDING HERE
   
   private void print(Map<String, Integer> votes, PrintStream sys, String correct_classLabel) {
      
      int votes_count = 0;
      for (String s : votes.keySet()) {
         votes_count += votes.get(s);
      }
      
      List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>();
      entryList.addAll(votes.entrySet());
      
      Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
         public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
            return a.getValue() - b.getValue();
         }
      }
      );
      
      int counter = 0;
      for (Map.Entry<String, Integer> a: entryList) {
         String key = "" + a.getKey();
         sys.print(" " + a.getKey() + " " + a.getValue());
         counter ++;
         if (counter == 1) {
            if (!test_matrix.containsKey(correct_classLabel)) {
               test_matrix.put(correct_classLabel, new HashMap<String, Integer>());
            } else if (test_matrix.get(correct_classLabel).containsKey(key)) {
               test_matrix.get(correct_classLabel).put(key, test_matrix.get(correct_classLabel).get(key) + 1);
            } else {
               test_matrix.get(correct_classLabel).put(key, 1);
            }
         }
      }
   }
   
   public void confusion_matrix() throws IOException {
      System.out.println("Confusion matrix for the test data:");
      System.out.println("row is the truth, column is the system output\n");
      System.out.print("\t");
      for (String class_3 : classLabs) {
         System.out.print(class_3 + " ");
      }
      System.out.println();
      for (String class_4 : classLabs) {
         System.out.print(class_4 + " ");
         for (String class_5 : classLabs) {
            if (test_matrix.get(class_4).get(class_5) != null) {
               System.out.print(test_matrix.get(class_4).get(class_5) + " ");
            } else {
               System.out.print("0 ");
            }
         }
         System.out.println();
      }
      System.out.println();
      System.out.println(" Test accuracy=" + getAccuracy(test_matrix));
   }
    
   private double getAccuracy(Map<String, Map<String, Integer>> matrix) {
      
      int correct_count = 0;
      int sum = 0;
      for (String str_1 : matrix.keySet()) {
         for (String str_2 : matrix.get(str_1).keySet()) {
            int count = matrix.get(str_1).get(str_2);
            if (str_1.equals(str_2)) {
               correct_count += count;
            }
            sum += count;
         }
      }
      return correct_count * 1.0 / sum;
   }
}