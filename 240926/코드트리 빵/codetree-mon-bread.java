import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static final int[] di = {-1, 0, 0, 1}, dj = {0, -1, 1, 0}; // 상,좌,우,하
    static final int EMPTY = 0, BLOCKED = -2, CAMP = -1; // TARGET= 1~m
    static int n, m, cnt;
    static HashMap<Integer, Point> nowPos; // <idx, 현재 위치> 사람의 현재 위치
    static HashSet<Integer> peopleOnMap; // 격자에 있는 사람 idx
    static HashMap<Integer, Point> allTarget; // <idx, target point>
    static int[][] map; // 격자(EMPTY= 0 , BLOCKED = -2, CAMP = -1, TARGET= 1~M

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= n; j++) {
                int value = Integer.parseInt(st.nextToken());
                if (value == 1) {
                    value = CAMP;
                }
                map[i][j] = value;
            }
        }

        allTarget = new HashMap<>();
        nowPos = new HashMap<>();
        peopleOnMap = new HashSet<>();
        for (int idx = 1; idx <= m; idx++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());

            allTarget.put(idx, new Point(r, c));
            nowPos.put(idx, null);
            map[r][c] = idx;
        }

        int t = 1;
        while (true) {
            moveToTarget();
            if (cnt == m) {
                break;
            }
            enterBaseCamp(t++);
        }
        System.out.println(t);
    }

    private static void moveToTarget() {

        if (peopleOnMap.isEmpty()) {
            return;
        }

        Queue<Point> blocked = new ArrayDeque<>();

        // move
        for (Integer idx : peopleOnMap) {
            Point pos = nowPos.get(idx);
            Point target = allTarget.get(idx);
            Queue<Integer> path = bfs(pos, target);
            int nextR = path.poll();
            int nextC = path.poll();

            pos.r = nextR;
            pos.c = nextC;

            if (pos.r == target.r && pos.c == target.c) {
                blocked.add(pos);
            }
        }

        // block
        for (Point p : blocked) {
            peopleOnMap.remove(map[p.r][p.c]);
            map[p.r][p.c] = BLOCKED;
            cnt++;
        }
    }

    private static Queue<Integer> bfs(Point start, Point target) {
        Queue<Integer> queue = new ArrayDeque<>();
        ArrayDeque<Queue<Integer>> pathQueue = new ArrayDeque<>();
        boolean[][] visited = new boolean[n + 1][n + 1];

        queue.add(start.r);
        queue.add(start.c);
        pathQueue.add(new ArrayDeque<>());

        while (!queue.isEmpty()) {
            int nowR = queue.poll();
            int nowC = queue.poll();
            Queue<Integer> nowPath = pathQueue.poll();

            visited[nowR][nowC] = true;
            if (target.r == nowR && target.c == nowC) {
                return nowPath;
            }

            for (int d = 0; d < 4; d++) { // 상,좌,우,하
                int nextR = nowR + di[d];
                int nextC = nowC + dj[d];

                if (isOutOfBounds(nextR, nextC)) {
                    continue;
                }
                if (visited[nextR][nextC]) {
                    continue;
                }
                if (map[nextR][nextC] == BLOCKED) {
                    continue;
                }

                queue.add(nextR);
                queue.add(nextC);
                Queue<Integer> nextPath = new ArrayDeque<>(nowPath);
                nextPath.add(nextR);
                nextPath.add(nextC);
                pathQueue.add(nextPath);


            }

        }

        return null;
    }

    private static void enterBaseCamp(int nowT) {

        if (nowT > m) {
            return;
        }

        Point target = allTarget.get(nowT);
        Point baseCamp = findNearBaseCamp(target);
        // enter
        nowPos.put(nowT, baseCamp);
        peopleOnMap.add(nowT);

        // block
        map[baseCamp.r][baseCamp.c] = BLOCKED;
    }


    private static Point findNearBaseCamp(Point start) {
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[n + 1][n + 1];

        queue.add(start.r);
        queue.add(start.c);

        while (!queue.isEmpty()) {
            int nowR = queue.poll();
            int nowC = queue.poll();

            visited[nowR][nowC] = true;
            if (map[nowR][nowC] == CAMP) {
                return new Point(nowR, nowC);
            }

            for (int d = 0; d < 4; d++) { // 상,좌,우,하
                int nextR = nowR + di[d];
                int nextC = nowC + dj[d];

                if (isOutOfBounds(nextR, nextC)) {
                    continue;
                }
                if (visited[nextR][nextC]) {
                    continue;
                }
                if (map[nextR][nextC] == BLOCKED) {
                    continue;
                }

                queue.add(nextR);
                queue.add(nextC);
            }

        }

        return null;

    }


    private static boolean isOutOfBounds(int nextR, int nextC) {
        return nextR < 1 || nextR > n || nextC < 1 || nextC > n;
    }

    static class Point {

        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}