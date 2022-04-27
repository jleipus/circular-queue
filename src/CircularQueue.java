public class CircularQueue {

    private int front, rear; // Buffer front and rear markers
    private int[] nums; // buffer array

    public CircularQueue(int initialSize) {

        this.front = this.rear = -1; // -1 means the buffer is empty
        this.nums = new int[initialSize]; // Creating array of specified length
    }

    public void enqueue(int data) throws InterruptedException {

        synchronized (this) {
            if (isFull()) { // If buffer is full, waiting for space
                wait();
            } else if (isEmpty()) {
                front++;
            }

            rear = rear + 1;
            nums[rear % nums.length] = data;

            notify(); // Notifying that data has been added
        }
    }

    public int dequeue() throws InterruptedException {

        synchronized (this) {
            if (isEmpty()) { // If buffer is empty, witing for data
                wait();
            }

            int temp = nums[front % nums.length];
            if (front == rear) {
                front = rear = -1;
            } else {
                front = front + 1;
            }

            notify(); // Notifying that space has been freed

            return temp;
        }
    }

    private boolean isEmpty() {

        return front == -1;
    }

    private boolean isFull() {

        return (rear + 1) % nums.length == front % nums.length;
    }
}
