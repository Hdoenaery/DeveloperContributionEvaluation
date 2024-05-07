import re

from scipy import stats

# 假设有一组数据x
x = [43, 6]
x2 = [256.76, 288.85]
x3 = [1, 1]
x4 = [9, 9]
x5 = [11.11, 0.00001]

# 进行Box-Cox变换 convert_res是输出结果
convert_res1, _ = stats.boxcox(x)
convert_res2, _ = stats.boxcox(x2)
# convert_res3, _ = stats.boxcox(x3)
# convert_res4, _ = stats.boxcox(x4)
convert_res5, _ = stats.boxcox(x5)

print(convert_res1)
print(convert_res2)
# print(convert_res3)
# print(convert_res4)
print(convert_res5)

method_name = 'checkAutoType(String typeName, Class<?> expectClass, int features)'
method_pattern = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
        method_name) + r'\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*{'

print(method_pattern)
method_regex = re.compile(method_pattern)
line = 'public Class<?> checkAutoType(String typeName, Class<?> expectClass, int features) {'
if method_regex.search(line):
    print("yes")
else:
    print("no")

