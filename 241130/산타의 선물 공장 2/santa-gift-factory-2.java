import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {

    static int n, m;
    static Box[] belts, tails, allBoxes;
    static int[] boxCount;


    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();
        int m_src, m_dst;

        st = new StringTokenizer(br.readLine());
        int q = Integer.parseInt(st.nextToken());

        for (int i = 0; i < q; i++) {
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());

            switch (cmd) {
                case 100:
                    init(st);
                    break;
                case 200:
                    m_src = Integer.parseInt(st.nextToken());
                    m_dst = Integer.parseInt(st.nextToken());

                    sb.append(moveBox(m_src, m_dst)+"\n");
                    break;
                case 300:
                    m_src = Integer.parseInt(st.nextToken());
                    m_dst = Integer.parseInt(st.nextToken());

                    sb.append(replaceFirstBox(m_src, m_dst)+"\n");
                    break;
                case 400:
                    m_src = Integer.parseInt(st.nextToken());
                    m_dst = Integer.parseInt(st.nextToken());

                    sb.append(divideBox(m_src, m_dst)+"\n");
                    break;
                case 500:
                    int p_num = Integer.parseInt(st.nextToken());
                    sb.append(getBoxInfo(p_num)+"\n");
                    break;
                case 600:
                    int b_num = Integer.parseInt(st.nextToken());
                    sb.append(getBeltInfo(b_num)+"\n");
                    break;
            }
        }
        System.out.println(sb);
    }

    private static int getBeltInfo(int bNum) {
        int a = belts[bNum] == null ? -1 : belts[bNum].id;
        int b = tails[bNum] == null ? -1 : tails[bNum].id;
        int c = boxCount[bNum];

        return a + (2 * b) + (3 * c);
    }

    private static int getBoxInfo(int pNum) {

        Box box = allBoxes[pNum];
        int a = box.prev == null ? -1 : box.prev.id;
        int b = box.next == null ? -1 : box.next.id;

        return a + (2 * b);

    }

    private static int divideBox(int mSrc, int mDst) {

        if (boxCount[mSrc] <= 1) {
            return boxCount[mDst];
        }

        // floor(n/2)번째 box 찾기
        Box startBox = belts[mSrc];
        Box endBox = belts[mSrc];
        double cnt = Math.floor((double) boxCount[mSrc] / 2);
        for (int i = 1; i < cnt; i++) {
            endBox = endBox.next;
        }

        belts[mSrc] = endBox.next;
        if (belts[mSrc] != null) {
            belts[mSrc].prev = null;
        }

        endBox.next = belts[mDst];

        if (belts[mDst] != null) {
            belts[mDst].prev = endBox;
        } else {
            tails[mDst] = endBox;
        }

        belts[mDst] = startBox;

        boxCount[mDst] += (int) cnt;
        boxCount[mSrc] -= (int) cnt;

        return boxCount[mDst];
    }

    private static int replaceFirstBox(int mSrc, int mDst) {

        Box srcBox = belts[mSrc];
        Box dstBox = belts[mDst];

        if (srcBox == null && dstBox == null) {
            return boxCount[mDst];
        }

        if (srcBox == null) {
            belts[mSrc] = dstBox;
            belts[mDst] = dstBox.next;
            if (belts[mDst] != null) {
                belts[mDst].prev = null;
                belts[mSrc].next = null;
            } else {
                tails[mDst] = null;
            }

            tails[mSrc] = dstBox;

            boxCount[mSrc]++;
            boxCount[mDst]--;
        } else if (dstBox == null) {
            belts[mDst] = srcBox;
            belts[mSrc] = srcBox.next;
            if (belts[mSrc] != null) {
                belts[mSrc].prev = null;
                belts[mDst].next = null;
            } else {
                tails[mSrc] = null;
            }

            tails[mDst] = srcBox;

            boxCount[mDst]++;
            boxCount[mSrc]--;
        } else {
            Box dstSecond = dstBox.next;
            Box srcSecond = srcBox.next;

            belts[mDst] = srcBox;
            belts[mSrc] = dstBox;

            belts[mDst].next = dstSecond;
            if (dstSecond != null) {
                dstSecond.prev = belts[mDst];
            }

            belts[mSrc].next = srcSecond;
            if (srcSecond != null) {
                srcSecond.prev = belts[mSrc];
            }

            if (boxCount[mDst] == 1) {
                tails[mDst] = srcBox;
            }
            if (boxCount[mSrc] == 1) {
                tails[mSrc] = dstBox;
            }
        }

        return boxCount[mDst];
    }

    private static int moveBox(int mSrc, int mDst) {

        if (boxCount[mSrc] == 0) {
            return boxCount[mDst];
        }

        if (belts[mDst] != null) {
            belts[mDst].prev = tails[mSrc];
        } else {
            tails[mDst] = tails[mSrc];
        }
        tails[mSrc].next = belts[mDst];

        belts[mDst] = belts[mSrc];

        belts[mSrc] = null;
        tails[mSrc] = null;

        boxCount[mDst] += boxCount[mSrc];
        boxCount[mSrc] = 0;

        return boxCount[mDst];


    }

    private static void init(StringTokenizer st) {
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        belts = new Box[n + 1];
        tails = new Box[n + 1];
        boxCount = new int[n + 1];
        allBoxes = new Box[m + 1];

        for (int id = 1; id <= m; id++) {
            int b_num = Integer.parseInt(st.nextToken());
            Box newBox = new Box(id, null, null);
            if (belts[b_num] == null) {
                belts[b_num] = newBox;
            } else {
                tails[b_num].next = newBox;
                newBox.prev = tails[b_num];
            }
            tails[b_num] = newBox;
            boxCount[b_num]++;
            allBoxes[id] = newBox;
        }

    }

    static class Box {

        int id;
        Box prev, next;

        public Box(int id, Box prev, Box next) {
            this.id = id;
            this.prev = prev;
            this.next = next;
        }

    }
}
