import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int[] dp = new int[n+1];   


        for(int i=2; i<=n;i++){
            if(i==2 || i==3) {
                dp[i]=1;
                continue;
            }
            dp[i] = (dp[i-2]+dp[i-3])%10007;
        }
        
        System.out.println(dp[n]%10007);


    }
}