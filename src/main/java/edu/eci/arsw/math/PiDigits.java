package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {

    public static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;

    
    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int N) throws InterruptedException {
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        Scanner scanner = new Scanner(System.in);
        AtomicBoolean flag = new AtomicBoolean(false);

        int interval = count/N;
        int remaining = count%N;
        ArrayList<PiDigitsThread> hilos = new ArrayList<>();

        for (int i = 0; i < N; i++){
            if (i+1 == N){
                PiDigitsThread hilo = new PiDigitsThread(start + (i*interval), interval + remaining, flag);
                hilo.start();
                hilos.add(hilo);
            }
            else {
                PiDigitsThread hilo = new PiDigitsThread(start + (i*interval), interval, flag);
                hilo.start();
                 hilos.add(hilo);
            }
        }

        while (true){
            int counter = 0;
            boolean vivo = true;
            for (PiDigitsThread p: hilos){
                if (p.isAlive()){
                    vivo = true;
                    break;
                }
                else {
                    vivo = false;

                }
            }

            if (!vivo){
                break;
            }
            flag.set(true);
            Thread.sleep(5000);


            for (PiDigitsThread p : hilos){
                counter += p.getCuantos();
            }
            System.out.println("counter = " + counter);

            scanner.nextLine();

            flag.set(false);
            synchronized (flag){
                flag.notifyAll();
            }
        }

        for (PiDigitsThread p: hilos){
            p.join();
        }

        byte[] digits = new byte[count];

        int currentPosition = 0;
        for (PiDigitsThread p : hilos) {
            byte[] hiloResulto = p.getDigits();
            System.arraycopy(hiloResulto, 0, digits, currentPosition, hiloResulto.length);
            currentPosition += hiloResulto.length;
        }

        return digits;


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
