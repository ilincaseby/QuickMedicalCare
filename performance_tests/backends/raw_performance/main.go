package main

import (
	"fmt"
	"time"
)

func main() {
	var a [3000]int

	start := time.Now()
	for i := range 1000000000 {
		index := i % 3000
		a[index] = a[0] + 1
	}
	end := time.Since(start).Seconds()
	fmt.Printf("Execution time: %.8f seconds\n", end)
}
