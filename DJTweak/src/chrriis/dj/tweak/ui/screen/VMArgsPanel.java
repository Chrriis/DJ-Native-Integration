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
import chrriis.dj.tweak.data.VMArgsInfo;
import chrriis.dj.tweak.ui.TableSorter;

/**
 * @author Christopher Deckers
 */
public class VMArgsPanel extends JPanel {

  protected List<VMArgsInfo> vmArgsInfoList;
  protected JTable table;
  protected TableModel tableModel;
  protected TableSorter tableSorter;
  protected JButton addButton;
  protected JButton editButton;
  protected JButton removeButton;

  protected JarFileInfo jarFileInfo;
  
  public VMArgsPanel() {
    super(new BorderLayout(0, 0));
    JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
    centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    tableModel = new AbstractTableModel() {
      public int getColumnCount() {
        return 3;
      }
      public int getRowCount() {
        return vmArgsInfoList.size();
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        VMArgsInfo vmArgsInfo = vmArgsInfoList.get(rowIndex);
        switch (columnIndex) {
          case -1: return vmArgsInfo;
          case 0: return vmArgsInfo.getVendor();
          case 1: return vmArgsInfo.getVersion();
          case 2: return vmArgsInfo.getArgs();
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
        VMArgsInfo vmArgsInfo = (VMArgsInfo)getValueAt(rowIndex, -1);
        String newValue = aValue.toString().trim();
        String oldValue;
        switch(columnIndex) {
          case 0:
            oldValue = vmArgsInfo.getVendor();
            break;
          case 1:
            oldValue = vmArgsInfo.getVersion();
            break;
          case 2:
            oldValue = vmArgsInfo.getArgs();
            break;
          default:
            return;
        }
//        newValue = newValue.replace(" ", "-");
        if(oldValue.equals(newValue)) {
          return;
        }
        VMArgsPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        switch(columnIndex) {
          case 0:
            vmArgsInfo.setVendor(newValue);
            break;
          case 1:
            vmArgsInfo.setVersion(newValue);
            break;
          case 2:
            vmArgsInfo.setArgs(newValue);
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
        VMArgsPanel.this.firePropertyChange("jarModified", false, true);
        VMArgsInfo vmArgsInfo = new VMArgsInfo("", "", "");
        vmArgsInfoList.add(vmArgsInfo);
        adjustTable();
        int rowCount = table.getRowCount();
        for(int i=0; i<rowCount; i++) {
          if(table.getValueAt(i, -1) == vmArgsInfo) {
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
        VMArgsPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        List<VMArgsInfo> removableAttributeInfoList = new ArrayList<VMArgsInfo>();
        for(int i: table.getSelectedRows()) {
          removableAttributeInfoList.add((VMArgsInfo)table.getValueAt(i, -1));
        }
        for(VMArgsInfo vmArgsInfo: removableAttributeInfoList) {
          vmArgsInfoList.remove(vmArgsInfo);
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
      vmArgsInfoList = Collections.EMPTY_LIST;
    } else {
      vmArgsInfoList = new ArrayList<VMArgsInfo>(Arrays.asList(jarFileInfo.getVMArgsInfos()));
//      Comparator<VMArgsInfo> attributeInfoComparator = new Comparator<VMArgsInfo>() {
//        public int compare(VMArgsInfo o1, VMArgsInfo o2) {
//          return o1.getVendor().toLowerCase(Locale.ENGLISH).compareTo(o2.getVendor().toLowerCase(Locale.ENGLISH));
//        }
//      };
//      Collections.sort(vmArgsInfoList, attributeInfoComparator);
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

  protected List<VMArgsInfo> selectedVMArgsInfoList;
  protected VMArgsInfo focusedVMArgsInfo;
  protected int focusedColumn;
  
  protected void storeState() {
    int leadRow = table.getSelectionModel().getAnchorSelectionIndex();
    int leadColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
    if(leadRow != -1 && leadColumn != -1) {
      focusedVMArgsInfo = (VMArgsInfo)table.getValueAt(leadRow, -1);
      focusedColumn = leadColumn;
    }
    selectedVMArgsInfoList = new ArrayList<VMArgsInfo>();
    for(int i: table.getSelectedRows()) {
      table.getValueAt(i, -1);
      selectedVMArgsInfoList.add((VMArgsInfo)table.getValueAt(i, -1));
    }
  }
  
  protected void restoreState() {
    if(selectedVMArgsInfoList == null) {
      return;
    }
    table.clearSelection();
    int rowCount = table.getRowCount();
    ListSelectionModel selectionModel = table.getSelectionModel();
    ListSelectionModel columnSelectionModel = table.getColumnModel().getSelectionModel();
    for(int i=0; i<rowCount; i++) {
      VMArgsInfo vmArgsInfo = (VMArgsInfo)table.getValueAt(i, -1);
      if(selectedVMArgsInfoList.contains(vmArgsInfo)) {
        selectionModel.addSelectionInterval(i, i);
      }
      if(vmArgsInfo == focusedVMArgsInfo) {
        selectionModel.setAnchorSelectionIndex(i);
        selectionModel.setLeadSelectionIndex(i);
        columnSelectionModel.setAnchorSelectionIndex(focusedColumn);
        columnSelectionModel.setLeadSelectionIndex(focusedColumn);
        editButton.setEnabled(true);
      }
    }
    selectedVMArgsInfoList = null;
    focusedVMArgsInfo = null;
  }
  
  protected void adjustTable() {
    tableSorter.fireTableDataChanged();
    for(int i=tableSorter.getColumnCount()-1; i>=0; i--) {
      tableSorter.setSortingStatus(i, tableSorter.getSortingStatus(i));
    }
  }
  
  public VMArgsInfo[] getVMArgsInfos() {
    return vmArgsInfoList.toArray(new VMArgsInfo[0]);
  }
  
}
