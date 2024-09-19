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
                sum = 4 * PiDigits.sum(1, start)
                        - 2 * PiDigits.sum(4, start)
                        - PiDigits.sum(5, start)
                        - PiDigits.sum(6, start);

                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));

            digits[i] = (byte) sum;
            cuantos ++;
        }
    }
}


















