import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

    static int N, arr[][];
    static int maxTotal = Integer.MIN_VALUE;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());

        arr = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 2; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (j == 0 || j == N - 1) {
                    continue;
                }
                findSquare(i, j);
            }
        }

        System.out.println(maxTotal);

    }

    private static void findSquare(int startI, int startJ) {
        int[] di = {0, -1, -1, 1, 1}, dj = {0, 1, -1, -1, 1};
        int rowSize = 0, colSize = 0, total = 0;

        // d=1
        int nextI = startI;
        int nextJ = startJ;

        while (!isOutOfBounds(nextI + di[1], nextJ + dj[1])) {
            rowSize++;
            nextI = nextI + di[1];
            nextJ = nextJ + dj[1];
            total += arr[nextI][nextJ];
        }

        // d=2
        if (rowSize == 0) {
            return;
        }

        while (!isOutOfBounds(nextI + di[2], nextJ + dj[2])) {
            colSize++;
            nextI = nextI + di[2];
            nextJ = nextJ + dj[2];
            total += arr[nextI][nextJ];
        }

        // d=3
        if (colSize == 0) {
            return;
        }

        for (int i = 0; i < rowSize; i++) {
            if (isOutOfBounds(nextI + di[3], nextJ + dj[3])) {
                return;
            }
            nextI = nextI + di[3];
            nextJ = nextJ + dj[3];
            total += arr[nextI][nextJ];
        }

        // d=4

        for (int i = 0; i < colSize; i++) {
            if (isOutOfBounds(nextI + di[4], nextJ + dj[4])) {
                return;
            }
            nextI = nextI + di[4];
            nextJ = nextJ + dj[4];
            total += arr[nextI][nextJ];
        }

        if (nextI == startI && nextJ == startJ) {
            maxTotal = Math.max(maxTotal, total);
        }
    }

    private static boolean isOutOfBounds(int i, int j) {
        return i < 0 || i > N - 1 || j < 0 || j > N - 1;
    }

}