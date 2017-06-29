# coding=utf-8
class LifeCycle():
    def __init__(self,model_name,model_branch):
        self.model_name = model_name  # 模型名称
        self.model_branch = model_branch  # 模型分支名
        self.file_associate = None # 关联文件
        self.current_status = None # 当前模型构建的状态

