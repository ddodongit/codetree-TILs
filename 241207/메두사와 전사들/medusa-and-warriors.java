import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static final int[] dr = { -1, 1, 0, 0 }, dc = { 0, 0, -1, 1 }; // 상하좌우
	static int N, M;
	static int[][] medusaMap, sightMap;
	static Point medusa, park;
	static HashMap<Integer, Point> warriors;
	static HashSet<Integer> stoneWarriors;
	static ArrayList<Point>[][] warriorMap;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine());
		int sr = Integer.parseInt(st.nextToken());
		int sc = Integer.parseInt(st.nextToken());
		medusa = new Point(sr, sc);
		int er = Integer.parseInt(st.nextToken());
		int ec = Integer.parseInt(st.nextToken());
		park = new Point(er, ec);

		warriors = new HashMap<>();
		warriorMap = new ArrayList[N][N];
		sightMap = new int[N][N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				warriorMap[i][j] = new ArrayList<>();
			}
		}
		st = new StringTokenizer(br.readLine());
		for (int i = 1; i <= M; i++) {
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());

			Point warrior = new Point(r, c);
			warrior.id = i;
			warriors.put(i, warrior);
			warriorMap[r][c].add(warrior);
		}

		stoneWarriors = new HashSet<>();
		medusaMap = new int[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				medusaMap[i][j] = Integer.parseInt(st.nextToken());
				medusaMap[i][j] = medusaMap[i][j] == 1 ? Integer.MAX_VALUE : -1;
			}
		}

		if (!bfs()) {
			System.out.println(-1);
			return;
		}

		while (true) {
			// move medusa
			moveMedusa();
			if (medusa.r == park.r && medusa.c == park.c) {
				sb.append(0);
				break;
			}

			// change view
			changeView();

			// move warriors
			int move = moveWarrior();

			// attack
			int cnt = attack();

			sb.append(move + " " + stoneWarriors.size() + " " + cnt + "\n");

			// stone
			stoneWarriors.clear();

		}

		System.out.println(sb);

	}

	private static int attack() {

		int cnt = warriorMap[medusa.r][medusa.c].size();

		for (Point p : warriorMap[medusa.r][medusa.c]) {
			warriors.remove(p.id);

		}

		warriorMap[medusa.r][medusa.c].clear();

		return cnt;

	}

	private static int moveWarrior() {

		int cnt = 0;
		for (Integer id : warriors.keySet()) {
			if (stoneWarriors.contains(id)) {
				continue;
			}
			Point warrior = warriors.get(id);
			if (warrior.r == medusa.r && warrior.c == medusa.c) {
				continue;
			}

			int nowR = warrior.r;
			int nowC = warrior.c;

			int minDist = getDistance(nowR, nowC);
			int dir = -1;

			// first
			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (sightMap[nextR][nextC] == 1) {
					continue;
				}
				int dist = getDistance(nextR, nextC);
				if (dist < minDist) {
					dir = d;
					minDist = dist;
				}
			}

			if (dir == -1) {
				continue;
			}

			cnt++;
			warriorMap[nowR][nowC].remove(warrior);

			warrior.r += dr[dir];
			warrior.c += dc[dir];

			warriorMap[warrior.r][warrior.c].add(warrior);
			dir = -1;

			nowR = warrior.r;
			nowC = warrior.c;

			// second
			for (int d = 2; d < 6; d++) {
				int nextR = nowR + dr[d % 4];
				int nextC = nowC + dc[d % 4];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (sightMap[nextR][nextC] == 1) {
					continue;
				}

				int dist = getDistance(nextR, nextC);
				if (dist < minDist) {
					dir = d % 4;
					minDist = dist;
				}
			}
			if (dir == -1) {
				continue;
			}

			cnt++;
			warriorMap[nowR][nowC].remove(warrior);

			warrior.r += dr[dir];
			warrior.c += dc[dir];

			warriorMap[warrior.r][warrior.c].add(warrior);

		}

		return cnt;
	}

	private static int getDistance(int nowR, int nowC) {
		return Math.abs(medusa.r - nowR) + Math.abs(medusa.c - nowC);
	}

	private static void changeView() {

		int maxCnt = 0;
		int dir = 0;

		for (int d = 0; d < 4; d++) {
			int[][] tmpSightMap = new int[N][N];
			HashSet<Integer> result = countWarriors(tmpSightMap, d);
			if (result.size() > maxCnt) {
				dir = d;
				maxCnt = result.size();
				sightMap = tmpSightMap;
				stoneWarriors = result;
			}
		}

	}

	private static HashSet<Integer> countWarriors(int[][] tmpSightMap, int dir) {

		HashSet<Integer> hs = new HashSet<>();

		int startR = medusa.r + dr[dir];
		int startC = medusa.c + dc[dir];

		if (isOutOfBounds(startR, startC)) {
			return hs;
		}

		if (dir == 0 || dir == 1) {
			int endR = dir == 0 ? 0 : N - 1;

			for (int r = startR;; r += dr[dir]) {
				int len = Math.abs(r - startR) + 1;
				int fromC = Math.max(startC - len, 0);
				int toC = Math.min(startC + len, N - 1);

				for (int c = fromC; c <= toC; c++) {
					if (tmpSightMap[r][c] == -1) {
						continue;
					}
					tmpSightMap[r][c] = 1;
					if (!warriorMap[r][c].isEmpty()) {
						setSightMap(r, c, dir, tmpSightMap);
						for (Point p : warriorMap[r][c]) {
							hs.add(p.id);
						}
					}
				}
				if (r == endR) {
					break;
				}
			}
		} else if (dir == 2 || dir == 3) {
			int endC = dir == 2 ? 0 : N - 1;

			for (int c = startC;; c += dc[dir]) {
				int len = Math.abs(c - startC) + 1;
				int fromR = Math.max(startR - len, 0);
				int toR = Math.min(startR + len, N - 1);

				for (int r = fromR; r <= toR; r++) {
					if (tmpSightMap[r][c] == -1) {
						continue;
					}
					tmpSightMap[r][c] = 1;
					if (!warriorMap[r][c].isEmpty()) {
						setSightMap(r, c, dir, tmpSightMap);
						for (Point p : warriorMap[r][c]) {
							hs.add(p.id);
						}
					}
				}
				if (c == endC) {
					break;
				}
			}
		}
		return hs;
	}

	private static void setSightMap(int nowR, int nowC, int dir, int[][] tmpSightMap) {

		int startR = nowR + dr[dir];
		int startC = nowC + dc[dir];

		if (dir == 0 || dir == 1) {
			int endC = -1;
			int d = 0;
			if (medusa.r > nowR) {
				if (medusa.c < nowC) {
					d = 1;
					endC = N - 1;
				} else if (medusa.c == nowC) {
					d = 0;
					endC = nowC;
				} else {
					d = -1;
					endC = 0;
				}
				int len = 1;
				for (int r = startR; r >= 0; r--) {
					for (int c = startC;; c += d) {
						tmpSightMap[r][c] = -1;
						if (c == startC + (len * d) || c == endC) {
							break;
						}
					}
					len++;
				}
			} else if (medusa.r < nowR) {
				if (medusa.c < nowC) {
					endC = N - 1;
					d = 1;
				} else if (medusa.c == nowC) {
					endC = nowC;
					d = 0;
				} else {
					endC = 0;
					d = -1;
				}
				int len = 1;
				for (int r = startR; r <= N - 1; r++) {
					for (int c = startC;; c += d) {
						tmpSightMap[r][c] = -1;
						if (c == startC + (len * d) || c == endC) {
							break;
						}
					}
					len++;
				}
			}
		}

		else if (dir == 2) {
			int endR = -1;
			int d = 0;
			if (medusa.r < nowR) {
				if (medusa.c > nowC) {
					d = 1;
					endR = N - 1;
				}
			} else if (medusa.r == nowR) {
				if (medusa.c > nowC) {
					d = 0;
					endR = nowR;
				}
			}

			else {
				if (medusa.c > nowC) {
					d = -1;
					endR = 0;
				}
			}

			int len = 1;
			for (int c = startC; c >= 0; c--) {
				for (int r = startR;; r += d) {
					tmpSightMap[r][c] = -1;
					if (r == startR + (len * d) || r == endR) {
						break;
					}
				}
				len++;
			}

		} else if (dir == 3) {
			int endR = -1;
			int d = 0;
			if (medusa.r < nowR) {
				if (medusa.c < nowC) {
					endR = N - 1;
					d = 1;
				}
			} else if (medusa.r == nowR) {
				if (medusa.c < nowC) {
					d = 0;
					endR = nowR;
				}
			} else {
				if (medusa.c < nowC) {
					endR = 0;
					d = -1;
				}
			}

			int len = 1;
			for (int c = startC; c <= N - 1; c++) {
				for (int r = startR;; r += d) {
					tmpSightMap[r][c] = -1;
					if (r == startR + (len * d) || r == endR) {
						break;
					}
				}
				len++;
			}

		}

	}

	private static void moveMedusa() {

		int minVal = Integer.MAX_VALUE;
		int dir = -1;

		for (int d = 0; d < 4; d++) {
			int nextR = medusa.r + dr[d];
			int nextC = medusa.c + dc[d];

			if (isOutOfBounds(nextR, nextC)) {
				continue;
			}
			if (medusaMap[nextR][nextC] == Integer.MAX_VALUE) {
				continue;
			}
			if (medusaMap[nextR][nextC] == -1) {
				continue;
			}

			if (medusaMap[nextR][nextC] >= minVal) {
				continue;
			}
			
			dir = d;
			minVal = medusaMap[nextR][nextC];
		}

		medusa.r += dr[dir];
		medusa.c += dc[dir];

		if (!warriorMap[medusa.r][medusa.c].isEmpty()) {
			kill();
		}

	}

	private static void kill() {
		ArrayList<Point> killedWarriors = warriorMap[medusa.r][medusa.c];

		for (Point p : killedWarriors) {
			warriors.remove(p.id);
		}

		warriorMap[medusa.r][medusa.c].clear();

	}

	private static boolean bfs() {

		Queue<Integer> queue = new LinkedList<>();
		queue.add(park.r);
		queue.add(park.c);
		medusaMap[park.r][park.c] = 0;

		while (!queue.isEmpty()) {
			int nowR = queue.poll();
			int nowC = queue.poll();

			if (nowR == medusa.r && nowC == medusa.c) {
				break;
			}

			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (medusaMap[nextR][nextC] != -1) {
					continue;
				}

				medusaMap[nextR][nextC] = medusaMap[nowR][nowC] + 1;
				queue.add(nextR);
				queue.add(nextC);
			}
		}

		return medusaMap[medusa.r][medusa.c] != -1;
	}

	private static boolean isOutOfBounds(int r, int c) {
		return r < 0 || r > N - 1 || c < 0 || c > N - 1;
	}

	static class Point {
		int id, r, c;

		public Point(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}


	}
}
