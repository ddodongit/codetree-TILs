import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {

    static final int[] dr = {-1, 0, 1, 0}, dc = {0, 1, 0, -1};
    static final int EMPTY = 0, HOLE = 1, WALL = 2;
    static int L, N, Q;
    static int[][] map, barriers;
    static HashMap<Integer, Knight> allKnights;
    static int[] allDamage;


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());

        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        allKnights = new HashMap<>();
        allDamage = new int[N + 1];

        map = new int[L + 1][L + 1];
        barriers = new int[L + 1][L + 1];

        for (int i = 1; i <= L; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= L; j++) {
                barriers[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            Knight newKnight = new Knight(i, r, c, h, w, k);
            allKnights.put(i, newKnight);
            for (int row = r; row < r + h; row++) {
                for (int col = c; col < c + w; col++) {
                    map[row][col] = i;
                    if (barriers[row][col] == HOLE) {
                        newKnight.hole++;
                    }
                }
            }


        }

        for (int i = 1; i <= Q; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

            if (!allKnights.containsKey(idx)) {
                continue;
            }
            command(idx, d);
        }

        int total = 0;
        for (Integer idx : allKnights.keySet()) {
            total += allDamage[idx];
        }

        System.out.println(total);
    }

    private static void command(int idx, int d) {

        HashSet<Integer> allPushed = new HashSet<>();

        if (moveKnights(idx, d, allPushed)) {
            moveKnight(allKnights.get(idx), d);
            for (Integer kId : allPushed) {
                Knight knight = allKnights.get(kId);

                // map
                moveKnight(knight, d);
                if (knight.hole > 0) {
                    knight.k -= knight.hole;
                    allDamage[kId] += knight.hole;
                    if (knight.k == 0) {
                        removeKnight(knight);
                    }
                }

            }
        }
    }

    private static void removeKnight(Knight knight) {
        allKnights.remove(knight.idx);

        for (int i = knight.r; i < knight.r + knight.h; i++) {
            for (int j = knight.c; j < knight.c + knight.w; j++) {
                map[i][j] = EMPTY;
            }
        }
    }

    private static void moveKnight(Knight knight, int dir) {
        int nowI, nowJ;
        int beforeI, beforeJ;
        switch (dir) {
            case 0: // UP
                nowI = knight.r + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    map[nowI][nextJ] = knight.idx;
                    if (barriers[nowI][nextJ] == HOLE) {
                        knight.hole++;
                    }
                }

                beforeI = knight.r + knight.h - 1;
                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    if (map[beforeI][nextJ] == knight.idx) {
                        map[beforeI][nextJ] = EMPTY;
                    }
                    if (barriers[beforeI][nextJ] == HOLE) {
                        knight.hole--;
                    }
                }

                break;
            case 1: // RIGHT
                nowI = knight.r + dr[dir];
                nowJ = knight.c + knight.w - 1 + dc[dir];

                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    map[nextI][nowJ] = knight.idx;
                    if (barriers[nextI][nowJ] == HOLE) {
                        knight.hole++;
                    }
                }

                beforeJ = knight.c;
                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    if (map[nextI][beforeJ] == knight.idx) {
                        map[nextI][beforeJ] = EMPTY;
                    }
                    if (barriers[nextI][beforeJ] == HOLE) {
                        knight.hole--;
                    }
                }

                break;
            case 2: // DOWN
                nowI = knight.r + knight.h - 1 + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    map[nowI][nextJ] = knight.idx;
                    if (barriers[nowI][nextJ] == HOLE) {
                        knight.hole++;
                    }
                }

                beforeI = knight.r;
                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    if (map[beforeI][nextJ] == knight.idx) {
                        map[beforeI][nextJ] = EMPTY;
                        if (barriers[beforeI][nextJ] == HOLE) {
                            knight.hole--;
                        }
                    }
                }

                break;
            case 3: // LEFT
                nowI = knight.r + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    map[nextI][nowJ] = knight.idx;
                    if (barriers[nextI][nowJ] == HOLE) {
                        knight.hole++;
                    }
                }

                beforeJ = knight.c + knight.w - 1;
                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    if (map[nextI][beforeJ] == knight.idx) {
                        map[nextI][beforeJ] = EMPTY;
                    }
                    if (barriers[nextI][beforeJ] == HOLE) {
                        knight.hole--;
                    }
                }

                break;
        }

        // hashSet
        knight.r += dr[dir];
        knight.c += dc[dir];

    }

    private static boolean moveKnights(int idx, int dir, HashSet<Integer> allPushed) {

        Knight knight = allKnights.get(idx);
        HashSet<Integer> nowPushed = new HashSet<>();

        if (isWall(knight, dir, nowPushed)) {
            allPushed.clear();
            return false;
        } else {
            for (Integer kIdx : nowPushed) {
                boolean result = moveKnights(kIdx, dir, nowPushed);
                if (!result) {
                    return false;
                }
            }
        }

        allPushed.addAll(nowPushed);
        return true;
    }

    private static boolean isWall(Knight knight, int dir, HashSet<Integer> pushed) {

        int nowI, nowJ;

        switch (dir) {
            case 0: // UP
                nowI = knight.r + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    if (isOutOfBounds(nowI, nextJ)) {
                        return true;
                    }
                    if (barriers[nowI][nextJ] == WALL) {
                        return true;
                    }
                    if (map[nowI][nextJ] > 0) {
                        pushed.add(map[nowI][nextJ]);
                    }
                }
                break;
            case 1: // RIGHT
                nowI = knight.r + dr[dir];
                nowJ = knight.c + knight.w - 1 + dc[dir];

                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    if (isOutOfBounds(nextI, nowJ)) {
                        return true;
                    }
                    if (barriers[nextI][nowJ] == WALL) {
                        return true;
                    }
                    if (map[nextI][nowJ] > 0) {
                        pushed.add(map[nextI][nowJ]);
                    }
                }

                break;
            case 2: // DOWN
                nowI = knight.r + knight.h - 1 + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextJ = nowJ; nextJ < nowJ + knight.w; nextJ++) {
                    if (isOutOfBounds(nowI, nextJ)) {
                        return true;
                    }
                    if (barriers[nowI][nextJ] == WALL) {
                        return true;
                    }
                    if (map[nowI][nextJ] > 0) {
                        pushed.add(map[nowI][nextJ]);
                    }
                }
                break;
            case 3: // LEFT
                nowI = knight.r + dr[dir];
                nowJ = knight.c + dc[dir];

                for (int nextI = nowI; nextI < nowI + knight.h; nextI++) {
                    if (isOutOfBounds(nextI, nowJ)) {
                        return true;
                    }
                    if (barriers[nextI][nowJ] == WALL) {
                        return true;
                    }
                    if (map[nextI][nowJ] > 0) {
                        pushed.add(map[nextI][nowJ]);
                    }
                }
                break;
        }

        return false;
    }

    private static boolean isOutOfBounds(int i, int j) {
        return i < 1 || i > L || j < 1 || j > L;
    }

    static class Knight {

        int idx;
        int r, c, h, w, k;
        int hole;

        public Knight(int idx, int r, int c, int h, int w, int k) {
            this.idx = idx;
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
        }
    }

}