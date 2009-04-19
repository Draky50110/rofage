/*
 * RoFageFrame.java
 *
 * Created on 28 févr. 2009, 11:25:20
 */

package rofage.ui;

/**
 *
 * @author Pierrot
 */
public class InitializerFrame extends javax.swing.JFrame {
    
    /** Creates new form RoFageFrame */
    public InitializerFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        pgrSplash = new javax.swing.JProgressBar();
        lblSplash = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Starting RoFage");
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("resources/images/rom.png")));
        setResizable(false);
        setUndecorated(true);

        pgrSplash.setString(""); // NOI18N
        pgrSplash.setStringPainted(true);
        pgrSplash.setBounds(10, 270, 380, 19);
        jLayeredPane1.add(pgrSplash, javax.swing.JLayeredPane.DEFAULT_LAYER);

        lblSplash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/splash.jpg"))); // NOI18N
        lblSplash.setBounds(4, 4, 390, 290);
        jLayeredPane1.add(lblSplash, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InitializerFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lblSplash;
    public javax.swing.JProgressBar pgrSplash;
    // End of variables declaration//GEN-END:variables
    
}