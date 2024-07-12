import java.util.*;
import java.io.*;

public class Main {
    static int n,m,map[][], max_total = Integer.MIN_VALUE;

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

        checkBlock1(); 
        checkBlock2(n,m, true);
        checkBlock2(m,n, false);

        System.out.println(max_total);
    }

    static void checkBlock1(){
    
        int sum = 0;
        for(int i=0; i<n-1; i++){
            for(int j=0; j<m-1; j++){ 
                sum = checkSquare(i,j,sum);
            }
        }
    }

    static int checkSquare(int row, int col, int beforeSum){
        /*  _       _ _     _ _       _
            _ _     _         _     _ _
            2        4       3      1
            2*2 에서 하나 뺀 값
        */ 

        // 4개 sum 구하고 max 값 구하기

        int sum = beforeSum;

        if(col == 0){
            if(row ==0){
                sum += map[row][col] + map[row][col+1] + map[row+1][col] + map[row+1][col+1];
            }
            else {
                sum = map[row-1][col] + map[row-1][col+1];
                sum += map[row+1][col] + map[row+1][col+1];
            }
        }
        else{
            sum -= map[row][col-1] + map[row+1][col-1];
            sum += map[row][col+1] + map[row+1][col+1];    
        }



        for(int r= row; r<= row+1; r++){
            for(int c= col; c<=col+1; c++){
                sum -= map[r][c];
                max_total = Integer.max(sum, max_total);
                sum += map[r][c];
            }
        }

        return sum;

    }

    static void checkBlock2(int y, int x, boolean isRow){
        
        for(int i=0; i<y; i++){
            int j=0;
            int start = j;
            int end = start+2;
            int sum = 0;
            while(start<x){
                if(isRow) sum += map[i][start];
                else sum += map[start][i];

                if(start == end){
                    max_total = Integer.max(sum, max_total);
                    if(isRow) sum -= map[i][start-2];
                    else sum -= map[start-2][i];
                    end += 1;
                }
                start++;
            }
        }        
    }

}