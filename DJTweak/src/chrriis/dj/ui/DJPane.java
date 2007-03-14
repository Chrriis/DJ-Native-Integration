/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

/**
 * @author Christopher Deckers
 */
public class DJPane extends JPanel {

  protected Runnable openJarChooserRunnable;
  
  public DJPane() {
    super(new BorderLayout());
    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints cons = new GridBagConstraints();
    JPanel sourceJarPanel = new JPanel(gridBag);
    sourceJarPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 3));
    cons.insets = new Insets(2, 0, 0, 2);
    cons.gridx = 0;
    cons.gridy = 0;
    cons.fill = GridBagConstraints.HORIZONTAL;
    JLabel jarFileLabel = new JLabel("Source JAR File: ");
    gridBag.setConstraints(jarFileLabel, cons);
    sourceJarPanel.add(jarFileLabel);
    cons.gridx++;
    cons.weightx = 1;
    final JTextField jarFileTextField = new JTextField(14);
    final JarPane jarPane = new JarPane();
    final Runnable loadTextFieldJarRunnable = new Runnable() {
      public void run() {
        File selectedFile = new File(jarFileTextField.getText());
        if(!selectedFile.exists() || !selectedFile.isFile()/* || !selectedFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar")*/) {
          jarPane.loadFile(null);
          return;
        }
        jarPane.loadFile(selectedFile);
      }
    };
    add(jarPane, BorderLayout.CENTER);
    jarFileTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadTextFieldJarRunnable.run();
      }
    });
    gridBag.setConstraints(jarFileTextField, cons);
    sourceJarPanel.add(jarFileTextField);
    cons.gridx++;
    cons.weightx = 0;
    JButton jarFileButton = new JButton("...");
    jarFileButton.setMargin(new Insets(1, 5, 1, 5));
    gridBag.setConstraints(jarFileButton, cons);
    sourceJarPanel.add(jarFileButton);
    add(sourceJarPanel, BorderLayout.NORTH);
    openJarChooserRunnable = new Runnable() {
      public void run() {
        JFileChooser fileChooser = UIUtil.getJarFileChooser();
        if(fileChooser.showOpenDialog(DJPane.this) == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          jarFileTextField.setText(selectedFile.getAbsolutePath());
          loadTextFieldJarRunnable.run();
        }
      }
    };
    jarFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openJarChooserRunnable.run();
      }
    });
    setTransferHandler(new TransferHandler() {
      public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for(DataFlavor dataFlavor: transferFlavors) {
          if(dataFlavor.equals(DataFlavor.javaFileListFlavor)) {
            return true;
          }
        }
        return false;
      }
      @Override
      public boolean importData(JComponent comp, Transferable t) {
        DataFlavor[] transferFlavors = t.getTransferDataFlavors();
        for(DataFlavor dataFlavor: transferFlavors) {
          if(dataFlavor.equals(DataFlavor.javaFileListFlavor)) {
            try {
              List fileList = (List)t.getTransferData(dataFlavor);
              jarFileTextField.setText(((File)fileList.get(0)).getAbsolutePath());
              loadTextFieldJarRunnable.run();
            } catch(Exception e) {
              e.printStackTrace();
            }
          }
        }
        return false;
      }
    });
  }

  public void openJarChooser() {
    openJarChooserRunnable.run();
  }
  
}
