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
            dp[i] = (dp[i-1]+dp[i-2])%10007;
        }
        
        System.out.println(dp[n]%10007);


    }
}