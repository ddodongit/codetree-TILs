import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Main {
	static final int[] dr = { -1, 0, 1, 0 }, dc = { 0, 1, 0, -1 };
	static int[][] map, bestMap;
	static int K, M, centerR, centerC, maxCnt, minAngle, total;
	static Queue<Integer> wallNumbers;
	static TreeSet<Point> allPieces;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());

		map = new int[5][5];
		for (int i = 0; i < 5; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		wallNumbers = new LinkedList<Integer>();
		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			wallNumbers.add(Integer.parseInt(st.nextToken()));
		}

		for (int i = 0; i < K; i++) {
			total = 0;
			explore();
			while (true) {
				fillBlank();

				TreeSet<Point> pieces = getPieces(map);
				allPieces = pieces;
				if (pieces.size() == 0) {
					break;
				}
			}
			if (total == 0) {
				break;
			}
			sb.append(total +"\n");
		}

		System.out.print(sb);
	}

	private static void fillBlank() {
		total += allPieces.size();
		while (!allPieces.isEmpty()) {
			Point p = allPieces.pollFirst();
			int num = wallNumbers.poll();
			map[p.r][p.c] = num;
		}
	}

	private static void explore() {

		bestMap = new int[5][5];
		centerR = -1;
		centerC = -1;
		maxCnt = 0;
		minAngle = 360;

		for (int r = 1; r <= 3; r++) {
			for (int c = 1; c <= 3; c++) {
				int[][] copied = new int[3][3];
				int[][] tmpMap = copyMap(map);

				rotate(r, c, copied, tmpMap, 90);
				rotate(r, c, copied, tmpMap, 180);
				rotate(r, c, copied, tmpMap, 270);

			}
		}

		map = bestMap;

	}

	private static int[][] copyMap(int[][] origin) {
		int[][] arr = new int[5][5];

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				arr[i][j] = origin[i][j];
			}
		}
		return arr;
	}

	private static void rotate(int nowR, int nowC, int[][] copied, int[][] tmpMap, int angle) {

		int startR = nowR - 1;
		int startC = nowC - 1;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				copied[j][2 - i] = tmpMap[startR + i][startC + j];
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				tmpMap[startR + i][startC + j] = copied[i][j];
			}
		}

		TreeSet<Point> pieces = getPieces(tmpMap);
		int cnt = pieces.size();

		if (maxCnt < cnt) {
			maxCnt = cnt;
			minAngle = angle;
			centerR = nowR;
			centerC = nowC;
			bestMap = copyMap(tmpMap);
			allPieces = pieces;
		} else if (maxCnt == cnt) {
			if (angle < minAngle) {
				minAngle = angle;
				centerR = nowR;
				centerC = nowC;
				bestMap = copyMap(tmpMap);
				allPieces = pieces;
			} else if (angle == minAngle) {
				if (nowC < centerC) {
					centerR = nowR;
					centerC = nowC;
					bestMap = copyMap(tmpMap);
					allPieces = pieces;
				} else if (nowC == centerC) {
					if (nowR < centerR) {
						centerR = nowR;
						bestMap = copyMap(tmpMap);
						allPieces = pieces;
					}
				}
			}
		}

	}

	private static TreeSet<Point> getPieces(int[][] arr) {

		TreeSet<Point> pieces = new TreeSet<>();
		boolean[][] selected = new boolean[5][5];

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (selected[i][j]) {
					continue;
				}
				bfs(i, j, arr, selected, pieces);

			}
		}

		return pieces;
	}

	private static void bfs(int startR, int startC, int[][] arr, boolean[][] selected, TreeSet<Point> pieces) {

		boolean[][] visited = new boolean[5][5];
		Queue<Point> pos = new LinkedList<>();
		Queue<Integer> queue = new LinkedList<>();
		queue.add(startR);
		queue.add(startC);

		while (!queue.isEmpty()) {
			int nowR = queue.poll();
			int nowC = queue.poll();

			pos.add(new Point(nowR, nowC));
			visited[nowR][nowC] = true;

			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (visited[nextR][nextC]) {
					continue;
				}

				if (selected[nextR][nextC]) {
					continue;
				}

				if (arr[startR][startC] != arr[nextR][nextC]) {
					continue;
				}

				queue.add(nextR);
				queue.add(nextC);
			}
		}

		if (pos.size() >= 3) {
			while (!pos.isEmpty()) {
				Point p = pos.poll();
				selected[p.r][p.c] = true;
				pieces.add(p);
			}
		}

	}

	private static boolean isOutOfBounds(int r, int c) {
		return r < 0 || r > 4 || c < 0 || c > 4;
	}

	static class Point implements Comparable<Point> {
		int r, c;

		public Point(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}

		@Override
		public int compareTo(Point o) {

			if (this.c == o.c) {
				return Integer.compare(o.r, this.r);
			}
			return Integer.compare(this.c, o.c);
		}
	}

	}
