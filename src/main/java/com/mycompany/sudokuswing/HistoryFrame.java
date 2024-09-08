/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sudokuswing;

/**
 *
 * @author GiapKun
 */
import Database.DatabaseConnector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Vector;

public class HistoryFrame extends JFrame {
    private JTable historyTable;
    private SudokuFrame parentFrame;
    private JButton filterButton;
    private JComboBox<String> filterComboBox;

    public HistoryFrame(SudokuFrame parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("Game History");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Game History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Bảng lịch sử
        historyTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // Tùy chọn lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterComboBox = new JComboBox<>(new String[]{"All", "Easy", "Medium", "Hard"});
        filterButton = new JButton("Filter");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHistoryData((String) filterComboBox.getSelectedItem());
            }
        });
        filterPanel.add(new JLabel("Filter by Mode:"));
        filterPanel.add(filterComboBox);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.SOUTH);

        loadHistoryData("All");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentFrame.setVisible(true);
                dispose();
            }
        });
    }

    private void loadHistoryData(String mode) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("User ID");
        model.addColumn("Date");
        model.addColumn("Mode");
        model.addColumn("Total Time");
        model.addColumn("Puzzle Type");

        String query = "SELECT * FROM History";
        if (!mode.equals("All")) {
            query += " WHERE mode = '" + mode + "'";
        }

        try (Connection con = DatabaseConnector.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("user_id"));
                row.add(rs.getString("date"));
                row.add(rs.getString("mode"));
                row.add(rs.getString("total_time"));
                row.add(rs.getString("puzzle_type"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        historyTable.setModel(model);
        styleTable();
    }

    private void styleTable() {
        historyTable.setFont(new Font("Serif", Font.PLAIN, 16));
        historyTable.setRowHeight(30);

        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Serif", Font.BOLD, 18));
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);

        historyTable.setGridColor(Color.LIGHT_GRAY);
        historyTable.setSelectionBackground(Color.LIGHT_GRAY);
        historyTable.setSelectionForeground(Color.BLACK);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuFrame parentFrame = new SudokuFrame(1);
            HistoryFrame historyFrame = new HistoryFrame(parentFrame);
            historyFrame.setVisible(true);
        });
    }
}
