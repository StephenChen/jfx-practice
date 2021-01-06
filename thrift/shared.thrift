/**
 * This Thrift file can be included by other Thrift files that want to share
 * these definitions.
 */

namespace java jfx.thrift.protocol

struct SharedStruct {
    1: i32 key
    2: string value
}

service SharedService {
    SharedStruct getStruct(1: i32 key)
}


