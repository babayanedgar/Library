/*
 * Copyright (c) 2025. Author - Edgar Babayan
 *
 */

package utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.*;

public class BubbleSort {
    private static int currentSortColumn = -1; // Track the currently sorted column
    private static boolean isAscending = true; // Track the sorting order
    private static List<Object[]> originalData; // Store the original unsorted data

    public static void sortTable(JTable table, int columnIndex) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();

        // Store the original data if not already stored
        if (originalData == null) {
            originalData = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                Object[] row = new Object[model.getColumnCount()];
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row[j] = model.getValueAt(i, j);
                }
                originalData.add(row);
            }
        }

        // If the same column is clicked again, cycle through sorting states
        if (currentSortColumn == columnIndex) {
            if (isAscending) {
                // Switch to descending
                isAscending = false;
            } else {
                // Switch to unsorted
                currentSortColumn = -1;
                isAscending = true;
                restoreOriginalData(model);
                updateHeaderRenderer(table, columnIndex, ""); // Clear the mark
                return;
            }
        } else {
            // If a different column is clicked, sort in ascending order by default
            currentSortColumn = columnIndex;
            isAscending = true;
        }

        // Convert table data to a list of rows for sorting
        Object[][] rows = new Object[rowCount][];
        for (int i = 0; i < rowCount; i++) {
            rows[i] = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                rows[i][j] = model.getValueAt(i, j);
            }
        }

        // Sort the rows using Bubble Sort
        for (int i = 0; i < rowCount - 1; i++) {
            for (int j = 0; j < rowCount - i - 1; j++) {
                Comparable<Object> value1 = (Comparable<Object>) rows[j][columnIndex];
                Comparable<Object> value2 = (Comparable<Object>) rows[j + 1][columnIndex];
                int comparisonResult = value1.compareTo(value2);

                // Apply sorting order (ascending or descending)
                if ((isAscending && comparisonResult > 0) || (!isAscending && comparisonResult < 0)) {
                    // Swap rows
                    Object[] temp = rows[j];
                    rows[j] = rows[j + 1];
                    rows[j + 1] = temp;
                }
            }
        }

        // Update the table model with the sorted data
        model.setRowCount(0); // Clear the table
        for (Object[] row : rows) {
            model.addRow(row);
        }

        // Update the header renderer to show the sorting mark
        String mark = isAscending ? " ↑" : " ↓";
        updateHeaderRenderer(table, columnIndex, mark);
    }

    private static void restoreOriginalData(DefaultTableModel model) {
        model.setRowCount(0); // Clear the table
        for (Object[] row : originalData) {
            model.addRow(row);
        }
    }

    private static void updateHeaderRenderer(JTable table, int columnIndex, String mark) {
        JTableHeader header = table.getTableHeader();
        TableColumn column = header.getColumnModel().getColumn(columnIndex);
        String columnName = column.getHeaderValue().toString().replace(" ↑", "").replace(" ↓", "");
        column.setHeaderValue(columnName + mark);
        header.repaint();
    }
}