package distQueue;
import org.apache.zookeeper.ZooKeeper;


public class Producer {
    public static void main(String[] args) throws Exception {
        ZooKeeper zk = new ZooKeeper("localhost:2181", 20000, null);

        Queue queue = new Queue(zk,"/queue");

        int i = 0;
        while (i < Integer.MAX_VALUE){
            System.out.println("PRODUCE : item "+i);
            queue.enqueue("item"+i);
            Thread.sleep(1000);
            i++;
        }

        zk.close();
    }
}
