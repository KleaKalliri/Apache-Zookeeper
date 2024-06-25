import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.List;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;


public class ZookeeperMonitor {
    private static String RootNode = "/members";
    private static ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zookeeper = new ZooKeeper("localhost:2181", 15000, new Watcher() {

            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("----------------------------------------------------------");
                System.out.println("NODE = "+ watchedEvent.getPath());
                System.out.println("EVENT TYPE = "+ watchedEvent.getType());
                System.out.println("----------------------------------------------------------");
                try {
                    startWatch();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        if(zookeeper.exists(RootNode, false) == null){
            zookeeper.create(RootNode, "data".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, null);
        }

        startWatch();
        Thread.sleep(100_000_000);
    }

    private static void startWatch() throws InterruptedException, KeeperException {
        if(zookeeper!=null){
            List<String> children  =zookeeper.getChildren(RootNode, true, null);
            System.out.println("List of children : ");
            children.forEach(c -> System.out.println(c+" "));
            System.out.println();
        }
    }
}
