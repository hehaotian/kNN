import java.io.*;
import java.util.*;
import java.math.*;
import java.lang.*;

public class kNearestNeighbors {
   
   private static String train_path;
   private Map<String, Map<String, Integer>> train_data = new HashMap<String, Map<String, Integer>>();
   private Set<String> feature_counts = new TreeSet<String>();
   private Map<String, Map<String, Integer>> test_matrix = new HashMap<String, Map<String, Integer>>();
   private Set<String> classLabs = new TreeSet<String>();
   private Map<String, Integer> majority_votes = new HashMap<String, Integer>();
   
   public kNearestNeighbors(String train_path) {
      this.train_path = train_path;    
   }
   
   public void prediction(String test_path, int k_val, boolean isEuclidean, PrintStream sys) throws IOException {
      
      sys.println("%%%%% test data:");
      
      // reads the test file
      BufferedReader test_br = new BufferedReader(new FileReader(test_path));
      
      String testLine = "";
      String testClassLabel = "";
      int test_line_num = -1;
      
      // reads each test instance
      while ((testLine = test_br.readLine()) != null) {
         
         test_line_num ++;
         // System.out.println(testLine + "testLine" + test_line_num);
         String[] tokens = testLine.split(" ");
         testClassLabel = tokens[0];
         sys.print("array:" + test_line_num + " " + testClassLabel);
         
         Map<String, Integer> testTokensCount = new HashMap<String, Integer>();
         
         for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            int count = Integer.parseInt(token.replaceAll("[\\w]+:", ""));
            testTokensCount.put(word, count);
         }
         
         // reads the training file   
         BufferedReader train_br = new BufferedReader(new FileReader(train_path));
         
         String trainLine = "";
         String trainClassLabel = "";
         int train_line_num = 0;
         
         // stores k-nearest instances of training instances
         Map<String, Double> dist_tally = new HashMap<String, Double>();
         
         // reads each training instance
         while ((trainLine = train_br.readLine()) != null) {
               
            train_line_num ++;
            String[] trainTokens = trainLine.split(" ");
            trainClassLabel = trainTokens[0];
            classLabs.add(trainClassLabel);
            // System.out.println(trainLine + "trainLine" + train_line_num);
               
            // stores the feature vectors of the test and training instances
            Map<String, Integer> trainTokensCount = new HashMap<String, Integer>();
               
            for (int j = 1; j < trainTokens.length; j++) {
               String trainToken = trainTokens[j];
               String trainWord = trainToken.replaceAll(":[\\d]+", "");
               feature_counts.add(trainWord);
               int trainCount = Integer.parseInt(trainToken.replaceAll("[\\w]+:", ""));
               trainTokensCount.put(trainWord, trainCount);
            }
            
            double dist = 0.0;
            double sum = 0.0;
            double cos_prod = 0.0;
            double cos_ik = 0.0;
            double cos_jk = 0.0;
            
            for (String testWord : testTokensCount.keySet()) {
               int testCount = testTokensCount.get(testWord);
               
               if (trainTokensCount.containsKey(testWord)) {
                  int trainCount = trainTokensCount.get(testWord);
                  if (isEuclidean) {
                     double minus = testCount - trainCount;
                     sum += minus * minus;
                  } else {
                     cos_prod += testCount * trainCount;
                     cos_ik += testCount * testCount;
                     cos_jk += trainCount * trainCount;
                  }
               } else {
                  if (isEuclidean) {
                     sum += testCount * testCount;
                  } else {
                     cos_ik += testCount * testCount;
                  }
               }
            }
            
            for (String trainWord : trainTokensCount.keySet()) {
               int trainCount = trainTokensCount.get(trainWord);
               if (!testTokensCount.containsKey(trainWord)) {
                  if (isEuclidean) {
                     sum += trainCount * trainCount;
                  } else {
                     cos_jk += trainCount * trainCount;
                  }
               }
            }
            
            if (isEuclidean) {
               dist = Math.sqrt(sum);
            } else {
               dist = cos_prod / (Math.sqrt(cos_ik) * Math.sqrt(cos_jk));
            }
            
            // DEBUG:
            // System.out.println(testClassLabel + "\t" + train_line_num + "\t" + trainClassLabel + "\t" + dist);
            //
            
            // stores the training instance with the distance
            String new_key = train_line_num + trainClassLabel;
            if (dist_tally.size() < k_val) {
               dist_tally.put(new_key, dist);
            } else {
               dist_tally = pick_nearest(dist_tally, new_key, dist);
            }
            
            // DEBUG:
            // System.out.println("SMALLEST DISTANCE:");
            // for (String str : dist_tally.keySet()) {
            //    System.out.print(str + ":" + dist_tally.get(str) + "\t");
            // }
            // System.out.println("");
            // 
         }
            
         // converts the top k instances map into a instances count map
         Map<String, Integer> sys_votes = new HashMap<String, Integer>();
         sys_votes = convert(dist_tally);
            
         // DEBUG:
         // System.out.println("FILE NAMES:");
         // for (String str : sys_votes.keySet()) {
         //    System.out.print(str + "\t" + sys_votes.get(str) + "\t");
         // }
         // System.out.println("");
         //
         // prints the probabilities of each predicted instances
         print(sys_votes, sys, testClassLabel);
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
            if (dist_tally.get(key) <= new_dist) {
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
         String key = "" + a.getKey().replaceAll("[0-9]+", "");
         double prob = a.getValue() * 1.0 / votes_count;
         if (counter == 0) {
            if (!test_matrix.containsKey(correct_classLabel)) {
               test_matrix.put(correct_classLabel, new HashMap<String, Integer>());
            }
            if (test_matrix.get(correct_classLabel).containsKey(key)) {
               test_matrix.get(correct_classLabel).put(key, test_matrix.get(correct_classLabel).get(key) + 1);
            } else {
               test_matrix.get(correct_classLabel).put(key, 1);
            }
         }
         sys.print(" " + key + " " + prob);
         counter ++;
      }
      sys.println("");
   }
   
