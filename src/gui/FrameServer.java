package gui;

import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameServer extends JFrame {

    public static final int PUERTO = 9999;

    private final JPanel mainPanel;
    private final OperatingSystemMXBean mxBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
    private Icon gif;
    private ImageIcon imageCheck;
    private final JLabel labelGif;
    private final JLabel labelMessage;

    public FrameServer(final String title) {
        super(title);
        setSystemLookAndFeel();
        labelMessage = new JLabel();
        labelMessage.setFont(new Font("Sans-Serif", Font.BOLD, 22));
        labelMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelGif = new JLabel();
        labelGif.setSize(400, 200);
        labelGif.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel = new JPanel();
        mainPanel.add(labelMessage);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(labelGif);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        BoxLayout boxlayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(boxlayout);

        this.add(mainPanel);
        Thread thread = new Thread(this::run);
        thread.start();
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            FrameServer window = new FrameServer("Server Connection");
            window.setResizable(false);
            window.setSize(800,600);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }


    public void run() {
        labelMessage.setText("Connecting to Client...");
        gif = new ImageIcon(this.getClass().getResource("/gui/images/animation.gif"));
        labelGif.setIcon(gif);

        try (ServerSocket server = new ServerSocket(PUERTO);
             Socket socket = server.accept();
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Inside try");
            settingImageDuringConnection();

            while (true) {
                Thread.sleep(1000);
                double data = mxBean.getSystemCpuLoad() * 100;
                dataOutputStream.writeDouble(data);
            }

        } catch (IOException | InterruptedException exception) {
            System.out.println("Client shutdown the socket connection");
            run();
        }
    }

    private void settingImageDuringConnection() {

        imageCheck = new ImageIcon(this.getClass().getResource("/gui/images/check.png"));
        Image getImage = imageCheck.getImage();
        Image imgScale = getImage.getScaledInstance(400, 400, Image.SCALE_DEFAULT);
        ImageIcon scaledImage = new ImageIcon(imgScale);

        labelGif.setIcon(scaledImage);
        labelMessage.setText("Connected Successfully");

    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
