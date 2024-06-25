package org.example;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class DistributedLocking {
    static ZooKeeper zookeeper;
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String myId = UUID.randomUUID().toString();
        String rootNode = "/root";
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        tryToGetLock(myId, rootNode);
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        zookeeper = new ZooKeeper("localhost:2181", 20000, watcher);
        if(zookeeper.exists(rootNode, false) ==  null){
            zookeeper.create(rootNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        zookeeper.create(rootNode+ "/"+ "node-", myId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        tryToGetLock(myId, rootNode);
    }

    private static void tryToGetLock(String sessionId, String rootNode) throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren(rootNode, false);
        children.sort(String::compareTo);
        byte[] data = zookeeper.getData(rootNode+ "/"+children.get(0), false, null);
        if(data!=null && new String(data).equalsIgnoreCase(sessionId)){
            System.out.println("I acquired a lock :). will leave it in 10 seconds");
            for (int i=0;i<10;i++){
                System.out.println("leaving in "+ i + "seconds");
                Thread.sleep(1000);
            }
            zookeeper.delete(rootNode+ "/"+children.get(0), -1);
        }else{
            System.out.println("i could not acquire a lock. So will wait");
            zookeeper.getChildren(rootNode, true);
        }

        Thread.sleep(100_100_100);
    }
}
