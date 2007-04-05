/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ui.screen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import chrriis.dj.tweak.data.JarFileInfo;
import chrriis.dj.tweak.data.VMArgumentsInfo;
import chrriis.dj.tweak.ui.TableSorter;

/**
 * @author Christopher Deckers
 */
public class VMArgumentsPanel extends JPanel {

  protected List<VMArgumentsInfo> vmArgumentsInfoList;
  protected JTable table;
  protected TableModel tableModel;
  protected TableSorter tableSorter;
  protected JButton addButton;
  protected JButton editButton;
  protected JButton removeButton;

  protected JarFileInfo jarFileInfo;
  
  public VMArgumentsPanel() {
    super(new BorderLayout(0, 0));
    JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
    centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    tableModel = new AbstractTableModel() {
      public int getColumnCount() {
        return 3;
      }
      public int getRowCount() {
        return vmArgumentsInfoList.size();
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        VMArgumentsInfo vmArgumentsInfo = vmArgumentsInfoList.get(rowIndex);
        switch (columnIndex) {
          case -1: return vmArgumentsInfo;
          case 0: return vmArgumentsInfo.getVendor();
          case 1: return vmArgumentsInfo.getVersion();
          case 2: return vmArgumentsInfo.getArguments();
        }
        return null;
      }
      @Override
      public String getColumnName(int column) {
        switch(column) {
          case 0: return "Vendor";
          case 1: return "Version";
          case 2: return "Arguments";
        }
        return null;
      }
      @Override
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return jarFileInfo != null && !jarFileInfo.isSigned();
      }
      @Override
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        VMArgumentsInfo vmArgumentsInfo = (VMArgumentsInfo)getValueAt(rowIndex, -1);
        String newValue = aValue.toString().trim();
        String oldValue;
        switch(columnIndex) {
          case 0:
            oldValue = vmArgumentsInfo.getVendor();
            break;
          case 1:
            oldValue = vmArgumentsInfo.getVersion();
            break;
          case 2:
            oldValue = vmArgumentsInfo.getArguments();
            break;
          default:
            return;
        }
//        newValue = newValue.replace(" ", "-");
        if(oldValue.equals(newValue)) {
          return;
        }
        VMArgumentsPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        switch(columnIndex) {
          case 0:
            vmArgumentsInfo.setVendor(newValue);
            break;
          case 1:
            vmArgumentsInfo.setVersion(newValue);
            break;
          case 2:
            vmArgumentsInfo.setArguments(newValue);
            break;
        }
        adjustTable();
        restoreState();
      }
    };
    table = new JTable() {
      @Override
      public boolean getScrollableTracksViewportHeight() {
        return getParent().getHeight() > getPreferredSize().height;
      }
    };
    table.setDropTarget(null);
    tableSorter = new TableSorter(tableModel);
    table.setModel(tableSorter);
    tableSorter.setTableHeader(table.getTableHeader());
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);
    columnModel.getColumn(1).setPreferredWidth(50);
    columnModel.getColumn(2).setPreferredWidth(300);
    adjustTable();
    centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    centerPanel.add(new JLabel("Note: \"Vendor\" and \"Version\" are regular expressions. When undefined, they match all vendors/versions."), BorderLayout.SOUTH);
    add(centerPanel, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    addButton = new JButton("Add");
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        VMArgumentsPanel.this.firePropertyChange("jarModified", false, true);
        VMArgumentsInfo vmArgumentsInfo = new VMArgumentsInfo("", "", "");
        vmArgumentsInfoList.add(vmArgumentsInfo);
        adjustTable();
        int rowCount = table.getRowCount();
        for(int i=0; i<rowCount; i++) {
          if(table.getValueAt(i, -1) == vmArgumentsInfo) {
            table.getSelectionModel().setSelectionInterval(i, i);
            table.editCellAt(i, 0);
            Component component = table.getEditorComponent();
            if(component instanceof JTextComponent) {
              component.requestFocus();
            }
          }
        }
      }
    });
    buttonPanel.add(addButton);
    editButton = new JButton("Edit");
    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int leadRow = table.getSelectionModel().getAnchorSelectionIndex();
        int leadColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        if(leadRow != -1 && leadColumn != -1) {
          table.editCellAt(leadRow, leadColumn);
          Component component = table.getEditorComponent();
          if(component instanceof JTextComponent) {
            ((JTextComponent)component).selectAll();
            component.requestFocus();
          }
        }
      }
    });
    buttonPanel.add(editButton);
    removeButton = new JButton("Remove");
    removeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        table.removeEditor();
        VMArgumentsPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        List<VMArgumentsInfo> removableAttributeInfoList = new ArrayList<VMArgumentsInfo>();
        for(int i: table.getSelectedRows()) {
          removableAttributeInfoList.add((VMArgumentsInfo)table.getValueAt(i, -1));
        }
        for(VMArgumentsInfo vmArgumentsInfo: removableAttributeInfoList) {
          vmArgumentsInfoList.remove(vmArgumentsInfo);
        }
        adjustTable();
        restoreState();
      }
    });
    buttonPanel.add(removeButton);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(jarFileInfo == null || jarFileInfo.isSigned()) {
          return;
        }
        removeButton.setEnabled(table.getSelectedRow() != -1);
        int leadRow = table.getSelectionModel().getAnchorSelectionIndex();
        int leadColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        editButton.setEnabled(leadRow != -1 && leadColumn != -1);
      }
    });
    add(buttonPanel, BorderLayout.SOUTH);
    loadContent(null);
  }

  @SuppressWarnings("unchecked")
  public void loadContent(final JarFileInfo jarFileInfo) {
    this.jarFileInfo = jarFileInfo;
    if(jarFileInfo == null) {
      vmArgumentsInfoList = Collections.EMPTY_LIST;
    } else {
      vmArgumentsInfoList = new ArrayList<VMArgumentsInfo>(Arrays.asList(jarFileInfo.getVMArgumentsInfos()));
//      Comparator<VMArgumentsInfo> attributeInfoComparator = new Comparator<VMArgumentsInfo>() {
//        public int compare(VMArgumentsInfo o1, VMArgumentsInfo o2) {
//          return o1.getVendor().toLowerCase(Locale.ENGLISH).compareTo(o2.getVendor().toLowerCase(Locale.ENGLISH));
//        }
//      };
//      Collections.sort(vmArgumentsInfoList, attributeInfoComparator);
    }
    tableSorter.fireTableDataChanged();
    for(int i=tableSorter.getColumnCount()-1; i>=0; i--) {
      tableSorter.setSortingStatus(i, tableSorter.getSortingStatus(i));
    }
    boolean isEnabled = jarFileInfo != null && !jarFileInfo.isSigned();
    table.setEnabled(jarFileInfo != null);
    addButton.setEnabled(isEnabled);
    editButton.setEnabled(false);
    removeButton.setEnabled(false);
  }

  protected List<VMArgumentsInfo> selectedVMArgumentsInfoList;
  protected VMArgumentsInfo focusedVMArgumentsInfo;
  protected int focusedColumn;
  
  protected void storeState() {
    int leadRow = table.getSelectionModel().getAnchorSelectionIndex();
    int leadColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
    if(leadRow != -1 && leadColumn != -1) {
      focusedVMArgumentsInfo = (VMArgumentsInfo)table.getValueAt(leadRow, -1);
      focusedColumn = leadColumn;
    }
    selectedVMArgumentsInfoList = new ArrayList<VMArgumentsInfo>();
    for(int i: table.getSelectedRows()) {
      table.getValueAt(i, -1);
      selectedVMArgumentsInfoList.add((VMArgumentsInfo)table.getValueAt(i, -1));
    }
  }
  
  protected void restoreState() {
    if(selectedVMArgumentsInfoList == null) {
      return;
    }
    table.clearSelection();
    int rowCount = table.getRowCount();
    ListSelectionModel selectionModel = table.getSelectionModel();
    ListSelectionModel columnSelectionModel = table.getColumnModel().getSelectionModel();
    for(int i=0; i<rowCount; i++) {
      VMArgumentsInfo vmArgumentsInfo = (VMArgumentsInfo)table.getValueAt(i, -1);
      if(selectedVMArgumentsInfoList.contains(vmArgumentsInfo)) {
        selectionModel.addSelectionInterval(i, i);
      }
      if(vmArgumentsInfo == focusedVMArgumentsInfo) {
        selectionModel.setAnchorSelectionIndex(i);
        selectionModel.setLeadSelectionIndex(i);
        columnSelectionModel.setAnchorSelectionIndex(focusedColumn);
        columnSelectionModel.setLeadSelectionIndex(focusedColumn);
        editButton.setEnabled(true);
      }
    }
    selectedVMArgumentsInfoList = null;
    focusedVMArgumentsInfo = null;
  }
  
  protected void adjustTable() {
    tableSorter.fireTableDataChanged();
    for(int i=tableSorter.getColumnCount()-1; i>=0; i--) {
      tableSorter.setSortingStatus(i, tableSorter.getSortingStatus(i));
    }
  }
  
  public VMArgumentsInfo[] getVMArgumentsInfos() {
    return vmArgumentsInfoList.toArray(new VMArgumentsInfo[0]);
  }
  
}
