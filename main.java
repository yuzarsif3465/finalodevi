import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import DosyaTasimaUygulamasiGUI;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
 
class DosyaTasimaUygulamasiGUI extends JFrame {
 
    private JTextField kaynakDizinTextField;
    private JTextField hedefDizinTextField;
    private JCheckBox sifreleCheckBox;
    private JButton taşıButton;
 
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
 
    public DosyaTasimaUygulamasiGUI() {
        setTitle("Dosya Taşıma ve Şifreleme Uygulaması");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new BorderLayout());
 
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        JLabel kaynakLabel = new JLabel("Kaynak Dizin:");
        kaynakDizinTextField = new JTextField();
        JLabel hedefLabel = new JLabel("Hedef Dizin:");
        hedefDizinTextField = new JTextField();
        JLabel sifreleLabel = new JLabel("Dosyaları Şifrele:");
        sifreleCheckBox = new JCheckBox();
        taşıButton = new JButton("Dosyaları Taşı ve Şifrele");
        taşıButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taşıButtonClicked();
            }
        });
 
        formPanel.add(kaynakLabel);
        formPanel.add(kaynakDizinTextField);
        formPanel.add(hedefLabel);
        formPanel.add(hedefDizinTextField);
        formPanel.add(sifreleLabel);
        formPanel.add(sifreleCheckBox);
        formPanel.add(new JLabel());
        formPanel.add(taşıButton);
 
        add(formPanel, BorderLayout.CENTER);
    }
 
    private void taşıButtonClicked() {
        String kaynakDizin = kaynakDizinTextField.getText();
        String hedefDizin = hedefDizinTextField.getText();
        boolean sifrele = sifreleCheckBox.isSelected();
        String sifre = "0000";
 
        File kaynakDizinFile = new File(kaynakDizin);
        File hedefDizinFile = new File(hedefDizin);
        File[] dosyalar = kaynakDizinFile.listFiles();
 
        if (dosyalar != null) {
            for (File dosya : dosyalar) {
                if (dosya.isFile()) {
                    String dosyaAdi = dosya.getName();
                    File hedefDosya = new File(hedefDizinFile, dosyaAdi);
                    dosya.renameTo(hedefDosya);
 
                    if (sifrele) {
                        try {
                            SecretKey secretKey = generateSecretKey(sifre);
                            encryptFile(hedefDosya.toString(), secretKey);
                            System.out.println("Dosya başarıyla şifrelendi.");
                        } catch (Exception ex) {
                            System.out.println("Şifreleme Hatası: " + ex.getMessage());
                        }
 
                        // Eski dosya silinir.
                        hedefDosya.delete();
                    }
                }
            }
        }
 
        JOptionPane.showMessageDialog(this, "Dosyalar başarıyla taşındı ve şifrelendi.");
    }
 
    public static void encryptFile(String filePath, SecretKey secretKey) throws Exception {
        File inputFile = new File(filePath);
        File encryptedFile = new File(filePath + ".encrypted");
 
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
 
        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new FileOutputStream(encryptedFile)) {
 
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] encryptedBytes = cipher.update(buffer, 0, bytesRead);
                outputStream.write(encryptedBytes);
            }
 
            byte[] encryptedBytes = cipher.doFinal();
            outputStream.write(encryptedBytes);
        }
    }
 
    private static SecretKey generateSecretKey(String password) throws Exception {
        byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, KEY_SIZE / 8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DosyaTasimaUygulamasiGUI gui = new DosyaTasimaUygulamasiGUI();
                gui.setVisible(true);
            }
        });
    }
}