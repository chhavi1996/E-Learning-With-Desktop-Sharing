package voip;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Server {
ServerSocket MyService;
Socket clientSocket = null;
InputStream input;
TargetDataLine targetDataLine;
OutputStream out;
AudioFormat audioFormat;
SourceDataLine sourceDataLine;
int Size = 10000;
byte tempBuffer[] = new byte[Size];
static Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

Server() throws LineUnavailableException, HeadlessException, UnknownHostException {
//	JFrame.setDefaultLookAndFeelDecorated(true);
//    JFrame frame = new JFrame("Network Phone");
//    JLabel label = new JLabel("Server ip: "+InetAddress.getLocalHost().getHostAddress(), JLabel.CENTER );
//    frame.getContentPane().add( label );
//    
////    JLabel lblNewLabel = new JLabel("");
////    lblNewLabel.setIcon(new ImageIcon("F:\\Eclipse\\Eclipse_for_Java\\1.gif"));
////    frame.getContentPane().add(lblNewLabel, BorderLayout.EAST);
//    
//    frame.setSize(600,400);
//    frame.setVisible(true);
////    ImageIcon img = new ImageIcon("0.png");
////    frame.setIconImage(img.getImage());
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    


    
    ImageIcon imageIcon = new ImageIcon("0.png");
    JLabel label1 = new JLabel(imageIcon);
   // frame.pack();
   // frame.show();
    try {
        Mixer mixer_ = AudioSystem.getMixer(mixerInfo[1]);   // Select Available Hardware Devices for the speaker, for my Notebook it is number 1
        audioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        MyService = new ServerSocket(500);
        clientSocket = MyService.accept();
        captureAudio();
        input = new BufferedInputStream(clientSocket.getInputStream());
        out = new BufferedOutputStream(clientSocket.getOutputStream());
        while (input.read(tempBuffer) != -1) {
            sourceDataLine.write(tempBuffer, 0, Size);

        }

    } catch (IOException e) {

        e.printStackTrace();
    }

}

private AudioFormat getAudioFormat() {
    float sampleRate = 8000.0F;
    int sampleSizeInBits = 16;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = false;
    return new AudioFormat(
            sampleRate,
            sampleSizeInBits,
            channels,
            signed,
            bigEndian);
}

public static void main(String s[]) throws LineUnavailableException, UnknownHostException {
    Server s2 = new Server();
    
}

private void captureAudio() {
    try {

        audioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(
                TargetDataLine.class, audioFormat);
        Mixer mixer = null;
        System.out.println("Server Ip Address "+InetAddress.getLocalHost().getHostAddress());
        System.out.println("Available Hardware Devices:");
        mixer = AudioSystem.getMixer(mixerInfo[3]);         // Select Available Hardware Devices for the micro, for my Notebook it is number 3

        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
            if (mixer.isLineSupported(dataLineInfo)) {
                System.out.println(cnt+":"+mixerInfo[cnt].getName());
                targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
            }
        }
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        Thread captureThread = new CaptureThread();
        captureThread.start();
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
}

class CaptureThread extends Thread {

    byte tempBuffer[] = new byte[Size];

    @Override
    public void run() {
        try {
            while (true) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                out.write(tempBuffer);
                out.flush();

            }

        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}}