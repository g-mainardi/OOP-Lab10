package it.unibo.oop.lab.workers02;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        Worker(final double[][] array, final int startpos, final int nelem) {
            super();
            this.matrix = Arrays.copyOf(array, array.length);
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("I have to sum from the line " + this.startpos + " to the line " 
                    + (this.startpos + this.nelem - 1));
            for (int i = this.startpos; i < this.matrix.length && i < this.startpos + this.nelem; i++) {
                for (final double num : this.matrix[i]) {
                    this.res += num;
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % this.nthread + matrix.length / this.nthread;
        return IntStream.iterate(0, start -> start + size)
            .limit(this.nthread)
            .mapToObj(start -> new Worker(matrix, start, size))
            .peek(Thread::start)
            .peek(MultiThreadedSumMatrix::joinUninterruptibly)
            .mapToDouble(Worker::getResult)
            .sum();
    }

    private static void joinUninterruptibly(final Thread target) {
        boolean joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
