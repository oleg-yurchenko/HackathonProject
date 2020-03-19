import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame mainFrame = new JFrame();
        final ThreadedChecker[] threadedChecker = {new ThreadedChecker()};
        CloseApps threadedCloser[] = {new CloseApps()};

        final JButton activeButton = new JButton("Activate");
        JButton editWhitelist = new JButton("Edit Whitelist");
        activeButton.setBounds(200, 190, 100,40);
        editWhitelist.setBounds(200, 240, 100, 40);

        JLabel mainText = new JLabel("Welcome to the application");
        mainText.setBounds(175, 100, 300, 100);
        JLabel onOff = new JLabel("Program is not running");
        onOff.setBounds(190, 250, 300, 100);

        activeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(onOff.getText().equals("Program is not running")){
                    onOff.setText("Program is running");
                    activeButton.setText("Deactivate");
                    CloseApps.done = false;
                    threadedCloser[0].start();
                } else {
                    threadedCloser[0] = new CloseApps();
                    onOff.setText("Program is not running");
                    activeButton.setText("Activate");
                    CloseApps.done = true;
                }
            }
        });
        editWhitelist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                            threadedChecker[0].start();
                        } catch (IllegalThreadStateException err){
                            try {
                                threadedChecker[0] = new ThreadedChecker();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    threadedChecker[0].start();
                }
            }
        });

        mainFrame.add(activeButton);
        mainFrame.add(mainText);
        mainFrame.add(onOff);
        mainFrame.add(editWhitelist);

        mainFrame.setSize(500, 500);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return;
    }
}
