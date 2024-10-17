import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int[] dp = new int[n+1];   


        for(int i=1; i<=n;i++){
            if(i==1 || i==2) {
                dp[i]=i;
                continue;
            }
            for(int j=0; j<i;j++){
                if(j==i-1-j) dp[i]+=1;
                else dp[i]+=dp[j]+dp[i-1-j];
            }
        }
        
        System.out.println(dp[n]);


    }
}