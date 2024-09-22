import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Main {

    static final int[] dr_8 = {0, -1, -1, -1, 0, 1, 1, 1, 0}, dc_8 = {0, -1, 0, 1, 1, 1, 0, -1, -1};
    static final int[] dr_4 = {0, -1, 0, 1, 0}, dc_4 = {0, 0, 1, 0, -1};
    private static final int EMPTY = 0, RUDOLPH = -1; // SANTA (1~P)
    static int N, M, P, C, D;
    static int[][] map;
    static int nowTurn;
    static Point rudolph;
    static HashMap<Integer, Point> allSanta; // < idx, (r,c) >
    static HashMap<Integer, Integer> faintedSanta; // < idx, k+2 turn >
    static TreeMap<Integer, Integer> santaScore; // < idx, score >

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        map = new int[N + 1][N + 1];

        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        rudolph = new Point(r, c);
        map[r][c] = RUDOLPH;

        allSanta = new HashMap<>();
        faintedSanta = new HashMap<>();
        santaScore = new TreeMap<>();

        for (int i = 1; i <= P; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            r = Integer.parseInt(st.nextToken());
            c = Integer.parseInt(st.nextToken());

            allSanta.put(idx, new Point(r, c));
            map[r][c] = idx;
            santaScore.put(idx, 0);
        }

        for (int i = 1; i <= M; i++) {
            if (allSanta.isEmpty()) {
                break;
            }
            nowTurn = i;
            playTurn();

            for (Integer idx : allSanta.keySet()) {
                santaScore.replace(idx, santaScore.get(idx) + 1);
            }
        }

        for (Integer idx : santaScore.keySet()) {
            System.out.print(santaScore.get(idx)+" ");
        }

    }

    private static void playTurn() {

        int minDist = Integer.MAX_VALUE;
        Point result = new Point(0, 0);
        int dir = 0;

        // 가장 가까운 산타 뽑기
        for (Integer sId : allSanta.keySet()) {
            Point santa = allSanta.get(sId);
            int dist = getDist(rudolph.r, rudolph.c, santa.r, santa.c);

            if (dist < minDist) {
                minDist = dist;
                result.r = santa.r;
                result.c = santa.c;
            } else if (dist == minDist) {
                if (result.r < santa.r) {
                    result.r = santa.r;
                    result.c = santa.c;
                } else if (result.r == santa.r) {
                    if (result.c < santa.c) {
                        result.c = santa.c;
                    }
                }
            }
        }

        dir = getRudolphDirection(rudolph, result);
        moveRudolph(dir);

    }


    private static void moveRudolph(int dir) {

        if (map[rudolph.r + dr_8[dir]][rudolph.c + dc_8[dir]] != EMPTY) { // collision
            collide(rudolph.r, rudolph.c, rudolph.r + dr_8[dir], rudolph.c + dc_8[dir], dir);
        }

        map[rudolph.r][rudolph.c] = EMPTY;

        rudolph.r += dr_8[dir];
        rudolph.c += dc_8[dir];

        map[rudolph.r][rudolph.c] = RUDOLPH;

        moveAllSanta();


    }

    private static void moveAllSanta() {

        for (int i = 1; i <= P; i++) {
            if (!allSanta.containsKey(i)) {
                continue;
            }
            Point santa = allSanta.get(i);

            // 기절한 산타 건너뛰기
            if (faintedSanta.containsKey(i) && faintedSanta.get(i) >= nowTurn) {
                if (faintedSanta.get(i) == nowTurn) {
                    faintedSanta.remove(i);
                }
                continue;
            }

            int dir = getSantaDirection(santa, rudolph, i);

            if (map[santa.r + dr_4[dir]][santa.c + dc_4[dir]] != EMPTY) { // collision
                collide(santa.r, santa.c, santa.r + dr_4[dir], santa.c + dc_4[dir], dir);
            } else {
                map[santa.r][santa.c] = EMPTY;

                santa.r += dr_4[dir];
                santa.c += dc_4[dir];

                map[santa.r][santa.c] = i;

            }


        }
    }

    private static void collide(int from_r, int from_c, int to_r, int to_c, int dir) {

        if (map[from_r][from_c] == RUDOLPH) { // 루돌프가 움직여서 충돌
            int idx = map[to_r][to_c];
            Point movedSanta = allSanta.get(idx);

            movedSanta.r += dr_8[dir] * C;
            movedSanta.c += dc_8[dir] * C;

            santaScore.replace(idx, santaScore.get(idx) + C);

            map[to_r][to_c] = EMPTY;

            if (isOutOfBounds(movedSanta.r, movedSanta.c)) { // 탈락
                allSanta.remove(idx);
            } else {
                faintedSanta.put(idx, nowTurn + 1);

                if (map[movedSanta.r][movedSanta.c] == EMPTY) {
                    map[movedSanta.r][movedSanta.c] = idx;
                } else { // 밀려난 칸에 다른 산타가 있는 경우 상호작용 발생
                    interact(movedSanta.r, movedSanta.c, dr_8, dc_8, dir);
                    map[movedSanta.r][movedSanta.c] = idx;

                }
            }


        } else { // 산타가 움직여서 충돌
            int idx = map[from_r][from_c];
            dir = getOppositeDirection(dir);
            Point movedSanta = allSanta.get(idx);

            movedSanta.r = rudolph.r + dr_4[dir] * D;
            movedSanta.c = rudolph.c + dc_4[dir] * D;

            santaScore.replace(idx, santaScore.get(idx) + D);

            map[from_r][from_c] = EMPTY;
            if (isOutOfBounds(movedSanta.r, movedSanta.c)) { // 탈락
                allSanta.remove(idx);
            } else {
                faintedSanta.put(idx, nowTurn + 1);

                if (map[movedSanta.r][movedSanta.c] == EMPTY) {
                    map[movedSanta.r][movedSanta.c] = idx;

                } else { // 산타와 산타 충돌 => 상호작용
                    interact(movedSanta.r, movedSanta.c, dr_4, dc_4, dir);
                    map[movedSanta.r][movedSanta.c] = idx;
                }
            }

        }

    }

    private static int getOppositeDirection(int dir) {
        switch (dir) {
            case 1:
                return 3;
            case 2:
                return 4;
            case 3:
                return 1;
            case 4:
                return 2;
        }
        return 0;
    }

    private static void interact(int r, int c, int[] dr, int[] dc, int dir) {

        int sId = map[r][c];
        Point santa = allSanta.get(sId);

        santa.r += dr[dir];
        santa.c += dc[dir];

        if (isOutOfBounds(santa.r, santa.c)) {
            allSanta.remove(sId);
        } else {
            if (map[santa.r][santa.c] == EMPTY) {
                map[santa.r][santa.c] = sId;
            } else {
                interact(santa.r, santa.c, dr, dc, dir);
                map[santa.r][santa.c] = sId;
            }

        }

    }

    private static boolean isOutOfBounds(int r, int c) {
        return r < 1 || c < 1 || r > N || c > N;
    }


    private static int getRudolphDirection(Point rudolph, Point santa) {
        if (santa.r < rudolph.r) {
            if (santa.c < rudolph.c) {
                return 1;
            } else if (santa.c == rudolph.c) {
                return 2;
            } else {
                return 3;
            }

        } else if (santa.r == rudolph.r) {
            if (santa.c > rudolph.c) {
                return 4;
            } else {
                return 8;
            }
        } else {
            if (santa.c > rudolph.c) {
                return 5;
            } else if (santa.c == rudolph.c) {
                return 6;
            } else {
                return 7;
            }

        }

    }

    private static int getSantaDirection(Point santa, Point rudolph, int sId) {

        int minDist = Integer.MAX_VALUE;
        int dir = 0;

        for (int d = 1; d <= 4; d++) {
            int nextR = santa.r + dr_4[d];
            int nextC = santa.c + dc_4[d];

            if (isOutOfBounds(nextR, nextC)) {
                continue;
            }
            if (map[nextR][nextC] >= 1 && map[nextR][nextC] <= P) {
                continue;
            }
            int dist = getDist(rudolph.r, rudolph.c, nextR, nextC);
            if (minDist > dist) {
                minDist = dist;
                dir = d;
            }

        }

        return dir;
    }


    private static int getDist(int from_r, int from_c, int to_r, int to_c) {
        return (int) (Math.pow(from_r - to_r, 2) + Math.pow(from_c - to_c, 2));
    }

    static class Point implements Comparable<Point> {

        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public int compareTo(Point o) {
            if (this.r == o.r) {
                return Integer.compare(o.c, this.c);
            } else {
                return Integer.compare(o.r, this.r);
            }
        }

        @Override
        public String toString() {
            return "Point{" +
                "r=" + r +
                ", c=" + c +
                '}';
        }
    }
}