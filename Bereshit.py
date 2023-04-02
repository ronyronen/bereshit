from datetime import datetime
import math

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

import numpy as np
from scipy.interpolate import interp1d

X = np.array(
    [0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 8])*1e5  # distance
Y = np.array(
    [25000, 23333, 21667, 20000, 18333, 16667, 15000, 13333, 11667, 10000, 8333, 6667, 5000, 3333, 1000, 0])  # altitude
f_trajectory = interp1d(X, Y, kind='cubic')  # distance -> alt

X1 = np.array(
    [1700.00, 1586.67, 1473.33, 1360.00, 1246.67, 1133.33, 1020.00, 906.67, 793.33, 680.00, 566.67, 453.33, 340.00,
     226.67, 113.33, 0])  # hs
Y1 = np.array([0, 6, 12, 18, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90])  # angle
f_angle = interp1d(X1, Y1, kind='cubic')  # hs -> angle


X3 = np.array(
    [0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 8])*1e5  # distance
Y3 = np.array(
    [25000, 23333, 21667, 20000, 18333, 16667, 15000, 13333, 11667, 10000, 8333, 6667, 5000, 900, 100, 0])  # altitude
f3_trajectory = interp1d(X3, Y3, kind='cubic')  # distance -> alt

X4 = np.array(
    [1700.00, 1586.67, 1473.33, 1360.00, 1246.67, 1133.33, 1020.00, 906.67, 793.33, 680.00, 566.67, 40.33, 30.00,
     20, 10, 0])  # hs
Y4 = np.array([0, 3, 6, 9, 12, 15, 18, 21, 46, 47, 48, 49, 40, 50, 88, 90])  # angle
f4_angle = interp1d(X4, Y4, kind='cubic')  # hs -> angle

# def print_f1():
#     xnew = np.arange(0, 800, 0.1)
#     ynew = f1(xnew)   # use interpolation function returned by `interp1d`
#     plt.plot(X, Y, 'o', xnew, ynew, '-')
#     plt.show()

# Constants
SELF_WEIGHT = 164  # kg
MAIN_ENGINE_TRUST = 430  # N
SIDE_ENGINE_TRUST = 25  # N
NUM_SIDE_ENGINE = 8

FUEL_MAIN_BURN = 0.15  # liter per sec, 12 liter per m'
FUEL_SIDE_BURN = 0.009  # liter per sec 0.6 liter per m'

ALT0 = 25000  # Altitude
V0 = 1700  # m / s

dt = 1  # Delta Time is 1 sec


