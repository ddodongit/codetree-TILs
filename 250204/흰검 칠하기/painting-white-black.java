import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args)  throws IOException {
        // Please write your code here.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int n = Integer.parseInt(br.readLine());
        int[] black = new int[200000];
        int[] white = new int[200000];
        int[] arr = new int[200000];
        final int BLACK = 1, WHITE = -1;

        int min = 100000;
        int max = 100000;

        int now = 100000;
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            int dist = Integer.parseInt(st.nextToken());
            char dir = st.nextToken().charAt(0);

            if(dir == 'R'){
                arr[now] = BLACK;
                black[now]++;
                for(int j=0; j<dist-1; j++){
                    now++;
                    arr[now] = BLACK;
                    black[now]++;
                    min = Integer.min(min, now);
                    max = Integer.max(max, now);
                }
            }

            else {
                arr[now] = WHITE;
                white[now]++;

                for(int j=0; j<dist-1; j++){
                    --now;
                    arr[now] = WHITE;
                    white[now]++;
                    min = Integer.min(min, now);
                    max = Integer.max(max, now);
                }
            }
        }

        int grayCnt = 0, blackCnt = 0, whiteCnt = 0;

        for(int i=min; i<=max; i++){
            if(black[i] >= 2 && white[i] >=2){
                grayCnt++;
            }else{
                if(arr[i] == BLACK) blackCnt++;
                else if(arr[i] == WHITE) whiteCnt++;
            }
            // System.out.println(arr[i] + " "+ black[i] + " "+ white[i]);
        }

        System.out.println(whiteCnt + " "+ blackCnt + " "+ grayCnt);
    }
}