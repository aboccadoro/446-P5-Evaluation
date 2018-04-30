import com.google.common.collect.LinkedHashMultimap;

import java.io.*;
import java.util.*;

public class Evaluation {

    private static LinkedHashMultimap<Integer, Tuple<String, Integer>> rels = LinkedHashMultimap.create();
    private static LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> bm25Map = LinkedHashMultimap.create();
    private static LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> qlMap = LinkedHashMultimap.create();
    private static LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> sdmMap = LinkedHashMultimap.create();
    private static LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> stressMap = LinkedHashMultimap.create();

    public static void main(String[] args) throws IOException {
        BufferedReader qrels = new BufferedReader(new FileReader("evaluation-data\\qrels"));
        for (String line = qrels.readLine(); line != null; line = qrels.readLine()) {
            String[] rel = line.split(" ");
            rels.put(Integer.parseInt(rel[0]), new Tuple<>(rel[2], Integer.parseInt(rel[3])));
        }
        BufferedReader bm25 = new BufferedReader(new FileReader("evaluation-data\\bm25.trecrun"));
        for (String line = bm25.readLine(); line != null; line = bm25.readLine()) {
            String[] rel = line.split(" ");
            bm25Map.put(Integer.parseInt(rel[0]), new Tuple2<>(rel[2], Integer.parseInt(rel[3]), Float.parseFloat(rel[4])));
        }
        BufferedReader ql = new BufferedReader(new FileReader("evaluation-data\\ql.trecrun"));
        for (String line = ql.readLine(); line != null; line = ql.readLine()) {
            String[] rel = line.split(" ");
            qlMap.put(Integer.parseInt(rel[0]), new Tuple2<>(rel[2], Integer.parseInt(rel[3]), Float.parseFloat(rel[4])));
        }
        BufferedReader sdm = new BufferedReader(new FileReader("evaluation-data\\sdm.trecrun"));
        for (String line = sdm.readLine(); line != null; line = sdm.readLine()) {
            String[] rel = line.split(" ");
            sdmMap.put(Integer.parseInt(rel[0]), new Tuple2<>(rel[2], Integer.parseInt(rel[3]), Float.parseFloat(rel[4])));
        }
        BufferedReader stress = new BufferedReader(new FileReader("evaluation-data\\stress.trecrun"));
        for (String line = stress.readLine(); line != null; line = stress.readLine()) {
            String[] rel = line.split(" ");
            stressMap.put(Integer.parseInt(rel[0]), new Tuple2<>(rel[2], Integer.parseInt(rel[3]), Float.parseFloat(rel[4])));
        }

        PrintWriter writer = new PrintWriter("output.trecrun");
        float sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += NDCG_p(bm25Map, i);
        }
        writer.println(String.format("bm25.trecrun NDCG@15 %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += NDCG_p(qlMap, i);
        }
        writer.println(String.format("ql.trecrun NDCG@15 %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += NDCG_p(sdmMap, i);
        }
        writer.println(String.format("sdm.trecrun NDCG@15 %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += NDCG_p(stressMap, i);
        }
        writer.println(String.format("stress.trecrun NDCG@15 %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += MRR(bm25Map);
        }
        writer.println(String.format("bm25.trecrun MRR %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += MRR(qlMap);
        }
        writer.println(String.format("ql.trecrun MRR %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += MRR(sdmMap);
        }
        writer.println(String.format("sdm.trecrun MRR %f", sum / (700 - 601 + 450 - 301)));
        sum = 0.0f;
        for (int i = 301; i <= 700; i++) {
            sum += MRR(stressMap);
        }
        writer.println(String.format("stress.trecrun MRR %f", sum / (700 - 601 + 450 - 301)));
        for (float precision : Precision_k(bm25Map, 5)) {
            writer.println(String.format("bm25.trecrun P@5 %f", precision));
        }
        for (float precision : Precision_k(qlMap, 5)) {
            writer.println(String.format("ql.trecrun P@5 %f", precision));
        }
        for (float precision : Precision_k(sdmMap, 5)) {
            writer.println(String.format("sdm.trecrun P@5 %f", precision));
        }
        for (float precision : Precision_k(stressMap, 5)) {
            writer.println(String.format("stress.trecrun P@5 %f", precision));
        }
        for (float precision : Precision_k(bm25Map, 10)) {
            writer.println(String.format("bm25.trecrun P@10 %f", precision));
        }
        for (float precision : Precision_k(qlMap, 10)) {
            writer.println(String.format("ql.trecrun P@10 %f", precision));
        }
        for (float precision : Precision_k(sdmMap, 10)) {
            writer.println(String.format("sdm.trecrun P@10 %f", precision));
        }
        for (float precision : Precision_k(stressMap, 10)) {
            writer.println(String.format("stress.trecrun P@10 %f", precision));
        }
        for (float precision : Recall_k(bm25Map, 10)) {
            writer.println(String.format("bm25.trecrun R@10 %f", precision));
        }
        for (float precision : Recall_k(qlMap, 10)) {
            writer.println(String.format("ql.trecrun R@10 %f", precision));
        }
        for (float precision : Recall_k(sdmMap, 10)) {
            writer.println(String.format("sdm.trecrun R@10 %f", precision));
        }
        for (float precision : Recall_k(stressMap, 10)) {
            writer.println(String.format("stress.trecrun R@10 %f", precision));
        }
        /*******************************************************************
         *
         * I could not figure out how to make AP run faster
         *
        ArrayList<Float> precisions = Precision_k(bm25Map, 1);
        ArrayList<Float> precs  = precisions;
        int k = 1;
        int i = 301;
        int j = 0;
        for (float ignored : precisions) {
            if (k + 1 >= bm25Map.get(i).size()) break;
            while(k <= bm25Map.get(i).size()) {
                ArrayList<Float> next_prec = Precision_k(bm25Map, ++k);
                for (float next : next_prec) {
                     precs.set(j, precs.get(j) + next);
                }
            }
            precs.set(j, precs.get(j) / (float) k);
            i++;
            j++;
            if (i == 451) i = 601;
        }
        i = 301;
        for (Float prec : precisions) {
            writer.println(String.format("bm25.trecrun AP %f", prec / bm25Map.get(i++).size()));
            if (i == 451) i = 601;
        }
        precs  =  Precision_k(qlMap, 1);
        k = 1;
        i = 301;
        j = 0;
        for (float ignored : precisions) {
            if (k + 1 >= qlMap.get(i).size()) break;
            while(k <= qlMap.get(i).size()) {
                ArrayList<Float> next_prec = Precision_k(qlMap, ++k);
                for (float next : next_prec) {
                    precs.set(j, precs.get(j) + next);
                }
            }
            precs.set(j, precs.get(j) / (float) k);
            i++;
            j++;
            if (i == 451) i = 601;
        }
        i = 301;
        for (Float prec : precisions) {
            writer.println(String.format("ql.trecrun AP %f", prec / qlMap.get(i++).size()));
            if (i == 451) i = 601;
        }
        precs  =  Precision_k(sdmMap, 1);
        k = 1;
        i = 301;
        j = 0;
        for (float ignored : precisions) {
            if (k + 1 >= sdmMap.get(i).size()) break;
            while(k <= sdmMap.get(i).size()) {
                ArrayList<Float> next_prec = Precision_k(sdmMap, ++k);
                for (float next : next_prec) {
                    precs.set(j, precs.get(j) + next);
                }
            }
            precs.set(j, precs.get(j) / (float) k);
            i++;
            j++;
            if (i == 451) i = 601;
        }
        i = 301;
        for (Float prec : precisions) {
            writer.println(String.format("sdm.trecrun AP %f", prec / sdmMap.get(i++).size()));
            if (i == 451) i = 601;
        }
        precs  =  Precision_k(stressMap, 1);
        k = 1;
        i = 301;
        j = 0;
        for (float ignored : precisions) {
            if (k + 1 >= stressMap.get(i).size()) break;
            while(k <= stressMap.get(i).size()) {
                ArrayList<Float> next_prec = Precision_k(stressMap, ++k);
                for (float next : next_prec) {
                    precs.set(j, precs.get(j) + next);
                }

            }
            precs.set(j, precs.get(j) / (float) k);
            i++;
            j++;
            if (i == 451) i = 601;
        }
        i = 301;
        for (Float prec : precisions) {
            writer.println(String.format("stress.trecrun AP %f", prec / stressMap.get(i++).size()));
            if (i == 451) i = 601;
         }
         ************************************************************************/
        writer.close();
    }

    private static int findRel(int query, String doc) {
        if (rels.containsKey(query)) {
            Set<Tuple<String, Integer>> results = rels.get(query);
            for (Tuple<String, Integer> result : results) {
                if (result.x.equals(doc)) {
                    return result.y;
                }
            }
        }
        return 0;
    }

    private static float NDCG_p(LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> trecrun, int query)  {
        ArrayList<Integer> idcg = new ArrayList<>();
        Set<Tuple2<String, Integer, Float>> results = trecrun.get(query);
        float dcg = 0.0f;
        int i = 1;
        for(Tuple2<String, Integer, Float> result : results) {
            if (i == 1) {
                int rel = findRel(query, result.x);
                idcg.add(rel);
                dcg += rel;
                i++;
            }
            else {
                if (i > 16) break;
                else {
                    int rel = findRel(query, result.x);
                    idcg.add(rel);
                    dcg += (float)rel / (float)(Math.log(i) / Math.log(2));
                    i++;
                }
            }
        }
        if (idcg.size() == 0 || results.size() == 0) return 0.0f;
        float ideal = IDCG_p(idcg);
        if (ideal == 0.0f) return 0.0f;
        return dcg / ideal;
    }

    private static float IDCG_p(ArrayList<Integer> idcg) {
        idcg.sort(Comparator.reverseOrder());
        float idcgp = idcg.get(0);
        int i = 1;
        if (idcg.size() > 1) {
            for (int rel : idcg.subList(1, idcg.size() - 1)) {
                if (i == 1) {
                    idcgp += (float) rel;
                    i++;
                } else {
                    idcgp += (float) rel / (float) (Math.log(i) / Math.log(2));
                    i++;
                }
            }
        }
        return idcgp;
    }

    private static float MRR(LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> trecrun) {
        float mrr = 0.0f;
        int count = 0;
        for (Map.Entry<Integer, Tuple2<String, Integer, Float>> query : trecrun.entries()) {
            if (query.getValue().y == 1) {
                mrr += 1.0f / query.getValue().z;
                count++;
            }
        }
        return mrr / count;
    }

    private static ArrayList<Float> Precision_k(LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> trecrun, int k) {
        ArrayList<Float> precisions = new ArrayList<>();
        for (int i : trecrun.keySet()) {
            Set<Tuple2<String, Integer, Float>> set = trecrun.get(i);
            int count = 1;
            int amt = 0;
            for (Tuple2<String, Integer, Float> entry : set) {
                if (count++ > k) break;
                if (findRel(i, entry.x) != 0) amt++;
            }
            if (set.size() != 0) precisions.add((float) amt / (float) set.size());
            else precisions.add(0.0f);
        }
        return precisions;
    }

    private static ArrayList<Float> Recall_k(LinkedHashMultimap<Integer, Tuple2<String, Integer, Float>> trecrun, int k) {
        ArrayList<Float> precisions = new ArrayList<>();
        for (int i : trecrun.keySet()) {
            Set<Tuple2<String, Integer, Float>> set = trecrun.get(i);
            int count = 1;
            int amt = 0;
            for (Tuple2<String, Integer, Float> entry : set) {
                if (count++ > k) break;
                if (findRel(i, entry.x) != 0) amt++;
            }
            int j = 0;
            for (Tuple<String, Integer> value : rels.get(i)) {
                if (value.y != 0) j++;
            }
            if (j != 0) precisions.add((float) amt / (float) j);
            else precisions.add(0.0f);
        }
        return precisions;
    }
}
