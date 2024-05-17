import numpy as np

# 六组数据
HV = np.array([281.7628977173992, 269.2118756352258])
CC = np.array([1, 1])
LOC = np.array([1, 1])
PCOM = np.array([11.11111111, 0])
weight = np.array([0, 6.692768472737069])
ast = np.array([0.2, 0.02])

# 计算均值和标准差
HV_mean, HV_std = np.mean(HV), np.std(HV)
# CC_mean, CC_std = np.mean(CC), np.std(CC)
# LOC_mean, LOC_std = np.mean(LOC), np.std(LOC)
PCOM_mean, PCOM_std = np.mean(PCOM), np.std(PCOM)
weight_mean, weight_std = np.mean(weight), np.std(weight)
ast_mean, ast_std = np.mean(ast), np.std(ast)

# Z-score标准化
HV_zscore = (HV - HV_mean) / HV_std
# CC_zscore = (CC - CC_mean) / CC_std
# LOC_zscore = (LOC - LOC_mean) / LOC_std
PCOM_zscore = (PCOM - PCOM_mean) / PCOM_std
weight_zscore = (weight - weight_mean) / weight_std
ast_zscore = (ast - ast_mean) / ast_std

print("HV Z-score标准化后：", HV_zscore)
# print("CC Z-score标准化后：", CC_zscore)
# print("LOC Z-score标准化后：", LOC_zscore)
print("PCOM Z-score标准化后：", PCOM_zscore)
print("weight Z-score标准化后：", weight_zscore)
print("ast Z-score标准化后：", ast_zscore)


# 缩放和平移
HV_scaled_shifted = (HV_zscore * (1/3)) + 1
# CC_scaled_shifted = (CC_zscore * (1/3)) + 1
# LOC_scaled_shifted = (LOC_zscore * (1/3)) + 1
PCOM_scaled_shifted = (PCOM_zscore * (1/3)) + 1
weight_scaled_shifted = (weight_zscore * (1/3)) + 1
ast_scaled_shifted = (ast_zscore * (1/3)) + 1

print("HV 缩放和平移后：", HV_scaled_shifted)
# print("CC 缩放和平移后：", CC_scaled_shifted)
# print("LOC 缩放和平移后：", LOC_scaled_shifted)
print("PCOM 缩放和平移后：", PCOM_scaled_shifted)
print("weight 缩放和平移后：", weight_scaled_shifted)
print("ast 缩放和平移后：", ast_scaled_shifted)

CM = (HV_scaled_shifted + CC + LOC - PCOM_scaled_shifted)/2 + 1
print(CM)

IR = ast_std * CM * weight_scaled_shifted
print(IR)