
package com.mycompany.sudokuswing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import Database.DatabaseConnector;
import Database.GameState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class SudokuPanel extends JPanel{
    
    private SudokuFrame frame;
    
    //Attribute
    private SudokuPuzzle puzzle;
	private int currentlySelectedCol;
	private int currentlySelectedRow;
	private int usedWidth;
	private int usedHeight;
	private int fontSize;
    private int mistake;
    private final Timer timer;
    private boolean timerIsRunning = false;
    private int secondsPassed = 0;
    private final Stack<int[]> moveHistory = new Stack<>();
    private int hint;
    private int user_id;
    private String[][] noteValues;

	//Contructor
	public SudokuPanel(Integer ID) {
                this.user_id = ID;
		this.setPreferredSize(new Dimension(540,450));
		this.addMouseListener(new SudokuPanelMouseAdapter());
		this.puzzle = new SudokuGenerator().generateRandomSudoku(SudokuPuzzleType.NINEBYNINE, "Easy");
        // Initialize the timer
        this.timer = new Timer(1000, (ActionEvent e) -> {
            secondsPassed++;
            updateTimerDisplay();
        });
		currentlySelectedCol = -1;
		currentlySelectedRow = -1;
		usedWidth = 0;
		usedHeight = 0;
		fontSize = 32;
        noteValues = new String[puzzle.getNumRows()][puzzle.getNumColumns()];
                
	}
	
	public SudokuPanel(SudokuPuzzle puzzle) {
		this.setPreferredSize(new Dimension(540,450));
		this.addMouseListener(new SudokuPanelMouseAdapter());
		this.puzzle = puzzle;
        // Initialize the timer
        this.timer = new Timer(1000, (ActionEvent e) -> {
            secondsPassed++;
            updateTimerDisplay();
        });
		currentlySelectedCol = -1;
		currentlySelectedRow = -1;
		usedWidth = 0;
		usedHeight = 0;
		fontSize = 32;   
                
	}
        
	//Method
        public String[][] getBoard(){
            return puzzle.getBoard();
        }
        
        public SudokuPuzzle getPuzzle() {
            return puzzle;
        }

        public void setPuzzle(SudokuPuzzle puzzle) {
            this.puzzle = puzzle;
        }

        public int getMistake() {
            return mistake;
        }

        public void setMistake(int mistake) {
            this.mistake = mistake;
        }

        public int getSecondsPassed() {
            return secondsPassed;
        }

        public void setSecondsPassed(int secondsPassed) {
            this.secondsPassed = secondsPassed;
        }
        
	public void newSudokuPuzzle(SudokuPuzzle puzzle) {
		this.puzzle = puzzle;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
        
        public void setFrame(SudokuFrame frame) {
                this.frame = frame;
        }
        public void resetHint(){
            this.hint = 0;
        }
        public int getCurrentlySelectedCol() {
            return currentlySelectedCol;
        }

        public int getCurrentlySelectedRow() {
            return currentlySelectedRow;
        }

        public int getHint() {
            return hint;
        }

        public Timer getTimer(){
            return timer;
        }

        public void restoreGameState(GameState gameState) {
            this.puzzle.setBoard(gameState.getBoard());
            this.puzzle.setMutable(gameState.getMutable());
            this.puzzle.setSolution(gameState.getSolution());
            this.secondsPassed = gameState.getSecondsPassed();
            this.mistake = gameState.getMistake();
            this.hint = gameState.getHint();
            this.noteValues = gameState.getNoteValues();
            this.moveHistory.addAll(gameState.getMoveHistory());

            updateTimerDisplay();
            frame.updateMistakeLabel(mistake);
            frame.updateHint(hint);

            this.revalidate();
            this.repaint();
            frame.revalidate();
            frame.repaint();
        }

        public boolean getTimerState(){
            return timerIsRunning;
        }
        public void resetMistakes() {
            this.mistake = 0;
            if (frame != null) {
                frame.updateMistakeLabel(mistake);
            }
        }
        
        public void startTimer() {
            if (!timerIsRunning) {
                timer.start();
                timerIsRunning = true;
            }
        }
        
        public String[][] getNoteValues() {
            return noteValues;
        }

        public Stack<int[]> getMoveHistory() {
            return moveHistory;
        }

        public void pauseTimer() {
            if (timerIsRunning) {
                timer.stop();
                timerIsRunning = false;
            }
        }

        public void resumeTimer() {
            if (!timerIsRunning) {
                startTimer();
            }
        }

        public void resetTimer() {
            pauseTimer();
            timerIsRunning = false;
            secondsPassed = 0;
            updateTimerDisplay();
        }
        private void updateTimerDisplay() {
            int minutes = secondsPassed / 60;
            int seconds = secondsPassed % 60;
            String timeFormatted = String.format("%02d:%02d", minutes, seconds);
            if (frame != null) {
                frame.updateTimerLabel(timeFormatted); 
            }
        }
        
        public void undoMove() {
        if (!moveHistory.isEmpty()) {
            int[] lastMove = moveHistory.pop();
            int row = lastMove[0]; 
            int col = lastMove[1];

            puzzle.board[row][col] = "";
            repaint();
                }
        }
        
        public void resetMoveHistory() {
            moveHistory.clear();
        }
        
        public void deleteValue(){
            if (currentlySelectedRow != -1 && currentlySelectedCol != -1) {
                if(puzzle.getBoard()[currentlySelectedRow][currentlySelectedCol].equals("")){
                JOptionPane.showMessageDialog(this, "Cell trống");
                frame.playSound("sounds/button.wav");
                }else{
                if(puzzle.getCellColor(currentlySelectedRow, currentlySelectedCol).equals(Color.RED)){
                    puzzle.getBoard()[currentlySelectedRow][currentlySelectedCol] = "";
                }else if (puzzle.getCellColor(currentlySelectedRow, currentlySelectedCol).equals(Color.BLUE)||
                        puzzle.getCellColor(currentlySelectedRow, currentlySelectedCol).equals(Color.GREEN)) {
                    JOptionPane.showMessageDialog(this, "Cell đã điền đúng, không thể xóa");
                    frame.playSound("sounds/button.wav");
                }
                else{
                    JOptionPane.showMessageDialog(this, "Cell đề bài");
                    frame.playSound("sounds/button.wav");
                }
            }
            repaint();
            }else{
                JOptionPane.showMessageDialog(this, "Chưa chọn ô để xóa");
                frame.playSound("sounds/button.wav");
            }
            
            
        }
        
        public void autoFill(){
            // Retrieve empty slots and convert to a stack for random access
            Stack<int[]> emptySlots = puzzle.emptySlot();

            // Reset currently selected row and column if they are set
            if (currentlySelectedRow != -1 && currentlySelectedCol != -1) {
                currentlySelectedRow = -1;
                currentlySelectedCol = -1;
            }

            Random rand = new Random();
            
            for (String value : puzzle.getValidValues()) {
                if (!emptySlots.isEmpty()) {
                    // Select a random index from the list of empty slots
                    int randomIndex = rand.nextInt(emptySlots.size());
                    int[] emptySlot = emptySlots.remove(randomIndex); // Remove the slot to prevent reuse

                    int row = emptySlot[0];
                    int col = emptySlot[1];

                    // Try to place the value in the empty slot if it's a valid move
                    if (puzzle.isValidMove(row, col, value) && hint < 5) {
                        puzzle.board[row][col] = value;
                        puzzle.setCellColor(row, col, Color.GREEN);
                        repaint();
                        hint++;
                        frame.updateHint(hint);
                        return; // Exit the method after filling one slot
                    }
                } else {
                    return; // No empty slots left to fill
                }
            }
        }

        
	    //Use to draw grid layout
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Calculate width and height of one box using panel width and height / numCols and numRows
            int slotWidth = this.getWidth() / puzzle.getNumColumns();
            int slotHeight = this.getHeight() / puzzle.getNumRows();

            usedWidth = (this.getWidth() / puzzle.getNumColumns()) * puzzle.getNumColumns();
            usedHeight = (this.getHeight() / puzzle.getNumRows()) * puzzle.getNumRows();

            g2d.setColor(new Color(1.0f, 1.0f, 1.0f)); // white
            g2d.fillRect(0, 0, usedWidth, usedHeight);
            g2d.setColor(new Color(0.0f, 0.0f, 0.0f));

            // Draw vertical lines
            for (int x = 0; x <= usedWidth; x += slotWidth) {
                if ((x / slotWidth) % puzzle.getBoxWidth() == 0) {
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(x, 0, x, usedHeight);
                } else {
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(x, 0, x, usedHeight);
                }
            }

            // Draw horizontal lines
            for (int y = 0; y <= usedHeight; y += slotHeight) {
                if ((y / slotHeight) % puzzle.getBoxHeight() == 0) {
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(0, y, usedWidth, y);
                } else {
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(0, y, usedWidth, y);
                }
            }

            // Set font for cell values
            Font f = new Font("Times New Roman", Font.PLAIN, fontSize);
            g2d.setFont(f);
            FontRenderContext fContext = g2d.getFontRenderContext();

            // Draw cell values and notes
            for (int row = 0; row < puzzle.getNumRows(); row++) {
                for (int col = 0; col < puzzle.getNumColumns(); col++) {
                    String cellValue = puzzle.getValue(row, col);
                    if (!cellValue.equals("")) {
                        Color cellColor = puzzle.getCellColor(row, col);
                        if (cellColor != null) {
                            g2d.setColor(cellColor);
                        } else {
                            g2d.setColor(Color.BLACK);
                            puzzle.setCellColor(row, col, Color.BLACK); // Default color
                        }

                        // Calculate x and y positions for drawing the string
                        int textWidth = (int) f.getStringBounds(cellValue, fContext).getWidth();
                        int textHeight = (int) f.getStringBounds(cellValue, fContext).getHeight();
                        g2d.drawString(cellValue, (col * slotWidth) + ((slotWidth / 2) - (textWidth / 2)),
                                (row * slotHeight) + ((slotHeight / 2) + (textHeight / 2)));
                    }

                    // Draw note text in the top-left corner
                    String noteValue = noteValues[row][col]; // Assuming you have a method to get note value
                    if (noteValue != null && !noteValue.isEmpty()) {
                        Font noteFont = new Font("Times New Roman", Font.PLAIN, fontSize / 2); // Smaller font for notes
                        g2d.setFont(noteFont);
                        g2d.setColor(Color.GRAY); // Different color for notes

                        // Split the noteValue into lines that fit within the cell width
                        List<String> lines = wrapText(noteValue, noteFont, slotWidth, g2d);
                        int lineHeight = g2d.getFontMetrics(noteFont).getHeight();
                        for (int i = 0; i < lines.size(); i++) {
                            g2d.drawString(lines.get(i), col * slotWidth + 2, row * slotHeight + (i + 1) * lineHeight);
                        }

                        g2d.setFont(f); // Reset to original font
                    }
                }
            }

            // Highlight selected cell
            if (currentlySelectedCol != -1 && currentlySelectedRow != -1) {
                g2d.setColor(new Color(0.0f, 0.0f, 1.0f, 0.3f));
                g2d.fillRect(currentlySelectedCol * slotWidth, currentlySelectedRow * slotHeight, slotWidth, slotHeight);
            }
        }

        // Helper method to wrap text into lines that fit within the specified width
        private List<String> wrapText(String text, Font font, int width, Graphics2D g2d) {
            List<String> lines = new ArrayList<>();
            FontRenderContext frc = g2d.getFontRenderContext();
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line + word + " ";
                if (g2d.getFontMetrics(font).stringWidth(testLine) < width) {
                    line.append(word).append(" ");
                } else {
                    lines.add(line.toString().trim());
                    line = new StringBuilder(word + " ");
                }
            }

            if (line.length() > 0) {
                lines.add(line.toString().trim());
            }

            return lines;
        }


        public void gameOver(int mistake){
            if (mistake == 3) {
                JOptionPane.showMessageDialog(this, "Game Over!", "Error", JOptionPane.OK_OPTION);
                playAgain();
                this.mistake = 0; // Reset mistake
                if (frame != null) {
                    frame.updateMistakeLabel(this.mistake); 
                }
            }
        }
       
        private void updateCellColor(int row, int col, Color color) {
            puzzle.setCellColor(row, col, color);
        }
        
        
        public void messageFromNumActionListener(String buttonValue) {          
                // Check if the currently selected row and column are set
            if (currentlySelectedRow == -1 || currentlySelectedCol == -1) { 
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a cell before making a move.",
                    "No Cell Selected",
                    JOptionPane.WARNING_MESSAGE
                );
                frame.playSound("sounds/button.wav");
                return; // Exit the method as no cell is selected
            }
            if (frame.isNoteStatus()) {
                if (noteValues[currentlySelectedRow][currentlySelectedCol] == null) {
                    noteValues[currentlySelectedRow][currentlySelectedCol] = buttonValue;
                } else {
                    String currentNotes = noteValues[currentlySelectedRow][currentlySelectedCol];
                    String[] notesArray = currentNotes.split(" ");
                    boolean valueExists = false;
                    
                    for (int i = 0; i < notesArray.length; i++) {
                        if (notesArray[i].equals(buttonValue)) {
                            notesArray[i] = buttonValue; // Overwrite the existing value
                            valueExists = true;
                            break;
                        }
                    }
                    if (!valueExists) {
                        currentNotes += " " + buttonValue ;
                        noteValues[currentlySelectedRow][currentlySelectedCol] = currentNotes;
                    } else {
                        // Join the array back into a string with space-separated values
                        noteValues[currentlySelectedRow][currentlySelectedCol] = String.join(" ", notesArray);
                    }
                }
            
                // Ensure note values do not exceed the cell limit (example: 9 characters)
                String updatedNotes = noteValues[currentlySelectedRow][currentlySelectedCol];
                if (updatedNotes.length() > puzzle.getNumRows()) {
                    noteValues[currentlySelectedRow][currentlySelectedCol] = updatedNotes.substring(0, 9);
                }
            }
            else{
                 //Check user make right choice or not
                if (!puzzle.getSolutionValue(currentlySelectedRow, currentlySelectedCol).equals(buttonValue)) {
                    frame.playSound("sounds/wrong.wav");
                    mistake++;
                    frame.updateMistakeLabel(mistake);
                    updateCellColor(currentlySelectedRow, currentlySelectedCol, Color.RED);
                    puzzle.getBoard()[currentlySelectedRow][currentlySelectedCol] = buttonValue;
                    moveHistory.push(new int[]{currentlySelectedRow, currentlySelectedCol});
                    noteValues[currentlySelectedRow][currentlySelectedCol] = "";
                    gameOver(mistake);
                }
                else{
                    frame.playSound("sounds/correct.wav");
                    puzzle.makeMove(currentlySelectedRow, currentlySelectedCol, buttonValue, true);
                    moveHistory.push(new int[]{currentlySelectedRow, currentlySelectedCol});
                    updateCellColor(currentlySelectedRow, currentlySelectedCol, Color.BLUE);
                    noteValues[currentlySelectedRow][currentlySelectedCol] = "";
                    if (puzzle.boardFull()) {
                        frame.saveGameHistory(user_id, frame.getCurrentMode(), secondsPassed, frame.getCurrentPuzzleType());
                        updateUserScore(user_id);
                        int option = JOptionPane.showOptionDialog(
                        this,
                        "Bạn đã chiến thắng! Bạn có muốn chơi lại không?",
                        "Chúc mừng",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Play Again", "Exit"},
                        "Play Again"
                        );       
                        if (option == JOptionPane.YES_OPTION) {
                            frame.playSound("sounds/button.wav");
                            playAgain();
                        } else {
                            System.exit(0);
                        }
                    }
                }
            }
            repaint();
        }
        
        private void playAgain() {
            SudokuPuzzle newPuzzle = new SudokuGenerator().generateRandomSudoku(SudokuPuzzleType.NINEBYNINE, "Medium");
            newSudokuPuzzle(newPuzzle);
            currentlySelectedCol = -1;
            currentlySelectedRow = -1;
            resetTimer();
            resetMoveHistory();
            startTimer();
            repaint();
        
    }
        private void saveGameHistory(int userId, String mode, int totalTime) {
            String sql = "INSERT INTO History (user_id, date, mode, total_time) VALUES (?, GETDATE(), ?, ?)";

            try (Connection con = DatabaseConnector.getConnection();
                 PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.setString(2, mode);
                stmt.setInt(3, totalTime);
                stmt.executeUpdate();
                System.out.println("Game history saved successfully.");

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to save game history.");
            }
        }

        private void updateUserScore(int userId) {
        String sql = "IF EXISTS (SELECT 1 FROM UserScores WHERE user_id = ?) " +
                     "BEGIN " +
                     "    UPDATE UserScores SET score = score + 100 WHERE user_id = ?; " +
                     "END " +
                     "ELSE " +
                     "BEGIN " +
                     "    INSERT INTO UserScores (user_id, score) VALUES (?, 100); " +
                     "END";

        try (Connection con = DatabaseConnector.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
            System.out.println("Score updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update score.");
        }
    } 
                
                
	public class NumActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            String buttonValue = ((JButton) e.getSource()).getText();
            messageFromNumActionListener(buttonValue);	
		}
	}
	
	private class SudokuPanelMouseAdapter extends MouseInputAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				int slotWidth = usedWidth/puzzle.getNumColumns();
				int slotHeight = usedHeight/puzzle.getNumRows();
				currentlySelectedRow = e.getY() / slotHeight;
				currentlySelectedCol = e.getX() / slotWidth;
				e.getComponent().repaint();
			}
		}
	}
}
