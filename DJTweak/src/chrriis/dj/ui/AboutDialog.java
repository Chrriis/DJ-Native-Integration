/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

/**
 * @author Christopher Deckers
 */
public class AboutDialog extends JDialog {

  public AboutDialog(JFrame jFrame) {
    super(jFrame, "About " + jFrame.getTitle(), true);
    setResizable(false);
    JPanel contentPane = new JPanel(new BorderLayout(0, 0));
    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    contentPane.add(new JLabel(new ImageIcon(getClass().getResource("resource/DJTweak200x200.png"))), BorderLayout.WEST);
    JEditorPane editorPane = new JEditorPane("text/html", 
        "<html><head><style>body{font:Sans-Serif}</style></head>" +
        "<h1>The DJ Project<br>" +
        "Rediscover the Desktop</h1><br>" +
        getLink("http://djproject.sourceforge.net") + "<br>" +
        "<br>" +
        "DJ Tweak - Configuration Utility<br>" +
        "Christopher Deckers (chrriis@nextencia.net)<br>" +
        "</html>");
    if(System.getProperty("java.version").compareTo("1.6") >= 0) {
      editorPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if(e.getEventType() != EventType.ACTIVATED) {
            return;
          }
          if(Desktop.isDesktopSupported()) {
            try {
              Desktop.getDesktop().browse(e.getURL().toURI());
            } catch(Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
    }
    editorPane.setEditable(false);
    contentPane.setBackground(editorPane.getBackground());
    contentPane.add(editorPane, BorderLayout.CENTER);
//    GridBagLayout gridBag = new GridBagLayout();
//    GridBagConstraints cons = new GridBagConstraints();
//    JPanel textPanel = new JPanel(gridBag);
//    textPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
//    cons.anchor = GridBagConstraints.WEST;
//    cons.gridx = 0;
//    cons.gridy = 0;
//    JLabel label = new JLabel("DJ - Rediscover the Desktop");
//    gridBag.setConstraints(label, cons);
//    textPanel.add(label);
//    cons.gridy++;
//    label = new JLabel("Tweak configuration utility");
//    gridBag.setConstraints(label, cons);
//    textPanel.add(label);
//    cons.gridy++;
//    label = new JLabel("Christopher Deckers");
//    gridBag.setConstraints(label, cons);
//    textPanel.add(label);
//    cons.gridy++;
//    label = new JLabel("http://djproject.sourceforge.net");
//    gridBag.setConstraints(label, cons);
//    textPanel.add(label);
//    contentPane.add(textPanel, BorderLayout.CENTER);
    getContentPane().add(contentPane, BorderLayout.CENTER);
    pack();
  }
  
  protected static String getLink(String link) {
    if(System.getProperty("java.version").compareTo("1.6") >= 0) {
      return "<a href=\"" + link + "\">" + link + "</a>";
    }
    return link;
  }
  
}
