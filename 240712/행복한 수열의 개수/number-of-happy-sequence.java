import java.util.*;
import java.io.*;

public class Main {
    static int n, m, map[][];

    public static void main(String[] args)throws IOException  {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;


        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
      
        int count = 0;
        for(int i=0; i<n; i++){
            HashMap<Integer, Integer> r_count = new HashMap();
            HashMap<Integer, Integer> c_count = new HashMap();
            
            for(int j=0; j<n; j++){
                if(r_count.containsKey(map[i][j])) r_count.replace(map[i][j], r_count.get(map[i][j])+1);
                else r_count.put(map[i][j],1);

                if(c_count.containsKey(map[i][j])) c_count.replace(map[i][j], c_count.get(map[i][j])+1);
                else c_count.put(map[i][j],1);

            }
            for(Integer value : r_count.values()){
                if(value == m) count++;
            }
            for(Integer value : c_count.values()){
                if(value == m) count++;
            }
        }

        System.out.println(count);
    }
}