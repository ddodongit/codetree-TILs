import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {

    static int n, m;
    static Box[] belts;
    static Box[] tails;
    static HashMap<Integer, Box> allBoxes;
    static int[] beltParents;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

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
                    int w_max = Integer.parseInt(st.nextToken());
                    System.out.println(unload_box(w_max));
                    break;
                case 300:
                    int r_id = Integer.parseInt(st.nextToken());
                    System.out.println(remove_box(r_id));
                    break;
                case 400:
                    int f_id = Integer.parseInt(st.nextToken());
                    System.out.println(find_box(f_id));
                    break;
                case 500:
                    int b_num = Integer.parseInt(st.nextToken());
                    System.out.println(breakdown_belt(b_num));
                    break;
            }
        }

    }

    private static int breakdown_belt(int b_num) {
        if (beltParents[b_num] != b_num) { // 고장
            return -1;
        }

        Box box = belts[b_num];

        if (box != null) {
            int idx = b_num + 1;
            while (idx != b_num) {
                if (idx > m) {
                    idx = 1;
                }
                if (beltParents[idx] == idx) { // 정상
                    // 옮기기

                    tails[idx].next = box;
                    box.prev = tails[idx];

                    tails[idx] = tails[b_num];
                    tails[b_num] = null;
                    belts[b_num] = null;

                    beltParents[b_num] = idx;
                    break;
                }
                idx++;
            }
        }

        return b_num;
    }

    private static int find_box(int fId) {

        if (!allBoxes.containsKey(fId)) {
            return -1;
        }

        Box box = allBoxes.get(fId);
        int bId = box.beltId;
        int result = bId;
        if (beltParents[bId] != bId) {
            result = find_belts_parents(bId);
            beltParents[bId] = result;
        }

        if (box.prev != null) {
            tails[result].next = belts[result];
            belts[result].prev = tails[result];

            tails[result] = box.prev;
            tails[result].next = null;

            belts[result] = box;
            box.prev = null;
        }

        return result;
    }

    private static int find_belts_parents(int bId) {
        if (beltParents[bId] == bId) {
            return bId;
        }

        return beltParents[bId] = find_belts_parents(beltParents[bId]);
    }

    private static int remove_box(int rId) {
        if (allBoxes.containsKey(rId)) {
            allBoxes.remove(rId);
            return rId;
        } else {
            return -1;
        }
    }

    private static long unload_box(int w_max) {
        long sum = 0;

        for (int b_idx = 1; b_idx <= m; b_idx++) {
            Box box = belts[b_idx];

            while (true) {
                if (box == null) {
                    break;
                }
                if (allBoxes.containsKey(box.id)) {
                    belts[b_idx] = box;
                    box.prev = null;
                    break;
                }
                box = box.next;
            }

            if (box == null) {
                belts[b_idx] = null;
                tails[b_idx] = null;
                continue;
            }

            belts[b_idx] = box.next;
            if (belts[b_idx] != null) { // 2개 이상
                belts[b_idx].prev = null;

                if (box.weight <= w_max) {
                    remove_box(box.id);
                    sum += box.weight;
                } else {
                    tails[b_idx].next = box;

                    box.prev = tails[b_idx];
                    box.next = null;

                    tails[b_idx] = box;
                }
            } else { // 벨트에 박스 1개
                if (box.weight <= w_max) {
                    remove_box(box.id);
                    sum += box.weight;
                    tails[b_idx] = null;
                } else {
                    belts[b_idx] = box;
                }
            }
        }

        return sum;
    }

    private static void init(StringTokenizer st) {
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        belts = new Box[m + 1];
        tails = new Box[m + 1];
        allBoxes = new HashMap<>();
        beltParents = new int[m + 1];

        int b_idx = 0;
        for (int j = 1; j <= n; j++) {
            int id = Integer.parseInt(st.nextToken());
            Box box = new Box(id, 0, null, null);

            if (j % (n / m) == 1) {
                b_idx++;
                belts[b_idx] = box;
                beltParents[b_idx] = b_idx;
            } else {
                tails[b_idx].next = box;
                box.prev = tails[b_idx];
            }
            tails[b_idx] = box;
            box.beltId = b_idx;
            allBoxes.put(id, box);
        }

        b_idx = 0;
        Box box = belts[1];
        for (int j = 1; j <= n; j++) {
            int weight = Integer.parseInt(st.nextToken());

            if (j % (n / m) == 1) {
                b_idx++;
                box = belts[b_idx];
            } else {
                box = box.next;
            }
            box.weight = weight;
        }
    }

    static class Box {

        int id, weight, beltId;
        Box prev, next;


        public Box(int id, int weight, Box prev, Box next) {
            this.id = id;
            this.weight = weight;
            this.prev = prev;
            this.next = next;
        }

    }
}