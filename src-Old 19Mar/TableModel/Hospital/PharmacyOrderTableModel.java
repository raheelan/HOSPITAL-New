/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TableModel.Hospital;
 
import BO.Hospital.MedicineOrderEntry;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pacslink
 */
public class PharmacyOrderTableModel extends AbstractTableModel {

    private String[] columnNames = {"Sr.", "Item Name", "Quantity", "Price",
        "Amount", "Payable"};

    private Object[][] data;

    public PharmacyOrderTableModel(List lovs) {
        data = new Object[lovs.size()][columnNames.length];

        for (int i = 0; i < lovs.size(); i++) {
            MedicineOrderEntry pharmacyOrder = (MedicineOrderEntry) lovs.get(i);
            data[i][0] = "" + (i + 1);
            data[i][1] = pharmacyOrder.getItemDescription();
            data[i][2] = pharmacyOrder.getQuantity();
            data[i][3] = pharmacyOrder.getSalePrice();
            String totalPrice = "";
            try {
                totalPrice = "" + Math.round(Float.parseFloat(pharmacyOrder.getSalePrice())
                        * Integer.parseInt(pharmacyOrder.getQuantity()));
            } catch (Exception e) {
                e.printStackTrace();
                totalPrice = "0";
            }
            data[i][4] = totalPrice;
            data[i][5] = "" + (Integer.parseInt(totalPrice)
                    - (Integer.parseInt(totalPrice)
                    * (pharmacyOrder.getDiscount() / 100)));
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return false;

    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    private boolean DEBUG = false;

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
