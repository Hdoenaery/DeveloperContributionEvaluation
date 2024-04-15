import math
import os
import re
import sys

from pydriller import Repository

java_keywords = [
    'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch', 'char',
    'class', 'const', 'continue', 'default', 'do', 'double', 'else', 'enum',
    'extends', 'final', 'finally', 'float', 'for', 'goto', 'if', 'implements',
    'import', 'instanceof', 'int', 'interface', 'long', 'native', 'new', 'package',
    'private', 'protected', 'public', 'return', 'short', 'static', 'strictfp',
    'super', 'switch', 'synchronized', 'this', 'throw', 'throws', 'transient',
    'try', 'void', 'volatile', 'while'
]


def is_java_keyword(string):
    return string in java_keywords


def getChangedMethods(repo_path, old_commit, new_commit):
    changed_methods = set()
    LOC = {}  # 代码行数
    CC = {}  # 圈复杂度
    Halstead_Volume = {}  # 代码容量
    PCom = {}  # 注释百分比

    for commit in Repository(repo_path, new_commit).traverse_commits():
        # print(commit.msg)
        for modified_file in commit.modified_files:
            if modified_file.new_path.endswith('.java'):
                for method in modified_file.changed_methods:
                    # print(method.name.split("::")[-1])
                    # 过滤误报内容，pydriller工具可能会把一个for循环识别为一个方法
                    if is_java_keyword(method.name.split("::")[-1]):
                        continue

                    # 将发生变化的方法名称添加到集合中
                    changed_methods.add(method)
                    # 获取方法行数和圈复杂度
                    LOC[method.long_name] = method.nloc
                    CC[method.long_name] = method.complexity

                    # 提取该方法内容
                    lines = modified_file.source_code.split('\n')
                    extracted_code = '\n'.join(
                        lines[method.start_line - 1:method.end_line])  # 将start_line和end_line之间的列表用换行符连接成一个字符串
                    # print(extracted_code)

                    # 计算该方法的代码容量
                    Halstead_Volume[method.long_name] = calculate_method_halstead_volume(extracted_code)

                    # 计算该方法的注释百分比
                    PCom[method.long_name] = calculate_percentage_of_comments(extracted_code)

                    # 将该方法内容保存到本地
                    file_path = 'DeveloperContributionEvaluation/methodsContent/' + old_commit[0:6] + \
                                '_to_' + new_commit[0:6] + '/' + str(method.name.replace("::", "_")) + '_new.java'
                    # 如果有方法重载，则会出现方法名重复，需要将方法参数也带上
                    if os.path.exists(file_path):
                        # 文件名中不允许出现的特殊字符
                        forbidden_chars = '/\\?*:|"<>'

                        # 删除不允许出现在文件名中的字符
                        file_path = 'DeveloperContributionEvaluation/methodsContent/' + old_commit[0:6] + \
                                    '_to_' + new_commit[0:6] + '/' + str(''.join(
                            c for c in method.long_name.replace("::", "_") if c not in forbidden_chars)) + '_new.java'

                    save_to_file(extracted_code, file_path)

    return changed_methods, LOC, CC, Halstead_Volume, PCom


# 计算代码注释百分比（若字符串内部出现"//"或"/**/"会被误识别为注释，但考虑到一般很少有这种情况）
def calculate_percentage_of_comments(file_content):
    lines = file_content.split('\n')
    total_lines = len([line for line in lines if line.strip() != ''])  # 忽略空行
    # comment_lines = len(re.findall(r'//.*?(?=\n)|/\*.*?\*/|//.*', file_content))  #多行注释无法识别
    comment_lines = 0
    flag = 0
    tmp = 0
    for line in lines:
        if flag == 0:
            if "//" in line:
                comment_lines += 1
            elif "/*" in line:
                flag = 1
                tmp += 1
        else:
            tmp += 1
            if "*/" in line:
                flag = 0
                comment_lines += tmp
                tmp = 0


    # print(total_lines)
    # print(comment_lines)
    return (comment_lines / total_lines) * 100 if total_lines > 0 else 0



def calculate_halstead_volume(unique_operator_count, unique_operand_count, total_operator_count, total_operand_count):
    vocabulary_size = unique_operator_count + unique_operand_count
    program_length = total_operator_count + total_operand_count
    volume = program_length * (math.log(vocabulary_size) / math.log(2))
    return volume


def extract_operators_and_operands(source_code):
    # 正则表达式模式来匹配Java代码中的操作符和操作数
    operator_pattern = r'(\+|\-|\*|\/|\%|\=|\==|\!=|\<|\>|\<=|\>=|\&\&|\|\||\!)'
    operand_pattern = r'(\w+)'
    operators = re.findall(operator_pattern, source_code)
    operands = re.findall(operand_pattern, source_code)
    return operators, operands

# 计算代码容量（计算不准确，有待完善）
def calculate_method_halstead_volume(source_code):
    operators, operands = extract_operators_and_operands(source_code)
    unique_operators = set(operators)
    unique_operands = set(operands)
    total_operator_count = len(operators)
    total_operand_count = len(operands)
    unique_operator_count = len(unique_operators)
    unique_operand_count = len(unique_operands)
    volume = calculate_halstead_volume(unique_operator_count, unique_operand_count, total_operator_count,
                                       total_operand_count)
    return volume


def save_to_file(code, file_path):
    # 获取文件夹路径
    folder_path = os.path.dirname(file_path)

    # 如果文件夹不存在则创建
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)

    with open(file_path, 'w') as file:
        file.write(code)


if __name__ == "__main__":
    # 从命令行参数中获取 Git 项目路径、旧提交和新提交
    repo_path, old_commit, new_commit = sys.argv[1], sys.argv[2], sys.argv[3]
    changedMethods, LOC, CC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)

    for method in changedMethods:
        print(method.name)
        print(LOC[method.long_name])
        print(CC[method.long_name])
        print(Halstead_Volume[method.long_name])
        print(PCom[method.long_name])

# repo_path = 'E:/Postgraduate_study/FlappyBird'
# old_commit = 'd7b64a56e687ca6e3a3b295342fe07f0fdcb87a2'
# new_commit = '5e5ba4bf131b5998c33474ebe34ac7e9d86187ad'
#
# changedMethods, LOC, CC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)
#
# for method in changedMethods:
#     print(method.long_name)
#     print(LOC[method.long_name])
#     print(CC[method.long_name])
#     print(Halstead_Volume[method.long_name])
#     print(PCom[method.long_name])


# java_code = """
# // 这是单行注释
# public class Example {
#     /*
#     这是
#     多行
#     注释
#     */
#     public static void main(String[] args) {
#         // 单行注释
#         System.out.println("Hello, World!"); // 行尾注释
#         // "123"
#     }
# }
# """
#
# print(calculate_percentage_of_comments(java_code))
