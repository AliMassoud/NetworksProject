import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	public static byte[] serialize(OurPacket obj) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		byte [] dt = out.toByteArray();
		out.close();
		os.close();
		return dt;
		
	
	}
	
	public static Object deserialize(byte []data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		Object b = is.readObject();
		is.close();
		in.close();
		return b;
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		BufferedReader infromusr = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientsckt = new DatagramSocket();
		
		InetAddress IPaddress = InetAddress.getByName("localhost");
		
		byte[] sendata = new byte[1024];
		byte[] rcvdata = new byte [1024];
		String data = "";
		File inputFile = new File("test.txt");
		Scanner sc = new Scanner(inputFile);
		while(sc.hasNextLine()) {
			data += sc.nextLine();
		}
		OurPacket send_packet_class = new OurPacket(6789,6789,1024,5,"1011101110",0,data);
		sendata = serialize(send_packet_class);
		
		DatagramPacket sendpckt = new DatagramPacket(sendata,sendata.length,IPaddress,6789);
		
		clientsckt.send(sendpckt);
		
		DatagramPacket rcvpckt = new DatagramPacket (rcvdata,rcvdata.length);
		clientsckt.receive(rcvpckt);
		
		OurPacket rcv_packet_class = (OurPacket)deserialize(rcvpckt.getData());
		System.out.println("From Server "+rcv_packet_class.toString());
		clientsckt.close();
	}

}
