import sys
import numpy as np

# 读取数据
data = []
for line in sys.stdin:
    data.append(float(line.strip()))

# Z-score标准化
mean = np.mean(data)
std_dev = np.std(data)
data_zscore = (data - mean) / std_dev

# 缩放和平移
data_scaled_shifted = (data_zscore * (1/3)) + 1

# 输出结果
for value in data_scaled_shifted:
    print(value)
