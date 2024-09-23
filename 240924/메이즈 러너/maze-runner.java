import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {

    static final int[] dr = {0, -1, 1, 0, 0}, dc = {0, 0, 0, -1, 1};
    static final int EMPTY = 0, EXIT = -1;
    static int N, M, K;
    static Point exitP; // 출구 좌표
    static HashMap<Integer, Point> allParticipants; // < idx, point> 모든 참가자 좌표
    static HashMap<Integer, HashSet<Integer>> participantsPos; // <2차원->1차원, id[] >
    static int[][] map; // EMPTY, WALL, EXIT만 저장
    static int totalDist; // 모든 이동 거리
    static int rotateR, rotateC, squareSize;

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        // map
        map = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // participants
        allParticipants = new HashMap<>();
        participantsPos = new HashMap<>();

        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            allParticipants.put(i, new Point(r, c));
            int num = r * N + c;
            participantsPos.computeIfAbsent(num, k -> new HashSet<>()).add(i);
        }

        // exit
        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken()) - 1;
        int c = Integer.parseInt(st.nextToken()) - 1;
        exitP = new Point(r, c);
        map[r][c] = EXIT;

        for (int i = 1; i <= K; i++) {
            if (allParticipants.isEmpty()) {
                break;
            }
            move();
            rotate();
        }

        System.out.println(totalDist);
        System.out.println((exitP.r + 1) + " " + (exitP.c + 1));
    }

    private static void move() {
        for (int i = 1; i <= M; i++) {
            if (!allParticipants.containsKey(i)) {
                continue;
            }
            Point p = allParticipants.get(i);
            int dir = getMoveDirection(p); // 가장 가까운 방향

            if (dir == 0) { // 이동 안 함
                continue;
            }

            participantsPos.get(p.r * N + p.c).remove(i);
            // 참가자 이동
            p.r += dr[dir];
            p.c += dc[dir];

            ++totalDist; // 총 이동거리

            if (map[p.r][p.c] == EXIT) { // 출구로 나감
                allParticipants.remove(i);
            }

            participantsPos.computeIfAbsent(p.r * N + p.c, k -> new HashSet<>()).add(i);

        }

    }

    private static int getMoveDirection(Point now) {

        int minDist = Integer.MAX_VALUE;
        int dir = 0;

        for (int d = 0; d < 5; d++) { // x, 상, 하, 좌, 우
            int nextR = now.r + dr[d];
            int nextC = now.c + dc[d];

            if (isOutOfBounds(nextR, nextC)) { // 범위 밖
                continue;
            }
            if (map[nextR][nextC] > 0) { // 벽
                continue;
            }

            int dist = getDistance(nextR, nextC); // 이동할 위치 ~ 출구 사이의 거리
            if (dist < minDist) {
                minDist = dist;
                dir = d;
            }
        }

        return dir;
    }

    private static void rotate() {

        findMinSquare();

        rotateSquare();

    }

    private static void rotateSquare() {

        int[][] copied = new int[squareSize][squareSize];
        HashMap<Integer, HashSet<Integer>> tmpPos = new HashMap<>();

        int col = squareSize - 1;
        int row = 0;
        for (int r = rotateR; r < rotateR + squareSize; r++) {
            row = 0;
            for (int c = rotateC; c < rotateC + squareSize; c++) {
                copied[row][col] = map[r][c];
                if (participantsPos.containsKey(r * N + c)) {
                    tmpPos.put(row * squareSize + col, participantsPos.remove(r * N + c));
                }
                row++;
            }
            col--;
        }

        // result
        row = rotateR;
        for (int r = 0; r < squareSize; r++) {
            col = rotateC;
            for (int c = 0; c < squareSize; c++) {
                map[row][col] = copied[r][c];
                if (tmpPos.containsKey(r * squareSize + c)) {
                    HashSet<Integer> list = tmpPos.get(r * squareSize + c);
                    participantsPos.put(row * N + col, list);

                    for (Integer id : list) {
                        allParticipants.replace(id, new Point(row, col));
                    }
                }

                if (map[row][col] == EXIT) {
                    exitP.r = row;
                    exitP.c = col;
                } else if (map[row][col] > 0) { // WALL
                    map[row][col]--;
                }

                col++;
            }
            row++;
        }

    }

    private static void findMinSquare() {

        int id = getNearParticipant();
        Point p = allParticipants.get(id);

        makeSquare(p);
    }

    private static void makeSquare(Point p) {

        int minR = Integer.min(exitP.r, p.r);
        int minC = Integer.min(exitP.c, p.c);

        int maxR = Integer.max(exitP.r, p.r);
        int maxC = Integer.max(exitP.c, p.c);

        int diffR = maxR - minR + 1;
        int diffC = maxC - minC + 1;

        int maxSize = Integer.max(diffR, diffC);
        int size = Integer.min(diffR, diffC);

        rotateR = minR;
        rotateC = minC;

        if (diffR < diffC) {
            // 상
            for (int r = minR - 1; r >= 0; r--) {
                if (size == maxSize) {
                    break;
                }
                size++;
                rotateR = r;
            }

            if (size < maxSize) {
                // 하
                for (int r = maxR + 1; r < N; r++) {
                    if (size == maxSize) {
                        break;
                    }
                    size++;
                }
            }
        } else if (diffR > diffC) {
            // 좌
            for (int c = minC - 1; c >= 0; c--) {
                if (size == maxSize) {
                    break;
                }
                size++;
                rotateC = c;
            }

            if (size < maxSize) {
                // 우
                for (int c = maxC + 1; c < N; c++) {
                    if (size == maxSize) {
                        break;
                    }
                    size++;
                }
            }
        }

        squareSize = size;
    }

    private static int getNearParticipant() {
        int minDist = Integer.MAX_VALUE;
        Point minP = new Point(0, 0);
        int result = 0;

        for (Integer id : allParticipants.keySet()) {
            Point p = allParticipants.get(id);
            int dist = getDistance(p.r, p.c);

            if (dist < minDist) {
                minDist = dist;
                result = id;
                minP = p;
            } else if (dist == minDist) {
                if (p.r < minP.r) {
                    minP = p;
                    result = id;
                } else if (minP.r == p.r) {
                    if (p.c < minP.c) {
                        minP = p;
                        result = id;
                    }
                }
            }
        }

        return result;
    }


    private static boolean isOutOfBounds(int r, int c) {
        return r < 0 || r > N - 1 || c < 0 || c > N - 1;
    }

    private static int getDistance(int r, int c) {
        return Math.abs(exitP.r - r) + Math.abs(exitP.c - c);
    }


    static class Point {

        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

    }
}