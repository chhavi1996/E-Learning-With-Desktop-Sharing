package server;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sql.ConnectionPoolDataSource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class DisplayAndVoipServer extends Thread {

	ServerSocket ss = null;
	// ServerSocket admin=null;
	ServerSocket MyService = null;
	ServerSocket fileService = null;
	String year = null;
	String branch = null;
	String labname=null;
	JTextArea ta=null;

	Vector<ClientThread> clientList = new Vector<ClientThread>();
	static Vector<String> ipList = new Vector<String>();
	long sleepInterval = 125;

	public DisplayAndVoipServer(long interval) throws Exception {
		ss = new ServerSocket(2020);
		MyService = new ServerSocket(1200);
		fileService = new ServerSocket(1300);

		sleepInterval = interval;
		this.setPriority(MIN_PRIORITY);
		
		startServer();
	}

	public void startServer() {
		Socket client = null;
		Socket voip = null;
		Socket file = null;
		createGUI();
		System.out.println("Server listening for client");
		
		while (true) {
			try {

				client = ss.accept();

				ClientThread ct = new ClientThread(client);

				clientList.addElement(ct);
				ct.start();
				voip = MyService.accept();
				VoipServer vs = new VoipServer(voip);
				vs.start();

				file = fileService.accept();
				FileServer fs = new FileServer(file);
				fs.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void createGUI() {
		
		JFrame fr=new JFrame("Server");
		fr.setSize(400, 400);
		fr.setLayout(new GridBagLayout());
		Dimension d = fr.getToolkit().getScreenSize();
		fr.setLocation(d.width / 2 - fr.getSize().width / 2, d.height / 2 - fr.getSize().height / 2);
		fr.setVisible(true);
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel lyear = new JLabel("Year");
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(20, 0, 0, 0);
		fr.add(lyear, gbc);

		JComboBox<String> tyear = new JComboBox<String>();
		tyear.addItem("--Select--");
		tyear.addItem("First");
		tyear.addItem("Second");
		tyear.addItem("Third");
		tyear.addItem("Final");
		tyear.setPreferredSize(new Dimension(100, 20));
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(20, 10, 0, 0);
		fr.add(tyear, gbc);

		JLabel lbranch = new JLabel("Branch");
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(20, 0, 0, 0);
		fr.add(lbranch, gbc);

		JComboBox<String> tbranch = new JComboBox<String>();
		tbranch.addItem("--Select--");
		tbranch.addItem("CSE B.Tech");
		tbranch.addItem("CSE Dual Degree");
		tbranch.addItem("CSE IIIT");
		tbranch.addItem("ECE");
		tbranch.addItem("EEE");
		tbranch.addItem("Civil");
		tbranch.addItem("Mechanical");
		tbranch.setPreferredSize(new Dimension(100, 20));
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(20, 10, 0, 0);
		fr.add(tbranch, gbc);

		JLabel llab = new JLabel("Lab Name");
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 0, 0, 0);
		fr.add(llab, gbc);

		JComboBox<String> tlab = new JComboBox<String>();
		tlab.addItem("--Select--");
		tlab.addItem("DataWarehouse and Data Mining");
		tlab.addItem("Mobile Computing");
		tlab.setPreferredSize(new Dimension(150, 20));
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.insets = new Insets(20, 10, 0, 0);
		fr.add(tlab, gbc);
		
		JButton submit = new JButton("Submit");
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.insets = new Insets(50, 120, 0, 0);
		fr.add(submit, gbc);
		JFrame fr1=new JFrame("Users Connected");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					 year = (String) tyear.getSelectedItem();
					 branch = (String) tbranch.getSelectedItem();
					 labname = (String) tlab.getSelectedItem();
					updateData(year,branch,labname);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				fr.dispose();
				fr1.setVisible(true);

			}

			private void updateData(String year, String branch, String labname) throws SQLException {
				
				Connection con=connectDatabase();
				PreparedStatement ps=con.prepareStatement("update `"+year+"_"+branch+"_"+labname+"` set Total_No_of_Labs="
						+ "Total_No_of_Labs+1;");
				
				int i=ps.executeUpdate();
				
				
			}

		});
		
		/***************************************************************************/
		
		
		fr1.setSize(500, 500);
		fr1.setLocation(d.width / 2 - fr.getSize().width / 2, d.height / 2 - fr.getSize().height / 2);
		
		
	    ta=new JTextArea();
		ta.setSize(500, 500);
		ta.setEditable(false);
		JScrollPane scroll=new JScrollPane(ta);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		fr1.add(scroll);
		
		
	}
	
	public Connection connectDatabase() {
		
		Connection con=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/LabAttendance?useSSL=false","root","mysql@123");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
	}

	public void removeMe(Socket s) {
		clientList.removeElement(s);
	}

	public static void shutdownAll() {
		try {

			System.out.println("Shutting down computer");
			Iterator i = ipList.iterator();
			while (i.hasNext()) {
				System.out.println("shutdown " + i.next());
				Runtime.getRuntime().exec("shutdown -s -m \\" + i.next() + " -t 10");

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	class FileServer extends Thread {
		Socket file = null;
		DataInputStream dis = null;
		FileOutputStream fos = null;

		public FileServer(Socket s) throws IOException {
			file = s;

			dis = new DataInputStream(file.getInputStream());
			String filename = dis.readUTF();
			fos = new FileOutputStream("C:\\Users\\hp\\Documents\\Program Submission\\"+year+"\\"+branch+"\\"+labname+"\\"+filename);

		}

		public void run() {
			int bytesRead;

			try {
				long size = dis.readLong();
				byte[] buffer = new byte[1024];

				while (size > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
					fos.write(buffer, 0, bytesRead);
					size -= bytesRead;
				}

				fos.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ClientThread extends Thread {
		Socket client = null;
		ObjectOutputStream os = null;
		DataInputStream dis = null;

		public ClientThread(Socket ct) {
			client = ct;

			try {

				os = new ObjectOutputStream(ct.getOutputStream());
				dis = new DataInputStream(ct.getInputStream());
				String rollno=dis.readUTF();
				updateData(rollno,year,branch,labname);
				ipList.add(ct.getInetAddress().getHostAddress());
				ta.append("Roll No.:"+rollno+" from " + ct.getInetAddress().getHostAddress() + " connected\n");

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		private void updateData(String rollno, String year, String branch, String labname) {
			
			
			try {
				
				Connection con= connectDatabase();
				PreparedStatement ps=con.prepareStatement("update `"+year+"_"+branch+"_"+labname+"` set No_of_Lab_Attended="
						+ "No_of_Lab_Attended+1 where RollNo='"+rollno+"';");
				
				int i=ps.executeUpdate();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public void run() {
			java.awt.image.BufferedImage img = null;
			Robot r = null;

			try {
				r = new Robot();
			} catch (Exception e) {

				e.printStackTrace();
			}

			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle rect = new Rectangle(0, 0, size.width, size.height);

			javax.swing.ImageIcon icon = null;

			while (true) {
				try {
					System.gc();
					img = r.createScreenCapture(rect);
					icon = new javax.swing.ImageIcon(img);

					os.writeObject(icon);
					os.flush();
					icon = null;
					System.gc();

					try {
						Thread.currentThread().sleep(sleepInterval);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					closeAll();
					break;
				}
			}
		}

		private void closeAll() {
			DisplayAndVoipServer.this.removeMe(client);

			try {
				os.close();
				dis.close();
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	class VoipServer extends Thread {

		// ServerSocket MyService;
		Socket clientSocket = null;
		InputStream input;
		TargetDataLine targetDataLine;
		OutputStream out;
		AudioFormat audioFormat;
		SourceDataLine sourceDataLine;
		int Size = 10000;
		byte tempBuffer[] = new byte[Size];
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

		public VoipServer(Socket client) throws LineUnavailableException {
			clientSocket = client;

			try {
				
//				System.out.println(""+mixerInfo.length);
//				for(int i=0;i<mixerInfo.length;i++)
//				{
//					System.out.println(mixerInfo[i]);
//				}
				Mixer mixer_ = AudioSystem.getMixer(mixerInfo[1]); // Select Available Hardware Devices for the speaker,
																	// for my Notebook it is number 1
				audioFormat = getAudioFormat();
				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();
				input = new BufferedInputStream(clientSocket.getInputStream());// 2
				out = new BufferedOutputStream(clientSocket.getOutputStream());// 3


			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		public void run() {
			captureAudio();
			try {
				while (input.read(tempBuffer) != -1) {
					sourceDataLine.write(tempBuffer, 0, Size);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		private void captureAudio() {
			try {

				audioFormat = getAudioFormat();
				DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
				Mixer mixer = null;
				System.out.println("Server Ip Address " + InetAddress.getLocalHost().getHostAddress());
				System.out.println("Available Hardware Devices:");
				mixer = AudioSystem.getMixer(mixerInfo[3]); // Select Available Hardware Devices for the micro, for my
															// Notebook it is number 3

				for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
					if (mixer.isLineSupported(dataLineInfo)) {
						System.out.println(cnt + ":" + mixerInfo[cnt].getName());
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
		}

	}

	public static void main(String[] args) throws Exception {

		long interval = 80;
		if (args.length == 1)
			interval = Long.parseLong(args[0]);

		// shutbtn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		//
		// System.out.println("Initiating shutting....");
		// shutdownAll();
		//
		// }
		// });

		DisplayAndVoipServer ds = new DisplayAndVoipServer(interval);

	}

}
