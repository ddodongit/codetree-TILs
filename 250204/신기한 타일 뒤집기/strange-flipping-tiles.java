import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int n = Integer.parseInt(br.readLine());
        int[] arr = new int[200000];

        int min = 100000;
        int max = 100000;

        int now = 100000;
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            char ch = st.nextToken().charAt(0);
            

            int dir = ch == 'R' ? 1: -1;
            now += -dir;
            for(int j=0; j<x; j++){
                now += dir;
                arr[now] = dir;
                
                min = Integer.min(min, now);
                max = Integer.max(max, now);
            }
        }

        int black = 0, white = 0;
        for(int i=min; i<=max; i++){
            if(arr[i] == 1) black++;
            else white++;
            // System.out.println(arr[i]);
        }
        System.out.println(white + " " + black);
        

    }
}