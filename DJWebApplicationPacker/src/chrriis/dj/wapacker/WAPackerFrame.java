/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

/**
 * @author Christopher Deckers
 */
public class WAPackerFrame extends JFrame {

  protected JLabel statusLabel;
  protected JCheckBox readmeFileCheckBox;
  protected JCheckBox licenseFileCheckBox;
  protected JCheckBox applicationArchiveFileCheckBox;
  protected JTextField jnlpDescriptorTextField;
  protected JTextField readmeFileTextField;
  protected JTextField licenseFileTextField;
  protected JTextField outputJarFileTextField;
  protected JTextField applicationArchiveFileTextField;
  protected JButton jnlpDescriptorButton;
  protected JButton readmeFileButton;
  protected JButton licenseFileButton;
  protected JButton outputJarFileButton;
  protected JButton applicationArchiveFileButton;
  protected JButton generateButton;
  
  public WAPackerFrame() {
    super("DJ Web-Application Packer");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    JPanel contentPane = new JPanel(new BorderLayout(0, 0));
    final JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
    JPanel fieldsPanel = new JPanel();
    GridBagLayout gridBag = new GridBagLayout();
    fieldsPanel.setLayout(gridBag);
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridx = 0;
    cons.gridy = 0;
    cons.insets = new Insets(1, 1, 1, 1);
    cons.fill = GridBagConstraints.HORIZONTAL;
    cons.anchor = GridBagConstraints.WEST;
    JLabel jnlpDescriptorLabel = new JLabel("JNLP Descriptor (File or URL): ");
    gridBag.setConstraints(jnlpDescriptorLabel, cons);
    fieldsPanel.add(jnlpDescriptorLabel);
    cons.gridy++;
    JLabel outputJarFileLabel = new JLabel("Output Installer JAR File: ");
    gridBag.setConstraints(outputJarFileLabel, cons);
    fieldsPanel.add(outputJarFileLabel);
    cons.gridy++;
    readmeFileCheckBox = new JCheckBox("Readme File: ");
    readmeFileCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setReadmeFileEnabled(e.getStateChange() == ItemEvent.SELECTED);
      }
    });
    gridBag.setConstraints(readmeFileCheckBox, cons);
    fieldsPanel.add(readmeFileCheckBox);
    cons.gridy++;
    licenseFileCheckBox = new JCheckBox("License File: ");
    licenseFileCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setLicenseFileEnabled(e.getStateChange() == ItemEvent.SELECTED);
      }
    });
    gridBag.setConstraints(licenseFileCheckBox, cons);
    fieldsPanel.add(licenseFileCheckBox);
    cons.gridy++;
    applicationArchiveFileCheckBox = new JCheckBox("Install Offline from archive: ");
    applicationArchiveFileCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setApplicationArchiveFileEnabled(e.getStateChange() == ItemEvent.SELECTED);
      }
    });
    gridBag.setConstraints(applicationArchiveFileCheckBox, cons);
    fieldsPanel.add(applicationArchiveFileCheckBox);
    cons.gridx++;
    cons.gridy = 0;
    cons.weightx = 1.0;
    jnlpDescriptorTextField = new JTextField(30);
    gridBag.setConstraints(jnlpDescriptorTextField, cons);
    fieldsPanel.add(jnlpDescriptorTextField);
    cons.gridy++;
    outputJarFileTextField = new JTextField(30);
    gridBag.setConstraints(outputJarFileTextField, cons);
    fieldsPanel.add(outputJarFileTextField);
    cons.gridy++;
    readmeFileTextField = new JTextField(30);
    gridBag.setConstraints(readmeFileTextField, cons);
    fieldsPanel.add(readmeFileTextField);
    cons.gridy++;
    licenseFileTextField = new JTextField(30);
    gridBag.setConstraints(licenseFileTextField, cons);
    fieldsPanel.add(licenseFileTextField);
    cons.gridy++;
    applicationArchiveFileTextField = new JTextField(30);
    gridBag.setConstraints(applicationArchiveFileTextField, cons);
    fieldsPanel.add(applicationArchiveFileTextField);
    cons.gridx++;
    cons.gridy = 0;
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0;
    jnlpDescriptorButton = new JButton("...");
    jnlpDescriptorButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = Util.getJnlpFileChooser();
        if(fileChooser.showOpenDialog(WAPackerFrame.this) == JFileChooser.APPROVE_OPTION) {
          jnlpDescriptorTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
          jnlpDescriptorTextField.setCaretPosition(0);
        }
      }
    });
    jnlpDescriptorButton.setMargin(new Insets(1, 5, 1, 5));
    fieldsPanel.add(jnlpDescriptorButton);
    cons.gridy++;
    outputJarFileButton = new JButton("...");
    outputJarFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = Util.getJarFileChooser();
        if(fileChooser.showOpenDialog(WAPackerFrame.this) == JFileChooser.APPROVE_OPTION) {
          outputJarFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
          outputJarFileTextField.setCaretPosition(0);
        }
      }
    });
    gridBag.setConstraints(outputJarFileButton, cons);
    outputJarFileButton.setMargin(new Insets(1, 5, 1, 5));
    fieldsPanel.add(outputJarFileButton);
    cons.gridy++;
    readmeFileButton = new JButton("...");
    readmeFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = Util.getTextHtmlFileChooser();
        if(fileChooser.showOpenDialog(WAPackerFrame.this) == JFileChooser.APPROVE_OPTION) {
          readmeFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
          readmeFileTextField.setCaretPosition(0);
        }
      }
    });
    gridBag.setConstraints(readmeFileButton, cons);
    readmeFileButton.setMargin(new Insets(1, 5, 1, 5));
    fieldsPanel.add(readmeFileButton);
    cons.gridy++;
    licenseFileButton = new JButton("...");
    licenseFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = Util.getTextHtmlFileChooser();
        if(fileChooser.showOpenDialog(WAPackerFrame.this) == JFileChooser.APPROVE_OPTION) {
          licenseFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
          licenseFileTextField.setCaretPosition(0);
        }
      }
    });
    gridBag.setConstraints(licenseFileButton, cons);
    licenseFileButton.setMargin(new Insets(1, 5, 1, 5));
    fieldsPanel.add(licenseFileButton);
    centerPanel.add(fieldsPanel, BorderLayout.CENTER);
    cons.gridy++;
    applicationArchiveFileButton = new JButton("...");
    applicationArchiveFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = Util.getZipJarFileChooser();
        if(fileChooser.showOpenDialog(WAPackerFrame.this) == JFileChooser.APPROVE_OPTION) {
          applicationArchiveFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
          applicationArchiveFileTextField.setCaretPosition(0);
        }
      }
    });
    gridBag.setConstraints(applicationArchiveFileButton, cons);
    applicationArchiveFileButton.setMargin(new Insets(1, 5, 1, 5));
    fieldsPanel.add(applicationArchiveFileButton);
    // Set states of checkboxes
    setReadmeFileEnabled(false);
    setLicenseFileEnabled(false);
    setApplicationArchiveFileEnabled(false);
    // Continue
    centerPanel.add(fieldsPanel, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
    generateButton = new JButton("Generate");
    generateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        generateInstaller();
      }
    });
    buttonPanel.add(generateButton);
    centerPanel.add(buttonPanel, BorderLayout.SOUTH);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    contentPane.add(centerPanel, BorderLayout.CENTER);
    JPanel statusPanel = new JPanel(new BorderLayout(0, 0));
    statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    statusLabel = new JLabel(" ");
    statusPanel.add(statusLabel, BorderLayout.CENTER);
    contentPane.add(statusPanel, BorderLayout.SOUTH);
    setContentPane(contentPane);
