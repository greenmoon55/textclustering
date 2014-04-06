package test;
import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

public class CreateDataset {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Dataset data = new DefaultDataset();
		Instance instance1 = new DenseInstance(new double[] {2, 10});
		data.add(instance1);
		Instance instance2 = new DenseInstance(new double[] {2, 5});
		data.add(instance2);
		Instance instance3 = new DenseInstance(new double[] {8, 4});
		data.add(instance3);
		Instance instance4 = new DenseInstance(new double[] {5, 8});
		data.add(instance4);
		Instance instance5 = new DenseInstance(new double[] {7, 5});
		data.add(instance5);
		Instance instance6 = new DenseInstance(new double[] {6, 4});
		data.add(instance6);
		Instance instance7 = new DenseInstance(new double[] {1, 2});
		data.add(instance7);
		Instance instance8 = new DenseInstance(new double[] {4, 9});
		data.add(instance8);
		
		try {
			FileHandler.exportDataset(data,new File("data.csv"), false, ",");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
