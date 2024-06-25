package distQueue;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import java.util.List;

public class DistributedLock {
    public static String lockRootNode = "/lock";
    private final ZooKeeper zk;
    private final String nodePath;
    private String lockNode;

    public DistributedLock(ZooKeeper zk, String path) throws InterruptedException, KeeperException {
        this.zk = zk;
        this.nodePath = lockRootNode + path;
        Helper.createNode(zk, lockRootNode);
        Helper.createNode(zk, this.nodePath);
    }

    public boolean lock() throws InterruptedException, KeeperException {
        lockNode = zk.create(nodePath + "/lock-", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        List<String> children = zk.getChildren(nodePath, false);

        children.sort(String::compareTo);

        int index = children.indexOf(lockNode.substring(lockNode.lastIndexOf('/') + 1));

        if(index == 0){
            return true;
        }else{
            zk.delete(lockNode, -1 );
            return false;
        }
    }

    public void unlock() throws InterruptedException, KeeperException {
        zk.delete(lockNode, -1);
    }
}