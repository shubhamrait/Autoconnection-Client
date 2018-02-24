package clientthread;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
public class ClientThread 
{
    static boolean running=true; 
    static String macaddr="7c:46:85:80:77:08";
    public static void main(String[] args) throws Exception
    {
        Scanner sc=new Scanner(System.in);
	int port;
        String ipaddr;
         
        System.out.println("Enter port number for comunication");
        port=sc.nextInt();
        
        Process p = Runtime.getRuntime().exec("sudo arp-scan --retry=8 --ignoredups -I wlan0 --localnet");
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "",temp="";
        while ((line = reader.readLine()) != null) 
        {
            if(line.contains(ClientThread.macaddr))
            {
                temp=line;
            }
        }
        String tmp=" ";
        String array[]=temp.split(ClientThread.macaddr);
        ipaddr=array[0].trim();
        
        
                                  
        System.out.println("Enter ip address for comunication  "+ipaddr);
        //ipaddr=new Scanner(System.in).nextLine();
        Thread th=new Thread(new MainThread(ipaddr,port));
        th.start();	
        for(;;)
        {
        if(ClientThread.running==false)
                {
                    {
                    ClientThread.running=true;
                    System.out.println("Trying to connect....");
                    th=new Thread(new MainThread(ipaddr,port));
                    th.start();
                    }
                }
        }
        
    }
}
class ClientThread2
{
    static boolean running=true;
    static String message="Alright";
    static String macaddr="7c:46:85:80:77:08";
}
class MainThread implements Runnable
{
    String ipaddr;
    int port;
    public MainThread(String a,int b)
    {
        this.ipaddr=a;
        this.port=b;
    }
    public void run()
    {
        Socket sock=null;
            try 
            {
                sock = new Socket(this.ipaddr,this.port);
                Thread thread = new Thread(new SendThread(sock));
                thread.start();
                Thread thread2 =new Thread(new RecieveThread(sock));
                thread2.start();
            } 
            catch (Exception e) 
            {
                System.out.println("ERROR!!______________");
                System.out.println(e.getMessage());
                ClientThread.running=false;
                
                //System.exit(0);
            }
        
    }
}           
            /*try
            {
            sock.close();
            }
            catch(Exception xp){System.exit(0);}*/
class RecieveThread implements Runnable
{
    Socket sock=null;
    BufferedReader recieve=null;
	
    public RecieveThread(Socket sock) //constructor to initialise the values
    {
	this.sock = sock;
    }
    public void run() 
    {
        try
        {
            recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));//get inputstream
            String msgRecieved = "Hello";
		while(ClientThread.running==true)
		{
                    msgRecieved = recieve.readLine();
                    if(msgRecieved.equals(null))
                    {
                        break;
                    }
                    System.out.println("Server: " + msgRecieved);
                    System.out.println("");//add a space of one line
		}
                if(ClientThread.running==false)
                {
                    System.out.println("Exit from Receive Thread \n");
                }
        }
        catch(NullPointerException ex)
        {
            System.out.println("ERROR!!______________ receive thread");
            System.out.println(ex.getMessage());
            ClientThread.running=false;
            
        }
            catch(Exception e)
            {
            System.out.println("ERROR!!______________ receive thread");
            System.out.println(e.getMessage());
            ClientThread.running=false;
            
            //System.exit(0);
            }
	}//end run
}//end class recievethread
class SendThread implements Runnable
{
	Socket sock=null;
	PrintWriter print=null;
	BufferedReader brinput=null;
	
	public SendThread(Socket sock)
	{
		this.sock = sock;
	}//end constructor
	public void run()
        {
            try
            {
		if(sock.isConnected())
		{
                    System.out.println("Client connected to "+sock.getInetAddress() + " on port "+sock.getPort());
                    this.print = new PrintWriter(sock.getOutputStream(), true);	
                    while(ClientThread.running==true)
                    {
                        
			this.print.println(ClientThread2.message);
			this.print.flush();
                        
                    }//end while
		//sock.close();
                    if(ClientThread.running==false)
                    {
                        System.out.println("Exit from Send Thread \n");
                    }
                }
            }
            catch(Exception e)
            {                
                System.out.println("ERROR!!______________ send thread");
                System.out.println(e.getMessage());
                ClientThread.running=false;
                
                //System.exit(0);
            }
	}//end run method
}//end class

