import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static final int[] di = {-1, 0, 1, 0}, dj = {0, 1, 0, -1};
    static int K, M, maxValue, degree;
    static int midR, midC;
    static int[][] map, rotatedMap;
    static Queue<Integer> newNumbers;
    static PriorityQueue<Point> nowPieces;


    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();

        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        map = new int[6][6];
        rotatedMap = new int[6][6];

        for (int i = 1; i <= 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= 5; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        newNumbers = new ArrayDeque<>();
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            int num = Integer.parseInt(st.nextToken());
            newNumbers.add(num);
        }

        for (int i = 0; i < K; i++) { // !!! K로 수정
            maxValue = Integer.MIN_VALUE;
            degree = Integer.MAX_VALUE;

            int result = explore();

            if (result == -1) {
                break;
            }
            rotatedMap = new int[6][6];

            sb.append(result).append(" ");
        }

        System.out.println(sb);
    }

    private static int explore() {

        int total = 0;

        // rotate and find max value
        for (int r = 2; r <= 4; r++) {
            for (int c = 2; c <= 4; c++) {
                rotate(r, c, 90);
                rotate(r, c, 180);
                rotate(r, c, 270);
            }
        }


        // max value rotated map
        map = rotatedMap;

        if (nowPieces.isEmpty()) {
            return -1;
        }

        total += nowPieces.size();
        while (true) {
            int result = getTreasure();
            if (result == 0) {
                break;
            }

            total += result;
        }

        return total;
    }

    private static int getTreasure() {

        // change treasure
        while (!nowPieces.isEmpty()) {
            Point p = nowPieces.poll();
            int num = newNumbers.poll();

            map[p.i][p.j] = num;
        }

        HashSet<Integer> selected = findTreasure(map);

        if (selected.isEmpty()) {
            return 0;
        }

        nowPieces = new PriorityQueue<>();
        for (Integer idx : selected) {
            nowPieces.add(new Point((idx - 1) / 5 + 1, (idx - 1) % 5 + 1));
        }

        return selected.size();
    }

    private static void rotate(int nowR, int nowC, int nowDegree) {

        int row = 0, col = 0;

        int[][] tmpMap = copyMap();

        switch (nowDegree) {
            case 90:
                col = nowC + 1;
                for (int i = nowR - 1; i <= nowR + 1; i++) {
                    row = nowR - 1;
                    for (int j = nowC - 1; j <= nowC + 1; j++) {
                        tmpMap[row++][col] = map[i][j];
                    }
                    col--;
                }
                break;
            case 180:
                row = nowR + 1;
                for (int i = nowR - 1; i <= nowR + 1; i++) {
                    col = nowC + 1;
                    for (int j = nowC - 1; j <= nowC + 1; j++) {
                        tmpMap[row][col--] = map[i][j];
                    }
                    row--;
                }
                break;
            case 270:
                row = nowR - 1;
                for (int i = nowR - 1; i <= nowR + 1; i++) {
                    col = nowC + 1;
                    for (int j = nowC - 1; j <= nowC + 1; j++) {
                        tmpMap[col--][row] = map[i][j];
                    }
                    row++;
                }
                break;
        }

        HashSet<Integer> selected = findTreasure(tmpMap);

        if (selected.size() > maxValue) {
            maxValue = selected.size();
            degree = nowDegree;

            change_map(nowR, nowC, tmpMap, selected);
        } else if (selected.size() == maxValue) {
            if (nowDegree < degree) {
                degree = nowDegree;
                change_map(nowR, nowC, tmpMap, selected);
            } else if (degree == nowDegree) {
                if (nowC < midC) {
                    change_map(nowR, nowC, tmpMap, selected);
                } else if (nowC == midC) {
                    if (nowR < midR) {
                        change_map(nowR, nowC, tmpMap, selected);
                    }
                }

            }
        }

    }

    private static int[][] copyMap() {
        int[][] tmpMap = new int[6][6];

        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                tmpMap[i][j] = map[i][j];
            }
        }
        return tmpMap;
    }

    private static void change_map(int r, int c, int[][] tmpMap, HashSet<Integer> selected) {

        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                rotatedMap[i][j] = tmpMap[i][j];
            }
        }

        midR = r;
        midC = c;

        nowPieces = new PriorityQueue<>();
        for (Integer idx : selected) {
            nowPieces.add(new Point((idx - 1) / 5 + 1, (idx - 1) % 5 + 1));
        }


    }

    private static HashSet<Integer> findTreasure(int[][] tmpMap) {

        HashSet<Integer> selected = new HashSet<>();

        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (selected.contains((i - 1) * 5 + j)) {
                    continue;
                }
                HashSet<Integer> result = bfs(i, j, tmpMap);
                if (result.size() >= 3) {
                    selected.addAll(result);
                }
            }
        }

        return selected;
    }

    private static HashSet<Integer> bfs(int start_i, int start_j, int[][] tmpMap) {
        int nowValue = tmpMap[start_i][start_j];

        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        queue.add(start_i);
        queue.add(start_j);

        while (!queue.isEmpty()) {
            int nowI = queue.poll();
            int nowJ = queue.poll();

            visited.add((nowI - 1) * 5 + nowJ);

            for (int d = 0; d < 4; d++) {
                int nextI = nowI + di[d];
                int nextJ = nowJ + dj[d];

                if (isOutOfBounds(nextI, nextJ)) {
                    continue;
                }
                if (tmpMap[nextI][nextJ] != nowValue) {
                    continue;
                }
                if (visited.contains((nextI - 1) * 5 + nextJ)) {
                    continue;
                }

                queue.add(nextI);
                queue.add(nextJ);

            }
        }

        return visited;
    }

    private static boolean isOutOfBounds(int i, int j) {
        return i < 1 || i > 5 || j < 1 || j > 5;
    }


    static class Point implements Comparable<Point> {

        int i, j;

        public Point(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public int compareTo(Point o) {

            if (this.j == o.j) {
                return Integer.compare(o.i, this.i);
            } else {
                return this.j - o.j;
            }
        }

        @Override
        public String toString() {
            return "Point{" +
                "i=" + i +
                ", j=" + j +
                '}';
        }
    }
}

// 아래 전체를 K번 반복, 턴마다 총합 출력, 0인 턴에서 중간 종료.
//// 1. 탐사
/*
중심이 될 수 있는 좌표 : 2 <= r, c <= 4 에서, 각도별로 회전해보고

가상 배열에서 최대 유물 획득 가치 비교. 가치 큰 순, 회전 각도 작은순으로.
=> maxValue, degree

여기서 pieces 만들어야 함.  기준 pieces와 비교대상 tmp_pieces 구분.
이번 턴에서 아무리 회전해도 유물 없으면 종료.



 */

//// 2. 유물 획득 (같은 숫자 3개 이상 연결, 없을때까지 반복)
        /*
        Point class 만들고,
        유물의 좌표를 열 번호 작은 순, 행 번호 큰 순서로 PQ에 저장해서 뽑아서 숫자 변경.
        => PQ<Point> pieces;

        새로 쓸 숫자는 Queue에 저장? => Queue<Integer> newNumbers;
        */