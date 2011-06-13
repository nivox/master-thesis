#include "net_helper.h"
#include "cloud_helper.h"
#include "peersampler.h"
#include "cloud_helper_utils.h"

int main(int argc, char *argv[]) {
  struct nodeID *local_node;
  struct cloud_helper_context *cloud_ctx;
  struct psample_context *ps_ctx;
  uint8_t buff[1024];

  local_node = net_helper_init("127.0.0.1", 1234, "");
  cloud_ctx = cloud_helper_init(local_node,
                "provder=delegate,delegate_lib=my_helper.so," \
                "param1=value1,...");
  ps_ctx = psample_init(local_node, NULL, 0,
                        "protocol=cloudcast,cache_size=25");

  /* Perform first cycle to initialize the protocol */
  psample_parse_data(con->ps_context, NULL, 0);
  while(1) {
    const struct timeval tout = {1, 0};
    int data_source = -1;
    int news = 0;

    /* Wait for incoming data either from peers or cloud */
    news = wait4any_threaded(local_node, cloud_ctx, &tout, NULL,
                             &data_source);
    if (news > 0) {
      struct nodeID *remote = NULL;
      int len = 0;

      /* Incoming data available, perform passive thread */
      if (data_source == DATA_SOURCE_NET) {
        len = recv_from_peer(local_node, &remote, buff, 1024);
      }
      else if (data_source == DATA_SOURCE_CLOUD) {
        len = recv_from_cloud(cloud_ctx, buff, 1024);
      }
      psample_parse_data(con->ps_context, buff, len);
      if (remote) nodeid_free(remote);
    } else {
      /* Let the peer sampler handles active cycles and
         sleep periods */
      psample_parse_data(ps_ctx, NULL, 0);
    }
  }
}
