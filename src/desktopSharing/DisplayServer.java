package desktopSharing;

import java.net.*;
import java.awt.*;
import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;



public class DisplayServer extends Thread
{
	ServerSocket ss=null;
	ServerSocket admin=null;
	

	Vector<ClientThread> clientList=new Vector<ClientThread>();
	long sleepInterval=125;

	public DisplayServer(long interval) throws Exception
	{
		ss=new ServerSocket(2020);
		admin=new ServerSocket(2021);
		sleepInterval=interval;
		this.setPriority(MIN_PRIORITY);
		this.start();
		startServer();
	}

	public void startServer()
	{
		Socket client=null;
		System.out.println("Server listening for client");

		while(true)
		{
			try{

				client=ss.accept();
				ClientThread ct=new ClientThread(client);
				clientList.addElement(ct);
				ct.start();
			}
			catch (Exception e) {
				e.printStackTrace();	
			}
		}
	}


	public void run()
	{
		String localhost="127.0.0.1";

		try{

			localhost=InetAddress.getLocalHost().getHostAddress();
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}

		while(true)
		{
			try{

				Socket ad=admin.accept();
				if(ad.getInetAddress().getHostAddress().equals(localhost) && ad.getInputStream().read()=='X')
				{
					ss.close();
					for(int i=0,n=clientList.size();i<n;i++)
						clientList.get(i).client.close();

					System.exit(0);
				}
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	}

	public void removeMe(Socket s)
	{
		clientList.removeElement(s);
	}



class ClientThread extends Thread
{
	Socket client=null;
	ObjectOutputStream os=null;
	InputStream is=null;

	public ClientThread(Socket ct)
	{
		client=ct;

		try{

			os=new ObjectOutputStream(ct.getOutputStream());
			is=ct.getInputStream();
			System.out.println("Client from "+ct.getInetAddress().getHostAddress()+" connected");

		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void run()
	{
		java.awt.image.BufferedImage img=null;
		Robot r =null;

		try
		{
			r=new Robot();
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}

		Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rect=new Rectangle(0,0,size.width,size.height);

		javax.swing.ImageIcon icon=null;

		while(true)
		{
			try
			{
				System.gc();
				img=r.createScreenCapture(rect);
				icon=new javax.swing.ImageIcon(img);

				os.writeObject(icon);
				os.flush();
				icon=null;
				System.gc();

				try
				{
					Thread.currentThread().sleep(sleepInterval);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (Exception e) {
				closeAll();
				break;
			}
		}
	}

	private void closeAll()
	{
		DisplayServer.this.removeMe(client);

		try
		{
			os.close();
			is.close();
			client.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class VoipServer{
	
	ServerSocket MyService;
	Socket clientSocket = null;
	InputStream input;
	TargetDataLine targetDataLine;
	OutputStream out;
	AudioFormat audioFormat;
	SourceDataLine sourceDataLine;
	int Size = 10000;
	byte tempBuffer[] = new byte[Size];
	Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	
	public VoipServer() throws LineUnavailableException
	{
		try {
	        Mixer mixer_ = AudioSystem.getMixer(mixerInfo[1]);   // Select Available Hardware Devices for the speaker, for my Notebook it is number 1
	        audioFormat = getAudioFormat();
	        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
	        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        sourceDataLine.open(audioFormat);
	        sourceDataLine.start();
	        MyService = new ServerSocket(500);
	        clientSocket = MyService.accept();
	        
	       // while(clientSocket==null);//1
	        captureAudio();
	        input = new BufferedInputStream(clientSocket.getInputStream());//2
	        out = new BufferedOutputStream(clientSocket.getOutputStream());//3
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
	}
	
}

public static void main(String[] args) throws Exception {
	
	long interval=80;
	if(args.length==1)
		interval=Long.parseLong(args[0]);

	DisplayServer ds=new DisplayServer(interval);
	DisplayServer.VoipServer vs=ds.new VoipServer();
	
	
}

}