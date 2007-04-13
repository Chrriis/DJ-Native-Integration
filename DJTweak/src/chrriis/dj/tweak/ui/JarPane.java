/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import chrriis.dj.tweak.data.JarFileInfo;
import chrriis.dj.tweak.ui.screen.AttributesPanel;
import chrriis.dj.tweak.ui.screen.IconsPanel;
import chrriis.dj.tweak.ui.screen.VMArgsPanel;

/**
 * @author Christopher Deckers
 */
public class JarPane extends JPanel {

  protected JTabbedPane tabbedPane;
  protected JLabel jarFileLabel;
  protected JTextField jarFileTextField;
  protected JButton jarFileButton;
  protected IconsPanel iconsPanel;
  protected AttributesPanel attributesPanel;
  protected VMArgsPanel vmArgsPanel;
  protected JButton saveButton;
  protected JLabel signatureLabel;
  
  public JarPane(final DJPane djPane) {
    super(new BorderLayout(0, 0));
    setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.setEnabled(false);
    iconsPanel = new IconsPanel(djPane);
    tabbedPane.addTab("Icons", iconsPanel);
    attributesPanel = new AttributesPanel();
    tabbedPane.addTab("Attributes", attributesPanel);
    vmArgsPanel = new VMArgsPanel();
    tabbedPane.addTab("VM Arguments", vmArgsPanel);
    add(tabbedPane, BorderLayout.CENTER);
    JPanel southPanel = new JPanel(new BorderLayout(0, 0));
    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints cons = new GridBagConstraints();
    JPanel savePanel = new JPanel(gridBag);
    savePanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));
    cons.insets = new Insets(0, 0, 0, 2);
    cons.gridx = 0;
    cons.gridy = 0;
    cons.fill = GridBagConstraints.HORIZONTAL;
    jarFileLabel = new JLabel("Target JAR File: ");
    gridBag.setConstraints(jarFileLabel, cons);
    savePanel.add(jarFileLabel);
    cons.gridx++;
    cons.weightx = 1;
    jarFileTextField = new JTextField(14);
    jarFileTextField.setDropTarget(null);
    gridBag.setConstraints(jarFileTextField, cons);
    savePanel.add(jarFileTextField);
    jarFileTextField.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        update();
      }
      public void insertUpdate(DocumentEvent e) {
        update();
      }
      public void removeUpdate(DocumentEvent e) {
        update();
      }
      protected void update() {
        saveButton.setEnabled(true);
      }
    });
    cons.gridx++;
    cons.weightx = 0;
    jarFileButton = new JButton("...");
    jarFileButton.setMargin(new Insets(1, 5, 1, 5));
    gridBag.setConstraints(jarFileButton, cons);
    savePanel.add(jarFileButton);
    jarFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = UIUtil.getJarFileChooser();
        if(fileChooser.showOpenDialog(JarPane.this) == JFileChooser.APPROVE_OPTION) {
          jarFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
      }
    });
    cons.insets = new Insets(0, 0, 0, 0);
    cons.gridx++;
    saveButton = new JButton("Save");
    saveButton.setEnabled(false);
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String outFileName = jarFileTextField.getText();
        File outFile = new File(outFileName);
        if(jarFileInfo.saveInfos(attributesPanel.getAttributeInfos(), iconsPanel.getIconInfos(), vmArgsPanel.getVMArgsInfos(), outFile)) {
          saveButton.setEnabled(false);
        } else {
          String reason = (outFile.exists()? "The file may be in use": "The path may be invalid") + ", or you may not have the necessary permissions.";
          JOptionPane.showMessageDialog(JarPane.this, "The information could not be written to the file \"" + outFileName + "\".\n" + reason, "Error while saving", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    gridBag.setConstraints(saveButton, cons);
    savePanel.add(saveButton);
    southPanel.add(savePanel, BorderLayout.CENTER);
    signatureLabel = new JLabel("The file appears to be signed and cannot be edited.");
    signatureLabel.setHorizontalAlignment(JLabel.CENTER);
    southPanel.add(signatureLabel, BorderLayout.NORTH);
    PropertyChangeListener jarModificationPropertyChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        if("jarModified".equals(e.getPropertyName())) {
          saveButton.setEnabled(Boolean.TRUE.equals(e.getNewValue()));
        }
      }
    };
    iconsPanel.addPropertyChangeListener(jarModificationPropertyChangeListener);
    attributesPanel.addPropertyChangeListener(jarModificationPropertyChangeListener);
    vmArgsPanel.addPropertyChangeListener(jarModificationPropertyChangeListener);
    add(southPanel, BorderLayout.SOUTH);
    loadContent(null);
  }

  protected void loadFile(final File sourceFile) {
    new Thread() {
      @Override
      public void run() {
        JarFileInfo _jarFileInfo;
        try {
          _jarFileInfo = JarFileInfo.getJarfileInfo(sourceFile);
        } catch(Exception e) {
          e.printStackTrace();
          _jarFileInfo = null;
        }
        final JarFileInfo jarFileInfo = _jarFileInfo;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            loadContent(jarFileInfo);
            tabbedPane.setEnabled(jarFileInfo != null);
          }
        });
      }
    }.start();
  }
  
  protected JarFileInfo jarFileInfo;
  
  protected void loadContent(JarFileInfo jarFileInfo) {
    this.jarFileInfo = jarFileInfo;
    boolean isEnabled = jarFileInfo != null;
    jarFileLabel.setEnabled(isEnabled);
    if(isEnabled) {
      jarFileTextField.setText(jarFileInfo.getSourceFile().getAbsolutePath());
    } else {
      jarFileTextField.setText("");
    }
    jarFileTextField.setEnabled(isEnabled);
    jarFileButton.setEnabled(isEnabled);
    iconsPanel.loadContent(jarFileInfo);
    attributesPanel.loadContent(jarFileInfo);
    vmArgsPanel.loadContent(jarFileInfo);
    saveButton.setEnabled(false);
    signatureLabel.setVisible(isEnabled && jarFileInfo.isSigned());
  }
  
}