//    setResizable(false);
    pack();
    setLocationByPlatform(true);
  }
  
  protected void setReadmeFileEnabled(boolean isEnabled) {
    readmeFileCheckBox.setSelected(isEnabled);
    readmeFileTextField.setEnabled(isEnabled);
    readmeFileButton.setEnabled(isEnabled);
  }
  
  protected void setLicenseFileEnabled(boolean isEnabled) {
    licenseFileCheckBox.setSelected(isEnabled);
    licenseFileTextField.setEnabled(isEnabled);
    licenseFileButton.setEnabled(isEnabled);
  }
  
  protected void setApplicationArchiveFileEnabled(boolean isEnabled) {
    applicationArchiveFileCheckBox.setSelected(isEnabled);
    applicationArchiveFileTextField.setEnabled(isEnabled);
    applicationArchiveFileButton.setEnabled(isEnabled);
  }
  
  protected void setUIEnabled(boolean isEnabled) {
    jnlpDescriptorTextField.setEnabled(isEnabled);
    outputJarFileTextField.setEnabled(isEnabled);
    boolean isReadmeFileEnabled = readmeFileCheckBox.isSelected();
    boolean isLicenseFileEnabled = licenseFileCheckBox.isSelected();
    boolean isApplicationArchiveEnabled = applicationArchiveFileCheckBox.isSelected();
    readmeFileCheckBox.setEnabled(isEnabled);
    licenseFileCheckBox.setEnabled(isEnabled);
    applicationArchiveFileCheckBox.setEnabled(isEnabled);
    readmeFileTextField.setEnabled(isEnabled && isReadmeFileEnabled);
    licenseFileTextField.setEnabled(isEnabled && isLicenseFileEnabled);
    applicationArchiveFileTextField.setEnabled(isEnabled && isApplicationArchiveEnabled);
    jnlpDescriptorButton.setEnabled(isEnabled);
    outputJarFileButton.setEnabled(isEnabled);
    readmeFileButton.setEnabled(isEnabled && isReadmeFileEnabled);
    licenseFileButton.setEnabled(isEnabled && isLicenseFileEnabled);
    applicationArchiveFileButton.setEnabled(isEnabled && isApplicationArchiveEnabled);
    generateButton.setEnabled(isEnabled);
  }
  
  protected void generateInstaller() {
    statusLabel.setText("Generating pack...");
    setUIEnabled(false);
    final String jnlpDescriptorText = jnlpDescriptorTextField.getText().trim();
    final String outputJarFileText = outputJarFileTextField.getText().trim();
    final String readmeFileText = readmeFileCheckBox.isSelected()? readmeFileTextField.getText().trim(): null;
    final String licenseFileText = licenseFileCheckBox.isSelected()? licenseFileTextField.getText().trim(): null;
    final String applicationArchiveFileText = applicationArchiveFileCheckBox.isSelected()? applicationArchiveFileTextField.getText().trim(): null;
    new Thread() {
      @Override
      public void run() {
        URL jnlpURL;
        try {
          jnlpURL = new URL(jnlpDescriptorText);
        } catch(Exception ex) {
          try {
            File jnlpDescriptorFile = new File(jnlpDescriptorText);
            if(jnlpDescriptorFile.exists()) {
              jnlpURL = jnlpDescriptorFile.toURI().toURL();
            } else {
              jnlpURL = null;
            }
          } catch(Exception exc) {
            jnlpURL = null;
          }
        }
        String message;
        try {
          if(jnlpURL == null) {
            throw new IllegalStateException("The JNLP descriptor could not be accessed!");
          }
          File readmeFile;
          if(readmeFileText == null) {
            readmeFile = null;
          } else {
            readmeFile = new File(readmeFileText);
            if(!readmeFile.exists() || !readmeFile.isFile()) {
              throw new IllegalStateException("The Readme file could not be accessed!");
            }
          }
          File licenseFile;
          if(licenseFileText == null) {
            licenseFile = null;
          } else {
            licenseFile = new File(licenseFileText);
            if(!licenseFile.exists() || !licenseFile.isFile()) {
              throw new IllegalStateException("The License file could not be accessed!");
            }
          }
          File applicationArchiveFile;
          if(applicationArchiveFileText == null) {
            applicationArchiveFile = null;
          } else {
            applicationArchiveFile = new File(applicationArchiveFileText);
            if(!applicationArchiveFile.exists() || !applicationArchiveFile.isFile()) {
              throw new IllegalStateException("The Application Archive file could not be accessed!");
            }
          }
          File outputJarFile = new File(outputJarFileText);
          WAPackerConfiguration wsPackerConfiguration = new WAPackerConfiguration(jnlpURL, outputJarFile, readmeFile, licenseFile, applicationArchiveFile);
          new WAPacker().pack(wsPackerConfiguration);
          message = "Pack generated succesfully!";
        } catch(Throwable t) {
          t.printStackTrace();
          message = t.getMessage();
          if(t instanceof NoClassDefFoundError) {
            message = "Could not find class " + message;
          }
          message = "Generation failed" + (message == null? "!": ": " + message); 
        }
        final String message_ = message;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            statusLabel.setText(message_);
            setUIEnabled(true);
            generateButton.requestFocus();
          }
        });
      }
    }.start();
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      e.printStackTrace();
    }
    new WAPackerFrame().setVisible(true);
  }
  
}
