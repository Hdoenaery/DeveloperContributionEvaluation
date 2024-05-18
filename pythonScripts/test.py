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

method_name = 'copyDistributionsIntoScmDirectoryStructureAndAddToSvn'.replace(",", ", ")
method_pattern = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
        method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*\{?'
method_pattern2 = r'(?:=\s*|\s+)new\s*' + re.escape(
        method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*\{?'
method_pattern3 = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
        method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\).*'
method_pattern4 = r'\b\s+.*?' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*\{'
method_pattern5 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(.*\)\s*\w+'   #单行不带大括号的方法匹配
method_pattern6 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(.*\)(?!\s*;)' #匹配带完整的左右括号()且不以分号;结尾
method_pattern7 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(\s*(?!\S)'    #匹配左括号(后不包含任何除空格以外的字符
#匹配左括号(，后如果有非空白字符，则匹配到逗号结束并忽略其后的空白字符，如果没有非空白字符，只匹配空白字符且不允许后面有非空白字符。
method_pattern77 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(\s*(\S+.*?,\s*|\s*(?!\S))$'
method_pattern8 = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
        method_name).replace(r'\ ', r'\s*') + r'\(\s*(\S+.*?,\s*|\s*(?!\S))$'

method_regex = re.compile(method_pattern8)
# line1 = 'protected Class<?> resolveClass(ObjectStreamClass desc) {'
# line2 = 'protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {'
# line3 = 'protected Class<?> resolveClass(ObjectStreamClass desc)'
# line4 = 'super.resolveClass(desc);'
# line5 = 'protected Class<?> resolveClass(ObjectStreamClass desc, String[] a = {"123", "456"})'
line6 = ' private List<File> copyDistributionsIntoScmDirectoryStructureAndAddToSvn(final File copiedReleaseNotes,'
line7 = ' private List<File> copyDistributionsIntoScmDirectoryStructureAndAddToSvn('
line8 = ' private List<File> copyDistributionsIntoScmDirectoryStructureAndAddToSvn(final File copiedReleaseNotes,  a;'

if method_regex.search(line6):
    print("yes")
else:
    print("no")
if method_regex.search(line7):
    print("yes")
else:
    print("no")
if method_regex.search(line8):
    print("yes")
else:
    print("no")
# if method_regex.search(line1):
#     print("yes")
# else:
#     print("no")
# if method_regex.search(line2):
#     print("yes")
# else:
#     print("no")
# if method_regex.search(line3):
#     print("yes")
# else:
#     print("no")
# if method_regex.search(line4):
#     print("yes")
# else:
#     print("no")
# if method_regex.search(line5):
#     print("yes")
# else:
#     print("no")