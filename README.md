#hadoop2.6.0�汾��Ⱥ�����

###�������2̨���������һ̨hostnameΪmaster����һ̨hostnameΪslave1

###1.����hadoop�û��˺�
```
sudo addgroup hadoop
sudo adduser -ingroup hadoop hadoop
/etc/sudoers  => hadoop ALL=(ALL:ALL) ALL
/etc/hostname  �˴��޸������̨��������hostnameΪmaster
/etc/hosts    ���slave1 ipӳ��
������Ч
���������hadoop�û���¼��
���ߵ�¼root�û������� sudo -su hadoop �л��û�
```
###2.jdk����
```
��װJava����
```

###3.����hadoop2.6
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
###4.�޸������ļ�
```
����ΪҪ�޸ĵ��ļ�
~/hadoop/etc/hadoop/hadoop-env.sh
~/hadoop/etc/hadoop/yarn-env.sh
~/hadoop/etc/hadoop/slaves
~/hadoop/etc/hadoop/core-site.xml
~/hadoop/etc/hadoop/hdfs-site.xml
~/hadoop/etc/hadoop/mapred-site.xml
~/hadoop/etc/hadoop/yarn-site.xml
```
```
4.1 ���� hadoop-env.sh�ļ�-->�޸�JAVA_HOME
����ָ��Ϊ�鿴��װ��java·�������û�����Ȱ�װjava
hadoop@master:update-alternatives --config java �鿴��װ��java·��
��hadoop-env.sh�ļ��ҵ�������һ�У��޸�JAVA_HOMEΪ���java��ַ��
�˴�Ϊ/usr/lib/jvm/java-7-openjdk-amd64
# The java implementation to use.
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

4.2 ���� yarn-env.sh �ļ�-->>�޸�JAVA_HOME
ͬ��
# some Java parameters
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

4.3 ����slaves�ļ�-->>����slave�ڵ�
���ı�����������ݾͿ����ˡ�
slave1

4.4 ���� core-site.xml�ļ�-->>����hadoop��������
�޸� hdfs�ļ��˿���9000����hadoop.tmp.dir�ĵ�ַ
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

4.5 ����hdfs-site.xml �ļ�-->>����hdfs������Ϣ
�޸� namenode��datanode�˿ں�Ŀ¼λ��
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

4.6 ����mapred-site.xml �ļ�-->>����mapreduce����
ʹ��yarn��ܡ�jobhistoryʹ�õ�ַ�Լ�web��ַ
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

4.7 ����yarn-site.xml�ļ�
����yarn����
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

###5.����hadoop��������
```
��/etc/bash.bashrc�ں���������������

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
###6.��ʽ��
```
hadoop@master:hdfs namenode -format
```

###7.���Ƶ�slvae1����
```
������Ϻ���������ָ��
hadoop@slave1:cd /usr/local
hadoop@slave1:sudo chown -R hadoop:hadoop hadoop 
hadoop@slave1:sudo chmod -R 777 /usr/local/hadoop
```

###8.���м��
```
hadoop@master:start-all.sh
```











  