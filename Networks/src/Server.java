import java.net.*;
import java.io.*;
public class Server {

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
		DatagramSocket srvrsckt = new DatagramSocket(6789);
		byte[] rcvdata = new byte[1024];
		byte[] sendata = new byte[1024];
		while (true) {
			DatagramPacket rcvpckt = new DatagramPacket(rcvdata,rcvdata.length);
			srvrsckt.receive(rcvpckt);
			
			OurPacket rcv_packet_class = (OurPacket) deserialize(rcvpckt.getData());
			
			InetAddress IPaddr = rcvpckt.getAddress();	
			int port = rcvpckt.getPort();
			//String capitalizedword = rcv_packet_class.toUpperCase();
			
//			String send = rcv_packet_class.data.toUpperCase();
			//OurPacket send_packet_class = new OurPacket(); //////
//			System.out.println(send);
			sendata = serialize(rcv_packet_class);
			
			DatagramPacket sendpckt = new DatagramPacket(sendata,sendata.length,IPaddr,port);
			srvrsckt.send(sendpckt);
			
		}
	}

}