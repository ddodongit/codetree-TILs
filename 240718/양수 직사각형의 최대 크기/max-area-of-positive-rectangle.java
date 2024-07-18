import java.util.*;
import java.io.*;

public class Main {
    static int n, m, map[][], maxArea;
    static int[] di = {-1, 0, 1, 0}, dj = {0, 1, 0, -1};

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

        int colSize = 0;

        for(int j=start_j; j<m ;j++) {
            if(map[start_i][j] < 0) {
                break;
            }
            colSize++;
        }


        int maxColSize = colSize-1;
        int s = maxColSize;
        for(int i=start_i+1; i<n; i++){
            for(s=maxColSize; s>=0; s--){
                if(map[i][start_j+s] < 0) {
                    colSize--;
                    int rowSize = i - start_i;
                    int area = (colSize+1)*rowSize;
                    // System.out.println("return : "+colSize+ " "+ rowSize);
                    // return;
                }
                if(map[i][s]>0) {
                    // System.out.println(map[i][start_j+s]+" ");
                    int rowSize = i - start_i+1;
                    // System.out.println(rowSize + " "+i);
                    int area = colSize*rowSize;
                    // System.out.println("return : "+colSize+ " "+ rowSize); 
                    return area;
                }
                // System.out.println(map[i][start_j+s]+" ");
            }
        }

        int rowSize = n-1-start_i;
        // System.out.println(start_i);
        // System.out.println("return : "+colSize+ " "+ (rowSize+1));
        int area = colSize * (rowSize+1);
        return area;
    }

}