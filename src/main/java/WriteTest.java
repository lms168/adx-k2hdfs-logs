import java.io.*;

/**
 * Created by lms on 17-8-15.
 */
public class WriteTest {
    static String fileName = "/home/lms/a1.txt";
    public  void writeMethod(){

        try {
            FileWriter writer = new FileWriter(fileName,true);
            int i=0;
          while (true){
                i++;
                String line = String.valueOf(Math.random());
                writer.write(line);
                System.out.println("写入了==================="+line);
                Thread.sleep(100);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
        WriteTest writeTest = new WriteTest();
        writeTest.writeMethod();
    }
}
