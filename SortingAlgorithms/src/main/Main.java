package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Main {

  public static final int SM = 10000;
  public static final int MED = 100000;
  public static final int LG = 1000000;

  public static void main(String[] args) {
    //Add all file names to ArrayList and use foreach to go through them
    ArrayList<String> filePrefixes = new ArrayList<>();
    filePrefixes.add("sorted_LG_DEC_");
    filePrefixes.add("sorted_LG_INC_");
    filePrefixes.add("unsorted_LG_");
    filePrefixes.add("sorted_MED_DEC_");
    filePrefixes.add("sorted_MED_INC_");
    filePrefixes.add("unsorted_MED_");
    filePrefixes.add("sorted_SM_DEC_");
    filePrefixes.add("sorted_SM_INC_");
    filePrefixes.add("unsorted_SM_");
    
    int[] h;
    int[] m;
    int[] q;

    //will hold run times in the order they are executed
    ArrayList<Long> runTimes = new ArrayList<>();
    int lineCount = 1;
    FileWriter w = null;
    try {
      w = new FileWriter("results");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    for (String s : filePrefixes) {
      long startTime, endTime, elapsedTime;
      int parsed;
      try {
        w.write(s + "\n");
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      //initialize array for the correct size of the file
      if (s.contains("SM")) {
        h = new int[SM];
        m = new int[SM];
        q = new int[SM];
      } else if (s.contains("MED")) {
        h = new int[MED];
        m = new int[MED];
        q = new int[MED];
      } else {
        h = new int[LG];
        m = new int[LG];
        q = new int[LG];
      }

      for (int i = 1; i <= 2; i++) {
        File fin = new File(s + i + ".txt");

        FileInputStream fis;
        try {
          fis = new FileInputStream(fin);

          BufferedReader in = new BufferedReader(new InputStreamReader(fis));
          String aLine;
          int j = 0;
          while ((aLine = in.readLine()) != null) {
            parsed = Integer.parseInt(aLine);
            //since algorithms work within the given data structure create 3 arrays
            //to run each algorithm on
            h[j] = parsed;
            m[j] = parsed;
            q[j] = parsed;
            j++;
          }
          startTime = System.currentTimeMillis();
          heapsort(h, h.length);
          endTime = System.currentTimeMillis();
          elapsedTime = endTime - startTime;
          runTimes.add(elapsedTime);    //will be written to the file
          System.out.println("HeapSort " + i + " " + elapsedTime);  //console output to keep track of run progress

          startTime = System.currentTimeMillis();
          quicksort(q, 0, q.length);
          endTime = System.currentTimeMillis();
          elapsedTime = endTime - startTime;
          runTimes.add(elapsedTime);
          System.out.println("Quicksort " + i + " " + elapsedTime);

          startTime = System.currentTimeMillis();
          mergesort(m);
          endTime = System.currentTimeMillis();
          elapsedTime = endTime - startTime;
          runTimes.add(elapsedTime);
          System.out.println("Mergesort " + i + " " + elapsedTime);

        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (NumberFormatException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }
    try {
      //Write the run times to the file
      //run times will be in the order heapsort, quicksort, mergesort on the same line
      //new line is new file
      for (long l : runTimes) {

        if (lineCount % 3 == 0)
          w.write(l + "\n");
        else
          w.write(l + ", ");

        lineCount++;
      }
      w.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("DONE");
  }

  /**
   * Splits arrays in half
   * @param a array to sort
   * @return sorted array
   */
  public static int[] mergesort(int[] a) {
    int n = a.length;
    if (n > 1) {
      int floor = Math.floorDiv(n, 2);
      int ceiling = (int) Math.ceil(n / 2);
      int[] b = new int[floor];
      int[] c = new int[ceiling];
      System.arraycopy(a, 0, b, 0, floor);
      System.arraycopy(a, floor, c, 0, ceiling);
      b = mergesort(b);
      c = mergesort(c);
      merge(b, c, a);
    }

    return a;
  }

  /**
   * merge arrays back together
   * @param b first half of array a
   * @param c second half of array a
   * @param a merged sorted
   * @return
   */
  public static int[] merge(int[] b, int[] c, int[] a) {
    int i = 0, j = 0, k = 0;
    int bSize = b.length;
    int cSize = c.length;
    while (i < bSize && j < cSize) {
      if (b[i] < c[j]) {
        a[k] = b[i];
        i++;
      } else {
        a[k] = c[j];
        j++;
      }
      k++;
      if (i == bSize) {
        System.arraycopy(c, j, a, k, cSize - j);
      } else {
        System.arraycopy(b, i, a, k, bSize - i);
      }
    }
    return a;
  }
/**
 * sort array using quicksort median of three
 * @param a array to be sorted
 * @param lower lower limit of what needs to be sorted
 * @param upper upper limit of what needs to be sorted
 */
  public static void quicksort(int[] a, int lower, int upper) {
    if (upper <= lower) {
      return;
    }
    int i = lower;
    int j = lower;
    int k = upper;
    int pivot;
    if (upper - lower > 100) {
      pivot = randomPivot(a, lower, upper);
    } else {
      pivot = a[lower];
    }
    while (j < k) {
      if (a[j] < pivot) {
        swap(a, i, j);
        i++;
        j++;
      } else if (a[j] > pivot) {
        k--;
        swap(a, j, k);
      } else {
        j++;
      }
    }
    quicksort(a, lower, i); //sorts values lower than pivot
    quicksort(a, j, upper); // sorts values higher than pivot
    //i - j should all be equal to the pivot
  }
  
  /**
   * generate a median of three pivot value between the values that still need to be sorted
   * @param a array that is being sorted
   * @param lower
   * @param upper
   * @return
   */
  public static int randomPivot(int[] a, int lower, int upper) {
    Random r = new Random();
    int first = r.nextInt(upper - lower) + lower;
    int second = r.nextInt(upper - lower) + lower;
    int third = r.nextInt(upper - lower) + lower;
    return (a[first] + a[second] + a[third]) / 3;
  }

  /**
   * builds heap then runs max key deletion until array is sorted
   * @param a
   * @param n size of a
   */
  public static void heapsort(int[] a, int n) {
    buildHeap(a);
    while (n > 0) {
      n = maxKeyDelete(a, n);
    }

  }
  
  /**
   * heapifys data until the heap property holds
   * @param a
   * @param n the end of what still needs to be sorted
   * @param i the root of what still needs to be heapified
   */
  public static void heapify(int[] a, int n, int i) {
    int leftIndex = 2 * i;
    int rightIndex = 2 * i + 1;
    int largestIndex = i;
    if (rightIndex >= n)
      return;
    if (leftIndex <= n && a[leftIndex] > a[largestIndex]) {
      largestIndex = leftIndex;
    }

    if (rightIndex <= n && a[rightIndex] > a[largestIndex]) {
      largestIndex = rightIndex;
    }

    if (largestIndex != i) {
      swap(a, i, largestIndex);
      heapify(a, n, largestIndex);
    }
  }

  /**
   * Max key delete but instead of deleting the value just puts it to the end of the array
   * and continues with the first n-1 terms
   * @param a
   * @param n
   * @return
   */
  public static int maxKeyDelete(int[] a, int n) {
    swap(a, 0, n - 1);
    n--;
    heapify(a, n, 0);
    return n;
  }

  /**
   * initial building of the heap
   * @param a
   */
  public static void buildHeap(int[] a) {

    for (int i = Math.floorDiv(a.length, 2); i >= 0; i--) {
      heapify(a, a.length, i);
    }
  }
  /**
   * swap utility function
   * @param ar
   * @param first
   * @param second
   */
  public static void swap(int[] ar, int first, int second) {
    int temp = ar[first];
    ar[first] = ar[second];
    ar[second] = temp;
  }

  /**
   * print array utility function
   * @param arr
   */
  static void printArray(int arr[]) {
    int n = arr.length;
    for (int i = 0; i < n; i++)
      System.out.print(arr[i] + " ");
    System.out.println();
  }

}
