import matplotlib.pyplot as plt
import numpy as np


class PID:
    def __init__(self, Kp=1.0, Ki=1.0, Kd=1.0):
        self.Kp = Kp
        self.Ki = Ki
        self.Kd = Kd
        self.set_point = 0
        self.previous_error = 0

    def proportional_control(self, error):
        return self.Kp * error

    def integral_control(self, error, dt):
        return self.Ki * error * dt

    def derivative_control(self, error, dt):
        return self.Kd * (error - self.previous_error) / dt

    def update(self, point, dt=1):
        error = self.set_point - point
        p = self.proportional_control(error)
        i = self.integral_control(error, dt)
        d = self.derivative_control(error, dt)
        output = p + i + d
        return output


def main():
    pid = PID(0.4, 0.1, 0.01)
    pid.set_point = 100
    t, o = [], []
    t.append(0)
    o.append(0)
    for i in range(1, 11):
        t.append(i)
        output = 100 + pid.update(o[i-1], 1)
        print(output)
        o.append(output)

    fig, ax = plt.subplots()
    ax.plot(t, o)
    plt.show()


if __name__ == "__main__":
    main()
