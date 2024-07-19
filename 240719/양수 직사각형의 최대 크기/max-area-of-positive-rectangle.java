import java.util.*;
import java.io.*;

public class Main {
    static int n, m, map[][], maxArea;
    static int[] di = {0,1}, dj = {1,0};


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());


        map = new int[n][m];
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<m; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
 
        maxArea = Integer.MIN_VALUE;
        // int maxArea = findSquare(0,0);
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                if(map[i][j] > 0){
                    // System.out.println("start : "+ i + " "+j);
                    int area = findSquare(i,j);
                    // System.out.println("area: "+ area);
                    maxArea = Integer.max(area, maxArea);
                }
            }
        }

        System.out.println(maxArea == Integer.MIN_VALUE ? -1 : maxArea);
    }

    static int findSquare(int start_i, int start_j){

        final int RIGHT = 0, DOWN = 1;
        int area = 0;
        int colSize = 1;
        int rowSize = 1;
        int dir = RIGHT;


        int now_i = start_i;
        int now_j = start_j;


        // right
        while (true){
            now_i = now_i + di[dir];    
            now_j = now_j + dj[dir];    

            if(now_i >= n || now_j >= m) break;
            if(map[now_i][now_j]<0) {
                break;
            }
            
            colSize++;
        }

        area = colSize;
        // System.out.println("area: " +colSize);
        // down
        boolean flag = false;
        for(int i=start_i+1; i<n; i++){
            int j = start_j;
            for(j=start_j; j<start_j+colSize; j++){
                if(map[i][j]<0){
                    flag = true;
                    area = Integer.max(area,rowSize*colSize);
                    colSize--;
                    // System.out.println("rowsize : "+rowSize + " colsize: "+colSize);   
                    // System.out.println("area: "+area);   
                    // System.out.println("minus: "+i+ " "+j);   
                    break;
                }
                
                // System.out.println(map[i][j]);
            }
            if(j==start_j+colSize){
                rowSize++;
                area = Integer.max(area,rowSize*colSize);
            }
        }



        // System.out.println("area : "+area);
        return area;
    }

}

// 1 2 3 4 -1
// 3 3 4 -1 4