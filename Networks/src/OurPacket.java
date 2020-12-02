
public class OurPacket implements java.io.Serializable{
	int sourcePort, destinationPort, lenghOfData, acknowledgeBits, seqNum;
	final String sourceIP = "";
	final String destinationIP= "";
	String checksum;
	String data;
	
	
	public int getSourcePort() {
		return sourcePort;
	}
	public OurPacket() {
		sourcePort = 0;
		destinationPort = 0;
		lenghOfData = 1024;
		seqNum = 0;
		checksum = "";
		acknowledgeBits = -1;
		data = "Test packet";
	}
	public OurPacket(int sourcePort, int destinationPort, int lengthOfData,
			int seqNum, String checkSum,int acknowledgeBits, String data){
		setAcknowledgeBits(acknowledgeBits);
		setChecksum(checkSum);
		setData(data);
		setDestinationPort(destinationPort);
		setSeqNum(seqNum);
		setSourcePort(sourcePort);
		setLenghOfData(lengthOfData);
	}
	public void setSourcePort(int sourcePort) {
			this.sourcePort = sourcePort;
	}
	public int getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(int destinationPort) {
			this.destinationPort = destinationPort;
	}
	public int getLenghOfData() {
		return lenghOfData;
	}
	public void setLenghOfData(int lenghOfData) {
		this.lenghOfData = lenghOfData;
	}
	public int getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public int getAcknowledgeBits() {
		return acknowledgeBits;
	}
	public void setAcknowledgeBits(int acknowledgeBits) {
		this.acknowledgeBits = acknowledgeBits;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "OurPacket [sourcePort=" + sourcePort + ", destinationPort=" + destinationPort + ", lenghOfData="
				+ lenghOfData + ", acknowledgeBits=" + acknowledgeBits + ", seqNum=" + seqNum + ", sourceIP=" + sourceIP
				+ ", destinationIP=" + destinationIP + ", checksum=" + checksum + ", data=" + data + "]";
	}
	
	
	
}
