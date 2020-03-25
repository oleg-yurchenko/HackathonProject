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
    JTextField searchField = new JTextField();
    JButton exitButton = new JButton("Exit");
    ImageIcon icon = new ImageIcon("C:\\Users\\oleg\\IdeaProjects\\HackathonTesting\\settings.png");
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
        return parsedLine;
    }
    public void checkBoxLister(ArrayList<String> processList) {
        for (int i=0; i<processesCheckBoxs.size(); ++i){
            if(processList.contains(processesCheckBoxs.get(i).getText())){
                processesCheckBoxs.get(i).setVisible(true);
            } else {
                processesCheckBoxs.get(i).setVisible(false);
            }
        }
    }
    public void changeWhitelist() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit = true;
            }
        });
        try {
            if (!CloseApps.whitelistedApps.isEmpty()) {
                for (int i = 0; i < CloseApps.whitelistedApps.size(); ++i) {
                    if (!processes.contains(CloseApps.whitelistedApps.get(i))) {
                        processes.add(CloseApps.whitelistedApps.get(i));
                    }
                }
            }
        }catch (NullPointerException err){
            ;
        }

        for (int i=0; i<processes.size(); ++i){
            processesCheckBoxs.add(new JCheckBox(processes.get(i)));
            processesCheckBoxs.get(i).setBounds(250, 50+i*15, 300, 15);
            try {
                if (previouslyWhitelisted.contains(processesCheckBoxs.get(i).getText())) {
                    processesCheckBoxs.get(i).setSelected(true);
                    processesCheckBoxs.get(i).setForeground(Color.RED);
                }
            } catch (NullPointerException err){
                ;
            }
            processesCheckBoxs.get(i).setForeground(new Color(188, 19, 254));
            processesCheckBoxs.get(i).setBackground(new Color(20, 70, 90));
            optionsFrame.add(processesCheckBoxs.get(i));
            exitButton.setBounds(300, 70+i*15, 100, 20);
        }

        searchField.setBounds(300, 20, 200, 20);

        optionsFrame.setTitle("Blacklist");
        optionsFrame.setIconImage(icon.getImage());
        optionsFrame.getContentPane().setBackground(new Color(20, 70, 90));
        optionsFrame.add(exitButton);
        optionsFrame.add(searchField);
        optionsFrame.setSize(800, 140+processesCheckBoxs.size()*15);
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
    public void searchWhitelist() {
        String query = searchField.getText();
        ArrayList<String> queriedProcesses = new ArrayList<String>();
        for(int i=0; i<processes.size(); ++i){
            if(processes.get(i).toLowerCase().contains(query.toLowerCase())){
                queriedProcesses.add(processes.get(i));
            }
        }
        checkBoxLister(queriedProcesses);
    }
    public void run() {
        changeWhitelist();
        while(!exit){
            previouslyWhitelisted = checkWhitelist();
            searchWhitelist();
            CloseApps.whitelistedApps = checkWhitelist();
            CloseApps.allProcesses = processes;
        }
        optionsFrame.dispose();
    }
}
