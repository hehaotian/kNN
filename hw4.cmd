Executable = ./build_kNN.sh
Universe   = vanilla

getenv     = true
output     = 1.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 1 1 1.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 1.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 1 2 1.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 5.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 5 1 5.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 5.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 5 2 5.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 10.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 10 1 10.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 10.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train.vectors.txt ~/dropbox/13-14/572/hw4/examples/test.vectors.txt 10 2 10.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 1.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 1 1 1.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 1.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 1 2 1.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 5.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 5 1 5.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 5.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 5 2 5.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 10.1.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 10 1 10.1.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue

getenv     = true
output     = 10.2.acc
arguments  = "~/dropbox/13-14/572/hw4/examples/train2.vectors.txt ~/dropbox/13-14/572/hw4/examples/test2.vectors.txt 10 2 10.2.sys_output"
transfer_executable = false
request_memory = 2*1024
Queue