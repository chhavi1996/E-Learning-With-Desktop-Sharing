package desktopSharing;

import java.awt.image.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class DisplayClient extends Thread implements KeyListener,MouseListener{
	
	Socket s=null;
	ObjectInputStream ois=null;
	OutputStream os=null;

	JLabel l=new JLabel();
	JWindow win=new JWindow();
	ImageIcon icon=new ImageIcon();
	JFrame fr=new JFrame();
	String add=null;
	
	/*............................*/
	
	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	BufferedOutputStream out = null;
	BufferedInputStream in = null;
	//Socket sock = null;
	SourceDataLine sourceDataLine;

	public DisplayClient()
	{
		try
		{
			add=JOptionPane.showInputDialog(null,"Server Address","127.0.0.1");
			//System.out.println(""+add);
			s=new Socket(add,2020);
			os=s.getOutputStream();
			ois=new ObjectInputStream(s.getInputStream());
			fr.setTitle("Displaying "+add+":port 2020");
			fr.addWindowListener(new WindowCloser());
			fr.getContentPane().add(l);
			l.setIcon(icon);

			Dimension d=fr.getToolkit().getScreenSize();
			fr.setSize(300*d.width/d.height,300);
			fr.addKeyListener(this);
			win.addMouseListener(this);
			fr.setVisible(true);
			this.start();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	 public void keyPressed(KeyEvent ke) { }
	 public void keyReleased(KeyEvent ke) {
	 int code = ke.getKeyCode();
	 if(code == KeyEvent.VK_F)
	 switchDisplay();
	 else if(code == KeyEvent.VK_X) {
	 try { s.close(); } catch(Exception ex) {}
	 System.exit(0);
	 }
	 }
	 public void keyTyped(KeyEvent ke) { }

	 public void mouseClicked(java.awt.event.MouseEvent me) {
	 switchDisplay();
	 }
	 public void mouseEntered(java.awt.event.MouseEvent me) {}
	 public void mouseExited(java.awt.event.MouseEvent me) {}
	 public void mousePressed(java.awt.event.MouseEvent me) {}
	 public void mouseReleased(java.awt.event.MouseEvent me) {}

	 private void switchDisplay() {
		if(fr.isVisible()) {
			fr.setVisible(false);
			fr.getContentPane().removeAll();
			win.getContentPane().removeAll();
			win.getContentPane().add(l);
			win.setSize(win.getToolkit().getScreenSize());
			win.setVisible(true);
			win.requestFocusInWindow();
		}
		else {
			 win.setVisible(false);
			 win.getContentPane().removeAll();
			 fr.getContentPane().removeAll();
			 fr.getContentPane().add(l);
			 fr.setVisible(true);
			 fr.requestFocus();
		}
	}

	public void run() {
 Dimension d = null;
 BufferedImage i = null;
 while(true) {
 try {
 d = fr.getContentPane().getSize();
 icon = (ImageIcon)ois.readObject();
 if(d == null || icon == null) continue;
 if(d.width>0 && d.height>0 && (d.width != icon.getIconWidth() || d.height != icon.getIconHeight())) 
 icon.setImage(icon.getImage().getScaledInstance(d.width, d.height, i.SCALE_FAST));
 l.setIcon(icon);
 l.validate();
 fr.validate();
 } catch(Exception ex) { ex.printStackTrace(); }
 }
 }

 class WindowCloser extends WindowAdapter {
 public void windowClosing(WindowEvent we) {
 try { s.close(); } catch(Exception ex) { ex.printStackTrace(); }
 System.exit(0);
 }
 }

 public static void main(String arg[]) {
	 DisplayClient tx = new DisplayClient();//4
  
  /*********************************************************************/
  Display display = Display.getDefault();
	Shell shell = new Shell();
	shell.setSize(850, 400);
	shell.setText("Voip Phone");
	
	Button btnNewButton = new Button(shell, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			// DisplayClient tx = new DisplayClient();
		    tx.captureAudio();
		}
	});
	btnNewButton.setBounds(37, 35, 261, 79);
	btnNewButton.setText("Open Gateway");

	shell.open();
	shell.layout();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) {
			display.sleep();
		}
	}

 }
 
 
 private void captureAudio() {
	    try {
	        s = new Socket("192.168.0.107", 500);//1
	        out = new BufferedOutputStream(s.getOutputStream());//2
	        in = new BufferedInputStream(s.getInputStream());//3

	        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	        System.out.println("Available Hardware devices:");
	        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
	            System.out.println(cnt+":"+mixerInfo[cnt].getName());
	        }
	        audioFormat = getAudioFormat();

	        DataLine.Info dataLineInfo = new DataLine.Info(
	                TargetDataLine.class, audioFormat);

	        Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);    //Select Available Hardware Devices for the micro, for my Notebook it is number 3.

	        targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);

	        targetDataLine.open(audioFormat);
	        targetDataLine.start();

	        Thread captureThread = new CaptureThread();
	        captureThread.start();

	        DataLine.Info dataLineInfo1 = new DataLine.Info(
	                SourceDataLine.class, audioFormat);
	        sourceDataLine = (SourceDataLine) AudioSystem
	                .getLine(dataLineInfo1);
	        sourceDataLine.open(audioFormat);
	        sourceDataLine.start();

	        Thread playThread = new PlayThread();
	        playThread.start();

	    } catch (Exception e) {
	        System.out.println(e);
	        System.exit(0);
	    }
	}
	class CaptureThread extends Thread {

	    byte tempBuffer[] = new byte[10000];

	    @Override
	    public void run() {
	        byteArrayOutputStream = new ByteArrayOutputStream();
	        stopCapture = false;
	        try {
	            while (!stopCapture) {

	                int cnt = targetDataLine.read(tempBuffer, 0,
	                        tempBuffer.length);

	                out.write(tempBuffer);

	                if (cnt > 0) {

	                    byteArrayOutputStream.write(tempBuffer, 0, cnt);

	                }
	            }
	            byteArrayOutputStream.close();
	        } catch (Exception e) {
	            System.out.println(e);
	            System.exit(0);
	        }
	    }
	}
	
	private AudioFormat getAudioFormat() {
	    float sampleRate = 8000.0F;

	    int sampleSizeInBits = 16;

	    int channels = 2;

	    boolean signed = true;

	    boolean bigEndian = false;

	    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
	            bigEndian);
	}
	
	class PlayThread extends Thread {

	    byte tempBuffer[] = new byte[10000];

	    @Override
	    public void run() {
	        try {
	            while (in.read(tempBuffer) != -1) {
	                sourceDataLine.write(tempBuffer, 0, 10000);

	            }
	            sourceDataLine.drain();
	            sourceDataLine.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	  }


}