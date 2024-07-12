import java.util.*;
import java.io.*;

public class Main {
    static int n,m,map[][], minCost= Integer.MAX_VALUE, maxCount = Integer.MIN_VALUE, goldCnt;
    static int[] di = {-1,0,1,0}, dj={0,1,0,-1};
    
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

        goldCnt = 0;
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
                if(map[i][j] ==1) goldCnt++;
            }
        }

        // bfs(3,3);
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                // System.out.println("start i : "+ i +" start j :" +j);
                bfs(i,j);
                // System.out.println();
            }
        }

        System.out.println(maxCount == Integer.MIN_VALUE ? 0: maxCount);
    }


    static void bfs(int start_i, int start_j){
        Queue<Point> queue = new ArrayDeque();
        boolean[][] isVisited = new boolean[n][n];
        int totalCost = 0;
        int count = 0;


        queue.add(new Point(start_i, start_j, 0));


        while(!queue.isEmpty()){
            Point now = queue.poll();
            totalCost = getCost(now.dist);
            if(!isVisited[now.i][now.j] && map[now.i][now.j]==1){
                count += 1;
                // System.out.println("get: " + now);
                // System.out.println("cost: "+ totalCost+" m*count: "+ (m*count));
                if(totalCost <= m*count) {
                    // System.out.println(totalCost+" "+ (m*count)+" count: "+count);
                    maxCount = Integer.max(maxCount, count);
                    if (goldCnt == count) return;
                    // int remains = goldCnt-cnt;
                    // if()
                
                }
            }
            isVisited[now.i][now.j]= true;
            

            for(int d=0; d<4; d++){
                int next_i = now.i+di[d];
                int next_j = now.j+dj[d];

                if(isOutOfBounds(next_i, next_j)) continue;
                if(isVisited[next_i][next_j]) continue;

                queue.add(new Point(next_i, next_j, now.dist+1));

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