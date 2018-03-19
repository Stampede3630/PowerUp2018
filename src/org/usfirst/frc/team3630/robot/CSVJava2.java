import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;

public class CSVJava2 extends JApplet {
    
	PrintWriter pw;
    
    String csvFile = "/Users/clarca/Desktop/PitScout.csv";
    File f = new File(csvFile);
    char shift = ',';
    char newLine = '\n';
    
    JCheckBox checkBox1;
    JCheckBox checkBox10;
    JCheckBox checkBox2;
    JCheckBox checkBox3;
    JCheckBox checkBox4;
    JCheckBox checkBox5;
    JCheckBox checkBox6;
    JCheckBox checkBox7;
    JCheckBox checkBox8;
    JCheckBox checkBox9;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    JLabel jLabel6;
    JLabel jLabel7;
    JLabel jLabel8;
    JLabel jLabel9;
    JScrollPane jScrollPane1;
    JButton subButton;
    JTextArea textArea;
    JTextField textField1;
    JTextField textField2;
    JTextField textField3;
    
    @Override
    public void init() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(CSVJava2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setSize(650, 550);
                    initComponents();
                    writeFile();
                }
            });
        }
        catch (InterruptedException | InvocationTargetException ex) {}
    }
    
    public void clearText() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textArea.setText("");
    }
    
    public void writeFile() {
        try {
            pw = new PrintWriter(new FileWriter(f, true));
        }
        
        catch (Exception e) {}
        
        subButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pw.write(newLine);
                pw.write(textField1.getText());
                pw.write(shift);
                
                if (checkBox1.isEnabled()) {
                    pw.write("1");
                }
                
                else if (checkBox2.isEnabled()) {
                    pw.write("0");
                }
                
                pw.write(shift);
                
                if (checkBox3.isSelected()) {
                    pw.write("1");
                }
                
                else if (checkBox4.isSelected()) {
                    pw.write("0");
                }
                
                pw.write(shift);
                
                if (checkBox5.isSelected()) {
                    pw.write("1");
                }
                
                else if (checkBox6.isSelected()) {
                    pw.write("0");
                }
                
                pw.write(shift);
                
                if (checkBox7.isSelected()) {
                    pw.write("1");
                }
                
                else if (checkBox8.isSelected()) {
                    pw.write("0");
                }
                
                pw.write(shift);
                
                if (checkBox9.isSelected()) {
                    pw.write("1");
                }
                
                else if (checkBox10.isSelected()) {
                    pw.write("0");
                }
        
                pw.write(shift);
                pw.write(textField2.getText());
                pw.write(shift);
                pw.write(textField3.getText());
                pw.write(shift);
                pw.write(textArea.getText());
                pw.write(newLine);
                
                pw.flush();
                clearText();
            }
        });
    }
    
    public void initComponents() {

        jLabel1 = new JLabel();
        textField1 = new JTextField();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        textField2 = new JTextField();
        jLabel9 = new JLabel();
        jScrollPane1 = new JScrollPane();
        textArea = new JTextArea();
        subButton = new JButton();
        jLabel8 = new JLabel();
        textField3 = new JTextField();
        checkBox1 = new JCheckBox();
        checkBox2 = new JCheckBox();
        checkBox3 = new JCheckBox();
        checkBox4 = new JCheckBox();
        checkBox5 = new JCheckBox();
        checkBox6 = new JCheckBox();
        checkBox7 = new JCheckBox();
        checkBox8 = new JCheckBox();
        checkBox9 = new JCheckBox();
        checkBox10 = new JCheckBox();

        jLabel1.setText("Team #");

        textField1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textField1ActionPerformed(evt);
            }
        });
        
        jLabel2.setText("Can do Vault");

        jLabel3.setText("Can do Switch");

        jLabel4.setText("Can do Scale");

        jLabel5.setText("Can Intake @ Portal");

        jLabel6.setText("Can Intake @ Ground");

        jLabel7.setText("Climb Type");

        jLabel9.setText("Extra Notes");

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        subButton.setText("Submit");

        jLabel8.setText("DriveTrain type");

        textField3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textField3ActionPerformed(evt);
            }
        });

        checkBox1.setText("Yes");
        checkBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                checkBox1ActionPerformed(evt);
            }
        });

        checkBox2.setText("No");

        checkBox3.setText("Yes");

        checkBox4.setText("No");
        checkBox4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                checkBox4ActionPerformed(evt);
            }
        });

        checkBox5.setText("Yes");
        checkBox5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                checkBox5ActionPerformed(evt);
            }
        });

        checkBox6.setText("No");

        checkBox7.setText("Yes");

        checkBox8.setText("No");

        checkBox9.setText("Yes");

        checkBox10.setText("No");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox9)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox10)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(textField2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textField3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox6)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox8))
                                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel2)
                                        .addGap(2, 2, 2)
                                        .addComponent(checkBox1)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkBox3)))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkBox4))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(265, 265, 265)
                        .addComponent(subButton)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(checkBox1)
                    .addComponent(checkBox2)
                    .addComponent(checkBox3)
                    .addComponent(checkBox4))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(checkBox5)
                    .addComponent(checkBox6)
                    .addComponent(checkBox7)
                    .addComponent(checkBox8))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                        .addComponent(checkBox9))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(textField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(textField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(checkBox10)))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subButton)
                .addContainerGap(370, Short.MAX_VALUE))
        );
    }                       

    public void textField1ActionPerformed(ActionEvent evt) {                                           
        
    }                                          

    public void textField3ActionPerformed(ActionEvent evt) {                                           
        
    }                                          

    public void checkBox1ActionPerformed(ActionEvent evt) {                                          
        
    }                                         

    public void checkBox4ActionPerformed(ActionEvent evt) {                                          
        
    }                                         

    public void checkBox5ActionPerformed(ActionEvent evt) {                                          
        
    }
}