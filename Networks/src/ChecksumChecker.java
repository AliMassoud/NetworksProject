
public class ChecksumChecker {
	public static String toBinary(Object num) {
		String binary = Integer.toBinary(num);
		int empty = 16 - Integer.toBinary(num).length();
		if (empty == -1) {
			int num2 = Integer.parseInt(binary.substring(1), 2);
			num2 = num2 + 1;
			binary = Integer.toBinaryString(num2);
		} else if (empty > 0) {
			for (int i = 0; i < empty; i++)
				binary = '0' + binary;
		} else {
			return binary;
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

	public static void checksumSender(OurPacket pckt) {
		int src = pckt.getSourcePort();
		int dest = pckt.getDestinationPort();
		int len = pckt.getLenghOfData();
		int sum = src + dest + len;
		String strSum = toBinary(sum);
		String str = onesComplement(strSum);
		pckt.setChecksum(str);
	}
}
