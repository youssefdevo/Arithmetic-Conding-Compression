
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public void E1(Double lower, Double upper) {
        lower = lower * 2;
        upper = upper * 2;
    }

    public static void main(String[] args) {

        HashMap<Character, Double> frq = new HashMap<>();
        HashMap<Character, Double> lower = new HashMap<>();
        HashMap<Character, Double> upper = new HashMap<>();
        System.out.println("1-Compression.\n2-Decompression.");
        Scanner in = new Scanner(System.in);
        int ord = in.nextInt();
        if (ord == 1) {

            //read from file.
            String input = "";

            try {
                File Obj = new File("input.txt");
                Scanner Reader = new Scanner(Obj);
                input = Reader.nextLine();
                Reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error has occurred.");
                e.printStackTrace();
            }
            for (int i = 0; i < input.length(); i++) {
                if (frq.containsKey(input.charAt(i))) {
                    double x = frq.get(input.charAt(i));
                    frq.put(input.charAt(i), x + 1);
                } else {
                    frq.put(input.charAt(i), 1.0);
                }
            }
            HashMap<Character, Double> probability = new HashMap<>();
            double min = 100000;
            for (Map.Entry<Character, Double> entry : frq.entrySet()) {
                Double prob = entry.getValue() / input.length();


                min = Math.min(min, prob);

                probability.put(entry.getKey(), prob);
            }

            //  1/(2^k) < min
            int k = 0;
            while (1 / (Math.pow(2, k)) >= min) {
                k++;
            }
            double r = 0;
            for (Map.Entry<Character, Double> entry : probability.entrySet()) {
                double cLower = r, cUpper = r + entry.getValue();
                lower.put(entry.getKey(), cLower);
                upper.put(entry.getKey(), cUpper);
                r = cUpper;
            }
            String scaling = "";
            //Compress
            double lo = -1, up = -1;
            for (int i = 0; i < input.length(); i++) {
                if (i == 0) {
                    lo = lower.get(input.charAt(i));
                    up = upper.get(input.charAt(i));
                } else {
                    double lo1 = lo + (up - lo) * lower.get(input.charAt(i));
                    double up1 = lo + (up - lo) * upper.get(input.charAt(i));
                    lo = lo1;
                    up = up1;
                }
                while (!(lo <= 0.5 && up >= 0.5)) {
                    //need E1
                    if (lo < 0.5 && up < 0.5) {
                        lo = lo * 2;
                        up = up * 2;
                        scaling += "0";
                    }
                    //need E2
                    else if (lo > 0.5 && up > 0.5) {
                        lo = (lo - 0.5) * 2;
                        up = (up - 0.5) * 2;
                        scaling += "1";
                    }
                }
            }
            for (int i = 0; i < k; i++) {
                if(i == 0)scaling+="1";
                else scaling+="0";
            }
            try {
                FileWriter Writer = new FileWriter("Compressed.txt");
                Writer.write(String.valueOf(k)+"\n");
                Writer.write(scaling);
                Writer.close();
            } catch (IOException e) {
                System.out.println("An error has occurred.");
                e.printStackTrace();
            }
        }
        else{
            //Decompress
            // read
            int k = 0;
            String scale = "";
            try {
                File Obj = new File("Compressed.txt");
                Scanner Reader = new Scanner(Obj);
                k = Integer.parseInt(Reader.nextLine());
                scale = Reader.nextLine();
                Reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error has occurred.");
                e.printStackTrace();
            }

            int i = 0;
            String temp="";
            int c = i;
            for (; i < (k+c); i++) {
                temp+=scale.charAt(i);
            }
            double code =0;
            for (int j = temp.length()-1; j >= 0; j--) {
                if(temp.charAt(j)=='1'){
                    code+=Math.pow(2,j);
                }
            }
            String ans = "";
            code/=Math.pow(2,k);
            double low = 0,up = 1;
            for (Map.Entry<Character, Double> entry : frq.entrySet()){
                double lo = lower.get(entry.getKey());
                double ro = upper.get(entry.getKey());
                if(lo <=code && ro >=code){
                    ans+=entry.getKey();

                    double l = low + (up-low) * lower.get(entry.getKey());
                    double r = low + (up-low) * upper.get(entry.getKey());
                    while (!(l <= 0.5 && r >= 0.5)) {
                        //need E1
                        if (l < 0.5 && r < 0.5) {
                            l = l * 2;
                            r = r * 2;
                            temp="";
                            c = i;
                            for (; i < Math.min((k+c),scale.length()); i++) {
                                temp+=scale.charAt(i);
                            }
                            code = 0;
                            for (int j = temp.length()-1; j >= 0; j--) {
                                if(temp.charAt(j)=='1'){
                                    code+=Math.pow(2,j);
                                }
                            }
                            code/=Math.pow(2,k);
                        }
                        //need E2
                        else if (l > 0.5 && r > 0.5) {
                            l = (l - 0.5) * 2;
                            r = (r - 0.5) * 2;
                            temp="";
                            c = i;
                            for (; i < Math.min((k+c),scale.length()); i++) {
                                temp+=scale.charAt(i);
                            }
                            code = 0;
                            for (int j = temp.length()-1; j >= 0; j--) {
                                if(temp.charAt(j)=='1'){
                                    code+=Math.pow(2,j);
                                }
                            }
                            code/=Math.pow(2,k);
                        }
                    }
                    low = l;
                    up = r;
                 code = (code - 0) / (up - low);
                }
            }
            try {
                FileWriter Writer = new FileWriter("DeCompressed.txt");
                Writer.write(ans);
                Writer.close();
            } catch (IOException e) {
                System.out.println("An error has occurred.");
                e.printStackTrace();
            }
        }

    }
}