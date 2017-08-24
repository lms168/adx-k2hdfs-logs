import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by lms on 17-8-15.
 */
public class ReadTest {
    static String fileName = "/home/lms/a1.txt";
    public static void readMethod1(){
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            line = in.readLine();
            while (line!=null){
                System.out.println("读取到了==================="+line);
                Thread.sleep(100);
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        ReadTest.readMethod1();

    }
}
