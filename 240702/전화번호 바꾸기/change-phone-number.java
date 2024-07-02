import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine(),"-");
        String str1 = st.nextToken();
        String str2 = st.nextToken();
        String str3 = st.nextToken();
        System.out.println(str1+"-"+str3+"-"+str2);
        

    }
}