class Bereshit():
    def __init__(self):
        # Variables
        self.time = 0
        self.altitude = ALT0
        self.fuel = 420 * 0.5
        self.vertical_speed = 0.0
        self.horizontal_speed = V0
        self.distance = 0.0
        self.angle = 0.0
        self.acc_x = 0.0
        self.acc_y = 0.0
        self.engine_is_on = False
        self.weight = SELF_WEIGHT + self.fuel
        self.df = pd.DataFrame()

        # make CSV file name from these params
        time_stamp = datetime.now().strftime("%d%m%Y_%H%M%S")
        self.filename = f'logfile_{time_stamp}.csv'
        self.logfile = open(self.filename, 'w')
        self.logfile.write('time, alt, fuel, vs, hs, distance, angle, engine\n')

    # Main function to start the landing stage
    def land(self):
        points = dict()
        angles = list()
        engines = list()
        # Continue the process until alt = 0 or out of fuel

        print('Time, Alt, Fuel, VS, HS, Dist, angle')
        while self.fuel > 0 and self.altitude >= 0:
            row = {'time': self.time, 'altitude': self.altitude, 'fuel': self.fuel, 'vs': self.vertical_speed,
                   'hs': self.horizontal_speed, 'distance': self.distance, 'angle': self.angle}
            self.df = self.df.append(row, ignore_index=True)

            print(
                f'T:[{self.time}] A:[{self.altitude}] F:[{self.fuel}] VS:[{self.vertical_speed}] HS:[{self.horizontal_speed}] D:[{self.distance}] a:[{self.angle}]')

            # 'Time, Alt, Fuel, VS, HS, Dist, angle\n')
            self.logfile.write(
                f'{self.time}, {self.altitude}, {self.fuel}, {self.vertical_speed}, {self.horizontal_speed}, {self.distance}, {self.angle}, {int(self.engine_is_on)}\n')

            self.time += dt  # 1 sec pear loop
            dx = self.horizontal_speed + self.acc_x * dt
            self.distance += dx
            dy = self.vertical_speed + self.acc_y * dt
            self.altitude -= math.fabs(dy)

            points[self.distance] = self.altitude
            angles.append(self.angle)

            '''
            // Stage 1
			// V0x: 1700 -> 1000 m/s
			// V0y: 0 -> 32 m/s
			// X: 0km -> 300km
			// Y: 25km -> 18km
            '''

            if self.altitude >= 100 and self.distance < 300000:
                if self.horizontal_speed > 1000:
                    # put some breaks
                    self.breaks()
                    engines.append(True)
                else:
                    # reduce break
                    self.gas()
                    engines.append(False)

                if self.vertical_speed > 34:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    # reduce break
                    self.acc_y = 1.6

                if self.angle > 1.3:
                    # reduce
                    self.angle -= 0.01
                else:
                    # increase
                    self.angle += 0.01

            elif self.altitude >= 100 and (300000 <= self.distance < 650000):
                if self.horizontal_speed > 800:
                    # put some breaks
                    self.breaks()
                    engines.append(True)
                else:
                    # reduce break
                    self.gas()
                    engines.append(False)

                if self.vertical_speed > 28:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    # reduce break
                    self.acc_y = 1.6

                if self.angle > 3:
                    # reduce
                    self.angle -= 0.01
                else:
                    # increase
                    self.angle += 0.01

            elif self.altitude >= 100 and (650000 <= self.distance < 780000):
                if self.horizontal_speed > 0:
                    # put some breaks
                    self.breaks()
                    engines.append(True)
                else:
                    # reduce break
                    self.gas()
                    engines.append(False)

                if self.vertical_speed > 26:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    # reduce break
                    self.acc_y = 1.6

                if self.angle < 90:
                    # reduce
                    self.angle += 0.49
                else:
                    # increase
                    self.angle -= 0.49
            else:
                self.breaks()
                engines.append(False)
                self.acc_y = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / self.weight

                if self.angle < 90:
                    # reduce
                    self.angle += 0.1
                else:
                    # increase
                    self.angle -= 0.1

            if self.angle >= 90:
                self.angle = 90

            self.horizontal_speed += self.acc_x
            if self.horizontal_speed < 0:
                self.horizontal_speed = 0

            self.vertical_speed += self.acc_y
            if self.vertical_speed < 1.6:
                self.vertical_speed = 1.6

        if self.fuel <= 0 < self.altitude:
            print("Oooops... I've crashed!.")

        self.logfile.close()
        plot(self.filename, 'land.png')

    def land_traj(self):
        # Continue the process until alt = 0 or out of fuel
        print('Time, Alt, Fuel, VS, HS, Dist, angle')
        while self.altitude >= 0:
            row = {'time': self.time, 'altitude': self.altitude, 'fuel': self.fuel, 'vs': self.vertical_speed,
                   'hs': self.horizontal_speed, 'distance': self.distance, 'angle': self.angle}
            self.df = self.df.append(row, ignore_index=True)

            print(
                f'T:[{self.time}] A:[{self.altitude}] F:[{self.fuel}] VS:[{self.vertical_speed}] HS:[{self.horizontal_speed}] D:[{self.distance}] a:[{self.angle}]')

            # 'Time, Alt, Fuel, VS, HS, Dist, angle\n')
            self.logfile.write(
                f'{self.time}, {self.altitude}, {self.fuel}, {self.vertical_speed}, {self.horizontal_speed}, {self.distance}, {self.angle} , {int(self.engine_is_on)}\n')

            # , time, altitude, fuel, verticalSpeed, horizontalSpeed, distance, angle)
            self.time += dt  # 1 sec pear loop
            dx = self.horizontal_speed + self.acc_x * dt
            self.distance += dx
            dy = self.vertical_speed + self.acc_y * dt
            self.altitude -= math.fabs(dy)

            if self.distance < 800e3:
                target_altitude = f_trajectory(self.distance)
                if target_altitude < self.altitude:
                    self.breaks()
                else:
                    self.gas()
            else:
                self.breaks()

            if self.altitude >= 100 and self.distance < 300e3:
                if self.vertical_speed > 34:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    self.acc_y = 1.6
            elif self.altitude >= 100 and (300e3 <= self.distance < 650e3):
                if self.vertical_speed > 28:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    self.acc_y = 1.6
            elif self.altitude >= 100 and (650e3 <= self.distance < 780e3):
                if self.vertical_speed > 26:
                    # put some breaks
                    self.acc_y = -1.6
                else:
                    self.acc_y = 1.6
            else:
                self.acc_y = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / self.weight

            self.angle = f_angle(self.horizontal_speed)
            if self.angle > 90:
                self.angle = 90
            elif self.angle < 0:
                self.angle = 0

            self.horizontal_speed += self.acc_x
            if self.horizontal_speed < 0:
                self.horizontal_speed = 0

            self.vertical_speed += self.acc_y
            if self.vertical_speed < 1.6:
                self.vertical_speed = 1.6

            if self.fuel <= 0 < self.altitude:
                print("Oooops... I've crashed!.")
                break

        self.logfile.close()
        plot(self.filename, 'land_traj.png')

    def land_pid(self):
        from pid_control import PID

        # all you need is set the kp, ki, kd, it's really simple.
        # you can set your set point and sample time too.

        # Continue the process until alt = 0 or out of fuel
        print('Time, Alt, Fuel, VS, HS, Dist, angle')
        pid_alt = PID(0.4, 0.2, 0.01)
        pid_alt.set_point = ALT0

        pid_ang = PID(1, 0.5, 0.01)
        pid_ang.set_point = 0
        while self.altitude >= 5:
            row = {'time': self.time, 'altitude': self.altitude, 'fuel': self.fuel, 'vs': self.vertical_speed,
                   'hs': self.horizontal_speed, 'distance': self.distance, 'angle': self.angle}
            self.df = self.df.append(row, ignore_index=True)

            print(
                f'T:[{self.time}] A:[{self.altitude}] F:[{self.fuel}] VS:[{self.vertical_speed}] HS:[{self.horizontal_speed}] D:[{self.distance}] a:[{self.angle}]')

            # 'Time, Alt, Fuel, VS, HS, Dist, angle\n')
            self.logfile.write(
                f'{self.time}, {self.altitude}, {self.fuel}, {self.vertical_speed}, {self.horizontal_speed}, {self.distance}, {self.angle}, {int(self.engine_is_on)}\n')

            # , time, altitude, fuel, verticalSpeed, horizontalSpeed, distance, angle)
            self.time += dt  # 1 sec pear loop
            dx = self.horizontal_speed + self.acc_x * dt
            self.distance += dx
            dy = self.vertical_speed + self.acc_y * dt
            self.altitude -= math.fabs(dy)

            if self.distance < 800e3:
                target_altitude = f3_trajectory(self.distance)

                # update(accel, max_accel, gap, prev_gap, safety_thresh, dt)
                pid_alt.set_point = target_altitude
                output = pid_alt.update(self.altitude, dt)
                self.altitude += output
                print(target_altitude, self.altitude)

                if target_altitude < self.altitude or self.altitude > 5:
                    self.breaks1()
                else:
                    self.gas()
            else:
                self.gas()

            # if self.altitude >= 100 and self.distance < 300e3:
            #     if self.vertical_speed > 34:
            #         # put some breaks
            #         self.acc_y = -1.6
            #     else:
            #         self.acc_y = 1.6
            # elif self.altitude >= 100 and (300e3 <= self.distance < 650e3):
            #     if self.vertical_speed > 28:
            #         # put some breaks
            #         self.acc_y = -1.6
            #     else:
            #         self.acc_y = 1.6
            # elif self.altitude >= 100 and (650e3 <= self.distance < 780e3):
            #     if self.vertical_speed > 26:
            #         # put some breaks
            #         self.acc_y = -1.6
            #     else:
            #         self.acc_y = 1.6
            # else:
            #     self.acc_y = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / self.weight

            target_angle = f4_angle(self.horizontal_speed)
            pid_ang.set_point = target_angle
            output_ang = pid_ang.update(self.angle, dt)
            self.angle += output_ang
            print(target_angle, self.angle)

            if self.angle >= 90:
                self.angle = 90
            elif self.angle <= 0:
                self.angle = 0

            self.horizontal_speed += self.acc_x
            if self.horizontal_speed < 0:
                self.horizontal_speed = 0

            self.vertical_speed += self.acc_y
            if self.vertical_speed < 1.6:
                self.vertical_speed = 1.6

            if self.fuel <= 0 < self.altitude:
                print("Oooops... I've crashed!.")
                break

        self.logfile.close()
        plot(self.filename, 'land_pid.png')

    def gas(self):
        self.engines_off()

    def breaks(self):
        self.acc_x = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / self.weight
        self.engines_on()

    def breaks1(self):
        self.acc_x = -(MAIN_ENGINE_TRUST + SIDE_ENGINE_TRUST * NUM_SIDE_ENGINE) / self.weight
        self.acc_x = self.acc_x * math.cos(math.radians(self.angle))
        self.acc_y = -(1.6 * math.sin(math.radians(self.angle)))
        self.engines_on()

    def engines_on(self):
        self.engine_is_on = True
        self.total_fuel()

    def engines_off(self):
        self.engine_is_on = False
        self.acc_x = 0
        self.acc_y = 1.6

    def total_fuel(self):
        self.fuel -= (dt * (FUEL_MAIN_BURN + FUEL_SIDE_BURN * NUM_SIDE_ENGINE))
        self.weight = SELF_WEIGHT + self.fuel


def plot(filename, fig_name='fig.png'):
    data = np.genfromtxt(filename, delimiter=',', skip_header=1)

    # time, altitude, fuel, verticalSpeed, horizontalSpeed, distance, angle, engine
    tm = data[:, 0]
    alt = data[:, 1]
    ful = data[:, 2]
    vs = data[:, 3]
    hs = data[:, 4]
    d = data[:, 5]
    ag = data[:, 6]

    fig, axs = plt.subplots(2)
    d = d / 1000
    alt = alt / 1000
    ag = ag.astype(int)
    g = sns.lineplot(x=d, y=alt, ax=axs[0])
    sns.lineplot(x=tm, y=ag, ax=g.axes.twinx())

    hs = hs / 10
    sns.lineplot(x=tm, y=ful, ax=axs[1], label='fuel')
    sns.lineplot(x=tm, y=hs, ax=axs[1], label='hs')
    sns.lineplot(x=tm, y=vs, ax=axs[1], label='vs')
    a = np.vstack((data[:, 0], data[:, 7]))
    a = a[:, a[1] > 0]
    sns.scatterplot(x=a[0], y=a[1], ax=axs[1], label='engine', marker=".")
    plt.savefig('docs/' + fig_name)

    plt.show()
