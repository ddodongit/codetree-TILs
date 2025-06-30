import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");

		int N = Integer.parseInt(st.nextToken());
		Map map = new Map();

		for (int i = 0; i < N; ++i) {
			st = new StringTokenizer(br.readLine(), " ");
			String command = st.nextToken();

			int k, v;
			switch (command) {
				case "add":
					k = Integer.parseInt(st.nextToken());
					v = Integer.parseInt(st.nextToken());
					map.put(k, v);
					break;
				case "remove":
					k = Integer.parseInt(st.nextToken());
					map.remove(k);
					break;
				case "find":
					k = Integer.parseInt(st.nextToken());
					Integer key = map.get(k);
					System.out.println(key == null ? "None" : key);
					break;
				case "print_list":
					map.inorder();
					break;
			}
		}
	}
}

class Map {

	Node root = null;
	Node start = null;
	Node end = null;

	public Integer endKey() {
		return end == null ? null : end.key;
	}

	public Integer startKey() {
		return start == null ? null : start.key;
	}

	public Integer upperBound(Integer key) {
		Node now = root;
		Node candidate = null;

		while (now != null) {
			if (key <= now.key) {
				candidate = now;
				now = now.left;
			} else {
				now = now.right;
			}
		}
		if (candidate == null) {
			return null;
		}
		return candidate.key;
	}

	public void put(Integer key, Integer value) {
		if (root == null) {
			root = new Node(key, value, null, null);
			return;
		}

		Node now = root;
		while (true) {
			if (key < now.key) {
				if (now.left == null) {
					now.left = new Node(key, value, null, null);
					break;
				}
				now = now.left;
			} else if (key > now.key) {
				if (now.right == null) {
					now.right = new Node(key, value, null, null);
					break;
				}
				now = now.right;
			} else {
				now.value = value;
				break;
			}
		}
		refreshMinMax();
	}

	public Integer get(int key) {
		Node now = root;
		while (now != null) {
			if (key < now.key) {
				now = now.left;
			} else if (key > now.key) {
				now = now.right;
			} else {
				return now.value;
			}
		}
		return null;
	}

	private void refreshMinMax() {
		start = root;
		while (start != null && start.left != null) {
			start = start.left;
		}

		end = root;
		while (end != null && end.right != null) {
			end = end.right;
		}
	}

	public void remove(int key) {
		root = deleteRec(root, key);
		refreshMinMax();          // 삭제 후 최소/최대 다시 계산
	}

	private Node deleteRec(Node node, int key) {
		if (node == null) {
			return null;
		}

		if (key < node.key) {
			node.left = deleteRec(node.left, key);
		} else if (key > node.key) {
			node.right = deleteRec(node.right, key);
		} else {        // === node.key : 삭제 대상 발견
			/* ① 자식이 0개 또는 1개 */
			if (node.left == null) {
				return node.right;
			}
			if (node.right == null) {
				return node.left;
			}

			/* ② 자식이 2개 → 중위 후계자(오른쪽 서브트리 최소)로 교체 */
			Node succ = node.right;
			while (succ.left != null) {
				succ = succ.left;
			}

			node.key = succ.key;
			node.value = succ.value;
			node.right = deleteRec(node.right, succ.key); // 후계자 노드 제거
		}
		return node;
	}

	public void inorder() {
		if (root == null) {
			System.out.println("None");
			return;
		}
		inorder(root);
		System.out.println();
	}

	private void inorder(Node now) {
		if (now == null) {
			return;
		}
		inorder(now.left);
		System.out.print(now.value + " ");
		inorder(now.right);
	}

	static class Node {

		Integer key, value;
		Node left, right;

		public Node(Integer key, Integer value, Node left, Node right) {
			this.key = key;
			this.value = value;
			this.left = left;
			this.right = right;
		}
	}
}
