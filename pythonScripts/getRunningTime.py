import re
import pandas as pd

# 定义提取时间的正则表达式
get_call_graph_time_pattern = re.compile(r"本次getCallGraph计算的运行时间（秒）：(\d+\.\d+)")
get_ddg_time_pattern = re.compile(r"本次getDDG计算的运行时间（秒）：(\d+\.\d+)")
get_cdg_time_pattern = re.compile(r"本次getCDG计算的运行时间（秒）：(\d+\.\d+)")
commit_time_pattern = re.compile(r"本次commit计算的运行时间（秒）：(\d+\.\d+)")

# 读取文件内容
with open('../output_guice_1-124(生成文件).log', 'r', encoding='utf-8') as file:
    text = file.read()

# 使用正则表达式提取数据
get_call_graph_times = get_call_graph_time_pattern.findall(text)
get_ddg_times = get_ddg_time_pattern.findall(text)
get_cdg_times = get_cdg_time_pattern.findall(text)
commit_times = commit_time_pattern.findall(text)

# 将数据放入DataFrame
data = {
    "commit_time": commit_times,
    "getCallGraph_time": get_call_graph_times,
    "getDDG_time": get_ddg_times,
    "getCDG_time": get_cdg_times,
}

df = pd.DataFrame(data)

# 将DataFrame写入Excel文件
df.to_excel("timing_data.xlsx", index=False)

print("数据已成功提取并写入timing_data.xlsx文件中")
