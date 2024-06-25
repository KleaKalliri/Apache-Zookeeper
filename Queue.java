package distQueue;
import org.apache.zookeeper.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class Queue {
    private ZooKeeper zk;
    private String queuePath;

    private Lock lock;
    public Queue(ZooKeeper zk, String queuePath) throws InterruptedException, KeeperException {
        this.zk = zk;
        this.queuePath = queuePath;
        this.lock = new ReentrantLock();
        Helper.createNode(zk, queuePath);
    }

    public void enqueue(String item) throws Exception {

        lock.lock();
        zk.create(queuePath + "/item", item.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        lock.unlock();
    }

    public String dequeue() throws Exception {
        while (true) {
            List<String> children = zk.getChildren(queuePath, false);
            if (children.isEmpty()) {
                return null;
            }

            children.sort(String::compareTo);

            for (String node : children) {
                boolean lockAcquired = false;
                DistributedLock distlock = null;
                try {
                    String nodePath = queuePath + "/" + node;  //psh  /queue/item0
                    distlock = new DistributedLock(zk, queuePath);
                    lockAcquired = distlock.lock();

                    if (lockAcquired && zk.exists(nodePath, false) != null) {

                        byte[] data = zk.getData(nodePath, false, null);
                        String item = new String(data);

                        zk.delete(nodePath, -1);
                        return item;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (lockAcquired && distlock != null) {
                        distlock.unlock();
                    }
                }
            }
        }
    }
}
