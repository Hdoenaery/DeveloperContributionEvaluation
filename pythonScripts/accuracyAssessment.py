import os

import pandas as pd
from scipy.stats import spearmanr
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from scipy.stats import spearmanr

# 读取csv文件
project_name = 'fastjson'
file_path = 'E:/IDEA/maven-project/DeveloperContributionEvaluation/commons-ognl_result.csv'
# file_path = 'E:/Postgraduate_study/papers/ASE23ContributionMeasurement-main/ASE23ContributionMeasurement修改版' \
#              '/RQ1/tagged/commons-ognl_result_final_manifest_tagged_eloc.csv'
file_path2 = 'E:/Postgraduate_study/papers/ASE23ContributionMeasurement-main/ASE23ContributionMeasurement修改版' \
             '/RQ1/tagged/ELOC/commons-ognl_result_final_manifest_tagged_eloc.csv'
# df = pd.read_csv(file_path, encoding='gbk', header=None)
df = pd.read_csv(file_path, encoding='gbk')
df2 = pd.read_csv(file_path2, encoding='gbk')

st = 0
ed = 150
human_tagged_data = df.iloc[st:ed, 1].tolist()
author_score = df.iloc[st:ed, 2].tolist()
my_unstandardized_scores = df.iloc[st:ed, 3].tolist()
my_standardized_scores = df.iloc[st:ed, 4].tolist()
loc = df.iloc[st:ed, 12].tolist()
# loc = df.iloc[st:ed, 3].tolist()
# eloc = df.iloc[st:ed, 4].tolist()

cnt = 84
human_tagged_data2 = df2.iloc[:cnt, 1].tolist()
eloc2 = df2.iloc[:cnt, 4].tolist()

print(len(human_tagged_data))
print(human_tagged_data)

# 计算Spearman相关性
spearman_corr, p_value = spearmanr(human_tagged_data, author_score)
print("作者的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data, my_unstandardized_scores)
print("我标准化前的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data, my_standardized_scores)
print("我标准化后的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data,loc)
print("loc的Spearman相关系数:", spearman_corr)
print("p值:", p_value)

spearman_corr, p_value = spearmanr(human_tagged_data2,eloc2)
print("eloc的Spearman相关系数:", spearman_corr)
print("p值:", p_value)
# 创建数据框以计算多个变量之间的相关性
data = pd.DataFrame({
    'ground truth': human_tagged_data,
    'UnstandardizedScore' : my_unstandardized_scores,
    'StandardizedScore': my_standardized_scores,
    'LOC' : loc,
})

# 计算Spearman相关系数矩阵
corr_matrix = data.corr(method='spearman')
print(corr_matrix)

# 设置图形风格
sns.set(style='white')

# 创建热图
plt.figure(figsize=(10, 8))
heatmap = sns.heatmap(corr_matrix, annot=True, fmt='.4f', cmap='coolwarm', vmin=-1, vmax=1, linewidths=0.5, annot_kws={"size": 18})

# 添加图表标题
# plt.title(project_name, fontsize=28)

# 调整刻度标签的字体大小
plt.xticks(fontsize=11)
plt.yticks(fontsize=11)

# 显示图表
plt.show()



# # 设置图形风格
# sns.set(style='whitegrid')
#
# # 创建散点图
# plt.figure(figsize=(10, 6))
# sns.scatterplot(x=human_tagged_data, y=my_score_normal)
#
# # 添加图表标题和标签
# plt.title('Scatter Plot of Human Tagged Data vs My Score Normal')
# plt.xlabel('Human Tagged Data')
# plt.ylabel('My Score Normal')
#
# # 显示图表
# plt.show()
#
# # 创建带回归线的散点图
# plt.figure(figsize=(10, 6))
# sns.regplot(x=human_tagged_data, y=my_score_normal, scatter_kws={'s': 50}, line_kws={'color': 'red'})
#
# # 添加图表标题和标签
# plt.title('Scatter Plot with Regression Line: Human Tagged Data vs My Score Normal')
# plt.xlabel('Human Tagged Data')
# plt.ylabel('My Score Normal')
#
# # 显示图表
# plt.show()




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