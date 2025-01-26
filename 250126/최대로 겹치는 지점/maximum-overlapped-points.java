import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args)  throws IOException{

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());


        int[] checked = new int[101];
        int maxX=0;
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            int x1 = Integer.parseInt(st.nextToken());
            int x2 = Integer.parseInt(st.nextToken());
            maxX = Integer.max(maxX, x2);
            for(int j=x1; j<=x2; j++){
                checked[j]++;
            }
        }

        int result = 0;
        for(int i=0; i<=maxX; i++){
            result = Integer.max(checked[i], result);
        }
        System.out.println(result);
    }
}