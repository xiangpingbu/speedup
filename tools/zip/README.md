编译
----
g++ -o zip zip.c

索引文件
----
./zip index pruned.tsv

生成2个文件:

* pruned.tsv.dat
* pruned.tsv.dat.idx

前提条件:

* Key必须是递增的（OS type可能要放到最后）

查找记录
----
./zip search pruned.tsv.dat 0-0-0-14
