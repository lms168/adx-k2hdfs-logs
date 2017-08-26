package com.zzcm.upload;

/**
 * Created by lms on 17-8-22.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class HdfsClient {

    FileSystem fs = null;

    /**
     * 初始化FileSystem
     */

    public void init(String defaultFS, String user) throws Exception {
        // 构造一个配置参数对象，设置一个参数：我们要访问的hdfs的URI
        // 从而FileSystem.get()方法就知道应该是去构造一个访问hdfs文件系统的客户端，以及hdfs的访问地址
        // new Configuration();的时候，它就会去加载jar包中的hdfs-default.xml
        // 然后再加载classpath下的hdfs-site.xml
        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS", "hdfs://hdp-node01:9000");
        /**
         * 参数优先级： 1、客户端代码中设置的值 2、classpath下的用户自定义配置文件 3、然后是服务器的默认配置
         */
        conf.set("dfs.replication", "1");

        // 获取一个hdfs的访问客户端，根据参数，这个实例应该是DistributedFileSystem的实例
        // fs = FileSystem.get(conf);

        // 如果这样去获取，那conf里面就可以不要配"fs.defaultFS"参数，而且，这个客户端的身份标识已经是hadoop用户
        fs = FileSystem.get(new URI(defaultFS), conf, user);
    }

    /**
     * 往hdfs上传文件
     */

    public void uploadFileToHdfs(String localPath,String hdfsPath) throws Exception {
        // 要上传的文件所在的本地路径
        Path src = new Path(localPath);
        // 要上传到hdfs的目标路径
        Path dst = new Path(hdfsPath);

        if (!fs.exists(dst)){
            fs.mkdirs(dst);
        }
        fs.copyFromLocalFile(src, dst);
        fs.close();
    }

    /**
     * 从hdfs中复制文件到本地文件系统
     */

    public void downloadFileToLocal(String localPath, String hdfsPath) throws IllegalArgumentException, IOException {
        fs.copyToLocalFile(new Path(hdfsPath), new Path(localPath));
        fs.close();
    }


    /**
     * 在hfds中创建目录
     */

    public void mkdirOnHdfs(String path) throws IllegalArgumentException, IOException {
        // 创建目录
        fs.mkdirs(new Path(path));
        fs.close();
    }

    /**
     * 在hfds中创建删除目录
     */

    public void deleteOnHdfs(String path) throws IllegalArgumentException, IOException {
        // 创建目录
        fs.mkdirs(new Path(path));
        fs.close();
    }



    /**
     * 在hfds中重命名
     */

    public void renameOnHdfs(String oldPath,String newPath) throws IllegalArgumentException, IOException {

        // 重命名文件或文件夹
        fs.rename(new Path(oldPath), new Path(newPath));
        fs.close();
    }

    /**
     * 查看目录信息，只显示文件
     */

    public void listFiles() throws FileNotFoundException, IllegalArgumentException, IOException {
        // 思考：为什么返回迭代器，而不是List之类的容器
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();

            System.out.println(fileStatus.getPath().getName());
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getLen());
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            for (BlockLocation bl : blockLocations) {
                System.out.println("block-length:" + bl.getLength() + "--" + "block-offset:" + bl.getOffset());
                String[] hosts = bl.getHosts();
                for (String host : hosts) {
                    System.out.println(host);
                }
            }
            System.out.println("--------------分割线--------------");
        }
        fs.close();
    }

    /**
     * 查看文件及文件夹信息
     */

    public void listAll() throws FileNotFoundException, IllegalArgumentException, IOException {
        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        String flag = "d--             ";

        for (FileStatus fstatus : listStatus) {
            if (fstatus.isFile())
                flag = "f--         ";
            System.out.println(flag + fstatus.getPath().getName());
        }
        fs.close();
    }

    public static void main(String[] args){
        HdfsClient hdfsClient = new HdfsClient();
        try {
            hdfsClient.init("hdfs://192.168.0.61:9000","root");
            hdfsClient.renameOnHdfs("/azkaban3.0","/azkaban4.0");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}