package edu.eci.arsw.math;

import java.util.concurrent.atomic.AtomicBoolean;

import static edu.eci.arsw.math.PiDigits.DigitsPerSum;
import static java.lang.Long.sum;

public class PiDigitsThread extends Thread{
    private int start;
    private int count;
    private byte[] digits;
    private AtomicBoolean flag;
    private int cuantos = 0;
    private static double Epsilon = 1e-17;

    public PiDigitsThread(int start, int count, AtomicBoolean flag) {
        this.start = start;
        this.count = count;
        digits = new byte[count];
        this.flag = flag;
    }

    public byte[] getDigits() {
        return digits;
    }

    public int getCuantos() {
        return cuantos;
    }

    @Override
    public void run(){
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (flag.get()){
                try {
                    synchronized (flag){
                        flag.wait();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);

                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));

            digits[i] = (byte) sum;
            cuantos ++;
        }
    }

    /// <summary>
    /// Returns the sum of 16^(n - k)/(8 * k + m) from 0 to k.
    /// </summary>
    /// <param name="m"></param>
    /// <param name="n"></param>
    /// <returns></returns>
    public static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }
}


















