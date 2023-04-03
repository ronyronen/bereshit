import java.awt.Point;
import java.util.ArrayList;

public class Bereshit {

	// Constants
	public static final double SELF_WEIGHT = 164; // kg
	public static final double MAIN_ENGINE_TRUST = 430; // N
	public static final double SIDE_ENGINE_TRUST = 25; // N
	public static final double NUM_SIDE_ENGINE = 8;

	public static final double FUEL_MAIN_BURN = 0.15; //liter per sec, 12 liter per m'
	public static final double FUEL_SIDE_BURN = 0.009; //liter per sec 0.6 liter per m'


	public static final double ALT0 = 25000; //Altitude
	public static final double V0 = 1700;    // m/s

	public static final int dt = 1;    // Delta Time is 1 sec

	// Variables
	private int time = 0;
	private double altitude = ALT0;
	private double fuel = 420 * 0.5;
	private double verticalSpeed = 0;
	private double horizontalSpeed = V0;
	private double distance = 0;
	private double angle = 0;
	private double accX = 0;
	private double accY = 0;
	private double weight = SELF_WEIGHT + fuel;

	// Main function to start the landing stage
	public ArrayList <Point> land() {
		System.out.println(this.toString());
		ArrayList <Point> points = new ArrayList<Point>();

		// Continue the process until alt = 0 or out of fuel
		while (this.fuel > 0 && this.altitude >= 0) {
			this.time += dt; // 1 sec pear loop
			if( time % 60 == 0 || altitude < 100) {
				System.out.println(this.toString());
				//System.out.println(this.toCSVString());
			}


			double dx = this.horizontalSpeed + this.accX * Bereshit.dt;
			this.distance += dx;
			double dy = this.verticalSpeed + this.accY * Bereshit.dt;
			this.altitude -= Math.abs(dy);

			points.add(new Point((int)this.distance, (int)this.altitude));
			// Stage 1
			// V0x: 1700 -> 1000 m/s
			// V0y: 0 -> 32 m/s
			// X: 0km -> 300km
			// Y: 25km -> 18km

			if (this.altitude >= 100 && this.distance < 300000) {
				if (this.horizontalSpeed > 1000) {
					// put some breaks
					breaks();
				} else {
					// reduce break
					gas();
				}
				if (this.verticalSpeed > 34) {
					// put some breaks
					this.accY = -1.6;
				} else {
					// reduce break
					this.accY = 1.6;
				}
				if (this.angle > 1.3) {
					// reduce
					this.angle -= 0.01;
				} else {
					// increase
					this.angle += 0.01;
				}
			} else if (this.altitude >= 100 && (this.distance >= 300000 && this.distance < 650000)) {
				if (this.horizontalSpeed > 800) {
					// put some breaks
					breaks();
				} else {
					// reduce break
					gas();
				}
				if (this.verticalSpeed > 28) {
					// put some breaks
					this.accY = -1.6;
				} else {
					// reduce break
					this.accY = 1.6;
				}
				if (this.angle > 3) {
					// reduce
					this.angle -= 0.01;
				} else {
					// increase
					this.angle += 0.01;
				}
			} else if (this.altitude >= 100 && (this.distance >= 650000 && this.distance < 780000)) {
				if (this.horizontalSpeed > 0) {
					// put some breaks
					breaks();
				} else {
					// reduce break
					gas();
				}
				if (this.verticalSpeed > 26) {
					// put some breaks
					this.accY = -1.6;
				} else {
					// reduce break
					this.accY = 1.6;
				}
				if (this.angle < 90) {
					// reduce
					this.angle += 0.49;
				} else {
					// increase
					this.angle -= 0.49;
				}
			} else {
				breaks();
				this.accY = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / this.weight;
				
				if (this.angle < 90) {
					// reduce
					this.angle += 0.1;
				} else {
					// increase
					this.angle -= 0.1;
				}
			}
			
			if (this.angle >= 90) {this.angle = 90;}

			this.horizontalSpeed += this.accX;
			if (this.horizontalSpeed < 0) {this.horizontalSpeed = 0;}
			this.verticalSpeed += this.accY;
			if (this.verticalSpeed < 1.6) {this.verticalSpeed = 1.6;}

		}

		if (this.fuel <= 0 && this.altitude > 0) {
			//System.out.println("Oooops... I've crashed!.");
		}

		return points;
		//System.out.println(this.toString());
	}

