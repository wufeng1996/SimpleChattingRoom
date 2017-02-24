import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	ServerSocket ss=null;
	boolean started=false;
	List<Client> clients=new ArrayList<Client>();
	
	public void start(){
		try {
			ss=new ServerSocket(8888);
			started=true;
		}catch(BindException e){
			System.out.println("端口使用中...\n请退出服务器并重新启动...");
			System.exit(0);
		}catch(IOException e){
			e.printStackTrace();
		}
		try{
			while(started){
				Socket s = ss.accept();    //主线程阻塞式监听用户请求
				System.out.println("A client connected!");
				Client c=new Client(s);   //为每一个客户端开启单独线程
				clients.add(c);
				new Thread(c).start();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(ss!=null){
						ss.close();						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	

	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	
	
	class Client implements Runnable{
		private Socket s;
		private DataInputStream dis=null;
		private DataOutputStream dos=null;
		private boolean bconnected=false;
		
		public Client(Socket s){
			this.s=s;
			try {
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				bconnected=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str){
			try {
				dos.writeUTF(str);
			}catch(SocketException e){
				System.out.println("客户端已退出了，不要在发给它了！");
				clients.remove(this); // 把业务逻辑写进catch 不好
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try{
				while(bconnected){
					String str=dis.readUTF();
					//System.out.println(str);
					for(int i=0;i<clients.size();i++){
						clients.get(i).send(str);
					}
				}
			}catch(EOFException e){
				System.out.println("一个客户端已退出!");
			}catch (IOException e) {
				e.printStackTrace();
			}finally{			
				try{
					if(dis!=null) dis.close();
					if(dos!=null) dos.close();
					if(s!=null) s.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			}
		
		}
		
	}

