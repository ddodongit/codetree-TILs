import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;


        PriorityQueue<Integer> pq = new PriorityQueue<>();
        int N = Integer.parseInt(br.readLine());
        st = new StringTokenizer(br.readLine());
        
        for(int i=0; i<N; i++){
            int num = Integer.parseInt(st.nextToken());
            pq.add(num);
        }
        
        while(!pq.isEmpty()){
            System.out.print(pq.poll()+" ");
        }
    }
}