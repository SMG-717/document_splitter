package smg;

import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;

/**
 *
 * @author SaifeldinMohamed
 */
public class UI extends JFrame {

    /**
     * Creates new form UI
     */
    public UI() {
        initComponents();
    }
                           
    private void initComponents() {

        progressPanel = new JPanel();
        progress = new JProgressBar();
        scroller = new JScrollPane();
        logLabel = new JLabel();
        root = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        documentField = new JTextField();
        browseButton = new JButton();
        cancelButton = new JButton();
        proceedButton = new JButton();
        logger = new JEditorPane();

        scroller.setViewportView(logger);

        logLabel.setText("Process Log:");

        documentField.setFont(Font.decode(Font.MONOSPACED));
        logger.setFont(Font.decode(Font.MONOSPACED));
        progress.setMaximum(100);

        GroupLayout progressPanelLayout = new GroupLayout(progressPanel);
        progressPanel.setPreferredSize(new Dimension(700, 190));
        progressPanel.setLayout(progressPanelLayout);
        
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(progressPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(scroller)
                    .addComponent(progress, GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.LEADING, progressPanelLayout.createSequentialGroup()
                        .addComponent(logLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addComponent(logLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroller, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Document Splitter");
        setResizable(false);

        label1.setText("  Welcome to the Document Splitter.");

        label2.setText("  Select your mail merge document here.");

        browseButton.setText("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        proceedButton.setText("Proceed");
        proceedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });

        GroupLayout rootLayout = new GroupLayout(root);
        root.setLayout(rootLayout);
        rootLayout.setHorizontalGroup(
            rootLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(rootLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(rootLayout.createSequentialGroup()
                        .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(rootLayout.createSequentialGroup()
                        .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(cancelButton)
                            .addComponent(documentField, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(browseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(proceedButton, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))))
                .addContainerGap())
        );
        rootLayout.setVerticalGroup(
            rootLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(rootLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(documentField, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rootLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(proceedButton))
                .addGap(11, 11, 11))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(root, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(root, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    private void browseButtonActionPerformed(ActionEvent evt) {                                             
        FileDialog dialog = new FileDialog((Frame) null, "Select Mail Merge Document", FileDialog.LOAD);
        dialog.setMultipleMode(false);
        dialog.setVisible(true);

        if (dialog.getFile() != null) {
            documentField.setText(Paths.get(dialog.getDirectory() + dialog.getFile()).toAbsolutePath().toString());
            documentField.moveCaretPosition(0);
        }
    }                                            

    private void proceedButtonActionPerformed(ActionEvent evt) {                                              
        setContentPane(progressPanel);
        setResizable(true);
        pack();
        new Thread(new Runnable() {
            public void run() {
                proceedCallback.accept(documentField.getText());
            }
        }).start();
    }                                             

    private void cancelButtonActionPerformed(ActionEvent evt) {                                             
        this.dispose();
        System.exit(0);
    }                                            

    public static void ui_main(Consumer<String> onProceedCallback, Consumer<UI> framesetter) {

        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                UI ui = new UI();
                framesetter.accept(ui);
                ui.setProceedCallback(onProceedCallback);
                ui.setVisible(true);
            }
        });
    }

    public void markProgress(int percentage) {
        progress.setValue(percentage);
    }

    public void log(String message) {
        logger.setText(logger.getText() + message);
        logger.setCaretPosition(logger.getText().length());
    }
    
    public void clearLog() {
        logger.setText("");
    }

    public JFrame setProceedCallback(Consumer<String> callback) {
        proceedCallback = callback;
        return this;
    }
    
    private Consumer<String> proceedCallback;
    private JButton browseButton;
    private JButton cancelButton;
    private JTextField documentField;
    private JLabel logLabel;
    private JScrollPane scroller;
    private JEditorPane logger;
    private JLabel label1;
    private JLabel label2;
    private JButton proceedButton;
    private JProgressBar progress;
    private JPanel progressPanel;
    private JPanel root;           
}
