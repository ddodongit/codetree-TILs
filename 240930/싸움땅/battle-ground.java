import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static final int[] di = { -1, 0, 1, 0 }, dj = { 0, 1, 0, -1 };
	static int n, m, k;
	static PriorityQueue<Integer>[][] gunMap;
	static HashMap<Integer, Player> players;
	static Player[][] pMap;

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());

		gunMap = new PriorityQueue[n + 1][n + 1];
		pMap = new Player[n + 1][n + 1];
		players = new HashMap<>();

		for (int i = 1; i <= n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= n; j++) {
				gunMap[i][j] = new PriorityQueue<>(new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return Integer.compare(o2, o1);
					}
				});
				int mp = Integer.parseInt(st.nextToken());

				if (mp > 0)
					gunMap[i][j].add(mp);
			}
		}

		for (int id = 1; id <= m; id++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			Player newPlayer = new Player(x, y, s, 0, id, d);
			players.put(id, newPlayer);
			pMap[x][y] = newPlayer;
		}

		for (int i = 1; i <= k; i++) {
			play();
		}

		for (int i = 1; i <= m; i++) {
			System.out.print(players.get(i).score + " ");
		}
	}

	private static void play() {
		for (Player player : players.values()) {
			int nextI = player.r + di[player.dir];
			int nextJ = player.c + dj[player.dir];

			if (isOutOfBounds(nextI, nextJ)) {
				player.dir = getOppositeDir(player.dir);
				nextI = player.r + di[player.dir];
				nextJ = player.c + dj[player.dir];
			}

			// player X
			if (pMap[nextI][nextJ] == null) {
				// gun check
				if (!gunMap[nextI][nextJ].isEmpty()) {
					if (player.mp > 0) {
						if (gunMap[nextI][nextJ].peek() > player.mp) {
							int gun = gunMap[nextI][nextJ].poll();
							gunMap[nextI][nextJ].add(player.mp);
							player.mp = gun;
						}
					} else {
						player.mp = gunMap[nextI][nextJ].poll();
					}

				}

				// move
				pMap[player.r][player.c] = null;
				pMap[nextI][nextJ] = player;

				player.r = nextI;
				player.c = nextJ;

			} else { // player O
				Player[] result = fight(nextI, nextJ, player);
				Player winner = result[0];
				Player loser = result[1];

				pMap[player.r][player.c] = null;

				// movePlayer == loser
				if (loser == player) {
					loser.r = nextI;
					loser.c = nextJ;
				}

				// loser
				if (loser.mp > 0) {
					gunMap[nextI][nextJ].add(loser.mp);
					loser.mp = 0;
				}
				moveLoser(loser);

				// winner
				if (winner == player) {
					winner.r = nextI;
					winner.c = nextJ;
					pMap[winner.r][winner.c] = winner;
				}

				if (!gunMap[nextI][nextJ].isEmpty()) {
					if (winner.mp > 0) {
						if (gunMap[nextI][nextJ].peek() > winner.mp) {
							int gun = gunMap[nextI][nextJ].poll();
							gunMap[nextI][nextJ].add(winner.mp);
							winner.mp = gun;
						}
					} else {
						winner.mp = gunMap[nextI][nextJ].poll();
					}
				}

			}

		}

	}

	private static void moveLoser(Player loser) {

		while (true) {
			int nextI = loser.r + di[loser.dir];
			int nextJ = loser.c + dj[loser.dir];

			if (isOutOfBounds(nextI, nextJ) || pMap[nextI][nextJ] != null) {
				loser.dir = (loser.dir + 1) % 4;
				continue;
			}

			if (!gunMap[nextI][nextJ].isEmpty()) {
				loser.mp = gunMap[nextI][nextJ].poll();
			}

			pMap[nextI][nextJ] = loser;
			loser.r = nextI;
			loser.c = nextJ;

			return;
		}

	}

	private static Player[] fight(int nextI, int nextJ, Player movePlayer) {
		Player originPlayer = pMap[nextI][nextJ];
		Player winner, loser;

		if (originPlayer.hp + originPlayer.mp == movePlayer.hp + movePlayer.mp) {
			if (originPlayer.hp > movePlayer.hp) {
				winner = originPlayer;
				loser = movePlayer;
			} else {
				winner = movePlayer;
				loser = originPlayer;
			}
		} else if (originPlayer.hp + originPlayer.mp > movePlayer.hp + movePlayer.mp) {
			winner = originPlayer;
			loser = movePlayer;
		} else {
			winner = movePlayer;
			loser = originPlayer;
		}

		int diff = Math.abs((winner.hp + winner.mp) - (loser.hp + loser.mp));
		winner.score += diff;

		Player[] result = new Player[2];
		result[0] = winner;
		result[1] = loser;

		return result;

	}

	private static int getOppositeDir(int dir) {
		switch (dir) {
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 0;
		case 3:
			return 1;
		}
		return 0;
	}

	private static boolean isOutOfBounds(int i, int j) {
		return i < 1 || i > n || j < 1 || j > n;
	}

	static class Player {
		int r, c, hp, mp, id, dir, score;

		public Player(int r, int c, int hp, int mp, int id, int dir) {
			super();
			this.r = r;
			this.c = c;
			this.hp = hp;
			this.mp = mp;
			this.id = id;
			this.dir = dir;
		}
	}

}