#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/sctp.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <net/if.h>
#include <stdlib.h>
#include <time.h>
#include <sys/stat.h>
#include <fcntl.h>

int BUFFER_SIZE = 1024;
FILE *one;
FILE *two;
FILE *three;
int n;
int count = 0;
int written = 0;


int main(int argc, char** argv) {

    unsigned char buffer[BUFFER_SIZE];
    int sockCli, sockServ, i, res;

    //files to send are opened in different mode, based on the file type
    one = fopen("files/bsn.dat", "rb");
    two = fopen("files/skela.dat", "rb");
    three = fopen("files/third.txt", "r");

    if (one < 0 || two < 0 || three < 0) {
        printf("Error on opening file\n");
        exit(1);
    }

    struct sockaddr_in client, server;

    struct sctp_initmsg initmsg;

    //creating a socket with sctp type
    sockServ = socket(AF_INET, SOCK_STREAM, IPPROTO_SCTP);

    if (sockServ < 0) {
        printf("failed on creating socket\n");
        exit(1);
    }

    memset(&server, 0, sizeof (server));
    memset(&client, 0, sizeof (client));

    server.sin_family = AF_INET;
    server.sin_addr.s_addr = htonl(INADDR_ANY);
    server.sin_port = htons(35000);
    int servSize = sizeof (server);

    memset(&initmsg, 0, sizeof (initmsg));
    initmsg.sinit_num_ostreams = 3; //number of streams = 3
    initmsg.sinit_max_instreams = 3;
    initmsg.sinit_max_attempts = 2;
    setsockopt(sockServ, IPPROTO_SCTP, SCTP_INITMSG, &initmsg, sizeof (initmsg));

    res = bind(sockServ, (struct sockaddr *) &server, sizeof (server));

    if (res < 0) {
        printf("bind() failed\n");
        exit(1);
    }

    res = listen(sockServ, 5);

    sockCli = accept(sockServ, (struct sockaddr*) &client, (socklen_t*) & servSize);

    for (i = 0; i < 3; i++) {

//        memset(buffer, 0, sizeof (buffer));

        if (i == 0) {
            //send the first file in the list on stream 0
            while(!(feof(one))) {
                n = fread(buffer, 1, BUFFER_SIZE, one);
                count += n;
                printf("n1 = %d\n", n);
                sctp_sendmsg(sockCli, (void *) buffer, (size_t) n, 
                        (struct sockaddr *) &client, (socklen_t)sizeof(client),
                        0, 0, 0 /*stream no. */, 0, 0);
            }
            printf("%d bytes read from one.\n", count);

        }


        if (i == 1) {
            //send the second file in the list on stream 1
            while(!(feof(two))) {
                n = fread(buffer, 1, BUFFER_SIZE, two);
                count += n;
                printf("n2 = %d\n", n);
                sctp_sendmsg(sockCli, (void *) buffer, (size_t) n, 
                        (struct sockaddr *) &client, (socklen_t)sizeof(client),
                        0, 0, 1 /*stream no. */, 0, 0);
            }
            printf("%d bytes read from two.\n", count);

        }


        if (i == 2) {
            //send the third file in the list on stream 2
            while(!(feof(three))) {
                n = fread(buffer, 1, BUFFER_SIZE, three);
                count += n;
                printf("n3 = %d\n", n);
                sctp_sendmsg(sockCli, (void *) buffer, (size_t) n, 
                        (struct sockaddr *) &client, (socklen_t)sizeof(client),
                        0, 0, 2 /*stream no. */, 0, 0);
            }
            printf("%d bytes read from three.\n", count);

        }

    }

    //close the open sockets & files
    close(sockCli);
    close(sockServ);
    fclose(one);
    fclose(two);
    fclose(three);
//    free (buffer);
    return 0;

}
