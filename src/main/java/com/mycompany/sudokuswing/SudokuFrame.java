package com.mycompany.sudokuswing;

import Database.DatabaseConnector;
import Database.GameState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class SudokuFrame extends JFrame {
    private final JPanel buttonSelectionPanel;
    private final SudokuPanel sPanel;
    private final JLabel lTimerJLabel;
    private final JLabel mistakeJLabel;
    private final JButton btnUndo ;
    private final JButton btnDelete;
    private final JButton btnHint;
    private Clip clip;
    private Sound sound = new Sound();
    private JSlider slider;
    private final JLabel modelb;
    private final FunctionButton btnPaint;
    private JLabel lbNewGame;
    private final FunctionButton btnPrize;
    private final FunctionButton btnSetting;
    private final FunctionButton btnPauseGame;
    private final JToggleButton btnNote;
    private final FunctionButton drop;
    private final JLabel lbHint;
    private final JLabel lbPencil;
    private final JLabel lbErase;
    private final JLabel lbUndo;
    private boolean noteStatus;
    private FunctionButton volumeMute;
    private final Font font18 = new Font("Time New Roman",Font.PLAIN,18);
    private final Font font20 = new Font("Time New Roman",Font.PLAIN,20);
    private final String DEFAULT_MODE = "Medium";
    private String currentMode = "Medium";
    private String currentPuzzleType = "9x9";


    public SudokuFrame(Integer user_id) {
        SudokuFrame frame = this;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Sudoku");
        this.setMinimumSize(new Dimension(700, 800));
        this.setLocationRelativeTo(null);
        
        //Create Panel
        JPanel windowPanel = new JPanel();
        windowPanel.setLayout(new BorderLayout());
        windowPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        //Create button
        buttonSelectionPanel = new JPanel();
        
        //Create SudokuPanel
        sPanel = new SudokuPanel(user_id);
        sPanel.setFrame(frame);

        //Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,3));
        
        modelb = new JLabel();
        modelb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        modelb.setFont(font18);
        
        btnPaint = new FunctionButton(createResizedIcon("icons/paint.png", 30, 30));
        btnPaint.setFocusable(false);
        btnPaint.setHorizontalAlignment(SwingConstants.LEFT);
        
        lTimerJLabel = new JLabel("00:00");
        lTimerJLabel.setFont(font18);
        lTimerJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        mistakeJLabel = new JLabel("Mistakes: 0/3");
        mistakeJLabel.setFont(font18);
        
        lbNewGame = new JLabel("New Game");
        lbNewGame.setFont(font20);
        
        btnPrize = new FunctionButton(createResizedIcon("icons/prize.png", 30, 30));
        btnPrize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HistoryFrame historyFrame = new HistoryFrame(SudokuFrame.this);
                historyFrame.setVisible(true);
                SudokuFrame.this.setVisible(false);
            }
        });
        
        btnPauseGame = new FunctionButton(createResizedIcon("icons/pause.png", 20, 20));
        createPauseAction();
                
        btnSetting = new FunctionButton(createResizedIcon("icons/settings.png", 30, 30));
        btnSetting.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame setting = new JFrame();
                setting.setLayout(new FlowLayout());
                setting.setLocationRelativeTo(null);
                slider = new JSlider(-40, 6);
                slider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        sound.currentVolume = slider.getValue();
                        if (sound.currentVolume == -40) {
                            sound.currentVolume = -80;
                        }
                        sound.fc.setValue(sound.currentVolume);
                    }
                });
                volumeMute = new FunctionButton(createResizedIcon("sounds/volume.png", 30, 30));
                if (sound.isMute()==false) {  
                    volumeMute.setIcon(createResizedIcon("sounds/volume.png", 30, 30));
                }else{
                    volumeMute.setIcon(createResizedIcon("sounds/mute.png", 30, 30));
                }
                volumeMute.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sound.volumeMute(slider);
                        if (sound.isMute()==false) {  
                            volumeMute.setIcon(createResizedIcon("sounds/volume.png", 30, 30));
                        }else{
                            volumeMute.setIcon(createResizedIcon("sounds/mute.png", 30, 30));
                        }
                    }
                }); 
                
                setting.add(volumeMute);
                setting.add(slider);
                setting.pack();
                setting.setVisible(true);
            }
        });
        
        drop = new FunctionButton(createResizedIcon("icons/drop.png", 20, 20));
        drop.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenu sixBySixGame  = new JMenu("6 By 6 Game");
                JMenu nineByNineGame  = new JMenu("9 By 9 Game");
                JMenu twelveByTwelveGame  = new JMenu("12 By 12 Game");
                
                JMenuItem easyMenuItem6x6 = new JMenuItem("Easy");
                easyMenuItem6x6.addActionListener(new NewGameListener(SudokuPuzzleType.SIXBYSIX, 30, easyMenuItem6x6.getText()));
                JMenuItem mediumMenuItem6x6 = new JMenuItem("Medium");
                mediumMenuItem6x6.addActionListener(new NewGameListener(SudokuPuzzleType.SIXBYSIX, 30, mediumMenuItem6x6.getText()));
                JMenuItem hardMenuItem6x6 = new JMenuItem("Hard");
                hardMenuItem6x6.addActionListener(new NewGameListener(SudokuPuzzleType.SIXBYSIX, 30, hardMenuItem6x6.getText()));

                JMenuItem easyMenuItem9x9 = new JMenuItem("Easy");
                easyMenuItem9x9.addActionListener(new NewGameListener(SudokuPuzzleType.NINEBYNINE, 26, easyMenuItem9x9.getText()));
                JMenuItem mediumMenuItem9x9 = new JMenuItem("Medium");
                mediumMenuItem9x9.addActionListener(new NewGameListener(SudokuPuzzleType.NINEBYNINE, 26, mediumMenuItem9x9.getText()));
                JMenuItem hardMenuItem9x9 = new JMenuItem("Hard");
                hardMenuItem9x9.addActionListener(new NewGameListener(SudokuPuzzleType.NINEBYNINE, 26, hardMenuItem9x9.getText()));

                JMenuItem easyMenuItem12x12 = new JMenuItem("Easy");
                easyMenuItem12x12.addActionListener(new NewGameListener(SudokuPuzzleType.TWELVEBYTWELVE, 20, easyMenuItem12x12.getText()));
                JMenuItem mediumMenuItem12x12 = new JMenuItem("Medium");
                mediumMenuItem12x12.addActionListener(new NewGameListener(SudokuPuzzleType.TWELVEBYTWELVE, 20, mediumMenuItem12x12.getText()));
                JMenuItem hardMenuItem12x12 = new JMenuItem("Hard");
                hardMenuItem12x12.addActionListener(new NewGameListener(SudokuPuzzleType.TWELVEBYTWELVE, 20, hardMenuItem12x12.getText()));
                
                sixBySixGame.add(easyMenuItem6x6);
                sixBySixGame.add(mediumMenuItem6x6);
                sixBySixGame.add(hardMenuItem6x6);
                nineByNineGame.add(easyMenuItem9x9);
                nineByNineGame.add(mediumMenuItem9x9);
                nineByNineGame.add(hardMenuItem9x9);
                twelveByTwelveGame.add(easyMenuItem12x12);
                twelveByTwelveGame.add(mediumMenuItem12x12);
                twelveByTwelveGame.add(hardMenuItem12x12);


                popupMenu.add(sixBySixGame);
                popupMenu.add(nineByNineGame);
                popupMenu.add(twelveByTwelveGame);
                popupMenu.show(lbNewGame, 0, lbNewGame.getHeight());
            }
        });
        
        JPanel nullPanel1 = new JPanel();
        nullPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        nullPanel1.add(btnPrize);
        nullPanel1.add(btnSetting);
        JPanel nullPanel2 = new JPanel();
        nullPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        nullPanel2.add(lbNewGame);
        nullPanel2.add(drop);
        JPanel nullPanel3 = new JPanel();
        nullPanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
        nullPanel3.add(lTimerJLabel);
        nullPanel3.add(btnPauseGame);
        
        topPanel.add(btnPaint);
        topPanel.add(nullPanel2);
        topPanel.add(nullPanel1);
        topPanel.add(mistakeJLabel);
        topPanel.add(modelb);
        topPanel.add(nullPanel3);
        
        //tool panel
        JPanel bottomPanel = new JPanel();
        //bottomPanel.setPreferredSize(new Dimension(100,100));
        bottomPanel.setLayout(new GridLayout(2, 4, 50, 0));
        
        btnUndo = new JButton(createResizedIcon("icons/undo.png", 30, 30));
        createUndoAction();

        btnDelete = new JButton(createResizedIcon("icons/eraser.png", 30, 30));
        createDeleteAction();
        
        btnHint = new JButton(createResizedIcon("icons/bulb.png", 40, 40));
        createHintAction();
        
        btnNote = new JToggleButton(createResizedIcon("icons/pencil.png", 30, 30));
        createNoteAction();
        
        lbUndo = new JLabel("Undo");
        lbUndo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbUndo.setFont(font18);
        lbErase = new JLabel("Erase");
        lbErase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbErase.setFont(font18);
        lbPencil = new JLabel("Pencil off");
        lbPencil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPencil.setFont(font18);
        lbHint = new JLabel();
        lbHint.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbHint.setFont(font18);
        
        bottomPanel.add(btnUndo);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnNote);
        bottomPanel.add(btnHint);
        bottomPanel.add(lbUndo);
        bottomPanel.add(lbErase);
        bottomPanel.add(lbPencil);
        bottomPanel.add(lbHint);
 
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(sPanel,BorderLayout.CENTER);
        centerPanel.add(bottomPanel,BorderLayout.SOUTH);
                
         btnPaint.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(windowPanel, "Chọn màu nền", Color.WHITE);
                windowPanel.setBackground(color);
                bottomPanel.setBackground(color);
                centerPanel.setBackground(color);
                topPanel.setBackground(color);
                nullPanel1.setBackground(color);
                nullPanel2.setBackground(color);
                nullPanel3.setBackground(color);
                sPanel.setBackground(color);
                buttonSelectionPanel.setBackground(color);
            }
        });
        //Add this to frame
        windowPanel.add(centerPanel,BorderLayout.CENTER);
        windowPanel.add(topPanel,BorderLayout.NORTH);    
        windowPanel.add(buttonSelectionPanel,BorderLayout.SOUTH);
        this.add(windowPanel);
        //Use to create new game when openning game (defalut 9x9, font size 26, mode: easy)

        rebuildInterface(SudokuPuzzleType.NINEBYNINE, 26, DEFAULT_MODE);
        GameState savedGame = loadGame();
        if (savedGame != null) {
            System.out.println("Loaded GameState: " + savedGame);
            int confirm = JOptionPane.showOptionDialog(
                    frame,
                    "Do you want to continue your previous game?",
                    "Load Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null);
            if (confirm == JOptionPane.YES_OPTION) {
                SudokuPuzzleType puzzleType = determinePuzzleType(savedGame.getBoard());
                int fontSize = (puzzleType == SudokuPuzzleType.SIXBYSIX) ? 30 : (puzzleType == SudokuPuzzleType.NINEBYNINE) ? 26 : 20;
                rebuildInterface(puzzleType, fontSize, DEFAULT_MODE);
                playMusic("sounds/gamemusic.wav");
                sPanel.restoreGameState(savedGame);
            } else {
                rebuildInterface(SudokuPuzzleType.NINEBYNINE, 26, DEFAULT_MODE);
                playMusic("sounds/gamemusic.wav");
                // Hoặc kích thước khác nếu không tiếp tục
            }
        }

        // Add window listener to handle save game on close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        frame,
                        "Do you want to save your game before exiting?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null);
                if (confirm == JOptionPane.YES_OPTION) {
                    GameState gameState = new GameState(sPanel.getBoard(), sPanel.getPuzzle().getMutable(), sPanel.getSecondsPassed(), sPanel.getMistake(), sPanel.getHint(), sPanel.getNoteValues(), sPanel.getMoveHistory(), sPanel.getPuzzle().getSolution());
                    saveGame(gameState);
                }
                System.exit(0);
            }
        });

    }

  
    private void createUndoAction() {
        btnUndo.addActionListener((ActionEvent e) -> {
            playSound("sounds/button.wav");
            sPanel.undoMove();
        });
    }
    private void createPauseAction() {
        btnPauseGame.addActionListener((ActionEvent e) -> {
            playSound("sounds/pausing.wav");
            if (sound.isMute()==false) {
                sound.stop();
            }
            btnPauseGame.setIcon(createResizedIcon("icons/play.png", 20, 20));
            if (sPanel.getTimerState()) {
                sPanel.pauseTimer();
                
                // Create a JOptionPane with a custom OK button
                JOptionPane optionPane = new JOptionPane(
                    "Game is pausing",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[] {"OK"},  // Custom options with only an OK button
                    "OK"  // Default button
                );

                // Create a JDialog from the JOptionPane
                JDialog dialog = optionPane.createDialog(this, "Pause");
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                // Add an action listener to the OK button
                optionPane.addPropertyChangeListener(evt -> {
                    String prop = evt.getPropertyName();

                    if (dialog.isActive()
                            && (evt.getSource() == optionPane)
                            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                        // Resume the timer when OK is clicked
                        sPanel.resumeTimer();
                        dialog.dispose();
                        playSound("sounds/button.wav");
                        if (sound.isMute()==false) {
                            playMusic("sounds/gamemusic.wav");
                        }
                        btnPauseGame.setIcon(createResizedIcon("icons/pause.png", 20, 20));
                    }
                });

                dialog.setVisible(true);
            }
        });
    }
    public void updateMistakeLabel(int mistakes) {
           mistakeJLabel.setText("Mistakes: " + mistakes + "/3");
    }
    
    public void updateTimerLabel(String time) {
        lTimerJLabel.setText(time);
    }
    
    public void createDeleteAction(){
        btnDelete.addActionListener((ActionEvent e) -> {
            playSound("sounds/button.wav");
            sPanel.deleteValue();
        });
    }

    public void createHintAction(){
        btnHint.addActionListener((ActionEvent e) -> {
            playSound("sounds/hint.wav");
            if (sPanel.getHint() < 5) {
                sPanel.autoFill();
            }else{
                JOptionPane.showMessageDialog(sPanel, "Đã hết số lần gợi ý");
            }
        });
    }
    
    public void createNoteAction(){
        btnNote.addActionListener((ActionEvent e) -> {
            playSound("sounds/button.wav");
            if (btnNote.isSelected()) {
                noteStatus = true;
                lbPencil.setText("Pencil On");
            }else{
                noteStatus = false;
                lbPencil.setText("Pencil Off");
            }
        });
    }

    public boolean isNoteStatus() {
        return noteStatus;
    }
    
    public void updateHint(int hint){
        lbHint.setText("Hint: " + hint + "/5");
    }
    
    
    private SudokuPuzzleType determinePuzzleType(String[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        if (rows == 6 && cols == 6) {
            return SudokuPuzzleType.SIXBYSIX;
        } else if (rows == 9 && cols == 9) {
            return SudokuPuzzleType.NINEBYNINE;
        } else if (rows == 12 && cols == 12) {
            return SudokuPuzzleType.TWELVEBYTWELVE;
        } else {
            throw new IllegalArgumentException("Unsupported puzzle size");
        }
    }
    
    public void saveGame(GameState gameState) {
        try {
            File directory = new File("Appdata");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            FileOutputStream fileOut = new FileOutputStream("Appdata/gameState.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gameState);
            out.close();
            fileOut.close();
            System.out.println("Game state saved in Appdata/gameState.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public GameState loadGame() {
        GameState gameState = null;
        try (FileInputStream fileIn = new FileInputStream("Appdata/gameState.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            gameState = (GameState) in.readObject();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return gameState;
    }

    
    // History 
    public String getCurrentMode() {
        return currentMode;
    }
    
     public String getCurrentPuzzleType() {
        return currentPuzzleType;
    }
    
    public void saveGameHistory(int userId, String mode, int totalTime, String puzzleType) {
           String sql = "INSERT INTO History (user_id, date, mode, total_time, puzzle_type) VALUES (?, GETDATE(), ?, ?, ?)";

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, mode);
            stmt.setInt(3, totalTime);
            stmt.setString(4, puzzleType);
            stmt.executeUpdate();
            System.out.println("Game history saved successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save game history.");
        }
    }
    
//    //Like above
   public void rebuildInterface(SudokuPuzzleType puzzleType, int fontSize, String mode) {
        currentMode = mode; // Cập nhật currentMode
        currentPuzzleType = puzzleType.toString(); // Cập nhật currentPuzzleType
        SudokuPuzzle generatedPuzzle = new SudokuGenerator().generateRandomSudoku(puzzleType, mode);
        modelb.setText(mode);
        sPanel.newSudokuPuzzle(generatedPuzzle);
        sPanel.setFontSize(fontSize);
        sPanel.resetMistakes();
        sPanel.resetHint();
        sPanel.resetTimer();
        updateHint(sPanel.getHint());
        buttonSelectionPanel.removeAll();
        for (String value : generatedPuzzle.getValidValues()) {
            JButton b = new JButton(value);
            Font numberFont = new Font("Times New Roman", Font.PLAIN,40);
            b.setFont(numberFont);
            b.setContentAreaFilled(false);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setFocusable(false);
            b.setForeground(Color.BLUE);
            b.setBackground(Color.white);
            b.setPreferredSize(new Dimension(40, 50));
            b.addActionListener(sPanel.new NumActionListener());
            b.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(value), value);
            b.getActionMap().put(value, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    b.doClick();
                }
            });
            buttonSelectionPanel.add(b);
        }
        
                
        sPanel.repaint();
        sPanel.startTimer();
        sPanel.resetMoveHistory();
        buttonSelectionPanel.revalidate();
        buttonSelectionPanel.repaint();        
    }

    //Create new game with option(SudokuPuzzleType(6x6,9x9,12x12))
    private class NewGameListener implements ActionListener {

        private final SudokuPuzzleType puzzleType;
        private final int fontSize;
        private final String mode;

        public NewGameListener(SudokuPuzzleType puzzleType, int fontSize, String mode) {
            this.puzzleType = puzzleType;
            this.fontSize = fontSize;
            this.mode = mode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rebuildInterface(puzzleType, fontSize, mode);
        }
    }
    
    public void playMusic(String path){
        sound.setFile(path);
        sound.play();
        sound.loop();
    }
    
    public void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
    
   private static ImageIcon createResizedIcon(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    
    //Main: use to run this frame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageIcon logo = new ImageIcon("icons/icon2.png");
            SudokuFrame frame = new SudokuFrame(1);
            frame.setIconImage(logo.getImage());
            frame.setVisible(true);
            frame.setResizable(false);
        });
    }
}
