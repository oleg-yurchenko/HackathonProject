import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ThreadedChecker extends Thread{
    private ArrayList<String> processes;
    public static ArrayList<String> previouslyWhitelisted;
    JFrame optionsFrame = new JFrame();
    boolean exit = false;
    ArrayList<JCheckBox> processesCheckBoxs = new ArrayList<JCheckBox>();
    public static ArrayList<String> getProcesses() throws IOException {
        String line;
        ArrayList<String> parsedLine = new ArrayList<String>();
        Process process = Runtime.getRuntime().exec("tasklist.exe /FI \"STATUS eq running\" /FI \"SESSIONNAME eq Console\" /fo csv /nh");
        BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = output.readLine()) != null){
            boolean contains = false;
            for(int i=0; i<parsedLine.size(); ++i){
                if(line.split("\"")[1].equals(parsedLine.get(i))){
                    contains = true;
                }
            }
            if(!contains) {
                parsedLine.add(line.split("\"")[1]);
            }
        }
        System.out.println(parsedLine.toString());
        return parsedLine;
    }
    public void changeWhitelist() {
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionsFrame.dispose();
                exit = true;
            }
        });
        if (!CloseApps.bufferedWhitelist.isEmpty()) {
            for (int i = 0; i < CloseApps.bufferedWhitelist.size(); ++i) {
                processes.add(CloseApps.bufferedWhitelist.get(i));
            }
        }

        for (int i=0; i<processes.size(); ++i){
            System.out.println(CloseApps.bufferedWhitelist.toString());
            processesCheckBoxs.add(new JCheckBox(processes.get(i)));
            processesCheckBoxs.get(i).setBounds(50, 10+i*15, 400, 15);
            try {
                if (previouslyWhitelisted.contains(processesCheckBoxs.get(i).getText())) {
                    processesCheckBoxs.get(i).setSelected(true);
                }
            } catch (NullPointerException err){
                ;
            }
            optionsFrame.add(processesCheckBoxs.get(i));
            exitButton.setBounds(170, 30+i*15, 100, 20);
        }

        optionsFrame.add(exitButton);
        optionsFrame.setSize(800, 800);
        optionsFrame.setLayout(null);
        optionsFrame.setVisible(true);
        optionsFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit = true;
            }
        });
    }
    public ArrayList<String> checkWhitelist() {
        ArrayList<String> whitelistSelected = new ArrayList<String>();

        for(int i=0; i<processesCheckBoxs.size(); ++i){
            if(processesCheckBoxs.get(i).isSelected()){
                whitelistSelected.add(processesCheckBoxs.get(i).getText());
                if(!CloseApps.bufferedWhitelist.contains(processesCheckBoxs.get(i).getText())){
                    CloseApps.bufferedWhitelist.add(processesCheckBoxs.get(i).getText());
                }
            }
        }

        return whitelistSelected;
    }
    public ThreadedChecker() throws IOException {
        processes = getProcesses();
    }
    public void run() {
        changeWhitelist();
        while(!exit){
            previouslyWhitelisted = checkWhitelist();
            CloseApps.whitelistedApps = checkWhitelist();
            CloseApps.allProcesses = processes;
        }
    }
}
