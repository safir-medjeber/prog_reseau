#include <stdlib.h>
#include <string.h>

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>

int main(){
  struct sockaddr_in addr;
  int socket_desc;
  char *msg = "HLO user... machine... port...";
  
  socket_desc = socket(AF_INET, SOCK_DGRAM, 0);
  
  bzero(&addr, sizeof(addr));
  
  addr.sin_family = AF_INET;
  addr.sin_addr.s_addr = INADDR_ANY;
  addr.sin_port = htons(28888);
  
  sendto(socket_desc, msg, strlen(msg), 0, (struct sockaddr *)&addr, sizeof(addr));
  
  close(socket_desc);
  
  exit(EXIT_SUCCESS);
    

}
