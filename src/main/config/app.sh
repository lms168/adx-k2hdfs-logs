#!/bin/bash

echo "╭－－－－－－╮ 			"
echo "╰╮〥－－－〥╭╯灌灌水-脚本	"
echo "╭╯╰－－－－╯╰╮ 				"
echo "║～～～～～～║				"
echo "║◢██◣◢～～～ ║　 			"
echo "║◥██◤◥～～～ ║　　 			"
echo "║～～◢██◣◢～ ║				"
echo "║～～◥██◤◥～ ║				"
echo "╰═==========═╯			"

echo "Welcome Software"
#echo "App Run ..."
echo "使用JDK信息:"$JAVA_HOME
echo `java -version`

#----------------------------------------------------------------------------------
#系统常量配置信息
#配置日志输出目录
LOG_PATH="/home/ad/logs/"
#配置文档目录
CONFIG_PATH="./config"
#启动类
APP_CLASS="com.zzcm.App"
#----------------------------------------------------------------------------------

#获取当前项目路径
PROJECT_PATH=`pwd`
echo "当前服务路径:"$PROJECT_PATH

#检测服务是否运行
if [ -e $PROJECT_PATH"/PID" ];then
	ISRUN=`cat $PROJECT_PATH"/PID"`
	#echo $ISRUN
fi

#选择列表
echo "├┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┤	|命令模式						"
echo "├ 1.启动程序服务 ┤	./app.sh start				"
echo "├ 2.停止程序服务 ┤	./app.sh stop				"
echo "├ 3.正式环境服务 ┤	./app.sh server				"
echo "├ 4.检测程序服务 ┤	./app.sh run				"
echo "├┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┤ 							"
if [ "$ISRUN" = "" ];then
		echo "当前服务尚未启动:推荐选择[1]启动服务"
	else
		echo "当前服务已经启动:推荐选择[2]停止服务"
fi

if [ "$1" = "" ];then
		printf "请选择操作类型:"
		read INDEX
	else
		if [ "$1" = "start" ];then
			echo "准备启动服务"
			INDEX=1
		fi
		if [ "$1" = "stop" ];then
			echo "准备停止服务"
			INDEX=2
		fi
		if [ "$1" = "server" ];then
			echo "准备启动正式环境服务"
			INDEX=3
		fi
		if [ "$1" = "run" ];then
			echo "准备检测程序服务"
			INDEX=4
		fi
fi

if [ "$INDEX" = "" ];then
	echo "操作无效服务退出"
	exit 1;
fi

#准备检测程序服务
MODENUM="1"
if [ "$INDEX" = "4" ];then
	MODENUM="4"
	INDEX="1"
	echo "启动模式代号:$MODENUM"
fi

#服务上运行参数设置最大内存=最小内存 内存释放
MEMORY=""
if [ "$INDEX" = "3" ];then
	MEMORY="-Xms6g -Xmx6g -Xmn4g"
	INDEX="1"
	echo "JVM内存配置:$MEMORY"
fi

#停止过程
if [ "$INDEX" = "2" ];then
	if [ "$ISRUN" = "" ];then
		echo "当前服务尚未启动:推荐先启动服务"
		exit 1
	fi
	kill $ISRUN
	#5次检测
	for ((i=1;i<=25;i++)) ;do
		printf "."
		COUNT=`ps -ef |grep $ISRUN |grep -v "grep" |wc -l`
	   if [ 0 == $COUNT ];then
			rm -rf $PROJECT_PATH"/PID"
			echo ""
			echo "服务停止工作."
			exit 0
	   fi
		sleep 1
	done
	echo ""
	echo "服务正常停止失败,是否使用Kill -9 强制停止服务?"
	echo "任意键回车执行强制停止服务或Ctrl+C退出"
	read KILL9
	rm -rf $PROJECT_PATH"/PID"
	kill -9 $ISRUN
	exit 1;
fi

#启动过程
if [ "$INDEX" = "1" ];then
	#搜索当前的jar包
	JAR_LIST=""
	for jar in `find ./* -print |grep jar` ;do
		if [ "$JAR_LIST" = "" ];then
		  JAR_LIST="$JAR_LIST$PROJECT_PATH${jar//.\///}"
		else  
		  JAR_LIST="$JAR_LIST:$PROJECT_PATH${jar//.\///}"
		fi
	done
	#echo $JAR_LIST

	#启动服务
	if [ "$MODENUM" = "4" ];then
		#java -Dlog.home=$LOG_PATH -cp $JAR_LIST:$CONFIG_PATH $APP_CLASS
		java $MEMORY -Dlog.home=$LOG_PATH -cp $JAR_LIST:$CONFIG_PATH $APP_CLASS
		#java -Dlog.home=/home/ad/logs/ -cp ./szunoc-0.0.1.jar:./lib/*:./config com.zzcm.statsz.szunoc.App
		exit 1
	fi

	if [ "$MODENUM" = "1" ];then
		RMI="-XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=80 -XX:+CMSParallelRemarkEnabled"
		nohup java $MEMORY $RMI -Dlog.home=$LOG_PATH -cp $JAR_LIST:$CONFIG_PATH $APP_CLASS 1>/dev/null 2>$PROJECT_PATH/ERROR&
	fi

	#启动后休息4秒
	for i in 0 1 2 4 ;do
		printf "."
		sleep 1
	done
	echo ""

	#获取进程号并写入PID文件内
	PID=`ps -ef | grep "java" | fgrep "$PROJECT_PATH" | awk '{print $2}'`

	if [ "$PID" = "" ];then
			echo "启动失败!"
			echo `cat ERROR`
			exit 1
		else  
			echo "启动成功!进程号:$PID"
			echo $PID > $PROJECT_PATH"/PID"
			rm -rf $PROJECT_PATH"/ERROR"
	fi
fi














