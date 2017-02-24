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
				} catch(SocketException e){   //把业务写进try-catch 语句是不恰当的
					System.out.println("程序已退出!");
				} catch (IOException e) {
					System.out.println("从服务器获取数据失败！");
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
		}*/  //此代码段通过join方法来停止线程是比较可取的，但由于readUTF是阻塞式的，导致在
		     // 运行完thread.join(); 后，disconnect()一直执行不完，Frame 窗口一直关不掉
	}		 // 可以打印出如“程序正在退出中...”等语句，随后Thread.sleep(1000); (这段时间内可以给系统做一些善后工作。) 再强制关掉线程
	
	public void connect(){
		try {
			s=new Socket("127.0.0.1",8888);
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			bConnected=true;
		}catch(ConnectException e){
			System.out.println("服务端 尚未启动！");
			System.exit(0);
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
