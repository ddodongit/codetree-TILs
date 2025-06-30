
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int[] arr = new int[n];
		for (int i = 0; i < n; i++) {
			arr[i] = sc.nextInt();
		}

		sort(arr, new int[arr.length], 0, arr.length - 1);

		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
        for (int i = arr.length-1; i >=0; --i) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();

	}

	private static void sort(int[] arr, int[] tmp, int start, int end) {
		if (start >= end) {
			return;
		}

		int mid = (start + end) / 2;
		sort(arr, tmp, start, mid);
		sort(arr, tmp, mid + 1, end);
		merge(arr, tmp, start, mid, end);
	}

	private static void merge(int[] arr, int[] tmp, int start, int mid, int end) {
		for (int i = start; i <= end; ++i) {
			tmp[i] = arr[i];
		}

		int i = start;
		int j = mid + 1;
		int x = start;
		while (i <= mid && j <= end) {
			if (tmp[i] < tmp[j]) {
				arr[x] = tmp[i];
				++i;
			} else {
				arr[x] = tmp[j];
				++j;
			}
			++x;
		}

		while (i <= mid) {
			arr[x] = tmp[i];
			++i;
			++x;
		}

		while (j <= end) {
			arr[x] = tmp[j];
			++j;
			++x;
		}

	}

}
