package com.siyuan.airport;

public class Runway
{

	private int id;

	private AirPlane occupiedBy;

	public Runway(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public String toString()
	{
		return String.valueOf(this.id);
	}

	public AirPlane getOccupiedBy()
	{
		return occupiedBy;
	}

	public void setOccupiedBy(AirPlane plane)
	{
		this.occupiedBy = plane;

	}

	public void clear()
	{
		this.occupiedBy = null;
	}
}
