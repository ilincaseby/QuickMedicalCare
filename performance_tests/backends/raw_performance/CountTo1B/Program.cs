using System;
using System.Diagnostics;

class CountTo1B
{
    static void Main()
    {
        int[] a = new int[3000];
        Stopwatch stopwatch = new Stopwatch();

        stopwatch.Start();

        for (int i = 0; i < 1000000000; i++)
        {
            int index = i % 3000;
            a[index] = a[0] + 1;
        }

        stopwatch.Stop();

        Console.WriteLine($"Execution time: {stopwatch.Elapsed.TotalSeconds:F8} seconds");
    }
}