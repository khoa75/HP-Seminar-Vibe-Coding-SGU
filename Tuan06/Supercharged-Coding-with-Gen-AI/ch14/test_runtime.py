import time
from fibonacci import fibonacci_recursive

print("Runtime for fibonacci_recursive(n=10:41:5):\n")
for n in range(10, 42, 5):
    start_time = time.time()
    fibonacci_recursive(n)
    end_time = time.time()
    print(f"fibonacci_recursive({n}): {end_time - start_time:.6f} seconds")
