import time

a = [0] * 3000


start_time = time.perf_counter()

for i in range(1000000000):
    index = i % 3000
    a[index] = a[0] + 1

end_time = time.perf_counter()

print(f"Execution time: {end_time - start_time:.8f} seconds")