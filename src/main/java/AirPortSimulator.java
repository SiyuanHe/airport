import java.io.IOException;
import java.util.Random;

import com.siyuan.airport.AirPlane;
import com.siyuan.airport.ControlTower;

/**
 * User: siyuan
 * Date: 7/05/13
 * Time: 6:50 AM
 */
public class AirPortSimulator
{
	public static void main(String[] args) throws IOException
	{
		ControlTower controlTower = new ControlTower();
		Random r = new Random();

		for (int i = 0; i < 10; i++)
		{
			AirPlane plane = new AirPlane(i, r.nextInt(10) * 1000, r.nextInt(10) * 1000);
			plane.setTower(controlTower);

			Thread t = new Thread(plane);
			t.start();
		}

	}
}
