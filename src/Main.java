import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    // Program reads numbers from a file until it finds -1
    // Starts two threads, one for reading and one for writing
    // Reading thread pushes to the queue, writing pulls from it

    public static int BUFFER_SIZE = 3; // Buffer length parameter
    public static int READER_DELAY_MS = 0; // Reading thread delay
    public static int WRITER_DELAY_MS = 0; // Writing thread delay

    public static void main(String[] args) {

        // Creating queue of specified length
        CircularQueue queue = new CircularQueue(BUFFER_SIZE);

        try {
            // Starting the reading thread
            Reader reader = new Reader(queue, args[0]);
            reader.start();

            // Startoing the writing thread
            Writer writer = new Writer(queue);
            writer.start();

            // Waiting for both threads to finish
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        }
    }
}

class Reader extends Thread {

    private CircularQueue queue;
    private String inputFile;

    public Reader(CircularQueue _syncObj, String _inpuString) {

        this.queue = _syncObj;
        this.inputFile = _inpuString;
    }

    public void run() {

        // String inputFile = "in.data";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            for (String line; (line = reader.readLine()) != null; ) {
                if(Main.READER_DELAY_MS > 0) Thread.sleep(Main.READER_DELAY_MS); // Pausing the thread
                queue.enqueue(Integer.parseInt(line)); // Pushing the value from the file to the queue
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Writer extends Thread {

    private CircularQueue queue;

    public Writer(CircularQueue _syncObj) {

        this.queue = _syncObj;
    }

    public void run() {

        String outputFile = "out.data";

        try (FileWriter writer = new FileWriter(outputFile)) {
            while (true) {
                if(Main.WRITER_DELAY_MS > 0) Thread.sleep(Main.WRITER_DELAY_MS); // Pausing the thread
                int data = queue.dequeue(); // Pulling value from the buffer

                writer.write(Integer.toString(data));
                if(data == -1) { // If value is -1, stopping reader
                    break;
                } else {
                    writer.write("\n");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
