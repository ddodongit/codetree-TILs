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
    static HashSet<Point> allBaseCamp;
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
        allBaseCamp = new HashSet<>();

        for (int i = 1; i <= n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= n; j++) {
                int value = Integer.parseInt(st.nextToken());
                if (value == 1) {
                    value = CAMP;
                    allBaseCamp.add(new Point(i, j));
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

            int minDist = Integer.MAX_VALUE;
            Point result = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            for (int d = 0; d < 4; d++) {
                Point next = new Point(pos.r + di[d], pos.c + dj[d]);
                if (isOutOfBounds(next.r, next.c)) {
                    continue;
                }
                if (map[next.r][next.c] == BLOCKED) {
                    continue;
                }
                int dist = bfs(next, target, minDist);

                if (map[next.r][next.c] == idx) {
                    result = next;
                    break;
                }

                if (dist > minDist) {
                    continue;
                }

                if (minDist == dist) {
                    result = result.compareTo(next) < 0 ? result : next;
                } else {
                    result = next;
                }
                minDist = dist;

            }
            pos.r = result.r;
            pos.c = result.c;

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

    private static int bfs(Point start, Point end, int minDist) {
        Queue<Target> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[n + 1][n + 1];

        queue.add(new Target(0, start));

        while (!queue.isEmpty()) {
            Target now = queue.poll();
            Point nowP = now.p;

            if (now.dist > minDist) {
                return now.dist;
            }

            visited[nowP.r][nowP.c] = true;
            if (nowP.r == end.r && nowP.c == end.c) {
                return now.dist;
            }

            for (int d = 0; d < 4; d++) { // 상,좌,우,하
                int nextR = nowP.r + di[d];
                int nextC = nowP.c + dj[d];

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
                queue.add(new Target(now.dist + 1, next));
            }
        }

        return Integer.MAX_VALUE;
    }


    private static void enterBaseCamp(int nowT) {

        if (nowT > m) {
            return;
        }

        Point target = allTarget.get(nowT);
        Point baseCamp = findNearBaseCamp(target);
        // enter
        nowPos.put(nowT, new Point(baseCamp.r, baseCamp.c));
        peopleOnMap.add(nowT);

        // block
        map[baseCamp.r][baseCamp.c] = BLOCKED;
        allBaseCamp.remove(baseCamp);
    }


    private static Point findNearBaseCamp(Point target) {

        int minDist = Integer.MAX_VALUE;
        Point result = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);


        for (Point baseCamp : allBaseCamp) {
            int dist = bfs(target, baseCamp, minDist);

            if (dist > minDist) {
                continue;
            }

            if (minDist == dist) {
                result = result.compareTo(baseCamp) < 0 ? result : baseCamp;
            } else {
                result = baseCamp;
            }
            minDist = dist;

        }

        return result;
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
                return this.p.compareTo(o.p);
            }
            return Integer.compare(this.dist, o.dist);
        }
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
                return Integer.compare(this.c, o.c);
            }
            return Integer.compare(this.r, o.r);
        }

    
    }
}