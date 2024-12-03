import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static final int[] dr = { -1, 1, 0, 0 }, dc = { 0, 0, -1, 1 };// 상하좌우
	static int N, M, s_r, s_c, e_r, e_c;
	static ArrayList<Soldier>[][] soldierMap;
	static int[][] medusaMap, sightMap;
	static HashMap<Integer, Soldier> allSoldiers;
	static HashSet<Integer> rockSoldiers;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine());
		s_r = Integer.parseInt(st.nextToken());
		s_c = Integer.parseInt(st.nextToken());
		e_r = Integer.parseInt(st.nextToken());
		e_c = Integer.parseInt(st.nextToken());

		allSoldiers = new HashMap<>();
		soldierMap = new ArrayList[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				soldierMap[i][j] = new ArrayList<Soldier>();
			}
		}

		st = new StringTokenizer(br.readLine());
		for (int id = 1; id <= M; id++) {
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			Soldier soldier = new Soldier(id, r, c);
			allSoldiers.put(id, soldier);
			soldierMap[r][c].add(soldier);
		}

		medusaMap = new int[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				int val = Integer.parseInt(st.nextToken());
				medusaMap[i][j] = val == 1 ? Integer.MAX_VALUE : -1;
			}
		}

		if (!bfsMedusa()) {
			System.out.println(-1);
			return;
		}

		while (true) {
			// 1
			moveMedusa();
			if (s_r == e_r && s_c == e_c) {
				sb.append(0);
				break;
			}
			// 2
			checkVisible();
			// 3
			int moveDist = moveSoldiers();
			// 4
			int attackSoldiers = attackMedusa();

			sb.append(moveDist + " " + rockSoldiers.size() + " " + attackSoldiers + "\n");
			for (Integer id : rockSoldiers) {
				allSoldiers.get(id).isRock = false;
			}
			rockSoldiers.clear();
		}
		System.out.println(sb);

	}

	private static int attackMedusa() {
		int count = soldierMap[s_r][s_c].size();

		if (count > 0) {
			disappearSoldier();
		}
		return count;
	}

	private static void disappearSoldier() {
		for (Soldier soldier : soldierMap[s_r][s_c]) {
			allSoldiers.remove(soldier.id);
		}

		soldierMap[s_r][s_c].clear();
	}

	private static int moveSoldiers() {

		int count = 0;

		for (Soldier soldier : allSoldiers.values()) {
			if (soldier.isRock) {
				continue;
			}
			if (soldier.r == s_r && soldier.c == s_c) {
				continue;
			}

			int minDist = getDistance(soldier.r, soldier.c, s_r, s_c);
			int dir = -1;
			boolean flag = false;

			// first move
			for (int d = 0; d < 4; d++) {
				int nextR = soldier.r + dr[d % 4];
				int nextC = soldier.c + dc[d % 4];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (sightMap[nextR][nextC] == 1) {
					continue;
				}

				int dist = getDistance(nextR, nextC, s_r, s_c);

				if (dist < minDist) {
					minDist = dist;
					dir = d % 4;
					flag = true;
				}
			}

			if (flag) {
				int tmpR = soldier.r + dr[dir];
				int tmpC = soldier.c + dc[dir];
				dir = -1;
				count++;
				flag = false;

				// second move
				for (int d = 2; d <= 5; d++) {
					int nextR = tmpR + dr[d % 4];
					int nextC = tmpC + dc[d % 4];

					if (isOutOfBounds(nextR, nextC)) {
						continue;
					}
					if (sightMap[nextR][nextC] == 1) {
						continue;
					}

					int dist = getDistance(nextR, nextC, s_r, s_c);

					if (dist < minDist) {
						minDist = dist;
						dir = d % 4;
						flag = true;
					}
				}
				soldierMap[soldier.r][soldier.c].remove(soldier);
				if (flag) {
					count++;
					tmpR += dr[dir];
					tmpC += dc[dir];
				}
				soldier.r = tmpR;
				soldier.c = tmpC;
				soldierMap[soldier.r][soldier.c].add(soldier);
			}
		}

		return count;
	}

	private static void checkVisible() {
		int maxCnt = 0;
		int dir = -1;

		for (int d = 0; d < 4; d++) {
			HashSet<Integer> hs = new HashSet<>();
			int[][] invisibleMap = new int[N][N];
			int ret = countSoldiers(d, hs, invisibleMap);
			if (ret > maxCnt) {
				maxCnt = ret;
				dir = d;
				rockSoldiers = hs;
				sightMap = invisibleMap;
			}
		}

		for (Integer id : rockSoldiers) {
			allSoldiers.get(id).isRock = true;
		}

	}

	private static int countSoldiers(int dir, HashSet<Integer> rock, int[][] invisibleMap) {

		int startR = s_r + dr[dir];
		int startC = s_c + dc[dir];
		int cnt = 0;

		int y, dy, startX;
		if (dir < 2) {
			y = s_r;
			dy = dr[dir];
			startX = startC;
		} else {
			y = s_c;
			dy = dc[dir];
			startX = startR;
		}

		int toY = dir % 2 == 0 ? 0 : N - 1;
		int halfLen = 0;

		while (y != toY) {
			y += dy;
			halfLen++;
			int fromX = Math.max(startX - halfLen, 0);
			int toX = Math.min(startX + halfLen, N - 1);
			for (int x = fromX; x <= toX; x++) {
				int r = dir < 2 ? y : x;
				int c = dir < 2 ? x : y;

				if (invisibleMap[r][c] == -1) { // hide
					continue;
				}

				invisibleMap[r][c] = 1;
				if (soldierMap[r][c].isEmpty()) {
					continue;
				}
				makeInvisible(dir < 2, r, c, invisibleMap);
				cnt += soldierMap[r][c].size();

				for (Soldier soldier : soldierMap[r][c]) {
					rock.add(soldier.id);
				}
			}
		}

		return cnt;

	}

	private static void makeInvisible(boolean isVertical, int nowR, int nowC, int[][] invisibleMap) {

		int dir = getDirection(nowR, nowC);

		if (dir < 4) {
			while (true) {
				nowR += dr[dir];
				nowC += dc[dir];

				if (isOutOfBounds(nowR, nowC)) {
					break;
				}

				invisibleMap[nowR][nowC] = -1;
			}
		} else {

			boolean isIncreased = false;
			if (dir == 4) {
				if (isVertical) {
					isIncreased = false;
					dir = 0;
				} else {
					isIncreased = true;
					dir = 2;
				}
			} else if (dir == 5) {
				isIncreased = true;
				if (isVertical) {
					dir = 0;
				} else {
					dir = 3;
				}
			} else if (dir == 6) {
				isIncreased = false;
				if (isVertical) {
					dir = 1;
				} else {
					dir = 2;
				}
			} else if (dir == 7) {
				if (isVertical) {
					isIncreased = true;
					dir = 1;
				} else {
					isIncreased = false;
					dir = 3;
				}
			}

			int y, dy, startX;
			int startR = nowR + dr[dir];
			int startC = nowC + dc[dir];
			if (isVertical) {
				y = nowR;
				dy = dr[dir];
				startX = startC;
			} else {
				y = nowC;
				dy = dc[dir];
				startX = startR;
			}

			int toY = dir % 2 == 0 ? 0 : N - 1;
			int halfLen = 0;
			int fromX = 0, toX = 0;

			while (y != toY) {
				y += dy;
				halfLen++;

				if (dir == 0 || dir == 1) {
					fromX = isIncreased ? startX : Math.max(startX - halfLen, 0);
					toX = isIncreased ? Math.min(startX + halfLen, N - 1) : startX;

				} else if (dir == 2 || dir == 3) {
					fromX = isIncreased ? Math.max(startX - halfLen, 0) : startX;
					toX = isIncreased ? startX : Math.min(startX + halfLen, N - 1);
				}

				for (int x = fromX; x <= toX; x++) {
					int r = dir < 2 ? y : x;
					int c = dir < 2 ? x : y;

					if (invisibleMap[r][c] == -1) { // hide
						continue;
					}

					invisibleMap[r][c] = -1;
				}
			}
		}

	}

	private static int getDirection(int r, int c) {

		if (r < s_r) {
			if (c < s_c) {
				return 4;
			} else if (c == s_c) {
				return 0;
			} else {
				return 5;
			}
		} else if (r == s_r) {
			if (c < s_c) {
				return 2;
			} else if (c > s_c) {
				return 3;
			}
		} else {
			if (c < s_c) {
				return 6;
			} else if (c == s_c) {
				return 1;
			} else {
				return 7;
			}
		}
		return -1;
	}

	private static void moveMedusa() {

		int minDist = Integer.MAX_VALUE;
		int dir = -1;

		for (int d = 0; d < 4; d++) {
			int nextR = s_r + dr[d];
			int nextC = s_c + dc[d];

			if (isOutOfBounds(nextR, nextC))
				continue;

			if (medusaMap[nextR][nextC] == Integer.MAX_VALUE)
				continue;

			if (medusaMap[nextR][nextC] < minDist) {
				minDist = medusaMap[nextR][nextC];
				dir = d;
			}
		}

		s_r += dr[dir];
		s_c += dc[dir];

		disappearSoldier();

	}

	private static boolean bfsMedusa() {

		Queue<Integer> queue = new ArrayDeque<>();

		queue.add(e_r);
		queue.add(e_c);
		medusaMap[e_r][e_c] = 0;

		while (!queue.isEmpty()) {
			int nowR = queue.poll();
			int nowC = queue.poll();

			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];

				if (isOutOfBounds(nextR, nextC))
					continue;

				if (medusaMap[nextR][nextC] != -1)
					continue;

				medusaMap[nextR][nextC] = medusaMap[nowR][nowC] + 1;
				queue.add(nextR);
				queue.add(nextC);

			}
		}

		return medusaMap[s_r][s_c] != -1;
	}

	private static int getDistance(int fromR, int fromC, int toR, int toC) {
		return Math.abs(fromR - toR) + Math.abs(fromC - toC);
	}

	private static boolean isOutOfBounds(int r, int c) {
		return r < 0 || r > N - 1 || c < 0 || c > N - 1;
	}

	static class Soldier {
		int id, r, c;
		boolean isRock;

		public Soldier(int id, int r, int c) {
			this.id = id;
			this.r = r;
			this.c = c;
		}

	}
}
