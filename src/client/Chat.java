package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.text.*;
import javax.swing.filechooser.FileNameExtensionFilter;

//https://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea
//https://www.flaticon.es/packs/user-avatar-2
public class Chat extends JFrame implements ActionListener, KeyListener {

    public JTextPane chatBox;
    private JButton enviar;
    private JTextField inputField;
    private JScrollPane scrollPane;

    private JLabel infoUsuario, imgUsuario;

    private Usuario u;
    public PrintWriter cout;
    private Socket socket;
    private String imgFondo = "src/client/UserBackground/Fondo (1).jpg";

    // Constructor de la clase Chat que recibe un objeto de la calse Usuario
    public Chat(Usuario u) {
        this.setVisible(true);

        this.setTitle("Interfaz Chat");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        // Se asigna el usuario
        this.u = u;
        // Se selecciona la imagen de fondo
        this.imgFondo = seleccionarImagenFondo();

        // Se inicia el socket con la ip del servidor
        inicarSocket(JOptionPane.showInputDialog("Ingrese la ip del servidor", "localhost"));
        componentes();
    }

    // Metodo para iniciar el socket
    private void inicarSocket(String ip) {
        try {
            socket = new Socket(ip, 5000);
            cout = new PrintWriter(socket.getOutputStream(), true);

            ThreadClient threadClient = new ThreadClient(socket, this);
            new Thread(threadClient).start(); // start thread to receive message
            // Se envia el nombre del usuario al servidor
            cout.println(u.getNombre() + ": ha entrado al chat¤-8355712");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e + "No se ha podido establecer conexion");
        }
    }

    // Metodo para crear los componentes de la interfaz
    private void componentes() {
        GridBagConstraints cons = new GridBagConstraints();

        // Se crea un label con la imagen de usuario
        imgUsuario = new JLabel();
        ImageIcon imageIcon = new ImageIcon("src/client/UserIcon/mujer.png");
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imgUsuario.setIcon(imageIcon);
        imgUsuario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cambiarImagenUsuario();
            }
        });

        // Se crea un label con el nombre del usuario
        infoUsuario = new JLabel("Usuario: " + u.getNombre());
        infoUsuario.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
        infoUsuario.setHorizontalAlignment(JLabel.LEFT);

        // Se crea un JTextPane para mostrar los mensajes
        chatBox = new JTextPane() {
            Image img = new ImageIcon(imgFondo).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f)); // Ajusta la opacidad aquí
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        chatBox.setBorder(BorderFactory.createEmptyBorder());
        chatBox.setOpaque(false);
        chatBox.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
        chatBox.setEditable(false);
        chatBox.setBackground(Color.BLACK);
        scrollPane = new JScrollPane(chatBox);

        // Se crea un JTextField para ingresar los mensajes
        inputField = new JTextField(20);
        inputField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
        inputField.addKeyListener(this);

        // Se crea un JButton para enviar los mensajes
        enviar = new JButton("Enviar");

        // Restricciones de la imagen de usuario
        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridwidth = 1;
        cons.gridheight = 1;
        cons.weightx = 0.0;
        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.BOTH;
        this.add(imgUsuario, cons);

        // Restricciones del label de informacion del usuario
        cons.gridx = 1;
        cons.gridy = 0;
        cons.gridwidth = 1;
        cons.gridheight = 1;
        cons.weightx = 1.0;
        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.BOTH;
        this.add(infoUsuario, cons);

        // Restricciones del JTextPane
        cons.gridx = 0;
        cons.gridy = 1;
        cons.gridwidth = 2;
        cons.gridheight = 1;
        cons.weightx = 1.0;
        cons.weighty = 1.0;
        cons.fill = GridBagConstraints.BOTH;
        scrollPane.setFont(new Font("Arial Unicode MS", Font.CENTER_BASELINE, 20));
        this.add(scrollPane, cons);

        // Restricciones del JTextField
        cons.gridx = 0;
        cons.gridy = 2;
        cons.gridwidth = 1;
        cons.gridheight = 1;
        cons.weightx = 1.0;
        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.BOTH;
        add(inputField, cons);

        // Restricciones del JButton
        cons.gridx = 1;
        cons.gridy = 2;
        cons.gridwidth = 1;
        cons.gridheight = 1;
        cons.weightx = 0.0;
        cons.weighty = 0.0;
        cons.fill = GridBagConstraints.BOTH;
        enviar.addActionListener(this);
        this.add(enviar, cons);

    }

    // Metodo para agregar texto a la caja de chat
    public void appendToPane(JTextPane tp, String msg, Color c) {
        tp.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
        tp.setEditable(false);
    }

    // Metodo para enviar mensajes
    public void enviar() {
        // Si el campo de texto no esta vacio
        if (!inputField.getText().isEmpty()) {
            String msg = inputField.getText();
            // Si el mensaje empieza con '/' se considera un comando
            if (msg.charAt(0) == '/') {
                switch (msg.substring(1).toLowerCase()) {
                    // Comando para borrar la pantalla
                    case "clear":
                        chatBox.setText("");
                        break;
                    // Comando para cambiar el color del usuario
                    case "color":
                        u.setColor(JColorChooser.showDialog(null, "Seleccione un Color", u.getColor()));
                        appendToPane(chatBox, "El color se ha cambiado\n", Color.gray);
                        break;
                    // Comando para cerrar la aplicacion
                    case "exit":
                        System.exit(0);
                        break;
                    // Comando para mostrar el menu de comandos
                    case "help":
                        appendToPane(chatBox, "CLEAR    Borra la pantalla\n", Color.gray);
                        appendToPane(chatBox, "COLOR    Cambia el color del usuario\n", Color.gray);
                        appendToPane(chatBox, "EXIT     Cierra la aplicacion\n", Color.gray);
                        appendToPane(chatBox, "HELP     Muestra el menu de comandos\n", Color.gray);
                        appendToPane(chatBox, "NAME     Cambia el nombre del usuario\n", Color.gray);
                        appendToPane(chatBox, "USERIMAGE    Cambia la imagen del usuario\n", Color.gray);
                        break;
                    // Comando para cambiar el nombre del usuario
                    case "name":
                        String nuevoNombre = JOptionPane.showInputDialog("Ingrese el nuevo nombre", "nombre");
                        if (!nuevoNombre.isBlank()) {
                            u.setNombre(nuevoNombre);
                            appendToPane(chatBox, "Nuevo nombre cambiado a '" + nuevoNombre + "'\n", Color.gray);
                            infoUsuario.setText("Usuario: " + u.getNombre());
                        } else {
                            appendToPane(chatBox, "Nombre no valido" + "\n", Color.gray);
                        }
                        break;
                    // Comando para cambiar la imagen del usuario
                    case "userimage":
                        cambiarImagenUsuario();
                        break;
                    default:
                        // Si el comando no es reconocido se muestra un mensaje
                        appendToPane(chatBox, "'" + msg + "' no es un comando reconocido\n", Color.gray);
                        break;
                }
            } else {
                // Si el mensaje no es un comando se envia al servidor y se muestra en la caja
                // de chat con el color de la letra del usuario
                appendToPane(chatBox, u.getNombre() + "(Tu): ", u.getColor());
                appendToPane(chatBox, msg + "\n", Color.BLACK);
                // Se envia el mensaje al servidor con el nombre del usuario y el color
                cout.println(u.getNombre() + ": " + msg + "¤" + u.getColor().getRGB());
            }
            // Se limpia el campo de texto
            inputField.setText("");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enviar) {
            enviar();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Metodo para enviar mensajes al presionar enter
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            enviar();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // Metodo para seleccionar una imagen de usuario
    public String seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser("src/client/UserIcon");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes", "jpg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        } else {
            return null;
        }
    }

    // Metodo para seleccionar una imagen de fondo
    public String seleccionarImagenFondo() {
        JFileChooser fileChooser = new JFileChooser("src/client/UserBackground");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes", "jpg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        } else {
            return null;
        }
    }

    // Metodo para cambiar la imagen de usuario
    public void cambiarImagenUsuario() {
        String rutaArchivo = seleccionarImagen();
        if (rutaArchivo != null) {
            ImageIcon imageIcon = new ImageIcon(rutaArchivo);
            Image image = imageIcon.getImage();
            Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newimg);
            imgUsuario.setIcon(imageIcon);
        }
    }

}
