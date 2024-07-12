import java.util.*;
import java.io.*;

public class Main {
    static int n,m,map[][], minCost= Integer.MAX_VALUE, maxCount = Integer.MIN_VALUE, goldCnt;
    static int[] di = {-1,0,1,0}, dj={0,1,0,-1};
    static ArrayList<Point> golds;

    
    static class Point{

        int i, j, dist;

        Point(int i, int j, int dist){
            this.i = i;
            this.j = j;
            this.dist = dist;
        }

        @Override
        public String toString(){
            return i + " "+ j +" "+dist;
        }
    }
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;


        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        golds = new ArrayList();
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
                if(map[i][j] ==1) {
                    golds.add(new Point(i,j,0));
                }
            }
        }


        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                calDist(i,j);
            }
        }

        System.out.println(maxCount == Integer.MIN_VALUE ? 0: maxCount);
    }

    static void calDist(int start_i, int start_j){
        PriorityQueue<Point> pq = new PriorityQueue(new Comparator<Point>(){
            @Override
            public int compare(Point p1, Point p2){
                return p1.dist-p2.dist;
            }
        });


        for(int index=0; index<golds.size(); index++){
            Point now_gold = golds.get(index);
            int dist = Math.abs(start_i - now_gold.i)+Math.abs(start_j - now_gold.j);
            now_gold.dist = dist;
            pq.add(now_gold);
        }

        int totalCost = 0;
        int count = 0;
        while(!pq.isEmpty()){
            Point gold = pq.poll();   
            count++;
            totalCost = getCost(gold.dist);
         
            if(totalCost <= m*count) {
                maxCount = Integer.max(maxCount, count);
            }
        }
    }

    static int getCost(int K){
        return (K*K) + (K+1)*(K+1);
    }

    static boolean isOutOfBounds(int i, int j){
        return i<0 || i>n-1 || j<0 || j>n-1;
    }
}