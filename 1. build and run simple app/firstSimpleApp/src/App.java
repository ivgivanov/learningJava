import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class App {
    public static void main(String[] args) throws Exception {
        //JFrame application = createGUI();
        //application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //application.setVisible(true);

        Author author = new Author();
        author.setName("John");
        author.setAge(51);

        System.out.println(author.getName()+" - "+author.getAge());
    }

    private static JFrame createGUI() {
        JTextField input = new JTextField();
        input.setPreferredSize(new Dimension(300, 40));

        JButton convertButton = new JButton("convert");

        JLabel output = new JLabel();
        output.setPreferredSize(new Dimension(300, 40));

        convertButton.addActionListener(event -> {
            output.setText(LowerCaseConverter.convertToLowercase(input.getText()));
        });

        JFrame gui = new JFrame("Case converter");
        gui.setLayout(new FlowLayout());
        gui.add(input);
        gui.add(convertButton);
        gui.add(output);
        gui.pack();
        gui.setLocationRelativeTo(null);

        return gui;

    }
}
