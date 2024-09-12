import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    static final int[] di = {-1, 0, 1, 0}, dj = {0, 1, 0, -1};
    static int R, C, K, total;
    static int angel_i, angel_j, move_dir, exit_dir;
    static int[][] map;
    static HashMap<Integer, int[]> posCenterMap;

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[R + 1][C + 1];
        posCenterMap = new HashMap<>();

        for (int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine());
            int center = Integer.parseInt(st.nextToken());
            int dir = Integer.parseInt(st.nextToken());

            angel_i = -1;
            angel_j = center;
            exit_dir = dir;
            enter(i);
        }

        System.out.println(total);

    }

    static void enter(int idx) {

        move_dir = DOWN;

        while (true) {
            boolean result = move_golem();
            if (angel_i + 1 == R) {
                break;
            }
            if (!result && move_dir == -1) {
                break;
            }

        }

        // init forest
        if (angel_i - 1 < 1) {
            map = new int[R + 1][C + 1];
            posCenterMap = new HashMap<>();
            return;
        }

        // draw golem
        drawMap(idx);

        move_angel(idx);

    }

    static void move_angel(int idx) {

        int maxRow = angel_i + 1;

        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] visited = new boolean[K + 1];
        visited[idx] = true;

        queue.add(angel_i + di[exit_dir]);
        queue.add(angel_j + dj[exit_dir]);

        while (!queue.isEmpty()) {
            int exit_i = queue.poll();
            int exit_j = queue.poll();

            for (int d = 1; d < 4; d++) {
                int next_i = exit_i + di[d];
                int next_j = exit_j + dj[d];

                if (isOutOfBounds(next_i, next_j)) {
                    continue;
                }

                int nextIdx = Math.abs(map[next_i][next_j]);
                if (visited[nextIdx]) {
                    continue;
                }
                if (nextIdx == 0) {
                    continue;
                }
                if (idx == nextIdx) {
                    continue;
                }

                int[] pos = posCenterMap.get(nextIdx);
                int[] exitArr = getExitPos(pos[0], pos[1]);
                maxRow = Integer.max(maxRow, pos[0] + 1);
                queue.add(exitArr[0]);
                queue.add(exitArr[1]);
                visited[nextIdx] = true;
            }
        }

        total += maxRow;
    }

    static void drawMap(int idx) {

        map[angel_i][angel_j] = idx;
        for (int d = 0; d < 4; d++) {
            if (exit_dir == d) {
                map[angel_i + di[d]][angel_j + dj[d]] = -idx;
            } else {
                map[angel_i + di[d]][angel_j + dj[d]] = idx;
            }
        }
        int[] arr = new int[2];
        arr[0] = angel_i;
        arr[1] = angel_j;

        posCenterMap.put(idx, arr);
    }

    static boolean move_golem() {

        if (isAvailable(angel_i, angel_j, move_dir)) // check available (green)
        {

            if (move_dir == LEFT || move_dir == RIGHT) {
                if (!isAvailable(angel_i + di[move_dir], angel_j + dj[move_dir], DOWN)) {
                    if (move_dir == LEFT) {
                        move_dir = RIGHT;
                    } else if (move_dir == DOWN) {
                        move_dir = LEFT;
                    } else if (move_dir == RIGHT) {
                        move_dir = -1;
                    }
                    return false;
                }

                // change exit_dir
                if (move_dir == LEFT) {
                    exit_dir = (exit_dir + 3) % 4;
                } else if (move_dir == RIGHT) {
                    exit_dir = (exit_dir + 1) % 4;
                }

                angel_i = angel_i + di[move_dir];
                angel_j = angel_j + dj[move_dir];

                move_dir = DOWN;

            }

            angel_i = angel_i + di[move_dir];
            angel_j = angel_j + dj[move_dir];

            return true;

        }

        if (move_dir == LEFT) {
            move_dir = RIGHT;
        } else if (move_dir == DOWN) {
            move_dir = LEFT;
        } else if (move_dir == RIGHT) {
            move_dir = -1;
        }

        return false;

    }

    static boolean isAvailable(int center_i, int center_j, int move_dir) {

        center_i = center_i + di[move_dir];
        center_j = center_j + dj[move_dir];

        if (isOutOfBounds(center_i + di[move_dir], center_j + dj[move_dir])) {
            return false;
        }

        for (int d = -1; d <= 1; d++) {
            if (center_i + di[(move_dir + d + 4) % 4] >= 0
                && map[center_i + di[(move_dir + d + 4) % 4]][center_j + dj[(move_dir + d + 4) % 4]]
                != 0) {
                return false;
            }

        }

//    

        return true;
    }

    static int[] getExitPos(int i, int j) {
        int[] pos = new int[2];

        for (int d = 0; d < 4; d++) {
            if (map[i + di[d]][j + dj[d]] < 0) {
                pos[0] = i + di[d];
                pos[1] = j + dj[d];
                return pos;
            }
        }
        return pos;
    }

    static boolean isOutOfBounds(int i, int j) {
        return i < -1 || i > R || j < 1 || j > C;
    }

}