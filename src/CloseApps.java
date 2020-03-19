import java.io.IOException;
import java.util.ArrayList;

public class CloseApps extends Thread{
    public static ArrayList<String> whitelistedApps;
    public static ArrayList<String> allProcesses;
    public static ArrayList<String> bufferedWhitelist = new ArrayList<String>();
    public static boolean done = false;

    public void run() {
        if(!(whitelistedApps == null || allProcesses == null)) {
            while (!done) {
                try {
                    for (int i = 0; i < allProcesses.size(); ++i) {
                        if (whitelistedApps.contains(allProcesses.get(i))) {
                            try {
                                System.out.println(allProcesses.get(i));
                                Runtime.getRuntime().exec("taskkill /F /IM " + allProcesses.get(i));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Thread.currentThread().sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
