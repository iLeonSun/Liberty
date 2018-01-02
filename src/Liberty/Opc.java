package Liberty;

/*
 * this is a class for normalized operating condition
 * the format is:
 * SSG_0.81_-40 (string_double_int)
 */
public class Opc {
	private String process;
	private double voltage;
	private int temperture;
	private String opc_name;

	public Opc(String process, double voltage, int temperture) {
		this.process = process;
		this.voltage = voltage;
		this.temperture = temperture;
		this.opc_name = process + "_" + voltage + "_" + temperture;
	}

	public Opc(String opc_name) {
		this.opc_name = opc_name;
		String[] pvt=opc_name.split("_");
		this.process=pvt[0];
		this.voltage=Double.parseDouble(pvt[1]);
		this.temperture=Integer.parseInt(pvt[2]);
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public int getTemperture() {
		return temperture;
	}

	public void setTemperture(int temperture) {
		this.temperture = temperture;
	}

	public String getOpc_name() {
		return opc_name;
	}

	public void setOpc_name(String opc_name) {
		this.opc_name = opc_name;
	}

	@Override
	public String toString() {
		return "Opc [name= "+ opc_name +", process=" + process + ", voltage=" + voltage + ", temperture=" + temperture + "]";
	}

	public static void main(String[] args) {
		System.out.println(new Opc("ff", 0.9, -40));
		System.out.println(new Opc("ff_0.9_-40"));
	}
}
