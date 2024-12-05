import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {
	static final int[] di = { -1, 0, 1, 0 }, dj = { 0, 1, 0, -1 };
	static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	static int R, C, K, maxRow;
	static int[][] map;
	static HashMap<Integer, Golrem> allGolrems;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		st = new StringTokenizer(br.readLine());
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		map = new int[R + 1][C + 1];
		allGolrems = new HashMap<>();
		int colSum = 0;
		for (int i = 1; i <= K; i++) {
			st = new StringTokenizer(br.readLine());
			int c = Integer.parseInt(st.nextToken());
			int dir = Integer.parseInt(st.nextToken());
			Golrem g = new Golrem(i, -1, c, dir);
			allGolrems.put(i, g);

			// move golrem
			if (!moveGolrem(g)) {
				// init map
				allGolrems = new HashMap<>();
				map = new int[R + 1][C + 1];
				continue;
			}

			// set map
			map[g.centerR][g.centerC] = g.id;
			for (int d = 0; d < 4; d++) {
				int nextR = g.centerR + di[d];
				int nextC = g.centerC + dj[d];
				if (g.exitDir == d) {
					map[nextR][nextC] = -g.id;
					continue;
				}
				map[nextR][nextC] = g.id;
			}

			// move angel
			maxRow = Integer.MIN_VALUE;
			moveAngel(g);
			colSum += maxRow;
		}

		System.out.println(colSum);
	}


	private static void moveAngel(Golrem nowGolrem) {

		boolean[] visited = new boolean[K + 1];
		dfsAngel(nowGolrem, visited);
	}

	private static void dfsAngel(Golrem nowGolrem, boolean[] visited) {

		maxRow = Math.max(maxRow, nowGolrem.centerR + 1);

		int exitR = nowGolrem.centerR + di[nowGolrem.exitDir];
		int exitC = nowGolrem.centerC + dj[nowGolrem.exitDir];

		for (int d = 0; d < 4; d++) {
			int nextR = exitR + di[d];
			int nextC = exitC + dj[d];
			if (isOutOfBounds(nextR, nextC)) {
				continue;
			}
			int nextId = Math.abs(map[nextR][nextC]);
			if (visited[nextId]) {
				continue;
			}
			if (map[nextR][nextC] == 0 || nextId == nowGolrem.id) {
				continue;
			}

			visited[nextId] = true;
			dfsAngel(allGolrems.get(nextId), visited);
		}

	}

	private static boolean moveGolrem(Golrem nowGolrem) {

		while (true) {
			moveSouth(nowGolrem);

			if (isAvailable(nowGolrem.centerR, nowGolrem.centerC, WEST)) {
				// change pos
				nowGolrem.centerR += 1;
				nowGolrem.centerC -= 1;

				// change exit dir
				nowGolrem.exitDir = (nowGolrem.exitDir - 1) % 4;

				continue;
			} else {
				if (isAvailable(nowGolrem.centerR, nowGolrem.centerC, EAST)) {
					// change pos
					nowGolrem.centerR += 1;
					nowGolrem.centerC += 1;

					// change exit dir
					nowGolrem.exitDir = (nowGolrem.exitDir + 1) % 4;
					continue;
				}
			}

			break;
		}

		return (nowGolrem.centerR - 1) >= 1; // golrem 가장 윗 부분이 숲 바깥에

	}

	private static boolean isAvailable(int centerR, int centerC, int moveDir) {

		if (moveDir != SOUTH) {
			if (moveDir == WEST) {
				int leftC = centerC - 1;

				if (centerR - 1 < 1) {
					if (isOutOfBounds(centerR, leftC - 1) // center
							|| isOutOfBounds(centerR + 1, leftC)) { // bottom
						return false;
					}

					if (map[centerR][leftC - 1] != 0 // center
							|| map[centerR + 1][leftC] != 0) { // bottom
						return false;
					}
				} else {
					if (centerR - 1 < 1) {
						if (isOutOfBounds(centerR, leftC - 1) // center
								|| isOutOfBounds(centerR + 1, leftC)) { // bottom
							return false;
						}

						if (map[centerR][leftC - 1] != 0 // center
								|| map[centerR + 1][leftC] != 0) { // bottom
							return false;
						}
					} else {
						if (isOutOfBounds(centerR - 1, leftC) // top
								|| isOutOfBounds(centerR, leftC - 1) // center
								|| isOutOfBounds(centerR + 1, leftC)) { // bottom
							return false;
						}

						if (map[centerR - 1][leftC] != 0 // top
								|| map[centerR][leftC - 1] != 0 // center
								|| map[centerR + 1][leftC] != 0) { // bottom
							return false;
						}
					}
				}
			} else { // EAST
				int rightC = centerC + 1;

				if (isOutOfBounds(centerR - 1, rightC) // top
						|| isOutOfBounds(centerR, rightC + 1) // center
						|| isOutOfBounds(centerR + 1, rightC)) { // bottom
					return false;
				}

				if (map[centerR - 1][rightC] != 0 // top
						|| map[centerR][rightC + 1] != 0 // center
						|| map[centerR + 1][rightC] != 0) { // bottom
					return false;
				}
			}

			// check south
			return isAvailable(centerR + di[moveDir], centerC + dj[moveDir], SOUTH);
		}

		// check South
		else {
			int bottomR = centerR + 1;

			if (bottomR < 1) {
				if (isOutOfBounds(bottomR + 1, centerC)) { // center
					return false;
				}

				if (map[bottomR + 1][centerC] != 0) { // center
					return false;
				}

			} else {
				if (isOutOfBounds(bottomR, centerC - 1) // left
						|| isOutOfBounds(bottomR + 1, centerC) // center
						|| isOutOfBounds(bottomR, centerC + 1)) { // right
					return false;
				}

				if (map[bottomR][centerC - 1] != 0 // left
						|| map[bottomR + 1][centerC] != 0 // center
						|| map[bottomR][centerC + 1] != 0) { // right
					return false;
				}
			}

			return true;
		}
	}

	private static boolean moveSouth(Golrem nowGolrem) {
		while (isAvailable(nowGolrem.centerR, nowGolrem.centerC, SOUTH)) {
			nowGolrem.centerR += 1;
		}
		return false;

	}

	private static boolean isOutOfBounds(int r, int c) {
		return r < 1 || r > R || c < 1 || c > C;
	}

	static class Golrem {
		int id, centerR, centerC, exitDir;

		public Golrem(int id, int centerR, int centerC, int exitDir) {
			super();
			this.id = id;
			this.centerR = centerR;
			this.centerC = centerC;
			this.exitDir = exitDir;
		}

	}
}
