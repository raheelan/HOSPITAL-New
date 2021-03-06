/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPD.TableModel;

import Library.TableModel.*;
import Library.BO.BookIssue;
import OPD.BO.BOMedicineFrequencySetup;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class chkMedata extends AbstractTableModel {

    private final String[] columnNames
            = {"Sr.", "Book Name", "Status Id","Issue Date",
                "Return Date","Book Collect By"};
    private final Object[][] data;

    public chkMedata(List<BOMedicineFrequencySetup> LstBook) {
        data = new Object[LstBook.size()][columnNames.length];
        int row = 0;

        for (int i = 0; i < LstBook.size(); i++) {
           
            BOMedicineFrequencySetup bookIss = LstBook.get(i);
            
            data[row][0] = i+1;
            data[row][1] = bookIss.getId();
            data[row][2] = bookIss.getDescription();
            data[row][3] = bookIss.getCount();
            data[row][4] = bookIss.getActionId();
            data[row][5] = bookIss.getUrduText();
            row++;
        }
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
