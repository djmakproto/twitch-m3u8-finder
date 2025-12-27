import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class UIClass {
    private final String[] domains;
    private ArrayList<String[]> searchList;
    JFrame mainFrame;
    JPanel buttonLayout, listLayout, usernamePanel, timePanel, streamIDPanel;
    JLabel usernameLabel, timeLabel, streamIDLabel, waitLabel;
    JTextField usernameInput, startTimeInput, streamIDInput;
    JTextArea outputArea;
    JScrollPane listScrollPane;
    JButton searchButton, clearButton, listButton, searchListButton, clearListButton;
    public UIClass(String[] d){
        domains = d;
        searchList = new ArrayList<String[]>();
        makeGUI();
    }

    private void makeGUI(){
        mainFrame = new JFrame("Twitch M3U8 Finder v1.5.1");
        buttonLayout = new JPanel();
        listLayout = new JPanel();
        usernamePanel = new JPanel();
        timePanel = new JPanel();
        streamIDPanel = new JPanel();
        listScrollPane = new JScrollPane(listLayout);
        usernameLabel = new JLabel("Username: ", JLabel.CENTER);
        timeLabel = new JLabel("Start Time: ", JLabel.CENTER);
        streamIDLabel = new JLabel("Stream ID: ", JLabel.CENTER);
        waitLabel = new JLabel("Please wait, searching...", JLabel.CENTER);

        outputArea = new JTextArea();
        usernameInput = new JTextField(30);
        startTimeInput = new JTextField(30);
        streamIDInput = new JTextField(30);
        searchButton = new JButton("Search for stream");
        clearButton = new JButton("Clear fields");
        clearListButton = new JButton("Clear list");
        listButton = new JButton("Add to list");
        searchListButton = new JButton("Search list");

        mainFrame.setSize(800,600);
        mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        searchButton.addActionListener(e -> {
            String username = usernameInput.getText().trim().toLowerCase();
            String startTime = startTimeInput.getText().trim().toLowerCase();
            String streamID = streamIDInput.getText().trim().toLowerCase();
            waitLabel.setVisible(true);
            outputArea.setVisible(false);
            outputArea.setText("");
            listButton.setEnabled(false);
            searchListButton.setEnabled(false);
            searchButton.setEnabled(false);
            clearButton.setEnabled(false);
            if(!username.isEmpty() && !startTime.isEmpty() && !streamID.isEmpty()) {
                SwingWorker<Set<String>, Void> worker = new SwingWorker<Set<String>, Void>() {
                    @Override
                    protected Set<String> doInBackground() throws Exception {
                        publish();
                        return Utility.useThreadedGuesser(domains, username, startTime, streamID);
                    }

                    @Override
                    protected void done() {
                        try {
                            Set<String> results = get();
                            for(String r:results){
                                if(outputArea.getText().isEmpty()){
                                    outputArea.setText(r);
                                } else {
                                    outputArea.setText(outputArea.getText() + "\n" + r);
                                }
                            }
                            waitLabel.setVisible(false);
                            outputArea.setVisible(true);
                            listButton.setEnabled(true);
                            searchListButton.setEnabled(true);
                            searchButton.setEnabled(true);
                            clearButton.setEnabled(true);
                        } catch (Exception ex) {
                            String msg = ex.getMessage();
                            System.out.println(Arrays.toString(ex.getStackTrace()));
                            waitLabel.setText(msg.substring(msg.indexOf(":")+1).trim());
                            waitLabel.setVisible(true);
                            listButton.setEnabled(true);
                            searchListButton.setEnabled(true);
                            searchButton.setEnabled(true);
                            clearButton.setEnabled(true);
                        }
                    }

                    @Override
                    protected void process(List<Void> chunks) {
                        if(!isDone()){
                            waitLabel.setText(String.format("searching for %s %s %s", username, startTime, streamID));
                        }
                    }
                };
                worker.execute();
            } else {
                waitLabel.setText("One or more of your inputs are incorrect, please check them closely and try again.");
                waitLabel.setVisible(true);
                listButton.setEnabled(true);
                searchListButton.setEnabled(true);
                searchButton.setEnabled(true);
                clearButton.setEnabled(true);
            }
        });

        clearButton.addActionListener(e ->{
            searchList.clear();
            outputArea.setText("");
            usernameInput.setText("");
            startTimeInput.setText("");
            streamIDInput.setText("");
            waitLabel.setText("Please wait, searching...");
            outputArea.setVisible(false);
            waitLabel.setVisible(false);
            listButton.setEnabled(true);
            searchListButton.setEnabled(true);
            searchButton.setEnabled(true);
            clearButton.setEnabled(true);
        });

        clearListButton.addActionListener(e -> {
            for(Component c: listLayout.getComponents()){
                listLayout.remove(c);
            }
            clearListButton.setVisible(false);
            listLayout.setVisible(false);
            listScrollPane.setVisible(false);
            searchListButton.setVisible(false);
        });

        searchListButton.addActionListener(e -> {
            waitLabel.setVisible(true);
            outputArea.setVisible(false);
            outputArea.setText("");
            listButton.setEnabled(false);
            searchListButton.setEnabled(false);
            searchButton.setEnabled(false);
            clearButton.setEnabled(false);
            clearListButton.setEnabled(false);
            SwingWorker<List<Set<String>>, Void> worker = new SwingWorker<List<Set<String>>, Void>() {
                List<Set<String>> resultsArr = new ArrayList<Set<String>>();
                @Override
                protected List<Set<String>> doInBackground() throws Exception {
                    for (String[] streamInfo : searchList) {
                        resultsArr.add(Utility.useThreadedGuesser(domains, streamInfo[0], streamInfo[1], streamInfo[2]));
                        publish();
                    }
                    return resultsArr;
                }

                @Override
                protected void done() {
                    try {
                        for (Set<String> results : resultsArr) {
                            for (String r : results) {
                                if (outputArea.getText().isEmpty()) {
                                    outputArea.setText(r);
                                } else {
                                    outputArea.setText(outputArea.getText() + "\n" + r);
                                }
                            }
                        }
                        waitLabel.setVisible(false);
                        outputArea.setVisible(true);
                        listButton.setEnabled(true);
                        searchListButton.setEnabled(true);
                        searchButton.setEnabled(true);
                        clearButton.setEnabled(true);
                        clearListButton.setEnabled(true);
                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        System.out.println(Arrays.toString(ex.getStackTrace()));
                        waitLabel.setText(msg.substring(msg.indexOf(":")+1).trim());
                        waitLabel.setVisible(true);
                        listButton.setEnabled(true);
                        searchListButton.setEnabled(true);
                        searchButton.setEnabled(true);
                        clearButton.setEnabled(true);
                        clearListButton.setEnabled(true);
                    }
                }

                @Override
                protected void process(List<Void> chunks) {
                    if(!isDone()){
                        waitLabel.setText("Searching list...");
                    }
                }
            };
            worker.execute();
        });

        //todo fix remove item from list
        listButton.addActionListener(e-> {
            if (!usernameInput.getText().isEmpty() && !startTimeInput.getText().isEmpty() && !streamIDInput.getText().isEmpty()){
                String[] streamInfo = new String[3];
                streamInfo[0] = usernameInput.getText().toLowerCase().trim();
                streamInfo[1] = startTimeInput.getText().toLowerCase().trim();
                streamInfo[2] = streamIDInput.getText().toLowerCase().trim();
                searchList.add(streamInfo);
                JPanel listCard = new JPanel();
                listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
                JLabel listUser = new JLabel(streamInfo[0], JLabel.CENTER);
                JLabel listTime = new JLabel(streamInfo[1], JLabel.CENTER);
                JLabel listID = new JLabel(streamInfo[2], JLabel.CENTER);
                JButton removeFromListButton = new JButton("X");
                removeFromListButton.addActionListener(f -> {
                    for(int i = 0; i < searchList.size(); i++){
                        if(Arrays.equals(streamInfo, searchList.get(i))){
                            searchList.remove(i);
                            listLayout.remove(i);
                            listLayout.updateUI();
                        }
                    }
                });
                listCard.add(listUser);
                listCard.add(listTime);
                listCard.add(listID);
                listCard.add(removeFromListButton);
                listLayout.add(listCard);
                listLayout.updateUI();
                listScrollPane.setVisible(true);
                listLayout.setVisible(true);
                searchListButton.setVisible(true);
                clearListButton.setVisible(true);
            }
        });

        listLayout.setVisible(false);
        listScrollPane.setVisible(false);
        outputArea.setVisible(false);
        waitLabel.setVisible(false);
        clearListButton.setVisible(false);
        searchListButton.setVisible(false);

        listScrollPane.setMaximumSize(new Dimension(800,100));
        mainFrame.add(listScrollPane);

        //usernamePanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameInput);
        usernamePanel.setMaximumSize(new Dimension(800,50));
        mainFrame.add(usernamePanel);

        //timePanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        timePanel.add(timeLabel);
        timePanel.add(startTimeInput);
        timePanel.setMaximumSize(new Dimension(800,50));
        mainFrame.add(timePanel);

        //streamIDPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        streamIDPanel.add(streamIDLabel);
        streamIDPanel.add(streamIDInput);
        streamIDPanel.setMaximumSize(new Dimension(800,50));
        mainFrame.add(streamIDPanel);

        //buttonLayout.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        buttonLayout.add(listButton);
        buttonLayout.add(searchListButton);
        buttonLayout.add(searchButton);
        buttonLayout.add(clearButton);
        buttonLayout.add(clearListButton);
        buttonLayout.setMaximumSize(new Dimension(800, 50));
        mainFrame.add(buttonLayout);

        mainFrame.add(waitLabel);
        mainFrame.add(outputArea);

        mainFrame.setVisible(true);
    }
};
