import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
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
            map[r][c] = idx;
        }

        int t = 1;
        while (true) {
            moveToTarget();
            if (cnt == m) {
                break;
            }
            enterBaseCamp(t);
            if (cnt == m) {
                break;
            }
            t++;
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
            Point next = bfs(pos, idx);
            pos.r = next.r;
            pos.c = next.c;

            if (map[pos.r][pos.c] == idx) {
                blocked.add(pos);
            }
        }

        // block
        for (Point p : blocked) {
            cnt++;
            peopleOnMap.remove(map[p.r][p.c]);
            map[p.r][p.c] = BLOCKED;
        }
    }

    private static Point bfs(Point start, int targetIdx) {
        Queue<Point> queue = new ArrayDeque<>();
        ArrayDeque<Queue<Point>> pathQueue = new ArrayDeque<>();
        boolean[][] visited = new boolean[n + 1][n + 1];
        PriorityQueue<Target> pq = new PriorityQueue<>();

        queue.add(start);
        pathQueue.add(new ArrayDeque<>());

        while (!queue.isEmpty()) {
            Point now = queue.poll();
            Queue<Point> nowPath = pathQueue.poll();

            visited[now.r][now.c] = true;
            if (map[now.r][now.c] == targetIdx) {
                Point p = nowPath.poll();
                int dist = getDistance(p, start);
                pq.add(new Target(dist, p));
            }

            for (int d = 0; d < 4; d++) { // 상,좌,우,하
                int nextR = now.r + di[d];
                int nextC = now.c + dj[d];

                if (isOutOfBounds(nextR, nextC)) {
                    continue;
                }
                if (visited[nextR][nextC]) {
                    continue;
                }
                if (map[nextR][nextC] == BLOCKED) {
                    continue;
                }

                Point next = new Point(nextR, nextC);
                queue.add(next);
                Queue<Point> nextPath = new ArrayDeque<>(nowPath);
                nextPath.add(next);
                pathQueue.add(nextPath);


            }

        }

        return pq.poll().p;
    }

    private static int getDistance(Point p, Point start) {
        return Math.abs(p.r - start.r) + Math.abs(p.c - start.c);
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
        Queue<Point> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[n + 1][n + 1];
        PriorityQueue<Target> pq = new PriorityQueue<>();

        queue.add(start);

        while (!queue.isEmpty()) {
            Point now = queue.poll();

            visited[now.r][now.c] = true;
            if (map[now.r][now.c] == CAMP) {
                int dist = getDistance(now, start);
                pq.add(new Target(dist, now));
            }

            for (int d = 0; d < 4; d++) { // 상,좌,우,하
                int nextR = now.r + di[d];
                int nextC = now.c + dj[d];

                if (isOutOfBounds(nextR, nextC)) {
                    continue;
                }
                if (visited[nextR][nextC]) {
                    continue;
                }
                if (map[nextR][nextC] == BLOCKED) {
                    continue;
                }

                queue.add(new Point(nextR, nextC));
            }

        }

        return pq.poll().p;

    }


    private static boolean isOutOfBounds(int nextR, int nextC) {
        return nextR < 1 || nextR > n || nextC < 1 || nextC > n;
    }

    static class Target implements Comparable<Target> {

        int dist;
        Point p;

        public Target(int dist, Point p) {
            this.dist = dist;
            this.p = p;
        }


        @Override
        public int compareTo(Target o) {
            if (this.dist == o.dist) {
                if (this.p.r == o.p.r) {
                    return Integer.compare(this.p.c, o.p.c);
                } else {
                    return Integer.compare(this.p.r, o.p.r);
                }
            }
            return Integer.compare(this.dist, o.dist);
        }
    }

    static class Point {

        int r, c;

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }


    }
}