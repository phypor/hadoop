#hadoop2.6.0版本集群环境搭建

###搭建环境：2台物理机器，一台hostname为master，另一台hostname为slave1

###1.创建hadoop用户账号
```
sudo addgroup hadoop
sudo adduser -ingroup hadoop hadoop
/etc/sudoers  => hadoop ALL=(ALL:ALL) ALL
/etc/hostname  此处修改你的这台服务器的hostname为master
/etc/hosts    添加slave1 ip映射
重启生效
重启后可用hadoop用户登录；
或者登录root用户后，输入 sudo -su hadoop 切换用户
```
###2.jdk环境
```
安装Java环境
```

###3.下载hadoop2.6
```
hadoop@master:cd /usr/local/
hadoop@master:wget http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
hadoop@master:tar -xzvf hadoop-2.6.0.tar.gz
hadoop@master:sudo mv hadoop-2.6.0 /usr/local/hadoop
hadoop@master:sudo chown -R hadoop:hadoop hadoop
hadoop@master:sudo chmod -R 777 /usr/local/hadoop
hadoop@master:cd hadoop
hadoop@master:sudo mkdir tmp
hadoop@master:sudo mkdir dfs
hadoop@master:sudo mkdir dfs/data
hadoop@master:sudo mkdir dfs/name
```
###4.修改配置文件
```
以下为要修改的文件
~/hadoop/etc/hadoop/hadoop-env.sh
~/hadoop/etc/hadoop/yarn-env.sh
~/hadoop/etc/hadoop/slaves
~/hadoop/etc/hadoop/core-site.xml
~/hadoop/etc/hadoop/hdfs-site.xml
~/hadoop/etc/hadoop/mapred-site.xml
~/hadoop/etc/hadoop/yarn-site.xml
```
```
4.1 配置 hadoop-env.sh文件-->修改JAVA_HOME
以下指令为查看安装的java路径，如果没有请先安装java
hadoop@master:update-alternatives --config java 查看安装的java路径
在hadoop-env.sh文件找到以下这一行，修改JAVA_HOME为你的java地址。
此处为/usr/lib/jvm/java-7-openjdk-amd64
# The java implementation to use.
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

4.2 配置 yarn-env.sh 文件-->>修改JAVA_HOME
同上
# some Java parameters
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

4.3 配置slaves文件-->>增加slave节点
在文本添加以下内容就可以了。
slave1

4.4 配置 core-site.xml文件-->>增加hadoop核心配置
修改 hdfs文件端口是9000、和hadoop.tmp.dir的地址
<configuration>
 <property>
  <name>fs.defaultFS</name>
  <value>hdfs://master:9000</value>
 </property>
 <property>
  <name>io.file.buffer.size</name>
  <value>131072</value>
 </property>
 <property>
  <name>hadoop.tmp.dir</name>
  <value>file:/usr/local/hadoop/tmp</value>
  <description>Abasefor other temporary directories.</description>
 </property>
 <property>
  <name>hadoop.proxyuser.spark.hosts</name>
  <value>*</value>
 </property>
<property>
  <name>hadoop.proxyuser.spark.groups</name>
  <value>*</value>
 </property>
</configuration>

4.5 配置hdfs-site.xml 文件-->>增加hdfs配置信息
修改 namenode、datanode端口和目录位置
<configuration>
 <property>
  <name>dfs.namenode.secondary.http-address</name>
  <value>master:9001</value>
 </property>
  <property>
   <name>dfs.namenode.name.dir</name>
   <value>file:/usr/local/hadoop/dfs/name</value>
 </property>
 <property>
  <name>dfs.datanode.data.dir</name>
  <value>file:/usr/local/hadoop/dfs/data</value>
  </property>
 <property>
  <name>dfs.replication</name>
  <value>3</value>
 </property>
 <property>
  <name>dfs.webhdfs.enabled</name>
  <value>true</value>
 </property>
</configuration>

4.6 配置mapred-site.xml 文件-->>增加mapreduce配置
使用yarn框架、jobhistory使用地址以及web地址
<configuration>
  <property>
   <name>mapreduce.framework.name</name>
   <value>yarn</value>
 </property>
 <property>
  <name>mapreduce.jobhistory.address</name>
  <value>master:10020</value>
 </property>
 <property>
  <name>mapreduce.jobhistory.webapp.address</name>
  <value>master:19888</value>
 </property>
</configuration>

4.7 配置yarn-site.xml文件
增加yarn功能
<configuration>
  <property>
   <name>yarn.nodemanager.aux-services</name>
   <value>mapreduce_shuffle</value>
  </property>
  <property>
   <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
   <value>org.apache.hadoop.mapred.ShuffleHandler</value>
  </property>
  <property>
   <name>yarn.resourcemanager.address</name>
   <value>master:8032</value>
  </property>
  <property>
   <name>yarn.resourcemanager.scheduler.address</name>
   <value>master:8030</value>
  </property>
  <property>
   <name>yarn.resourcemanager.resource-tracker.address</name>
   <value>master:8035</value>
  </property>
  <property>
   <name>yarn.resourcemanager.admin.address</name>
   <value>master:8033</value>
  </property>
  <property>
   <name>yarn.resourcemanager.webapp.address</name>
   <value>master:8088</value>
  </property>
</configuration> 
```

###5.配置hadoop环境变量
```
打开/etc/bash.bashrc在后面输入以下内容

#HADOOP VARIABLES START
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export HADOOP_INSTALL=/usr/local/hadoop
export PATH=$PATH:$HADOOP_INSTALL/bin
export PATH=$PATH:$HADOOP_INSTALL/sbin
export HADOOP_MAPRED_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_HOME=$HADOOP_INSTALL
export HADOOP_HDFS_HOME=$HADOOP_INSTALL
export YARN_HOME=$HADOOP_INSTALL
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_INSTALL/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_INSTALL/lib"
#HADOOP VARIABLES END

hadoop@master:source /etc/bash.bashrc
```
###6.格式化
```
hadoop@master:hdfs namenode -format
```

###7.复制到slvae1主机
```
复制完毕后输入以下指令
hadoop@slave1:cd /usr/local
hadoop@slave1:sudo chown -R hadoop:hadoop hadoop 
hadoop@slave1:sudo chmod -R 777 /usr/local/hadoop
```

###8.运行检查
```
hadoop@master:start-all.sh
```











  