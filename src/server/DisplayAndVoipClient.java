package server;

import java.awt.image.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;

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
	String rollno = null;
	volatile boolean captureScreen = true;

	public DisplayAndVoipClient() {
		try {

			add = JOptionPane.showInputDialog(null, "Server Address", "127.0.0.1");
			// System.out.println(""+add);
			s = new Socket(add, 7500);
			os = s.getOutputStream();
			ois = new ObjectInputStream(s.getInputStream());
			fr.setTitle("Displaying " + add + ":port 2020");
			// fr.addWindowListener(new WindowCloser());
			fr.getContentPane().add(l);
			l.setIcon(icon);

			Dimension d = fr.getToolkit().getScreenSize();
			fr.setSize(300 * d.width / d.height, 300);
			fr.setLocation(d.width / 2 - fr.getSize().width / 2, d.height / 2 - fr.getSize().height / 2);
			fr.addKeyListener(this);
			win.addMouseListener(this);
			// fr.setVisible(true);
			// this.start();

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
		captureScreen = true;
		while (captureScreen) {
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

	public void stopCaptureScreen() {
		captureScreen = false;
	}

	public void sendData(String rollno) throws IOException {

		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		dos.writeUTF(rollno);
//		dos.writeUTF(year);
//		dos.writeUTF(branch);
//		dos.writeUTF(labname);

	}

	public static void main(String arg[]) throws IOException {

		JFrame fr1 = new JFrame("Login");
		fr1.setSize(400, 400);
		fr1.setLayout(new GridBagLayout());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		fr1.setLocation(dim.width / 2 - fr1.getSize().width / 2, dim.height / 2 - fr1.getSize().height / 2);
		fr1.setVisible(true);
		fr1.getContentPane().setBackground(Color.LIGHT_GRAY);

		GridBagConstraints gbc = new GridBagConstraints();

		JLabel lname = new JLabel("Name");
		gbc.gridx = 0;
		gbc.gridy = 0;
		fr1.add(lname, gbc);

		JTextField tname = new JTextField();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 10, 0, 0);
		tname.setPreferredSize(new Dimension(100, 20));
		fr1.add(tname, gbc);

		JLabel lroll = new JLabel("RollNo");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(20, 0, 0, 0);
		fr1.add(lroll, gbc);

		JTextField troll = new JTextField();
		troll.setPreferredSize(new Dimension(100, 20));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(20, 10, 0, 0);
		fr1.add(troll, gbc);

//		JLabel lyear = new JLabel("Year");
//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		gbc.insets = new Insets(20, 0, 0, 0);
//		fr1.add(lyear, gbc);

//		JComboBox<String> tyear = new JComboBox<String>();
//		tyear.addItem("--Select--");
//		tyear.addItem("First");
//		tyear.addItem("Second");
//		tyear.addItem("Third");
//		tyear.addItem("Final");
//		tyear.setPreferredSize(new Dimension(100, 20));
//		gbc.gridx = 1;
//		gbc.gridy = 2;
//		gbc.insets = new Insets(20, 10, 0, 0);
//		fr1.add(tyear, gbc);
//
//		JLabel lbranch = new JLabel("Branch");
//		gbc.gridx = 0;
//		gbc.gridy = 3;
//		gbc.insets = new Insets(20, 0, 0, 0);
//		fr1.add(lbranch, gbc);
//
//		JComboBox<String> tbranch = new JComboBox<String>();
//		tbranch.addItem("--Select--");
//		tbranch.addItem("CSE B.Tech");
//		tbranch.addItem("CSE Dual Degree");
//		tbranch.addItem("CSE IIIT");
//		tbranch.addItem("ECE");
//		tbranch.addItem("EEE");
//		tbranch.addItem("Civil");
//		tbranch.addItem("Mechanical");
//		tbranch.setPreferredSize(new Dimension(100, 20));
//		gbc.gridx = 1;
//		gbc.gridy = 3;
//		gbc.insets = new Insets(20, 10, 0, 0);
//		fr1.add(tbranch, gbc);
//
//		JLabel llab = new JLabel("Lab Name");
//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		gbc.insets = new Insets(20, 0, 0, 0);
//		fr1.add(llab, gbc);
//
//		JComboBox<String> tlab = new JComboBox<String>();
//		tlab.addItem("--Select--");
//		tlab.addItem("DataWarehouse and Data Mining");
//		tlab.addItem("Mobile Computing");
//		tlab.setPreferredSize(new Dimension(150, 20));
//		gbc.gridx = 1;
//		gbc.gridy = 4;
//		gbc.insets = new Insets(20, 10, 0, 0);
//		fr1.add(tlab, gbc);

		JButton submit = new JButton("Submit");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.insets = new Insets(50, 120, 0, 0);
		fr1.add(submit, gbc);

		

		DisplayAndVoipClient tx = new DisplayAndVoipClient();
		DisplayAndVoipClient.VoipClient vc = tx.new VoipClient();

		JFrame fr = new JFrame("Desktop Sharing App");
		fr.setSize(500, 500);
		fr.setLayout(new GridBagLayout());
		fr.setLocation(dim.width / 2 - fr1.getSize().width / 2, dim.height / 2 - fr1.getSize().height / 2);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.getContentPane().setBackground(Color.BLACK);
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridwidth = GridBagConstraints.REMAINDER;
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		gbc1.insets = new Insets(10, 0, 0, 0);

		JButton b1 = new JButton("Share Screen");
		fr.add(b1, gbc1);

		JButton b4 = new JButton("Stop Screen Sharing");
		fr.add(b4, gbc1);

		JButton b2 = new JButton("Open Gateway for Voice");
		fr.add(b2, gbc1);

		JButton b5 = new JButton("Close Gateway for Voice");
		fr.add(b5, gbc1);

		JButton b3 = new JButton("Submit Program");
		fr.add(b3, gbc1);

		//fr.setVisible(true);

		b1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				tx.fr.setVisible(true);
				tx.start();

			}
		});

		b4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tx.stopCaptureScreen();

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

				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int s = jfc.showOpenDialog(null);

				if (s == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					try {
						DisplayAndVoipClient.ProgramSubmit ps = tx.new ProgramSubmit(f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		});

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					String rollno = troll.getText();
//					String year = (String) tyear.getSelectedItem();
//					String branch = (String) tbranch.getSelectedItem();
//					String labname = (String) tlab.getSelectedItem();
					tx.sendData(rollno);
					fr1.dispose();
					fr.setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

	}

	class ProgramSubmit extends Thread {
		Socket s = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		File myfile;

		public ProgramSubmit(File f) throws UnknownHostException, IOException {
			s = new Socket(add, 1300);
			myfile = f;
			dos = new DataOutputStream(s.getOutputStream());
			fis = new FileInputStream(f.getAbsolutePath());
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			this.start();
		}

		public void run() {
			byte buffer[] = new byte[(int) myfile.length()];

			try {
				dis.readFully(buffer, 0, buffer.length);
				dos.writeUTF(myfile.getName());
				dos.writeLong(buffer.length);
				dos.write(buffer, 0, buffer.length);
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
			sock = new Socket(add, 1200);// 1
			out = new BufferedOutputStream(sock.getOutputStream());// 2
			in = new BufferedInputStream(sock.getInputStream());// 3
		}

		public void run() {
			captureAudio();
		}

		public void closeAudio() {
			((CaptureThread) captureThread).captureStop();
		}

		private void captureAudio() {
			try {

				Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
				System.out.println("Available Hardware devices:");
				for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
					System.out.println(cnt + ":" + mixerInfo[cnt].getName());
				}
				audioFormat = getAudioFormat();

				DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

				Mixer mixer = AudioSystem.getMixer(mixerInfo[3]); // Select
																	// Available
																	// Hardware
																	// Devices
																	// for the
																	// micro,
																	// for my
																	// Notebook
																	// it is
																	// number 3.

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
				// stopCapture = false;
				try {
					while (!stopCapture) {

						if (stopCapture == true)
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

			public void captureStop() {
				stopCapture = true;
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
