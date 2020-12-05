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

	public static void checksumSender(OurPacket pckt) {
		int src = pckt.getSourcePort();
		int dest = pckt.getDestinationPort();
		int len = pckt.getLenghOfData();
		String data = pckt.getData();
		data = convertStringToBinary(data);

		int sum = src + dest + len;
		sum = summer(data, sum);

		String strSum = toBinary(sum);
		String str = onesComplement(strSum);
		pckt.setChecksum(str);
	}

	public static final int MAX_PACKET_DATA_SIZE = 48;

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
//		OurPacket pak_rcv = new OurPacket();
		int seqNum = 0;
		int ack = 0, linepointer = 0;

		boolean repeat = false;
		boolean fileIsSent = false;
		boolean receivedFile = false;
		boolean UpdatedAck = true;
		
		while (!fileIsSent) {
			// This is for fragmentation of the file to be sent
			boolean Full = false;
			while (!Full && !repeat) {
				while (linepointer < temp.length()) {
					data += temp.charAt(linepointer);

					if (data.getBytes().length == MAX_PACKET_DATA_SIZE) {
						Full = true;
						break;
					}
					linepointer++;
				}
				if (linepointer == temp.length()) {
					if (sc.hasNext()) {
						linepointer = 0;
						temp = sc.nextLine();
					} else {

						Full = true;
					}
				}
			}
			linepointer++;
			// Fragmentation ends
			// Sending -- no changes
			OurPacket packet1 = new OurPacket(6789, 6789, 1024, 0, "1011101110", ack, data);
			
			if (!sc.hasNext() && linepointer >= temp.length()) {
				packet1.setLastFrags();
			}
			checksumSender(packet1);
			sendata = serialize(packet1);
			DatagramPacket sendpckt = new DatagramPacket(sendata, sendata.length, IPaddress, 6789);
			//System.out.println(packet1.toString());
			try {
				clientsckt.send(sendpckt);
				clientsckt.setSoTimeout(1000);
				DatagramPacket rcvpckt = new DatagramPacket(rcvdata, rcvdata.length);
				clientsckt.receive(rcvpckt);
				OurPacket pak_rcv = (OurPacket) deserialize(rcvpckt.getData());
				
//
//				System.out.println("The PACKET");
//				System.out.println("Sent from client  "+ packet1.toString());
//				System.out.println("received from server   "+pak_rcv.toString());
//				
				while (ack == pak_rcv.acknowledgeBit) {// check seqNum in phase four -- checksum for phase 3
					try {
						System.out.println("Inner while");
					sendpckt = new DatagramPacket(sendata, sendata.length, IPaddress, 6789);
					clientsckt.send(sendpckt);
					clientsckt.setSoTimeout(1000);
					rcvpckt = new DatagramPacket(rcvdata, rcvdata.length);
					clientsckt.receive(rcvpckt);
					pak_rcv = (OurPacket) deserialize(rcvpckt.getData());
					}catch (Exception e) {
						System.out.println("Timeout exceeded two");
						repeat = true;
						continue;
					}
				}
				
				System.out.println("From Server " + pak_rcv.toString());
				ack = (ack == 0) ? 1 : 0;
				repeat = false;
				data = "";
				if (!sc.hasNext() && linepointer >= temp.length()) {
					fileIsSent = true;
				}
			} catch (Exception e) {
				System.out.println("Timeout exceeded");
				repeat = true;
				continue;
			}
		}
		clientsckt.close();

	}

}