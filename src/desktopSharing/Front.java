package desktopSharing;

import java.io.File;

import javax.swing.JFileChooser;

public class Front {
	
	
	public static void main(String[] args) {
		
		JFileChooser jfc=new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int s=jfc.showOpenDialog(null);
		
		if(s==JFileChooser.APPROVE_OPTION)
		{
			File f=jfc.getSelectedFile();
			System.out.println(f.getAbsolutePath());
			
		}
		
		
		
		
		
		
		
		
	}

}
