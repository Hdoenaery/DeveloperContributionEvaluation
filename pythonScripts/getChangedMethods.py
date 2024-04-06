import os
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

                    # #提取该方法内容并保存到本地
                    lines = modified_file.source_code.split('\n')
                    extracted_code = '\n'.join(
                        lines[method.start_line - 1:method.end_line])  # 将start_line和end_line之间的列表用换行符连接成一个字符串
                    # print(extracted_code)
                    file_path = 'DeveloperContributionEvaluation/methodsContent/' + old_commit[0:6] + '_to_' + new_commit[0:6] + '/' + str(
                        method.name.replace("::", "_")) + '_new.txt'
                    # 如果有方法重载，则会出现方法名重复，需要将方法参数也带上
                    if os.path.exists(file_path):
                        # 文件名中不允许出现的特殊字符
                        forbidden_chars = '/\\?*:|"<>'

                        # 删除不允许出现在文件名中的字符
                        file_path = 'DeveloperContributionEvaluation/methodsContent/' + old_commit[0:6] + '_to_' + new_commit[0:6] + '/' + str(
                            ''.join(c for c in method.long_name.replace("::", "_") if
                                    c not in forbidden_chars)) + '_new.txt'

                    save_to_file(extracted_code, file_path)


    return changed_methods


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
    changedMethods = getChangedMethods(repo_path, old_commit, new_commit)

    for method in changedMethods:
        print(method.name)

# repo_path = 'E:/Postgraduate_study/FlappyBird'
# old_commit = 'd7b64a56e687ca6e3a3b295342fe07f0fdcb87a2'
# new_commit = '5e5ba4bf131b5998c33474ebe34ac7e9d86187ad'
# changedMethods = getChangedMethods(repo_path, old_commit, new_commit)
#
# for method in changedMethods:
#     print(f"{method.name}   {method.start_line}   {method.end_line}")
