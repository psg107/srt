package com.srt.client.vo

internal data class GetNetFunnelKeyRequest(
    val opcode: String = "5101",
    val nfid: String = "0",
    val prefix: String = "NetFunnel.gRtype=5101;",
    val sid: String = "service_1",
    val aid: String = "act_10",
    val js: String = "true",
)
