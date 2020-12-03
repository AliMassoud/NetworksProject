import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
	public static byte[] serialize(OurPacket obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		byte[] dt = out.toByteArray();
		out.close();
		os.close();
		return dt;
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		Object b = is.readObject();
		is.close();
		in.close();
		return b;
	}

	public static final int MAX_PACKET_DATA_SIZE = 512;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		BufferedReader infromusr = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientsckt = new DatagramSocket();

		InetAddress IPaddress = InetAddress.getByName("localhost");

		byte[] sendata = new byte[1024];
		byte[] rcvdata = new byte[1024];
		String data = "";
		File inputFile = new File("test.txt");
		Scanner sc = new Scanner(inputFile);
		String temp = "";
//		int seqNum = 0;
		int ack = 0, linepointer = 0;

		boolean fileIsSent = false;

		while (!fileIsSent) {

			//This is for fragmentation of the file to be sent
			boolean Full = false;
			while (!Full) {
				while (linepointer < temp.length()) {
					data += temp.charAt(linepointer);
					
					if (data.getBytes().length == MAX_PACKET_DATA_SIZE) {
						Full = true;
						break;
					}
					linepointer++;
				}
				if (linepointer == temp.length()) {
					linepointer = 0;
					if(sc.hasNext())
						temp = sc.nextLine();
					else Full = true;
				}
			}
			//Fragmentation ends
			
			//Sending -- no changes
			OurPacket packet1 = new OurPacket(6789, 6789, 1024, ack, "1011101110", 0, data);
			sendata = serialize(packet1);
			DatagramPacket sendpckt = new DatagramPacket(sendata, sendata.length, IPaddress, 6789);

			try {
				clientsckt.send(sendpckt);
				clientsckt.setSoTimeout(5000);
				DatagramPacket rcvpckt = new DatagramPacket(rcvdata, rcvdata.length);
				clientsckt.receive(rcvpckt);

				OurPacket pak_rcv = (OurPacket) deserialize(rcvpckt.getData());
				System.out.println("From Server " + pak_rcv.toString());

				while (ack != pak_rcv.acknowledgeBits) {// check seqNum in phase four -- checksum for phase 3
					sendpckt = new DatagramPacket(sendata, sendata.length, IPaddress, 6789);

					clientsckt.send(sendpckt);
					clientsckt.setSoTimeout(1000);
					rcvpckt = new DatagramPacket(rcvdata, rcvdata.length);
					clientsckt.receive(rcvpckt);
					pak_rcv = (OurPacket) deserialize(rcvpckt.getData());
				}
//				seqNum++;
				ack = (ack == 0) ? 1 : 0;
//				if(seqNum-1 == PacketsReuired)
//					fileIsSent =true;
			} catch (Exception e) {
				System.out.println("Timeout exceeded");
			}
		}
		clientsckt.close();

	}

}
