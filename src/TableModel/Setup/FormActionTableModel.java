/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TableModel.Setup;

import BO.Setup.FormActionBO;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class FormActionTableModel extends AbstractTableModel {

    private final String[] columnNames
            = {"Sr.", "ID", "Description", "Form Name", "Active", "Remarks"};
    private final Object[][] data;

    public FormActionTableModel(List<FormActionBO> listformAction) {
        data = new Object[listformAction.size()][columnNames.length];
        int row = 0;

        for (int i = 0; i < listformAction.size(); i++) {

            FormActionBO formaction = listformAction.get(i);

            data[row][0] = i + 1;
            data[row][1] = formaction.getActionId();
            data[row][2] = formaction.getActionDescription();
            data[row][3] = formaction.getFormDescription();
            data[row][4] = formaction.getActive();
            data[row][5] = formaction.getRemarks();
            row++;
        }
//            if(currentInfo.getQuestionResult().equalsIgnoreCase("Y")){
//                data[row][2] = true;
//            }
//            else if(currentInfo.getQuestionResult().equalsIgnoreCase("N")){
//                data[row][3] = true;
//            }

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {

        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
//    @Override
//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 2 || col == 3) {
            return true;
        }
        return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    private final boolean DEBUG = false;

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

}
