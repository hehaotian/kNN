import java.io.*;
import java.util.*;
import java.math.*;

public class kNearestNeighbors {
   
   private static String train_path;
   
   private Map<String, Map<String, Integer>> train_data = new HashMap<String, Map<String, Integer>>();
   private static Map<String, Integer> word_counts = new HashMap<String, Integer>();
   
   private Map<String, Map<String, Integer>> test_matrix;
   private Set<String> classLabs = new TreeSet<String>();
   
   public kNearestNeighbors(String train_path) {
      this.train_path = train_path;    
   }
   
   public void build_vectors() throws IOException {
      
      BufferedReader br = new BufferedReader(new FileReader(train_path));

      String line = "";
      String classLabel = "";
      
      while ((line = br.readLine()) != null) {
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         
         for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            int count = Integer.parseInt(token.replaceAll("[\\w]+:", ""));
            
            if (!word_counts.containsKey(word)) {
               word_counts.put(word, count);
            } else {    
               word_counts.put(word, word_counts.get(word) + count);
            }
            
            if (train_data.get(classLabel) == null) {
               Map<String, Integer> temp = new HashMap<String, Integer>();
               train_data.put(classLabel, temp);  
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
         
         double dist = 0.0;
         double cos_mult = 0.0;
         double cos_ik = 0.0;
         double cos_jk = 0.0;
            
         BufferedReader train_br = new BufferedReader(new FileReader(test_path));
         
         String trainLine = "";
         String trainClassLabel = "";
         int train_line_num = 0;
         while ((line = train_br.readLine()) != null) {
               
            train_line_num ++;
            String[] trainTokens = line.split(" ");
            trainClassLabel = trainTokens[0];
               
            Map<String, Integer> testTokensCount = new HashMap<String, Integer>();
            Map<String, Integer> trainTokensCount = new HashMap<String, Integer>();   
                           
            for (int i = 1; i < tokens.length; i++) {
               String token = tokens[i];
               String word = token.replaceAll(":[\\d]+", "");
               int count = Integer.parseInt(token.replaceAll("[\\w]+:", ""));
               testTokensCount.put(word, count);
            }
               
            for (int j = 1; j < trainTokens.length; j++) {
               String trainToken = trainTokens[j];
               String trainWord = trainToken.replaceAll(":[\\d]+", "");
               int trainCount = Integer.parseInt(trainToken.replaceAll("[\\w]+:", ""));
               trainTokensCount.put(trainWord, trainCount);
            }
            
            for (String word : testTokensCount.keySet()) {               
               int count = testTokensCount.get(word);               
               
               int word_total_count = 0;
               if (word_counts.containsKey(word)) {
                  word_total_count = word_counts.get(word);
               }
                  
               int denominator = count + word_total_count;
               // System.out.println(denominator);
               if (trainTokensCount.containsKey(word)) {
                  if (isEuclidean) {
                      
                     System.out.println()
                     double euc = (count / denominator) - (trainTokensCount.get(word) / denominator);
                      
                     double w_dist = euc * euc;
                      System.out.println(euc + "\t" + w_dist);
                     dist += w_dist;                     
                  } else {
                      System.out.println("yes1");
                     cos_mult += (count / denominator) * (trainTokensCount.get(word) / denominator);
                     cos_ik += (count / denominator) * (count / denominator);
                     cos_jk += (trainTokensCount.get(word) / denominator) * (trainTokensCount.get(word) / denominator);
                  }
               } else {
                  if (isEuclidean) {
                     dist += (count / denominator) * (count / denominator);
                  } else {
                      System.out.println("yes2");
                     cos_mult += 0;
                     cos_ik += (count / denominator) * (count / denominator);
                     cos_jk += 0;
                  }
               }
            }
            
            if (isEuclidean) {
                dist = Math.sqrt(dist);
            } else {
                dist = cos_mult / (Math.sqrt(cos_ik) * Math.sqrt(cos_jk));
            }
            
            // System.out.println(classLabel + "\t" +  + "\t" + trainClassLabel + "\t" + dist);
         }

         String key = train_line_num + trainClassLabel;
         dist_tally.put(key, dist); 
         Map<String, Integer> votes = pick_instances(dist_tally, k_val); 
         print(votes, sys, classLabel);
      }     
   }
   
   private Map<String, Integer> pick_instances(Map<String, Double> dist_tally, int k_val) {   
      Map<String, Integer> votes = new HashMap<String, Integer>();
      return votes;
   }
   
   
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