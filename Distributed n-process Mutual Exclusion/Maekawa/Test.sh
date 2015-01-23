remoteuser=rxs132730
remotecomputer1=dc14.utdallas.edu
remotecomputer2=dc15.utdallas.edu
remotecomputer3=dc16.utdallas.edu
remotecomputer4=dc17.utdallas.edu
remotecomputer5=dc18.utdallas.edu
remotecomputer6=dc19.utdallas.edu
remotecomputer7=dc20.utdallas.edu
remotecomputer8=dc21.utdallas.edu
remotecomputer9=dc22.utdallas.edu
#remotecomputer5=net15.utdallas.edu
#remotecomputer6=net16.utdallas.edu
#remotecomputer7=net17.utdallas.edu

ssh -l "$remoteuser" "$remotecomputer1" "cd $HOME/AOS/Maekawa/src;java Main 0" &
ssh -l "$remoteuser" "$remotecomputer2" "cd $HOME/AOS/Maekawa/src;java Main 1" &
ssh -l "$remoteuser" "$remotecomputer3" "cd $HOME/AOS/Maekawa/src;java Main 2" &
ssh -l "$remoteuser" "$remotecomputer4" "cd $HOME/AOS/Maekawa/src;java Main 3" &
ssh -l "$remoteuser" "$remotecomputer5" "cd $HOME/AOS/Maekawa/src;java Main 4" &
ssh -l "$remoteuser" "$remotecomputer6" "cd $HOME/AOS/Maekawa/src;java Main 5" &
ssh -l "$remoteuser" "$remotecomputer7" "cd $HOME/AOS/Maekawa/src;java Main 6" &
ssh -l "$remoteuser" "$remotecomputer8" "cd $HOME/AOS/Maekawa/src;java Main 7" &
ssh -l "$remoteuser" "$remotecomputer9" "cd $HOME/AOS/Maekawa/src;java Main 8" &
#ssh -l "$remoteuser" "$remotecomputer5" "cd $HOME/AOS/Project1;java Project1 4" &
#ssh -l "$remoteuser" "$remotecomputer6" "cd $HOME/AOS/Project1;java Project1 5" &
#ssh -l "$remoteuser" "$remotecomputer7" "cd $HOME/AOS/Project1;java Project1 6" &


#Run this script on CS machine.
#Prerequisite - Passwordless login should be enabled using Public keys and you should have logged on to the net machines atleast once after creating a public key.
#example
#-bash-4.1$ ssh net23.utdallas.edu
#The authenticity of host 'net23.utdallas.edu (10.176.67.86)' can't be established.
#RSA key fingerprint is 66:af:c1:ce:29:b8:5b:7b:8e:25:33:92:bb:96:0e:46.
#Are you sure you want to continue connecting (yes/no)? yes

#Your code should be in directory $HOME/AOS/Project1
#Your main program should be named Project1.java or Project1.cpp or Project1.c