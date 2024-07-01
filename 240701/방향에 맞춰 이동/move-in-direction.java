import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int[] dx = {0,1,0,-1}, dy={1,0,-1,0};
        int x = 0, y=0;
        int dir_idx = -1;

        int N = Integer.parseInt(br.readLine());

        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());

            char dir = st.nextToken().charAt(0);
            int dist = Integer.parseInt(st.nextToken());

            if(dir == 'N') dir_idx = 0;
            else if(dir == 'E') dir_idx = 1;
            else if(dir == 'S') dir_idx = 2;
            else if(dir == 'W') dir_idx = 3;

            x += dx[dir_idx]*dist;
            y += dy[dir_idx]*dist;
        }
        System.out.println(x +" "+y);

    }
}