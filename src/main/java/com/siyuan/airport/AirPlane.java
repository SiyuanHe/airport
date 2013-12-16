package com.siyuan.airport;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public class AirPlane implements Runnable
{

	private static final Logger LOG = Logger.getLogger(AirPlane.class);

	private long time_on_runway;

	private long time_on_flying_or_stop;

	private int id;

	private Runway occupy;

	private ControlTower tower;

	private AirPlaneState state;

	private Queue<String> notifications = new ConcurrentLinkedQueue<String>();

	public AirPlane(int id, long time_on_runway, long time_on_flying_or_stop)
	{
		this.id = id;
		this.time_on_runway = time_on_runway;
		this.time_on_flying_or_stop = time_on_flying_or_stop;
	}

	public int getId()
	{
		return id;
	}

	public void wantToTakeRunway()
	{
		tower.requestTakingRunway(this);
	}

	public synchronized void leaveRunway()
	{
		LOG.info("air plane " + id + "is leaving runway " + this.occupy);
		tower.notifiedLeavingRunway(this);
		this.occupy = null;
		this.state = AirPlaneState.FLYING_OR_STOP;
	}

	public String toString()
	{
		return String.valueOf(this.id);
	}

	public Runway getOccupy()
	{
		return occupy;
	}

	public void setOccupy(Runway occupy)
	{
		this.occupy = occupy;
		this.notifications.add("You get the runway");
	}

	public ControlTower getTower()
	{
		return tower;
	}

	public void setTower(ControlTower tower)
	{
		this.tower = tower;
	}

	public void runningOnRunway()
	{
		LOG.info("air plane " + id + " is running on runway " + this.occupy);
		this.state = AirPlaneState.RUNING_ON_RUNWAY;
		try
		{
			Thread.sleep(this.time_on_runway);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stopOrFlying()
	{
		this.state = AirPlaneState.FLYING_OR_STOP;
		LOG.info("air plane " + id + " stops or is flying");
		try
		{
			Thread.sleep(this.time_on_flying_or_stop);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{

		while (true)
		{
			String notification = notifications.poll();
			if (notification == "You get the runway")
			{
				this.runningOnRunway();
				this.leaveRunway();
				//stop the this airplane thread
				break;
			}
			else
			{
				if (this.state != AirPlaneState.FLYING_OR_STOP)
				{
					this.stopOrFlying();
					this.wantToTakeRunway();
				}

			}

		}
	}
}