	// Main function to start the landing stage
	public void land1() {
		System.out.println(this.toString());

		// Continue the process until alt = 0 or out of fuel
		while (this.fuel > 0 && this.altitude >= 5) {
			this.time += dt; // 1 sec pear loop
			if( this.time % 60 == 0 || altitude < 100) {
				System.out.println(this.toString());
			}

			double vx = this.horizontalSpeed + (-this.accX * Bereshit.dt);
			double vy = this.verticalSpeed + (-this.accY * Bereshit.dt);
			//System.out.println(vx + ":" + vy);

			if( time % 60 == 0 || altitude < 100) {
				//System.out.println(vx + ":" + vy);
			}
			double angleInRad = Math.toRadians(angle);
			double xt = Math.sin(angleInRad) * vx; //X(t)
			double yt = Math.cos(angleInRad) * vy; //Y(t)

			if( time % 60 == 0 || altitude < 100) {
				//System.out.println(xt + ":" + yt);
			}
			this.distance += xt;
			this.altitude -= yt;


			if (this.altitude > 9000) {
				this.accY += 0.05 * Bereshit.dt;
				//this.angle += 0.005;
			}

			if (this.altitude < 9000 && this.altitude >= 3000) {
				this.accY += 0.02 * Bereshit.dt;
				//this.angle += 0.01;
			}

			if (this.altitude < 3000 && this.altitude >= 1000) {
				this.accY += 0.01 * Bereshit.dt;
				//this.angle += 0.5;
			}

			if (this.altitude < 1000 && this.altitude >= 5) {
				this.accY += 0.1 * Bereshit.dt;
				//this.angle += 0.2;
			}

			if (this.distance <= 450000) {
				this.accX += 0.0002 * Bereshit.dt;
			}

			if (this.distance > 450000 && this.distance <= 550000) {
				this.accX += 0.5 * Bereshit.dt;
			}

			if (this.distance > 550000) {
				this.accX += 1 * Bereshit.dt;
			}

			if (this.altitude < 5) {
				this.accY = 0;
				this.accX = 0;
			}

			if (this.accY > 100) {
				this.accY = 100;
			}
			if (this.accY <= 0) {
				this.accY = 0;
			}
			if (this.accX > 1700) {
				this.accX = 1700;
			}
			if (this.accX <= 0) {
				this.accX = 0;
			}
			/*if(this.altitude > 20000) {
					if(this.verticalSpeed > 25) {
						this.accY -= 0.003 * Bereshit.dt;
					} if(this.verticalSpeed < 20) {
						this.accY += 0.003 * Bereshit.dt;
					}
				} else if(this.altitude < 20000 && this.altitude > 5000) {
					if(this.verticalSpeed > 44) {
						this.accY -= 0.03 * Bereshit.dt;
					} if(this.verticalSpeed < 39) {
						this.accY += 0.03 * Bereshit.dt;
					}
				} else {
					if(this.verticalSpeed > 75) {
						this.accY -= 0.6 * Bereshit.dt;
					} if(this.verticalSpeed < 70) {
						this.accY += 0.6 * Bereshit.dt;
					}
				}

				if(this.distance < 400000) {
					this.accX -= 0.0001 * Bereshit.dt;
				} else if(this.distance > 400000 && this.distance < 600000) {
					this.accX -= 0.006 * Bereshit.dt;
				} else {
					this.accX -= 0.25 * Bereshit.dt;
				}*/

			this.horizontalSpeed -= this.accX;
			this.verticalSpeed -= this.accY;

			if (this.horizontalSpeed <= 0) {
				this.horizontalSpeed = 0;
			}
			if (this.verticalSpeed <= 0) {
				this.verticalSpeed = 0;
			}

			/*
				//double vacc = Moon.getAcc(hs);
				double x = 0 + this.horizontalSpeed * Bereshit.dt + 0.5 * 0 * (Bereshit.dt * Bereshit.dt);
				this.distance += x;

				double y = this.altitude + 0 * Bereshit.dt + 0.5 * -this.verticalSpeed * (Bereshit.dt * Bereshit.dt);	
				this.altitude = y;
			 */
		}

		if (this.fuel <= 0 && this.altitude > 0) {
			System.out.println("Oooops... I've crashed!.");
		}

		System.out.println(this.toString());
	}

	// Turn off engines to accelerate
	private void gas() {
		enginesOff();
	}

	// Turn on engines to decelerate
	private void breaks() {
		this.accX = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / this.weight;
		enginesOn();
	}

	// Turn off engines to accelerate
	private void enginesOn() {
		fuel();
	}

	// Turn off engines to accelerate
	private void enginesOff() {
		this.accX = 0;
		this.accY = 1.6;
	}
	
	// Functions to calculate the burning fuel
	private void fuel() {
		this.fuel -= (Bereshit.dt * (FUEL_MAIN_BURN + FUEL_SIDE_BURN * NUM_SIDE_ENGINE));
		this.weight = Bereshit.SELF_WEIGHT + this.fuel;
	}


	@Override
	public String toString() {
		return String.format("T:[%04ds] A:[%,.2f] F:[%03.2f] VS:[%04.2fm/s] HS:[%04.2fm/s] D:[%,6.2fm] D[%f]" 
				,time, altitude, fuel, verticalSpeed, horizontalSpeed, distance, angle);
	}
	
	public String toCSVString() {
		return String.format("%04d;%,.2f;%04.2f;%04.2f;%,6.2f" 
				,time, altitude, verticalSpeed, horizontalSpeed, distance);
	}


}
