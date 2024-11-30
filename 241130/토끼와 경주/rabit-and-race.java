import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

    final static int[] dr = {-1, 0, 1, 0}, dc = {0, 1, 0, -1};
    static int N, M, P;
    static ArrayList<Rabbit>[][] map;
    static HashMap<Integer, Rabbit> allRabbits;
    static PriorityQueue<Rabbit> jumpPQ;


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        int Q = Integer.parseInt(st.nextToken());

        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            switch (cmd) {
                case 100:
                    init(st);
                    break;
                case 200:
                    int K = Integer.parseInt(st.nextToken());
                    int S = Integer.parseInt(st.nextToken());
                    play(K, S);
                    break;
                case 300:
                    int pid = Integer.parseInt(st.nextToken());
                    int L = Integer.parseInt(st.nextToken());
                    changeDist(pid, L);
                    break;
                case 400:
                    printBestRabbit();
                    break;
            }
        }

    }

    private static void printBestRabbit() {

        PriorityQueue<Rabbit> pq = new PriorityQueue<>(new Comparator<Rabbit>() {

            @Override
            public int compare(Rabbit o1, Rabbit o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });

        for (Rabbit rabbit : allRabbits.values()) {
            pq.add(rabbit);
        }

        System.out.println(pq.poll().score);
    }

    private static void changeDist(int pid, int L) {
        allRabbits.get(pid).dist *= L;
    }

    private static void play(int K, int S) {

        HashSet<Integer> pickedRabbits = new HashSet<>();

        for (int k = 0; k < K; k++) {
            Rabbit rabbit = jumpPQ.poll();
            pickedRabbits.add(rabbit.pid);

            moveToNextPos(rabbit);

            for (int id : allRabbits.keySet()) {
                if (id == rabbit.pid) {
                    continue;
                }
                allRabbits.get(id).score += rabbit.rcSum;
            }

            rabbit.totalJump += 1;
            jumpPQ.add(rabbit);
        }

        PriorityQueue<Rabbit> pq = new PriorityQueue<>(new Comparator<Rabbit>() {
            @Override
            public int compare(Rabbit o1, Rabbit o2) {
                if (o1.rcSum == o2.rcSum) {
                    if (o1.r == o2.r) {
                        if (o1.c == o2.c) {
                            return Integer.compare(o2.pid, o1.pid);
                        }
                        return Integer.compare(o2.c, o1.c);
                    }
                    return Integer.compare(o2.r, o1.r);
                }
                return Integer.compare(o2.rcSum, o1.rcSum);
            }
        });

        for (Integer id : pickedRabbits) {
            pq.add(allRabbits.get(id));
        }

        Rabbit bestRabbit = pq.poll();
        bestRabbit.score += S;

    }


    private static void moveToNextPos(Rabbit rabbit) {

        int maxR = Integer.MIN_VALUE, maxC = Integer.MIN_VALUE, maxSum = Integer.MIN_VALUE;

        for (int d = 0; d < 4; d++) {
            int distR = rabbit.dist * dr[d];
            int distC = rabbit.dist * dc[d];

            int nextR = getNextPos(rabbit.r, distR, N - 1);
            int nextC = getNextPos(rabbit.c, distC, M - 1);

            if (maxSum < nextR + nextC) {
                maxSum = nextR + nextC;
                maxR = nextR;
                maxC = nextC;
            } else if (maxSum == nextR + nextC) {
                if (maxR < nextR) {
                    maxR = nextR;
                    maxC = nextC;
                } else if (maxR == nextR) {
                    if (maxC < nextC) {
                        maxC = nextC;
                    }
                }
            }
        }

        map[rabbit.r][rabbit.c].remove(rabbit);
        map[maxR][maxC].add(rabbit);
        rabbit.r = maxR;
        rabbit.c = maxC;
        rabbit.rcSum = maxR + 1 + maxC + 1;

    }

    private static int getNextPos(int start, int dist, int len) {

        dist %= 2 * len;

        if (len - start >= dist) {
            return start + dist;
        } else {
            return len - (dist - (len - start));
        }
    }

    private static void init(StringTokenizer st) {
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());

        allRabbits = new HashMap<>();
        map = new ArrayList[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                map[i][j] = new ArrayList<>();
            }
        }

        jumpPQ = new PriorityQueue<>(new Comparator<Rabbit>() {
            @Override
            public int compare(Rabbit o1, Rabbit o2) {
                if (o1.totalJump == o2.totalJump) {
                    if (o1.rcSum == o2.rcSum) {
                        if (o1.r == o2.r) {
                            if (o1.c == o2.c) {
                                return Integer.compare(o1.pid, o2.pid);
                            }
                            return Integer.compare(o1.c, o2.c);
                        }
                        return Integer.compare(o1.r, o2.r);
                    }
                    return Integer.compare(o1.rcSum, o2.rcSum);
                }
                return Integer.compare(o1.totalJump, o2.totalJump);
            }
        });

        for (int p = 0; p < P; p++) {
            int pid = Integer.parseInt(st.nextToken());
            int dist = Integer.parseInt(st.nextToken());

            Rabbit newRabbit = new Rabbit(pid, dist, 0, 0, 0, 2, 0);
            allRabbits.put(pid, newRabbit);
            map[0][0].add(newRabbit);
            jumpPQ.add(newRabbit);
        }

    }

    static class Rabbit {

        int pid, dist, totalJump, r, c, rcSum, score;

        public Rabbit(int pid, int dist, int totalJump, int r, int c, int rcSum, int score) {
            this.pid = pid;
            this.dist = dist;
            this.totalJump = totalJump;
            this.r = r;
            this.c = c;
            this.rcSum = rcSum;
            this.score = score;
        }
    }
}