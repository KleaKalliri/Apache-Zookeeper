import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;
import java.util.Random;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Server1 {
    private static String RootNode = "/members";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        Random r = new Random();
        int id = r.nextInt(100)+1;
        System.out.println("MY ID = " + id);
        ZooKeeper zookeeper = new ZooKeeper("localhost:2181", 15000, null);
        String node = zookeeper.create(RootNode+"/Node"+ id, ("Node"+id).getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, null);
        System.out.println("NODE = " + node);
        Thread.sleep(100_000_000);

    }
}