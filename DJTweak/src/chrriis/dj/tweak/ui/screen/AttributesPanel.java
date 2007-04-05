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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import chrriis.dj.tweak.data.AttributeInfo;
import chrriis.dj.tweak.data.JarFileInfo;
import chrriis.dj.tweak.ui.TableSorter;

/**
 * @author Christopher Deckers
 */
public class AttributesPanel extends JPanel {

  protected List<AttributeInfo> attributeInfoList;
  protected JTable table;
  protected TableModel tableModel;
  protected TableSorter tableSorter;
  protected JButton addButton;
  protected JButton editButton;
  protected JButton removeButton;

  protected JarFileInfo jarFileInfo;
  
  public AttributesPanel() {
    super(new BorderLayout(0, 0));
    JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
    centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    tableModel = new AbstractTableModel() {
      public int getColumnCount() {
        return 2;
      }
      public int getRowCount() {
        return attributeInfoList.size();
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        AttributeInfo attributeInfo = attributeInfoList.get(rowIndex);
        switch (columnIndex) {
          case -1: return attributeInfo;
          case 0: return attributeInfo.getKey();
          case 1: return attributeInfo.getValue();
        }
        return null;
      }
      @Override
      public String getColumnName(int column) {
        switch(column) {
          case 0: return "Key";
          case 1: return "Value";
        }
        return null;
      }
      @Override
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return jarFileInfo != null && !jarFileInfo.isSigned();
      }
      @Override
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        AttributeInfo attributeInfo = (AttributeInfo)getValueAt(rowIndex, -1);
        String newValue = aValue.toString();
        String oldValue;
        switch(columnIndex) {
          case 0:
            oldValue = attributeInfo.getKey();
            newValue = newValue.trim();
            break;
          case 1:
            oldValue = attributeInfo.getValue();
            break;
          default:
            return;
        }
//        newValue = newValue.replace(" ", "-");
        if(oldValue.equals(newValue)) {
          return;
        }
        AttributesPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        switch(columnIndex) {
          case 0:
            attributeInfo.setKey(newValue);
            break;
          case 1:
            attributeInfo.setValue(newValue);
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
    TableColumn column0 = columnModel.getColumn(0);
    JTextField textField = new JTextField();
    Component tableCellEditorComponent = table.getDefaultEditor(String.class).getTableCellEditorComponent(table, null, false, -1, -1);
    textField.setBorder(((JComponent)tableCellEditorComponent).getBorder());
    textField.setDocument(new PlainDocument() {
      @Override
      public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        str = str.replaceAll("[^0-9a-zA-Z\\-_]", "");
        try {
          if((getText(0, getLength()) + str).getBytes("UTF-8").length <= 68) {
            super.insertString(offs, str, a);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    });
    column0.setCellEditor(new DefaultCellEditor(textField));
    column0.setPreferredWidth(50);
    columnModel.getColumn(1).setPreferredWidth(300);
    adjustTable();
    centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    add(centerPanel, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    addButton = new JButton("Add");
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        AttributesPanel.this.firePropertyChange("jarModified", false, true);
        AttributeInfo attributeInfo = new AttributeInfo("", "");
        attributeInfoList.add(attributeInfo);
        adjustTable();
        int rowCount = table.getRowCount();
        for(int i=0; i<rowCount; i++) {
          if(table.getValueAt(i, -1) == attributeInfo) {
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
        AttributesPanel.this.firePropertyChange("jarModified", false, true);
        storeState();
        List<AttributeInfo> removableAttributeInfoList = new ArrayList<AttributeInfo>();
        for(int i: table.getSelectedRows()) {
          removableAttributeInfoList.add((AttributeInfo)table.getValueAt(i, -1));
        }
        for(AttributeInfo attributeInfo: removableAttributeInfoList) {
          attributeInfoList.remove(attributeInfo);
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
      attributeInfoList = Collections.EMPTY_LIST;
    } else {
      attributeInfoList = new ArrayList<AttributeInfo>(Arrays.asList(jarFileInfo.getAttributeInfos()));
      Comparator<AttributeInfo> attributeInfoComparator = new Comparator<AttributeInfo>() {
        public int compare(AttributeInfo o1, AttributeInfo o2) {
          return o1.getKey().toLowerCase(Locale.ENGLISH).compareTo(o2.getKey().toLowerCase(Locale.ENGLISH));
        }
      };
      Collections.sort(attributeInfoList, attributeInfoComparator);
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

  protected List<AttributeInfo> selectedAttributeInfoList;
  protected AttributeInfo focusedAttributeInfo;
  protected int focusedColumn;
  
  protected void storeState() {
    int leadRow = table.getSelectionModel().getAnchorSelectionIndex();
    int leadColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
    if(leadRow != -1 && leadColumn != -1) {
      focusedAttributeInfo = (AttributeInfo)table.getValueAt(leadRow, -1);
      focusedColumn = leadColumn;
    }
    selectedAttributeInfoList = new ArrayList<AttributeInfo>();
    for(int i: table.getSelectedRows()) {
      table.getValueAt(i, -1);
      selectedAttributeInfoList.add((AttributeInfo)table.getValueAt(i, -1));
    }
  }
  
  protected void restoreState() {
    if(selectedAttributeInfoList == null) {
      return;
    }
    table.clearSelection();
    int rowCount = table.getRowCount();
    ListSelectionModel selectionModel = table.getSelectionModel();
    ListSelectionModel columnSelectionModel = table.getColumnModel().getSelectionModel();
    for(int i=0; i<rowCount; i++) {
      AttributeInfo attributeInfo = (AttributeInfo)table.getValueAt(i, -1);
      if(selectedAttributeInfoList.contains(attributeInfo)) {
        selectionModel.addSelectionInterval(i, i);
      }
      if(attributeInfo == focusedAttributeInfo) {
        selectionModel.setAnchorSelectionIndex(i);
        selectionModel.setLeadSelectionIndex(i);
        columnSelectionModel.setAnchorSelectionIndex(focusedColumn);
        columnSelectionModel.setLeadSelectionIndex(focusedColumn);
        editButton.setEnabled(true);
      }
    }
    selectedAttributeInfoList = null;
    focusedAttributeInfo = null;
  }
  
  protected void adjustTable() {
    tableSorter.fireTableDataChanged();
    for(int i=tableSorter.getColumnCount()-1; i>=0; i--) {
      tableSorter.setSortingStatus(i, tableSorter.getSortingStatus(i));
    }
  }
  
  public AttributeInfo[] getAttributeInfos() {
    List<AttributeInfo> filteredAttributeInfoList = new ArrayList<AttributeInfo>();
    for(AttributeInfo attributeInfo: attributeInfoList) {
      if(!"".equals(attributeInfo.getKey()) && !"".equals(attributeInfo.getValue())) {
        filteredAttributeInfoList.add(attributeInfo);
      }
    }
    return filteredAttributeInfoList.toArray(new AttributeInfo[0]);
  }
  
}
