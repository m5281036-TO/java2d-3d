// run this program
// reference:
// https://docs.oracle.com/javase/8/docs/api/javax/swing/SwingUtilities.html

import java.util.Scanner;

public class MainApp {
    public static String inputFileName;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Type the shape to display [2torus, armadillo, bunny]");
        inputFileName = scanner.nextLine();

        Boolean isFound = false;
        for (String element : Config.fileNameArray) {
            if (element.equals(inputFileName)) {
                isFound = true;
            }
        }
        if (isFound == false){ // when file name is not correct
            System.err.println("File name not matched");
        }
        System.out.println("Displaying " + inputFileName);
        scanner.close();

        javax.swing.SwingUtilities.invokeLater(() -> {
            SurfaceDrawer frame = new SurfaceDrawer();
            frame.setVisible(true);
        });
    }
}
