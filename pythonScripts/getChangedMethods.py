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

def find_method_interval(lines, method_name):
    method_name = method_name.replace(",", ", ")
    # print(method_name)
    """
    在 Java 代码中查找方法的起始行和结束行。
    """
    # print(f"in find_method_interval   :{method_name}")
    # 方法定义的正则表达式模式
    # r'(?:\{.*|(?!{)(?!;))'表示当前位置后面若为{，则匹配任意字符，若不为{，则也不能为分号;
    method_pattern = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp).*\s' + re.escape(
        method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*' + r'(?:\{.*|(?!{)(?!;))'

    method_regex = re.compile(method_pattern)

    # lines = extracted_code.split('\n')
    start_line = None
    end_line = None
    in_method = False
    bracket_cnt = 0
    for i, line in enumerate(lines):
        # 搜索方法定义
        if method_regex.search(line):
            start_line = i + 1
            in_method = True
            # print(f"start_line = {start_line}")
        if in_method:
            # 处理方法内部的代码行
            if '{' in line:
                # 增加括号计数器
                bracket_cnt += line.count('{')
            if '}' in line:
                # 减少括号计数器
                bracket_cnt -= line.count('}')
                if bracket_cnt > 0:
                    # 如果计数器仍大于0，继续搜索方法的结束
                    continue
                else:
                    # 如果计数器为0，表示找到了方法的结束
                    end_line = i + 1
                    break

    if start_line == None:
        method_pattern2 = r'(?:=\s*|\s+)new\s+' + re.escape(
            method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*' + r'(?:\{.*|(?!{)(?!;))'
        # method_pattern3 = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
        #     method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\).*'
        method_pattern4 = r'\b\s+.*?' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\s*\([^)]*\)\s*(?:throws\s+\w+(?:,\s*\w+)*)?\s*\{'
        # 匹配左括号(，后如果有非空白字符，则匹配到逗号结束并忽略其后的空白字符，如果没有非空白字符，只匹配空白字符且不允许后面有非空白字符。
        method_pattern8 = r'\b(?:public|protected|private|static|final|synchronized|abstract|native|strictfp)\s+.*?' + re.escape(
            method_name).replace(r'\ ', r'\s*') + r'\(\s*(\S+.*?,\s*|\s*(?!\S))$'
        method_regex2 = re.compile(method_pattern2)
        # method_regex3 = re.compile(method_pattern3)
        method_regex4 = re.compile(method_pattern4)
        method_regex8 = re.compile(method_pattern8)
        for i, line in enumerate(lines):
            # 搜索方法定义
            if method_regex2.search(line) or method_regex4.search(line) or method_regex8.search(line):
                start_line = i + 1
                in_method = True
                # print(f"start_line = {start_line}")
            if in_method:
                # 处理方法内部的代码行
                if '{' in line:
                    # 增加括号计数器
                    bracket_cnt += line.count('{')
                if '}' in line:
                    # 减少括号计数器
                    bracket_cnt -= line.count('}')
                    if bracket_cnt > 0:
                        # 如果计数器仍大于0，继续搜索方法的结束
                        continue
                    elif bracket_cnt == 0:
                        # 如果计数器为0，表示找到了方法的结束
                        end_line = i + 1
                        break
                    else:
                        in_method = False
                        bracket_cnt = 0

    if start_line == None:
        # 用于匹配"Class<?> serializer() default Void.class;"或"Class<?> serializer() {default Void.class;}"
        method_pattern5 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(.*\)\s*\w+'   #单行不带大括号的方法匹配
        method_pattern6 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(.*\)(?!\s*;)' #匹配带完整的左右括号()且不以分号;结尾
        method_pattern7 = r'.*\s+' + re.escape(method_name).replace(r'\ ', r'\s*') + r'\(\s*(?!\S)'    #匹配左括号(后不包含任何除空格以外的字符
        method_regex5 = re.compile(method_pattern5)
        method_regex6 = re.compile(method_pattern6)
        method_regex7 = re.compile(method_pattern7)
        for i, line in enumerate(lines):
            # 搜索方法定义
            if method_regex5.search(line):
                start_line = i + 1
                end_line = i + 1
                break

            if method_regex6.search(line) or method_regex7.search(line):
                start_line = i + 1
                in_method = True
                # print(f"start_line = {start_line}")
            if in_method:
                # 处理方法内部的代码行
                if '{' in line:
                    # 增加括号计数器
                    bracket_cnt += line.count('{')
                if '}' in line:
                    # 减少括号计数器
                    bracket_cnt -= line.count('}')
                    if bracket_cnt > 0:
                        # 如果计数器仍大于0，继续搜索方法的结束
                        continue
                    elif bracket_cnt == 0:
                        # 如果计数器为0，表示找到了方法的结束
                        end_line = i + 1
                        break
                    else:
                        in_method = False
                        bracket_cnt = 0

    # print(f"start_line = {start_line}, end_line = {end_line}")
    return start_line, end_line

def getChangedMethods(repo_path, old_commit, new_commit):
    changed_methods = set()
    LOC = {}  # 代码行数
    # CC = {}  # 圈复杂度
    Halstead_Volume = {}  # 代码容量
    PCom = {}  # 注释百分比

    for commit in Repository(repo_path, new_commit).traverse_commits():
        # print(commit.msg)
        for modified_file in commit.modified_files:
            if modified_file.new_path == None:
                continue
            if modified_file.new_path.endswith('.java'):
                # print(modified_file.new_path)
                for method in modified_file.changed_methods:
                    # print(method.name)
                    # print(method.parameters)

                    # 过滤误报内容，pydriller工具可能会把一个for循环识别为一个方法
                    if is_java_keyword(method.name.split("::")[-1]):
                        continue

                    # 将发生变化的方法名称添加到集合中
                    changed_methods.add(method)
                    # # 获取方法行数和圈复杂度
                    # LOC[method.long_name] = method.nloc
                    # CC[method.long_name] = method.complexity

                    # 提取该方法内容
                    old_lines = ""
                    if modified_file.source_code_before != None:
                        old_lines = modified_file.source_code_before.split('\n')

                    new_lines = ""
                    if modified_file.source_code != None:
                        new_lines = modified_file.source_code.split('\n')
                    start_line, end_line = find_method_interval(old_lines, method.name.split("::")[-1])
                    # print(f"start = {start_line} , end = {end_line}");
                    extracted_code = ""
                    if start_line == None or end_line == None:
                        start_line, end_line = find_method_interval(new_lines, method.name.split("::")[-1])
                        extracted_code = '\n'.join(
                            new_lines[start_line - 1:end_line])  # 将start_line和end_line之间的列表用换行符连接成一个字符串
                    else:
                        extracted_code = '\n'.join(
                            old_lines[start_line - 1:end_line])  # 将start_line和end_line之间的列表用换行符连接成一个字符串



                    # print(extracted_code)
                    # 获取方法行数和圈复杂度
                    LOC[method.long_name] = calculate_method_loc(extracted_code)
                    # CC[method.long_name] = method.complexity

                    # 计算该方法的代码容量
                    Halstead_Volume[method.long_name] = calculate_method_halstead_volume(extracted_code)

                    # 计算该方法的注释百分比
                    PCom[method.long_name] = calculate_percentage_of_comments(extracted_code)

                    # 将该方法内容保存到本地
                    file_path = 'DeveloperContributionEvaluation/changedMethodsContent/' + old_commit[0:7] + \
                                '_to_' + new_commit[0:7] + '/' + str(method.name.replace("::", "#").replace("<", "@lt@").replace(">", "@gt@")).replace("?", "@ques@") + '_new.java'
                    # # 如果有方法重载，则会出现方法名重复，需要将方法参数也带上
                    # if os.path.exists(file_path):
                    #     # 文件名中不允许出现的特殊字符
                    #     forbidden_chars = '/\\?*:|"<>'
                    # 
                    #     # 删除不允许出现在文件名中的字符
                    #     file_path = 'DeveloperContributionEvaluation/changedMethodsContent/' + old_commit[0:7] + \
                    #                 '_to_' + new_commit[0:7] + '/' + str(''.join(
                    #         c for c in method.long_name.replace("::", "#") if c not in forbidden_chars)) + '_new.java'

                    save_to_file(extracted_code, file_path)

    # return changed_methods, LOC, CC, Halstead_Volume, PCom
    return changed_methods, LOC, Halstead_Volume, PCom

# 计算代码行数
def calculate_method_loc(file_content):
    loc = 0;
    lines = file_content.split("\n")
    for line in lines:
        # print(line)
        if(line.strip() == ""):
            continue
        loc += 1
    return max(loc-2, 1)

# 计算代码注释百分比（若字符串内部出现"//"或"/**/"会被误识别为注释，但考虑到一般很少有这种情况）
def calculate_percentage_of_comments(file_content):
    lines = file_content.split('\n')
    lines = lines[1:-1]
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
    # print(f"vocabulary_size = {vocabulary_size}, program_length = {program_length}")
    if vocabulary_size == 0:
        vocabulary_size = 1
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
    # print(file_path)
    # 获取文件夹路径
    folder_path = os.path.dirname(file_path)

    # 如果文件夹不存在则创建
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)

    # 由于windows系统不区分文件名大小写，因此文件名仅大小写不同，则会产生覆盖问题
    if os.path.exists(file_path):
        file_path = file_path[:-9] + "@Duplicate@_new.java"

    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(code)


if __name__ == "__main__":
    # 从命令行参数中获取 Git 项目路径、旧提交和新提交
    repo_path, old_commit, new_commit = sys.argv[1], sys.argv[2], sys.argv[3]
    # changedMethods, LOC, CC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)
    changedMethods, LOC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)

    for method in changedMethods:
        print(method.name)
        print(LOC[method.long_name])
        # print(CC[method.long_name])
        print(Halstead_Volume[method.long_name])
        print(PCom[method.long_name])

# repo_path = 'E:/Postgraduate_study/httpcomponents-core'
#
# old_commit = "b9a6b17c34a54f302503f171d72685168ee7d153"
# new_commit = "3d548ce9ece1845915cc109c9f0fb7272aea9d73"
# # changedMethods, LOC, CC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)
# changedMethods, LOC, Halstead_Volume, PCom = getChangedMethods(repo_path, old_commit, new_commit)
#
# for method in changedMethods:
#     print(method.name)
#     print(LOC[method.long_name])
#     # print(CC[method.long_name])
#     print(Halstead_Volume[method.long_name])
#     print(PCom[method.long_name])
