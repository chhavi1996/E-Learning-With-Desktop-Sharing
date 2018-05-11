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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class DisplayAndVoipClient extends Thread implements KeyListener, MouseListener {

	Socket s = null;
	ObjectInputStream ois = null;
	OutputStream os = null;

	JLabel l = new JLabel();
	JWindow win = new JWindow();
	ImageIcon icon = new ImageIcon();
	JFrame fr = new JFrame();
	String add = null;

	public DisplayAndVoipClient() {
		try {
			add = JOptionPane.showInputDialog(null, "Server Address", "192.168.43.69");
			// System.out.println(""+add);
			s = new Socket(add, 2020);
			os = s.getOutputStream();
			ois = new ObjectInputStream(s.getInputStream());
			fr.setTitle("Displaying " + add + ":port 2020");
			fr.addWindowListener(new WindowCloser());
			fr.getContentPane().add(l);
			l.setIcon(icon);

			Dimension d = fr.getToolkit().getScreenSize();
			fr.setSize(300 * d.width / d.height, 300);
			fr.addKeyListener(this);
			win.addMouseListener(this);
			//fr.setVisible(true);
			//this.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent ke) {
	}

	public void keyReleased(KeyEvent ke) {
		int code = ke.getKeyCode();
		if (code == KeyEvent.VK_F)
			switchDisplay();
		else if (code == KeyEvent.VK_X) {
			try {
				s.close();
			} catch (Exception ex) {
			}
			System.exit(0);
		}
	}

	public void keyTyped(KeyEvent ke) {
	}

	public void mouseClicked(java.awt.event.MouseEvent me) {
		switchDisplay();
	}

	public void mouseEntered(java.awt.event.MouseEvent me) {
	}

	public void mouseExited(java.awt.event.MouseEvent me) {
	}

	public void mousePressed(java.awt.event.MouseEvent me) {
	}

	public void mouseReleased(java.awt.event.MouseEvent me) {
	}

	private void switchDisplay() {
		if (fr.isVisible()) {
			fr.setVisible(false);
			fr.getContentPane().removeAll();
			win.getContentPane().removeAll();
			win.getContentPane().add(l);
			win.setSize(win.getToolkit().getScreenSize());
			win.setVisible(true);
			win.requestFocusInWindow();
		} else {
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
		while (true) {
			try {
				d = fr.getContentPane().getSize();
				icon = (ImageIcon) ois.readObject();
				if (d == null || icon == null)
					continue;
				if (d.width > 0 && d.height > 0 && (d.width != icon.getIconWidth() || d.height != icon.getIconHeight()))
					icon.setImage(icon.getImage().getScaledInstance(d.width, d.height, i.SCALE_FAST));
				l.setIcon(icon);
				l.validate();
				fr.validate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			try {
				s.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static void main(String arg[]) throws IOException {
		
		
		DisplayAndVoipClient tx = new DisplayAndVoipClient();
		DisplayAndVoipClient.VoipClient vc = tx.new VoipClient();
		
		JFrame f=new JFrame("Desktop Sharing App");
		f.setSize(700,400);
		f.getContentPane().setBackground(Color.BLACK);
		f.setVisible(true);
		
		JButton b1=new JButton("Share Screen");
		JButton b2=new JButton("Open Gateway for Voice");
		JButton b5=new JButton("Close Gateway for Voice");
		JButton b3=new JButton("Submit Program");
		JButton b4=new JButton("Remote Shutdown");
		
		b1.setBounds(140, 45, 350, 60);
		b2.setBounds(140, 120, 350, 60);
		b5.setBounds(140,195,350,60);
		b3.setBounds(140, 270, 350, 60);
		b4.setBounds(140,345,350,60);
		f.add(b1);
		f.add(b2);
		f.add(b5);
		f.add(b3);
		f.add(b4);
		
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				tx.fr.setVisible(true);
				tx.start();
				
			}
		});
		
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vc.start();
				
			}
		});
		
		b5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vc.closeAudio();
				
			}
		});
		
		b3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int s=jfc.showOpenDialog(null);
				
				if(s==JFileChooser.APPROVE_OPTION)
				{
					File f=jfc.getSelectedFile();
					try {
						DisplayAndVoipClient.ProgramSubmit ps=tx.new ProgramSubmit(f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		
		
		
		
		
		
		
//		DisplayAndVoipClient tx = new DisplayAndVoipClient();// 4
//		DisplayAndVoipClient.VoipClient vc = tx.new VoipClient();;
		/*********************************************************************/
//		Display display = Display.getDefault();
//		Shell shell = new Shell();
//		shell.setSize(850, 400);
//		shell.setText("Voip Phone");
//
//		Button btnNewButton = new Button(shell, SWT.NONE);
//		Button btn2=new Button(shell,SWT.NONE);
//		btnNewButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//					vc.start();
//			}
//		});
//		
//		btn2.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//					vc.closeAudio();
//			}
//			
//		});
//		btnNewButton.setBounds(37, 35, 261, 79);
//		btnNewButton.setText("Open Gateway");
//		btn2.setBounds(37, 100, 261, 130);
//		btn2.setText("Close Gateway");
//
//		shell.open();
//		shell.layout();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}

	}
	
	class ProgramSubmit extends Thread
	{
		Socket s=null;
		DataOutputStream dos=null;
		FileInputStream fis=null;
		DataInputStream dis=null;
		BufferedInputStream bis=null;
		File myfile;
		
		public ProgramSubmit(File f) throws UnknownHostException, IOException
		{
			s=new Socket("192.168.43.69",90);
			myfile=f;
			dos=new DataOutputStream(s.getOutputStream());
			fis=new FileInputStream(f.getAbsolutePath());
			bis=new BufferedInputStream(fis);
			dis=new DataInputStream(bis);
			
			this.start();
		}
		
		public void run()
		{
			byte buffer[]=new byte[(int)myfile.length()];
			
			try {
				dis.readFully(buffer,0,buffer.length);
				dos.writeUTF(myfile.getName());
				dos.writeLong(buffer.length);
				dos.write(buffer,0,buffer.length);
				dos.flush();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class VoipClient extends Thread {
		volatile boolean stopCapture = false;
		ByteArrayOutputStream byteArrayOutputStream;
		AudioFormat audioFormat;
		TargetDataLine targetDataLine;
		AudioInputStream audioInputStream;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		Socket sock = null;
		SourceDataLine sourceDataLine;
		Thread captureThread;

		public VoipClient() throws IOException {
			sock = new Socket("192.168.43.69", 500);//1
			
			out = new BufferedOutputStream(sock.getOutputStream());// 2
			in = new BufferedInputStream(sock.getInputStream());// 3
		}

		public void run() {
				captureAudio();
		}
		
		public void closeAudio()
		{
			((CaptureThread) captureThread).captureStop();
		}

		private void captureAudio() {
			try {
				// s = new Socket("192.168.43.69", 500);//1
				// out = new BufferedOutputStream(s.getOutputStream());//2
				// in = new BufferedInputStream(s.getInputStream());//3

				Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
				System.out.println("Available Hardware devices:");
				for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
					System.out.println(cnt + ":" + mixerInfo[cnt].getName());
				}
				audioFormat = getAudioFormat();

				DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

				Mixer mixer = AudioSystem.getMixer(mixerInfo[3]); // Select Available Hardware Devices for the micro,
																	// for my Notebook it is number 3.

				targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);

				targetDataLine.open(audioFormat);
				targetDataLine.start();

				captureThread = new CaptureThread();
				captureThread.start();
				System.out.println("Capturing....");
				DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();

				Thread playThread = new PlayThread();
				playThread.start();
				System.out.println("Playing....");


			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
		}

		private AudioFormat getAudioFormat() {
			float sampleRate = 8000.0F;

			int sampleSizeInBits = 16;

			int channels = 2;

			boolean signed = true;

			boolean bigEndian = false;

			return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		}

		class CaptureThread extends Thread {

			byte tempBuffer[] = new byte[10000];

			@Override
			public void run() {
				byteArrayOutputStream = new ByteArrayOutputStream();
			//	stopCapture = false;
				try {
					while (!stopCapture) {

						if(stopCapture==true)
							System.out.println("Not stopped!!!!!");
						int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);

						out.write(tempBuffer);

						if (cnt > 0) {

							byteArrayOutputStream.write(tempBuffer, 0, cnt);

						}
					}
					byteArrayOutputStream.close();
					System.out.println("Byte array stream is closed!!");
				} catch (Exception e) {
					System.out.println(e);
					System.exit(0);
				}
			}
			
			
			public void captureStop()
			{
				stopCapture=true;
				System.out.println("Audio capture is stopped!!!");
			}
		}


class PlayThread extends Thread {

			byte tempBuffer[] = new byte[10000];

			@Override
			public void run() {
				try {
					while (in.read(tempBuffer) != -1 && !stopCapture) {
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

}
