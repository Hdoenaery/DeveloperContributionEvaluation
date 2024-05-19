import pandas as pd
from scipy.stats import spearmanr

# 读取csv文件
file_path = 'E:/IDEA/maven-project/DeveloperContributionEvaluation/httpcomponents-client_result.csv'
# csv_data = pd.read_csv(file_path, encoding='gbk', header=None)
csv_data = pd.read_csv(file_path, encoding='gbk')

commitHash = csv_data.iloc[:, 0].tolist()
print(commitHash)
for i in commitHash:
    print(f"\"{i}\",")