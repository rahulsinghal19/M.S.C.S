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

int BUFFER_SIZE = 1024;
FILE *one;
FILE *two;
FILE *three;

int main(int argc, char** argv) {

    //remove pre-existing files
    remove("bsn.dat");
    remove("skela.dat");
    remove("third.txt");

//    FILE * files[3];
    one = fopen("bsn.dat", "wb");
    two = fopen("skela.dat", "wb");
    three = fopen("third.txt", "w");

    int flags, count0 = 0, count1 = 0, count2 = 0, res, received, sockCli;
    unsigned char buffer[BUFFER_SIZE];

    struct sockaddr_in server;

    struct sctp_sndrcvinfo sndrcvinfo;
    struct sctp_event_subscribe events;
    struct sctp_initmsg initmsg;

    struct timeval begin, end;
    int sec, usecs;

//    char buffer[1025];

    char ipServer[32] = "127.0.0.1";
    short int servPort = 35000;

    //creating socket on sctp
    sockCli = socket(AF_INET, SOCK_STREAM, IPPROTO_SCTP);

    if (sockCli < 0) {
        printf("Error on creating socket\n");
        exit(1);
    }

    memset(&initmsg, 0, sizeof (initmsg));
    initmsg.sinit_num_ostreams = 3; //number of streams = 3
    initmsg.sinit_max_instreams = 3;
    initmsg.sinit_max_attempts = 2;
    res = setsockopt(sockCli, IPPROTO_SCTP, SCTP_INITMSG, &initmsg, sizeof (initmsg));

    if (res < 0) {
        printf("setsockopt() initmsg failed\n");
        exit(1);
    }

    memset(&events, 0, sizeof (events));
    events.sctp_data_io_event = 1;
    res = setsockopt(sockCli, SOL_SCTP, SCTP_EVENTS, (const void *) &events, sizeof (events));

    if (res < 0) {
        printf("setsockopt() events failed\n");
        exit(1);
    }


    memset(&server, 0, sizeof (server));
    server.sin_family = AF_INET;
    inet_pton(AF_INET, ipServer, &server.sin_addr);
    server.sin_port = htons(servPort);
    int servSize = sizeof (server);

    res = connect(sockCli, (struct sockaddr*) &server, sizeof (server));

    if (res < 0) {
        printf("connect() failed\n");
        exit(1);
    }

    gettimeofday(&begin, NULL);

    while (1) {

//        memset(buffer, 0, sizeof (buffer));
        //receive message on sctp
        received = sctp_recvmsg(sockCli, (void*) buffer, sizeof (buffer), 
                (struct sockaddr*) &server, (socklen_t*) & servSize, &sndrcvinfo, &flags);

        if (received == 0) {
            printf("Received 0 bytes, all streams ended\n");
            break;
        }

        //classify the stream received on and add the contents to appropriate file
        if (sndrcvinfo.sinfo_stream == 0) {
            fwrite(buffer, 1, received, one);
            count0++;
            continue;
        }

        if (sndrcvinfo.sinfo_stream == 1) {
            fwrite(buffer, 1, received, two);
            count1++;
            continue;
        }

        if (sndrcvinfo.sinfo_stream == 2) {
            fwrite(buffer, 1, received, three);
            count2++;
            continue;
        }

    }

    gettimeofday(&end, NULL);

    close(sockCli);
    
    fclose(one);
    fclose(two);
    fclose(three);

    sec = end.tv_sec - begin.tv_sec;

    if (end.tv_usec > begin.tv_usec)
        usecs = end.tv_usec - begin.tv_usec;
    else
        usecs = begin.tv_usec - end.tv_sec;

    printf("Time elapsed is %d,%d\n", sec, usecs);
    printf("Received\n%d messages on stream 0\n%d messages on stream 1\n%d messages on stream 2\n", count0, count1, count2);
    return 0;

}
