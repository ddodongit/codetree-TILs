import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Please write your code here.

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int[] arr = new int[2000];                

        int now = 1000;
        int N = Integer.parseInt(br.readLine());
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;


        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());
            int offset = Integer.parseInt(st.nextToken());
            char ch = st.nextToken().charAt(0);

            if(ch == 'R'){
                for(int j=0; j< offset; j++){
                    arr[now++]++;
                    min = Integer.min(now, min);
                    max = Integer.max(now, max);
                }
            }
            else{
                for(int j=0; j< offset; j++){
                    arr[--now]++;
                    min = Integer.min(now, min);
                    max = Integer.max(now, max);
                }
            }

        }
        int count = 0;
        for(int i=min; i<=max; i++){
            if(arr[i] >=2) count++;
        }

        System.out.println(count);
        
    }
}