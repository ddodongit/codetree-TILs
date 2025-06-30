import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int m = sc.nextInt();
		int[] arr = new int[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextInt();
		}
		for (int i = 0; i < m; i++) {
			int x = sc.nextInt();
			int bs = bs(arr, x);
			System.out.println(bs + 1);
		}
	}

	private static int bs(int[] arr, int x) {
		int i = 0;
		int j = arr.length;
		while (i < j) {
			int m = (i + j) / 2;
			if (arr[m] < x) {
				i = m + 1;
			} else if (arr[m] > x) {
				j = m;
			} else {
				return m;
			}
		}
		return -2;
	}
}