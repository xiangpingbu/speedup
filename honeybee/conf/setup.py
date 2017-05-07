# coding=utf-8
from setuptools import setup, find_packages

setup(
    name="honeybee",
    version="0.0",
    packages=find_packages(),
    package_data={
        # 包含所有.txt文件
        '': ['*.sh','*.py']
        # # 包含data目录下所有的.dat文件
        # 'test': ['data/*.dat'],
    },
    # include_package_data=True,
    zip_safe=True,
    install_requires=[  # 安装依赖的其他包
        'requests',
        'flask',
        'pandas',
        'pymysql',
        'DBUtils',
        'sklearn',
    ],

    author="maas",
    description="web service for maas tool",

)
