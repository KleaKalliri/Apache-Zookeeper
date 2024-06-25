package distQueue;
import org.apache.zookeeper.ZooKeeper;


public class Consumer {

    public static void main(String[] args) throws Exception
    {

        ZooKeeper zk = new ZooKeeper("localhost:2181", 20000, null);

        Helper.createNode(zk, DistributedLock.lockRootNode);

        Queue queue = new Queue(zk,"/queue");

        while (true){
            String dequeValue = queue.dequeue();
            if(dequeValue != null){
                System.out.println("CONSUME : "+dequeValue);
            }
            Thread.sleep(1000);
        }

    }
}