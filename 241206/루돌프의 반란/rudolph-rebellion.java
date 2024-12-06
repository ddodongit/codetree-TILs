import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static final int[] dr = { 0, -1, -1, 0, 1, 1, 1, 0, -1 }, dc = { 0, 0, 1, 1, 1, 0, -1, -1, -1 };
	static int N, M, P, C, D, K;
	static int[][] santaMap;
	static boolean[] isFainted;
	static HashMap<Integer, HashSet<Integer>> faintedSanta; // < k turn, hs<id> >
	static Point rudolph;
	static HashMap<Integer, Point> allSanta;
	static int[] scores;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine());
		int r = Integer.parseInt(st.nextToken());
		int c = Integer.parseInt(st.nextToken());
		rudolph = new Point(-1, r, c);

		santaMap = new int[N + 1][N + 1];
		allSanta = new HashMap<>();
		isFainted = new boolean[P + 1];
		faintedSanta = new HashMap<>();
		scores = new int[P + 1];
		for (int p = 1; p <= P; p++) {
			st = new StringTokenizer(br.readLine());
			int pn = Integer.parseInt(st.nextToken());
			int sr = Integer.parseInt(st.nextToken());
			int sc = Integer.parseInt(st.nextToken());

			allSanta.put(pn, new Point(pn, sr, sc));
			santaMap[sr][sc] = pn;
		}

		for (K = 1; K <= M; K++) {
			// move rudolph
			moveRudolph();

			// move santa
			moveAllSanta();

			if (allSanta.isEmpty()) {
				break;
			}

			for (Integer id : allSanta.keySet()) {
				scores[id] += 1;
			}

			HashSet<Integer> done = faintedSanta.remove(K);
			if (done != null) {
				for (Integer id : done) {
					isFainted[id] = false;
				}
			}
		}
        
        for(int i=1; i<= P; i++) {
			System.out.print(scores[i]+" ");
		}

	}

	private static void moveAllSanta() {

		for (int id = 1; id <= P; id++) {
			if (!allSanta.containsKey(id)) {
				continue;
			}
			if (isFainted[id]) {
				continue;
			}

			Point santa = allSanta.get(id);
			int minDist = getDistance(santa.r, santa.c, rudolph.r, rudolph.c);
			int dir = 0;
			for (int d = 1; d < 8; d += 2) {
				int nextR = santa.r + dr[d];
				int nextC = santa.c + dc[d];

				if (isOutOfBounds(nextR, nextC)) {
					continue;
				}
				if (santaMap[nextR][nextC] != 0) {
					continue;
				}

				int dist = getDistance(nextR, nextC, rudolph.r, rudolph.c);

				if (minDist > dist) {
					minDist = dist;
					dir = d;
				}
			}

			santaMap[santa.r][santa.c] = 0;
			santa.r += dr[dir];
			santa.c += dc[dir];
			santaMap[santa.r][santa.c] = santa.id;

			if (rudolph.r == santa.r && rudolph.c == santa.c) {
				// opposite dir
				dir = (dir + 4) % 8;
				collide(santa, dir, D);
			}
		}

	}

	private static void moveRudolph() {

		Point nearSanta = findSanta();
		int id = santaMap[nearSanta.r][nearSanta.c];
		int moveDir = getDirection(rudolph, nearSanta);

		// move
		rudolph.r += dr[moveDir];
		rudolph.c += dc[moveDir];

		// collision
		if (santaMap[rudolph.r][rudolph.c] != 0) {
			collide(nearSanta, moveDir, C);

		}

	}

	private static void collide(Point santa, int dir, int score) {

		scores[santa.id] += score;

		int nextR = santa.r + dr[dir] * score;
		int nextC = santa.c + dc[dir] * score;

		if (isOutOfBounds(nextR, nextC)) {
			allSanta.remove(santa.id);
			return;
		}

		// faint
		isFainted[santa.id] = true;
		faintedSanta.computeIfAbsent(K + 1, k -> new HashSet<>()).add(santa.id);

		if (santaMap[nextR][nextC] != 0) {
			interact(santa, nextR, nextC, dir);
		}

		else {
			santa.r = nextR;
			santa.c = nextC;
			santaMap[nextR][nextC] = santa.id;
		}

	}

	private static void interact(Point nowSanta, int nextR, int nextC, int dir) {

		while (true) {
			int id = santaMap[nextR][nextC];
			Point otherSanta = allSanta.get(id);

			santaMap[nextR][nextC] = nowSanta.id;
			nowSanta.r = nextR;
			nowSanta.c = nextC;

			nextR += dr[dir];
			nextC += dc[dir];

			if (isOutOfBounds(nextR, nextC)) {
				break;
			}

			if (santaMap[nextR][nextC] == 0) {
				otherSanta.r = nextR;
				otherSanta.c = nextC;
				santaMap[nextR][nextC] = otherSanta.id;
				break;
			}

			nowSanta = otherSanta;

		}

	}

	private static Point findSanta() {

		PriorityQueue<Point> pq = new PriorityQueue<>(new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {

				if (o1.dist == o2.dist) {
					if (o1.r == o2.r) {
						return Integer.compare(o2.c, o1.c);
					}
					return Integer.compare(o2.r, o1.r);
				}
				return Integer.compare(o1.dist, o2.dist);
			}

		});

		for (Integer id : allSanta.keySet()) {
			Point santa = allSanta.get(id);
			int dist = getDistance(rudolph.r, rudolph.c, santa.r, santa.c);
			santa.dist = dist;
			pq.add(santa);
		}

		return pq.poll();
	}

	private static int getDirection(Point from, Point to) {
		if (from.r < to.r) {
			if (from.c < to.c) {
				return 4;
			} else if (from.c > to.c) {
				return 6;
			} else {
				return 5;
			}

		} else if (from.r > to.r) {
			if (from.c < to.c) {
				return 2;
			} else if (from.c > to.c) {
				return 8;
			} else {
				return 1;
			}

		} else {
			if (from.c < to.c) {
				return 3;
			} else if (from.c > to.c) {
				return 7;
			} else {
				return 0;
			}
		}
	}

	private static int getDistance(int fromR, int fromC, int toR, int toC) {
		return (int) (Math.pow(fromR - toR, 2) + Math.pow(fromC - toC, 2));
	}

	private static boolean isOutOfBounds(int r, int c) {
		return r < 1 || r > N || c < 1 || c > N;
	}

	static class Point {
		int id, r, c, dist;

		public Point(int id, int r, int c) {
			super();
			this.id = id;
			this.r = r;
			this.c = c;
		}

	}

}
