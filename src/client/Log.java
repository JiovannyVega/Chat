package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Log extends JFrame implements ActionListener {

    JTextField FieldNombre;
    JButton conectar, colores;
    Color colorr;

    public Log() {
        this.setTitle("Interfaz Chat");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);

        componentes();
    }

    // Metodo para agregar los componentes a la ventana
    private void componentes() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.PINK);
        panel.setSize(500, 500);

        JLabel titulo = new JLabel("Chat de Salma");
        titulo.setForeground(Color.BLACK);
        titulo.setFont(new Font("Arial Unicode MS", Font.CENTER_BASELINE, 20));
        titulo.setBounds(150, 100, 500, 100);

        FieldNombre = new JTextField();
        FieldNombre.setBounds(100, 200, 200, 30);

        colores = new JButton("Colores");
        colores.setBounds(300, 200, 100, 30);
        colores.addActionListener(this);

        conectar = new JButton("Conectar");
        conectar.setBounds(200, 250, 100, 30);
        conectar.addActionListener(this);

        panel.add(titulo);
        panel.add(FieldNombre);
        panel.add(conectar);
        panel.add(colores);
        this.add(panel);
    }

    public static void main(String args[]) {
        Log log = new Log();
        log.setVisible(true);
    }

    // Metodo para manejar los eventos de los botones
    @Override
    public void actionPerformed(ActionEvent e) {
        // Si el boton presionado es el de conectar se verifica que se haya ingresado un
        // nombre y seleccionado un color
        if (e.getSource() == conectar) {
            // Si el campo de nombre esta vacio se muestra un mensaje
            if (FieldNombre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes ingresar un nombte");
            } else if (colorr == null) {
                // Si no se ha seleccionado un color se muestra un mensaje
                JOptionPane.showMessageDialog(null, "Debes seleccionar un color");
            } else {
                // Si se ingreso un nombre y se selecciono un color se crea un objeto de la
                // clase Usuario y se abre la ventana de chat
                Usuario u = new Usuario(FieldNombre.getText(), colorr);
                Chat ventanaChat = new Chat(u);
                ventanaChat.setVisible(true);
                ventanaChat.setLocationRelativeTo(null);
                setVisible(false);
            }
        }
        // Si el boton presionado es el de colores se abre un JColorChooser
        if (e.getSource() == colores) {
            colorr = JColorChooser.showDialog(null, "Seleccione un Color", Color.BLUE);
        }
    }
}
