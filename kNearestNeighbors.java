import java.io.*;
import java.util.*;
import java.math.*;
import java.lang.*;

public class kNearestNeighbors {
   
   private static String train_path;
   private Map<String, Map<String, Integer>> train_data = new HashMap<String, Map<String, Integer>>();
   private static Map<String, Integer> word_counts = new HashMap<String, Integer>();
   private Map<String, Map<String, Integer>> test_matrix = new HashMap<String, Map<String, Integer>>();
   private Set<String> classLabs = new TreeSet<String>();
   private Map<String, Integer> majority_votes = new HashMap<String, Integer>();
   
   public kNearestNeighbors(String train_path) {
      this.train_path = train_path;    
   }
   
   public void prediction(String test_path, int k_val, boolean isEuclidean, PrintStream sys) throws IOException {
      
      sys.println("%%%%% test data:");
      BufferedReader test_br = new BufferedReader(new FileReader(test_path));
      
      String line = "";
      String classLabel = "";
      int array_num = -1;
      
      while ((line = test_br.readLine()) != null) {
         
         array_num ++;
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
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
            classLabs.add(trainClassLabel);
               
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
            
            for (String testWord : testTokensCount.keySet()) {               
               int testCount = testTokensCount.get(testWord);
                  
               if (trainTokensCount.containsKey(testWord)) {
                  if (isEuclidean) {
                     double euc = testCount - trainTokensCount.get(testWord);
                     dist += euc * euc;
                  } else {
                     cos_mult += testCount * trainTokensCount.get(testWord);
                     cos_ik += testCount * testCount;
                     cos_jk += trainTokensCount.get(testWord) * trainTokensCount.get(testWord);
                  }
               } else {
                  if (isEuclidean) {
                     dist += testCount * testCount;
                  } else {
                     cos_mult = 0;
                     cos_ik = testCount * testCount;
                     cos_jk = 0;
                  }
               }
            }
            
            for (String trainWord : trainTokensCount.keySet()) {
               int trainCount = trainTokensCount.get(trainWord);
               
               if (!testTokensCount.containsKey(trainWord)) {
                  if (isEuclidean) {
                     dist += trainCount * trainCount;
                  } else {
                     cos_mult = 0;
                     cos_ik = 0;
                     cos_jk = trainCount * trainCount;
                  }
               }
            }     
            
            if (isEuclidean) {
                dist = Math.sqrt(dist);
            } else {
                dist = cos_mult / (Math.sqrt(cos_ik) * Math.sqrt(cos_jk));
            }
            
            String key = train_line_num + trainClassLabel;
         
            if (dist_tally.size() < k_val) {
               dist_tally.put(key, dist);
            } else {
               dist_tally = pick_nearest(dist_tally, key, dist);
            }
         }
         
         Map<String, Integer> sys_votes = new HashMap<String, Integer>();
         sys_votes = convert(dist_tally);
         print(sys_votes, sys, classLabel);
      }     
   }
   
   private Map<String, Double> pick_nearest(Map<String, Double> dist_tally, String new_key, double new_dist) {      
      if (dist_tally.size() > 1) {
         double max = 0.0;
         String max_key = "";
         for (String key : dist_tally.keySet()) {
            if (dist_tally.get(key) > max) {
               max = dist_tally.get(key);
               max_key = key;
            }
         }
         if (max > new_dist) {
            dist_tally.put(new_key, new_dist);
            dist_tally.remove(max_key);
         }
         return dist_tally;
      } else {
         Map<String, Double> one_nearest = new HashMap<String, Double>();
         for (String key : dist_tally.keySet()) {
            if (dist_tally.get(key) > new_dist) {
               return dist_tally;
            } else {
               one_nearest.put(new_key, new_dist);
               return one_nearest;
            }
         }
      }
      return null;
   }
   
   private Map<String, Integer> convert(Map<String, Double> dist_tally) {
      Map<String, Integer> votes = new HashMap<String, Integer>();
      for (String dist_key : dist_tally.keySet()) {
         String real_key = dist_key.replaceAll("[0-9]+", "");
         if (!votes.containsKey(real_key)) {
            votes.put(real_key, 1);
         } else {
            votes.put(real_key, votes.get(real_key) + 1);
         }
      }      
      if (votes.size() < classLabs.size()) {
         for (String cl : classLabs) {
            if (!votes.containsKey(cl)) {
               votes.put(cl, 0);
            }
         }
      }      
      return votes;
   }
   
   private void print(Map<String, Integer> votes, PrintStream sys, String correct_classLabel) {
      
      double votes_count = 0.0;
      for (String s : votes.keySet()) {
         votes_count += votes.get(s);
      }
      
      List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>();
      entryList.addAll(votes.entrySet());
      
      Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
         public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
            return b.getValue() - a.getValue();
         }
      }
      );
      
      int counter = 0;
      for (Map.Entry<String, Integer> a: entryList) {
         String key = "" + a.getKey();
         double prob = a.getValue() * 1.0 / votes_count;
         sys.print(" " + a.getKey().replaceAll("[0-9]+", "") + " " + prob);
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
      sys.println("");
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