   public void confusion_matrix() throws IOException {
      System.out.println("class_num=" + classLabs.size() + " feat_num=" + feature_counts.size());
      System.out.println();
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
//    
//    private double get_euclidean(Map<String, Integer> test, Map<String, Integer> train) {
//       double sum = 0.0;
//       Map<String, Integer> merged = new HashMap<String, Integer>();
//       merged = merge(test, train);
//       for (String feat : merged.keySet()) {
//          double minus = 0;
//          if (train.containsKey(feat) && test.containsKey(feat)) {
//             minus = test.get(feat) - train.get(feat);
//          } else if (test.containsKey(feat)) {
//             minus = test.get(feat);
//          } else {
//             minus = train.get(feat);
//          }
//          sum += minus * minus;
//       }
//       return Math.sqrt(sum);
//    }
//    
//    private double get_cosine(Map<String, Integer> test, Map<String, Integer> train) {
//       double prod = 0.0;
//       double sq1 = 0.0;
//       double sq2 = 0.0;
//       for (String feature : test.keySet()) {
//          if (train.containsKey(feature)) {
//             prod += test.get(feature) * train.get(feature);
//             sq1 += Math.pow(test.get(feature), 2);
//             sq2 += Math.pow(train.get(feature), 2);
//          } else {
//             prod += 0;
//             sq1 += Math.pow(test.get(feature), 2);
//             sq2 += 0;
//          }
//       }
//       for (String feature : train.keySet()) {
//          if (!test.containsKey(feature)) {
//             prod += 0;
//             sq1 += 0;
//             sq2 += train.get(feature) * train.get(feature);
//          }
//       }
//       return prod / (Math.sqrt(sq1) * Math.sqrt(sq2));
//    }
//        
//    private Map<String, Integer> merge(Map<String, Integer> test, Map<String, Integer> train) {
//       for (String s : train.keySet()) {
//          if (!test.containsKey(s)) {
//             test.put(s, train.get(s));
//          } else {
//             test.put(s, test.get(s) + train.get(s));
//          }
//       }
//       return test;
//    }
//    