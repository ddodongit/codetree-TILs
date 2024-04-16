import java.util.*;
import java.io.*;

public class Main {

    static int N,T, arr[][], total;
    static int[] dx={-1,0,1,0}, dy={0,1,0,-1};
    static String str;
    static char[] command;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());


        str = br.readLine();
        command = str.toCharArray();
        
        arr = new int[N][N];
        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<N; j++){
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        move(0, N/2, N/2, 0, arr[N/2][N/2]);
    
        System.out.println(total);
    }

    public static void move(int command_idx, int now_x, int now_y, int dir, int count){


        if(command_idx == command.length) {
            // System.out.println(count);
            total = count;
            return;
        }
// System.out.println(command[command_idx]+" "+command_idx+" ["+ now_x+", "+ now_y+"] "+dir%4+" "+count);

        if(command[command_idx] == 'L' ){
            move(command_idx+1, now_x, now_y, (dir-1)%4,count);
        }

        else if(command[command_idx] == 'R' ){
            move(command_idx+1, now_x, now_y, (dir+1)%4 ,count);            
        }

        else if(command[command_idx] == 'F' ){   
                if(dir<0) dir = 4+dir;
                int next_x = now_x+dx[dir%4];
                int next_y = now_y+dy[dir%4];
                // System.out.println("!!"+dir%4+" "+next_x+" "+next_y);
                // System.out.println(outOfBound(next_x, next_y));
            if(!outOfBound(next_x, next_y)){
                move(command_idx+1, next_x, next_y, dir%4, count+arr[next_x][next_y]);
            }
            else{
                move(command_idx+1, now_x, now_y, dir%4, count);
            }
        }

    }


    public static boolean outOfBound(int x, int y){
        return x <0 || x > N-1 || y <0 || y > N-1;
    }
}