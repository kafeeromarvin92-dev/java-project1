/* 

  Course  : CSC 1102 - Structured and Object-Oriented Programming
   Project : Vehicle Rental System
      Leader  : MAWEWE FAITH JESCA
  Members : KAFEERO MARVIN, ZZIWA LEMUEL PAUL, SSEMUTONO ALBERT MARIA, SSEMWOGERERE JIMMY, MAWEWE FAITH JESCA
   GitHub  : https://github.com/kafeeromarvin92-dev/java-project1
 
 
  
 */

// SwingUtilities helps us start the window in the safe, correct way.
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    // The "main" method must look EXACTLY like this for Java to find it.
    public static void main(String[] args) {

        // Use the system theme so the standard Swing controls match the dashboard.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException exception) {
            System.err.println("Could not apply the system theme: " + exception.getMessage());
        }

        // Give common Swing controls the same type family as the redesigned window.
        UIManager.put("Label.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        UIManager.put("Button.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        UIManager.put("Table.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

        // invokeLater asks Swing to build and show the window safely.
    
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();   // create the window object (runs MainFrame's constructor)
            frame.setVisible(true);              // make the window appear on the screen
        });
    }
}
