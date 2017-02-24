import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.awt.event.*;
import java.io.*;


public class ChatClient extends Frame{
	TextField tfTxt=new TextField ();
	TextArea taContent = new TextArea ();
	DataOutputStream dos=null;
	DataInputStream dis=null;
	Socket s=null;
	private boolean bConnected=false;
	Thread thread=new Thread(new receiveThread());
	
	public static void main(String[] args) {
		new ChatClient().Launch();

	}
	public void Launch(){
		this.setBounds(200, 200, 300, 300);
		this.setBackground(Color.GRAY);
		add(tfTxt,BorderLayout.SOUTH);
		add(taContent,BorderLayout.NORTH);
		pack();
		tfTxt.addActionListener(new tfListener());
		addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e){
				disconnect();
				System.exit(0);
			}	
		});
		setVisible(true);	
		connect();
		thread.start();
	}
	
	private class tfListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String str=tfTxt.getText();
			tfTxt.setText("");	
			try {
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		
	}	
	
	private class receiveThread implements Runnable{

		public void run() {
			try {
				while(bConnected && s!=null){
					String str1=dis.readUTF();
					taContent.setText(taContent.getText()+str1+'\n');
					}
				} catch(SocketException e){   //��ҵ��д��try-catch ����ǲ�ǡ����
					System.out.println("�������˳�!");
				} catch (IOException e) {
					System.out.println("�ӷ�������ȡ����ʧ�ܣ�");
					e.printStackTrace();
				}
				finally{
					try{
						if(dos!=null) dos.close();
						if(dis!=null) dis.close();
						if(s!=null) s.close();
						System.exit(0);
					}catch(IOException e){
						e.printStackTrace();
					}
					
				}
			}
		}
	
	
	
	public void disconnect(){
		try {
			dis.close();
			dos.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*try{
			bConnected=false;
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}finally{
			try {
				dis.close();
				dos.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/  //�˴����ͨ��join������ֹͣ�߳��ǱȽϿ�ȡ�ģ�������readUTF������ʽ�ģ�������
		     // ������thread.join(); ��disconnect()һֱִ�в��꣬Frame ����һֱ�ز���
	}		 // ���Դ�ӡ���硰���������˳���...������䣬���Thread.sleep(1000); (���ʱ���ڿ��Ը�ϵͳ��һЩ�ƺ�����) ��ǿ�ƹص��߳�
	
	public void connect(){
		try {
			s=new Socket("127.0.0.1",8888);
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			bConnected=true;
		}catch(ConnectException e){
			System.out.println("����� ��δ������");
			System.exit(0);
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
