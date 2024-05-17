import math
import sys
import json

import numpy as np
import pandas as pd


# 定义标准化函数，对数据进行Z-score标准化，转化为标准正态分布，之后调整分布使得均值为1标准差为1/3，这样能保证99.7%的数据落在[0,2]之间
def normalize(data):
    mean = np.mean(data)
    std_dev = np.std(data)

    if std_dev == 0:
        data_normal = [1 for _ in range(len(data))]
    else:
        data_normal = (data - mean) / std_dev
        data_normal = (data_normal * (1 / 3)) + 1
    return data_normal


print("----------------------------------------------------------------------------------------------")
print("IN python!!!!")
# 从标准输入读取 JSON 字符串
json_data = sys.stdin.readline()
# print(json_data)
# 将 JSON 字符串反序列化为 Python 对象
commit_score_list = json.loads(json_data)

# print(commit_score_list)

# 将 commit_score_list 转换为格式化后的JSON的字符串
# json_output = json.dumps(commit_score_list, indent=4)  # indent=4 表示输出格式化后的 JSON 字符串
# print(json_output)

ast_score = []
LOC = []
CC = []
HV = []
PCOM = []
weight = []
ddg_impact = []
cdg_impact = []

for commit_score in commit_score_list:
    # # 获取 commitHash
    # commit_hash = commit_score["commitHash"]
    # print("Commit Hash:", commit_hash)

    # 获取 methodScore 列表
    method_scores = commit_score["methodScore"]

    # 遍历 methodScore 列表
    for method_score in method_scores:
        # 获取 astScore
        ast_score.append(method_score["astScore"])
        # print("AST Score:", method_score["astScore"])

        # 获取 HV
        HV.append(method_score["HV"])
        # print("HV:", method_score["HV"])

        # 获取 PCOM
        PCOM.append(method_score["PCOM"])
        # print("PCOM:", method_score["PCOM"])

        # 获取 CC
        CC.append(method_score["CC"])
        # print("CC:", method_score["CC"])

        # 获取 LOC
        LOC.append(method_score["LOC"])
        # print("LOC:", method_score["LOC"])

        # 获取 weight
        weight.append(method_score["weight"])
        # print("Weight:", method_score["weight"])

        # 获取 DDG_impact
        ddg_impact.append(method_score["DDG_impact"])
        # print("DDG Impact:", method_score["DDG_impact"])

        # 获取 CDG_impact
        cdg_impact.append(method_score["CDG_impact"])
        # print("CDG Impact:", method_score["CDG_impact"])

# print(ast_score)
# print(LOC)
# print(CC)
# print(HV)
# print(PCOM)
# print(weight)
# print(ddg_impact)
# print(cdg_impact)

# 对每个指标进行Z-score标准化
ast_score_normal = normalize(ast_score)
LOC_normal = normalize(LOC)
CC_normal = normalize(CC)
HV_normal = normalize(HV)
PCOM_normal = normalize(PCOM)
weight_normal = normalize(weight)
ddg_impact_normal = normalize(ddg_impact)
cdg_impact_normal = normalize(cdg_impact)

# print(f"ast_score_normal = {ast_score_normal}")
# print(f"LOC = {LOC_normal}")
# print(f"CC = {CC_normal}")
# print(f"HV = {HV_normal}")
# print(f"CDG = {cdg_impact_normal}")
# print(f"mean = {np.mean(ast_score_normal)} ,  std_dev = {np.std(ast_score_normal)}")

score_of_commit_before = []
score_of_commit_after = []
for commit_score in commit_score_list:
    # 获取 methodScore 列表
    method_scores = commit_score["methodScore"]

    # 遍历 methodScore 列表
    for method_score in method_scores:
        # print(method_score["methodName"])
        # print(method_score["index"])
        index = method_score["index"]
        method_score["astScore_normal"] = ast_score_normal[index]
        method_score["HV_normal"] = HV_normal[index]
        method_score["PCOM_normal"] = PCOM_normal[index]
        method_score["CC_normal"] = CC_normal[index]
        method_score["LOC_normal"] = LOC_normal[index]
        method_score["weight_normal"] = weight_normal[index]
        method_score["DDG_impact_normal"] = ddg_impact_normal[index]
        method_score["CDG_impact_normal"] = cdg_impact_normal[index]

        method_score["CM"] = (method_score["HV"] + method_score["LOC"] + method_score[
            "CC"] - method_score["PCOM"]) / 2 + 1
        method_score["IR"] = math.sqrt(method_score["DDG_impact"]) + math.sqrt(
            method_score["CDG_impact"]) + 1
        method_score["scoreBeforeNormalize"] = method_score["astScore"] * method_score["CM"] * (
                method_score["weight"] + 1) * method_score["IR"]
        commit_score["scoreOfCommitBefore"] += method_score["scoreBeforeNormalize"]
        # --------------------------------------------------------------------------------------------------
        method_score["CM_normal"] = (method_score["HV_normal"] + method_score["LOC_normal"] + method_score[
            "CC_normal"] - method_score["PCOM_normal"]) / 2 + 1
        method_score["IR_normal"] = math.sqrt(method_score["DDG_impact_normal"]) + math.sqrt(
            method_score["CDG_impact_normal"]) + 1
        method_score["scoreAfterNormalize"] = method_score["astScore_normal"] * method_score["CM_normal"] * (
                method_score["weight_normal"] + 1) * method_score["IR_normal"]
        commit_score["scoreOfCommitAfter"] += method_score["scoreAfterNormalize"]


    # 获取 commitHash
    print("Commit Hash:", commit_score["commitHash"], end=' ')
    print(f"scoreOfCommitBefore = {commit_score['scoreOfCommitBefore']}", end=' ')
    print(f"scoreOfCommitAfter = {commit_score['scoreOfCommitAfter']}")
    score_of_commit_before.append(commit_score['scoreOfCommitBefore'])
    score_of_commit_after.append(commit_score['scoreOfCommitAfter'])

print(score_of_commit_before)
print(score_of_commit_after)
df1 = pd.DataFrame(score_of_commit_before, columns=['score_of_commit_before'])
df2 = pd.DataFrame(score_of_commit_after, columns=['score_of_commit_after'])
# 合并两个 DataFrame
df = pd.concat([df1, df2], axis=1)
# 将 DataFrame 存入 Excel 文件
df.to_excel('myscore.xlsx', index=False)

# # 将 commit_score_list 转换为格式化后的JSON的字符串
# json_output = json.dumps(commit_score_list, indent=4)  # indent=4 表示输出格式化后的 JSON 字符串
# print(json_output)
