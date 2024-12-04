import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	static final int[] dr = { 0, 0, 1, -1 }, dc = { 1, -1, 0, 0 };// 동서남북
	static final int EMPTY = 0, BARRIER = 1, MACHINE = 2, WALL = 3, EXIT = 4;
	static final int EAST = 0, WEST = 1, SOUTH = 2, NORTH = 3, TOP = 4;

	static int N, M, F, startWall, startJ;
	static Point exit, machine, start;
	static int[][][] wall;
	static int[][] bottom, bottomMap;
	static Point[] timeAbnormal;
	static int[] spreadDir, spreadV;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		F = Integer.parseInt(st.nextToken());

		bottom = new int[N][N];

		wall = new int[5][M][M];

		// bottom
		Point wallBottom = null; // 바닥에서 벽의 시작점
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				bottom[i][j] = Integer.parseInt(st.nextToken());
				if (bottom[i][j] == EXIT) {
					bottom[i][j] = EMPTY;
					exit = new Point(i, j);
				}
				if (wallBottom == null && bottom[i][j] == WALL) {
					wallBottom = new Point(i, j);
				}
			}
		}

		// wall
		for (int k = 0; k < 5; k++) {
			wall[k] = new int[M][M];

			for (int i = 0; i < M; i++) {
				st = new StringTokenizer(br.readLine());
				for (int j = 0; j < M; j++) {
					wall[k][i][j] = Integer.parseInt(st.nextToken());
					if (k == TOP && wall[k][i][j] == MACHINE) {
						machine = new Point(i, j);
					}
					wall[k][i][j] = wall[k][i][j] == BARRIER ? Integer.MAX_VALUE : -1;

				}
			}
		}

		// time abnormality
			timeAbnormal = new Point[F + 1];
		spreadDir = new int[F + 1];
		spreadV = new int[F + 1];
		for (int i = 1; i <= F; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int v = Integer.parseInt(st.nextToken());
			timeAbnormal[i] = new Point(r, c);
			spreadDir[i] = d;
			spreadV[i] = v;
			bottom[r][c] = 1;

			if (isOutOfBounds(r + dr[d], c + dc[d], N)) {
				continue;
			}

			if (bottom[r + dr[d]][c + dc[d]] != EMPTY) {
				spreadV[i] = -1;
			}
		}


		// find start r,c
		findStartPos(wallBottom.r, wallBottom.c);

		// 바닥 start ~ machine까지 bfs
		if (!bfsWall(M - 1, startJ, machine.r, machine.c)) {
			System.out.println(-1);
			return;
		}

		// machine 바닥까지 오는 시간
		int time = wall[TOP][machine.r][machine.c] + 1;
		machine.r = start.r;
		machine.c = start.c;

		// time 동안 바닥에서 발생했던 시간 이상 처리
		for (int t = 1; t <= time; t++) {
			spreadAbnormal(t);
		}

		initBottomMap();
		int moveDist = bottomMap[machine.r][machine.c];
		int count = 0;

		while (true) {
			if (machine.r == exit.r && machine.c == exit.c) {
				break;
			}
			time += 1;
			if (spreadAbnormal(time)) {
				initBottomMap();
				if (moveDist < bottomMap[machine.r][machine.c]) {
					machine.r = start.r;
					machine.c = start.c;
					for (int i = 0; i < count; i++) {
						moveDist = moveMachine();
						if (moveDist == -1) {
							System.out.println(-1);
							return;
						}
					}
				}
			}
            if (machine.r == exit.r && machine.c == exit.c) {
				break;
			}
			moveDist = moveMachine();
			count++;
			if (moveDist == -1) {
				System.out.println(-1);
				return;
			}
		}

		System.out.println(time);
	}

	private static int moveMachine() {

		int minDist = bottomMap[machine.r][machine.c];
		int dir = -1;

		for (int d = 0; d < 4; d++) {
			int nextR = machine.r + dr[d];
			int nextC = machine.c + dc[d];

			if (isOutOfBounds(nextR, nextC, N)) {
				continue;
			}

			if (nextR != exit.r && nextC != exit.c && bottom[nextR][nextC] != EMPTY) {
				continue;
			}

			if (bottomMap[nextR][nextC] < minDist) {
				minDist = bottomMap[nextR][nextC];
				dir = d;
			}
		}

		if (dir == -1) {
			return -1;
		}

		machine.r += dr[dir];
		machine.c += dc[dir];

		return bottomMap[machine.r][machine.c];

	}

	private static void initBottomMap() {

		bottomMap = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				bottomMap[i][j] = bottom[i][j] != EMPTY ? Integer.MAX_VALUE : -1;
			}
		}

		bfsBottom(exit.r, exit.c, start.r, start.c, bottomMap);

	}

	private static boolean spreadAbnormal(int t) {

		boolean changed = false;
		for (int i = 1; i <= F; i++) {
			if (spreadV[i] == -1 || t < spreadV[i] || t % spreadV[i] != 0) {
				continue;
			}

			int dir = spreadDir[i];
			int nextR = timeAbnormal[i].r + dr[dir];
			int nextC = timeAbnormal[i].c + dc[dir];

			if (isOutOfBounds(nextR, nextC, N)) {
				continue;
			}
			if (bottom[nextR][nextC] != EMPTY) {
				continue;
			}

			bottom[nextR][nextC] = BARRIER;
			timeAbnormal[i].r = nextR;
			timeAbnormal[i].c = nextC;
			changed = true;

		}

		return changed;
	}

	private static void findStartPos(int wallBottomR, int wallBottomC) {

		for (int i = wallBottomR - 1; i <= wallBottomR + M; i++) {
			for (int j = wallBottomC - 1; j <= wallBottomC + M; j++) {
				if (isOutOfBounds(i, j, N)) {
					continue;
				}
				if (bottom[i][j] == EMPTY) {
					start = new Point(i, j);
					if (i == wallBottomR - 1) { // wall : north
						startWall = NORTH;
						startJ = M - 1 - (j - wallBottomC);
					} else if (i == wallBottomR + M) { // wall : south
						startWall = SOUTH;
						startJ = j - wallBottomC;
					} else {
						if (j == wallBottomC - 1) { // wall : west
							startWall = WEST;
							startJ = i - wallBottomR;
						} else if (j == wallBottomC + M) { // wall : east
							startWall = EAST;
							startJ = M - 1 - (i - wallBottomR);
						}
					}
					break;
				}
				if (wallBottomR - 1 < i && i < wallBottomR + M) {
					j += M;
				}
			}
		}
	}

	private static boolean bfsBottom(int fromR, int fromC, int toR, int toC, int[][] bottomMap) {

		Queue<Integer> queue = new LinkedList<>();
		queue.add(fromR);
		queue.add(fromC);

		bottomMap[fromR][fromC] = 0;

		while (!queue.isEmpty()) {
			int nowR = queue.poll();
			int nowC = queue.poll();

			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];

				if (isOutOfBounds(nextR, nextC, N)) {
					continue;
				}

				if (bottomMap[nextR][nextC] != -1) { // 장애물, 방문함
					continue;
				}

				bottomMap[nextR][nextC] = bottomMap[nowR][nowC] + 1;
				queue.add(nextR);
				queue.add(nextC);
			}
		}

		return bottomMap[toR][toC] != -1;
	}

	private static boolean bfsWall(int fromR, int fromC, int toR, int toC) {

		Queue<Integer> queue = new LinkedList<>();
		queue.add(fromR);
		queue.add(fromC);
		queue.add(startWall);

		wall[startWall][fromR][fromC] = 0;

		while (!queue.isEmpty()) {
			int nowR = queue.poll();
			int nowC = queue.poll();
			int nowWall = queue.poll();

			for (int d = 0; d < 4; d++) {
				int nextR = nowR + dr[d];
				int nextC = nowC + dc[d];
				int nextWall = nowWall;

				if (isOutOfBounds(nextR, nextC, M)) {
					// change r,c, dir
					int[] ret = changeWall(nextR, nextC, nextWall);
					nextR = ret[0];
					nextC = ret[1];
					nextWall = ret[2];
				}

				if (nextWall == -1 || wall[nextWall][nextR][nextC] != -1) { // 장애물, 방문함, 바닥으로
					continue;
				}

				wall[nextWall][nextR][nextC] = wall[nowWall][nowR][nowC] + 1;
				queue.add(nextR);
				queue.add(nextC);
				queue.add(nextWall);
			}
		}

		return wall[TOP][toR][toC] != -1;
	}

	private static int[] changeWall(int nextR, int nextC, int nextWall) {
		int[] ret = new int[3]; // r,c, wall

		switch (nextWall) {
		case EAST:
			if (nextR < 0) { // TOP
				// r <-> c
				nextR = M - 1 - nextC;
				nextC = M - 1;
				nextWall = TOP;
			} else if (nextR > M - 1) { // bottom
				nextWall = -1;
			} else if (nextC < 0) { // SOUTH
				nextC = M - 1;
				nextWall = SOUTH;
			} else if (nextC > M - 1) { // NORTH
				nextC = 0;
				nextWall = NORTH;
			}
			break;
		case WEST:
			if (nextR < 0) { // TOP
				// r <-> c
				nextR = nextC;
				nextC = 0;
				nextWall = TOP;
			} else if (nextR > M - 1) { // bottom
				nextWall = -1;
			} else if (nextC < 0) { // NORTH
				nextC = M - 1;
				nextWall = NORTH;
			} else if (nextC > M - 1) { // SOUTH
				nextC = 0;
				nextWall = SOUTH;
			}
			break;
		case SOUTH:
			if (nextR < 0) { // TOP
				nextR = M - 1;
				nextWall = TOP;
			} else if (nextR > M - 1) { // bottom
				nextWall = -1;
			} else if (nextC < 0) { // WEST
				nextC = M - 1;
				nextWall = WEST;
			} else if (nextC > M - 1) { // EAST
				nextC = 0;
				nextWall = EAST;
			}
			break;
		case NORTH:
			if (nextR < 0) { // TOP
				nextR = 0;
				nextC = M - 1 - nextC;
				nextWall = TOP;
			} else if (nextR > M - 1) { // bottom
				nextWall = -1;
			} else if (nextC < 0) { // EAST
				nextC = M - 1;
				nextWall = EAST;
			} else if (nextC > M - 1) { // WEST
				nextC = 0;
				nextWall = WEST;
			}
			break;
		case TOP:
			if (nextR < 0) { // NORTH
				nextR = 0;
				nextC = M - 1 - nextC;
				nextWall = NORTH;
			} else if (nextR > M - 1) { // SOUTH
				nextR = 0;
				nextWall = SOUTH;
			} else if (nextC < 0) { // WEST
				nextC = nextR;
				nextR = 0;
				nextWall = WEST;
			} else if (nextC > M - 1) { // EAST
				nextR = 0;
				nextC = M - 1 - nextR;
				nextWall = EAST;
			}
			break;
		}

		ret[0] = nextR;
		ret[1] = nextC;
		ret[2] = nextWall;

		return ret;

	}

	private static boolean isOutOfBounds(int i, int j, int len) {

		return i < 0 || i >= len || j < 0 || j >= len;
	}

	static class Point {
		int r, c;

		public Point(int r, int c) {
			super();
			this.r = r;
			this.c = c;
		}

	}
}
