/* Imports statements skipped */

public class Example implements StoreUpdateHandler {
  public static void main(String args[]) throws Exception{
    NetworkHelper nh;
    CloudURI cloudURI
    URI mysqlCloud = new URI("mysql://sa@server:/db/bucket/");
    InetAddress addr = InetAddress.getByName("localhost");
    cloudURI = CloudURI.getInstance("mysql",mysqlCloud);

    /* Setup net/cloud helpers */
    nh = NetworkHelper.configureDefaultInstance(addr, , 1234);
    StorageCloud ch = StorageCloud.getInstance("mysql", cloudURI);

    /* Setup peer sampling */
    PeerNode node = nh.getLocalNode();
    CloudCast ps = CloudCast.getDefaultInstance(node, cloudURI);
    RandomPeerSelector selAe = new RandomPeerSelector(ps);
    RandomPeerSelector selRm = new RandomPeerSelector(ps);
    selRm.excludeCloud(true);


    /* Setup stores */
    Store localStore, cloudStore;
    StoreEntryDiffHandler differ = new FakeDiffHandler();
    localStore = new SimpleStore(new InMemoryPersistenceHandler(),
                     differ);
    cloudStore = new SimpleStore(
                     new BasicCloudPersistenceHandler(ch, "store/"),
                     differ);

    /* Setup epidemic broadcast */
    CloudEnabledAntiEntropyBroadcast ae;
    RumorMongeringBroadcast rm;
    ae = CloudEnabledAntiEntropyBroadcast.getDefaultInstance(node, selAe,
                     localStore, cloudStore);
    rm = RumorMongeringBroadcast.getDefaultInstance(node, selRm,
                     localStore, 5);

    /* Start execution */
    nh.start();
    ps.start();
    ae.start();
    rm.start();
  }

  public void notifyUpdate(String[] keys, Store store) {
    for (String key: keys) System.out.println("Received news: " + key);
  }
}
