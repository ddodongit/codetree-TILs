import java.util.*;
import java.io.*;

public class Main {
    static int n, m, map[][];

    public static void main(String[] args)throws IOException  {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;


        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        for(int i=0; i<n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<n; j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }


        int r_count=0;
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                int count = 1;
                int k=j+1;
                for(k=j+1; k<n; k++){
                    if(map[i][j]!= map[i][k]){
                        break;
                    }
                    count++;
                }
                if(count>=m){
                    r_count += 1;
                    j+=count;
                }

            }
        }   

        int c_count=0;
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                int count = 1;
                int k=j+1;
                for(k=j+1; k<n; k++){
                    if(map[j][i]!= map[k][i]){
                        break;
                    }
                    count++;
                }
                if(count>=m){
                    c_count += 1;
                    j+=count;
                }

            }
        }   
        System.out.println(r_count + c_count);
    }
}