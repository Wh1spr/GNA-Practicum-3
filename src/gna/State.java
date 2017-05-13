package gna;

import libpract.Position;

public class State {
	
	private State previous;
	private Position position;
	private int currentCost;
	private int totalCost;
	
	public State(State previous, Position pos, int cost) {
		this.previous = previous;
		this.currentCost = cost;
		if (this.previous != null) {
			this.totalCost = this.getPrevious().getTotalCost() + this.currentCost;
		} else {this.totalCost = this.currentCost;}
		this.position = pos;
		
	}
	
	public State getPrevious() {return this.previous;}
	public Position getPosition() {return this.position;}
	public int getCurrentCost() {return this.currentCost;}
	public int getTotalCost() {return this.totalCost;}
	
	@Override
	public String toString() {
		return "Position: " + this.getPosition().toString() + "\nCost: " + String.valueOf(getTotalCost()) + " (current = " + String.valueOf(this.getCurrentCost()) + ")";
	}
}
