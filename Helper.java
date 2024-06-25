package distQueue;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class Helper {
    public static void createNode(ZooKeeper zooKeeper, String nodePath) {
        try {
            if (zooKeeper.exists(nodePath, false) == null) {

                zooKeeper.create(nodePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
