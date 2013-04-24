
public class ConnInfo {

	int p_sent;
	int p_rcvd;
	double b_sent;
	double b_rcvd;
	
	ConnInfo(int ps,int pr,double bs,double br){
		this.p_sent=ps;
		this.p_rcvd=pr;
		this.b_sent=bs;
		this.b_rcvd=br;
	}
	
	public int getP_sent() {
		return p_sent;
	}

	public void setP_sent(int p_sent) {
		this.p_sent = p_sent;
	}

	public int getP_rcvd() {
		return p_rcvd;
	}

	public void setP_rcvd(int p_rcvd) {
		this.p_rcvd = p_rcvd;
	}

	public double getB_sent() {
		return b_sent;
	}

	public void setB_sent(double b_sent) {
		this.b_sent = b_sent;
	}

	public double getB_rcvd() {
		return b_rcvd;
	}

	public void setB_rcvd(double b_rcvd) {
		this.b_rcvd = b_rcvd;
	}

}
