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
	
	public static String convertStringToBinary(String input) {

		StringBuilder result = new StringBuilder();
		char[] chars = input.toCharArray();
		for (char aChar : chars) {
			result.append(String.format("%8s", Integer.toBinaryString(aChar)) // char -> int, auto-cast
					.replaceAll(" ", "0") // zero pads
			);
		}
		return result.toString();

	}

	public static String toBinary(int num) {
		String binary = Integer.toBinaryString(num);

		
		while (binary.length() > 16) {
			int f = (int) binary.charAt(0);
			String temp = binary.substring(1);
			int sum = f + Integer.parseInt(temp, 2);
			binary = Integer.toBinaryString(sum);
		}
		
		while (binary.length() < 16) {
			binary = '0' + binary;
		}
		return binary;
	}

	public static String onesComplement(String binary) {
		String binary2 = "";
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '0')
				binary2 += '1';
			else if (binary.charAt(i) == '1')
				binary2 += '0';
		}
		return binary2;
	}

	public static int summer(String data, int sum) {

		int ind = 0;
		while (true) {
			String t = "";
			while (ind < data.length()) {
				t += data.charAt(ind);
				ind++;
				if (t.length() >= 16)
					break;
			}
			String binary_data = t;
			int newdata = Integer.parseInt(binary_data, 2);
			sum += newdata;
			if (ind >= data.length())
				break;
		}

		return sum;

	}

	public static boolean checksumReceiver(OurPacket pckt) {
		int src = pckt.getSourcePort();
		int dest = pckt.getDestinationPort();
		int len = pckt.getLenghOfData();
		String data = pckt.getData();
		data = convertStringToBinary(data);


		int sum = src + dest + len;
		sum = summer(data, sum);
	
		String result = toBinary(sum+Integer.parseInt(pckt.getChecksum(), 2));
		System.out.println(result);
		

		
		
		for(int i=0; i<result.length(); i++) {
			if(result.charAt(i)=='0')
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		DatagramSocket srvrsckt = new DatagramSocket(6789);
		int Expected_Ack = 0;
		byte[] rcvdata = new byte[1024];
		byte[] sendata = new byte[1024];
		while (true) {
			DatagramPacket rcvpckt = new DatagramPacket(rcvdata,rcvdata.length);
			srvrsckt.receive(rcvpckt);
		
			
			OurPacket rcv_packet = (OurPacket) deserialize(rcvpckt.getData());
			//manually corrupting data
	//			rcv_packet.data="LORENJSDBAFIOHDBHBIOFBDABD";
			
	//	if (checksumReceiver(rcv_packet)) 
	//			System.out.println("The packet is not corrupted");
	//		else
	//			System.out.println("The packet is corrupted");
			
//			System.out.println(rcv_packet.toString());
			
			if(Expected_Ack != rcv_packet.acknowledgeBit || !checksumReceiver(rcv_packet)) {
//				System.out.println("not expected Ack " + "the expected ack = "+Expected_Ack);
				rcv_packet = new OurPacket(rcv_packet.destinationPort,rcv_packet.sourcePort,000,000, rcv_packet.checksum, Expected_Ack, rcv_packet.data);
				if(Expected_Ack != rcv_packet.acknowledgeBit) System.out.println("Recieved From Client: "+ rcv_packet.toString()+" DUP!");
				else {System.out.println("Recieved From Client: "+ rcv_packet.toString()+" Corrupted");}
			}
			else {
//				System.out.println("in the else");
				Expected_Ack = (Expected_Ack==0)?1:0;
				rcv_packet = new OurPacket(rcv_packet.destinationPort,rcv_packet.sourcePort,000,000, rcv_packet.checksum, Expected_Ack, rcv_packet.data);
				System.out.println("Recieved From Client: "+ rcv_packet.toString());

			}
			
			
			
			InetAddress IPaddr = rcvpckt.getAddress();	
			int port = rcvpckt.getPort();
			sendata = serialize(rcv_packet);
			DatagramPacket sendpckt = new DatagramPacket(sendata,sendata.length,IPaddr,port);
			srvrsckt.send(sendpckt);
		}
	}

}