import os

import pandas as pd
from scipy.stats import spearmanr

# 读取csv文件
file_path = 'E:/IDEA/maven-project/DeveloperContributionEvaluation/fastjson_result.csv'
df = pd.read_csv(file_path, encoding='gbk')


human_tagged_data = df.iloc[:, 1].tolist()
author_score = df.iloc[:, 2].tolist()
my_score_before = df.iloc[:, 7].tolist()
my_score_normal = df.iloc[:, 8].tolist()
loc = df.iloc[:, 15].tolist()
# human_tagged_data = df.iloc[:150, 1].tolist()
# author_score = df.iloc[:150, 2].tolist()
# my_score_before = df.iloc[:150, 7].tolist()
# my_score_normal = df.iloc[:150, 8].tolist()
# loc = df.iloc[:150, 15].tolist()
print(len(human_tagged_data))
print(human_tagged_data)
print(my_score_before)
print(my_score_normal)

# 计算Spearman相关性
spearman_corr, p_value = spearmanr(human_tagged_data, author_score)
print("作者的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data, my_score_before)
print("我标准化前的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data, my_score_normal)
print("我标准化后的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data,loc)
print("loc的Spearman相关系数:", spearman_corr)
print("p值:", p_value)


# # 指定文件夹路径
# folder_path = 'E:/Postgraduate_study/papers/ASE23ContributionMeasurement-main/ASE23ContributionMeasurement-main/RQ1/tagged/'
#
# # 获取文件夹下所有文件名
# file_names = os.listdir(folder_path)
#
# # 遍历文件名
# for file_name in file_names:
#     # 确保文件是以.csv结尾的
#     if file_name.endswith('.csv'):
#         print("\nData from file:", file_name)
#         # 构建完整的文件路径
#         file_path = os.path.join(folder_path, file_name)
#
#         # 读取 CSV 文件并指定编码为 GBK，同时告诉 Pandas 不将第一行作为列名
#         csv_data = pd.read_csv(file_path, encoding='gbk', header=None)
#
#         # 提取第二列数据并转换为列表
#         human = csv_data.iloc[:, 1].tolist()
#         cvalue = csv_data.iloc[:, 2].tolist()
#         loc = csv_data.iloc[:, 3].tolist()
#         # 计算Kendall's Tau相关系数
#         # tau, p_value = kendalltau(human, cvalue)
#         # print(f"Kendall's Tau相关系数: {tau}")
#         # print(f"p-value: {p_value}")
#
#         # 计算Spearman相关性
#         spearman_corr, p_value = spearmanr(human, cvalue)
#         print("CVALUE的Spearman相关系数:", spearman_corr)
#
#         # 计算Spearman相关性
#         spearman_corr, p_value = spearmanr(human, loc)
#         print("loc的Spearman相关系数:", spearman_corr)