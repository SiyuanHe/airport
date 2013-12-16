package com.siyuan.airport;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ControlTower
{

	private static final Logger LOG = Logger.getLogger(ControlTower.class);
	private final ThreadPoolExecutor airplaneRequests;

	private LinkedBlockingQueue<Runway> idleRunways = new LinkedBlockingQueue<Runway>();

	private final int SCHEDULE_THREADS = 1;
	private final int NUM_RUNWAYS = 4;

	public ControlTower()
	{
		final String n = Thread.currentThread().getName();
		this.airplaneRequests = new ThreadPoolExecutor(SCHEDULE_THREADS, SCHEDULE_THREADS,
				60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactory()
				{
					@Override
					public Thread newThread(Runnable r)
					{
						Thread t = new Thread(r);
						t.setName(n + "-airplane schedules-" + System.currentTimeMillis());
						return t;
					}
				});

		//initialize runways
		for (int i = 0; i < NUM_RUNWAYS; i++)
		{
			idleRunways.add(new Runway(i));
		}

		// print out size of idleRunways every 500 millis-sec
		new Thread("Chore")
		{
			public void run()
			{
				while (true)
				{
					LOG.info("idleRunways size: " + idleRunways.size());

					try
					{
						sleep(500);
					}
					catch (InterruptedException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}.start();

	}

	public synchronized void requestTakingRunway(final AirPlane plane)
	{

		try
		{
			this.airplaneRequests.execute(new TakingRunwayRequest(plane));
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Schedule for " + plane + "to take runway.");
			}
		}
		catch (RejectedExecutionException ree)
		{
			LOG.info("Could not schedule for " + plane);
		}
	}

	//don't need to be synchronized for this method.
	//both occupiedRunways and idleRunways are thread safe collections.
	public void notifiedLeavingRunway(AirPlane plane)
	{

		Runway runway = plane.getOccupy();
		runway.clear();

		try
		{
			idleRunways.put(runway);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class TakingRunwayRequest implements Runnable
	{
		private final AirPlane plane;

		public TakingRunwayRequest(AirPlane plane)
		{
			this.plane = plane;
		}

		@Override
		public void run()
		{
			Runway runway;
			try
			{
				runway = idleRunways.take();
				runway.setOccupiedBy(this.plane);
				plane.setOccupy(runway);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
