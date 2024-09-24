import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Main {

    static int[] dr = {0, -1, 0, 1, 1, 1, 0, -1, -1}, dc = {0, 1, 1, 1, 0, -1, -1, -1,
        0}; // 1~8, 우(2),하(4),좌(6),상(8)
    static Bomb[][] bombMap;
    static TreeSet<Bomb> allBombs;
    static Bomb blue, red;
    static ArrayDeque<Bomb> yellow;
    static int N, M, K, minDist;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        bombMap = new Bomb[N + 1][M + 1];
        allBombs = new TreeSet<>();
        for (int r = 1; r <= N; r++) {
            st = new StringTokenizer(br.readLine());
            for (int c = 1; c <= M; c++) {
                int power = Integer.parseInt(st.nextToken());
                Bomb newBomb = new Bomb(r, c, r + c, power, 0);
                bombMap[r][c] = newBomb;
                if (power > 0) {
                    allBombs.add(newBomb);
                }
            }
        }

        for (int i = 1; i <= K; i++) {
            choose(i);
            attack();
            if (allBombs.size() == 1) {
                break;
            }
            repair();
        }

        System.out.println(allBombs.last().power);
    }

    private static void choose(int nowTurn) {
        // choose blue
        blue = allBombs.pollFirst();
        blue.power += (N + M);
        blue.usedTurn = nowTurn;
    }

    private static void attack() {
        // choose red
        red = allBombs.pollLast();
        allBombs.add(blue);

        if (!laserAttack()) {
            bombAttack();
        }

        // red
        red.power -= blue.power;
        if (red.power > 0) {
            allBombs.add(red);
        } else {
            red.power = 0;
        }

        // yellow
        if (!yellow.isEmpty()) {
            TreeSet<Bomb> changed = new TreeSet<>();
            for (Bomb bomb : allBombs) {
                if (yellow.contains(bomb)) {
                    bomb.power -= (blue.power / 2);
                }
                if (bomb.power > 0) {
                    changed.add(bomb);
                } else {
                    bomb.power = 0;
                }
            }
            allBombs = changed;
        }
    }

    private static void repair() {
        TreeSet<Bomb> repaired = new TreeSet<>();

        for (Bomb bomb : allBombs) {
            if (bomb != blue && bomb != red && !yellow.contains(bomb)) {
                bomb.power += 1;
            }
            repaired.add(bomb);

        }

        allBombs = repaired;
    }

    private static void bombAttack() {
        yellow = new ArrayDeque<>();

        Bomb now = red;
        for (int d = 1; d <= 8; d++) {
            int nextR = now.r + dr[d];
            int nextC = now.c + dc[d];

            nextR = isOutOfBounds(nextR, N);
            nextC = isOutOfBounds(nextC, M);

            if (nextR == blue.r && nextC == blue.c) {
                continue;
            }
            if (bombMap[nextR][nextC].power == 0) {
                continue;
            }

            yellow.add(bombMap[nextR][nextC]);
        }
    }

    private static boolean laserAttack() {
        yellow = new ArrayDeque<>();

        ArrayDeque<Bomb> queue = new ArrayDeque<>();
        Queue<ArrayDeque<Bomb>> pathQueue = new ArrayDeque<>();

        boolean[][] visited = new boolean[N + 1][M + 1];

        queue.add(blue);
        ArrayDeque<Bomb> startPath = new ArrayDeque<>();
        pathQueue.add(startPath);

        while (!queue.isEmpty()) {
            Bomb now = queue.poll();
            ArrayDeque<Bomb> nowPath = pathQueue.poll();
            visited[now.r][now.c] = true;

            if (now == red) {
                nowPath.pollLast();
                yellow = nowPath;
                return true;
            }

            for (int d = 2; d <= 8; d += 2) {
                int nextR = now.r + dr[d];
                int nextC = now.c + dc[d];

                nextR = isOutOfBounds(nextR, N);
                nextC = isOutOfBounds(nextC, M);

                if (visited[nextR][nextC] || bombMap[nextR][nextC].power == 0) {
                    continue;
                }

                queue.add(bombMap[nextR][nextC]);
                ArrayDeque<Bomb> nextPath = new ArrayDeque<>(nowPath);
                nextPath.add(bombMap[nextR][nextC]);
                pathQueue.add(nextPath);
            }
        }
        return false;
    }

    private static int isOutOfBounds(int pos, int range) {

        if (pos < 1) {
            return range;
        } else if (pos > range) {
            return 1;
        } else {
            return pos;
        }

    }

    static class Bomb implements Comparable<Bomb> {

        int r, c, sumRC, power, usedTurn;

        public Bomb(int r, int c, int sumRC, int power, int usedTurn) {
            this.r = r;
            this.c = c;
            this.sumRC = sumRC;
            this.power = power;
            this.usedTurn = usedTurn;
        }

        @Override
        public int compareTo(Bomb o) {
            if (this.power == o.power) {
                if (this.usedTurn == o.usedTurn) {
                    if (this.sumRC == o.sumRC) {
                        return Integer.compare(o.c, this.c);
                    } else {
                        return Integer.compare(o.sumRC, this.sumRC);
                    }
                } else {
                    return Integer.compare(o.usedTurn, this.usedTurn);
                }
            }
            return Integer.compare(this.power, o.power);
        }
    }


}